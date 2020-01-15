package com.sogou.speech.utils;

import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class AudioSaver {
    private static final String TAG = "AudioSaver";
    private static String mAudioFilePath = Settings.SPEECH_FILE_PATH_RAW_AUDIO;
    private static ByteArrayOutputStream mByteArrayOutputStream;

    /**
     * 在子线程中调用，预防在创建文件目录时，时间过长出现ANR
     * @param audioFilePath 保存的音频文件所在的目录
     * @return init成功与否
     */
    public static void init(String audioFilePath) {
        if (Settings.isDebug) {
            if (TextUtils.isEmpty(audioFilePath))
                throw new IllegalArgumentException("AudioSaver.init(Context context, String audioFilePath),audioFilePath==null ");
            mAudioFilePath = audioFilePath;
            FileOperator.createDirectory(audioFilePath, true, false);
        }


    }

    public static synchronized void storeDataToStream(short[] data) {
        if (Settings.isDebug) {
            if (data == null || data.length == 0)
                throw new IllegalArgumentException("AudioSaver # empty audio data");
            if (mByteArrayOutputStream == null) {
                mByteArrayOutputStream = new ByteArrayOutputStream();
            }
            if (mByteArrayOutputStream != null) {
                byte[] byteData = new byte[data.length * 2];
                ByteBuffer.wrap(byteData).order(ByteOrder.nativeOrder()).asShortBuffer().put(data);
                try {
                    mByteArrayOutputStream.write(byteData);
                    mByteArrayOutputStream.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                    SpeechLogUtil.loge(TAG, "storeDataToStream Exception");
                }
            }
        }

    }


    public static synchronized String storeWav() {
        if (Settings.isDebug) {
            if (mByteArrayOutputStream != null) {
                String wavPath = mAudioFilePath + System.currentTimeMillis() + ".wav";
                SpeechLogUtil.log(TAG, "file store path:" + wavPath);
                FileOutputStream fileOutputStream;
                try {
                    fileOutputStream = new FileOutputStream(new File(wavPath));
                    byte[] byteData = mByteArrayOutputStream.toByteArray();
                    WavUtil.constructWav(fileOutputStream, ByteOrder.nativeOrder(), byteData, 16000, 1);
                    mByteArrayOutputStream.close();
                    mByteArrayOutputStream = null;
                    return wavPath;
                } catch (Exception e) {
                    e.printStackTrace();
                    SpeechLogUtil.loge(TAG, "storeWav() Exception");
                }
            }
        }

        return null;

    }


    public static synchronized String storePcm() {
        if (Settings.isDebug) {
            return storePcm(16000);
        }
        return null;
    }

    /**
     * @param sampleRate 采样率，16000 or 8000
     * @return 音频路径
     */
    public static synchronized String storePcm(int sampleRate) {
        if (Settings.isDebug) {
            if (sampleRate == 8000 || sampleRate == 16000) {
                if (mByteArrayOutputStream != null) {
                    String wavPath = mAudioFilePath + System.currentTimeMillis() + ".pcm";
                    SpeechLogUtil.log(TAG, "file store path:" + wavPath);
                    FileOutputStream fileOutputStream;
                    try {
                        fileOutputStream = new FileOutputStream(new File(wavPath));
                        byte[] byteData = mByteArrayOutputStream.toByteArray();
                        WavUtil.constructPcm(fileOutputStream, ByteOrder.nativeOrder(), byteData, sampleRate, 1);
                        mByteArrayOutputStream.close();
                        mByteArrayOutputStream = null;
                        return wavPath;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                throw new IllegalArgumentException("sampleRate不合法，只能8k或者16k");
            }
        }

        return null;
    }
}
