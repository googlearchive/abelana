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

package com.examples.abelanav2.storage;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.EmptyContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpStatusCodes;
import com.google.api.client.http.HttpTransport;

import com.examples.abelanav2.BackendConstants;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.logging.Logger;

/**
 * Utility class to interact with Google Cloud Storage.
 */
public class CloudStorage {

  /**
   * Logger.
   */
  private static final Logger LOGGER = Logger.getLogger(CloudStorage.class.getName());

  /**
   * The Google Cloud Storage scope.
   */
  private static final String STORAGE_SCOPE =
      "https://www.googleapis.com/auth/devstorage.full_control";

  /**
   * Constructor.
   */
  private CloudStorage() {
  }

  /**
   * Returns a Google Cloud Storage upload url.
   *
   * @param name the filename
   * @return a URL
   */
  public static String getUploadUrl(final String name) {

    try {
      GoogleCredential googleCredentials = GoogleCredential.getApplicationDefault()
          .createScoped(Collections.singleton(STORAGE_SCOPE));

      String uri = "https://www.googleapis.com/upload/storage/v1/b/"
          + URLEncoder.encode(BackendConstants.UPLOAD_BUCKET_NAME, "UTF-8")
          + "/o?uploadType=resumable&name="
          + URLEncoder.encode(name, "UTF-8");

      HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
      HttpRequestFactory requestFactory = httpTransport.createRequestFactory(googleCredentials);
      GenericUrl url = new GenericUrl(uri);
      HttpRequest request = requestFactory.buildPostRequest(url, new EmptyContent());
      try {
        HttpResponse response = request.execute();

        if (response.getStatusCode() == HttpStatusCodes.STATUS_CODE_OK) {
          return response.getHeaders().getLocation();
        } else {
          LOGGER.severe("Could not get upload URL, HTTP code = " + response.getStatusCode());
        }
      } catch (IOException e) {
        LOGGER.severe("API request to Google Cloud Storage failed: " + e.getMessage());
      }

    } catch (IOException e) {
      LOGGER.severe("Could not get credentials: " + e.getMessage());
    } catch (GeneralSecurityException e) {
      LOGGER.severe("Could not start the transport layer for upload: " + e.getMessage());
    }
    return null;
  }
}
