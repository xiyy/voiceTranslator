package com.sogou.speech.utils;

import android.util.Log;

/**
 * Created by zhouqilin on 16/9/30.
 */

public class SpeexDecoder extends SpeexCodec {
    public SpeexDecoder(){
        setSpeexNative(createDecoder(SPEEX_DEFAULT_BAND_MODE, SPEEX_DEFAULT_QUALITY));
    }

    public int decode(byte[] input, short[] output){
        if (mSpeexNative == 0){
            return -1;
        }
        return decode(mSpeexNative, input, output);
    }

    public short[] decode(byte[] input){
        int outputLen = decodedSizeInSamples(mSpeexNative, input.length);
        if(outputLen <= 0){
            return null;
        }
        short[] result = new short[outputLen];
        decode(input, result);
        return result;
    }

    public void destroy(){
        if (mSpeexNative != 0){
            destroyDecoder(mSpeexNative);
        }
        mSpeexNative = 0;
    }

    @Override
    protected void finalize() throws Throwable {
        if (mSpeexNative != 0){
            Log.i("SpeexDecoder", "!!! SpeexDecoder finalize. Forget to call destroy !!!");
        }
        destroy();
        super.finalize();
    }
}
