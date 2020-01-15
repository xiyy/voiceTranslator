package com.sogou.speech.framework;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.sogou.speech.audiosource.AudioSourceManager;
import com.sogou.speech.audiosource.IAudioSource;
import com.sogou.speech.audiosource.IAudioSourceListener;
import com.sogou.speech.http.GrpcAsrRequestProtocol;
import com.sogou.speech.listener.AudioRecordListener;
import com.sogou.speech.listener.GrpcAsrListener;
import com.sogou.speech.listener.PreprocessListener;
import com.sogou.speech.utils.SpeechLogUtil;
import com.sogou.speech.utils.SpeexEncoder;
import com.sogou.speech.utils.WavUtil;
import com.sogou.speech.vad.DefaultVoiceDetectorFactory;
import com.sogou.speech.vad.IVoiceDetector;
import com.sogou.speech.vad.VadListener;
import com.xi.liuliu.voicetranslator.bean.Language;


public class GrpcAsrManager implements IAudioSourceListener, VadListener{
    private static final String TAG = "GrpcAsrManager";
    public static final int ONLINE_ASR_MODE_SHORT = 0;
    public static final int AUDIO_SOURCE_MIC = 0;
    public static final int AUDIO_SOURCE_FILE = 1;
    public static final int MSG_RAW_DATA = 1;
    private static final int STATUS_UNINITED = 0;
    private static final int STATUS_STARTED = 1;
    private static final int STATUS_PAUSED = 2;
    private static final int STATUS_DEAD = 3;
    private final AudioSourceManager mAudioSourceManager;
    private final IVoiceDetector mVoiceDetector;
    private final SpeexEncoder mSpeexEncoder;
    private int mStatus = STATUS_UNINITED;
    private volatile Handler mVadHandler;
    //录音是否结束
    private volatile boolean isSpeechEnd;
    private GrpcAsrRequestProtocol mGrpcAsrRequestProtocol;
    private AudioRecordListener mAudioRecordListener;
    private PreprocessListener mPreprocessListener;
    public GrpcAsrManager(Context context, AudioRecordListener audioRecordListener,PreprocessListener preprocessListener, GrpcAsrListener grpcAsrListener, Language language) {
        mAudioRecordListener = audioRecordListener;
        mPreprocessListener = preprocessListener;
        mGrpcAsrRequestProtocol = new GrpcAsrRequestProtocol(grpcAsrListener, context, language);
        if (mGrpcAsrRequestProtocol != null) mGrpcAsrRequestProtocol.sendConfig();
        mAudioSourceManager = new AudioSourceManager(AUDIO_SOURCE_MIC, "", audioRecordListener);
        mSpeexEncoder = new SpeexEncoder();
        mVoiceDetector = DefaultVoiceDetectorFactory.create(preprocessListener, 16000, 320, 0, 3000, DefaultVoiceDetectorFactory.DynamicVoiceEndWaitPolicy.create(ONLINE_ASR_MODE_SHORT),
                60 * 1000, 0, true);

    }

    @Override
    protected void finalize() throws Throwable {
        stop();
        super.finalize();
    }

    public void start() {
        SpeechLogUtil.log(TAG, "AsrTranslateManager#start(), mStatus:" + mStatus);
        boolean firstStart;
        synchronized (this) {
            if (mStatus == STATUS_STARTED || mStatus == STATUS_DEAD) {
                return;
            }
            firstStart = (mStatus == STATUS_UNINITED);
            mStatus = STATUS_STARTED;
        }
        SpeechLogUtil.log(TAG, "AsrTranslateManager # start(),firstStart:" + firstStart);
        if (firstStart) {
            mAudioSourceManager.addAudioSourceListener(this);
            mAudioSourceManager.start(true);

            final PreProcessThread vadThread = new PreProcessThread();
            vadThread.start();
            mVadHandler = new Handler(vadThread.getLooper(), vadThread);

        } else {
            mAudioSourceManager.start(false);
        }

    }

    public void pause() {
        boolean started = false;
        synchronized (this) {
            if (mStatus == STATUS_STARTED) {
                mStatus = STATUS_PAUSED;
                started = true;
            }
        }
        if (started) {
            mAudioSourceManager.pause();
        }
    }

    public void stop() {
        boolean canStop = false;
        synchronized (this) {
            if (mStatus == STATUS_STARTED || mStatus == STATUS_PAUSED) {
                mStatus = STATUS_DEAD;
                canStop = true;
            }
        }
        if (canStop) {
            mAudioSourceManager.stop();
        }
    }

    public void release() {
        stop();
        if (mAudioSourceManager != null) {
            mAudioSourceManager.removeAudioSourceListener(this);
        }
        if (mVadHandler != null) {
            mVadHandler.removeMessages(MSG_RAW_DATA);
            mVadHandler.getLooper().quit();
        }

    }

    @Override
    public void onSpeechBegin(IAudioSource audioSource) {

    }

    /**
     * 点击stop按钮（用户主动停止录音时）该方法被回调
     *
     * @param audioSource
     * @param status
     * @param e
     * @param sampleCount
     */
    @Override
    public void onSpeechEnd(IAudioSource audioSource, int status, Exception e, long sampleCount) {
        if (!isSpeechEnd && mGrpcAsrRequestProtocol != null) {
            mGrpcAsrRequestProtocol.sendAudioData(null, true);
        }
        isSpeechEnd = true;
        SpeechLogUtil.log(TAG, "onSpeechEnd,isSpeechEnd:" + isSpeechEnd);

    }

