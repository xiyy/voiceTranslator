package com.sogou.speech.http;

import android.content.Context;

import com.sogou.speech.listener.GrpcTtsListener;
import com.sogou.speech.tts.v1.AudioConfig;
import com.sogou.speech.tts.v1.SynthesisInput;
import com.sogou.speech.tts.v1.SynthesizeConfig;
import com.sogou.speech.tts.v1.SynthesizeRequest;
import com.sogou.speech.tts.v1.SynthesizeResponse;
import com.sogou.speech.tts.v1.VoiceConfig;
import com.sogou.speech.tts.v1.ttsGrpc;
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

public class GrpcTtsRequestProtocol {
    private static final String TAG = GrpcTtsRequestProtocol.class.getSimpleName();
    private ttsGrpc.ttsStub ttsClient;
    private StreamObserver<SynthesizeResponse> mSynthesizeResponse;
    private ManagedChannel channel;
    private Context mContext;
    private GrpcTtsListener mGrpcTtsListener;

    public GrpcTtsRequestProtocol(Context context, GrpcTtsListener grpcTtsListener) {
        mContext = context;
        mGrpcTtsListener = grpcTtsListener;
        createClient();
    }

    private void createClient() {
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
        ttsClient = ttsGrpc.newStub(channel);
    }

    public void tts(String text, String languageCode, String speaker, int volume, int pitch, int speakRate, AudioConfig.AudioEncoding audioFormat) {
        mSynthesizeResponse = new StreamObserver<SynthesizeResponse>() {
            @Override
            public void onNext(SynthesizeResponse value) {
                SpeechLogUtil.log(TAG, "onNext");
                if (value == null) {
                    SpeechLogUtil.loge(TAG, "server return is null");
                    if (mGrpcTtsListener != null) {
                        mGrpcTtsListener.onTtsFailed(-1, "server return is null");
                    }
                    return;
                }
                if (mGrpcTtsListener != null) {
                    mGrpcTtsListener.onTtsSuccess(value.getAudioContent().toByteArray(), false);
                }
            }

            @Override
            public void onError(Throwable t) {
                SpeechLogUtil.loge(TAG, "onError:" + t.getMessage());
                if (mGrpcTtsListener != null) {
                    mGrpcTtsListener.onTtsFailed(-2, t.getMessage());
                }
            }

            @Override
            public void onCompleted() {
                SpeechLogUtil.log(TAG, "onCompleted");
                if (mGrpcTtsListener != null) {
                    mGrpcTtsListener.onTtsSuccess(new byte[0], true);
                }
            }
        };
        SynthesizeRequest ttsRequest = SynthesizeRequest.newBuilder().setConfig(
                SynthesizeConfig.newBuilder()
                        .setAudioConfig(
                                AudioConfig.newBuilder()
                                        .setAudioEncoding(audioFormat)
                                        .setVolume(volume)
                                        .setPitch(pitch)
                                        .setSpeakingRate(speakRate)
                                        .build())
                        .setVoiceConfig(VoiceConfig.newBuilder()
                                .setLanguageCode(languageCode)
                                .setSpeaker(speaker)
                                .build())
                        .build())
                .setInput(SynthesisInput.newBuilder()
                        .setText(text)
                        .build())
                .build();
        ttsClient.streamingSynthesize(ttsRequest, mSynthesizeResponse);
    }
}

