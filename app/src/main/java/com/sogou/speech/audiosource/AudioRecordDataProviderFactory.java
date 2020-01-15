package com.sogou.speech.audiosource;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.sogou.speech.listener.AudioRecordListener;
import com.sogou.speech.utils.DeviceUtil;
import com.sogou.speech.utils.SpeechLogUtil;



public class AudioRecordDataProviderFactory implements IAudioDataProviderFactory {

    private final String tag = "AudioRecordDataProviderFactory";

    private static final int DEFAULT_BUFFER_SIZE = 4 * 1024;
    private static final int DEFAULT_SAMPLING_RATE_HZ = 16 * 1000;
    private static final int LOW_SAMPLING_RATE_HZ = 8 * 1000;

    public static final int CHANNEL_MONO = AudioFormat.CHANNEL_IN_MONO;
    public static final int CHANNEL_STEREO = AudioFormat.CHANNEL_IN_STEREO;

    public static final int AUDIO_FORMAT_16BIT = AudioFormat.ENCODING_PCM_16BIT;

    final int mAudioSource;
    final int mSampleRateInHz;
    final int mChannelConfig;
    final int mAudioFormat;
    final int mBufferSizeInBytes;
    final boolean mHardwareSupported;
    final boolean mUseStereo;
    private AudioRecordListener mAudioRecorderListener;

    public AudioRecordDataProviderFactory(int audioSource, int sampleRateInHz, int channelConfig, int audioFormat, int bufferSizeInBytes){
        mAudioSource = audioSource;
        mSampleRateInHz = sampleRateInHz;
        mChannelConfig = channelConfig;
        mAudioFormat = audioFormat;
        if (bufferSizeInBytes <= 0){
            bufferSizeInBytes = Math.max(DEFAULT_BUFFER_SIZE, AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat));
        }
        mBufferSizeInBytes = bufferSizeInBytes;
        mHardwareSupported = true;
        mUseStereo = false;
    }

    public AudioRecordDataProviderFactory(AudioRecordListener audioRecordListener){
        //try only the intended params, if params not supported by device, IllegalArgumentException will be thrown
//        this(MediaRecorder.AudioSource.MIC, DEFAULT_SAMPLING_RATE_HZ,  AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, 0);

        int[] sampleRate = new int[1];
        int[] channelConfig = new int[1];
        int[] audioFormat = new int[1];
        int[] bufferSizeInBytes = new int[1];
        boolean[] useStereo = new boolean[1];

        mHardwareSupported = chooseAudioParams(sampleRate, channelConfig, audioFormat, bufferSizeInBytes, useStereo);
        mAudioSource = MediaRecorder.AudioSource.MIC;
        mSampleRateInHz = sampleRate[0];
        mChannelConfig = channelConfig[0];
        mAudioFormat = audioFormat[0];
        mUseStereo = useStereo[0];
        mBufferSizeInBytes = Math.max(DEFAULT_BUFFER_SIZE, bufferSizeInBytes[0]);
        mAudioRecorderListener = audioRecordListener;
    }

    /**
     * try to find suitable AudioRecord parameter supported by hardware
     * @param sampleRate
     * @param channelConfig
     * @param audioFormat
     * @param bufferSizeInBytes
     * @param useStereo
     * @return true if the chosen parameter is supported by device
     *         false if all combinations of parameters are not supported by device
     */
    private boolean chooseAudioParams(int[] sampleRate, int[] channelConfig, int[] audioFormat, int[] bufferSizeInBytes, boolean[] useStereo){

        SpeechLogUtil.log(tag, "chooseAudioParams");
        int localSampleRate = DEFAULT_SAMPLING_RATE_HZ;
        int localChannelConfig = CHANNEL_STEREO;
        int localAudioFormat = AUDIO_FORMAT_16BIT;
        boolean stereo = true;
        int localMinBufferSize;
        if (!DeviceUtil.isNexusPhone()) {
            //1: 16k, stereo, 16bit
            localMinBufferSize = AudioRecord.getMinBufferSize(localSampleRate, localChannelConfig, localAudioFormat);
            if(localMinBufferSize <= 0){
                //2: 16k, mono, 16bit
                localChannelConfig = CHANNEL_MONO;
                stereo = false;
                localMinBufferSize = AudioRecord.getMinBufferSize(localSampleRate, localChannelConfig, localAudioFormat);
            }
        }else {
            localChannelConfig = CHANNEL_MONO;
            stereo = false;
            localMinBufferSize = AudioRecord.getMinBufferSize(localSampleRate, localChannelConfig, localAudioFormat);
        }
        if(localMinBufferSize <= 0){
            //3: 8k , mono, 16bit
            localChannelConfig = CHANNEL_MONO;
            localSampleRate = LOW_SAMPLING_RATE_HZ;
            stereo = false;
            localMinBufferSize = AudioRecord.getMinBufferSize(localSampleRate, localChannelConfig, localAudioFormat);
        }
        sampleRate[0] = localSampleRate;
        channelConfig[0] = localChannelConfig;
        audioFormat[0] = localAudioFormat;
        bufferSizeInBytes[0] = localMinBufferSize;
        useStereo[0] = stereo;

        SpeechLogUtil.log(tag,"sample rate:"+localSampleRate+", channel config:"+localChannelConfig+",audio format:"+localAudioFormat+", buffer size in bytes:"+localMinBufferSize+",stereo:"+stereo);

        return localMinBufferSize > 0;
    }



    @Override
    public IAudioDataProvider create() {
        return new AudioRecordDataProvider(mAudioSource, mSampleRateInHz, mChannelConfig, mAudioFormat, mBufferSizeInBytes, mUseStereo, mHardwareSupported,mAudioRecorderListener);
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
