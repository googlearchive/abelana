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


import com.examples.abelanav2.datastore.DbClient;
import com.examples.abelanav2.storage.CloudStorage;
import com.google.api.services.datastore.DatastoreV1;
import com.google.api.services.datastore.client.DatastoreException;
import com.google.common.collect.ImmutableList;
import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.google.api.services.datastore.client.DatastoreHelper.makeKey;
import static com.google.api.services.datastore.client.DatastoreHelper.makeProperty;
import static com.google.api.services.datastore.client.DatastoreHelper.makeValue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.spy;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AuthUtils.class, CloudStorage.class})
@PowerMockIgnore("javax.crypto.*")
public class GrpcCallsTest {
  @Mock
  DbClient dbClient;

  /**
   * We want to test the gRPC server implementation.
   */
  private AbelanaGrpcImpl toTest = spy(new AbelanaGrpcImpl());

  @Before
  public void setup(){
    toTest.setDbClient(dbClient);
    PowerMockito.mockStatic(AuthUtils.class);
    PowerMockito.mockStatic(CloudStorage.class);
  }

  /**
   * Flag photo tests.
   */

  @Test
  public void testFlagPhotoCallAllGood()  {
    mockUserSignedIn();

    try {
      Mockito.when(dbClient.getPhoto(12345)).thenReturn(mockPhotoEntity());
      Mockito.when(dbClient.getPhotoFlags(any(DatastoreV1.Key.class)))
          .thenReturn(new ArrayList<DatastoreV1.Entity>());
      Mockito.when(dbClient.insertFlag(any(DatastoreV1.Key.class), eq("user_id"))).thenReturn(true);
      Mockito.when(dbClient.setPhotoFlagged(any(DatastoreV1.Key.class)) ).thenReturn(true);
    } catch (DatastoreException e) {
      fail("Unexpected Datastore exception, code= " + e.getCode() + ", message= " + e.getMessage());
    }

    toTest.flagPhoto(FlagRequest.newBuilder().setPhotoId(12345).build(),
        getStreamObserverStatusResponseCheckError(null));
  }


  @Test
  public void testFlagPhotoCallNotAuthenticated()  {
    toTest.flagPhoto(FlagRequest.newBuilder().setPhotoId(12345).build(),
        getStreamObserverStatusResponseCheckError("403"));
  }

  @Test
  public void testFlagPhotoCallPhotoDoesNotExist()  {
    mockUserSignedIn();

    try {
      Mockito.when(dbClient.getPhoto(123456)).thenReturn(null);
    } catch (DatastoreException e) {
      fail("Unexpected Datastore exception, code= " + e.getCode() + ", message= " + e.getMessage());
    }

    toTest.flagPhoto(FlagRequest.newBuilder().setPhotoId(123456).build(),
        getStreamObserverStatusResponseCheckError("500"));
  }

  @Test
  public void testFlagPhotoCallUserAlreadyFlaggedPhoto()  {
    mockUserSignedIn();

    try {
      Mockito.when(dbClient.getPhoto(12345)).thenReturn(mockPhotoEntity());
      List<DatastoreV1.Entity> flags = new ArrayList<>();
      flags.add(mockFlagEntity("user_id"));
      Mockito.when(dbClient.getPhotoFlags(any(DatastoreV1.Key.class)))
          .thenReturn(flags);
    } catch (DatastoreException e) {
      fail("Unexpected Datastore exception, code= " + e.getCode() + ", message= " + e.getMessage());
    }

    toTest.flagPhoto(FlagRequest.newBuilder().setPhotoId(12345).build(),
        getStreamObserverStatusResponseCheckError(null));
  }

