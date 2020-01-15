package com.xi.liuliu.voicetranslator.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Date:2019/7/31
 * Author:zhangxiaobei
 * Describe:
 */
public class Language implements Parcelable {
    private String chineseName;
    private String localName;
    private String asrCode;
    private String translateCode;

    public String getChineseName() {
        return chineseName;
    }

    public void setChineseName(String chineseName) {
        this.chineseName = chineseName;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public String getAsrCode() {
        return asrCode;
    }

    public void setAsrCode(String asrCode) {
        this.asrCode = asrCode;
    }

    public String getTranslateCode() {
        return translateCode;
    }

    public void setTranslateCode(String translateCode) {
        this.translateCode = translateCode;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(chineseName);
        parcel.writeString(localName);
        parcel.writeString(asrCode);
        parcel.writeString(translateCode);
    }

    public static final Parcelable.Creator<Language> CREATOR = new Parcelable.Creator<Language>() {
        @Override
        public Language createFromParcel(Parcel source) {
            return new Language(source);
        }

        @Override
        public Language[] newArray(int size) {
            return new Language[size];
        }
    };

    private Language(Parcel in) {
        chineseName = in.readString();
        localName = in.readString();
        asrCode = in.readString();
        translateCode = in.readString();
    }

    public Language() {

    }
}
