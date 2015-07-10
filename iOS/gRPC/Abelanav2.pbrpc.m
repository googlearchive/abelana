#import "Abelanav2.pbrpc.h"
#import <gRPC/GRXWriteable.h>
#import <gRPC/GRXWriter+Immediate.h>
#import <gRPC/ProtoRPC.h>

static NSString *const kPackageName = @"abelanav2.grpc";
static NSString *const kServiceName = @"Abelana";

@implementation AGRPCAbelana

// Designated initializer
- (instancetype)initWithHost:(NSString *)host {
  return (self = [super initWithHost:host packageName:kPackageName serviceName:kServiceName]);
}

// Override superclass initializer to disallow different package and service names.
- (instancetype)initWithHost:(NSString *)host
                 packageName:(NSString *)packageName
                 serviceName:(NSString *)serviceName {
  return [self initWithHost:host];
}


#pragma mark SignIn(SignInRequest) returns (SignInResponse)

- (void)signInWithRequest:(AGRPCSignInRequest *)request handler:(void(^)(AGRPCSignInResponse *response, NSError *error))handler{
  [[self RPCToSignInWithRequest:request handler:handler] start];
}
// Returns a not-yet-started RPC object.
- (ProtoRPC *)RPCToSignInWithRequest:(AGRPCSignInRequest *)request handler:(void(^)(AGRPCSignInResponse *response, NSError *error))handler{
  return [self RPCToMethod:@"SignIn"
            requestsWriter:[GRXWriter writerWithValue:request]
             responseClass:[AGRPCSignInResponse class]
        responsesWriteable:[GRXWriteable writeableWithSingleValueHandler:handler]];
}
#pragma mark PhotoStream(PhotoListRequest) returns (PhotoListResponse)

- (void)photoStreamWithRequest:(AGRPCPhotoListRequest *)request handler:(void(^)(AGRPCPhotoListResponse *response, NSError *error))handler{
  [[self RPCToPhotoStreamWithRequest:request handler:handler] start];
}
// Returns a not-yet-started RPC object.
- (ProtoRPC *)RPCToPhotoStreamWithRequest:(AGRPCPhotoListRequest *)request handler:(void(^)(AGRPCPhotoListResponse *response, NSError *error))handler{
  return [self RPCToMethod:@"PhotoStream"
            requestsWriter:[GRXWriter writerWithValue:request]
             responseClass:[AGRPCPhotoListResponse class]
        responsesWriteable:[GRXWriteable writeableWithSingleValueHandler:handler]];
}
#pragma mark FlagPhoto(FlagRequest) returns (StatusResponse)

- (void)flagPhotoWithRequest:(AGRPCFlagRequest *)request handler:(void(^)(AGRPCStatusResponse *response, NSError *error))handler{
  [[self RPCToFlagPhotoWithRequest:request handler:handler] start];
}
// Returns a not-yet-started RPC object.
- (ProtoRPC *)RPCToFlagPhotoWithRequest:(AGRPCFlagRequest *)request handler:(void(^)(AGRPCStatusResponse *response, NSError *error))handler{
  return [self RPCToMethod:@"FlagPhoto"
            requestsWriter:[GRXWriter writerWithValue:request]
             responseClass:[AGRPCStatusResponse class]
        responsesWriteable:[GRXWriteable writeableWithSingleValueHandler:handler]];
}
#pragma mark ListMyPhotos(PhotoListRequest) returns (PhotoListResponse)

- (void)listMyPhotosWithRequest:(AGRPCPhotoListRequest *)request handler:(void(^)(AGRPCPhotoListResponse *response, NSError *error))handler{
  [[self RPCToListMyPhotosWithRequest:request handler:handler] start];
}
// Returns a not-yet-started RPC object.
- (ProtoRPC *)RPCToListMyPhotosWithRequest:(AGRPCPhotoListRequest *)request handler:(void(^)(AGRPCPhotoListResponse *response, NSError *error))handler{
  return [self RPCToMethod:@"ListMyPhotos"
            requestsWriter:[GRXWriter writerWithValue:request]
             responseClass:[AGRPCPhotoListResponse class]
        responsesWriteable:[GRXWriteable writeableWithSingleValueHandler:handler]];
}
#pragma mark UploadPhoto(NewPhotoRequest) returns (UploadPhotoResponse)

