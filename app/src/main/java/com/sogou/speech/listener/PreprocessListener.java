package com.sogou.speech.listener;


public interface PreprocessListener {


    /**
     * 短时：用户开始说话时，第一次检测到有效声音，回调，输入法显示波形。；长时：每句话开始时，第一次检测到有效声音，回调，输入法显示波形
     */
    void onVadFirstDetected();

    void onVadError(int errorCode, String errorMessage);

    void onSpeexError(int errorCode, String errorMessage);


}
