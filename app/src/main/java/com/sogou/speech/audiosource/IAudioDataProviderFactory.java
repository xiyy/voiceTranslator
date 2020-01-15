package com.sogou.speech.audiosource;



public interface IAudioDataProviderFactory {
    IAudioDataProvider create();

    int samplingRateInHz();
    int bufferSizeInBytes();
    int bytesPerFrame();
}
