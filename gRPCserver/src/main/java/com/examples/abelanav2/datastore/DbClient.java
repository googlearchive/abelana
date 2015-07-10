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

package com.examples.abelanav2.datastore;

import static com.google.api.services.datastore.client.DatastoreHelper.getPropertyMap;
import static com.google.api.services.datastore.client.DatastoreHelper.makeFilter;
import static com.google.api.services.datastore.client.DatastoreHelper.makeKey;
import static com.google.api.services.datastore.client.DatastoreHelper.makeOrder;
import static com.google.api.services.datastore.client.DatastoreHelper.makeProperty;
import static com.google.api.services.datastore.client.DatastoreHelper.makeValue;

import com.google.api.services.datastore.DatastoreV1.Entity;
import com.google.api.services.datastore.DatastoreV1.EntityResult;
import com.google.api.services.datastore.DatastoreV1.Filter;
import com.google.api.services.datastore.DatastoreV1.Key;
import com.google.api.services.datastore.DatastoreV1.Property;
import com.google.api.services.datastore.DatastoreV1.PropertyFilter;
import com.google.api.services.datastore.DatastoreV1.PropertyOrder;
import com.google.api.services.datastore.DatastoreV1.Query;
import com.google.api.services.datastore.DatastoreV1.QueryResultBatch;
import com.google.api.services.datastore.DatastoreV1.RunQueryRequest;
import com.google.api.services.datastore.DatastoreV1.RunQueryResponse;
import com.google.api.services.datastore.DatastoreV1.Value;
import com.google.api.services.datastore.client.Datastore;
import com.google.api.services.datastore.client.DatastoreException;
import com.google.api.services.datastore.client.DatastoreFactory;
import com.google.api.services.datastore.client.DatastoreHelper;
import com.google.common.collect.ImmutableList;
import com.google.protobuf.ByteString;

import com.examples.abelanav2.BackendConstants;
import org.apache.commons.math3.stat.interval.ConfidenceInterval;
import org.apache.commons.math3.stat.interval.WilsonScoreInterval;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


/**
 * Client to make datastore request.
 */
public class DbClient {
  /**
   * The datastore we access.
   */
  private Datastore datastore = null;
  /**
   * The photos entities.
   */
  public static final String PHOTO_ENTITY = "Photo";
  /**
   * The votes entities, children of photos.
   */
  public static final String VOTE_ENTITY = "Vote";
  /**
   * The flag entities, children of photos.
   */
  public static final String FLAG_ENTITY = "Flag";
  /**
   * The flag entities, children of photos.
   */
  public static final String CURSOR_ENTITY = "Cursor";
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
   * Logger.
   */
  private static final Logger LOGGER =
      Logger.getLogger(DbClient.class.getName());

  /**
   * Constructor.
   */
  public DbClient() {
    try {
      // Setup the connection to Google Cloud Datastore and infer
      // credentials from the environment.
      datastore = DatastoreFactory.get().create(DatastoreHelper.getOptionsFromEnv()
          .dataset(BackendConstants.PROJECT_ID).build());
    } catch (GeneralSecurityException e) {
      LOGGER.severe("Security error connecting to the datastore: " + e.getMessage());
    } catch (IOException e) {
      LOGGER.severe("I/O error connecting to the datastore: " + e.getMessage());
    }
  }

  /**
   * Gets a photo by ID.
   * @param photoId the photo id.
   * @return a Photo entity.
   * @throws DatastoreException if there is a datastore error.
   */
  public Entity getPhoto(final long photoId) throws DatastoreException {
    return DbUtils.getEntity(datastore, PHOTO_ENTITY, photoId);
  }

  /**
   * Gets a photo by Key.
   * @param photoKey the photo key.
   * @return a Photo entity.
   * @throws DatastoreException if there is a datastore error.
   */
  public Entity getPhoto(final Key photoKey) throws DatastoreException {
    return DbUtils.getEntity(datastore, photoKey);
  }

  /**
   * Gets a photo by ID.
   * @param photoKey the photo key.
   * @return a list of Photo entities.
   * @throws DatastoreException if there is a datastore error.
   */
  public List<Entity> getPhotoFlags(final Key photoKey) throws DatastoreException {
    return DbUtils.getChildren(datastore, photoKey, FLAG_ENTITY, false);
  }

