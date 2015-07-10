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
import MobileCoreServices
import UIKit

//
// The upload view controller class, used for the upload photos flow.
//
class UploadViewController: MainViewController, UINavigationControllerDelegate,
  UIImagePickerControllerDelegate {

  @IBOutlet var imageView:UIImageView!=nil // The selected photo
  @IBOutlet var textField:UITextField!=nil // The photo description

  override func viewDidLoad() {
    super.viewDidLoad()

    let singleTap = UITapGestureRecognizer(target: self, action: Selector("tapDetected"))
    singleTap.numberOfTapsRequired = 1
    imageView.userInteractionEnabled = true
    imageView.addGestureRecognizer(singleTap)

    if imageView.image == nil {
      selectImage()
    }
  }

  override func didReceiveMemoryWarning() {
    super.didReceiveMemoryWarning()
  }

  //
  // Called when the user clicks on the 'cancel' button.
  //
  @IBAction func cancel() {
    self.navigationController!.popViewControllerAnimated(true)
  }

  //
  // Called when the user clicks on the 'upload' button.
  // Verifies the information provided by the user and uploads the photo.
  //
  @IBAction func upload(sender: AnyObject) {
    self.view.endEditing(true)
    if imageView.image != nil && !textField.text.isEmpty {
      showSpinner(true)
      abelanaClient.uploadPhoto(imageView.image!, description: textField.text,
        completionHandler: { (error, message) -> Void in
          self.hideSpinner()
          if error {
            self.showErrorAlert(message)
          } else {
            NSOperationQueue.mainQueue().addOperationWithBlock {
              self.navigationController!
              .pushViewController(Storyboard.photoStreamViewController()!, animated: true)
            }
          }
        })
    } else {
      var message = ""
      if imageView.image == nil {
        message += localizedString("Please select an image!")
      }
      if textField.text.isEmpty {
        if message != "" {
          message += "\n"
        }
        message += localizedString("Please enter a description for the image.")
      }
      showErrorAlert(message)
    }

  }

  //
  // Called if the user taps on the selected image, to change it.
  //
  @IBAction func tapDetected() {
    selectImage()
  }

  //
  // Initiates the select image flow in iOS.
  //
  func selectImage() {
    if UIImagePickerController.isSourceTypeAvailable(
      UIImagePickerControllerSourceType.PhotoLibrary) {
        var imagePicker = UIImagePickerController()
        imagePicker.delegate = self
        imagePicker.sourceType = UIImagePickerControllerSourceType.PhotoLibrary;
        imagePicker.mediaTypes = [String(kUTTypeImage)]
        imagePicker.allowsEditing = false
        self.presentViewController(imagePicker, animated: true, completion: nil)
    }
  }

  //
  // Called when the select image flow from iOS finishes.
  //
  func imagePickerController(picker: UIImagePickerController,
    didFinishPickingMediaWithInfo info: [NSObject : AnyObject]) {
      if let pickedImage = info[UIImagePickerControllerOriginalImage] as? UIImage {
        imageView.image = pickedImage
        imageView.backgroundColor = UIColor(white: 0.0, alpha: 0.0)
      }
      dismissViewControllerAnimated(true, completion: nil)
  }

}
