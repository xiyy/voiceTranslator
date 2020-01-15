package com.sogou.speech.audiosource;

import android.media.AudioRecord;

import com.sogou.speech.listener.AudioRecordListener;
import com.sogou.speech.utils.ErrorIndex;
import com.sogou.speech.utils.SpeechLogUtil;




public class AudioRecordDataProvider implements IAudioDataProvider {

    private static final String TAG = "AudioRecordDataProvider";
    private volatile AudioRecord mSysRecorder;
    private boolean mInitSucceed;
    private boolean mUseStereo;
    private AudioRecordListener mAudioRecorderListener;

    public AudioRecordDataProvider(int audioSource, int sampleRateInHz, int channelConfig, int audioFormat, int bufferSizeInBytes, boolean useStereo, boolean harewareSupported, AudioRecordListener audioRecorderListener) {
        this.mAudioRecorderListener = audioRecorderListener;
        releaseAudioRecord();
        try {
            if (useStereo) {//16k, stereo, 16bit
                mSysRecorder = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes * 2);
                if (mSysRecorder == null || (mSysRecorder != null && mSysRecorder.getState() != AudioRecord.STATE_INITIALIZED)) {
                    releaseAudioRecord();
                    mSysRecorder = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes);
                }
            } else {//16k, mono, 16bit或者8k , mono, 16bit
                mSysRecorder = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes);
            }
            if (mSysRecorder != null) {
                mInitSucceed = mSysRecorder.getState() == AudioRecord.STATE_INITIALIZED;
            }
            mUseStereo = useStereo;
            if (!mInitSucceed) {
                mSysRecorder.release();
                mSysRecorder = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            SpeechLogUtil.loge(TAG, "new AudioRecord Exception:" + e.getMessage());
            if (audioRecorderListener != null) {
                audioRecorderListener.onAudioRecordError(ErrorIndex.ERROR_AUDIO_IS_NULL, "new AudioRecord Exception:" + e.getMessage());
            }
            release();
            return;
        }
    }

    @Override
    public int read(short[] buffer, int offset, int len) {
        int readLen = mSysRecorder.read(buffer, offset, len);
        SpeechLogUtil.log(TAG, "SysRecorder.read  length:" + readLen);
        if (mUseStereo) {
            short[] convertToMono = new short[readLen / 2];
            if (mUseStereo) {
                for (int i = 0; i < readLen; i += 2) {
                    convertToMono[i / 2] = (short) ((buffer[i] + buffer[i + 1]) / 2);
                }
            }
            System.arraycopy(convertToMono, 0, buffer, 0, readLen / 2);
        }

        return mUseStereo ? readLen / 2 : readLen;
    }

    @Override
    public int read(byte[] buffer, int offset, int len) {
        int readLen = mSysRecorder.read(buffer, offset, len);
        if (mUseStereo) {
            byte[] convertToMono = new byte[readLen / 2];
            if (mUseStereo) {
                for (int i = 0; i < readLen; i += 2) {
                    convertToMono[i / 2] = (byte) ((buffer[i] + buffer[i + 1]) / 2);
                }
            }
            System.arraycopy(convertToMono, 0, buffer, 0, readLen / 2);
        }

        return mUseStereo ? readLen / 2 : readLen;
    }

    @Override
    public boolean isInitialized() {
        return mInitSucceed;
    }

    @Override
    public void start() {
        if (mSysRecorder != null && mSysRecorder.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
            return;
        }
        SpeechLogUtil.log(TAG, "AudioRecordDataProvider.start()");
        if (mSysRecorder != null) {
            try {
                mSysRecorder.startRecording();
                if (mAudioRecorderListener != null) {
                    mAudioRecorderListener.onAudioRecordStart();
                }
            } catch (Exception e) {
                e.printStackTrace();
                SpeechLogUtil.loge(TAG, "AudioRecord.startRecording(),Exception:" + e.getMessage());
                if (mAudioRecorderListener != null) {
                    mAudioRecorderListener.onAudioRecordError(ErrorIndex.ERROR_AUDIO_START_FAILED, "AudioRecord.startRecording() Exception:" + e.getMessage());
                    mAudioRecorderListener = null;
                }
            }
        }
    }

    @Override
    public void stop() {
        if (mSysRecorder != null) {
            try {
                mSysRecorder.stop();
                if (mAudioRecorderListener != null) {
                    mAudioRecorderListener.onAudioRecordStop();
                }
            } catch (Exception e) {
                e.printStackTrace();
                SpeechLogUtil.loge(TAG, "AudioRecord.stop(),Exception");
                if (mAudioRecorderListener != null) {
                    mAudioRecorderListener.onAudioRecordError(ErrorIndex.ERROR_AUDIO_STOP_FAILED, "AudioRecord.stop() Exception:" + e.getMessage());
                    mAudioRecorderListener = null;
                }
            }

        }
    }

    @Override
    public void release() {
        if (mSysRecorder != null) {
            try {
                mSysRecorder.release();
                if (mAudioRecorderListener != null) {
                    mAudioRecorderListener.onAudioRecordRelease();
                    mAudioRecorderListener = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                SpeechLogUtil.loge(TAG, "AudioRecord.release() Exception:" + e.getMessage());
                if (mAudioRecorderListener != null) {
                    mAudioRecorderListener.onAudioRecordError(ErrorIndex.ERROR_AUDIO_RELEASE_FAILED, "AudioRecord.release() Exception:" + e.getMessage());
                    mAudioRecorderListener = null;
                }
            }
            mSysRecorder = null;
        }
    }

    public void releaseAudioRecord() {
        if (mSysRecorder != null) {
            try {
                if (mSysRecorder.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                    mSysRecorder.stop();
                }
            } catch (Exception e) {
                SpeechLogUtil.loge(TAG, "AudioRecord.stop(),Exception:" + e.getMessage());
                e.printStackTrace();
                if (mAudioRecorderListener != null) {
                    mAudioRecorderListener.onAudioRecordError(ErrorIndex.ERROR_AUDIO_STOP_FAILED, "AudioRecord.stop() Exception:" + e.getMessage());
                    mAudioRecorderListener = null;
                }

            } finally {
                try {
                    mSysRecorder.release();
                } catch (Exception e) {
                    e.printStackTrace();
                    SpeechLogUtil.loge(TAG, "AudioRecord.release(),Exception:" + e.getMessage());
                    if (mAudioRecorderListener != null) {
                        mAudioRecorderListener.onAudioRecordError(ErrorIndex.ERROR_AUDIO_RELEASE_FAILED, "AudioRecord.release() Exception:" + e.getMessage());
                        mAudioRecorderListener = null;
                    }
                }

            }
            mSysRecorder = null;
        }
    }
}
