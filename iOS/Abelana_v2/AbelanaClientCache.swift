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

class AbelanaClientCache {
  // The phone's preferences storage.
  private let prefs = NSUserDefaults.standardUserDefaults()
  // The array used to cache the photo lists retrieved from the server.
  internal var photoLists = [PhotoListType: [Photo]]()
  // The array used to cache the next pages of the photo lists that we can retrieve from the server.
  internal var photoListsNextPage = [PhotoListType: NSNumber]()

  //
  // Constructor.
  //
  init() {
    photoLists = [PhotoListType: [Photo]]()
    photoListsNextPage = [PhotoListType: NSNumber]()
    if !restore() {
      photoLists.updateValue([Photo](), forKey: PhotoListType.Stream)
      photoLists.updateValue([Photo](), forKey: PhotoListType.Likes)
      photoLists.updateValue([Photo](), forKey: PhotoListType.Mine)
      photoListsNextPage.updateValue(0, forKey: PhotoListType.Stream)
      photoListsNextPage.updateValue(0, forKey: PhotoListType.Likes)
      photoListsNextPage.updateValue(0, forKey: PhotoListType.Mine)
    }
  }

  //
  // Backs up the cache content to the phone disk.
  //
  internal func backup() {
    prefs.setObject(photoLists[PhotoListType.Stream], forKey: "photoLists_Stream")
    prefs.setObject(photoLists[PhotoListType.Likes], forKey: "photoLists_Likes")
    prefs.setObject(photoLists[PhotoListType.Mine], forKey: "photoLists_Mine")
    prefs.setObject(photoListsNextPage[PhotoListType.Stream],
      forKey: "photoListsNextPage_Stream")
    prefs.setObject(photoListsNextPage[PhotoListType.Likes],
      forKey: "photoListsNextPage_Likes")
    prefs.setObject(photoListsNextPage[PhotoListType.Mine],
      forKey: "photoListsNextPage_Mine")
    self.prefs.synchronize()
  }

  //
  // Restores the cache content from the phone disk.
  //
  private func restore() -> Bool {
    if prefs.objectForKey("photoLists_Stream") != nil
      && prefs.objectForKey("photoLists_Likes") != nil
      && prefs.objectForKey("photoLists_Mine") != nil
      && prefs.objectForKey("photoListsNextPage_Stream") != nil
      && prefs.objectForKey("photoListsNextPage_Likes") != nil
      && prefs.objectForKey("photoListsNextPage_Mine") != nil {

    photoLists.updateValue((prefs.objectForKey("photoLists_Stream") as? [Photo])!,
      forKey: PhotoListType.Stream)
    photoLists.updateValue((prefs.objectForKey("photoLists_Likes") as? [Photo])!,
      forKey: PhotoListType.Likes)
    photoLists.updateValue((prefs.objectForKey("photoLists_Mine") as? [Photo])!,
      forKey: PhotoListType.Mine)
    photoListsNextPage.updateValue((prefs.objectForKey("photoListsNextPage_Stream") as? NSNumber)!,
      forKey: PhotoListType.Stream)
    photoListsNextPage.updateValue((prefs.objectForKey("photoListsNextPage_Likes") as? NSNumber)!,
      forKey: PhotoListType.Likes)
    photoListsNextPage.updateValue((prefs.objectForKey("photoListsNextPage_Mine") as? NSNumber)!,
      forKey: PhotoListType.Mine)
      return true
    }
    return false
  }
}