  /**
   * Inserts a new photo flag.
   * @param photoKey the photo key.
   * @param userId the user flagging the picture.
   * @return boolean for success.
   * @throws DatastoreException if there is a datastore error.
   */
  public boolean insertFlag(final Key photoKey, final String userId)
      throws DatastoreException {
    List<Property> properties = ImmutableList.of(
        makeProperty("userId", makeValue(userId)).build(),
        makeProperty("date", makeValue(new Date())).build()
    );
    Entity flag = DbUtils.insertEntity(datastore, FLAG_ENTITY, photoKey, properties);
    return flag.hasKey();
  }

  /**
   * Set photo flagged.
   * @param photoKey the photo key.
   * @return a boolean indicating success.
   * @throws DatastoreException if there is a datastore error.
   */
  public boolean setPhotoFlagged(final Key photoKey) throws DatastoreException {
    List<Property> properties = ImmutableList.of(
        makeProperty("flagged", makeValue(true)).build()
    );
    return DbUtils.updateEntity(datastore, photoKey, properties);
  }

  /**
   * Deletes a photo and its children - the votes and flags associated to it .
   * @param photoKey the photo key.
   * @return a boolean indicating the success.
   * @throws DatastoreException if there is a datastore error.
   */
  public boolean deletePhotoAndChildren(final Key photoKey) throws DatastoreException {
    Query.Builder query = Query.newBuilder();
    Filter photoIdFilter = makeFilter("photoId", PropertyFilter.Operator.EQUAL,
        makeValue(DbUtils.getEntityId(photoKey))).build();
    query.setFilter(makeFilter(photoIdFilter));
    query.addKindBuilder().setName(VOTE_ENTITY);
    RunQueryRequest request = RunQueryRequest.newBuilder().setQuery(query).build();
    RunQueryResponse response = datastore.runQuery(request);

    for (EntityResult result : response.getBatch().getEntityResultList()) {
      DbUtils.deleteEntity(datastore, result.getEntity().getKey());
    }
    while (response.getBatch().getMoreResults() == QueryResultBatch.MoreResultsType.NOT_FINISHED) {
      ByteString endCursor = response.getBatch().getEndCursor();
      query.setStartCursor(endCursor);
      request = RunQueryRequest.newBuilder().setQuery(query).build();
      response = datastore.runQuery(request);
      for (EntityResult result : response.getBatch().getEntityResultList()) {
        DbUtils.deleteEntity(datastore, result.getEntity().getKey());
      }
    }

    return DbUtils.deleteEntityAndChildren(datastore, photoKey);
  }

  /**
   * Update the photo description.
   * @param photoKey the photo key.
   * @param description the new photo description.
   * @return a boolean indicating the success.
   * @throws DatastoreException if there is a datastore error.
   */
  public boolean updatePhotoDescription(final Key photoKey, final String description)
      throws DatastoreException {
    List<Property> properties = ImmutableList.of(
        makeProperty("description", makeValue(description)).build()
    );
    return DbUtils.updateEntity(datastore, photoKey, properties);
  }

  /**
   * Set a vote for the photo.
   * @param photoId the photo id.
   * @param vote the new photo vote for this user.
   * @param userId the user ID of the user voting.
   * @return a boolean indicating the success.
   * @throws DatastoreException if there is a datastore error.
   */
  public boolean voteForPhoto(final long photoId, final int vote, final String userId)
      throws DatastoreException {

    // Has the user already rated the picture?
    Query.Builder query = Query.newBuilder();
    Filter userIdFilter = makeFilter("userId", PropertyFilter.Operator.EQUAL,
        makeValue(userId)).build();
    Filter photoIdFilter = makeFilter("photoId", PropertyFilter.Operator.EQUAL,
        makeValue(photoId)).build();
    query.setFilter(makeFilter(photoIdFilter, userIdFilter));
    query.addKindBuilder().setName(VOTE_ENTITY);
    RunQueryRequest request = RunQueryRequest.newBuilder().setQuery(query).build();
    RunQueryResponse response = datastore.runQuery(request);

    // if yes, update his vote
    if (response.getBatch().getEntityResultCount() == 1) {
      Entity voteEntity = response.getBatch().getEntityResultList().get(0).getEntity();
      Key voteKey = voteEntity.getKey();

      // Is the new vote neutral? if yes, delete the old vote
      if (vote == 0) {
        if (!DbUtils.deleteEntity(datastore, voteKey)) {
          return false;
        }
      } else {
        // If not, update the old vote
        List<Property> properties = ImmutableList.of(
            makeProperty("vote", makeValue(vote)).build(),
            makeProperty("date", makeValue(new Date())).build()
        );
        if (!DbUtils.updateEntity(datastore, voteKey, properties)) {
          return false;
        }
      }

      // Update photo
      Map<String, Value> propsVote = getPropertyMap(voteEntity);
      return updateVotesAndBounds(makeKey(PHOTO_ENTITY, photoId).build(),
          propsVote.get("vote").getIntegerValue(), vote);

    } else if (vote != 0) {
      // If this user has not rated the picture yet, create vote
      List<Property> properties = ImmutableList.of(
          makeProperty("vote", makeValue(vote)).build(),
          makeProperty("userId", makeValue(userId)).build(),
          makeProperty("photoId", makeValue(photoId)).build(),
          makeProperty("date", makeValue(new Date())).build()
      );
      Entity voteEntity = DbUtils.insertEntity(datastore, VOTE_ENTITY, null, properties);
      if (!voteEntity.hasKey()) {
        return false;
      }

      // Update photo
      return updateVotesAndBounds(makeKey(PHOTO_ENTITY, photoId).build(), 0, vote);
    } else {
      // The user had not voted and tries to put a
      // neutral vote.
      return true;
    }
  }

