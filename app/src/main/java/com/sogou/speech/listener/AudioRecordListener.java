package com.sogou.speech.listener;


public interface AudioRecordListener {
    /**
     * 录音开始
     */
    void onAudioRecordStart();

    /**
     * 读取到录音数据
     *
     * @param audioDataArray 读取的录音数据
     */
    void onAudioDataReceived(short[] audioDataArray);

    /**
     * 录音过程中报错
     *
     * @param errorCode    错误码
     * @param errorMessage 错误信息
     */

    void onAudioRecordError(int errorCode, String errorMessage);

    /**
     * 音频的分贝
     *
     * @param decibel 当前音量的分贝值,值域为0 dB 到90.3 dB
     */
    void onVoiceDecibelChanged(double decibel);

    /**
     * 录音结束
     */
    void onAudioRecordStop();

    /**
     * 释放录音资源
     */
    void onAudioRecordRelease();

}
