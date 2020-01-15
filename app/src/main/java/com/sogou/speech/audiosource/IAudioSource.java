package com.sogou.speech.audiosource;



public interface IAudioSource {
    void addAudioSourceListener(IAudioSourceListener listener);
    void removeAudioSourceListener(IAudioSourceListener listener);
    void clearAudioSourceListeners();

    int bytesPerSecond();

    int start();
    int pause();
    int stop();
}
