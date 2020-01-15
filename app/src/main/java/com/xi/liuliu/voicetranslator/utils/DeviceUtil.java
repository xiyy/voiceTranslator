package com.xi.liuliu.voicetranslator.utils;

import android.os.Build;

public class DeviceUtil {

    /**
     * @return 返回系统版本
     */
    public static String getSystemVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取手机厂商
     *
     * @return
     */

    /**
     * 获取手机型号
     *
     * @return
     */
    public static String getModel() {
        return Build.MODEL;
    }


}
