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

package com.examples.abelanav2.grpc;

import static io.grpc.stub.Calls.createMethodDescriptor;
import static io.grpc.stub.Calls.asyncUnaryCall;
import static io.grpc.stub.Calls.asyncServerStreamingCall;
import static io.grpc.stub.Calls.asyncClientStreamingCall;
import static io.grpc.stub.Calls.duplexStreamingCall;
import static io.grpc.stub.Calls.blockingUnaryCall;
import static io.grpc.stub.Calls.blockingServerStreamingCall;
import static io.grpc.stub.Calls.unaryFutureCall;
import static io.grpc.stub.ServerCalls.createMethodDefinition;
import static io.grpc.stub.ServerCalls.asyncUnaryRequestCall;
import static io.grpc.stub.ServerCalls.asyncStreamingRequestCall;

import java.io.IOException;

@javax.annotation.Generated("by gRPC proto compiler")
public class AbelanaGrpc {

  private static final io.grpc.stub.Method<com.examples.abelanav2.grpc.SignInRequest,
      com.examples.abelanav2.grpc.SignInResponse> METHOD_SIGN_IN =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.UNARY, "SignIn",
          io.grpc.protobuf.nano.NanoUtils.<com.examples.abelanav2.grpc.SignInRequest>marshaller(
              new io.grpc.protobuf.nano.Parser<com.examples.abelanav2.grpc.SignInRequest>() {
                  @Override
                  public com.examples.abelanav2.grpc.SignInRequest parse(com.google.protobuf.nano.CodedInputByteBufferNano input) throws IOException {
                      return com.examples.abelanav2.grpc.SignInRequest.parseFrom(input);
                  }
          }),
          io.grpc.protobuf.nano.NanoUtils.<com.examples.abelanav2.grpc.SignInResponse>marshaller(
              new io.grpc.protobuf.nano.Parser<com.examples.abelanav2.grpc.SignInResponse>() {
                  @Override
                  public com.examples.abelanav2.grpc.SignInResponse parse(com.google.protobuf.nano.CodedInputByteBufferNano input) throws IOException {
                      return com.examples.abelanav2.grpc.SignInResponse.parseFrom(input);
                  }
          }));
  private static final io.grpc.stub.Method<com.examples.abelanav2.grpc.PhotoListRequest,
      com.examples.abelanav2.grpc.PhotoListResponse> METHOD_PHOTO_STREAM =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.UNARY, "PhotoStream",
          io.grpc.protobuf.nano.NanoUtils.<com.examples.abelanav2.grpc.PhotoListRequest>marshaller(
              new io.grpc.protobuf.nano.Parser<com.examples.abelanav2.grpc.PhotoListRequest>() {
                  @Override
                  public com.examples.abelanav2.grpc.PhotoListRequest parse(com.google.protobuf.nano.CodedInputByteBufferNano input) throws IOException {
                      return com.examples.abelanav2.grpc.PhotoListRequest.parseFrom(input);
                  }
          }),
          io.grpc.protobuf.nano.NanoUtils.<com.examples.abelanav2.grpc.PhotoListResponse>marshaller(
              new io.grpc.protobuf.nano.Parser<com.examples.abelanav2.grpc.PhotoListResponse>() {
                  @Override
                  public com.examples.abelanav2.grpc.PhotoListResponse parse(com.google.protobuf.nano.CodedInputByteBufferNano input) throws IOException {
                      return com.examples.abelanav2.grpc.PhotoListResponse.parseFrom(input);
                  }
          }));
  private static final io.grpc.stub.Method<com.examples.abelanav2.grpc.FlagRequest,
      com.examples.abelanav2.grpc.StatusResponse> METHOD_FLAG_PHOTO =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.UNARY, "FlagPhoto",
          io.grpc.protobuf.nano.NanoUtils.<com.examples.abelanav2.grpc.FlagRequest>marshaller(
              new io.grpc.protobuf.nano.Parser<com.examples.abelanav2.grpc.FlagRequest>() {
                  @Override
                  public com.examples.abelanav2.grpc.FlagRequest parse(com.google.protobuf.nano.CodedInputByteBufferNano input) throws IOException {
                      return com.examples.abelanav2.grpc.FlagRequest.parseFrom(input);
                  }
          }),
          io.grpc.protobuf.nano.NanoUtils.<com.examples.abelanav2.grpc.StatusResponse>marshaller(
              new io.grpc.protobuf.nano.Parser<com.examples.abelanav2.grpc.StatusResponse>() {
                  @Override
                  public com.examples.abelanav2.grpc.StatusResponse parse(com.google.protobuf.nano.CodedInputByteBufferNano input) throws IOException {
                      return com.examples.abelanav2.grpc.StatusResponse.parseFrom(input);
                  }
          }));
  private static final io.grpc.stub.Method<com.examples.abelanav2.grpc.PhotoListRequest,
      com.examples.abelanav2.grpc.PhotoListResponse> METHOD_LIST_MY_PHOTOS =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.UNARY, "ListMyPhotos",
          io.grpc.protobuf.nano.NanoUtils.<com.examples.abelanav2.grpc.PhotoListRequest>marshaller(
              new io.grpc.protobuf.nano.Parser<com.examples.abelanav2.grpc.PhotoListRequest>() {
                  @Override
                  public com.examples.abelanav2.grpc.PhotoListRequest parse(com.google.protobuf.nano.CodedInputByteBufferNano input) throws IOException {
                      return com.examples.abelanav2.grpc.PhotoListRequest.parseFrom(input);
                  }
          }),
          io.grpc.protobuf.nano.NanoUtils.<com.examples.abelanav2.grpc.PhotoListResponse>marshaller(
              new io.grpc.protobuf.nano.Parser<com.examples.abelanav2.grpc.PhotoListResponse>() {
                  @Override
                  public com.examples.abelanav2.grpc.PhotoListResponse parse(com.google.protobuf.nano.CodedInputByteBufferNano input) throws IOException {
                      return com.examples.abelanav2.grpc.PhotoListResponse.parseFrom(input);
                  }
          }));
  private static final io.grpc.stub.Method<com.examples.abelanav2.grpc.NewPhotoRequest,
      com.examples.abelanav2.grpc.UploadPhotoResponse> METHOD_UPLOAD_PHOTO =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.UNARY, "UploadPhoto",
          io.grpc.protobuf.nano.NanoUtils.<com.examples.abelanav2.grpc.NewPhotoRequest>marshaller(
              new io.grpc.protobuf.nano.Parser<com.examples.abelanav2.grpc.NewPhotoRequest>() {
                  @Override
                  public com.examples.abelanav2.grpc.NewPhotoRequest parse(com.google.protobuf.nano.CodedInputByteBufferNano input) throws IOException {
                      return com.examples.abelanav2.grpc.NewPhotoRequest.parseFrom(input);
                  }
          }),
          io.grpc.protobuf.nano.NanoUtils.<com.examples.abelanav2.grpc.UploadPhotoResponse>marshaller(
              new io.grpc.protobuf.nano.Parser<com.examples.abelanav2.grpc.UploadPhotoResponse>() {
                  @Override
                  public com.examples.abelanav2.grpc.UploadPhotoResponse parse(com.google.protobuf.nano.CodedInputByteBufferNano input) throws IOException {
                      return com.examples.abelanav2.grpc.UploadPhotoResponse.parseFrom(input);
                  }
          }));
  private static final io.grpc.stub.Method<com.examples.abelanav2.grpc.EditPhotoRequest,
      com.examples.abelanav2.grpc.StatusResponse> METHOD_EDIT_PHOTO =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.UNARY, "EditPhoto",
          io.grpc.protobuf.nano.NanoUtils.<com.examples.abelanav2.grpc.EditPhotoRequest>marshaller(
              new io.grpc.protobuf.nano.Parser<com.examples.abelanav2.grpc.EditPhotoRequest>() {
                  @Override
                  public com.examples.abelanav2.grpc.EditPhotoRequest parse(com.google.protobuf.nano.CodedInputByteBufferNano input) throws IOException {
                      return com.examples.abelanav2.grpc.EditPhotoRequest.parseFrom(input);
                  }
          }),
          io.grpc.protobuf.nano.NanoUtils.<com.examples.abelanav2.grpc.StatusResponse>marshaller(
              new io.grpc.protobuf.nano.Parser<com.examples.abelanav2.grpc.StatusResponse>() {
                  @Override
                  public com.examples.abelanav2.grpc.StatusResponse parse(com.google.protobuf.nano.CodedInputByteBufferNano input) throws IOException {
                      return com.examples.abelanav2.grpc.StatusResponse.parseFrom(input);
                  }
          }));
  private static final io.grpc.stub.Method<com.examples.abelanav2.grpc.DeletePhotoRequest,
      com.examples.abelanav2.grpc.StatusResponse> METHOD_DELETE_PHOTO =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.UNARY, "DeletePhoto",
          io.grpc.protobuf.nano.NanoUtils.<com.examples.abelanav2.grpc.DeletePhotoRequest>marshaller(
              new io.grpc.protobuf.nano.Parser<com.examples.abelanav2.grpc.DeletePhotoRequest>() {
                  @Override
                  public com.examples.abelanav2.grpc.DeletePhotoRequest parse(com.google.protobuf.nano.CodedInputByteBufferNano input) throws IOException {
                      return com.examples.abelanav2.grpc.DeletePhotoRequest.parseFrom(input);
                  }
          }),
          io.grpc.protobuf.nano.NanoUtils.<com.examples.abelanav2.grpc.StatusResponse>marshaller(
              new io.grpc.protobuf.nano.Parser<com.examples.abelanav2.grpc.StatusResponse>() {
                  @Override
                  public com.examples.abelanav2.grpc.StatusResponse parse(com.google.protobuf.nano.CodedInputByteBufferNano input) throws IOException {
                      return com.examples.abelanav2.grpc.StatusResponse.parseFrom(input);
                  }
          }));
  private static final io.grpc.stub.Method<com.examples.abelanav2.grpc.PhotoListRequest,
      com.examples.abelanav2.grpc.PhotoListResponse> METHOD_LIST_MY_LIKES =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.UNARY, "ListMyLikes",
          io.grpc.protobuf.nano.NanoUtils.<com.examples.abelanav2.grpc.PhotoListRequest>marshaller(
              new io.grpc.protobuf.nano.Parser<com.examples.abelanav2.grpc.PhotoListRequest>() {
                  @Override
                  public com.examples.abelanav2.grpc.PhotoListRequest parse(com.google.protobuf.nano.CodedInputByteBufferNano input) throws IOException {
                      return com.examples.abelanav2.grpc.PhotoListRequest.parseFrom(input);
                  }
          }),
          io.grpc.protobuf.nano.NanoUtils.<com.examples.abelanav2.grpc.PhotoListResponse>marshaller(
              new io.grpc.protobuf.nano.Parser<com.examples.abelanav2.grpc.PhotoListResponse>() {
                  @Override
                  public com.examples.abelanav2.grpc.PhotoListResponse parse(com.google.protobuf.nano.CodedInputByteBufferNano input) throws IOException {
                      return com.examples.abelanav2.grpc.PhotoListResponse.parseFrom(input);
                  }
          }));
  private static final io.grpc.stub.Method<com.examples.abelanav2.grpc.VoteRequest,
      com.examples.abelanav2.grpc.StatusResponse> METHOD_RATE_PHOTO =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.UNARY, "RatePhoto",
          io.grpc.protobuf.nano.NanoUtils.<com.examples.abelanav2.grpc.VoteRequest>marshaller(
              new io.grpc.protobuf.nano.Parser<com.examples.abelanav2.grpc.VoteRequest>() {
                  @Override
                  public com.examples.abelanav2.grpc.VoteRequest parse(com.google.protobuf.nano.CodedInputByteBufferNano input) throws IOException {
                      return com.examples.abelanav2.grpc.VoteRequest.parseFrom(input);
                  }
          }),
          io.grpc.protobuf.nano.NanoUtils.<com.examples.abelanav2.grpc.StatusResponse>marshaller(
              new io.grpc.protobuf.nano.Parser<com.examples.abelanav2.grpc.StatusResponse>() {
                  @Override
                  public com.examples.abelanav2.grpc.StatusResponse parse(com.google.protobuf.nano.CodedInputByteBufferNano input) throws IOException {
                      return com.examples.abelanav2.grpc.StatusResponse.parseFrom(input);
                  }
          }));

  public static AbelanaStub newStub(io.grpc.Channel channel) {
    return new AbelanaStub(channel, CONFIG);
  }

  public static AbelanaBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new AbelanaBlockingStub(channel, CONFIG);
  }

  public static AbelanaFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new AbelanaFutureStub(channel, CONFIG);
  }

  public static final AbelanaServiceDescriptor CONFIG =
      new AbelanaServiceDescriptor();

  @javax.annotation.concurrent.Immutable
  public static class AbelanaServiceDescriptor extends
      io.grpc.stub.AbstractServiceDescriptor<AbelanaServiceDescriptor> {
    public final io.grpc.MethodDescriptor<com.examples.abelanav2.grpc.SignInRequest,
        com.examples.abelanav2.grpc.SignInResponse> signIn;
    public final io.grpc.MethodDescriptor<com.examples.abelanav2.grpc.PhotoListRequest,
        com.examples.abelanav2.grpc.PhotoListResponse> photoStream;
    public final io.grpc.MethodDescriptor<com.examples.abelanav2.grpc.FlagRequest,
        com.examples.abelanav2.grpc.StatusResponse> flagPhoto;
    public final io.grpc.MethodDescriptor<com.examples.abelanav2.grpc.PhotoListRequest,
        com.examples.abelanav2.grpc.PhotoListResponse> listMyPhotos;
    public final io.grpc.MethodDescriptor<com.examples.abelanav2.grpc.NewPhotoRequest,
        com.examples.abelanav2.grpc.UploadPhotoResponse> uploadPhoto;
    public final io.grpc.MethodDescriptor<com.examples.abelanav2.grpc.EditPhotoRequest,
        com.examples.abelanav2.grpc.StatusResponse> editPhoto;
    public final io.grpc.MethodDescriptor<com.examples.abelanav2.grpc.DeletePhotoRequest,
        com.examples.abelanav2.grpc.StatusResponse> deletePhoto;
    public final io.grpc.MethodDescriptor<com.examples.abelanav2.grpc.PhotoListRequest,
        com.examples.abelanav2.grpc.PhotoListResponse> listMyLikes;
    public final io.grpc.MethodDescriptor<com.examples.abelanav2.grpc.VoteRequest,
        com.examples.abelanav2.grpc.StatusResponse> ratePhoto;

    private AbelanaServiceDescriptor() {
      signIn = createMethodDescriptor(
          "abelanav2.grpc.Abelana", METHOD_SIGN_IN);
      photoStream = createMethodDescriptor(
          "abelanav2.grpc.Abelana", METHOD_PHOTO_STREAM);
      flagPhoto = createMethodDescriptor(
          "abelanav2.grpc.Abelana", METHOD_FLAG_PHOTO);
      listMyPhotos = createMethodDescriptor(
          "abelanav2.grpc.Abelana", METHOD_LIST_MY_PHOTOS);
      uploadPhoto = createMethodDescriptor(
          "abelanav2.grpc.Abelana", METHOD_UPLOAD_PHOTO);
      editPhoto = createMethodDescriptor(
          "abelanav2.grpc.Abelana", METHOD_EDIT_PHOTO);
      deletePhoto = createMethodDescriptor(
          "abelanav2.grpc.Abelana", METHOD_DELETE_PHOTO);
      listMyLikes = createMethodDescriptor(
          "abelanav2.grpc.Abelana", METHOD_LIST_MY_LIKES);
      ratePhoto = createMethodDescriptor(
          "abelanav2.grpc.Abelana", METHOD_RATE_PHOTO);
    }

    @SuppressWarnings("unchecked")
    private AbelanaServiceDescriptor(
        java.util.Map<java.lang.String, io.grpc.MethodDescriptor<?, ?>> methodMap) {
      signIn = (io.grpc.MethodDescriptor<com.examples.abelanav2.grpc.SignInRequest,
          com.examples.abelanav2.grpc.SignInResponse>) methodMap.get(
          CONFIG.signIn.getName());
      photoStream = (io.grpc.MethodDescriptor<com.examples.abelanav2.grpc.PhotoListRequest,
          com.examples.abelanav2.grpc.PhotoListResponse>) methodMap.get(
          CONFIG.photoStream.getName());
      flagPhoto = (io.grpc.MethodDescriptor<com.examples.abelanav2.grpc.FlagRequest,
          com.examples.abelanav2.grpc.StatusResponse>) methodMap.get(
          CONFIG.flagPhoto.getName());
      listMyPhotos = (io.grpc.MethodDescriptor<com.examples.abelanav2.grpc.PhotoListRequest,
          com.examples.abelanav2.grpc.PhotoListResponse>) methodMap.get(
          CONFIG.listMyPhotos.getName());
      uploadPhoto = (io.grpc.MethodDescriptor<com.examples.abelanav2.grpc.NewPhotoRequest,
          com.examples.abelanav2.grpc.UploadPhotoResponse>) methodMap.get(
          CONFIG.uploadPhoto.getName());
      editPhoto = (io.grpc.MethodDescriptor<com.examples.abelanav2.grpc.EditPhotoRequest,
          com.examples.abelanav2.grpc.StatusResponse>) methodMap.get(
          CONFIG.editPhoto.getName());
      deletePhoto = (io.grpc.MethodDescriptor<com.examples.abelanav2.grpc.DeletePhotoRequest,
          com.examples.abelanav2.grpc.StatusResponse>) methodMap.get(
          CONFIG.deletePhoto.getName());
      listMyLikes = (io.grpc.MethodDescriptor<com.examples.abelanav2.grpc.PhotoListRequest,
          com.examples.abelanav2.grpc.PhotoListResponse>) methodMap.get(
          CONFIG.listMyLikes.getName());
      ratePhoto = (io.grpc.MethodDescriptor<com.examples.abelanav2.grpc.VoteRequest,
          com.examples.abelanav2.grpc.StatusResponse>) methodMap.get(
          CONFIG.ratePhoto.getName());
    }

    @java.lang.Override
    protected AbelanaServiceDescriptor build(
        java.util.Map<java.lang.String, io.grpc.MethodDescriptor<?, ?>> methodMap) {
      return new AbelanaServiceDescriptor(methodMap);
    }

    @java.lang.Override
    public com.google.common.collect.ImmutableList<io.grpc.MethodDescriptor<?, ?>> methods() {
      return com.google.common.collect.ImmutableList.<io.grpc.MethodDescriptor<?, ?>>of(
          signIn,
          photoStream,
          flagPhoto,
          listMyPhotos,
          uploadPhoto,
          editPhoto,
          deletePhoto,
          listMyLikes,
          ratePhoto);
    }
  }

  public static interface Abelana {

    public void signIn(com.examples.abelanav2.grpc.SignInRequest request,
        io.grpc.stub.StreamObserver<com.examples.abelanav2.grpc.SignInResponse> responseObserver);

    public void photoStream(com.examples.abelanav2.grpc.PhotoListRequest request,
        io.grpc.stub.StreamObserver<com.examples.abelanav2.grpc.PhotoListResponse> responseObserver);

    public void flagPhoto(com.examples.abelanav2.grpc.FlagRequest request,
        io.grpc.stub.StreamObserver<com.examples.abelanav2.grpc.StatusResponse> responseObserver);

    public void listMyPhotos(com.examples.abelanav2.grpc.PhotoListRequest request,
        io.grpc.stub.StreamObserver<com.examples.abelanav2.grpc.PhotoListResponse> responseObserver);

    public void uploadPhoto(com.examples.abelanav2.grpc.NewPhotoRequest request,
        io.grpc.stub.StreamObserver<com.examples.abelanav2.grpc.UploadPhotoResponse> responseObserver);

    public void editPhoto(com.examples.abelanav2.grpc.EditPhotoRequest request,
        io.grpc.stub.StreamObserver<com.examples.abelanav2.grpc.StatusResponse> responseObserver);

    public void deletePhoto(com.examples.abelanav2.grpc.DeletePhotoRequest request,
        io.grpc.stub.StreamObserver<com.examples.abelanav2.grpc.StatusResponse> responseObserver);

    public void listMyLikes(com.examples.abelanav2.grpc.PhotoListRequest request,
        io.grpc.stub.StreamObserver<com.examples.abelanav2.grpc.PhotoListResponse> responseObserver);

    public void ratePhoto(com.examples.abelanav2.grpc.VoteRequest request,
        io.grpc.stub.StreamObserver<com.examples.abelanav2.grpc.StatusResponse> responseObserver);
  }

  public static interface AbelanaBlockingClient {

    public com.examples.abelanav2.grpc.SignInResponse signIn(com.examples.abelanav2.grpc.SignInRequest request);

    public com.examples.abelanav2.grpc.PhotoListResponse photoStream(com.examples.abelanav2.grpc.PhotoListRequest request);

    public com.examples.abelanav2.grpc.StatusResponse flagPhoto(com.examples.abelanav2.grpc.FlagRequest request);

    public com.examples.abelanav2.grpc.PhotoListResponse listMyPhotos(com.examples.abelanav2.grpc.PhotoListRequest request);

    public com.examples.abelanav2.grpc.UploadPhotoResponse uploadPhoto(com.examples.abelanav2.grpc.NewPhotoRequest request);

    public com.examples.abelanav2.grpc.StatusResponse editPhoto(com.examples.abelanav2.grpc.EditPhotoRequest request);

    public com.examples.abelanav2.grpc.StatusResponse deletePhoto(com.examples.abelanav2.grpc.DeletePhotoRequest request);

    public com.examples.abelanav2.grpc.PhotoListResponse listMyLikes(com.examples.abelanav2.grpc.PhotoListRequest request);

    public com.examples.abelanav2.grpc.StatusResponse ratePhoto(com.examples.abelanav2.grpc.VoteRequest request);
  }

  public static interface AbelanaFutureClient {

    public com.google.common.util.concurrent.ListenableFuture<com.examples.abelanav2.grpc.SignInResponse> signIn(
        com.examples.abelanav2.grpc.SignInRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.examples.abelanav2.grpc.PhotoListResponse> photoStream(
        com.examples.abelanav2.grpc.PhotoListRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.examples.abelanav2.grpc.StatusResponse> flagPhoto(
        com.examples.abelanav2.grpc.FlagRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.examples.abelanav2.grpc.PhotoListResponse> listMyPhotos(
        com.examples.abelanav2.grpc.PhotoListRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.examples.abelanav2.grpc.UploadPhotoResponse> uploadPhoto(
        com.examples.abelanav2.grpc.NewPhotoRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.examples.abelanav2.grpc.StatusResponse> editPhoto(
        com.examples.abelanav2.grpc.EditPhotoRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.examples.abelanav2.grpc.StatusResponse> deletePhoto(
        com.examples.abelanav2.grpc.DeletePhotoRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.examples.abelanav2.grpc.PhotoListResponse> listMyLikes(
        com.examples.abelanav2.grpc.PhotoListRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.examples.abelanav2.grpc.StatusResponse> ratePhoto(
        com.examples.abelanav2.grpc.VoteRequest request);
  }

  public static class AbelanaStub extends
      io.grpc.stub.AbstractStub<AbelanaStub, AbelanaServiceDescriptor>
      implements Abelana {
    private AbelanaStub(io.grpc.Channel channel,
        AbelanaServiceDescriptor config) {
      super(channel, config);
    }

    @java.lang.Override
    protected AbelanaStub build(io.grpc.Channel channel,
        AbelanaServiceDescriptor config) {
      return new AbelanaStub(channel, config);
    }

    @java.lang.Override
    public void signIn(com.examples.abelanav2.grpc.SignInRequest request,
        io.grpc.stub.StreamObserver<com.examples.abelanav2.grpc.SignInResponse> responseObserver) {
      asyncUnaryCall(
          channel.newCall(config.signIn), request, responseObserver);
    }

    @java.lang.Override
    public void photoStream(com.examples.abelanav2.grpc.PhotoListRequest request,
        io.grpc.stub.StreamObserver<com.examples.abelanav2.grpc.PhotoListResponse> responseObserver) {
      asyncUnaryCall(
          channel.newCall(config.photoStream), request, responseObserver);
    }

    @java.lang.Override
    public void flagPhoto(com.examples.abelanav2.grpc.FlagRequest request,
        io.grpc.stub.StreamObserver<com.examples.abelanav2.grpc.StatusResponse> responseObserver) {
      asyncUnaryCall(
          channel.newCall(config.flagPhoto), request, responseObserver);
    }

    @java.lang.Override
    public void listMyPhotos(com.examples.abelanav2.grpc.PhotoListRequest request,
        io.grpc.stub.StreamObserver<com.examples.abelanav2.grpc.PhotoListResponse> responseObserver) {
      asyncUnaryCall(
          channel.newCall(config.listMyPhotos), request, responseObserver);
    }

    @java.lang.Override
    public void uploadPhoto(com.examples.abelanav2.grpc.NewPhotoRequest request,
        io.grpc.stub.StreamObserver<com.examples.abelanav2.grpc.UploadPhotoResponse> responseObserver) {
      asyncUnaryCall(
          channel.newCall(config.uploadPhoto), request, responseObserver);
    }

    @java.lang.Override
    public void editPhoto(com.examples.abelanav2.grpc.EditPhotoRequest request,
        io.grpc.stub.StreamObserver<com.examples.abelanav2.grpc.StatusResponse> responseObserver) {
      asyncUnaryCall(
          channel.newCall(config.editPhoto), request, responseObserver);
    }

    @java.lang.Override
    public void deletePhoto(com.examples.abelanav2.grpc.DeletePhotoRequest request,
        io.grpc.stub.StreamObserver<com.examples.abelanav2.grpc.StatusResponse> responseObserver) {
      asyncUnaryCall(
          channel.newCall(config.deletePhoto), request, responseObserver);
    }

    @java.lang.Override
    public void listMyLikes(com.examples.abelanav2.grpc.PhotoListRequest request,
        io.grpc.stub.StreamObserver<com.examples.abelanav2.grpc.PhotoListResponse> responseObserver) {
      asyncUnaryCall(
          channel.newCall(config.listMyLikes), request, responseObserver);
    }

    @java.lang.Override
    public void ratePhoto(com.examples.abelanav2.grpc.VoteRequest request,
        io.grpc.stub.StreamObserver<com.examples.abelanav2.grpc.StatusResponse> responseObserver) {
      asyncUnaryCall(
          channel.newCall(config.ratePhoto), request, responseObserver);
    }
  }

  public static class AbelanaBlockingStub extends
      io.grpc.stub.AbstractStub<AbelanaBlockingStub, AbelanaServiceDescriptor>
      implements AbelanaBlockingClient {
    private AbelanaBlockingStub(io.grpc.Channel channel,
        AbelanaServiceDescriptor config) {
      super(channel, config);
    }

    @java.lang.Override
    protected AbelanaBlockingStub build(io.grpc.Channel channel,
        AbelanaServiceDescriptor config) {
      return new AbelanaBlockingStub(channel, config);
    }

    @java.lang.Override
    public com.examples.abelanav2.grpc.SignInResponse signIn(com.examples.abelanav2.grpc.SignInRequest request) {
      return blockingUnaryCall(
          channel.newCall(config.signIn), request);
    }

    @java.lang.Override
    public com.examples.abelanav2.grpc.PhotoListResponse photoStream(com.examples.abelanav2.grpc.PhotoListRequest request) {
      return blockingUnaryCall(
          channel.newCall(config.photoStream), request);
    }

    @java.lang.Override
    public com.examples.abelanav2.grpc.StatusResponse flagPhoto(com.examples.abelanav2.grpc.FlagRequest request) {
      return blockingUnaryCall(
          channel.newCall(config.flagPhoto), request);
    }

    @java.lang.Override
    public com.examples.abelanav2.grpc.PhotoListResponse listMyPhotos(com.examples.abelanav2.grpc.PhotoListRequest request) {
      return blockingUnaryCall(
          channel.newCall(config.listMyPhotos), request);
    }

    @java.lang.Override
    public com.examples.abelanav2.grpc.UploadPhotoResponse uploadPhoto(com.examples.abelanav2.grpc.NewPhotoRequest request) {
      return blockingUnaryCall(
          channel.newCall(config.uploadPhoto), request);
    }

    @java.lang.Override
    public com.examples.abelanav2.grpc.StatusResponse editPhoto(com.examples.abelanav2.grpc.EditPhotoRequest request) {
      return blockingUnaryCall(
          channel.newCall(config.editPhoto), request);
    }

    @java.lang.Override
    public com.examples.abelanav2.grpc.StatusResponse deletePhoto(com.examples.abelanav2.grpc.DeletePhotoRequest request) {
      return blockingUnaryCall(
          channel.newCall(config.deletePhoto), request);
    }

    @java.lang.Override
    public com.examples.abelanav2.grpc.PhotoListResponse listMyLikes(com.examples.abelanav2.grpc.PhotoListRequest request) {
      return blockingUnaryCall(
          channel.newCall(config.listMyLikes), request);
    }

    @java.lang.Override
    public com.examples.abelanav2.grpc.StatusResponse ratePhoto(com.examples.abelanav2.grpc.VoteRequest request) {
      return blockingUnaryCall(
          channel.newCall(config.ratePhoto), request);
    }
  }

  public static class AbelanaFutureStub extends
      io.grpc.stub.AbstractStub<AbelanaFutureStub, AbelanaServiceDescriptor>
      implements AbelanaFutureClient {
    private AbelanaFutureStub(io.grpc.Channel channel,
        AbelanaServiceDescriptor config) {
      super(channel, config);
    }

    @java.lang.Override
    protected AbelanaFutureStub build(io.grpc.Channel channel,
        AbelanaServiceDescriptor config) {
      return new AbelanaFutureStub(channel, config);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.examples.abelanav2.grpc.SignInResponse> signIn(
        com.examples.abelanav2.grpc.SignInRequest request) {
      return unaryFutureCall(
          channel.newCall(config.signIn), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.examples.abelanav2.grpc.PhotoListResponse> photoStream(
        com.examples.abelanav2.grpc.PhotoListRequest request) {
      return unaryFutureCall(
          channel.newCall(config.photoStream), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.examples.abelanav2.grpc.StatusResponse> flagPhoto(
        com.examples.abelanav2.grpc.FlagRequest request) {
      return unaryFutureCall(
          channel.newCall(config.flagPhoto), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.examples.abelanav2.grpc.PhotoListResponse> listMyPhotos(
        com.examples.abelanav2.grpc.PhotoListRequest request) {
      return unaryFutureCall(
          channel.newCall(config.listMyPhotos), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.examples.abelanav2.grpc.UploadPhotoResponse> uploadPhoto(
        com.examples.abelanav2.grpc.NewPhotoRequest request) {
      return unaryFutureCall(
          channel.newCall(config.uploadPhoto), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.examples.abelanav2.grpc.StatusResponse> editPhoto(
        com.examples.abelanav2.grpc.EditPhotoRequest request) {
      return unaryFutureCall(
          channel.newCall(config.editPhoto), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.examples.abelanav2.grpc.StatusResponse> deletePhoto(
        com.examples.abelanav2.grpc.DeletePhotoRequest request) {
      return unaryFutureCall(
          channel.newCall(config.deletePhoto), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.examples.abelanav2.grpc.PhotoListResponse> listMyLikes(
        com.examples.abelanav2.grpc.PhotoListRequest request) {
      return unaryFutureCall(
          channel.newCall(config.listMyLikes), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.examples.abelanav2.grpc.StatusResponse> ratePhoto(
        com.examples.abelanav2.grpc.VoteRequest request) {
      return unaryFutureCall(
          channel.newCall(config.ratePhoto), request);
    }
  }

  public static io.grpc.ServerServiceDefinition bindService(
      final Abelana serviceImpl) {
    return io.grpc.ServerServiceDefinition.builder("abelanav2.grpc.Abelana")
      .addMethod(createMethodDefinition(
          METHOD_SIGN_IN,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                com.examples.abelanav2.grpc.SignInRequest,
                com.examples.abelanav2.grpc.SignInResponse>() {
              @java.lang.Override
              public void invoke(
                  com.examples.abelanav2.grpc.SignInRequest request,
                  io.grpc.stub.StreamObserver<com.examples.abelanav2.grpc.SignInResponse> responseObserver) {
                serviceImpl.signIn(request, responseObserver);
              }
            })))
      .addMethod(createMethodDefinition(
          METHOD_PHOTO_STREAM,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                com.examples.abelanav2.grpc.PhotoListRequest,
                com.examples.abelanav2.grpc.PhotoListResponse>() {
              @java.lang.Override
              public void invoke(
                  com.examples.abelanav2.grpc.PhotoListRequest request,
                  io.grpc.stub.StreamObserver<com.examples.abelanav2.grpc.PhotoListResponse> responseObserver) {
                serviceImpl.photoStream(request, responseObserver);
              }
            })))
      .addMethod(createMethodDefinition(
          METHOD_FLAG_PHOTO,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                com.examples.abelanav2.grpc.FlagRequest,
                com.examples.abelanav2.grpc.StatusResponse>() {
              @java.lang.Override
              public void invoke(
                  com.examples.abelanav2.grpc.FlagRequest request,
                  io.grpc.stub.StreamObserver<com.examples.abelanav2.grpc.StatusResponse> responseObserver) {
                serviceImpl.flagPhoto(request, responseObserver);
              }
            })))
      .addMethod(createMethodDefinition(
          METHOD_LIST_MY_PHOTOS,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                com.examples.abelanav2.grpc.PhotoListRequest,
                com.examples.abelanav2.grpc.PhotoListResponse>() {
              @java.lang.Override
              public void invoke(
                  com.examples.abelanav2.grpc.PhotoListRequest request,
                  io.grpc.stub.StreamObserver<com.examples.abelanav2.grpc.PhotoListResponse> responseObserver) {
                serviceImpl.listMyPhotos(request, responseObserver);
              }
            })))
      .addMethod(createMethodDefinition(
          METHOD_UPLOAD_PHOTO,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                com.examples.abelanav2.grpc.NewPhotoRequest,
                com.examples.abelanav2.grpc.UploadPhotoResponse>() {
              @java.lang.Override
              public void invoke(
                  com.examples.abelanav2.grpc.NewPhotoRequest request,
                  io.grpc.stub.StreamObserver<com.examples.abelanav2.grpc.UploadPhotoResponse> responseObserver) {
                serviceImpl.uploadPhoto(request, responseObserver);
              }
            })))
      .addMethod(createMethodDefinition(
          METHOD_EDIT_PHOTO,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                com.examples.abelanav2.grpc.EditPhotoRequest,
                com.examples.abelanav2.grpc.StatusResponse>() {
              @java.lang.Override
              public void invoke(
                  com.examples.abelanav2.grpc.EditPhotoRequest request,
                  io.grpc.stub.StreamObserver<com.examples.abelanav2.grpc.StatusResponse> responseObserver) {
                serviceImpl.editPhoto(request, responseObserver);
              }
            })))
      .addMethod(createMethodDefinition(
          METHOD_DELETE_PHOTO,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                com.examples.abelanav2.grpc.DeletePhotoRequest,
                com.examples.abelanav2.grpc.StatusResponse>() {
              @java.lang.Override
              public void invoke(
                  com.examples.abelanav2.grpc.DeletePhotoRequest request,
                  io.grpc.stub.StreamObserver<com.examples.abelanav2.grpc.StatusResponse> responseObserver) {
                serviceImpl.deletePhoto(request, responseObserver);
              }
            })))
      .addMethod(createMethodDefinition(
          METHOD_LIST_MY_LIKES,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                com.examples.abelanav2.grpc.PhotoListRequest,
                com.examples.abelanav2.grpc.PhotoListResponse>() {
              @java.lang.Override
              public void invoke(
                  com.examples.abelanav2.grpc.PhotoListRequest request,
                  io.grpc.stub.StreamObserver<com.examples.abelanav2.grpc.PhotoListResponse> responseObserver) {
                serviceImpl.listMyLikes(request, responseObserver);
              }
            })))
      .addMethod(createMethodDefinition(
          METHOD_RATE_PHOTO,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                com.examples.abelanav2.grpc.VoteRequest,
                com.examples.abelanav2.grpc.StatusResponse>() {
              @java.lang.Override
              public void invoke(
                  com.examples.abelanav2.grpc.VoteRequest request,
                  io.grpc.stub.StreamObserver<com.examples.abelanav2.grpc.StatusResponse> responseObserver) {
                serviceImpl.ratePhoto(request, responseObserver);
              }
            }))).build();
  }
}