  @Test
  public void testFlagPhotoCallAnotherUserAlreadyFlaggedPhoto()  {
    mockUserSignedIn();

    try {
      Mockito.when(dbClient.getPhoto(12345)).thenReturn(mockPhotoEntity());
      List<DatastoreV1.Entity> flags = new ArrayList<>();
      flags.add(mockFlagEntity("user_id_2"));
      Mockito.when(dbClient.getPhotoFlags(any(DatastoreV1.Key.class)))
          .thenReturn(flags);
      Mockito.when(dbClient.insertFlag(any(DatastoreV1.Key.class), eq("user_id"))).thenReturn(true);
      Mockito.when(dbClient.setPhotoFlagged(any(DatastoreV1.Key.class)) ).thenReturn(true);
    } catch (DatastoreException e) {
      fail("Unexpected Datastore exception, code= " + e.getCode() + ", message= " + e.getMessage());
    }

    toTest.flagPhoto(FlagRequest.newBuilder().setPhotoId(12345).build(),
        getStreamObserverStatusResponseCheckError(null));
  }

  /**
   * Upload photo tests.
   */

  @Test
  public void testUploadPhotoCallNotAuthenticated()  {
    toTest.uploadPhoto(NewPhotoRequest.newBuilder().setDescription("Great description").build(),
        getStreamObserverUploadPhotoResponseCheckError("403"));
  }

  @Test
  public void testUploadPhotoCallAllGood()  {
    mockUserSignedIn();

    try {
      Mockito.when(dbClient.insertPhoto("Great description", "user_id"))
          .thenReturn(mockPhotoEntity());

      Mockito.when(CloudStorage.getUploadUrl(anyString())).thenReturn("https://url");
    } catch (DatastoreException e) {
      fail("Unexpected Datastore exception, code= " + e.getCode() + ", message= " + e.getMessage());
    }

    toTest.uploadPhoto(NewPhotoRequest.newBuilder().setDescription("Great description").build(),
        getStreamObserverUploadPhotoResponseCheckError(null));
  }

  @Test
  public void testUploadPhotoCallGcsUploadFails()  {
    mockUserSignedIn();

    try {
      Mockito.when(dbClient.insertPhoto("Great description", "user_id"))
          .thenReturn(mockPhotoEntity());

      Mockito.when(CloudStorage.getUploadUrl(anyString())).thenReturn(null);
    } catch (DatastoreException e) {
      fail("Unexpected Datastore exception, code= " + e.getCode() + ", message= " + e.getMessage());
    }

    toTest.uploadPhoto(NewPhotoRequest.newBuilder().setDescription("Great description").build(),
        getStreamObserverUploadPhotoResponseCheckError("500"));
  }

  @Test
  public void testUploadPhotoCallInsertPhotoFails()  {
    mockUserSignedIn();

    try {
      Mockito.when(dbClient.insertPhoto("Great description", "user_id"))
          .thenReturn(null);

    } catch (DatastoreException e) {
      fail("Unexpected Datastore exception, code= " + e.getCode() + ", message= " + e.getMessage());
    }

    toTest.uploadPhoto(NewPhotoRequest.newBuilder().setDescription("Great description").build(),
        getStreamObserverUploadPhotoResponseCheckError("500"));
  }

  /**
   * Edit photo tests.
   */

  @Test
  public void testEditPhotoCallNotAuthenticated()  {
    toTest.editPhoto(EditPhotoRequest.newBuilder().setPhotoId(12345)
        .setDescription("Great description").build(),
        getStreamObserverStatusResponseCheckError("403"));
  }

  @Test
  public void testEditPhotoCallAllGood()  {
    mockUserSignedIn();

    try {
      Mockito.when(dbClient.getPhoto(12345)).thenReturn(mockPhotoEntity());
      Mockito.when(dbClient.updatePhotoDescription(any(DatastoreV1.Key.class),
          eq("New description"))).thenReturn(true);

    } catch (DatastoreException e) {
      fail("Unexpected Datastore exception, code= " + e.getCode() + ", message= " + e.getMessage());
    }

    toTest.editPhoto(EditPhotoRequest.newBuilder().setPhotoId(12345)
        .setDescription("New description").build(),
        getStreamObserverStatusResponseCheckError(null));
  }

