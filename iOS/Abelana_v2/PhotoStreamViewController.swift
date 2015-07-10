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
// The photo stream view controller class, used to display the different photo streams (home,
// my pictures, my favorites).
//
class PhotoStreamViewController: MainViewController {

  private let sectionInsets = UIEdgeInsets(top: 20.0, left: 20.0,
    bottom: 20.0, right: 20.0)  // The margins around each cell.
  private let reuseIdentifier = "PhotoCell" // The identifier for the photo cell.
  internal var loading = false;   // Indicates if we are already loading some photos.
  @IBOutlet var collectionView: UICollectionView!
  private var photoList: [Photo] =  []
  // The type of photo stream to show. Will be used to adapt some UI accordingly.
  internal var photoListType = PhotoListType.Stream

  //
  // Adds the floating action button to the UI.
  //
  private func addFloatingButton() {
    floatingButton = UIButton.buttonWithType(UIButtonType.System) as? UIButton;
    floatingButton.frame =
      CGRectMake(self.view.frame.size.width-50-15,
        self.navigationController!.view.frame.size.height-50-15, 50, 50)
    floatingButton.backgroundColor = UIColor.redColor()
    floatingButton.setImage(UIImage(named:"ic_add"), forState: UIControlState.Normal)
    floatingButton.tintColor=UIColor.whiteColor()
    floatingButton.addTarget(self, action: "floatingButtonAction:",
      forControlEvents: UIControlEvents.TouchUpInside)
    floatingButton.layer.cornerRadius=25;

    floatingButton.layer.masksToBounds = false
    floatingButton.layer.shadowColor = UIColor.blackColor().CGColor
    floatingButton.layer.shadowOffset = CGSizeMake(0, 5)
    floatingButton.layer.shadowOpacity = 0.4

    self.view.insertSubview(floatingButton, atIndex:8)
  }

  //
  // Called when the floating action button is tapped.
  //
  func floatingButtonAction(sender: UIButton!) {
    self.navigationController!.pushViewController(Storyboard.uploadViewController()!,
      animated: true)
  }

  override func viewDidLoad() {
    super.viewDidLoad()

    switch(photoListType) {
    case .Stream:
      addFloatingButton()
      self.title = localizedString("Home")
    case .Mine:
      addFloatingButton()
      self.title = localizedString("My pictures")
    case .Likes:
      self.title = localizedString("Favorites")
    default:
      break;
    }

    showSpinner(false)
    refreshPhotoList(self)
  }

  override func didReceiveMemoryWarning() {
    super.didReceiveMemoryWarning()
  }

  func getIndexPath(sender: AnyObject) -> NSIndexPath {
    let point: CGPoint = collectionView.convertPoint(sender.center, fromView: sender.superview)
    let indexPath = collectionView.indexPathForItemAtPoint(point)
    return indexPath!
  }
}

//
// Implements all the UICollectionView data source related functions.
//
extension PhotoStreamViewController: UICollectionViewDataSource {

  func numberOfSectionsInCollectionView(collectionView: UICollectionView) -> Int {
    return 1
  }


  func collectionView(collectionView: UICollectionView, numberOfItemsInSection section: Int)
    -> Int {
      return self.photoList.count
  }

  func collectionView(collectionView: UICollectionView,
    cellForItemAtIndexPath indexPath: NSIndexPath) -> UICollectionViewCell {
      let cell = (collectionView.dequeueReusableCellWithReuseIdentifier(reuseIdentifier,
        forIndexPath: indexPath) as? PhotoViewCell)!
      // Configure the cell
      configureCell(cell)

      // Put stuff in the cell
      cell.photoImageView.sd_setImageWithURL(NSURL(string: self.photoList[indexPath.item].url))

      let timeInterval = NSTimeInterval(NSNumber(longLong:
        self.photoList[indexPath.item].date/1000000))
      let date = NSDate(timeIntervalSince1970: timeInterval)
      let formatter = NSDateFormatter()
      formatter.dateStyle = .MediumStyle
      cell.dateLabel.text = formatter.stringFromDate(date)
      cell.descriptionTextView.text = self.photoList[indexPath.item].description
      var rect = cell.descriptionTextView.frame
      rect.size.height = cell.descriptionTextView.contentSize.height;
      cell.descriptionTextView.sizeToFit()
      cell.descriptionTextView.layoutIfNeeded()
      cell.descriptionTextView.textContainer.size = cell.descriptionTextView.frame.size
      var range = NSMakeRange(0, 1)
      cell.descriptionTextView.scrollRangeToVisible(range)

      switch(self.photoList[indexPath.item].vote) {
      case 1:
        cell.thumbsUpButton.selected = true
        cell.thumbsDownButton.selected = false
      case -1:
        cell.thumbsUpButton.selected = false
        cell.thumbsDownButton.selected = true
      default:
        cell.thumbsUpButton.selected = false
        cell.thumbsDownButton.selected = false
      }

      return cell
  }

