//
// Copyright 2015 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

import Foundation
import UIKit

//
// The GRPC client to connect to the server.
//
class AbelanaClient {
  private var abelanaConfig = NSDictionary(contentsOfFile: NSBundle.mainBundle()
    .pathForResource("AbelanaConfig", ofType: "plist")!)

  // The remote host address, procol and port: 'http://host:port'
  private var remoteHost: String!
  // The Abelana protobuf package name
  private var packageName: String!
  // The Abelana protobuf service name
  private var interface: String!
  // The phone's preferences storage.
  private let prefs = NSUserDefaults.standardUserDefaults()
  // The photo streams list cache.
  private let cache = AbelanaClientCache()

  //
  // Constructor.
  //
  init() {
    self.remoteHost = (abelanaConfig?.valueForKey("remoteHost") as? String)!
    self.packageName = (abelanaConfig?.valueForKey("packageName") as? String)!
    self.interface = (abelanaConfig?.valueForKey("interface") as? String)!
  }

  //
  // Signs the user in using a GitkitToken.
  //
  internal func signIn(gitkitToken: String,
    completionHandler: (error: Bool, message: String) -> Void) {
      let signInRequest = AGRPCSignInRequest()
      signInRequest.gitkitToken = gitkitToken

      let call = callWithAuth("SignIn", requestsData: signInRequest.data())

      let responsesWriteable = GRXWriteable(valueHandler: { (data) -> Void in
        let signInResponse = AGRPCSignInResponse.parseFromData(data as? NSData, error: nil)
        if signInResponse.error.code != "" {
          completionHandler(error: true, message: self.getErrorMessage(signInResponse.error))
        } else {
          self.prefs.setValue(signInResponse.userToken, forKey: "userToken")
          completionHandler(error: false, message: "")
        }
      }) { (error) -> Void in
        if error != nil {
          completionHandler(error: true, message: self.getGrpcErrorMessage(error))
        }
      }

      call.startWithWriteable(responsesWriteable)
  }

  //
  // Adds a new photo in the database and uploads the photo data to CGS.
  //
  internal func uploadPhoto(image: UIImage, description: String,
    completionHandler: (error: Bool, message: String) -> Void) {
      let uploadRequest = AGRPCNewPhotoRequest()
      uploadRequest.description_p = description

      let call = callWithAuth("UploadPhoto", requestsData: uploadRequest.data())

      let responsesWriteable = GRXWriteable(valueHandler: { (data) -> Void in
        let uploadResponse = AGRPCUploadPhotoResponse.parseFromData(data as? NSData, error: nil)
        if uploadResponse.error.code != "" {
          completionHandler(error: true, message: self.getErrorMessage(uploadResponse.error))
        } else {
          let cloudStorage = CloudStorage()
          cloudStorage.uploadImageToGoogleCloud(image, uploadUrl: uploadResponse.uploadURL,
            completion: {(error: Bool, details: String) -> Void in
              if error {
                completionHandler(error: true, message: "An error occured while uploading.")
              } else {
                completionHandler(error: false, message: "")
              }
          })
        }
        }) { (error) -> Void in
          if error != nil {
            completionHandler(error: true, message: self.getGrpcErrorMessage(error))
          }
      }

      call.startWithWriteable(responsesWriteable)
  }

  //
  // Rates a photo.
  //
  internal func votePhoto(photoId: Int64, vote: Int,
    completionHandler: (error: Bool, message: String) -> Void) {
      let voteRequest = AGRPCVoteRequest()
      voteRequest.photoId = photoId
      switch(vote) {
      case 1:
        voteRequest.vote = AGRPCVoteRequest_VoteType.ThumbsUp
      case -1:
        voteRequest.vote = AGRPCVoteRequest_VoteType.ThumbsDown
      default:
        voteRequest.vote = AGRPCVoteRequest_VoteType.Neutral
      }

      let call = callWithAuth("RatePhoto", requestsData: voteRequest.data())

      let responsesWriteable = GRXWriteable(valueHandler: { (data) -> Void in
        let statusResponse = AGRPCStatusResponse.parseFromData(data as? NSData, error: nil)
        if statusResponse.error.code != "" {
          completionHandler(error: true, message: self.getErrorMessage(statusResponse.error))
        } else {
          completionHandler(error: false, message: "")
        }
        }) { (error) -> Void in
          if error != nil {
            completionHandler(error: true, message: self.getGrpcErrorMessage(error))
          }
      }

      call.startWithWriteable(responsesWriteable)
  }

  //
  // Edits the description of a photo.
  //
  internal func editPhoto(photoId: Int64, description: String,
    completionHandler: (error: Bool, message: String) -> Void) {
      let editRequest = AGRPCEditPhotoRequest()
      editRequest.photoId = photoId
      editRequest.description_p = description

      let call = callWithAuth("EditPhoto", requestsData: editRequest.data())

      let responsesWriteable = GRXWriteable(valueHandler: { (data) -> Void in
        let statusResponse = AGRPCStatusResponse.parseFromData(data as? NSData, error: nil)
        if statusResponse.error.code != "" {
          completionHandler(error: true, message: self.getErrorMessage(statusResponse.error))
        } else {
          completionHandler(error: false, message: "")
        }
        }) { (error) -> Void in
          if error != nil {
            completionHandler(error: true, message: self.getGrpcErrorMessage(error))
          }
      }

      call.startWithWriteable(responsesWriteable)
  }

