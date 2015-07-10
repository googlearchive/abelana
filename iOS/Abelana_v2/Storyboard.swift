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
// A class used for easy access to named Storyboard elements.
//
internal class Storyboard {
  //
  // Returns the Storyboard.
  //
  class func mainStoryboard() -> UIStoryboard {
    return UIStoryboard(name: "Main", bundle: NSBundle.mainBundle())
  }

  //
  // Returns the PhotoStreamViewController.
  //
  class func photoStreamViewController() -> PhotoStreamViewController? {
    return mainStoryboard().instantiateViewControllerWithIdentifier("PhotoStream")
      as? PhotoStreamViewController
  }

  //
  // Returns the UploadViewController.
  //
  class func uploadViewController() -> UploadViewController? {
    return mainStoryboard().instantiateViewControllerWithIdentifier("UploadView")
      as? UploadViewController
  }

  //
  // Returns the GitkitViewController.
  //
  class func gitkitViewController() -> GitkitViewController? {
    return mainStoryboard().instantiateViewControllerWithIdentifier("Gitkit")
      as? GitkitViewController
  }

  //
  // Returns the NavigationDrawerController.
  //
  class func navigationDrawerViewController() -> NavigationDrawerViewController? {
    return mainStoryboard().instantiateViewControllerWithIdentifier("NavigationDrawer")
      as? NavigationDrawerViewController
  }

}