  //
  // Improves the cell UI and add a gesture recognizer for contextual menu.
  //
  private func configureCell(cell: PhotoViewCell) {
    cell.layer.borderWidth=1;
    cell.layer.borderColor=UIColor.grayColor().CGColor;
    cell.layer.masksToBounds = false

    cell.layer.shadowColor = UIColor.blackColor().CGColor
    cell.layer.shadowOffset = CGSizeMake(0, 5)
    cell.layer.shadowOpacity = 0.3
    cell.thumbsUpButton.setBackgroundImage(UIImage(named:"ic_thumb_up_selected"),
      forState: UIControlState.Selected)
    cell.thumbsDownButton.setBackgroundImage(UIImage(named:"ic_thumb_down_selected"),
      forState: UIControlState.Selected)

    cell.photoImageView.clipsToBounds = true

    cell.descriptionTextView.selectable = false

    // Hide or show buttons based on type of PhotoListType
    switch(photoListType) {
    case .Stream, .Likes:
      cell.editButton.hidden = true
      cell.deleteButton.hidden = true
    case .Mine:
      cell.thumbsDownButton.hidden = true
      cell.thumbsUpButton.hidden = true
    default:
      break;
    }

    // Add long press popup to report photo
    let longPressRecognizer = UILongPressGestureRecognizer(target: self, action: "longPressed:")
    cell.addGestureRecognizer(longPressRecognizer)
  }

  //
  // Returns the height necessary to display a text in a TextView of fixed width.
  // Useful to compute the cell height.
  //
  func getTextViewHeight(text: String, textViewWidth: CGFloat) -> CGFloat {
    var textView = UITextView()
    textView.frame.size.width   = textViewWidth
    textView.frame.size.height   = 50
    textView.text    = text
    return textView.sizeThatFits(CGSizeMake(textView.frame.size.width, CGFloat.max)).height;
  }

  //
  // Fetches more photos from the server if there is a next page available.
  //
  private func fetchMoreItems() {
    if !loading && abelanaClient.hasMorePages(photoListType) {
      self.loading = true
      abelanaClient.getPhotoList(photoListType, nextPage: true) {
        (error, message, photos) -> Void in
        let oldCount = self.photoList.count
        self.photoList = photos
        if error {
          self.showErrorAlert(message)
        }
        if oldCount != self.photoList.count {
          self.collectionView!.reloadData()
        }
        self.loading = false
      }
    }
  }
}

//
// Implements the UICollectionView layout delegate functions.
//
extension PhotoStreamViewController: UICollectionViewDelegateFlowLayout {

  //
  // Did the user request more items by scrolling when already at the bottom?
  //
  func scrollViewDidScroll(scrollView: UIScrollView) {
    if scrollView.contentOffset.y >=
        (scrollView.contentSize.height - scrollView.frame.size.height) {
          fetchMoreItems()
    }
  }

  func collectionView(collectionView: UICollectionView,
    layout collectionViewLayout: UICollectionViewLayout,
    sizeForItemAtIndexPath indexPath: NSIndexPath) -> CGSize {
      var cellWidth = self.view.frame.size.width-self.sectionInsets.left-self.sectionInsets.right
      var textViewHeight = CGFloat(0)
      if indexPath.item < self.photoList.count {
        textViewHeight = getTextViewHeight(self.photoList[indexPath.item].description,
          textViewWidth: cellWidth)
      }
      var cellHeight = 150+textViewHeight+100
      return CGSize(width: cellWidth, height: cellHeight)
  }

  func collectionView(collectionView: UICollectionView,
    layout collectionViewLayout: UICollectionViewLayout, insetForSectionAtIndex section: Int)
    -> UIEdgeInsets {
      return sectionInsets
  }
}

//
// Handles all the user actions on the photos.
//
extension PhotoStreamViewController {
  //
  // Called when the user hits the 'refresh' button.
  //
  @IBAction func refreshPhotoList(sender: AnyObject) {
    loading = true
    abelanaClient.getPhotoList(photoListType, nextPage: false) { (error, message, photos) -> Void in
      self.hideSpinner()
      self.photoList = photos
      if error {
        self.showErrorAlert(message)
      }
      self.collectionView!.reloadData()
      self.loading = false
    }
  }

  //
  // Called when the user long clicks on a photo.
  //
  func longPressed(longPress: UIGestureRecognizer) {
    showContextMenu(longPress.view!)
  }

  //
  // Called when the user long click on a photo, to show a context menu.
  //
  @IBAction func showContextMenu(sender: AnyObject) {
    let alertController = UIAlertController(title: localizedString("Options"),
      message: localizedString("Select an option"), preferredStyle: UIAlertControllerStyle.Alert)
    alertController.addAction(UIAlertAction(title: localizedString("Report"),
      style: UIAlertActionStyle.Default,
      handler: { (alert: UIAlertAction!) in
        self.reportPhoto(sender)
    }))
    alertController.addAction(UIAlertAction(title: localizedString("Dismiss"),
      style: UIAlertActionStyle.Cancel, handler: nil))
    var popPresenter = alertController.popoverPresentationController
    popPresenter?.sourceView = self.view
    popPresenter?.sourceRect = self.view.frame
    self.presentViewController(alertController, animated: true, completion: nil)
  }

