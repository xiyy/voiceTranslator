package com.sogou.speech.listener;


public interface GrpcTtsListener {
    void onTtsSuccess(byte[] data, boolean isLast);
    void onTtsFailed(int errorCode, String errorMessage);
}
