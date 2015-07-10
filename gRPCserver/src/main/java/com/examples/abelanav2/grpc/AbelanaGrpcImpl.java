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

package com.examples.abelanav2.grpc;

import static com.google.api.services.datastore.client.DatastoreHelper.getByteString;
import static com.google.api.services.datastore.client.DatastoreHelper.getPropertyMap;
import static com.google.api.services.datastore.client.DatastoreHelper.getString;
import static com.google.api.services.datastore.client.DatastoreHelper.getTimestamp;

import com.google.api.services.datastore.DatastoreV1.Entity;
import com.google.api.services.datastore.DatastoreV1.Value;
import com.google.api.services.datastore.client.DatastoreException;
import com.google.identitytoolkit.GitkitClientException;
import com.google.identitytoolkit.GitkitUser;
import com.google.protobuf.ByteString;

import com.examples.abelanav2.BackendConstants;
import com.examples.abelanav2.datastore.DbClient;
import com.examples.abelanav2.datastore.DbUtils;
import com.examples.abelanav2.storage.CloudStorage;

import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * The controller for the Abelana GRPC server implementation.
 */
public class AbelanaGrpcImpl implements AbelanaGrpc.Abelana {

  /**
   * Logger.
   */
  private static final Logger LOGGER = Logger.getLogger(AbelanaGrpcImpl.class.getName());

  /**
   * Datastore client.
   */
  private DbClient dbClient = new DbClient();

  /**
   * Setter used by Mockito to set a mock DbClient.
   * @param dbClient the new DbClient.
   */
  void setDbClient(DbClient dbClient) {
    this.dbClient = dbClient;
  }

  @Override
  public final void signIn(final SignInRequest request,
                           final StreamObserver<SignInResponse> responseObserver) {

    SignInResponse reply;
    try {
      GitkitUser gitkitUser = AuthUtils.verifyGitkitToken(request.getGitkitToken());
      reply = SignInResponse.newBuilder().setUserToken(AuthUtils.getJwt(gitkitUser.getLocalId()))
          .build();
    } catch (GitkitClientException | IOException | SignatureException | InvalidKeyException e) {
      LOGGER.warning("Authentication error with Gitkit Client " + e.getMessage());
      reply = SignInResponse.newBuilder().setError(getErrorMessage("500", e.getMessage())).build();
    }
    responseObserver.onValue(reply);
    responseObserver.onCompleted();
  }

  @Override
  public final void photoStream(final PhotoListRequest request,
                                final StreamObserver<PhotoListResponse> responseObserver) {
    listPhotos(request, responseObserver, DbClient.PhotoListType.PHOTO_LIST_STREAM);
  }

  @Override
  public final void listMyPhotos(final PhotoListRequest request,
                                 final StreamObserver<PhotoListResponse> responseObserver) {
    listPhotos(request, responseObserver, DbClient.PhotoListType.PHOTO_LIST_MINE);
  }

  @Override
  public final void listMyLikes(final PhotoListRequest request,
                                final StreamObserver<PhotoListResponse> responseObserver) {
    listPhotos(request, responseObserver, DbClient.PhotoListType.PHOTO_LIST_LIKES);
  }

  /**
   * Lists photos for listMyPhotos, listMyLikes.
   * @param request the gRPC request
   * @param responseObserver the gRPC response observer that will send the response to the client
   * @param listKind the kind of list we want to return from dbClient.PhotoListType
   */
  public final void listPhotos(final PhotoListRequest request,
                               final StreamObserver<PhotoListResponse> responseObserver,
                               final DbClient.PhotoListType listKind) {
    PhotoListResponse reply;
    if (AuthUtils.isSignedIn()) {
      long page = request.getPageNumber();
      DbClient.EntityListAndCursorResult photoListAndCursor;
      try {
        ByteString cursor = null;
        boolean pageError = false;
        if (page != 0) {
          Entity cursorEntity = dbClient.getAndDeleteCursor(page);
          if (cursorEntity != null) {
            cursor = getByteString(getPropertyMap(cursorEntity).get("cursor"));
          } else {
            pageError = true;
          }
        }
        photoListAndCursor = dbClient.getPhotoList(AuthUtils.getUserId(), listKind, cursor);

        PhotoListResponse.Builder builder = PhotoListResponse.newBuilder();
        if (pageError) {
          // Invalid cursor, send an error but also return results
          builder.setError(getErrorMessage("400-200",
              "Invalid page, returning results from scratch"));
        }
        for (Entity result : photoListAndCursor.getEntityList()) {
          Map<String, Value> props = getPropertyMap(result);
          long rating = 1;
          if (listKind == DbClient.PhotoListType.PHOTO_LIST_STREAM) {
            rating = dbClient.getVoteValueForPhoto(DbUtils.getEntityId(result),
                getString(props.get("userId")));
          }
          Photo photo = Photo.newBuilder()
              .setPhotoId(DbUtils.getEntityId(result))
              .setUserId(getString(props.get("userId")))
              .setDate(getTimestamp(props.get("date")))
              .setDescription(getString(props.get("description")))
              .setRating(rating)
              .setUrl(BackendConstants.IMAGES_BASE_URL + DbUtils.getEntityId(result) + "_"
                  + getString(props.get("userId")) + ".webp")
              .build();

          builder.addPhoto(photo);
        }
        if (photoListAndCursor.getCursor() != null
            && photoListAndCursor.getEntityList().size() > 0) {
          long cursorId = dbClient.insertCursor(photoListAndCursor.getCursor(),
              AuthUtils.getUserId());
          builder.setNextPage(cursorId);
        }
        // Everything went well
        reply = builder.build();

      } catch (DatastoreException e) {
        // Internal error, impossible to list the photos
        reply = PhotoListResponse.newBuilder().setError(getDbErrorMessage()).build();
      }
    } else {
      reply = PhotoListResponse.newBuilder().setError(getAuthErrorMessage()).build();
    }
    responseObserver.onValue(reply);
    responseObserver.onCompleted();
  }

