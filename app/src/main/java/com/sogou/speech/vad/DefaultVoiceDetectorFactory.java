package com.sogou.speech.vad;

import android.text.TextUtils;

import com.sogou.speech.framework.GrpcAsrManager;
import com.sogou.speech.listener.PreprocessListener;
import com.sogou.speech.utils.SpeechLogUtil;

import java.util.Locale;



public class DefaultVoiceDetectorFactory {
    private static final int PACKAGE_LEN_IN_SAMPLES = 10240;
    private static final int PRE_RESERVE_LEN = 2560;
    private static final int FFT_SIZE = 512;
    private static final int FREQ_WIN_SIZE = 8;
    private static final double ALFA_FF = 0.8;
    private static final double ALFA_SF = 0.995;
    private static final double BETA_SF = 0.96;
    private static final double ALFA_SNR = 0.96;
    private static final double THRES_02 = 2.2;
    private static final double THRES_24 = 2.5;
    private static final double THRES_46 = 3.5;
    private static final double THRES_68 = 4.0;
    private static final int INITAL_FNUM = 20;
    private static final float DB_THRES = 50;

    private static final long MAX_BEGIN_WAIT_TIME_MILLIS = 3000;
    private static final long DYNAMIC_END_POLICY_MAX_END_WAIT_TIME_MILLIS = 2000;
    private static final long DYNAMIC_END_POLICY_MIN_END_WAIT_TIME_MILLIS = 300;


    private static final long CONST_END_POLICY_MIN_END_WAIT_TIME_MILLIS = 300;


    // max audio time: 2 min
    private static final int MAX_AUDIO_TIME_MILLIS = 60 * 2 * 1000;


    public static IVoiceDetector create(PreprocessListener preprocessListener, int sampleRate, int outputFrameSize, long firstSampleOffset, long maxBeginWaitTimeMillis, VadVoiceDetector.IEndOfVoicePolicy endWaitPolicy,
                                        long maxSessionDurationMillis, int rawPackageSizeInSamples, boolean newOutputBuffer) {
        double dbThresh = DB_THRES;
        //sensitive ssf_mic for nexus 4
        if (TextUtils.equals(android.os.Build.MODEL, "Nexus 4")) {
            dbThresh = 55;
        }
        final int windowSize = sampleRate / 40;
        final int shiftSize = sampleRate / 100;
        VadAlgorithm vad = new VadAlgorithm(sampleRate, windowSize, shiftSize, ALFA_FF, ALFA_SF, BETA_SF, ALFA_SNR,
                THRES_02, THRES_24, THRES_46, THRES_68, FFT_SIZE, FREQ_WIN_SIZE, INITAL_FNUM, dbThresh);
        maxSessionDurationMillis = maxSessionDurationMillis <= 0 ? MAX_AUDIO_TIME_MILLIS : maxSessionDurationMillis;

        rawPackageSizeInSamples = rawPackageSizeInSamples <= 0 ? PACKAGE_LEN_IN_SAMPLES : rawPackageSizeInSamples;
        final int nonSpeechBufferLen = rawPackageSizeInSamples + windowSize + INITAL_FNUM * shiftSize; /*+ rawPackageSizeInSamples*/
        final int rawBufferLen = rawPackageSizeInSamples * 2 + Math.max(windowSize, rawPackageSizeInSamples);
        final long outputBufferLen = outputFrameSize + nonSpeechBufferLen + rawBufferLen;/*sampleRate * maxSessionDurationMillis / 1000 ;*/

        return new VadVoiceDetector(preprocessListener, vad, sampleRate, maxSessionDurationMillis, (int) outputBufferLen, outputFrameSize, rawBufferLen, nonSpeechBufferLen,
                maxBeginWaitTimeMillis, endWaitPolicy, firstSampleOffset, newOutputBuffer);
    }

    public static IVoiceDetector create(PreprocessListener preprocessListener, int sampleRate, int outputFrameSize, long firstSampleOffset, long maxSessionDurationMillis, int rawPackageSizeInSamples,
                                        boolean newOutputBuffer, long maxEndWaitTimeMillis) {
        return create(preprocessListener, sampleRate, outputFrameSize, firstSampleOffset, MAX_BEGIN_WAIT_TIME_MILLIS, new ConstVoiceEndWaitPolicy(maxEndWaitTimeMillis <= 0 ? CONST_END_POLICY_MIN_END_WAIT_TIME_MILLIS : maxEndWaitTimeMillis, sampleRate),
                maxSessionDurationMillis, rawPackageSizeInSamples, newOutputBuffer);
    }

