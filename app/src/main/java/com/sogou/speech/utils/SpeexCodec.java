package com.sogou.speech.utils;



public class SpeexCodec {

    public static final int SPEEX_DEFAULT_BAND_MODE = 1;
    public static final int SPEEX_DEFAULT_QUALITY = 7;

    static {
        System.loadLibrary("speex");
    }

    protected long mSpeexNative;

    protected void setSpeexNative(long speexNative){
        mSpeexNative = speexNative;
    }

    public int samplesPerFrame(){
        return samplesPerFrame(mSpeexNative);
    }

    public int bytesPerFrame(){
        return bytesPerFrame(mSpeexNative);
    }

    public int encodedSizeInBytes(int sampleCount){
        return encodedSizeInBytes(mSpeexNative, sampleCount);
    }

    public int decodedSizeInSamples(int sampleCount){
        return decodedSizeInSamples(mSpeexNative, sampleCount);
    }

    native long createEncoder(int bandMode, int quality);
    native void destroyEncoder(long encoderPtr);

    native long createDecoder(int bandMode, int quality);
    public native void destroyDecoder(long encoderPtr);

    native int samplesPerFrame(long codecPtr);
    native int bytesPerFrame(long codecPtr);
    native int encodedSizeInBytes(long codecPtr, int sampleCount);
    native int decodedSizeInSamples(long codecPtr, int byteCount);

    native int encode(long encoderPtr, short[] input, byte[] output);
    native int decode(long decoderPtr,byte[] input, short[] output);
}
