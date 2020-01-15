package com.xi.liuliu.voicetranslator.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Date:2019/7/31
 * Author:zhangxiaobei
 * Describe:
 */
public class GsonUtil {
    private static final String TAG = GsonUtil.class.getSimpleName();
    private static GsonUtil mInstance;
    private static Gson mGson;

    private GsonUtil() {
        mGson = new Gson();
    }

    public static GsonUtil getInstance() {
        if (mInstance == null) {
            synchronized (GsonUtil.class) {
                if (mInstance == null) {
                    mInstance = new GsonUtil();
                }
            }
        }
        return mInstance;
    }

    public <T> T strToObject(String jsonData, Class<T> type) {
        T result = mGson.fromJson(jsonData, type);
        return result;
    }

    public String objectToStr(Object object) {
        return mGson.toJson(object);
    }

    public <T> List<T> getObjectList(String jsonString, Class<T> cls) {
        List<T> list = new ArrayList<T>();
        try {
            JsonArray array = new JsonParser().parse(jsonString).getAsJsonArray();
            for (JsonElement jsonElement : array) {
                list.add(mGson.fromJson(jsonElement, cls));
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.loge(TAG, "Exception:" + e.getMessage());
        }
        return list;
    }

}
