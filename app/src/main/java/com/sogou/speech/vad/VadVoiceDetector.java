package com.sogou.speech.vad;


import com.sogou.speech.framework.GrpcAsrManager;
import com.sogou.speech.listener.PreprocessListener;
import com.sogou.speech.audiosource.CachedBufferFactory;
import com.sogou.speech.utils.ErrorIndex;
import com.sogou.speech.audiosource.IBufferFactory;
import com.sogou.speech.utils.SpeechLogUtil;
import com.sogou.speech.audiosource.NewBufferFactory;

public class VadVoiceDetector implements IVoiceDetector {
    private static final String TAG = "VadVoiceDetector";

    public static class BufferOverrunException extends Exception {
        public BufferOverrunException(String msg) {
            super(msg);
        }
    }

    private final long mMaxBeginWaitTimeSamples;
    private final IEndOfVoicePolicy mEndOfVoicePolicy;

    private final VadContext mVadContext;
    private final VadAlgorithm.VadResult mVadResult = new VadAlgorithm.VadResult();

    private VadAlgorithm mVad;

    private long mSampleOffset;

    //Vad raw buffer
    private final RingBuffer mRawBuffer;

    //Vad ahead of speech buffer
    private final RingBuffer mNonSpeechBuffer;
    private final int mOutputFrameSize;

    //Voice buffer
    private final RingBuffer mVoiceBuffer;

    //Temporary buffer
    private final short[] mTempRawBuffer;
    private final short[] mTempAheadBuffer;

    //Max voice length
    private final long mMaxVoiceLengthInSamples;

    //Output buffer creator
    private final IBufferFactory mOutputBufferFactory;
    private PreprocessListener mPreprocessListener;

    public VadVoiceDetector(PreprocessListener preprocessListener, VadAlgorithm vad, int sampleRate, long maxVoiceLengthInMillis, int voiceBufferLen, int outputFrameSize, int rawBufferLen, int nonSpeechBufferLen,
                            long maxBeginWaitTimeMillis, IEndOfVoicePolicy endOfVoicePolicy, long firstSampleOffset, boolean newOutputBuffer) {
        mVad = vad;
        mMaxBeginWaitTimeSamples = maxBeginWaitTimeMillis * sampleRate / 1000;
        mEndOfVoicePolicy = endOfVoicePolicy;

        // init detectResult
        mVadContext = new VadContext();

        //Init raw data to be analyzed by VAD
        final Short none = Short.valueOf((short) 0);
        mRawBuffer = new RingBuffer(rawBufferLen, none);
        mTempRawBuffer = new short[rawBufferLen];

        //Init Vad ahead of speech buffer
        mNonSpeechBuffer = new RingBuffer(nonSpeechBufferLen, none);
        mTempAheadBuffer = new short[nonSpeechBufferLen];
        mOutputFrameSize = outputFrameSize;

        //Init voice buffer
        mVoiceBuffer = new RingBuffer(voiceBufferLen, none);
        mMaxVoiceLengthInSamples = sampleRate * maxVoiceLengthInMillis / 1000;

        mSampleOffset = firstSampleOffset;


        mOutputBufferFactory = newOutputBuffer ? new NewBufferFactory.ShortBufferFactory() :
                new CachedBufferFactory.ShortBufferFactory(voiceBufferLen);
        mPreprocessListener = preprocessListener;
    }

    private int addAheadOfVoiceData() throws BufferOverrunException {
        int nonSpeechLength = mNonSpeechBuffer.read(mTempAheadBuffer, 0, mNonSpeechBuffer.getLength());
        return mVoiceBuffer.write(mTempAheadBuffer, 0, nonSpeechLength);
    }

    private int addVoiceData(short[] data, int length) throws BufferOverrunException {
        return mVoiceBuffer.write(data, 0, length);
    }