  @Override
  public final void flagPhoto(final FlagRequest request,
                              final StreamObserver<StatusResponse> responseObserver) {
    StatusResponse reply;
    if (AuthUtils.isSignedIn()) {
      long photoId = request.getPhotoId();
      try {
        Entity photo = dbClient.getPhoto(photoId);
        if (photo == null) {
          throw new DatastoreException("flagPhoto", 404, "Photo not found in Database", null);
        }
        List<Entity> photoFlags = dbClient.getPhotoFlags(photo.getKey());

        boolean hasAlreadyFlagged = false;
        for (Entity flag : photoFlags) {
          Map<String, Value> propsFlag = getPropertyMap(flag);
          String userIdFlag = propsFlag.get("userId").getStringValue();
          if (userIdFlag != null && userIdFlag.equals(AuthUtils.getUserId())) {
            hasAlreadyFlagged = true;
          }
        }
        if (!hasAlreadyFlagged) {
          if (dbClient.insertFlag(photo.getKey(), AuthUtils.getUserId())) {
            if (photoFlags.size() + 1 >= 2 && !dbClient.setPhotoFlagged(photo.getKey())) {
              // Internal error, impossible to flag the photo
              // Let's not say anything to the user as he did
              // his job.
              LOGGER.warning("Impossible to flag photo=" + photoId + " with count_flags="
                  + photoFlags.size());
            }
            // Everything went well
            reply = StatusResponse.newBuilder().build();
          } else {
            // Internal error, impossible to store the flag
            reply = StatusResponse.newBuilder().setError(getDbErrorMessage()).build();
          }
        } else {
          // User has already flagged the picture, do nothing
          reply = StatusResponse.newBuilder().build();
        }
      } catch (DatastoreException e) {
        // Internal error, impossible to store the flag
        reply = StatusResponse.newBuilder().setError(getDbErrorMessage()).build();
      }
    } else {
      reply = StatusResponse.newBuilder().setError(getAuthErrorMessage()).build();
    }
    responseObserver.onValue(reply);
    responseObserver.onCompleted();
  }

  @Override
  public final void uploadPhoto(final NewPhotoRequest request,
                                final StreamObserver<UploadPhotoResponse> responseObserver) {
    UploadPhotoResponse reply;
    if (AuthUtils.isSignedIn()) {
      String description = request.getDescription();
      try {
        Entity photo = dbClient.insertPhoto(description, AuthUtils.getUserId());
        try {
          long photoId = DbUtils.getEntityId(photo);
          // Everything went well, get upload URL
          String uploadUrl = CloudStorage.getUploadUrl(photoId + "_" + AuthUtils.getUserId()
              + ".jpeg");
          if (uploadUrl != null) {
            reply = UploadPhotoResponse.newBuilder()
                .setPhotoId(photoId)
                .setUserId(AuthUtils.getUserId())
                .setUploadUrl(uploadUrl).build();
          } else {
            reply = UploadPhotoResponse.newBuilder().setError(getErrorMessage("500",
                "Impossible to start file upload")).build();
          }

        } catch (NullPointerException e) {
          // Internal error, impossible to insert a new photo in the database
          reply = UploadPhotoResponse.newBuilder().setError(getDbErrorMessage()).build();
        }

      }  catch (DatastoreException e) {
        // Internal error
        reply = UploadPhotoResponse.newBuilder().setError(getDbErrorMessage()).build();
      }
    } else {
      reply = UploadPhotoResponse.newBuilder().setError(getAuthErrorMessage()).build();
    }
    responseObserver.onValue(reply);
    responseObserver.onCompleted();
  }

