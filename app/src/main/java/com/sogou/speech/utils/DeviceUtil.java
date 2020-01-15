package com.sogou.speech.utils;

import android.os.Build;
import android.text.TextUtils;

import java.util.Locale;

public class DeviceUtil {
    private static final String PHONE_NEXUS = "nexus";

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


    public static boolean isNexusPhone() {
        String phoneType = Build.MODEL;
        if (!TextUtils.isEmpty(phoneType)) {
            String phoneTypeForLower = phoneType.toLowerCase(Locale.UK);
            if (!TextUtils.isEmpty(phoneTypeForLower)) {
                return phoneTypeForLower.contains(PHONE_NEXUS);
            }
        }
        return false;
    }


}
