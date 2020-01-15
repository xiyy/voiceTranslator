package com.sogou.speech.http;

import java.util.Map;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.ForwardingClientCallListener;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;


public class HeaderClientInterceptor implements ClientInterceptor {

    private static final String TAG = "HeaderClientInterceptor";

    private Map<String, String> mHeaderMap;

    public HeaderClientInterceptor(Map<String, String> headerMap) {
        mHeaderMap = headerMap;
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method,
                                                               CallOptions callOptions, Channel next) {
        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {

            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                /* put custom header */
                if (mHeaderMap != null && headers!=null) {
                    for (String key : mHeaderMap.keySet()) {
                        Metadata.Key<String> customHeadKey = Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER);
                        if(mHeaderMap.get(key)!=null) {
                            headers.put(customHeadKey, mHeaderMap.get(key));
                        }
                    }
                }
//                LogUtil.log(TAG, "header send to server:" + headers);
                super.start(new ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(responseListener) {
                    @Override
                    public void onHeaders(Metadata headers) {
                        /**
                         * if you don't need receive header from server,
                         * you can use {@link io.grpc.stub.MetadataUtils attachHeaders}
                         * directly to send header
                         */
//                        LogUtil.log(TAG, "header received from server:" + headers);
                        super.onHeaders(headers);
                    }
                }, headers);
            }
        };
    }
}