  @Test
  public void testEditPhotoCallPhotoDoesNotBelongToUser()  {
    mockUserSignedIn();

    try {
      Mockito.when(dbClient.getPhoto(12345)).thenReturn(mockPhotoEntity("user_id_2"));
    } catch (DatastoreException e) {
      fail("Unexpected Datastore exception, code= " + e.getCode() + ", message= " + e.getMessage());
    }

    toTest.editPhoto(EditPhotoRequest.newBuilder().setPhotoId(12345)
        .setDescription("New description").build(),
        getStreamObserverStatusResponseCheckError("403"));
  }

  @Test
  public void testEditPhotoCallPhotoNotFound()  {
    mockUserSignedIn();

    try {
      Mockito.when(dbClient.getPhoto(12345)).thenReturn(null);
    } catch (DatastoreException e) {
      fail("Unexpected Datastore exception, code= " + e.getCode() + ", message= " + e.getMessage());
    }

    toTest.editPhoto(EditPhotoRequest.newBuilder().setPhotoId(12345)
        .setDescription("New description").build(),
        getStreamObserverStatusResponseCheckError("500"));
  }

  @Test
  public void testEditPhotoCallUpdateFailed()  {
    mockUserSignedIn();

    try {
      Mockito.when(dbClient.getPhoto(12345)).thenReturn(mockPhotoEntity());
      Mockito.when(dbClient.updatePhotoDescription(any(DatastoreV1.Key.class),
          eq("New description"))).thenReturn(false);
    } catch (DatastoreException e) {
      fail("Unexpected Datastore exception, code= " + e.getCode() + ", message= " + e.getMessage());
    }

    toTest.editPhoto(EditPhotoRequest.newBuilder().setPhotoId(12345)
        .setDescription("New description").build(),
        getStreamObserverStatusResponseCheckError("500"));
  }

  /**
   * Delete photo tests.
   */

  @Test
  public void testDeletePhotoCallNotAuthenticated()  {
    toTest.deletePhoto(DeletePhotoRequest.newBuilder().setPhotoId(12345).build(),
        getStreamObserverStatusResponseCheckError("403"));
  }

  @Test
  public void testDeletePhotoCallAllGood()  {
    mockUserSignedIn();

    try {
      Mockito.when(dbClient.getPhoto(12345)).thenReturn(mockPhotoEntity());
      Mockito.when(dbClient.deletePhotoAndChildren(any(DatastoreV1.Key.class))).thenReturn(true);

    } catch (DatastoreException e) {
      fail("Unexpected Datastore exception, code= " + e.getCode() + ", message= " + e.getMessage());
    }

    toTest.deletePhoto(DeletePhotoRequest.newBuilder().setPhotoId(12345).build(),
        getStreamObserverStatusResponseCheckError(null));
  }

  @Test
  public void testDeletePhotoCallPhotoDoesNotBelongToUser()  {
    mockUserSignedIn();

    try {
      Mockito.when(dbClient.getPhoto(12345)).thenReturn(mockPhotoEntity("user_id_2"));
    } catch (DatastoreException e) {
      fail("Unexpected Datastore exception, code= " + e.getCode() + ", message= " + e.getMessage());
    }

    toTest.deletePhoto(DeletePhotoRequest.newBuilder().setPhotoId(12345).build(),
        getStreamObserverStatusResponseCheckError("403"));
  }

  @Test
  public void testDeletePhotoCallPhotoNotFound()  {
    mockUserSignedIn();

    try {
      Mockito.when(dbClient.getPhoto(12345)).thenReturn(null);
    } catch (DatastoreException e) {
      fail("Unexpected Datastore exception, code= " + e.getCode() + ", message= " + e.getMessage());
    }

    toTest.deletePhoto(DeletePhotoRequest.newBuilder().setPhotoId(12345).build(),
        getStreamObserverStatusResponseCheckError("500"));
  }

  @Test
  public void testEditPhotoCallDeleteFailed()  {
    mockUserSignedIn();

    try {
      Mockito.when(dbClient.getPhoto(12345)).thenReturn(mockPhotoEntity());
      Mockito.when(dbClient.deletePhotoAndChildren(any(DatastoreV1.Key.class))).thenReturn(false);
    } catch (DatastoreException e) {
      fail("Unexpected Datastore exception, code= " + e.getCode() + ", message= " + e.getMessage());
    }

    toTest.deletePhoto(DeletePhotoRequest.newBuilder().setPhotoId(12345).build(),
        getStreamObserverStatusResponseCheckError("500"));
  }

