package com.xi.liuliu.voicetranslator.bean;

import java.io.Serializable;

/**
 * Date:2019/8/1
 * Author:zhangxiaobei
 * Describe:
 */
public class VoiceTranslateResult implements Serializable{
    private String asrResult;
    private String translateResult;
    private String ttsFilePath;
    private int type;
    public static final int TYPE_SRC = 0;
    public static final int TYPE_DEST = 1;

    public VoiceTranslateResult(String asrResult, String translateResult, String ttsFilePath) {
        this.asrResult = asrResult;
        this.translateResult = translateResult;
        this.ttsFilePath = ttsFilePath;
    }

    public VoiceTranslateResult(String asrResult, String translateResult, String ttsFilePath, int type) {
        this(asrResult, translateResult, ttsFilePath);
        this.type = type;
    }

    public String getAsrResult() {
        return asrResult;
    }

    public void setAsrResult(String asrResult) {
        this.asrResult = asrResult;
    }

    public String getTranslateResult() {
        return translateResult;
    }

    public void setTranslateResult(String translateResult) {
        this.translateResult = translateResult;
    }

    public void setTtsFilePath(String ttsFilePath) {
        this.ttsFilePath = ttsFilePath;
    }

    public String getTtsFilePath() {
        return ttsFilePath;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


}
