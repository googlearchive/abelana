/*
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#import "Abelanav2.pbobjc.h"
#import <gRPC/ProtoService.h>


@protocol GRXWriteable;
@protocol GRXWriter;

@protocol AGRPCAbelana <NSObject>

#pragma mark SignIn(SignInRequest) returns (SignInResponse)

- (void)signInWithRequest:(AGRPCSignInRequest *)request handler:(void(^)(AGRPCSignInResponse *response, NSError *error))handler;

- (ProtoRPC *)RPCToSignInWithRequest:(AGRPCSignInRequest *)request handler:(void(^)(AGRPCSignInResponse *response, NSError *error))handler;


#pragma mark PhotoStream(PhotoListRequest) returns (PhotoListResponse)

- (void)photoStreamWithRequest:(AGRPCPhotoListRequest *)request handler:(void(^)(AGRPCPhotoListResponse *response, NSError *error))handler;

- (ProtoRPC *)RPCToPhotoStreamWithRequest:(AGRPCPhotoListRequest *)request handler:(void(^)(AGRPCPhotoListResponse *response, NSError *error))handler;


#pragma mark FlagPhoto(FlagRequest) returns (StatusResponse)

- (void)flagPhotoWithRequest:(AGRPCFlagRequest *)request handler:(void(^)(AGRPCStatusResponse *response, NSError *error))handler;

- (ProtoRPC *)RPCToFlagPhotoWithRequest:(AGRPCFlagRequest *)request handler:(void(^)(AGRPCStatusResponse *response, NSError *error))handler;


#pragma mark ListMyPhotos(PhotoListRequest) returns (PhotoListResponse)

- (void)listMyPhotosWithRequest:(AGRPCPhotoListRequest *)request handler:(void(^)(AGRPCPhotoListResponse *response, NSError *error))handler;

- (ProtoRPC *)RPCToListMyPhotosWithRequest:(AGRPCPhotoListRequest *)request handler:(void(^)(AGRPCPhotoListResponse *response, NSError *error))handler;


#pragma mark UploadPhoto(NewPhotoRequest) returns (UploadPhotoResponse)

- (void)uploadPhotoWithRequest:(AGRPCNewPhotoRequest *)request handler:(void(^)(AGRPCUploadPhotoResponse *response, NSError *error))handler;

- (ProtoRPC *)RPCToUploadPhotoWithRequest:(AGRPCNewPhotoRequest *)request handler:(void(^)(AGRPCUploadPhotoResponse *response, NSError *error))handler;


#pragma mark EditPhoto(EditPhotoRequest) returns (StatusResponse)

- (void)editPhotoWithRequest:(AGRPCEditPhotoRequest *)request handler:(void(^)(AGRPCStatusResponse *response, NSError *error))handler;

- (ProtoRPC *)RPCToEditPhotoWithRequest:(AGRPCEditPhotoRequest *)request handler:(void(^)(AGRPCStatusResponse *response, NSError *error))handler;


#pragma mark DeletePhoto(DeletePhotoRequest) returns (StatusResponse)

- (void)deletePhotoWithRequest:(AGRPCDeletePhotoRequest *)request handler:(void(^)(AGRPCStatusResponse *response, NSError *error))handler;

- (ProtoRPC *)RPCToDeletePhotoWithRequest:(AGRPCDeletePhotoRequest *)request handler:(void(^)(AGRPCStatusResponse *response, NSError *error))handler;


#pragma mark ListMyLikes(PhotoListRequest) returns (PhotoListResponse)

- (void)listMyLikesWithRequest:(AGRPCPhotoListRequest *)request handler:(void(^)(AGRPCPhotoListResponse *response, NSError *error))handler;

- (ProtoRPC *)RPCToListMyLikesWithRequest:(AGRPCPhotoListRequest *)request handler:(void(^)(AGRPCPhotoListResponse *response, NSError *error))handler;


#pragma mark RatePhoto(VoteRequest) returns (StatusResponse)

- (void)ratePhotoWithRequest:(AGRPCVoteRequest *)request handler:(void(^)(AGRPCStatusResponse *response, NSError *error))handler;

- (ProtoRPC *)RPCToRatePhotoWithRequest:(AGRPCVoteRequest *)request handler:(void(^)(AGRPCStatusResponse *response, NSError *error))handler;


@end

// Basic service implementation, over gRPC, that only does marshalling and parsing.
@interface AGRPCAbelana : ProtoService<AGRPCAbelana>
- (instancetype)initWithHost:(NSString *)host NS_DESIGNATED_INITIALIZER;
@end
