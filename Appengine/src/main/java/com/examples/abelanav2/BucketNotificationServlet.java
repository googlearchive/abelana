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
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.stdimpl.GCacheFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.gson.Gson;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class receives notifications from the GCS bucket, the Task Queue and the Image Resizer.
 */
public class BucketNotificationServlet extends HttpServlet {

  @Override
  public final void doPost(final HttpServletRequest req, final HttpServletResponse resp)
      throws IOException {
    // Decode the name and bucket of the notification
    BucketNotification notification;
    try {
      String jsonString = IOUtils.toString(req.getInputStream());
      notification =  new Gson().fromJson(jsonString, BucketNotification.class);
    } catch (IOException e) {
      log("Failed to decode the notification: " + e.getMessage());
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    try {
      if (!req.getHeader("X-Goog-Channel-Token").equals(ConfigurationConstants
          .SECRET_NOTIF_TOKEN)) {
        resp.setStatus(HttpStatusCodes.STATUS_CODE_FORBIDDEN);
        return;
      }

      if (!req.getHeader("X-Goog-Resource-State").equals("exists")) {
        resp.getWriter().write("This is not a new photo addition.");
        resp.setStatus(HttpStatusCodes.STATUS_CODE_OK);
        return;
      }

      // Handle duplicated notifications, drop keys after 5 minutes
      Boolean inCache = false;
      Cache cache = null;
      try {
        CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
        Map<Object, Object> properties = new HashMap<>();
        properties.put(GCacheFactory.EXPIRATION_DELTA, TimeUnit.MINUTES.toSeconds(5));
        properties.put(MemcacheService.SetPolicy.ADD_ONLY_IF_NOT_PRESENT, true);
        cache = cacheFactory.createCache(properties);
        inCache = (Boolean) cache.get(notification.getName());
      } catch (CacheException e) {
        log("Failed to instantiate the Memcache, risk of duplicate notifications: "
            + e.getMessage());
      }

      if (inCache == null || !inCache) {
        // Add a new task to the queue
        Queue queue = QueueFactory.getDefaultQueue();
        queue.add(TaskOptions.Builder.withUrl("/notice/incoming-image")
                .param("bucket", notification.getBucket())
                .param("name", notification.getName())
        );
        if (cache != null) {
          cache.put(notification.getName(), true);
        }
        resp.getWriter().write("Task added to the queue");
        log("Task created for bucket " + notification.getBucket() + " and file "
            + notification.getName());
      } else {
        resp.getWriter().write("This is a duplicate notification");
        log("Duplicate notification for bucket " + notification.getBucket() + " and file "
            + notification.getName());
      }
      resp.setStatus(HttpStatusCodes.STATUS_CODE_OK);

    } catch (IOException e) {
      log("Error while writing the response");
      resp.setStatus(HttpStatusCodes.STATUS_CODE_SERVER_ERROR);
    }
  }
}