  /**
   * Updates the vote counts of a photo and its popularity bounds.
   * @param photoKey the photo key.
   * @param oldVote the old vote.
   * @param newVote the new vote.
   * @return a boolean indicating success.
   * @throws DatastoreException  if there is a datastore error.
   */
  public boolean updateVotesAndBounds(final Key photoKey, final long oldVote,
                                            final long newVote) throws DatastoreException {
    Entity photoEntity = getPhoto(photoKey);
    Map<String, Value> propsPhoto = getPropertyMap(photoEntity);
    long newVoteCount = propsPhoto.get("numberVotes").getIntegerValue();
    long newPositiveVoteCount = propsPhoto.get("numberPositiveVotes").getIntegerValue();
    if (newVote == 0) {
      newVoteCount--;
    }
    if (oldVote == 0) {
      newVoteCount++;
    }
    if (oldVote == 1 && newVote != 1)  {
      newPositiveVoteCount--;
    }
    if (oldVote != 1 && newVote == 1)  {
      newPositiveVoteCount++;
    }

    // If there is no vote for a picture, it means that even the author
    // has removed his vote for this picture. Let's send it down the
    // abyss with upperTruePopularity of 0. If there is at least one
    // vote, let's compute the new popularity of the photo.
    double lowerTruePopularity = 0;
    double upperTruePopularity = 0;
    if (newVoteCount > 0) {
      WilsonScoreInterval wilsonScoreInterval = new WilsonScoreInterval();
      ConfidenceInterval confidenceInterval = wilsonScoreInterval.createInterval((int) newVoteCount,
              (int) newPositiveVoteCount, BackendConstants.CONFIDENCE_INTERVAL);
      lowerTruePopularity = confidenceInterval.getLowerBound();
      upperTruePopularity = confidenceInterval.getUpperBound();
    }

    List<Property> properties = ImmutableList.of(
        makeProperty("numberVotes", makeValue(newVoteCount)).build(),
        makeProperty("numberPositiveVotes", makeValue(newPositiveVoteCount)).build(),
        makeProperty("lowerTruePopularity", makeValue(lowerTruePopularity)).build(),
        makeProperty("upperTruePopularity", makeValue(upperTruePopularity)).build()
    );

    return DbUtils.updateEntity(datastore, photoEntity.getKey(),
        properties);
  }

  /**
   * Inserts a new photo flag.
   * @param description the photo description.
   * @param userId the user flagging the picture.
   * @return the entity created.
   * @throws DatastoreException if there is a datastore error.
   */
  public Entity insertPhoto(final String description,
                                  final String userId)
      throws DatastoreException {
    List<Property> properties = ImmutableList.of(
        makeProperty("description", makeValue(description)).build(),
        makeProperty("userId", makeValue(userId)).build(),
        makeProperty("date", makeValue(new Date())).build(),
        makeProperty("flagged", makeValue(false)).build(),
        makeProperty("available", makeValue(false)).build(),
        makeProperty("numberVotes", makeValue(0)).build(),
        makeProperty("numberPositiveVotes", makeValue(0)).build(),
        makeProperty("lowerTruePopularity", makeValue(0)).build(),
        makeProperty("upperTruePopularity", makeValue(0)).build()
    );
    Entity photo = DbUtils.insertEntity(datastore, PHOTO_ENTITY, null, properties);
    if (photo.hasKey()) {
      // A user always votes for his photo
      // Creates upper/lower bounds for ranking
      voteForPhoto(DbUtils.getEntityId(photo), 1 , userId);
    }
    return photo;
  }

