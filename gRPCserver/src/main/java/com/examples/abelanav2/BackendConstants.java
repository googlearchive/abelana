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
 * All the constants, IDs and credentials used by the application.
 */
public final class BackendConstants {

  /**
   * Constructor.
   */
  private  BackendConstants() { }

  /**
   *  The port on which the server should run.
   */
  public static final int PORT = 50051;
  /**
   * The audience for the JWT tokens.
   */
  public static final String TOKEN_ISSUER = "abelanav2.examples.com";
  /**
   * The key used to sign the JWT tokens.
   */
  public static final String SIGNING_KEY = "Your Secret MasterKey";
  /**
   * The expiration duration of the JWT tokens (in milliseconds).
   */
  public static final long JWT_EXPIRATION_DURATION = 6000000000L;
  /**
   * Google Client ID (oauth2 web client ID at Google).
   */
  public static final String GOOGLE_CLIENT_ID = "Your-client-id";

  /**
   * Service Account Email (in Google Developer Console).
   */
  public static final String GOOGLE_SERVICE_ACCOUNT = "Your-service-account";

  /**
   * Path to the service account private key file (should be the name of
   * the file if in the root of src/main/resources).
   */
  public static final String GOOGLE_SERVICE_ACCOUNT_KEY_FILEPATH =
      "Path-to-the-p12-file-in-/resources/";
  /**
   * Your project ID (in Google Developer Console).
   */
  public static final String PROJECT_ID = "Your-project-id";
  /**
   * Photos per page in a datastore query.
   */
  public static final int PHOTOS_PER_PAGE = 50;
  /**
   * Confidence interval for photos ranking.
   */
  public static final double CONFIDENCE_INTERVAL = 0.95;
  /**
   * Bucket name to upload the new photos in.
   * e.g abelanav2-in
   */
  public static final String UPLOAD_BUCKET_NAME = "Your-photos-upload-bucket-name";
  /**
   * Bucket name for the resized photos (the public bucket containing the resized photos).
   * e.g abelanav2
   */
  public static final String PUBLIC_BUCKET_NAME = "Your-photos-public-bucket-name";
  /**
   * Google Cloud Storage bucket URL to download the images once processed by the image resizer.
   */
  public static final String IMAGES_BASE_URL =
      "https://storage.googleapis.com/" + PUBLIC_BUCKET_NAME + "/";
}
