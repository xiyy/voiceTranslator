package com.xi.liuliu.voicetranslator.utils;

import android.text.TextUtils;
import android.util.Log;

/**
 * Date:2018/5/24
 * Author:zhangxiaobei
 * Describe:
 */
public class LogUtil {
    private static final String DEFAULT_TAG = "VoiceTranslator";
    private static boolean isDebug = true;

    public static void log(String content) {
        if (TextUtils.isEmpty(content)) return;
        if (isDebug) Log.d(DEFAULT_TAG, content);
    }


    public static void log(String tag, String content) {
        if (TextUtils.isEmpty(content)) return;
        if (isDebug) Log.d(tag, content);

    }


    public static void logw(String content) {
        if (TextUtils.isEmpty(content)) return;
        if (isDebug) Log.w(DEFAULT_TAG, content);

    }


    public static void logw(String tag, String content) {
        if (TextUtils.isEmpty(content)) return;
        if (isDebug) Log.w(tag, content);

    }

    public static void loge(String content) {
        if (TextUtils.isEmpty(content)) return;
        if (isDebug) Log.e(DEFAULT_TAG, content);

    }

    public static void loge(String tag, String content) {
        if (TextUtils.isEmpty(content)) return;
        if (isDebug) Log.e(tag, content);
    }

    public static void setDebug(boolean isDebug) {
        LogUtil.isDebug = isDebug;
    }

    public static boolean isDebug() {
        return isDebug;
    }

}
