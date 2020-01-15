package com.sogou.speech.listener;


public interface GrpcTranslateListener {
    void onGrpcTranslateResult(String srcText, String result);

    void onGrpcTranslateError(int errorCode, String errorMessage);
}
