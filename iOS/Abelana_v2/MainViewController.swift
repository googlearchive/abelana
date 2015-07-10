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
// This is the base class for almost all ViewControllers of the application.
// Handles most UI elements such as the navigation drawer, error messages, loading spinners and more
// Provides the Abelana client to its children.
//
class MainViewController: UIViewController {

  private var navigationDrawerViewController: NavigationDrawerViewController!
  private var navigationDrawerVisible: Bool = false
  internal var floatingButton: UIButton! // The floating action button to add a photo
  private var spinnerView: UIView! // The view containing the loading spinner
  internal var signInInProgress = false // Avoid infinite loop in the GitkitViewController
  // Height of the status bar and the navigation bar above the application
  private var drawerVerticalOffset: CGFloat = 0
  // Starting position of the navigation drawer during its appearance or disappearance
  private var startingPosition: CGFloat = 0

  internal let abelanaClient = AbelanaClient()

  //
  // Returns UI localized strings.
  //
  internal func localizedString(key: String) -> String {
    return NSBundle.mainBundle().localizedStringForKey(key, value: key, table: nil)
  }

  override func viewDidLoad() {
    super.viewDidLoad()

    if !signInInProgress && !abelanaClient.isSignedIn() {
      self.navigationController!
        .pushViewController(Storyboard.gitkitViewController()!, animated: true)
    }

    setUpNavigationDrawer()
  }

  override func didReceiveMemoryWarning() {
    super.didReceiveMemoryWarning()
    // Dispose of any resources that can be recreated.
  }

  //
  // Displays an error message in an alert.
  //
  internal func showErrorAlert(message: String) {
    let alertController = UIAlertController(title: localizedString("Error"), message: message,
      preferredStyle: UIAlertControllerStyle.Alert)
    alertController.addAction(UIAlertAction(title: localizedString("Dismiss"),
      style: UIAlertActionStyle.Cancel, handler: nil))

    self.presentViewController(alertController, animated: true, completion: nil)
  }

  //
  // Displays a loading spinner, with a background or not.
  //
  internal func showSpinner(background: Bool) {
    self.spinnerView = UIView(frame: self.view.frame)

    let spinner = UIActivityIndicatorView(activityIndicatorStyle:
      UIActivityIndicatorViewStyle.Gray)
    let spinnerSize = CGFloat(80)
    spinner.frame = CGRectMake(self.view.frame.size.width/2 - spinnerSize/2,
      self.view.frame.size.height/2 - spinnerSize/2, spinnerSize, spinnerSize);
    spinner.startAnimating()
    spinnerView.backgroundColor = UIColor(white:0.0, alpha:0.6)
    if background {
      spinnerView.addSubview(spinner)
    } else {
      spinnerView = spinner
    }
    self.view.addSubview(spinnerView)
  }

  //
  // Hides the loading spinner.
  //
  internal func hideSpinner() {
    spinnerView.removeFromSuperview()
  }
}

//
// This extension handles everything related to the navigation drawer.
//
extension MainViewController: UIGestureRecognizerDelegate {

  //
  // Initializes everything for the navigation drawer to work correctly.
  //
  internal func setUpNavigationDrawer() {
    self.drawerVerticalOffset =  self.navigationController!.navigationBar.frame.size.height+20
    self.navigationDrawerViewController = Storyboard.navigationDrawerViewController()
    self.navigationDrawerViewController.setNavigationController(self.navigationController!)
    self.navigationDrawerViewController!.view.frame =
      CGRectMake(-self.navigationController!.view.frame.size.width, self.drawerVerticalOffset,
        self.navigationDrawerViewController!.view.frame.size.width, self.view.frame.size.height)
    self.applyPlainShadow(self.navigationDrawerViewController!.view)

    let panGestureRecognizer = UIPanGestureRecognizer(target: self, action: "handlePanGesture:")
    self.navigationController!.view.addGestureRecognizer(panGestureRecognizer)
  }

  //
  // Called when the navigation drawer menu button is tapped.
  //
  @IBAction func navigationDrawerTapped(sender: AnyObject) {
    if !navigationDrawerVisible {
      self.navigationDrawerViewController!.view.hidden = false
      self.view.insertSubview(self.navigationDrawerViewController!.view, atIndex:4)
      showNavigationDrawer()
    } else {
      hideNavigationDrawer()
    }
  }

  //
  // Puts a shadow around the navigation drawer.
  //
  private func applyPlainShadow(view: UIView) {
    view.layer.masksToBounds = false
    view.layer.shadowColor = UIColor.blackColor().CGColor
    view.layer.shadowOffset = CGSizeMake(0, 10)
    view.layer.shadowOpacity = 0.4
  }


  //
  // Handles a sliding finger from side to side, either to show or to hide the drawer.
  //
  func handlePanGesture(recognizer: UIPanGestureRecognizer) {
    let gestureIsDraggingFromLeftToRight = (recognizer.velocityInView(view).x > 0)
    switch(recognizer.state) {
    case .Began:
      startingPosition = self.navigationDrawerViewController.view!.center.x
      if !navigationDrawerVisible {
        self.navigationDrawerViewController!.view.hidden = false
        self.view.insertSubview(self.navigationDrawerViewController!.view, atIndex:4)
      }
    case .Changed:
      let progress = 2 * recognizer.translationInView(view).x / view.bounds.size.width
      if gestureIsDraggingFromLeftToRight && !navigationDrawerVisible && progress <= 1 {
        self.navigationDrawerViewController.view!.center.x = startingPosition
        + progress * self.navigationDrawerViewController.view!.frame.size.width
      } else if !gestureIsDraggingFromLeftToRight && navigationDrawerVisible && progress >= -1 {
        self.navigationDrawerViewController.view!.center.x = startingPosition
          + progress * self.navigationDrawerViewController.view!.frame.size.width
      }
    case .Ended:
      if navigationDrawerViewController != nil {
        let hasMovedGreaterThanHalfway =
          abs(2 * recognizer.translationInView(view).x / view.bounds.size.width) > 1
        if hasMovedGreaterThanHalfway {
          self.navigationDrawerVisible = !self.navigationDrawerVisible
          if !self.navigationDrawerVisible {
            hideNavigationDrawer()
            self.navigationDrawerViewController.removeFromParentViewController()
          } else {
            showNavigationDrawer()
          }
        } else if !self.navigationDrawerVisible {
          hideNavigationDrawer()
        } else {
          showNavigationDrawer()
        }
      }
    default:
      break
    }
  }

  //
  // Hides the navigation drawer.
  //
  internal func hideNavigationDrawer() {
    UIView.animateWithDuration(0.5, delay: 0, options: .CurveEaseInOut, animations: {
      self.navigationDrawerViewController!.view.frame =
        CGRectMake(-self.navigationController!.view.frame.size.width,
          self.drawerVerticalOffset,
          self.navigationDrawerViewController!.view.frame.size.width,
          self.navigationController!.view.frame.size.height);
      }, completion: {completion in
        self.navigationDrawerViewController!.view.hidden = true
        self.navigationDrawerVisible = false
        self.navigationDrawerViewController.removeFromParentViewController()
    })
  }

  //
  // Displays the navigation drawer.
  //
  internal func showNavigationDrawer() {
    UIView.animateWithDuration(0.5, delay: 0, options: .CurveEaseInOut, animations: {
      self.navigationDrawerViewController!.view.frame = CGRectMake(0, self.drawerVerticalOffset,
        self.navigationDrawerViewController!.view.frame.size.width,
        self.navigationDrawerViewController!.view.frame.size.height);
      }, completion: {completion in
        self.navigationDrawerVisible = true
    })
  }

}
