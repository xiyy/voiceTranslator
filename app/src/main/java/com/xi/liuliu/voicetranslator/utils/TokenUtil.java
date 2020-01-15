package com.xi.liuliu.voicetranslator.utils;

import android.content.Context;
import android.text.TextUtils;

import com.sogou.speech.http.TokenFetchTask;

/**
 * Date:2019/8/5
 * Author:zhangxiaobei
 * Describe:
 */
public class TokenUtil {
    private static final String TAG = TokenUtil.class.getSimpleName();

    public static void checkToken(Context context) {
        String storedToken = SharedPrefUtil.getString(context, SharedPrefUtil.KEY_TOKEN_VALUE);
        String storedTokenEndTime = SharedPrefUtil.getString(context, SharedPrefUtil.KEY_TOKEN_END_TIME);
        if (TextUtils.isEmpty(storedToken)) {
            LogUtil.log(TAG, "SP中token为空，正在获取token...");
            getToken(context);
        } else {
            if (isTokenExpired(storedTokenEndTime)) {
                LogUtil.log(TAG, "isTokenExpired:true,正在获取token...");
                getToken(context);
            } else {
                LogUtil.log(TAG, "isTokenExpired:false");
            }
        }
    }

    private static void getToken(final Context context) {
        TokenFetchTask task = new TokenFetchTask(new TokenFetchTask.TokenFetchListener() {
            @Override
            public void onTokenFetchSucceed(final String token, final long endTimeStamp) {
                SharedPrefUtil.putString(context, SharedPrefUtil.KEY_TOKEN_VALUE, "Bearer " + token);
                SharedPrefUtil.putString(context, SharedPrefUtil.KEY_TOKEN_END_TIME, endTimeStamp + "");
                SharedPrefUtil.putBoolean(context, SharedPrefUtil.KEY_TOKEN_AVAILABLE, true);
                LogUtil.log(TAG,"获取Token成功，已记录SP中，token:"+"Bearer " + token);
            }

            @Override
            public void onTokenFetchFailed(final String errMsg) {
                LogUtil.loge(TAG, "SP中的token已过期，请求token失败，errMsg：" + errMsg);
            }
        });
        task.execute();

    }

    private static boolean isTokenExpired(String storedTokenEndTime) {
        if (System.currentTimeMillis() > Long.valueOf(storedTokenEndTime) * 1000)
            return true;
        return false;
    }
}
