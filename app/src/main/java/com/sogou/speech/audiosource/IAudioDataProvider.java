package com.sogou.speech.audiosource;



public interface IAudioDataProvider {
    int read(short[] buffer, int offset, int len);
    int read(byte[] buffer, int offset, int len);

    boolean isInitialized();
    void start();
    void stop();
    void release();
}
