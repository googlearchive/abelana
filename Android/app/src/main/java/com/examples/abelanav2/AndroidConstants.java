/*
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.examples.abelanav2;

import com.google.identitytoolkit.IdProvider;

/**
 * All the constants, IDs and credentials used by the application.
 */
public final class AndroidConstants {

    /**
     * Constructor.
     */
    private AndroidConstants() { }

    /**
     *  The port on which the server runs.
     */
    public static final int PORT = BuildConfig.PORT;

    /**
     *  The host on which the server runs.
     *  10.0.2.2 is the IP of the computer hosting the emulator.
     */
    public static final String HOST = BuildConfig.HOST;

    /**
     * Gitkit API key.
     */
    public static final String API_KEY = BuildConfig.API_KEY;

    /**
     * Gitkit server client ID.
     */
    public static final String SERVER_CLIENT_ID = BuildConfig.SERVER_CLIENT_ID;

    /**
     * Gitkit server widget URL.
     */
    public static final String SERVER_WIDGET_URL =
            BuildConfig.SERVER_WIDGET_URL;

    /**
     * Gitkit providers.
     */
    public static final IdProvider[] PROVIDERS = {IdProvider.GOOGLE,
            IdProvider.FACEBOOK};

    /**
     * Gitkit use Google+.
     */
    public static final boolean USE_GOOGLE_PLUS = true;

    /**
     * SharedPreferences name for user id token.
     */
    public static final String SHARED_PREFS_USER_ID =
            "AbelanaUserIdSharedPrefs";

    /**
     * SharedPreferences name for caching photo information.
     */
    public static final String SHARED_PREFS_PHOTOS =
            "AbelanaPhotosSharedPrefs";

    /**
     * SharedPreferences key name for user id token.
     */
    public static final String SHARED_PREFS_KEY_USER_ID =
            "user_id_token";
}
