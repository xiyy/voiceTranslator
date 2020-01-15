package com.sogou.speech.audiosource.fileaudiosource;

import com.sogou.speech.audiosource.IAudioDataProvider;
import com.sogou.speech.utils.SpeechLogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileDataProvider implements IAudioDataProvider {
    //    private AudioRecord mSysRecorder;
    private boolean mInitSucceed;
    private InputStream mInputStream;

    public FileDataProvider(int audioSource, int sampleRateInHz, int channelConfig, int audioFormat, int bufferSizeInBytes, String filePath) {
//        mSysRecorder = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes);
        SpeechLogUtil.log("FileDataProvider constructor");
        try {
            mInputStream = new FileInputStream(new File(filePath));
            if (filePath.endsWith("wav")) {
                mInputStream.skip(44);
            }
            mInitSucceed = true;
            SpeechLogUtil.log("FileDataProvider# mInitSucceed:" + mInitSucceed);
        } catch (Exception e) {
            SpeechLogUtil.loge("Exception in constructor of FileDataProvider:" + e.getMessage());
            mInitSucceed = false;
        }

//        mInitSucceed = mSysRecorder.getState() == AudioRecord.STATE_INITIALIZED;
//        if (!mInitSucceed){
//            mSysRecorder.release();
//            mSysRecorder = null;
//        }
    }

    @Override
    public int read(short[] buffer, int offset, int len) {
//        return mSysRecorder.read(buffer, offset, len);
        throw new IllegalArgumentException("File data provider does not suppport read(short[])");
    }

    @Override
    public int read(byte[] buffer, int offset, int len) {
//        return mSysRecorder.read(buffer, offset, len);
        int readNum = -1;
        try {
            readNum = mInputStream.read(buffer, offset, len);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return readNum;
    }

    @Override
    public boolean isInitialized() {
        return mInitSucceed;
    }

    @Override
    public void start() {
//        if (mSysRecorder != null){
//            mSysRecorder.startRecording();
//            SpeechLogUtil.log("AudioRecord#start recording");
//        }
    }

    @Override
    public void stop() {
//        if (mSysRecorder != null) {
//            mSysRecorder.stop();
//        }
    }

    @Override
    public void release() {
//        if (mSysRecorder != null) {
//            mSysRecorder.release();
//        }
    }
}
