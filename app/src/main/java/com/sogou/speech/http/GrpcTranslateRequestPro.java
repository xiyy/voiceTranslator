package com.sogou.speech.http;

import android.content.Context;

import com.sogou.speech.listener.GrpcTranslateListener;
import com.sogou.speech.mt.v1.TranslateConfig;
import com.sogou.speech.mt.v1.TranslateTextRequest;
import com.sogou.speech.mt.v1.TranslateTextResponse;
import com.sogou.speech.mt.v1.mtGrpc;
import com.sogou.speech.utils.ErrorIndex;
import com.sogou.speech.utils.Settings;
import com.sogou.speech.utils.SpeechLogUtil;
import com.xi.liuliu.voicetranslator.utils.SharedPrefUtil;

import java.util.HashMap;

import javax.net.ssl.SSLSocketFactory;

import io.grpc.ManagedChannel;
import io.grpc.okhttp.NegotiationType;
import io.grpc.okhttp.OkHttpChannelBuilder;
import io.grpc.okhttp.OkHttpChannelProvider;
import io.grpc.stub.StreamObserver;


public class GrpcTranslateRequestPro {
    private static final String TAG = GrpcTranslateRequestPro.class.getSimpleName();
    private mtGrpc.mtStub mtClient;
    private Context mContext;
    private ManagedChannel channel;
    private StreamObserver<TranslateTextResponse> mTranslateResponse = null;
    private GrpcTranslateListener mGrpcTranslateListener;

    public GrpcTranslateRequestPro(Context context, GrpcTranslateListener grpcTranslateListener) {
        mContext = context;
        mGrpcTranslateListener = grpcTranslateListener;
        createMtClient();
    }

    private void createMtClient() {
        HashMap<String, String> headerParams = new HashMap<>();
        headerParams.put("Authorization", SharedPrefUtil.getString(mContext, SharedPrefUtil.KEY_TOKEN_VALUE));
        headerParams.put("appid", "" + Settings.APP_ID);
        headerParams.put("uuid", "" + "" + android.os.Build.SERIAL);

        SSLSocketFactory sslSocketFactory = null;
        try {
            sslSocketFactory = HttpsUtil.getSSLSocketFactory(null, null, null);
        } catch (Throwable t) {

        }

        OkHttpChannelBuilder okHttpChannelProvider = new OkHttpChannelProvider()
                .builderForAddress(Settings.URL_RECOGNIZE,
                        443)
                .overrideAuthority(Settings.URL_RECOGNIZE
                        + ":443")
                .negotiationType(NegotiationType.TLS)
                .intercept(new HeaderClientInterceptor(headerParams))
                .userAgent(Settings.userAgent);

        if (sslSocketFactory != null) {
            okHttpChannelProvider.sslSocketFactory(sslSocketFactory);
        }

        channel = okHttpChannelProvider.build();
        mtClient = mtGrpc.newStub(channel);
    }

    public void translate(String text, String srcCode, String destCode) {
        mTranslateResponse = new StreamObserver<TranslateTextResponse>() {
            @Override
            public void onNext(TranslateTextResponse value) {
                SpeechLogUtil.log(TAG, "TranslateTextResponse " + value.toString());
                SpeechLogUtil.log(TAG, "getSourceText " + value.getSourceText());
                SpeechLogUtil.log(TAG, "getTranslatedText " + value.getTranslatedText());
                if (value == null) {
                    SpeechLogUtil.loge(TAG, "translation response is null");
                    return;
                }
                if (mGrpcTranslateListener != null) {
                    mGrpcTranslateListener.onGrpcTranslateResult(value.getSourceText(), value.getTranslatedText());
                }
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
                SpeechLogUtil.loge(TAG, "onError " + t.getMessage());
                if (mGrpcTranslateListener != null) {
                    mGrpcTranslateListener.onGrpcTranslateError(ErrorIndex.ERROR_GRPC_UNKNOWN, t.getMessage());
                }
            }

            @Override
            public void onCompleted() {
                SpeechLogUtil.loge(TAG, "translate onCompleted：" + this.hashCode());
            }
        };


        TranslateTextRequest mTranslateRequest = TranslateTextRequest.newBuilder()
                .setConfig(TranslateConfig.newBuilder()
                        .setSourceLanguageCode(srcCode)
                        .setTargetLanguageCode(destCode)
                        .build())
                .setText(text)
                .build();
        mtClient.translateText(mTranslateRequest, mTranslateResponse);

    }
}
