package com.sogou.speech.http;

import android.content.Context;

import com.google.protobuf.ByteString;
import com.sogou.speech.asr.v1.RecognitionConfig;
import com.sogou.speech.asr.v1.SpeechRecognitionAlternative;
import com.sogou.speech.asr.v1.StreamingRecognitionConfig;
import com.sogou.speech.asr.v1.StreamingRecognitionResult;
import com.sogou.speech.asr.v1.StreamingRecognizeRequest;
import com.sogou.speech.asr.v1.StreamingRecognizeResponse;
import com.sogou.speech.asr.v1.asrGrpc;
import com.sogou.speech.listener.GrpcAsrListener;
import com.sogou.speech.utils.ErrorIndex;
import com.sogou.speech.utils.Settings;
import com.sogou.speech.utils.SpeechLogUtil;
import com.xi.liuliu.voicetranslator.bean.Language;
import com.xi.liuliu.voicetranslator.utils.SharedPrefUtil;

import java.util.HashMap;

import javax.net.ssl.SSLSocketFactory;

import io.grpc.ManagedChannel;
import io.grpc.okhttp.NegotiationType;
import io.grpc.okhttp.OkHttpChannelBuilder;
import io.grpc.okhttp.OkHttpChannelProvider;
import io.grpc.stub.StreamObserver;

public class GrpcAsrRequestProtocol {
    private static final String TAG = "GrpcAsrRequestProtocol";
    private Language mLanguage;
    private GrpcAsrListener mGrpcAsrListener;
    private Context mContext;
    private asrGrpc.asrStub client;
    private ManagedChannel channel;
    private StreamObserver<StreamingRecognizeRequest> mRequestObserver;
    private StreamObserver<StreamingRecognizeResponse> mResponseObserver = new StreamObserver<StreamingRecognizeResponse>() {
        @Override
        public void onNext(StreamingRecognizeResponse response) {
            if (response!=null) {
                if (response.getResultsCount()>0) {
                    StreamingRecognitionResult result  = response.getResults(0);
                    boolean isFinal = result.getIsFinal();
                    if (result.getAlternativesCount() > 0) {
                        final SpeechRecognitionAlternative alternative = result.getAlternatives(0);
                        SpeechLogUtil.log(TAG,"onNext,result:"+ alternative.getTranscript());
                        if (mGrpcAsrListener!=null) {
                            mGrpcAsrListener.onGrpcAsrResult(alternative.getTranscript(),isFinal);
                        }
                    }
                }
                if (response.getError()!=null) {
                    if (0 != response.getError().getCode() && 200 != response.getError().getCode()) {
                        if (mGrpcAsrListener!=null) {
                            mGrpcAsrListener.onGrpcAsrError(response.getError().getCode(),response.getError().getMessage().toString());
                        }
                    }
                }
            }



        }

        @Override
        public void onError(Throwable t) {
            SpeechLogUtil.log(TAG,"onError:"+t.getMessage());
            if (mGrpcAsrListener != null) {
                mGrpcAsrListener.onGrpcAsrError(ErrorIndex.ERROR_NETWORK_UNAVAILABLE,t.getMessage());
            }
            t.printStackTrace();
        }

        @Override
        public void onCompleted() {
            SpeechLogUtil.log(TAG,"onCompleted");
        }
    };

    public GrpcAsrRequestProtocol(GrpcAsrListener grpcAsrListener,Context context,Language language) {
        mGrpcAsrListener = grpcAsrListener;
        mContext = context;
        mLanguage = language;
        createGrpcClient();
    }

    private void createGrpcClient() {
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
        client = asrGrpc.newStub(channel);
        mRequestObserver = client.streamingRecognize(mResponseObserver);
    }


    public void sendConfig() {
        RecognitionConfig.Builder recognitionConfigBuilder = RecognitionConfig.newBuilder()
                .setLanguageCode(mLanguage.getAsrCode())
                .setEncoding(RecognitionConfig.AudioEncoding.SOGOU_SPEEX)
                .setSampleRateHertz(16000)
                .setEnableWordTimeOffsets(false)
                .setMaxAlternatives(1)
                .setProfanityFilter(false)
                .setDisableAutomaticPunctuation(false);
        mRequestObserver.onNext(StreamingRecognizeRequest.newBuilder()
                .setStreamingConfig(StreamingRecognitionConfig.newBuilder()
                        .setConfig(recognitionConfigBuilder)
                        .setInterimResults(true)
                        .setSingleUtterance(true)
                        .build())
                .build());
        SpeechLogUtil.log(TAG,"sendConfig");

    }
    public void sendAudioData(byte[] bytes, boolean isLast) {
        if (bytes != null) {
            if (mRequestObserver != null) {
                ByteString tempData = ByteString.copyFrom(bytes);
                if (bytes != null && bytes.length > 0) {
                    SpeechLogUtil.log(TAG,"sendAudioData,length:"+bytes.length);
                    mRequestObserver.onNext(StreamingRecognizeRequest.newBuilder()
                            .setAudioContent(tempData)
                            .build());
                }
            }

        }
        if (isLast) {
            if (mRequestObserver != null) {
                mRequestObserver.onCompleted();
            }

        }

    }

}
