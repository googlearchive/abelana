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

package com.examples.abelanav2.grpcclient;

import android.graphics.Bitmap;

import com.google.api.client.http.InputStreamContent;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.internal.Util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * Class used to upload photos to Google Cloud Storage.
 */
public final class CloudStorage {

    /**
     * Constructor.
     */
    private CloudStorage() { }

    /**
     * Uploads an image to Google Cloud Storage.
     * @param url the upload url.
     * @param bitmap the image to upload.
     * @throws IOException if cannot upload the image.
     */
    public static void uploadImage(String url, Bitmap bitmap)
            throws IOException {

        ByteArrayOutputStream bOS = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bOS);
        byte[] bitmapData = bOS.toByteArray();

        InputStream stream = new ByteArrayInputStream(bitmapData);
            String contentType = URLConnection
                    .guessContentTypeFromStream(stream);
            InputStreamContent content = new InputStreamContent(contentType,
                    stream);

        MediaType MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg");

       OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = RequestBodyUtil.create(MEDIA_TYPE_JPEG,
                content.getInputStream());
        Request request = new Request.Builder()
                .url(url)
                .put(requestBody)
                .build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
    }

    public static class RequestBodyUtil {

        public static RequestBody create(final MediaType mediaType, final
        InputStream inputStream) {
            return new RequestBody() {
                @Override
                public MediaType contentType() {
                    return mediaType;
                }

                @Override
                public long contentLength() {
                    try {
                        return inputStream.available();
                    } catch (IOException e) {
                        return 0;
                    }
                }

                @Override
                public void writeTo(BufferedSink sink) throws IOException {
                    Source source = null;
                    try {
                        source = Okio.source(inputStream);
                        sink.writeAll(source);
                    } finally {
                        Util.closeQuietly(source);
                    }
                }
            };
        }
    }
}
