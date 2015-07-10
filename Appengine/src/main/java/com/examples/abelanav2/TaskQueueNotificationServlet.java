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

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpStatusCodes;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.UrlEncodedContent;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class receives notifications from the GCS bucket, the Task Queue and the Image Resizer.
 */
public class TaskQueueNotificationServlet extends HttpServlet {

  @Override
  public final void doPost(final HttpServletRequest req, final HttpServletResponse resp)
      throws IOException {
    HttpTransport httpTransport;
    try {
      Map<Object, Object> params = new HashMap<>();
      params.putAll(req.getParameterMap());
      params.put("task", req.getHeader("X-AppEngine-TaskName"));

      httpTransport = GoogleNetHttpTransport.newTrustedTransport();
      GoogleCredential credential = GoogleCredential.getApplicationDefault()
          .createScoped(Collections.singleton("https://www.googleapis.com/auth/userinfo.email"));
      HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
      GenericUrl url = new GenericUrl(ConfigurationConstants.IMAGE_RESIZER_URL);

      HttpRequest request = requestFactory.buildPostRequest(url, new UrlEncodedContent(params));
      credential.initialize(request);

      HttpResponse response = request.execute();
      if (!response.isSuccessStatusCode()) {
        log("Call to the imageresizer failed: " + response.getContent().toString());
        resp.setStatus(HttpStatusCodes.STATUS_CODE_SERVER_ERROR);
      } else {
        resp.setStatus(response.getStatusCode());
      }

    } catch (GeneralSecurityException | IOException e) {
      log("Http request error: " + e.getMessage());
      resp.setStatus(HttpStatusCodes.STATUS_CODE_SERVER_ERROR);
    }
  }
}
