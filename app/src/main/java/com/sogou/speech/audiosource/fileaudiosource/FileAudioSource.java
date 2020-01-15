package com.sogou.speech.audiosource.fileaudiosource;


import com.sogou.speech.audiosource.AbstractAudioSource;
import com.sogou.speech.audiosource.IAudioDataProvider;
import com.sogou.speech.audiosource.IAudioDataProviderFactory;
import com.sogou.speech.audiosource.IAudioSourceListener;
import com.sogou.speech.audiosource.CachedBufferFactory;
import com.sogou.speech.audiosource.ConditionVar;
import com.sogou.speech.audiosource.IBufferFactory;
import com.sogou.speech.audiosource.NewBufferFactory;
import com.sogou.speech.utils.SpeechLogUtil;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class FileAudioSource extends AbstractAudioSource implements  Runnable {
        private static final int STATUS_NOT_READY = 0;
        private static final int STATUS_STARTED = 1;
        private static final int STATUS_PAUSED = 2;
        private static final int STATUS_DEAD = 3;

        private final IAudioDataProviderFactory mProviderFactory;

        private final long mMaxRecordTimeSec;

        private int mDataProviderStatus = STATUS_NOT_READY;
        private final Object mStatusLock = new Object();
        private final ConditionVar mResumeCondition;
        private final boolean mNewOutputBuffer;

        public FileAudioSource(IAudioDataProviderFactory providerFactory, boolean newOutputBuffer,
                               int maxRecordTimeSec){
        mProviderFactory = providerFactory;
        mMaxRecordTimeSec = maxRecordTimeSec;
        mResumeCondition = new ConditionVar(mStatusLock, new ConditionVar.ICondition() {
            @Override
            public boolean satisfied() {
                return mDataProviderStatus != STATUS_PAUSED;
            }
        });
        mNewOutputBuffer = newOutputBuffer;
    }

        public FileAudioSource(IAudioDataProviderFactory providerFactory){
        this(providerFactory, true, Integer.MAX_VALUE);
    }

    private IAudioDataProvider createRecorderNoLock(){
        SpeechLogUtil.log("FileAudioSource # createRecorderNoLock");
        IAudioDataProvider tmp = mProviderFactory.create();
        SpeechLogUtil.log(String.format("FileAudioSource # createRecorderNoLock(), tmp.isInitialized():%b", tmp.isInitialized()));
        if(tmp.isInitialized()){
            try {
                tmp.start();
                SpeechLogUtil.log(String.format("FileAudioSource # createRecorderNoLock(), IAudioDataProvider tmp.start(), pos1)"));
            }catch (Exception e){
                e.printStackTrace();
                tmp.release();
                tmp = null;}

        }else {
            tmp.release();
            tmp = null;
        }
        return tmp;
    }

    private IBufferFactory getOutputBufferFactory(int length){
        IBufferFactory bufferFactory;
        final boolean useShort = mProviderFactory.bytesPerFrame() == 2;
        if (useShort){
            SpeechLogUtil.log("getOutputBufferFactory# new short buffer factory");
            bufferFactory = mNewOutputBuffer ? new NewBufferFactory.ShortBufferFactory() :
                    new CachedBufferFactory.ShortBufferFactory(length);
        }else {
            SpeechLogUtil.log("getOutputBufferFactory# new byte buffer factory");
            bufferFactory = mNewOutputBuffer ? new NewBufferFactory.ByteBufferFactory() :
                    new CachedBufferFactory.ByteBufferFactory(length);
        }
        return bufferFactory;
    }

    @Override
    public int start(){
        synchronized (mStatusLock){
            if (mDataProviderStatus != STATUS_STARTED){
                mDataProviderStatus = STATUS_STARTED;
                mStatusLock.notify();
            }
            return mDataProviderStatus == STATUS_STARTED ? 0 : -1;
        }
    }

    @Override
    public int pause(){
        synchronized (mStatusLock){
            if ( mDataProviderStatus == STATUS_STARTED){
                mDataProviderStatus = STATUS_PAUSED;
            }
            return mDataProviderStatus == STATUS_PAUSED ? 0 : -1;
        }
    }

    @Override
    public int stop(){
        synchronized (mStatusLock){
            if (mDataProviderStatus != STATUS_DEAD){
                //set status to dead, release call delayed or finalized
                mDataProviderStatus = STATUS_DEAD;
                mStatusLock.notify();
            }
            return mDataProviderStatus == STATUS_DEAD ? 0 : -1;
        }
    }

    private void releaseAudioDataSource(IAudioDataProvider audioDataProvider){
        if (audioDataProvider != null){
            audioDataProvider.release();
        }
    }

    private int waitForResume(){
        synchronized (mStatusLock){
            mResumeCondition.waitCondition();
            return mDataProviderStatus;
        }
    }

    private long getMaxRecordFrameCount(){
        return mMaxRecordTimeSec == Integer.MAX_VALUE ? Integer.MAX_VALUE : mMaxRecordTimeSec * mProviderFactory.samplingRateInHz();
    }

    private int pollStatusLocked(){
        synchronized (mStatusLock){
            return mDataProviderStatus;
        }
    }

    @Override
    public void run(){
        boolean twoBytesPerFrame = mProviderFactory.bytesPerFrame() == 2;
        final int bufferSizeInBytes = mProviderFactory.bufferSizeInBytes();
//        final Object audioBuffer = twoBytesPerFrame ? new short[bufferSizeInBytes / 2] :
//                new byte[bufferSizeInBytes];

        //AudioBuffer is used to keep bytes data from file.
//        final Object audioBuffer = new byte[1808901*2];
        //每次读8000个short
//        final Object audioBuffer = new byte[2560*2];
        final Object audioBuffer = new byte[3200];
        final int bufferLen = Array.getLength(audioBuffer);
        final long maxRecordFrameCnt = getMaxRecordFrameCount();

        int errorCode = 0;
        Exception exception = null;

        long totalFrameCnt = 0;
        long packageNum = 0;

        final IBufferFactory outputBufferFactory = getOutputBufferFactory(bufferLen);

        IAudioDataProvider dataProvider = null;
        try {
            dataProvider = createRecorderNoLock();
            if (dataProvider == null){
                errorCode = -1;
                //report finish
                fireOnEnd(errorCode, exception, 0);
                return;
            }

            start();
            //report begin
            fireOnBegin();
            dataProvider.start();
            SpeechLogUtil.log(String.format("FileAudioSource # createRecorderNoLock(), IAudioDataProvider tmp.start(), pos2)"));
            int index = 0;

            while (true){
                try {
                    int readFrameCnt = dataProvider.read( (byte[])audioBuffer, 0, bufferLen);
                    SpeechLogUtil.log("FileAudioSource # read bytes,count:"+(++index)+" bufferLen:"+bufferLen+",readFrameCount:"+readFrameCnt+" bytes");
                    if (readFrameCnt < 0){
                        errorCode = readFrameCnt;
                        break;
                    }
                    int shortLen = readFrameCnt/2;
                    short[] shortData = new short[shortLen];
                    ByteBuffer.wrap((byte[])audioBuffer).order(ByteOrder.nativeOrder()).asShortBuffer().get(shortData);

                    final Object tmpWavData = outputBufferFactory.newBuffer(shortLen);
                    System.arraycopy(shortData, 0, tmpWavData, 0, shortLen);
                    ++packageNum;

                    totalFrameCnt += shortLen;
//

                    int status = pollStatusLocked();
                    final boolean pausedOrStopped = (status == STATUS_PAUSED || status == STATUS_DEAD);
                    if (pausedOrStopped){
                        dataProvider.stop();
                    }
                    //report progress
                    fireOnNewData(tmpWavData, packageNum, totalFrameCnt - shortLen, pausedOrStopped ?
                            IAudioSourceListener.AUDIO_DATA_FLAG_SESSION_END : 0);

                    Thread.sleep(100);

                    //!!! Continue to avoid missing pause signal !!!
                    if (!pausedOrStopped){
                        continue;
                    }

                    status = waitForResume();
                    if(status != STATUS_STARTED){
                        break;
                    }

                    dataProvider.start();

                }catch (Exception e){
                    errorCode = -1;
                    exception = e;
                    break;
                }
            }

            fireLastPacketOfFile(packageNum, totalFrameCnt);
            fireOnEnd(errorCode, exception, totalFrameCnt);

        }
        finally {
            releaseAudioDataSource(dataProvider);
        }
    }

    private void fireLastPacketOfFile(long packageNum, long totalFrameCnt){
        final int SHORT_LEN = 10;
        short[] endWavData  = new short[SHORT_LEN];
        ++packageNum;
        totalFrameCnt += SHORT_LEN;
        //report progress
        fireOnNewData(endWavData, packageNum, totalFrameCnt - SHORT_LEN, IAudioSourceListener.AUDIO_DATA_FLAG_SESSION_END);
        SpeechLogUtil.log("fireLastPacketOfFile, packageNum:"+packageNum+",length:"+(endWavData == null?0:endWavData.length));
    }

    @Override
    public int bytesPerSecond() {
        return mProviderFactory.samplingRateInHz() * mProviderFactory.bytesPerFrame();
    }
}
