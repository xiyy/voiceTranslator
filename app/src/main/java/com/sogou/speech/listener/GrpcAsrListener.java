package com.sogou.speech.listener;

public interface GrpcAsrListener {
    void onGrpcAsrResult(String result,boolean isLast);
    void onGrpcAsrError(int errorCode, String errorMessage);
}
