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
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.examples.abelanav2.AndroidConstants;
import com.examples.abelanav2.grpc.AbelanaGrpc;
import com.examples.abelanav2.grpc.DeletePhotoRequest;
import com.examples.abelanav2.grpc.EditPhotoRequest;
import com.examples.abelanav2.grpc.FlagRequest;
import com.examples.abelanav2.grpc.NewPhotoRequest;
import com.examples.abelanav2.grpc.Photo;
import com.examples.abelanav2.grpc.PhotoListRequest;
import com.examples.abelanav2.grpc.PhotoListResponse;
import com.examples.abelanav2.grpc.SignInRequest;
import com.examples.abelanav2.grpc.SignInResponse;
import com.examples.abelanav2.grpc.StatusResponse;
import com.examples.abelanav2.R;
import com.examples.abelanav2.grpc.UploadPhotoResponse;
import com.examples.abelanav2.grpc.VoteRequest;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.grpc.Channel;
import io.grpc.ChannelImpl;
import io.grpc.ClientInterceptors;
import io.grpc.transport.okhttp.OkHttpChannelBuilder;

/**
 * The GRPC client to connect to the server.
 */
public class AbelanaClient {
    /**
     * The actual OkHttp channel implementation.
     */
    private ChannelImpl mChannelImpl;
    /**
     * The gRPC stub used to make calls to the server.
     */
    private AbelanaGrpc.AbelanaBlockingStub mBlockingStub;
    /**
     * The header client interceptor, to inject the Auth header.
     */
    private  AuthHeaderClientInterceptor mInterceptor;
    /**
     * The application context.
     */
    private  Context mContext;
    /**
     * Stores whether there is an active connection to the server or not.
     */
    private boolean mConnected;
    /**
     * Stores the photo lists and next pages.
     */
    private CacheStore mCacheStore;

    /**
     * The type of photo list to store.
     */
    public enum PhotoListType {
        /**
         * The photo list for the personalized stream.
         */
        PHOTO_LIST_STREAM,
        /**
         * The photo list for the my likes stream.
         */
        PHOTO_LIST_LIKES,
        /**
         * The photo list for the my photos stream.
         */
        PHOTO_LIST_MINE
    }

    /**
     * The Abelana GRPC client constructor.
     * @param context the application context.
     */
    public AbelanaClient(final Context context) {
        mContext = context;
        mCacheStore = new CacheStore(context);
        initServerConnection();
    }

    /**
     * Initializes a connection to the gRPC server.
     * @return a boolean indicating the success.
     */
    private boolean initServerConnection() {
        if(!mConnected) {
            mInterceptor = new AuthHeaderClientInterceptor(
                    getUserIdToken());
            try {
                mChannelImpl = OkHttpChannelBuilder
                        .forAddress(AndroidConstants.HOST,
                                AndroidConstants.PORT)
                        .build();
                Channel mOriginChannel = ClientInterceptors
                        .intercept(mChannelImpl, mInterceptor);
                mBlockingStub = AbelanaGrpc.newBlockingStub(mOriginChannel);
                mConnected = true;
            } catch (RuntimeException e) {
                mConnected = false;
            }
        }
        return mConnected;
    }

    /**
     * Checks if the client is connected to the server. If not, tries to connect
     * and returns the result.
     * @return a boolean indicating if the client is connected to the server.
     */
    private boolean isConnectedToServerOrTryToConnect() {
        return mConnected || initServerConnection();
    }

    /**
     * Stops the communication between the client and the server.
     */
    public void shutdown() {
        mChannelImpl.shutdown();
        mCacheStore.backup();
        mConnected = false;
    }

