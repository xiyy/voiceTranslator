package com.xi.liuliu.voicetranslator.utils;

import android.content.Context;

import com.xi.liuliu.voicetranslator.bean.VoiceTranslateResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Date:2019/8/23
 * Author:zhangxiaobei
 * Describe:
 */
public class SerializeUtil {
    private static final String TAG = SerializeUtil.class.getSimpleName();

    public static void SerializeVoiceTranslateResultList(Context context,List<VoiceTranslateResult> voiceTranslateResultArrayList) {
        if (voiceTranslateResultArrayList == null) return;
        if (voiceTranslateResultArrayList.size() == 0) return;
        File file = new File(context.getFilesDir(),"resultList.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                LogUtil.loge(TAG, "IOException:" + e.getMessage());
            }
        }
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));
            if (objectOutputStream != null) {
                objectOutputStream.writeObject(voiceTranslateResultArrayList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.loge(TAG, "Exception:" + e.getMessage());
        } finally {
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static ArrayList<VoiceTranslateResult> unSerializeVoiceTranslateResultList(Context context) {
        File file = new File(context.getFilesDir(),"resultList.txt");
        if (file.exists()) {
            ObjectInputStream objectInputStream = null;
            try {
                objectInputStream = new ObjectInputStream(new FileInputStream(file));
                if (objectInputStream != null) {
                    ArrayList<VoiceTranslateResult> voiceTranslateResultArrayList = (ArrayList<VoiceTranslateResult>) objectInputStream.readObject();
                    return voiceTranslateResultArrayList;
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.loge(TAG, "Exception:" + e.getMessage());
            } finally {
                if (objectInputStream != null) {
                    try {
                        objectInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return null;
    }
}
