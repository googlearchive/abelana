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
// This class extends the Base class and handles the GitKit sign in flow.
//
class GitkitViewController: MainViewController, GITClientDelegate, GITInterfaceManagerDelegate {
  // Gitkit interface manager
  var interfaceManager: GITInterfaceManager!

  override func viewDidLoad() {
    // Prevents loops as MainViewController checks for sign in state and tries to launch a
    // GitkitViewController, which would then also check for sign in state because of inheritance,
    // and so on.
    // Needs to be set before call to super.
    signInInProgress = true
    super.viewDidLoad()

    interfaceManager = GITInterfaceManager()
    interfaceManager.delegate = self
    GITClient.sharedInstance().delegate = self
  }

  //
  // Called when the user taps the 'sign in' button.
  //
  @IBAction func signInDidTap(sender: AnyObject) {
      interfaceManager.startSignIn()
  }

  //
  // Handles the response from the GitKit client, and retrieves an auth token fro, the gRPC server.
  //
  func client(client: GITClient!, didFinishSignInWithToken token: String!, account: GITAccount!,
    error: NSError!) {
      if error == nil {
        showSpinner(true)
        abelanaClient.signIn(token, completionHandler: {(error, message) -> Void in
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
        // Something went wring during the sign in.
        println(String(format: "error: %@", error));
        self.showErrorAlert(localizedString("Impossible to finish login"))
    }
  }
}
