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

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.examples.abelanav2.AndroidConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Caches the Photo retrieved from the server.
 */
public class CacheStore {



    /**
     * The application context.
     */
    private Context mContext;

    /**
     * The Map containing all 3 photo lists.
     */
    private Map<AbelanaClient.PhotoListType, List<PhotoInfo>> mPhotoLists;
    /**
     * The Map containing all 3 photo lists.
     */
    private Map<AbelanaClient.PhotoListType, Long> mPhotoListsNextPage;

    /**
     * Constructor.
     * @param pContext the application context.
     */
    public CacheStore(Context pContext) {
        this.mContext = pContext;
        mPhotoLists = new HashMap<>();
        mPhotoListsNextPage = new HashMap<>();
        if (!restore()) {
            mPhotoLists.put(AbelanaClient.PhotoListType.PHOTO_LIST_LIKES,
                    new ArrayList<PhotoInfo>());
            mPhotoLists.put(AbelanaClient.PhotoListType.PHOTO_LIST_STREAM,
                    new ArrayList<PhotoInfo>());
            mPhotoLists.put(AbelanaClient.PhotoListType.PHOTO_LIST_MINE,
                    new ArrayList<PhotoInfo>());
            mPhotoListsNextPage.put(AbelanaClient.PhotoListType
                            .PHOTO_LIST_LIKES, Long.valueOf(0));
            mPhotoListsNextPage.put(AbelanaClient.PhotoListType
                            .PHOTO_LIST_STREAM, Long.valueOf(0));
            mPhotoListsNextPage.put(AbelanaClient.PhotoListType
                            .PHOTO_LIST_MINE, Long.valueOf(0));
        }
    }

    /**
     * Returns the photo list requested from the cache.
     * @param photoListType any of the 3 photo list types.
     * @return a photo list.
     */
    public List<PhotoInfo> getPhotoList(
            AbelanaClient.PhotoListType photoListType) {
        return mPhotoLists.get(photoListType);
    }

    /**
     * Adds a new photo to the photo list selected.
     * @param photoListType any of the 3 photo list types.
     * @param photoInfo the photo to add.
     */
    public void addPhotoToList(AbelanaClient.PhotoListType photoListType,
                               PhotoInfo photoInfo) {
        getPhotoList(photoListType).add(photoInfo);
    }

    /**
     * Returns the next page for photo list requested.
     * @param photoListType any of the 3 photo list types.
     * @return the next page indicator.
     */
    public long getNextPage(AbelanaClient.PhotoListType photoListType) {
        return mPhotoListsNextPage.get(photoListType);
    }

    /**
     * Updates the next page indicator for the photo list selected.
     * @param photoListType any of the 3 photo list types.
     * @param nextPage the next page indicator.
     */
    public void setNextPage(AbelanaClient.PhotoListType photoListType,
                            long nextPage) {
        mPhotoListsNextPage.put(photoListType, nextPage);
    }

    /**
     * Stores everything in the SharedPreferences for caching.
     */
    public void backup() {
        SharedPreferences settings = mContext.getSharedPreferences(
                AndroidConstants.SHARED_PREFS_PHOTOS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        ObjectMapper mapper = new ObjectMapper();
        try {
            editor.putString("photoList",
                    mapper.writeValueAsString(mPhotoLists));
            editor.putString("photoListNextPage",
                    mapper.writeValueAsString(mPhotoListsNextPage));
        } catch (JsonProcessingException e) {
            Log.e("PhotoAdapter", e.getMessage());
        }
        editor.apply();
    }

    /**
     * Restores everything from the SharedPreferences for caching.
     * @return a boolean indicating the success of restoring elements.
     */
    public boolean restore() {
        SharedPreferences settings = mContext.getSharedPreferences(
                AndroidConstants.SHARED_PREFS_PHOTOS, 0);
        boolean res = true;
        String photoList = settings.getString("photoList", null);
        String photoListNextPage = settings.getString("photoListNextPage",
                null);
        res = photoList != null && photoListNextPage != null;
        if (res) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                mPhotoLists = mapper.readValue(photoList, new
                        TypeReference<Map<AbelanaClient.PhotoListType,
                                List<PhotoInfo>>>() { });
                mPhotoListsNextPage = mapper.readValue(photoListNextPage, new
                        TypeReference<Map<AbelanaClient.PhotoListType,
                                Long>>() { });
            } catch (IOException e) {
                Log.e("PhotoAdapter", e.getMessage());
                return false;
            }
        }
        return res;
    }
}