  /**
   * Rate photo tests.
   */

  @Test
  public void testRatePhotoCallNotAuthenticated()  {
    toTest.ratePhoto(VoteRequest.newBuilder().setPhotoId(12345).setVote(VoteRequest
        .VoteType.THUMBS_UP).build(), getStreamObserverStatusResponseCheckError("403"));
  }

  @Test
  public void testRatePhotoCallAllGood()  {
    mockUserSignedIn();

    try {
      Mockito.when(dbClient.getPhoto(12345)).thenReturn(mockPhotoEntity());
      Mockito.when(dbClient.voteForPhoto(eq(12345L), anyInt(), eq("user_id")))
          .thenReturn(true);
    } catch (DatastoreException e) {
      fail("Unexpected Datastore exception, code= " + e.getCode() + ", message= " + e.getMessage());
    }

    toTest.ratePhoto(VoteRequest.newBuilder().setPhotoId(12345).setVote(VoteRequest
        .VoteType.THUMBS_UP).build(), getStreamObserverStatusResponseCheckError(null));
  }

  @Test
  public void testRatePhotoCallVoteFailed()  {
    mockUserSignedIn();

    try {
      Mockito.when(dbClient.getPhoto(12345)).thenReturn(mockPhotoEntity());
      Mockito.when(dbClient.voteForPhoto(eq(12345L), anyInt(), eq("user_id")))
          .thenReturn(false);
    } catch (DatastoreException e) {
      fail("Unexpected Datastore exception, code= " + e.getCode() + ", message= " + e.getMessage());
    }

    toTest.ratePhoto(VoteRequest.newBuilder().setPhotoId(12345).setVote(VoteRequest
        .VoteType.THUMBS_UP).build(), getStreamObserverStatusResponseCheckError("500"));
  }

  @Test
  public void testRatePhotoCallPhotoNotFound()  {
    mockUserSignedIn();

    try {
      Mockito.when(dbClient.getPhoto(12345)).thenReturn(null);
    } catch (DatastoreException e) {
      fail("Unexpected Datastore exception, code= " + e.getCode() + ", message= " + e.getMessage());
    }

    toTest.ratePhoto(VoteRequest.newBuilder().setPhotoId(12345).setVote(VoteRequest
        .VoteType.THUMBS_UP).build(), getStreamObserverStatusResponseCheckError("404"));
  }

  /**
   * Photo stream tests.
   */

  @Test
  public void testPhotoStreamCallNotAuthenticated()  {
    toTest.photoStream(PhotoListRequest.newBuilder().setPageNumber(0).build(),
        getStreamObserverPhotoListResponseCheckError("403"));
  }

  @Test
  public void testPhotoStreamCallAllGood()  {
    mockUserSignedIn();

    try {
      Mockito.when(dbClient.getPhotoList(eq("user_id"), any(DbClient.PhotoListType.class),
          any(ByteString.class))).thenReturn(mockPhotoList());
      Mockito.when(dbClient.getVoteValueForPhoto(anyLong(), eq("user_id"))).thenReturn(-1L);
    } catch (DatastoreException e) {
      fail("Unexpected Datastore exception, code= " + e.getCode() + ", message= " + e.getMessage());
    }

    toTest.photoStream(PhotoListRequest.newBuilder().setPageNumber(0).build(),
        getStreamObserverPhotoListResponseCheckError(null));
  }

