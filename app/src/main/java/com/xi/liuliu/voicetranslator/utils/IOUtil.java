package com.xi.liuliu.voicetranslator.utils;

import java.io.IOException;
import java.util.Scanner;

/**
 * Date:2019/7/31
 * Author:zhangxiaobei
 * Describe:
 */
public class IOUtil {
    private static final String TAG = IOUtil.class.getSimpleName();

    /**
     * input 流转换为字符串
     *
     * @param is
     * @return
     */
    public static String convertStreamToString(java.io.InputStream is) {
        String s = null;
        if (is == null) return s;
        try {
            Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A");
            if (scanner.hasNext()) {
                s = scanner.next();
            }
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.loge(TAG, "IOException:" + e.getMessage());
        }
        return s;
    }

}
