package com.sogou.speech.http;

import com.google.protobuf.Duration;
import com.sogou.speech.auth.v1.CreateTokenRequest;
import com.sogou.speech.auth.v1.CreateTokenResponse;
import com.sogou.speech.auth.v1.authGrpc;
import com.sogou.speech.utils.Settings;
import com.sogou.speech.utils.SpeechLogUtil;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import io.grpc.ManagedChannel;
import io.grpc.okhttp.NegotiationType;
import io.grpc.okhttp.OkHttpChannelProvider;
import io.grpc.stub.StreamObserver;

public class TokenFetchTask {
    private static final String TAG = "TokenFetchTask";
    private TokenFetchListener tokenFetchListener;
    private static long TIME_EXP = 1 * 60 * 60;//一个小时有效期


    public TokenFetchTask(TokenFetchListener listener) {
        this.tokenFetchListener = listener;
    }

    public void execute() {
        try {
            grpcRequestToken();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }


    private void grpcRequestToken() throws NoSuchAlgorithmException, KeyManagementException {

        Duration duration = Duration.newBuilder().setSeconds(TIME_EXP).build();
        CreateTokenRequest request = CreateTokenRequest.newBuilder()
                .setExp(duration)
                .setAppid(Settings.APP_ID)
                .setAppkey(Settings.APP_KEY)
                .setUuid(""+android.os.Build.SERIAL)
                .buildPartial();
        ManagedChannel channel = new OkHttpChannelProvider().builderForAddress(Settings.URL_RECOGNIZE, 443)
                .negotiationType(NegotiationType.TLS)
                .overrideAuthority(Settings.URL_RECOGNIZE + ":443")
                .sslSocketFactory(HttpsUtil.getSSLSocketFactory(null, null, null))
                .build();
        authGrpc.authStub client = authGrpc.newStub(channel);
        client.createToken(request, new StreamObserver<CreateTokenResponse>() {
            @Override
            public void onNext(CreateTokenResponse tokenResponse) {
                SpeechLogUtil.log(TAG,"onNext");
                if (tokenFetchListener!=null) {
                    tokenFetchListener.onTokenFetchSucceed(tokenResponse.getToken(),tokenResponse.getEndTime().getSeconds());
                }
            }

            @Override
            public void onError(Throwable t) {
                SpeechLogUtil.log(TAG,"onError");
                if (tokenFetchListener!=null) {
                    tokenFetchListener.onTokenFetchFailed(t.getLocalizedMessage());
                }
            }

            @Override
            public void onCompleted() {
                SpeechLogUtil.log(TAG,"onCompleted");
            }
        });
    }

    public interface TokenFetchListener {
        void onTokenFetchSucceed(String token,long endTimeStamp);

        void onTokenFetchFailed(String errMsg);
    }
}