    public static IVoiceDetector createDynamicEndCheck(PreprocessListener preprocessListener, int sampleRate, int outputFrameSize, long firstSampleOffset, long maxSessionDurationMillis, int rawPackageSizeInSamples,
                                                       boolean newOutputBuffer, long maxEndWaitTimeMillis, long minEndWaitTimeMillis) {
        return create(preprocessListener, sampleRate, outputFrameSize, firstSampleOffset, MAX_BEGIN_WAIT_TIME_MILLIS,
                new DynamicVoiceEndWaitPolicy(maxEndWaitTimeMillis, minEndWaitTimeMillis, sampleRate, maxSessionDurationMillis),
                maxSessionDurationMillis, rawPackageSizeInSamples, newOutputBuffer);
    }

    public static class ConstVoiceEndWaitPolicy implements VadVoiceDetector.IEndOfVoicePolicy {
        private final long mMaxEndWaitTimeSamples;

        public static ConstVoiceEndWaitPolicy create() {
            return new ConstVoiceEndWaitPolicy(CONST_END_POLICY_MIN_END_WAIT_TIME_MILLIS, 16000);
        }

        public ConstVoiceEndWaitPolicy(long maxEndWaitTimeMillis, int sampleRate) {
            mMaxEndWaitTimeSamples = maxEndWaitTimeMillis * sampleRate / 1000;
        }

        @Override
        public boolean isVoiceEnd(VadContext context) {
            return context.voiceEndWaitSamples >= mMaxEndWaitTimeSamples;
        }
    }

    /**
     * 时间越往后，等价的maxEndWaitTime越小，vad越容易停止
     */
    public static class DynamicVoiceEndWaitPolicy implements VadVoiceDetector.IEndOfVoicePolicy {
        private final long mMaxEndWaitTimeSamples;
        private final long mMinEndWaitTimeSamples;
        private final long mMaxVoiceTimeSamples;

        public static DynamicVoiceEndWaitPolicy create(int onlineAsrMode) {
            //为了整合长时、短时VAD，添加参数onlineAsrMode，短时识别时，maxEndWaitTimeMillis设置为1.17秒；长时识别时，maxEndWaitTimeMillis设置为2秒
            long maxEndWaitTimeMillis;
            if (onlineAsrMode== GrpcAsrManager.ONLINE_ASR_MODE_SHORT) {
                maxEndWaitTimeMillis = 1170;
            }else {
                maxEndWaitTimeMillis = DYNAMIC_END_POLICY_MAX_END_WAIT_TIME_MILLIS;
            }
            return new DynamicVoiceEndWaitPolicy(maxEndWaitTimeMillis, DYNAMIC_END_POLICY_MIN_END_WAIT_TIME_MILLIS, 16000, MAX_AUDIO_TIME_MILLIS);
        }

        public DynamicVoiceEndWaitPolicy(long maxEndWaitTimeMillis, long minEndWaitTimeMillis, int sampleRate, long maxVoiceTimeMillis) {
            mMaxEndWaitTimeSamples = maxEndWaitTimeMillis * sampleRate / 1000;
            mMinEndWaitTimeSamples = minEndWaitTimeMillis * sampleRate / 1000;
            mMaxVoiceTimeSamples = maxVoiceTimeMillis * sampleRate / 1000;
        }

        @Override
        public boolean isVoiceEnd(VadContext context) {
//            SpeechLogUtil.log("vad DynamicVoiceEndWaitPolicy, isVoiceEnd(), mMaxVoiceTimeSamples:"+mMaxVoiceTimeSamples+",foundVoiceLengthInSamples:"+context.foundVoiceLengthInSamples);
            final long remains = mMaxVoiceTimeSamples - context.foundVoiceLengthInSamples;
            if (remains <= 0) {
                return true;
            }
            final long fraction = mMaxVoiceTimeSamples / remains;
            final long dynamicMaxEndWaitSample = mMaxEndWaitTimeSamples / fraction;
            final long endWaitSample = Math.max(dynamicMaxEndWaitSample, mMinEndWaitTimeSamples);
            SpeechLogUtil.log(String.format(Locale.getDefault(), "vad DynamicVoiceEndWaitPolicy, isVoiceEnd,context.voiceEndWaitSamples:%d, endWaitSample:%d",
                    context.voiceEndWaitSamples, endWaitSample));
            return context.voiceEndWaitSamples >= endWaitSample;
        }
    }

}