  @Test
  public void testPhotoStreamCallAllGoodInvalidCursor()  {
    mockUserSignedIn();

    try {
      Mockito.when(dbClient.getPhotoList(eq("user_id"), eq(DbClient.PhotoListType
              .PHOTO_LIST_STREAM), any(ByteString.class))).thenReturn(mockPhotoList());
      Mockito.when(dbClient.getVoteValueForPhoto(anyLong(), eq("user_id"))).thenReturn(-1L);
      Mockito.when(dbClient.getAndDeleteCursor(1)).thenReturn(null);
    } catch (DatastoreException e) {
      fail("Unexpected Datastore exception, code= " + e.getCode() + ", message= " + e.getMessage());
    }

    toTest.photoStream(PhotoListRequest.newBuilder().setPageNumber(1).build(),
        getStreamObserverPhotoListResponseCheckError("400-200"));
  }

  /**
   * My pictures tests.
   */

  @Test
  public void testMyPicturesCallNotAuthenticated()  {
    toTest.listMyPhotos(PhotoListRequest.newBuilder().setPageNumber(0).build(),
        getStreamObserverPhotoListResponseCheckError("403"));
  }

  @Test
  public void testMyPicturesCallAllGood()  {
    mockUserSignedIn();

    try {
      Mockito.when(dbClient.getPhotoList(eq("user_id"), eq(DbClient.PhotoListType.PHOTO_LIST_MINE),
          any(ByteString.class))).thenReturn(mockPhotoList());
    } catch (DatastoreException e) {
      fail("Unexpected Datastore exception, code= " + e.getCode() + ", message= " + e.getMessage());
    }

    toTest.listMyPhotos(PhotoListRequest.newBuilder().setPageNumber(0).build(),
        getStreamObserverPhotoListResponseCheckError(null));
  }

  @Test
  public void testMyPicturesCallAllGoodInvalidCursor()  {
    mockUserSignedIn();

    try {
      Mockito.when(dbClient.getPhotoList(eq("user_id"), eq(DbClient.PhotoListType
          .PHOTO_LIST_MINE), any(ByteString.class))).thenReturn(mockPhotoList());
      Mockito.when(dbClient.getVoteValueForPhoto(anyLong(), eq("user_id"))).thenReturn(-1L);
      Mockito.when(dbClient.getAndDeleteCursor(1)).thenReturn(null);
    } catch (DatastoreException e) {
      fail("Unexpected Datastore exception, code= " + e.getCode() + ", message= " + e.getMessage());
    }

    toTest.listMyPhotos(PhotoListRequest.newBuilder().setPageNumber(1).build(),
        getStreamObserverPhotoListResponseCheckError("400-200"));
  }

  /**
   * My likes tests.
   */

  @Test
  public void testMyLikesCallNotAuthenticated()  {
    toTest.listMyLikes(PhotoListRequest.newBuilder().setPageNumber(0).build(),
        getStreamObserverPhotoListResponseCheckError("403"));
  }

  @Test
  public void testMyLikesCallAllGood()  {
    mockUserSignedIn();

    try {
      Mockito.when(dbClient.getPhotoList(eq("user_id"), eq(DbClient.PhotoListType.PHOTO_LIST_LIKES),
          any(ByteString.class))).thenReturn(mockPhotoList());
    } catch (DatastoreException e) {
      fail("Unexpected Datastore exception, code= " + e.getCode() + ", message= " + e.getMessage());
    }

    toTest.listMyLikes(PhotoListRequest.newBuilder().setPageNumber(0).build(),
        getStreamObserverPhotoListResponseCheckError(null));
  }

  @Test
  public void testMyLikesCallAllGoodInvalidCursor()  {
    mockUserSignedIn();

    try {
      Mockito.when(dbClient.getPhotoList(eq("user_id"), eq(DbClient.PhotoListType
          .PHOTO_LIST_LIKES), any(ByteString.class))).thenReturn(mockPhotoList());
      Mockito.when(dbClient.getVoteValueForPhoto(anyLong(), eq("user_id"))).thenReturn(-1L);
      Mockito.when(dbClient.getAndDeleteCursor(1)).thenReturn(null);
    } catch (DatastoreException e) {
      fail("Unexpected Datastore exception, code= " + e.getCode() + ", message= " + e.getMessage());
    }

    toTest.listMyLikes(PhotoListRequest.newBuilder().setPageNumber(1).build(),
        getStreamObserverPhotoListResponseCheckError("400-200"));
  }

