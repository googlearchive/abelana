/*
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.examples.abelanav2.grpcclient;

import io.grpc.Call;
import io.grpc.Channel;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingCall.SimpleForwardingCall;
import io.grpc.ForwardingCallListener.SimpleForwardingCallListener;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;

/**
 * A interceptor to handle client header.
 */
public class AuthHeaderClientInterceptor implements ClientInterceptor {
    /**
     * The Authorization custom header field that contains the user auth token.
     */
    private static Metadata.Key<String> sCustomHeadKey =
            Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER);

    /**
     * The user id token used to authenticate calls.
     */
    private String mUserIdToken;

    /**
     * Constructor that takes as an argument the userIdToken used to
     * authenticate the user.
     * @param pUserIdToken the user id token.
     */
    public AuthHeaderClientInterceptor(final String pUserIdToken) {
        setUserIdToken(pUserIdToken);
    }

    /**
     * Sets the user id token used for authentication.
     * @param pUserIdToken the user id token.
     */
    public final void setUserIdToken(final String pUserIdToken) {
        if (pUserIdToken == null) {
            this.mUserIdToken = "";
        } else {
            this.mUserIdToken = pUserIdToken;
        }
    }

    @Override
    public final <ReqT, RespT> Call<ReqT, RespT> interceptCall(
            final MethodDescriptor<ReqT, RespT> method, final Channel next) {
        return new SimpleForwardingCall<ReqT, RespT>(next.newCall(method)) {

            @Override
            public void start(final Listener<RespT> responseListener,
                              final Metadata.Headers headers) {
                headers.put(sCustomHeadKey, mUserIdToken);
                super.start(
                        new SimpleForwardingCallListener<RespT>(
                                responseListener) {
                    @Override
                    public void onHeaders(final Metadata.Headers headers) {
                        super.onHeaders(headers);
                    }
                }, headers);
            }
        };
    }
}
