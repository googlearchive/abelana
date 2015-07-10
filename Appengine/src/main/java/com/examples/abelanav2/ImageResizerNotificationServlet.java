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

import com.google.api.client.http.HttpStatusCodes;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.oauth.OAuthService;
import com.google.appengine.api.oauth.OAuthServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.users.User;

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class receives notifications from the GCS bucket, the Task Queue and the Image Resizer.
 */
public class ImageResizerNotificationServlet extends HttpServlet {

  @Override
  public final void doPost(final HttpServletRequest req, final HttpServletResponse resp)
      throws IOException {
    OAuthService oauth = OAuthServiceFactory.getOAuthService();
    String scope = "https://www.googleapis.com/auth/userinfo.email";
    User user;
    try {
      user = oauth.getCurrentUser(scope);
    } catch (OAuthRequestException e) {
      log("Impossible to authenticate the request from the image resizer, oauth request exception: "
          + e.getMessage());
      resp.setStatus(HttpStatusCodes.STATUS_CODE_UNAUTHORIZED);
      return;
    }
    // Let's get the project ID number from the service account email used to authenticate the
    // notification and check it.
    log("Received notification with user: " + user.getEmail());
    if (!user.getEmail().split("-")[0].equals(ConfigurationConstants.PROJECT_ID_NUMBER)) {
      log("Impossible to authenticate the request from the image resizer, email invalid: "
          + user.getEmail());
      resp.setStatus(HttpStatusCodes.STATUS_CODE_UNAUTHORIZED);
      return;
    }

    String[] params = req.getRequestURI().replace("/photopush/", "").split("_");
    long photoId = Long.valueOf(params[0]);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Key photoKey = KeyFactory.createKey("Photo", photoId);
    try {
      Entity photo = datastore.get(photoKey);
      photo.setProperty("available", true);
      datastore.put(photo);

      String taskId = params[1];
      if (!taskId.isEmpty()) {
        Queue queue = QueueFactory.getDefaultQueue();
        queue.deleteTask(taskId);
      }

      resp.setStatus(HttpStatusCodes.STATUS_CODE_OK);
    } catch (EntityNotFoundException e) {
      log("Photo entity=" + photoId + " not found when updating its availability: "
          + e.getMessage());
      resp.setStatus(HttpStatusCodes.STATUS_CODE_SERVER_ERROR);
    }
  }
}