    /**
     * Signs the user in using a GitkitToken.
     * @param gitkitToken the gitkitToken string.
     * @return a boolean indicating that the user is successfully signed in.
     * @throws AbelanaClientException if cannot connect to server or
     * authenticate.
     */
    public final boolean signIn(final String gitkitToken)
            throws AbelanaClientException {
        if (isConnectedToServerOrTryToConnect()) {
            // Send request to the server
            SignInRequest signInRequest = new SignInRequest();
            signInRequest.gitkitToken = gitkitToken;
            try {
                SignInResponse signInResponse = mBlockingStub
                        .signIn(signInRequest);
                // Save the user id token received
                if (signInResponse.error == null) {
                    setUserIdToken(signInResponse.userToken);
                    return true;
                } else {
                    throw new AbelanaClientException(mContext.getString(R.string
                            .server_authentication_error));
                }
            }  catch (RuntimeException e) {
                throw new AbelanaClientException(mContext.getString(R.string
                        .server_connection_error));
            }

        } else {
            throw new AbelanaClientException(mContext.getString(R.string
                    .server_connection_error));
        }
    }

    /**
     * Flags a photo as inappropriate.
     * @param photoId the ID of the photo to flag.
     * @return a boolean indicating that the photo was successfully flagged.
     * @throws AbelanaClientException if cannot connect to server.
     * @throws AbelanaClientException if cannot authenticate to server.
     */
    public final boolean flagPhoto(final long photoId)
            throws AbelanaClientException, AbelanaClientException {
        if (isConnectedToServerOrTryToConnect()) {
            FlagRequest flagRequest = new FlagRequest();
            flagRequest.photoId = photoId;
            try {
                StatusResponse statusResponse = mBlockingStub
                        .flagPhoto(flagRequest);
                if (statusResponse.error != null
                        && statusResponse.error.code.equals("403")) {
                    throw new AbelanaClientException(mContext
                            .getString(R.string.abelana_auth_error_message)
                    );
                }
                return statusResponse.error == null;
            } catch (RuntimeException e) {
                Log.e("AbelanaClient", e.getMessage());
                throw new AbelanaClientException(mContext.getString(R.string
                        .server_connection_error));
            }
        } else {
            throw new AbelanaClientException(mContext.getString(R.string
                    .server_connection_error));
        }
    }

    /**
     * Deletes a photo.
     * @param photoId the ID of the photo to delete.
     * @return a boolean indicating that the photo was successfully deleted.
     * @throws AbelanaClientException if cannot connect to server.
     * @throws AbelanaClientException if cannot authenticate to server.
     */
    public final boolean deletePhoto(final long photoId)
            throws AbelanaClientException, AbelanaClientException {
        if (isConnectedToServerOrTryToConnect()) {
            DeletePhotoRequest deletePhotoRequest = new DeletePhotoRequest();
            deletePhotoRequest.photoId = photoId;
            try {
                StatusResponse statusResponse = mBlockingStub
                        .deletePhoto(deletePhotoRequest);
                if (statusResponse.error != null
                        && statusResponse.error.code.equals("403")) {
                    throw new AbelanaClientException(mContext
                            .getString(R.string.abelana_auth_error_message)
                    );
                }
                return statusResponse.error == null;
            } catch (RuntimeException e) {
                Log.e("AbelanaClient", e.getMessage());
                throw new AbelanaClientException(mContext.getString(R.string
                        .server_connection_error));
            }
        } else {
            throw new AbelanaClientException(mContext.getString(R.string
                    .server_connection_error));
        }
    }

    /**
     * Edits a photo.
     * @param photoId the ID of the photo to edit.
     * @param description the new description of the photo.
     * @return a boolean indicating that the photo was successfully edited.
     * @throws AbelanaClientException if cannot connect to server.
     * @throws AbelanaClientException if cannot authenticate to server.
     */
    public final boolean editPhoto(final long photoId, String description)
            throws AbelanaClientException, AbelanaClientException {
        if (isConnectedToServerOrTryToConnect()) {
            EditPhotoRequest editPhotoRequest = new EditPhotoRequest();
            editPhotoRequest.photoId = photoId;
            editPhotoRequest.description = description;
            try {
                StatusResponse statusResponse = mBlockingStub
                        .editPhoto(editPhotoRequest);
                if (statusResponse.error != null
                        && statusResponse.error.code.equals("403")) {
                    throw new AbelanaClientException(mContext
                            .getString(R.string.abelana_auth_error_message)
                    );
                }
                return statusResponse.error == null;
            } catch (RuntimeException e) {
                Log.e("AbelanaClient", e.getMessage());
                throw new AbelanaClientException(mContext.getString(R.string
                        .server_connection_error));
            }
        } else {
            throw new AbelanaClientException(mContext.getString(R.string
                    .server_connection_error));
        }
    }