    /**
     * 录音过程中可能报错
     *
     * @param errorCode
     * @param errorMessage
     */
    @Override
    public void onAudioError(int errorCode, String errorMessage) {
        if (mAudioRecordListener != null) {
            mAudioRecordListener.onAudioRecordError(errorCode, errorMessage);
            mAudioRecordListener = null;
        }
    }
    /**
     * 接收原始音频数据
     *
     * @param audioSource Audio data source
     * @param dataArray   short[] or byte[] contains raw audio data
     * @param packIndex   the sequence number of raw audio data
     * @param sampleIndex the sequence number of the first sample in dataArray.
     *                    total sample count = sampleIndex + dataArray.length
     * @param flag        the data flag. Eg. AUDIO_DATA_FLAG_SESSION_END means end of one session
     */
    @Override
    public void onSpeechNewData(IAudioSource audioSource, Object dataArray, long packIndex, long sampleIndex, int flag) {
        short[] data = (short[]) dataArray;
        Message msg = mVadHandler.obtainMessage(MSG_RAW_DATA);
        msg.obj = data;
        msg.arg1 = flag;
        mVadHandler.sendMessage(msg);
        if (mAudioRecordListener != null) {
            mAudioRecordListener.onAudioDataReceived(data);
            mAudioRecordListener.onVoiceDecibelChanged(WavUtil.getVoiceDecibel(data));
        }
        int length = 0;
        if (data != null) length = data.length;
        SpeechLogUtil.log(TAG, "onNewData,dataArray.length:" + length + " packIndex:" + packIndex + " sampleIndex:" + sampleIndex + " flag:" + flag);


    }


    /**
     * @param data                     Vad返回的数据
     * @param flag
     * @param sessionBeginSampleOffset
     * @param sessionEndSampleOffset
     * @param arg
     */
    @Override
    public void onNewVoiceDetected(short[] data, int flag, long sessionBeginSampleOffset, long sessionEndSampleOffset, Object arg) {
        final boolean sentenceBegin = (flag & VAD_FLAG_VOICE_BEGIN) != 0;
        final boolean sentenceEnd = (flag & VAD_FLAG_VOICE_END) != 0;
        final boolean manualEnd = (flag & VAD_FLAG_MANUAL_END) != 0;
        int length = 0;
        if (data != null) length = data.length;
        SpeechLogUtil.log(TAG, "data.length:" + length + " flag:" + flag + " sentenceBegin:" + sentenceBegin + " sentenceEnd:" + sentenceEnd + " manualEnd:" + manualEnd);
        byte[] speexData = mSpeexEncoder.encode(data);
        if (sentenceEnd) {//VAD检测到第一句话已经生成
            //Vad检测到末尾1.17秒空音时，自动判断停止录音，此时onAsrSpeechEnd被回调
            if (!manualEnd) {
                //mAsrTranslateListener.onAsrSpeechEnd(1, null, 1);
                if (!isSpeechEnd && mGrpcAsrRequestProtocol != null)
                    mGrpcAsrRequestProtocol.sendAudioData(speexData, true);
                SpeechLogUtil.log(TAG, "mVoicePrintRequestProtocol.sendAudioData,speexData.length:" + speexData.length + " isLast:" + true);
                isSpeechEnd = true;
                SpeechLogUtil.log(TAG, "Vad检测到第一句话已经生成，onAsrSpeechEnd被回调，录音结束，isSpeechEnd：" + isSpeechEnd);
            }
            if (mVadHandler != null) {
                mVadHandler.removeMessages(MSG_RAW_DATA);
                mVadHandler.getLooper().quit();
            }

        } else {
            if (!isSpeechEnd && mGrpcAsrRequestProtocol != null)
                mGrpcAsrRequestProtocol.sendAudioData(speexData, false);
            SpeechLogUtil.log(TAG, "mVoicePrintRequestProtocol.sendAudioData,speexData.length:" + speexData.length + " isLast:" + false);
        }
    }

    @Override
    public void onNoVoiceDetected(boolean isSessionEnd, Object arg) {

    }

    @Override
    public void onVadError(int errorCode, String errorMsg, Exception ex, Object arg) {
        if (mPreprocessListener != null) {
            mPreprocessListener.onVadError(errorCode, errorMsg);
        }
    }


    private class PreProcessThread extends HandlerThread implements Handler.Callback {
        public PreProcessThread() {
            super("PreProcess");
        }

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == MSG_RAW_DATA) {
                int flag = msg.arg1;
                short[] data = (short[]) msg.obj;

                final boolean isSessionEnd = (flag & AUDIO_DATA_FLAG_SESSION_END) != 0;
                int detectOption = 0;
                if (isSessionEnd) {
                    detectOption |= IVoiceDetector.DETECT_OPTION_END_OF_SESSION;
                }
                mVoiceDetector.detect(data, detectOption, GrpcAsrManager.this, null, ONLINE_ASR_MODE_SHORT);
            }
            return true;
        }
    }
}
