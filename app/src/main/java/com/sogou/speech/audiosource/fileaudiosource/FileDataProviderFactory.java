package com.sogou.speech.audiosource.fileaudiosource;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.sogou.speech.audiosource.IAudioDataProvider;
import com.sogou.speech.audiosource.IAudioDataProviderFactory;
import com.sogou.speech.utils.SpeechLogUtil;




public class FileDataProviderFactory implements IAudioDataProviderFactory {
    private static final int DEFAULT_BUFFER_SIZE = 4 * 1280;
    private static final int DEFAULT_SAMPLING_RATE_HZ = 16 * 1000;

    final int mAudioSource;
    final int mSampleRateInHz;
    final int mChannelConfig;
    final int mAudioFormat;
    final int mBufferSizeInBytes;
    final String mAudioFilePath;

    public FileDataProviderFactory( int audioSource, int sampleRateInHz, int channelConfig, int audioFormat, int bufferSizeInBytes,
                                   String filePath){
        mAudioSource = audioSource;
        mSampleRateInHz = sampleRateInHz;
        mChannelConfig = channelConfig;
        mAudioFormat = audioFormat;
        mAudioFilePath = filePath;
        if (bufferSizeInBytes <= 0){
            bufferSizeInBytes = Math.max(DEFAULT_BUFFER_SIZE, AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat));
        }
        mBufferSizeInBytes = bufferSizeInBytes;
    }

    public FileDataProviderFactory(String filePath){
        this(MediaRecorder.AudioSource.MIC, DEFAULT_SAMPLING_RATE_HZ,  AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, 0, filePath);
    }

    @Override
    public IAudioDataProvider create() {
        SpeechLogUtil.log("FileDataProviderFactory # create()");
        return new FileDataProvider(mAudioSource, mSampleRateInHz, mChannelConfig, mAudioFormat, mBufferSizeInBytes, mAudioFilePath);
    }

    @Override
    public int samplingRateInHz() {
        return mSampleRateInHz;
    }

    @Override
    public int bufferSizeInBytes() {
        return mBufferSizeInBytes;
    }

    @Override
    public int bytesPerFrame() {
        return mAudioFormat == AudioFormat.ENCODING_PCM_16BIT ? 2 : 1;
    }
}
