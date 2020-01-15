package com.sogou.speech.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class NetworkUtil {
    public static final int NETWORK_TYPE_2G = 2;
    public static final int NETWORK_TYPE_3G = 3;
    public static final int NETWORK_TYPE_4G = 4;
    public static final int NETWORK_TYPE_WIFI = 5;
    public static final int NETWORK_TYPE_UNKNOWN = 6;

    /**
     * @param context
     * @return 网络类型，2：2G  3：3G  4:4G  5：WiFi  6：未知
     */
    public static int getNetworkType(Context context) {
        int strNetworkType = 0;
        if (context != null) {
            ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    strNetworkType = NETWORK_TYPE_WIFI;
                } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    String strSubTypeName = networkInfo.getSubtypeName();
                    int networkType = networkInfo.getSubtype();
                    switch (networkType) {
                        case TelephonyManager.NETWORK_TYPE_GPRS:
                        case TelephonyManager.NETWORK_TYPE_EDGE:
                        case TelephonyManager.NETWORK_TYPE_CDMA:
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            strNetworkType = NETWORK_TYPE_2G;
                            break;
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            strNetworkType = NETWORK_TYPE_3G;
                            break;
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            strNetworkType = NETWORK_TYPE_4G;
                            break;
                        default:
                            if (strSubTypeName.equalsIgnoreCase("TD-SCDMA") || strSubTypeName.equalsIgnoreCase("WCDMA") || strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                                strNetworkType = NETWORK_TYPE_3G;
                            } else {
                                strNetworkType = NETWORK_TYPE_UNKNOWN;
                            }
                            break;
                    }
                }
            }
        }
        return strNetworkType;
    }

    /**
     * @param context
     * @return 网络是否可用，WiFi已连接或者GPRS打开或者宽带已连接时，返回true
     */
    public static boolean isNetworkAvailable(Context context) {
        if (context == null) return false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        } else {
            try {
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info != null) {
                    for (int i = 0; i < info.length; i++) {
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                    }
                }
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    /**
     * get network type in detail
     *
     * @return
     * @author songchunwei
     * @since 2013-04-12
     */
    public static String getDetailNetworkType(Context context) {
        if (context != null) {

            int tmp = getShortNetworkType(context);
            if (tmp == 1) {
                return "wifi";
            } else if (tmp == 0) {
                return "mobile-" + getNetworkAPNType(context);
            }
        }
        return "unknown";

    }

    /**
     * wifi : 1 mobile : 0 null : -1
     *
     * @return
     * @author songchunwei
     * @since 2013-04-12
     */
    public static int getShortNetworkType(Context context) {
        // set default value to mobile network, 2013-12-27
        // judge whether mConnectivityManager is null to avoid
        // NullpointerException, 2013-12-27
        ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectMgr == null) {
            return 0;
        }

        if (connectMgr.getActiveNetworkInfo() != null)
            return connectMgr.getActiveNetworkInfo().getType();
        else
            return -1;
    }

    /**
     * NETWORK_TYPE_UNKNOWN = 0; NETWORK_TYPE_GPRS = 1; NETWORK_TYPE_EDGE = 2;
     * NETWORK_TYPE_UMTS = 3; NETWORK_TYPE_CDMA = 4; NETWORK_TYPE_EVDO_0 = 5;
     * NETWORK_TYPE_EVDO_A = 6; NETWORK_TYPE_1xRTT = 7; NETWORK_TYPE_HSDPA = 8;
     * NETWORK_TYPE_HSUPA = 9; NETWORK_TYPE_HSPA = 10; NETWORK_TYPE_IDEN = 11;
     * NETWORK_TYPE_EVDO_B = 12;
     *
     * @return
     * @author songchunwei
     * @since 2013-04-12
     */
    public static int getNetworkAPNType(Context context) {
        // judge whether tm is null to avoid NullpointerException, 2013-12-27
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager == null) {
            // set default value to 2G, 2013-12-27
            return 2;
        }
        return telephonyManager.getNetworkType();
    }

}