  /**
   * Gets a photo list.
   * @param userId the user flagging the picture.
   * @param listType the type of list to return.
   * @param startCursor the page cursor.
   * @return the photo and cursor list.
   * @throws DatastoreException if there is a datastore error.
   */
  public EntityListAndCursorResult getPhotoList(final String userId,
                                                      final PhotoListType listType,
                                                      final ByteString startCursor)
      throws DatastoreException  {

    switch (listType) {
      case PHOTO_LIST_MINE:
        return getPhotoListMine(userId, startCursor);

      case PHOTO_LIST_LIKES:
        return getPhotoListLikes(userId, startCursor);

      case PHOTO_LIST_STREAM:
      default:
        return getPhotoListStream(startCursor);
    }
  }

  /**
   * Gets the photo list with the user photos.
   * @param userId the user flagging the picture.
   * @param startCursor the page cursor.
   * @return the photo and cursor list.
   * @throws DatastoreException if there is a datastore error.
   */
  private EntityListAndCursorResult getPhotoListMine(final String userId,
                                                     final ByteString startCursor)
      throws DatastoreException  {

    Query.Builder query = Query.newBuilder();
    query.addKindBuilder().setName(PHOTO_ENTITY);
    query.addOrder(makeOrder("date", PropertyOrder.Direction.DESCENDING));
    Filter userIdFilter = makeFilter("userId", PropertyFilter.Operator.EQUAL,
        makeValue(userId)).build();
    Filter availableFilter = makeFilter("available", PropertyFilter.Operator.EQUAL,
        makeValue(true)).build();
    query.setFilter(makeFilter(userIdFilter, availableFilter));
    query.setLimit(BackendConstants.PHOTOS_PER_PAGE);
    if (startCursor != null) {
      query.setStartCursor(startCursor);
    }

    RunQueryRequest request = RunQueryRequest.newBuilder().setQuery(query).build();
    RunQueryResponse response = datastore.runQuery(request);

    List<Entity> entityList = new ArrayList<>();
    for (EntityResult result : response.getBatch().getEntityResultList()) {
      entityList.add(result.getEntity());
    }
    ByteString cursor = null;
    if (response.getBatch().hasMoreResults()) {
      cursor = response.getBatch().getEndCursor();
    }

    return new EntityListAndCursorResult(ImmutableList.copyOf(entityList), cursor);
  }

  /**
   * Gets the photo list of the user likes.
   * @param userId the user flagging the picture.
   * @param startCursor the page cursor.
   * @return the photo and cursor list.
   * @throws DatastoreException if there is a datastore error.
   */
  private EntityListAndCursorResult getPhotoListLikes(final String userId,
                                                      final ByteString startCursor)
      throws DatastoreException  {

    Query.Builder query = Query.newBuilder();
    query.addKindBuilder().setName(VOTE_ENTITY);
    query.addOrder(makeOrder("date", PropertyOrder.Direction.DESCENDING));
    Filter userIdFilter = makeFilter("userId", PropertyFilter.Operator.EQUAL,
        makeValue(userId)).build();
    query.setFilter(makeFilter(userIdFilter));
    query.setLimit(BackendConstants.PHOTOS_PER_PAGE);
    if (startCursor != null) {
      query.setStartCursor(startCursor);
    }
    RunQueryRequest request = RunQueryRequest.newBuilder().setQuery(query).build();
    RunQueryResponse response = datastore.runQuery(request);

    List<Entity> entityList = new ArrayList<>();
    for (EntityResult result : response.getBatch().getEntityResultList()) {
      Map<String, Value> propsVote = getPropertyMap(result.getEntity());
      Entity photo = getPhoto(propsVote.get("photoId").getIntegerValue());
      if (photo != null) {
        Map<String, Value> propsPhoto = getPropertyMap(photo);
        if (propsPhoto.get("available").getBooleanValue()) {
          entityList.add(photo);
        }
      }
    }

    ByteString cursor = null;
    if (response.getBatch().hasMoreResults()) {
      cursor = response.getBatch().getEndCursor();
    }

    return new EntityListAndCursorResult(ImmutableList.copyOf(entityList), cursor);
  }

