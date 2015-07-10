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
// The navigation drawer view controller class, used to navigate in the application.
//
class NavigationDrawerViewController: UIViewController {

  //
  // Returns UI localized strings.
  //
  internal func localizedString(key: String) -> String {
    return NSBundle.mainBundle().localizedStringForKey(key, value: key, table: nil)
  }

  @IBOutlet var tableView: UITableView! // Contains the menu items.
  var menuItemsNames: [String] = []
  var menuItemsImages: [String] = []
  let cellIdentifier: String = "NavigationDrawerMenuItem" // Identifier for the menu items cells.
  var parentNavigationController: UINavigationController!   // The global navigation controller.
  let abelanaClient = AbelanaClient()

  //
  // Sets the navigation controller.
  //
  internal func setNavigationController(controller: UINavigationController){
    parentNavigationController = controller
  }

  override func viewDidLoad() {
    menuItemsNames = [localizedString("Home"), localizedString("Upload picture"),
      localizedString("My pictures"), localizedString("Favorites"), localizedString("Sign out")]
    menuItemsImages = ["ic_home", "ic_file_upload", "ic_image", "ic_favourite",
      "ic_exit_to_app"]
    if !abelanaClient.isSignedIn() {
      menuItemsNames[4] = localizedString("Sign in")
    }

    super.viewDidLoad()
  }

  override func viewWillAppear(animated: Bool) {
    if !abelanaClient.isSignedIn() {
      menuItemsNames[4] = localizedString("Sign in")
      self.tableView.reloadData()
    }

    super.viewWillAppear(animated)
  }


  override func didReceiveMemoryWarning() {
    super.didReceiveMemoryWarning()
  }
}

//
// Implements all TableView related functions.
//
extension NavigationDrawerViewController: UITableViewDelegate, UITableViewDataSource {

  func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
    return self.menuItemsNames.count
  }

  func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath)
    -> UITableViewCell {
    var cell: NavigationDrawerMenuItemCell =
      (self.tableView.dequeueReusableCellWithIdentifier(self.cellIdentifier)
        as? NavigationDrawerMenuItemCell)!
    cell.textLabel?.text = self.menuItemsNames[indexPath.row]
    cell.imageView?.image = UIImage(named:self.menuItemsImages[indexPath.row])

    return cell
  }

  func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
    abelanaClient.backup()
    switch indexPath.row {
    case 0:
      let photoStreamViewController = Storyboard.photoStreamViewController()!
      photoStreamViewController.photoListType = PhotoListType.Stream
      parentNavigationController.pushViewController(photoStreamViewController,
        animated: true)
    case 1:
      parentNavigationController.pushViewController(Storyboard.uploadViewController()!,
        animated: true)
    case 2:
      let photoStreamViewController = Storyboard.photoStreamViewController()!
      photoStreamViewController.photoListType = PhotoListType.Mine
      parentNavigationController.pushViewController(photoStreamViewController,
        animated: true)
    case 3:
      let photoStreamViewController = Storyboard.photoStreamViewController()!
      photoStreamViewController.photoListType = PhotoListType.Likes
      parentNavigationController.pushViewController(photoStreamViewController,
        animated: true)
      case 4:
        abelanaClient.signOut()
        parentNavigationController.pushViewController(Storyboard.gitkitViewController()!,
          animated: true)
    default:
        let photoStreamViewController = Storyboard.photoStreamViewController()!
        photoStreamViewController.photoListType = PhotoListType.Stream
        parentNavigationController.pushViewController(photoStreamViewController,
          animated: true)
    }
  }
}

//
// A navigation drawer table view cell.
//
class NavigationDrawerMenuItemCell: UITableViewCell {
  @IBOutlet weak var itemLabel: UILabel!
  @IBOutlet weak var itemImage: UIImageView!
}
