/*
 * Copyright 2015 Google, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.examples.abelanav2;

/**
 * Notification from a GCS bucket, used by Gson to deserialize the notification received.
 */
public class BucketNotification {
  /**
   * The name of the object changed.
   */
  public String name = "";
  /**
   * The name of the bucket.
   */
  public String bucket = "";

  /**
   * Returns the name of the object.
   * @return the name of the object.
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the bucket name.
   * @return the bucket.
   */
  public String getBucket() {
    return bucket;
  }

  @Override
  public String toString() {
    return "BucketNotification{name='" + name + "', bucket='" + bucket + '\'' + '}';
  }

  /**
   * No-arg constructor for Gson.
   */
  BucketNotification() {}
}
