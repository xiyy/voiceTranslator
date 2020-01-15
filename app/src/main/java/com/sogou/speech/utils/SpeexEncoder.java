package com.sogou.speech.utils;



public class SpeexEncoder extends SpeexCodec {
    public SpeexEncoder(){
        setSpeexNative(createEncoder(SPEEX_DEFAULT_BAND_MODE, SPEEX_DEFAULT_QUALITY));
    }

    public int encode(short[] input, byte[] output){
        if (mSpeexNative == 0){
            return -1;
        }
        return encode(mSpeexNative, input, output);
    }

    public byte[] encode(short[] input){
        int outputLen = encodedSizeInBytes(mSpeexNative, input.length);
        if(outputLen <= 0){
            return null;
        }
        byte[] result = new byte[outputLen];
        encode(input, result);
        return result;
    }

    public void destroy(){
        if (mSpeexNative != 0){
            destroyEncoder(mSpeexNative);
        }
        mSpeexNative = 0;
        SpeechLogUtil.log("SpeexEncoder","destroy() mSpeexNative = 0");
    }

    @Override
    protected void finalize() throws Throwable {
        destroy();
        super.finalize();
    }
}