    private void updateVadContext(VadAlgorithm.VadResult vadResult, int processedLen, VadContext vc) {
        final boolean is_speech = vadResult.voiceFrameCount > 0;

        vc.isSpeech = is_speech;

        if (is_speech) {
            vc.voiceEndWaitSamples = 0;
        } else {
            vc.voiceEndWaitSamples += (vadResult.nonVoiceFrameCount + vadResult.voiceFrameCount) * vadResult.samplesPerFrame;
        }

        vc.isVoiceFirstFound = is_speech && !vc.voiceFound;
        vc.voiceFound = vc.voiceFound || is_speech;

        if (vc.voiceFound) {
            mVadContext.voiceEnded = mEndOfVoicePolicy.isVoiceEnd(mVadContext);
        } else {
            mVadContext.voiceBeginWaitSamples += processedLen;
            mVadContext.beginWaitTimeout = mVadContext.voiceBeginWaitSamples >= mMaxBeginWaitTimeSamples;
            SpeechLogUtil.log("updateVadContext, processedLen:" + processedLen + "    mVadContext.beginWaitTimeout:" + mVadContext.beginWaitTimeout + "   mVadContext.voiceBeginWaitSamples"
                    + mVadContext.voiceBeginWaitSamples + "    mMaxBeginWaitTimeSamples:" + mMaxBeginWaitTimeSamples);

        }
    }

    /**
     * process raw data with vad
     *
     * @param body
     * @return whether vad processed data exceeds the limit
     * @throws BufferOverrunException
     */
    public boolean update(short[] body) throws BufferOverrunException {
        //prepare raw data to do VAD
        int rawWrote = mRawBuffer.write(body, 0, body.length);
        if (rawWrote != body.length) {
            throw new BufferOverrunException("Vad raw buffer overrun!");
        }

        final long inputLenLimit = Math.max(0, (mMaxVoiceLengthInSamples - mVadContext.foundVoiceLengthInSamples));
        boolean cutOff = inputLenLimit <= mRawBuffer.getLength();
        int inputLen = (int) Math.min(inputLenLimit, mRawBuffer.getLength());
        mRawBuffer.peek(mTempRawBuffer, 0, inputLen);
        int processedLen = mVad.detectVoice((++mVadContext.packCount == 1), mTempRawBuffer, inputLen, mVadResult);
        SpeechLogUtil.log(TAG, "nonVoiceFrameCount: " + mVadResult.nonVoiceFrameCount);
        updateVadContext(mVadResult, processedLen, mVadContext);

        if (mVadContext.voiceFound) { // speech detected
            boolean addAheadOfSpeech = mVadContext.isSpeech && mVadContext.isVoiceFirstFound;
            if (addAheadOfSpeech) {
                int aheadLen = addAheadOfVoiceData();
                mVadContext.aheadOfSpeechLengthInSamples = aheadLen;
                mVadContext.foundVoiceLengthInSamples += aheadLen;
            }
            processedLen = addVoiceData(mTempRawBuffer, processedLen);
            mVadContext.foundVoiceLengthInSamples += processedLen;
        } else { //speech not detected yet
            // Store wav ahead of speech
            addNonSpeechWav(mTempRawBuffer, processedLen);

        }

        //save raw data left for next VAD
        // if processedLen < total_raw_len, then left data will be processed by upcoming vad
        mRawBuffer.skipRead(processedLen);

        return cutOff;
    }

    /**
     * 获取320*n的数据，用于speex压缩
     *
     * @param sliceSize
     * @param upRound
     * @return
     */
    private short[] getVoiceData(int sliceSize, boolean upRound) {
        final int available = mVoiceBuffer.getLength();
        if (available <= 0) {
            return null;
        }
        int bufferLen = available;
        final int remains = available % sliceSize;
        if (remains != 0) {
            bufferLen = upRound ? (available - remains + sliceSize) : (available - remains);
        }
        if (bufferLen <= 0) {
            return null;
        }

        short[] result = (short[]) mOutputBufferFactory.newBuffer(bufferLen);
        mVoiceBuffer.read(result, 0, Math.min(bufferLen, available));
        return result;
    }

