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

package com.examples.abelanav2.grpc;

/**
 * Class used to store the authenticated user ID in each thread, and to be
 * accessed by the server as a global variable when creating a response.
 */
public final class AuthToken {

  /**
   * The user ID.
   */
  private static final ThreadLocal<String> AUTH_TOKEN = new ThreadLocal<>();

  /**
   * Constructor.
   */
  private AuthToken() { }

  /**
   * Sets the user ID.
   * @param userId the authenticated user ID.
   */
  public static void set(final String userId) {
    AUTH_TOKEN.set(userId);
  }

  /**
   * Deletes the user ID.
   */
  public static void remove() {
    AUTH_TOKEN.remove();
  }

  /**
   * Returns the user ID.
   * @return the user ID.
   */
  public static String get() {
    return AUTH_TOKEN.get();
  }
}