    /**
     * Rates a photo.
     * @param photoId the ID of the photo to rate.
     * @param vote the vote for the photo: -1, 0 or 1.
     * @return a boolean indicating that the photo was successfully rated.
     * @throws AbelanaClientException if cannot connect to server.
     * @throws AbelanaClientException if cannot authenticate to server.
     */
    public final boolean votePhoto(final long photoId, final int vote)
            throws AbelanaClientException, AbelanaClientException {
        if (isConnectedToServerOrTryToConnect()) {
            VoteRequest voteRequest = new VoteRequest();
            voteRequest.photoId = photoId;
            switch (vote) {
                case -1:
                    voteRequest.vote = VoteRequest.THUMBS_DOWN;
                    break;
                case 1:
                    voteRequest.vote = VoteRequest.THUMBS_UP;
                    break;
                case 0:
                default:
                    voteRequest.vote = VoteRequest.NEUTRAL;
                    break;
            }
            try {
                StatusResponse statusResponse = mBlockingStub
                        .ratePhoto(voteRequest);
                if (statusResponse.error != null
                        && statusResponse.error.code.equals("403")) {
                    throw new AbelanaClientException(mContext
                            .getString(R.string.abelana_auth_error_message)
                    );
                }
                return statusResponse.error == null;
            } catch (RuntimeException e) {
                Log.e("AbelanaClient", e.getMessage());
                throw new AbelanaClientException(mContext.getString(R.string
                        .server_connection_error));
            }
        } else {
            throw new AbelanaClientException(mContext.getString(R.string
                    .server_connection_error));
        }
    }

    /**
     * Uploads a new photo.
     * @param description the description of the photo.
     * @param image the image bitmap
     * @return a boolean indicating that the photo was successfully uploaded.
     * @throws AbelanaClientException if cannot connect to server.
     * @throws AbelanaClientException if cannot authenticate to server.
     */
    public final boolean uploadPhoto(final String description,
                                     final Bitmap image)
            throws AbelanaClientException, AbelanaClientException {
        if (isConnectedToServerOrTryToConnect()) {
            NewPhotoRequest newPhotoRequest = new NewPhotoRequest();
            newPhotoRequest.description = description;
            try {
                UploadPhotoResponse uploadPhotoResponse = mBlockingStub
                        .uploadPhoto(newPhotoRequest);
                if (uploadPhotoResponse.error == null) {
                    CloudStorage.uploadImage(uploadPhotoResponse.uploadUrl,
                            image);
                    return true;
                } else {
                    Log.e("AbelanaClient", uploadPhotoResponse.error.details);
                    if (uploadPhotoResponse.error.code.equals("403")) {
                        throw new AbelanaClientException(mContext
                                .getString(R.string.abelana_auth_error_message)
                        );
                    }
                    return false;
                }
            } catch (IOException e) {
                Log.e("AbelanaClient", e.getMessage());
                throw new AbelanaClientException(mContext
                        .getString(R.string.upload_photo_failed));
            }
        } else {
            throw new AbelanaClientException(mContext.getString(R.string
                    .server_connection_error));
        }
    }

