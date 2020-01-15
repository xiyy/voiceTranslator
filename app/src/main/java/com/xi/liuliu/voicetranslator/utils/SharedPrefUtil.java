package com.xi.liuliu.voicetranslator.utils;

import android.content.Context;
import android.text.TextUtils;

/**
 * Date:2018/5/25
 * Author:zhangxiaobei
 * Describe:
 */
public class SharedPrefUtil {
    private static final String VOICE_TRANSLATOR_SP_NAME = "voice_translator_sp";
    public static final String KEY_SRC_LANGUAGE = "src_language";
    public static final String KEY_DEST_LANGUAGE = "dest_language";
    public static final String KEY_LANGUAGE_INIT = "language_init";
    public static final String KEY_TOKEN_VALUE = "token_value";
    public static final String KEY_TOKEN_END_TIME = "token_end_time";
    public static final String KEY_TOKEN_AVAILABLE = "token_available";

    public static void putString(Context context, String key, String value) {
        if (context == null)
            throw new IllegalArgumentException("SharedPrefUtil putString context is null");
        if (TextUtils.isEmpty(key))
            throw new IllegalArgumentException("SharedPrefUtil putString key is null");
        context.getSharedPreferences(VOICE_TRANSLATOR_SP_NAME, Context.MODE_PRIVATE).edit().putString(key, value).commit();

    }

    public static String getString(Context context, String key) {
        if (context == null)
            throw new IllegalArgumentException("SharedPrefUtil getString context is null");
        if (TextUtils.isEmpty(key))
            throw new IllegalArgumentException("SharedPrefUtil getString key is null");
        return context.getSharedPreferences(VOICE_TRANSLATOR_SP_NAME, Context.MODE_PRIVATE).getString(key, "");

    }

    public static void putInt(Context context, String key, int value) {
        if (context == null)
            throw new IllegalArgumentException("SharedPrefUtil putInt context is null");
        if (TextUtils.isEmpty(key))
            throw new IllegalArgumentException("SharedPrefUtil putInt key is null");
        context.getSharedPreferences(VOICE_TRANSLATOR_SP_NAME, Context.MODE_PRIVATE).edit().putInt(key, value).commit();
    }

    public static int getInt(Context context, String key) {
        if (context == null)
            throw new IllegalArgumentException("SharedPrefUtil getInt context is null");
        if (TextUtils.isEmpty(key))
            throw new IllegalArgumentException("SharedPrefUtil getInt key is null");
        return context.getSharedPreferences(VOICE_TRANSLATOR_SP_NAME, Context.MODE_PRIVATE).getInt(key, 1);
    }

    public static void putBoolean(Context context, String key, boolean value) {
        if (context == null)
            throw new IllegalArgumentException("SharedPrefUtil putBoolean context is null");
        if (TextUtils.isEmpty(key))
            throw new IllegalArgumentException("SharedPrefUtil putBoolean key is null");
        context.getSharedPreferences(VOICE_TRANSLATOR_SP_NAME, Context.MODE_PRIVATE).edit().putBoolean(key, value).commit();


    }

    public static boolean getBoolean(Context context, String key) {
        if (context == null)
            throw new IllegalArgumentException("SharedPrefUtil putBoolean context is null");
        if (TextUtils.isEmpty(key))
            throw new IllegalArgumentException("SharedPrefUtil putBoolean key is null");
        return context.getSharedPreferences(VOICE_TRANSLATOR_SP_NAME, Context.MODE_PRIVATE).getBoolean(key, false);
    }
}
