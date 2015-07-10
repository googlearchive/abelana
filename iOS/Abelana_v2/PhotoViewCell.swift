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

import UIKit

//
// A UICollectionViewCell displaying a photo.
//
class PhotoViewCell: UICollectionViewCell {

  @IBOutlet weak var photoImageView: UIImageView!
  @IBOutlet weak var infoView: UIView!
  @IBOutlet weak var dateLabel: UILabel!
  @IBOutlet weak var descriptionTextView: UITextView!
  @IBOutlet weak var actionBar: UIView!
  @IBOutlet weak var thumbsUpButton: UIButton!
  @IBOutlet weak var thumbsDownButton: UIButton!
  @IBOutlet weak var editButton: UIButton!
  @IBOutlet weak var deleteButton: UIButton!

}
