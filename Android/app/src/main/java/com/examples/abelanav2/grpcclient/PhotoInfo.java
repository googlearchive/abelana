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

import com.examples.abelanav2.AndroidConstants;

/**
 * Contains the information for a picture to display.
 */
public class PhotoInfo {
    /**
     * The photo Id.
     */
    public long photoId;
    /**
     * The user Id.
     */
    public String userId;
    /**
     * The photo url.
     */
    public String url;
    /**
     * The date of the photo.
     */
    public long date;
    /**
     * The description of the photo.
     */
    public String description;
    /**
     * The user's vote on this photo.
     */
    public long vote;

    /**
     * Dummy constructor.
     */
    public PhotoInfo() {

    }

    /**
     * Constructor.
     * @param pUrl the photo
     * @param pPhotoId the photo ID
     * @param pUserId the user ID
     * @param pDate the photo date
     * @param pDescription the photo description
     * @param pVote the user's vote for this photo
     */
    public PhotoInfo(String pUrl, long pPhotoId, String pUserId, long pDate,
                     String pDescription, long pVote) {
        this.vote = pVote;
        this.photoId = pPhotoId;
        this.userId = pUserId;
        this.date = pDate;
        this.description = pDescription;
        this.url = pUrl;
    }
}