    private void addNonSpeechWav(short[] data, int length) {
        mNonSpeechBuffer.overWrite(data, 0, length);
    }

    @Override
    public void detect(short[] rawData, int detectOption, VadListener vadListener, Object arg, int onlineAsrMode) {
        final int length = rawData.length;
        final int preRawLen = mRawBuffer.getLength();

        boolean cutOff;
        try {
            cutOff = update(rawData);
            mSampleOffset += length;
        } catch (BufferOverrunException e) {
            e.printStackTrace();
            vadListener.onVadError(ErrorIndex.ERROR_VAD_AUDIO_BUFFER_OVERRUN, "ERROR_VAD_BUFFER_OVERRUN", e, arg);
            return;
        }
        final VadContext vc = this.mVadContext;
        final boolean lastRaw = ((detectOption & DETECT_OPTION_END_OF_SESSION) != 0);
        boolean endOfSession = vc.voiceEnded //Detect voice end
                || lastRaw //Last raw package
                || cutOff //Exceed max voice length
                || vc.beginWaitTimeout; // silence
        if (!vc.voiceFound) {
            if (vc.beginWaitTimeout || lastRaw) {
                SpeechLogUtil.log("vc.beginWaitTimeout:" + vc.beginWaitTimeout + ",lastRaw:" + lastRaw);
                if (vc.beginWaitTimeout) {
                    //短时识别时，才走下面的逻辑
                    if (onlineAsrMode == GrpcAsrManager.ONLINE_ASR_MODE_SHORT) {
                        vadListener.onVadError(ErrorIndex.ERROR_VAD_SPEECH_TIMEOUT, "ERROR_VAD_SPEECH_TIMEOUT", null, null);
                    }

                } else {
                    vadListener.onNoVoiceDetected(lastRaw, arg);
                }
                endOfSession = true;
            }
        } else {
            int flag = 0;
            if (vc.isVoiceFirstFound) {
                flag |= VadListener.VAD_FLAG_VOICE_BEGIN;
                vc.sessionBeginOffsetSample = mSampleOffset - length - preRawLen - vc.aheadOfSpeechLengthInSamples;
                //短时：用户开始说话时，第一次检测到有效声音，回调，输入法显示波形。；长时：每句话开始时，第一次检测到有效声音，回调，输入法显示波形
                if (mPreprocessListener != null) mPreprocessListener.onVadFirstDetected();
            }
            if (endOfSession) {
                flag |= VadListener.VAD_FLAG_VOICE_END;
            }
            if (lastRaw) {
                flag |= VadListener.VAD_FLAG_MANUAL_END;
            }
            short[] output = getVoiceData(mOutputFrameSize, endOfSession);
            if (output == null) {
                output = new short[0];
            }
            vadListener.onNewVoiceDetected(output, flag, vc.sessionBeginOffsetSample, vc.sessionBeginOffsetSample + vc.foundVoiceLengthInSamples - 1, arg);
        }
        if (endOfSession) {
            SpeechLogUtil.loge("vad VadVoiceDetector, endOfSession , reset vad param");
            mVadContext.reset(cutOff);
        }
        final boolean discardPrev = ((detectOption & DETECT_OPTION_DISCARD_PREVIOUS_DATA) != 0);
        if (discardPrev) {
            reset(-1);
        }
    }

    @Override
    public void reset(int sampleOffset) {
        if (sampleOffset >= 0) {
            mSampleOffset = sampleOffset;
        }
        mRawBuffer.clear();
        mNonSpeechBuffer.clear();
        mVoiceBuffer.clear();
        mVadContext.reset(false);
    }

    public interface IEndOfVoicePolicy {
        boolean isVoiceEnd(VadContext context);
    }
}
