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

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {

  var window: UIWindow?

  func application(application: UIApplication,
    didFinishLaunchingWithOptions launchOptions: [NSObject: AnyObject]?) -> Bool {
      // Override point for customization after application launch.
      let abelanaConfig = NSDictionary(contentsOfFile: NSBundle.mainBundle()
        .pathForResource("AbelanaConfig", ofType: "plist")!)

      var gitkitClient = GITClient.sharedInstance()
      gitkitClient.apiKey = abelanaConfig?.valueForKey("gitkitClient.ApiKey") as? String
      gitkitClient.widgetURL = abelanaConfig?.valueForKey("gitkitClient.WidgetURL") as? String
      gitkitClient.providers = [kGITProviderGoogle, kGITProviderFacebook]
      GPPSignIn.sharedInstance().clientID =
        abelanaConfig?.valueForKey("gitkitClient.ClientID") as? String
      return true
  }

  func application(application: UIApplication, openURL url: NSURL, sourceApplication: String?,
    annotation: AnyObject?) -> Bool {
      // Handle custom scheme redirect here.
      return GITClient.handleOpenURL(url, sourceApplication : sourceApplication,
        annotation: annotation)
  }
 }
