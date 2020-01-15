package com.sogou.speech.vad;



public interface VadListener {
    int VAD_FLAG_VOICE_BEGIN = 1;
    int VAD_FLAG_VOICE_END = 2;
    int VAD_FLAG_MANUAL_END = 4;

    void onNewVoiceDetected(short[] data, int flag, long sessionBeginSampleOffset, long sessionEndSampleOffset, Object arg);

    void onNoVoiceDetected(boolean isSessionEnd, Object arg);

    void onVadError(int errorCode, String errorMsg, Exception ex, Object arg);
}
