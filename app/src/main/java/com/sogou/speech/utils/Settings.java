package com.sogou.speech.utils;

import android.os.Environment;

public class Settings {
    public static String SPEECH_FILE_PATH = Environment.getExternalStorageDirectory() + "/voiceTranslator/speech/";
    public static String SPEECH_FILE_PATH_RAW_AUDIO = SPEECH_FILE_PATH+"raw/";
    public static String SPEECH_FILE_PATH_TTS_AUDIO = SPEECH_FILE_PATH+"tts/";
    public static String APP_ID = "15Dy8oGpZg25DldDipPFqyM5HtQ";
    public static final String APP_KEY = "sLi8bHfErc399IlJzhDsvdR7tmdPb41/+k8Y/195/lfT5khGylyXdRF4CL0eYCMJxoX8DQlRLM96s9ioe+ZG4g==";
    public static final String URL_RECOGNIZE = "api.speech.sogou.com";
    public static final String URL_RECOGNIZE_DEBUG = "canary.speech.sogou.com";
    public static final String userAgent = "android-201811291615";
    public static boolean isDebug = true;
}