  @Override
  public final void editPhoto(final EditPhotoRequest request,
                              final StreamObserver<StatusResponse> responseObserver) {
    StatusResponse reply;
    if (AuthUtils.isSignedIn()) {
      long photoId = request.getPhotoId();
      Entity photo;
      try {
        photo = dbClient.getPhoto(photoId);
        if (photo == null) {
          throw new DatastoreException("editPhoto", 404, "Photo not found in Database", null);
        }
        Map<String, Value> propsPhoto = getPropertyMap(photo);
        String userIdPhoto = propsPhoto.get("userId").getStringValue();
        if (userIdPhoto != null) {
          if (userIdPhoto.equals(AuthUtils.getUserId())) {
            // Let's update the photo
            if (dbClient.updatePhotoDescription(photo.getKey(), request.getDescription())) {
              // Everything went well
              reply = StatusResponse.newBuilder().build();
            } else {
              // Internal error, impossible to delete
              reply = StatusResponse.newBuilder().setError(getDbErrorMessage()).build();
            }
          } else {
            // The user is trying to edit a photo that he does not
            // own
            reply = StatusResponse.newBuilder().setError(getErrorMessage("403",
                    "Not the owner of the photo")).build();
          }
        } else {
          // Internal error - no property userId on the photo
          reply = StatusResponse.newBuilder().setError(getDbErrorMessage()).build();
        }
      }  catch (DatastoreException e) {
        // Internal error
        reply = StatusResponse.newBuilder().setError(getDbErrorMessage()).build();
      }
    } else {
      reply = StatusResponse.newBuilder().setError(getAuthErrorMessage()).build();
    }
    responseObserver.onValue(reply);
    responseObserver.onCompleted();
  }

  @Override
  public final void deletePhoto(final DeletePhotoRequest request,
                                final StreamObserver<StatusResponse> responseObserver) {
    StatusResponse reply;
    if (AuthUtils.isSignedIn()) {
      long photoId = request.getPhotoId();
      try {
        Entity photo = dbClient.getPhoto(photoId);
        if (photo == null) {
          throw new DatastoreException("deletePhoto", 404, "Photo not found in Database", null);
        }
        Map<String, Value> propsPhoto = getPropertyMap(photo);
        String userIdPhoto = propsPhoto.get("userId").getStringValue();
        if (userIdPhoto != null) {
          if (userIdPhoto.equals(AuthUtils.getUserId())) {
            // Let's delete all the data associated to this photo
            if (dbClient.deletePhotoAndChildren(photo.getKey())) {
              // Everything went well
              reply = StatusResponse.newBuilder().build();
            } else {
              // Internal error, impossible to delete
              reply = StatusResponse.newBuilder().setError(getDbErrorMessage()).build();
            }
          } else {
            // The user is trying to suppress a photo that he
            // does not own
            reply = StatusResponse.newBuilder().setError(getErrorMessage("403",
                    "Not the owner of the photo")).build();
          }
        } else {
          // Internal error - no property userId on the photo
          reply = StatusResponse.newBuilder().setError(getDbErrorMessage()).build();
        }
      }  catch (DatastoreException e) {
        // Internal error
        reply = StatusResponse.newBuilder().setError(getDbErrorMessage()).build();
      }
    } else {
      reply = StatusResponse.newBuilder().setError(getAuthErrorMessage()).build();
    }
    responseObserver.onValue(reply);
    responseObserver.onCompleted();
  }


  @Override
  public final void ratePhoto(final VoteRequest request,
                              final StreamObserver<StatusResponse> responseObserver) {
    StatusResponse reply;
    if (AuthUtils.isSignedIn()) {
      long photoId = request.getPhotoId();
      try {
        Entity photo = dbClient.getPhoto(photoId);
        // Let's rate the photo
        int vote = 0;
        if (request.getVote() == VoteRequest.VoteType.THUMBS_DOWN) {
          vote = -1;
        } else if (request.getVote() == VoteRequest.VoteType.THUMBS_UP) {
          vote = 1;
        }
        if (photo != null && dbClient.voteForPhoto(photoId, vote, AuthUtils.getUserId())) {
          // Everything went well
          reply = StatusResponse.newBuilder().build();
        } else if (photo == null) {
          // Photo does not exist
          reply = StatusResponse.newBuilder().setError(getErrorMessage("404", "Photo not found"))
              .build();
        } else {
          // Internal error, impossible to rate
          reply = StatusResponse.newBuilder().setError(getDbErrorMessage()).build();
        }
      }  catch (DatastoreException e) {
        // Internal error
        reply = StatusResponse.newBuilder().setError(getDbErrorMessage()).build();
      }
    } else {
      reply = StatusResponse.newBuilder().setError(getAuthErrorMessage()).build();
    }
    responseObserver.onValue(reply);
    responseObserver.onCompleted();
  }

  /**
   * Returns an ErrorMessage to send via gRPC.
   * @param code the error code.
   * @param details details on the error.
   * @return the error message to send.
   */
  private Error getErrorMessage(final String code, final String details) {
    return Error.newBuilder().setCode(code).setDetails(details).build();
  }

  /**
   * Returns an ErrorMessage for an authentication error to send via gRPC.
   * @return the error message to send.
   */
  private Error getAuthErrorMessage() {
    return getErrorMessage("403", "You are not authenticated");
  }

  /**
   * Returns an ErrorMessage for an authentication error to send via gRPC.
   * @return the error message to send.
   */
  private Error getDbErrorMessage() {
    return getErrorMessage("500", "Database error");
  }

}