    /**
     * Gets the photo list.
     * @param photoListType the kind of photo list.
     * @param nextPage if we want the next page. Gets new results if false.
     * @return The updated list of photos.
     * @throws AbelanaClientException if cannot connect to server.
     * @throws AbelanaClientException if cannot authenticate to server.
     */
    public final List<PhotoInfo> getPhotoList(final PhotoListType photoListType,
                                              final boolean nextPage)
            throws AbelanaClientException, AbelanaClientException {
        if (isConnectedToServerOrTryToConnect()) {
            PhotoListRequest photoListRequest = new PhotoListRequest();
            if (nextPage) {
                photoListRequest.pageNumber = mCacheStore
                        .getNextPage(photoListType);
            } else {
                photoListRequest.pageNumber = 0;
            }
            try {
                PhotoListResponse photoListResponse;
                switch (photoListType) {
                    case PHOTO_LIST_LIKES:
                        photoListResponse = mBlockingStub
                                .listMyLikes(photoListRequest);
                        break;
                    case PHOTO_LIST_MINE:
                        photoListResponse = mBlockingStub
                                .listMyPhotos(photoListRequest);
                        break;
                    case PHOTO_LIST_STREAM:
                    default:
                        photoListResponse = mBlockingStub
                            .photoStream(photoListRequest);
                        break;
                }
                if (photoListResponse.error == null) {
                    mCacheStore.setNextPage(photoListType,
                            photoListResponse.nextPage);
                    Log.i("AbelanaClient", "Next page=" + photoListResponse
                            .nextPage);
                    if (!nextPage) {
                        mCacheStore.getPhotoList(photoListType).clear();
                    }
                    for(Photo p : photoListResponse.photo) {
                        mCacheStore.addPhotoToList(photoListType,
                                new PhotoInfo(p.url, p.photoId, p.userId,
                                        p.date, p.description, p.rating));
                    }
                    return mCacheStore.getPhotoList(photoListType);
                } else {
                    Log.e("AbelanaClient", photoListResponse.error.details);
                    if (photoListResponse.error.code.equals("403")) {
                        throw new AbelanaClientException(mContext
                                .getString(R.string.abelana_auth_error_message)
                        );
                    }
                    throw new AbelanaClientException(mContext.getString(R.string
                            .server_error));
                }
            } catch (RuntimeException e) {
                Log.e("AbelanaClient", e.getMessage());
                throw new AbelanaClientException(mContext.getString(R.string
                        .server_connection_error));
            }
        } else {
            throw new AbelanaClientException(mContext.getString(R.string
                    .server_connection_error));
        }
    }

    /**
     * Returns a boolean indicating if there are more pages to load.
     * @param photoListType the kind of photo list.
     * @return true if there are more pages
     */
    public final boolean hasMorePages(PhotoListType photoListType) {
        return !(mCacheStore.getNextPage(photoListType) == 0);
    }

    /**
     * Uploads a new photo.
     * @param photoListType the kind of photo list.
     * @return The updated list of photos.
     */
    public final List<PhotoInfo> getPhotoListFromCache(
            final PhotoListType photoListType) {
        return mCacheStore.getPhotoList(photoListType);
    }

    /**
     * @return a boolean indicating if the user is signed in or not.
     */
    public boolean isSignedIn() {
        return getUserIdToken() != null && !getUserIdToken().equals("");
    }

    /**
     * Signs the user out.
     * @throws AbelanaClientException if impossible to sign the user out.
     */
    public void signOut() throws AbelanaClientException {
        setUserIdToken(null);
    }



    /**
     * Returns the user ID token from SharedPreferences.
     * @return the user ID token.
     */
    private final String getUserIdToken() {
        SharedPreferences settings = mContext.getSharedPreferences(
                AndroidConstants.SHARED_PREFS_USER_ID, 0);
        return settings.getString(AndroidConstants.SHARED_PREFS_KEY_USER_ID,
                null);
    }

    /**
     * Sets the user ID token in SharedPreferences.
     * @param userIdToken the user id token to save.
     * @throws AbelanaClientException if cannot save authentication
     */
    private final void setUserIdToken(final String userIdToken)
            throws AbelanaClientException {
        SharedPreferences settings = mContext.getSharedPreferences(
                        AndroidConstants.SHARED_PREFS_USER_ID, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(AndroidConstants.SHARED_PREFS_KEY_USER_ID,
                userIdToken);
        if (!editor.commit()) {
            Log.e("AbelanaClient", "Could not save the user id token.");
            throw new AbelanaClientException(mContext.getString(R.string
                    .server_authentication_error));
        }
        mInterceptor.setUserIdToken(userIdToken);
    }
}