  //
  // Deletes a photo.
  //
  internal func deletePhoto(photoId: Int64,
    completionHandler: (error: Bool, message: String) -> Void) {
      let deleteRequest = AGRPCDeletePhotoRequest()
      deleteRequest.photoId = photoId

      let call = callWithAuth("DeletePhoto", requestsData: deleteRequest.data())

      let responsesWriteable = GRXWriteable(valueHandler: { (data) -> Void in
        let statusResponse = AGRPCStatusResponse.parseFromData(data as? NSData, error: nil)
        if statusResponse.error.code != "" {
          completionHandler(error: true, message: self.getErrorMessage(statusResponse.error))
        } else {
          completionHandler(error: false, message: "")
        }
        }) { (error) -> Void in
          if error != nil {
            completionHandler(error: true, message: self.getGrpcErrorMessage(error))
          }
      }

      call.startWithWriteable(responsesWriteable)
  }

  //
  // Reports a photo as inappropriate.
  //
  internal func flagPhoto(photoId: Int64,
    completionHandler: (error: Bool, message: String) -> Void) {
      let flagRequest = AGRPCFlagRequest()
      flagRequest.photoId = photoId

      let call = callWithAuth("FlagPhoto", requestsData: flagRequest.data())

      let responsesWriteable = GRXWriteable(valueHandler: { (data) -> Void in
        let statusResponse = AGRPCStatusResponse.parseFromData(data as? NSData, error: nil)
        if statusResponse.error.code != "" {
          completionHandler(error: true, message: self.getErrorMessage(statusResponse.error))
        } else {
          completionHandler(error: false, message: "")
        }
        }) { (error) -> Void in
          if error != nil {
            completionHandler(error: true, message: self.getGrpcErrorMessage(error))
          }
      }

      call.startWithWriteable(responsesWriteable)
  }

  //
  // Fetches photo from the server, either refeshes the stream or gets the next page of a stream.
  //
  internal func getPhotoList(photoListType: PhotoListType, nextPage: Bool,
    completionHandler: (error: Bool, message: String, photos: [Photo] ) -> Void) {
      var photoListRequest = AGRPCPhotoListRequest()
      if nextPage {
        photoListRequest.pageNumber = cache.photoListsNextPage[photoListType]!.longLongValue
      } else {
        photoListRequest.pageNumber = 0
      }

      var callName = ""
      switch (photoListType) {
      case .Stream:
        callName = "PhotoStream"
      case .Likes:
        callName = "ListMyLikes"
      case .Mine:
        callName = "ListMyPhotos"
      default:
        break;
      }
      let call = callWithAuth(callName, requestsData: photoListRequest.data())

      let responsesWriteable = GRXWriteable(valueHandler: { (data) -> Void in
        let photoListResponse = AGRPCPhotoListResponse.parseFromData(data as? NSData, error: nil)
        if photoListResponse.error.code != "" {
          completionHandler(error: true, message: self.getErrorMessage(photoListResponse.error)
            + "\nDisplaying photos from cache.", photos: self.cache.photoLists[photoListType]!)
        } else {
            self.cache.photoListsNextPage[photoListType] =
              NSNumber(longLong:photoListResponse.nextPage)
          if self.cache.photoListsNextPage[photoListType] == nil {
            self.cache.photoListsNextPage[photoListType] = 0
          }
          if !nextPage {
            self.cache.photoLists[photoListType]?.removeAll(keepCapacity: false)
          }
          for p in photoListResponse.photoArray {
            self.cache.photoLists[photoListType]!.append(Photo(photoId: p.photoId, userId: p.userId,
              url: p.uRL, description: p.description_p, date: p.date, vote: p.rating))
          }
          completionHandler(error: false, message: "",photos: self.cache.photoLists[photoListType]!)
        }
        }) { (error) -> Void in
          if error != nil {
            completionHandler(error: true, message: self.getGrpcErrorMessage(error)
              + "\nDisplaying photos from cache.", photos: self.cache.photoLists[photoListType]!)
          }
      }
      call.startWithWriteable(responsesWriteable)
  }

  //
  // Says if a stream has more pages that can be fetched.
  //
  internal func hasMorePages(photoListType: PhotoListType) -> Bool {
    return (self.cache.photoListsNextPage[photoListType] != 0)
  }

  //
  // Signs the user out of the application.
  //
  internal func signOut() {
    self.prefs.setValue("", forKey: "userToken")
    self.prefs.synchronize()
  }

  //
  // Says if a user is signed in the application.
  //
  internal func isSignedIn() -> Bool {
    if prefs.stringForKey("userToken") != nil && prefs.stringForKey("userToken") != "" {
      return true
    }
    return false
  }

  //
  //  Stores the photo streams retrieved from the cache to the phone's storage.
  //
  internal func backup() {
    cache.backup()
  }

  //
  // Creates a gRPC call to the Abelana gRPC server.
  //
  private func callWithAuth(method: String, requestsData: NSData) -> GRPCCall {
    let method = GRPCMethodName(package: packageName, interface: interface, method: method)
    let requestsWriter = GRXWriter(value: requestsData)
    let call = GRPCCall(host: remoteHost, method:method, requestsWriter:requestsWriter)
    if call.requestMetadata == nil {
      call.requestMetadata = NSMutableDictionary()
    }
    call.requestMetadata.setValue(prefs.stringForKey("userToken"), forKey: "Authorization")

    return call
  }

  //
  // Returns and logs an error message based on the error code.
  //
  private func getErrorMessage(error: AGRPCError) -> String {
    println(String(format:"Response has error code %@ and message: %@",error.code, error.details))
    if error.code == "403" {
      signOut()
      return "You are not authenticated."
    } else if error.code == "500" {
      "An internal server error occured."
    }
    return "An unknown error occured."
  }

  //
  // Returns and logs an error message for GRPC errors.
  //
  private func getGrpcErrorMessage(error: NSError) -> String {
    println(String(format:"GRPC error: %@", error))
    return "Impossible to connect to the server."
  }
}