  /**
   * Gets the photo list stream.
   * @param startCursor the page cursor.
   * @return the photo and cursor list.
   * @throws DatastoreException if there is a datastore error.
   */
  private EntityListAndCursorResult getPhotoListStream(final ByteString startCursor)
      throws DatastoreException  {

    Query.Builder query = Query.newBuilder();
    query.addKindBuilder().setName(PHOTO_ENTITY);
    query.addOrder(makeOrder("upperTruePopularity", PropertyOrder.Direction.DESCENDING));
    Filter availableFilter = makeFilter("available", PropertyFilter.Operator.EQUAL, makeValue(true))
        .build();
    query.setFilter(makeFilter(availableFilter));
    if (startCursor != null) {
      query.setStartCursor(startCursor);
      query.setLimit(BackendConstants.PHOTOS_PER_PAGE);
    } else {
      // If on the first page, let's add recent photos too
      query.setLimit(BackendConstants.PHOTOS_PER_PAGE);
    }
    RunQueryRequest request = RunQueryRequest.newBuilder().setQuery(query).build();
    RunQueryResponse response = datastore.runQuery(request);
    List<Entity> entityList = new ArrayList<>();
    for (EntityResult result : response.getBatch().getEntityResultList()) {
      entityList.add(result.getEntity());
    }
    ByteString cursor = null;
    if (response.getBatch().hasMoreResults()) {
      cursor = response.getBatch().getEndCursor();
    }
    // Randomize
    Collections.shuffle(entityList);

    return new EntityListAndCursorResult(ImmutableList.copyOf(entityList), cursor);
  }

  /**
   * Returns the vote from a user for a specific photo.
   * @param photoId the photo ID.
   * @param userId the user ID.
   * @return the vote value (0 if database error).
   */
  public long getVoteValueForPhoto(final long photoId, final String userId) {

    // Has the user already rated the picture?
    Query.Builder query = Query.newBuilder();
    Filter userIdFilter = makeFilter("userId", PropertyFilter.Operator.EQUAL,
        makeValue(userId)).build();
    Filter photoIdFilter = makeFilter("photoId", PropertyFilter.Operator.EQUAL,
        makeValue(photoId)).build();
    query.setFilter(makeFilter(photoIdFilter, userIdFilter));
    query.addKindBuilder().setName(VOTE_ENTITY);
    RunQueryRequest request = RunQueryRequest.newBuilder().setQuery(query).build();
    RunQueryResponse response;
    try {
      response = datastore.runQuery(request);

      if (response.getBatch().getEntityResultCount() == 1) {
        Entity voteEntity = response.getBatch().getEntityResultList().get(0).getEntity();

        Map<String, Value> voteProp = getPropertyMap(voteEntity);
        return voteProp.get("vote").getIntegerValue();
      }
    } catch (DatastoreException e) {
      return 0;
    }
    return 0;
  }

  /**
   * Inserts a new photo flag.
   * @param cursor the cursor to store.
   * @param userId the user flagging the picture.
   * @return boolean for success.
   * @throws DatastoreException if there is a datastore error.
   */
  public long insertCursor(final ByteString cursor, final String userId)
      throws DatastoreException {
    List<Property> properties = ImmutableList.of(
        makeProperty("userId", makeValue(userId)).build(),
        makeProperty("date", makeValue(new Date())).build(),
        makeProperty("cursor", makeValue(cursor)).build()
    );
    Entity cursorEntity = DbUtils.insertEntity(datastore, CURSOR_ENTITY, null, properties);
    return DbUtils.getEntityId(cursorEntity);
  }

  /**
   * Inserts a new photo flag.
   * @param cursorId the cursor id to retrieve.
   * @return boolean for success.
   * @throws DatastoreException if there is a datastore error.
   */
  public Entity getAndDeleteCursor(final long cursorId) throws DatastoreException {
    Entity cursor = DbUtils.getEntity(datastore, CURSOR_ENTITY, cursorId);
    if (cursor != null) {
      DbUtils.deleteEntity(datastore, cursor.getKey());
    }
    return cursor;
  }

  /**
   * Class that holds an entity list and a cursor to the next page.
   */
  public static class EntityListAndCursorResult {
    /**
     * The list of entities.
     */
    private final List<Entity> entityList;
    /**
     * The cursor.
     */
    private final ByteString cursor;

    /**
     * Constructor.
     * @param list the List of entities.
     * @param cursor the cursor to the next page of results.
     */
    public EntityListAndCursorResult(final List<Entity> list, final ByteString cursor) {
      this.entityList = ImmutableList.copyOf(list);
      this.cursor = cursor;
    }

    /**
     * Gets the list of entities.
     * @return the list of entities.
     */
    public List<Entity> getEntityList() {
      return entityList;
    }

    /**
     * Gets the cursor.
     * @return the cursor.
     */
    public ByteString getCursor() {
      return cursor;
    }

  }
}
