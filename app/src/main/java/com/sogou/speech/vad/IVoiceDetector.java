package com.sogou.speech.vad;



public interface IVoiceDetector {
    // last part of current session
    int DETECT_OPTION_END_OF_SESSION = 1;

    // Discard unprocessed raw data of last session
    int DETECT_OPTION_DISCARD_PREVIOUS_DATA = 2;

    void detect(short[] rawData, int detectOption, VadListener listener, Object arg,int onlineAsrMode);

    // Reset status of the voice detector and set new sample offset.
    // If sampleOffset < 0, the new sample offset is ignored
    void reset(int sampleOffset);
}
