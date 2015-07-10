/*
 * Copyright 2015 Google, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.examples.abelanav2;

/**
 * Contains the configuration for this application.
 */
public class ConfigurationConstants {
  /**
   * The secret token sent in the CGS bucket notification, to ensure that the notification is valid.
   * You defined it when using 'gsutil notification watchbucket'.
   */
  public static final String SECRET_NOTIF_TOKEN = "secretToken";
  /**
   * The image resizer URL. For the IP address, please use the load-balancer IP.
   * e.g http://123.123.123.123:8080 where 123.123.123.123 is the load-balancer
   */
  public static final String IMAGE_RESIZER_URL = "Your-image-resizer-URL";
  /**
   * Your project ID number from the developer console.
   * Go to the Gear icon -> Project information in the top-right hand corner.
   */
  public static final String PROJECT_ID_NUMBER = "Your-project-id-number";
}