  private  StreamObserver<StatusResponse> getStreamObserverStatusResponseCheckError(
      final String errorCode ) {

    return new StreamObserver<StatusResponse>() {
      @Override
      public void onValue(StatusResponse response) {
        if(errorCode != null) {
          assert(response.hasError() && response.getError().getCode().equals(errorCode));
        } else {
          assert(!response.hasError());
        }
      }

      @Override
      public void onError(Throwable throwable) { }

      @Override
      public void onCompleted() { }
    };
  }

  private  StreamObserver<UploadPhotoResponse> getStreamObserverUploadPhotoResponseCheckError(
      final String errorCode ) {

    return new StreamObserver<UploadPhotoResponse>() {
      @Override
      public void onValue(UploadPhotoResponse response) {
        if(errorCode != null) {
          assert(response.hasError() && response.getError().getCode().equals(errorCode));
        } else {
          assert(!response.hasError());
        }
      }

      @Override
      public void onError(Throwable throwable) { }

      @Override
      public void onCompleted() { }
    };
  }

  private  StreamObserver<PhotoListResponse> getStreamObserverPhotoListResponseCheckError(
      final String errorCode ) {

    return new StreamObserver<PhotoListResponse>() {
      @Override
      public void onValue(PhotoListResponse response) {
        if(errorCode != null) {
          assert(response.hasError() && response.getError().getCode().equals(errorCode));
        } else {
          assert(!response.hasError());
        }
      }

      @Override
      public void onError(Throwable throwable) { }

      @Override
      public void onCompleted() { }
    };
  }


  /**
   * Returns a dummy Photo Entity with the default user id in those tests: user_id.
   * @return a Photo Entity.
   */
  public DatastoreV1.Entity mockPhotoEntity() {
   return mockPhotoEntity("user_id");
  }

  /**
   * Returns a dummy Photo Entity with the userId passed.
   * @param userId the user ID to use.
   * @return a Photo Entity.
   */
  public DatastoreV1.Entity mockPhotoEntity(String userId) {
    List<DatastoreV1.Property> properties = ImmutableList.of(
        makeProperty("description", makeValue("Great description")).build(),
        makeProperty("userId", makeValue(userId)).build(),
        makeProperty("date", makeValue(new Date())).build(),
        makeProperty("flagged", makeValue(false)).build(),
        makeProperty("available", makeValue(false)).build(),
        makeProperty("numberVotes", makeValue(0)).build(),
        makeProperty("numberPositiveVotes", makeValue(0)).build(),
        makeProperty("lowerTruePopularity", makeValue(0)).build(),
        makeProperty("upperTruePopularity", makeValue(0)).build()
    );
    return DatastoreV1.Entity.newBuilder().setKey(makeKey("Photo", 12345))
        .addAllProperty(properties).build();
  }

  /**
   * Returns a dummy Flag Entity with the userId passed.
   * @param userId the user ID to use.
   * @return a Flag Entity.
   */
  public DatastoreV1.Entity mockFlagEntity(String userId) {
    List<DatastoreV1.Property> properties = ImmutableList.of(
        makeProperty("userId", makeValue(userId)).build()
    );
    return DatastoreV1.Entity.newBuilder().setKey(makeKey(makeKey("Photo", 12345).build(), "Flag",
        67890)).addAllProperty(properties).build();
  }

  /**
   * Returns a dummy Photo list.
   * @return a  EntityListAndCursorResult with photos.
   */
  public DbClient.EntityListAndCursorResult mockPhotoList() {
    List<DatastoreV1.Entity> photoList = new ArrayList<>();
    photoList.add(mockPhotoEntity("user_id_2"));
    photoList.add(mockPhotoEntity("user_id_3"));
    return new DbClient.EntityListAndCursorResult(photoList, null);
  }

  /**
   * Sets the user as signed in the application.
   */
  public void mockUserSignedIn() {
    Mockito.when(AuthUtils.isSignedIn()).thenReturn(true);
    Mockito.when(AuthUtils.getUserId()).thenReturn("user_id");
  }
}
