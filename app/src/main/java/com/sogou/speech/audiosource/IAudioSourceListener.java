package com.sogou.speech.audiosource;

/**
 * Created by zhouqilin on 16/9/22.
 */

public interface IAudioSourceListener {
    /**
     * Flag indicating end of one session.
     * Eg. Recoding stopped or exceeds max recording time
     */
    int AUDIO_DATA_FLAG_SESSION_END = 1;

    void onSpeechBegin(IAudioSource audioSource);

    /**
     * Called when new data is ready
     * @param audioSource Audio data source
     * @param dataArray short[] or byte[] contains raw audio data
     * @param packIndex the sequence number of raw audio data
     * @param sampleIndex the sequence number of the first sample in dataArray.
     *                    total sample count = sampleIndex + dataArray.length
     * @param flag the data flag. Eg. AUDIO_DATA_FLAG_SESSION_END means end of one session
     */
    void onSpeechNewData(IAudioSource audioSource, Object dataArray, long packIndex, long sampleIndex, int flag);
    void onSpeechEnd(IAudioSource audioSource, int status, Exception e, long sampleCount);
    void onAudioError(int errorCode,String errorMessage);
}
