package com.xi.liuliu.voicetranslator.utils;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

/**
 * Date:2019/7/31
 * Author:zhangxiaobei
 * Describe:
 */
public class AssetsUtil {
    private static final String TAG = AssetsUtil.class.getSimpleName();

    public static InputStream getAssetsInputStreamByPath(Context context, String fileName) {
        InputStream inputStream = null;
        if (context != null) {
            try {
                inputStream = context.getAssets().open(fileName);
            } catch (IOException e) {
                e.printStackTrace();
                LogUtil.loge(TAG, "IOException:" + e.getMessage());
            }
        }

        return inputStream;

    }
}