  //
  // Called when the user clicks on the 'report' button from the context menu.
  //
  internal func reportPhoto(sender: AnyObject) {
    let alertController = UIAlertController(title: localizedString("Report photo"),
      message: localizedString("Are you sure this photo is inappropriate?"), preferredStyle:
      UIAlertControllerStyle.Alert)
    alertController.addAction(UIAlertAction(title: localizedString("Report"),
      style: UIAlertActionStyle.Destructive,
      handler: { (alert2: UIAlertAction!) in
        let indexPath = self.getIndexPath(sender)
        self.abelanaClient.flagPhoto(self.photoList[indexPath.item].photoId) {
          (error, message) -> Void in
          if !error {
            self.photoList.removeAtIndex(indexPath.item)
            self.collectionView!.reloadData()
          } else {
            self.showErrorAlert(message)
          }
        }
    }))
    alertController.addAction(UIAlertAction(title: localizedString("Dismiss"),
      style: UIAlertActionStyle.Cancel, handler: nil))
    self.presentViewController(alertController, animated: true, completion: nil)

  }

  //
  // Called when the users clicks on the 'tumbs up' button.
  //
  @IBAction func thumbsUpPhoto(sender: UIButton) {
    let indexPath = getIndexPath(sender)
    var vote = 1
    if photoList[indexPath.item].vote == 1 {
      vote = 0
    }
    abelanaClient.votePhoto(photoList[indexPath.item].photoId, vote: vote) {
      (error, message) -> Void in
      if !error {
        self.photoList[indexPath.item].vote = Int64(vote)
        self.collectionView!.reloadItemsAtIndexPaths([indexPath])
      } else {
        self.showErrorAlert(message)
      }
    }
  }

  //
  // Called when the users clicks on the 'tumbs down' button.
  //
  @IBAction func thumbsDownPhoto(sender: UIButton) {
    let indexPath = getIndexPath(sender)
    var vote = -1
    if photoList[indexPath.item].vote == -1 {
      vote = 0
    }
    abelanaClient.votePhoto(photoList[indexPath.item].photoId, vote: vote) {
      (error, message) -> Void in
      if !error {
        self.photoList[indexPath.item].vote = Int64(vote)
        self.collectionView!.reloadItemsAtIndexPaths([indexPath])
      } else {
        self.showErrorAlert(message)
      }
    }
  }

  //
  // Called when the users clicks on the 'edit' button.
  //
  @IBAction func editPhoto(sender: UIButton) {
    let indexPath = self.getIndexPath(sender)

    let alertController = UIAlertController(title: localizedString("Edit photo description"),
      message: localizedString("Update photo description?"),
      preferredStyle: UIAlertControllerStyle.Alert)
    alertController.addAction(UIAlertAction(title: localizedString("Dismiss"),
      style: UIAlertActionStyle.Cancel, handler: nil))
    alertController.addAction(UIAlertAction(title: localizedString("Update"),
      style: UIAlertActionStyle.Default,
      handler: { (alert: UIAlertAction!) in
        let textField = alertController.textFields?.first as? UITextField
        let newDescription = textField!.text
        if !newDescription.isEmpty && newDescription != "" {
          self.abelanaClient.editPhoto(self.photoList[indexPath.item].photoId,
            description: newDescription) {
              (error, message) -> Void in
              if !error {
                self.photoList[indexPath.item].description = newDescription
                self.collectionView!.reloadItemsAtIndexPaths([indexPath])
              } else {
                self.showErrorAlert(message)
              }
          }
        } else {
          let message =  self.localizedString("Please enter a description for the image.")
          self.showErrorAlert(message)
        }
    }))
    alertController.addTextFieldWithConfigurationHandler({(txtField: UITextField!) in
      txtField.text = self.photoList[indexPath.item].description
    })

    self.presentViewController(alertController, animated: true, completion: nil)
  }

  //
  // Called when the users clicks on the 'delete' button.
  //
  @IBAction func deletePhoto(sender: UIButton) {
    let alertController = UIAlertController(title: localizedString("Delete photo"),
      message: localizedString("Do you want to delete this photo?"),
      preferredStyle: UIAlertControllerStyle.Alert)
    alertController.addAction(UIAlertAction(title: localizedString("Delete"),
      style: UIAlertActionStyle.Destructive,
      handler: { (alert: UIAlertAction!) in
        let indexPath = self.getIndexPath(sender)
        self.abelanaClient.deletePhoto(self.photoList[indexPath.item].photoId) {
          (error, message) -> Void in
          if !error {
            self.photoList.removeAtIndex(indexPath.item)
            self.collectionView!.reloadData()
          } else {
            self.showErrorAlert(message)
          }
        }
    }))
    alertController.addAction(UIAlertAction(title: localizedString("Dismiss"),
      style: UIAlertActionStyle.Cancel, handler: nil))

    self.presentViewController(alertController, animated: true, completion: nil)
  }
}

//
// The different types of photo stream that the application can display.
//
internal enum PhotoListType {
  case Stream
  case Mine
  case Likes
}