- (void)uploadPhotoWithRequest:(AGRPCNewPhotoRequest *)request handler:(void(^)(AGRPCUploadPhotoResponse *response, NSError *error))handler{
  [[self RPCToUploadPhotoWithRequest:request handler:handler] start];
}
// Returns a not-yet-started RPC object.
- (ProtoRPC *)RPCToUploadPhotoWithRequest:(AGRPCNewPhotoRequest *)request handler:(void(^)(AGRPCUploadPhotoResponse *response, NSError *error))handler{
  return [self RPCToMethod:@"UploadPhoto"
            requestsWriter:[GRXWriter writerWithValue:request]
             responseClass:[AGRPCUploadPhotoResponse class]
        responsesWriteable:[GRXWriteable writeableWithSingleValueHandler:handler]];
}
#pragma mark EditPhoto(EditPhotoRequest) returns (StatusResponse)

- (void)editPhotoWithRequest:(AGRPCEditPhotoRequest *)request handler:(void(^)(AGRPCStatusResponse *response, NSError *error))handler{
  [[self RPCToEditPhotoWithRequest:request handler:handler] start];
}
// Returns a not-yet-started RPC object.
- (ProtoRPC *)RPCToEditPhotoWithRequest:(AGRPCEditPhotoRequest *)request handler:(void(^)(AGRPCStatusResponse *response, NSError *error))handler{
  return [self RPCToMethod:@"EditPhoto"
            requestsWriter:[GRXWriter writerWithValue:request]
             responseClass:[AGRPCStatusResponse class]
        responsesWriteable:[GRXWriteable writeableWithSingleValueHandler:handler]];
}
#pragma mark DeletePhoto(DeletePhotoRequest) returns (StatusResponse)

- (void)deletePhotoWithRequest:(AGRPCDeletePhotoRequest *)request handler:(void(^)(AGRPCStatusResponse *response, NSError *error))handler{
  [[self RPCToDeletePhotoWithRequest:request handler:handler] start];
}
// Returns a not-yet-started RPC object.
- (ProtoRPC *)RPCToDeletePhotoWithRequest:(AGRPCDeletePhotoRequest *)request handler:(void(^)(AGRPCStatusResponse *response, NSError *error))handler{
  return [self RPCToMethod:@"DeletePhoto"
            requestsWriter:[GRXWriter writerWithValue:request]
             responseClass:[AGRPCStatusResponse class]
        responsesWriteable:[GRXWriteable writeableWithSingleValueHandler:handler]];
}
#pragma mark ListMyLikes(PhotoListRequest) returns (PhotoListResponse)

- (void)listMyLikesWithRequest:(AGRPCPhotoListRequest *)request handler:(void(^)(AGRPCPhotoListResponse *response, NSError *error))handler{
  [[self RPCToListMyLikesWithRequest:request handler:handler] start];
}
// Returns a not-yet-started RPC object.
- (ProtoRPC *)RPCToListMyLikesWithRequest:(AGRPCPhotoListRequest *)request handler:(void(^)(AGRPCPhotoListResponse *response, NSError *error))handler{
  return [self RPCToMethod:@"ListMyLikes"
            requestsWriter:[GRXWriter writerWithValue:request]
             responseClass:[AGRPCPhotoListResponse class]
        responsesWriteable:[GRXWriteable writeableWithSingleValueHandler:handler]];
}
#pragma mark RatePhoto(VoteRequest) returns (StatusResponse)

- (void)ratePhotoWithRequest:(AGRPCVoteRequest *)request handler:(void(^)(AGRPCStatusResponse *response, NSError *error))handler{
  [[self RPCToRatePhotoWithRequest:request handler:handler] start];
}
// Returns a not-yet-started RPC object.
- (ProtoRPC *)RPCToRatePhotoWithRequest:(AGRPCVoteRequest *)request handler:(void(^)(AGRPCStatusResponse *response, NSError *error))handler{
  return [self RPCToMethod:@"RatePhoto"
            requestsWriter:[GRXWriter writerWithValue:request]
             responseClass:[AGRPCStatusResponse class]
        responsesWriteable:[GRXWriteable writeableWithSingleValueHandler:handler]];
}
@end
