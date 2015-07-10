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

//
// Describes a photo to display.
//
internal class Photo {

  var photoId: Int64!
  var userId: String!
  var url: String!   // The url to fetch the thumbnail photo
  var description: String!
  var date: Int64! // The photo date as a timestamp
  var vote: Int64!   // The photo vote as a value from +1/0/-1

  //
  // Constructor.
  //
  init(photoId: Int64, userId: String, url: String, description: String, date: Int64, vote: Int64) {
    self.photoId = photoId
    self.userId = userId
    self.url = url
    self.description = description
    self.date = date
    self.vote = vote
  }
}
