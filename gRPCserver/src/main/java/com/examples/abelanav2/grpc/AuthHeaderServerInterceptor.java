/*
 * Copyright 2015 Google, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.examples.abelanav2.grpc;

import com.google.gson.JsonObject;

import io.grpc.ForwardingServerCall.SimpleForwardingServerCall;
import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import net.oauth.jsontoken.JsonToken;

import java.util.logging.Logger;

/**
 * A interceptor to handle server header.
 */
public class AuthHeaderServerInterceptor implements ServerInterceptor {

  /**
   * The Authorization custom header field that contains the user auth token.
   */
  private static Metadata.Key<String> customHeadKey =
      Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER);

  /**
   * The user ID when authenticated. This variable is shared betzeen the class
   * {@link MyThreadLocalApplyingListener} and the overriden interceptCall
   * method.
   */
  private String authUserId = "";

  /**
   * Logger.
   */
  private static Logger LOGGER = Logger.getLogger(AuthHeaderServerInterceptor.class.getName());

  @Override
  public final <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(final String method,
      final ServerCall<RespT> call, final Metadata.Headers requestHeaders,
      final ServerCallHandler<ReqT, RespT> next) {

    authUserId = requestHeaders.get(customHeadKey);
    String userId;
    try {
      JsonToken token = AuthUtils.deserialize(authUserId);
      JsonObject payload = token.getPayloadAsJsonObject();
      JsonObject user = payload.get("user").getAsJsonObject();
      userId = user.get("user_id").getAsString();
    } catch (Exception e) {
      userId = "";
    }
    LOGGER.info("Request received from userID=" + userId);

    return new MyThreadLocalApplyingListener<>(
        next.startCall(method, new SimpleForwardingServerCall<RespT>(call) {
          private boolean sentHeaders = false;

          @Override
          public void sendHeaders(final Metadata.Headers
                                      responseHeaders) {
            AuthToken.set(authUserId);

            responseHeaders.put(customHeadKey, "customRespondValue");
            super.sendHeaders(responseHeaders);
            sentHeaders = true;
          }

          @Override
          public void sendPayload(final RespT payload) {
            if (!sentHeaders) {
              sendHeaders(new Metadata.Headers());
            }
            super.sendPayload(payload);
          }

          @Override
          public void close(final Status status,
                            final Metadata.Trailers trailers) {
            super.close(status, trailers);
          }
        }, requestHeaders));
  }

  /**
   * Class that will set the User ID variable as global for the current thread
   * before the server creates the response, and then unsets it.
   */
  public class MyThreadLocalApplyingListener<ReqT> extends ForwardingServerCallListener<ReqT> {
    /**
     * The ServerCall.Listener to delegate most of the operations to.
     */
    private final ServerCall.Listener<ReqT> delegate;

    /**
     *
     * @param delegate the ServerCall.Listener to delegate operations to.
     */
    protected MyThreadLocalApplyingListener(final ServerCall.Listener<ReqT> delegate) {
      this.delegate = delegate;
    }

    @Override
    protected final ServerCall.Listener<ReqT> delegate() {
      return delegate;
    }

    @Override
    public final void onPayload(final ReqT payload) {
      AuthToken.set(authUserId);
      super.onPayload(payload);
      AuthToken.remove();
    }

    @Override
    public final void onHalfClose() {
      AuthToken.set(authUserId);
      super.onHalfClose();
      AuthToken.remove();
    }
  }

}

