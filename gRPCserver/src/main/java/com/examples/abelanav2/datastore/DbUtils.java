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

import static com.google.api.services.datastore.client.DatastoreHelper.makeFilter;
import static com.google.api.services.datastore.client.DatastoreHelper.makeKey;
import static com.google.api.services.datastore.client.DatastoreHelper.makeValue;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.api.services.datastore.DatastoreV1.BeginTransactionRequest;
import com.google.api.services.datastore.DatastoreV1.BeginTransactionResponse;
import com.google.api.services.datastore.DatastoreV1.CommitRequest;
import com.google.api.services.datastore.DatastoreV1.CommitResponse;
import com.google.api.services.datastore.DatastoreV1.Entity;
import com.google.api.services.datastore.DatastoreV1.EntityResult;
import com.google.api.services.datastore.DatastoreV1.Filter;
import com.google.api.services.datastore.DatastoreV1.Key;
import com.google.api.services.datastore.DatastoreV1.LookupRequest;
import com.google.api.services.datastore.DatastoreV1.LookupResponse;
import com.google.api.services.datastore.DatastoreV1.Mutation;
import com.google.api.services.datastore.DatastoreV1.Property;
import com.google.api.services.datastore.DatastoreV1.PropertyExpression;
import com.google.api.services.datastore.DatastoreV1.PropertyFilter;
import com.google.api.services.datastore.DatastoreV1.PropertyReference;
import com.google.api.services.datastore.DatastoreV1.Query;
import com.google.api.services.datastore.DatastoreV1.ReadOptions;
import com.google.api.services.datastore.DatastoreV1.RollbackRequest;
import com.google.api.services.datastore.DatastoreV1.RunQueryRequest;
import com.google.api.services.datastore.client.Datastore;
import com.google.api.services.datastore.client.DatastoreException;

import java.util.ArrayList;
import java.util.List;


/**
 * Provides actual operations on the datastore database.
 */
public final class DbUtils {

  /**
   * Constructor.
   */
  private DbUtils() { }


  /**
   * Gets an entity from the datastore.
   * @param datastore the datastore.
   * @param kind the entity kind.
   * @param id the entity id.
   * @return the entity.
   * @throws DatastoreException if there is a datastore error.
   */
  public static Entity getEntity(final Datastore datastore, final String kind, final long id)
      throws DatastoreException {
    return getEntity(datastore, makeKey(kind, id).build());
  }

  /**
   * Gets an entity from the datastore.
   * @param datastore the datastore.
   * @param key the entity key.
   * @return the entity.
   * @throws DatastoreException if there is a datastore error.
   */
  public static Entity getEntity(final Datastore datastore, final Key key)
      throws DatastoreException {

    Entity entity;
    LookupRequest request = LookupRequest.newBuilder().addKey(key).build();
    LookupResponse response = datastore.lookup(request);
    if (response.getMissingCount() == 1) {
      entity = null;
    } else {
      entity = response.getFound(0).getEntity();
    }

    return entity;
  }

  /**
   * Inserts an entity in the datastore.
   * @param datastore the datastore.
   * @param kind the entity kind.
   * @param parent the entity parent. Can be null if not parent.
   * @param properties the entity properties list.
   * @return the entity inserted.
   * @throws DatastoreException if there is a datastore error.
   */
  public static Entity insertEntity(final Datastore datastore, final String kind, final Key parent,
                                    final List<Property> properties) throws DatastoreException {
    Key key;
    if (parent != null) {
      key = makeKey(parent, kind).build();
    } else {
      key = makeKey(kind).build();
    }
    Entity.Builder entityBuilder = Entity.newBuilder().setKey(key).addAllProperty(properties);

    CommitRequest commitRequest = CommitRequest.newBuilder()
        .setMode(CommitRequest.Mode.NON_TRANSACTIONAL).setMutation(Mutation.newBuilder()
        .addInsertAutoId(entityBuilder)).build();
    CommitResponse response = datastore.commit(commitRequest);

    return entityBuilder.setKey(response.getMutationResult().getInsertAutoIdKey(0)).build();
  }

  /**
   * Updates an entity in the datastore. Fails and return null if entity
   * does not exist.
   * @param datastore the datastore.
   * @param key the entity key.
   * @param properties the entity properties list to update.
   * @return the entity update.
   * @throws DatastoreException if there is a datastore error.
   */
  public static boolean updateEntity(final Datastore datastore, final Key key,
                                     final List<Property> properties) throws DatastoreException {

    Entity entity = getEntity(datastore, key);
    if (entity != null) {
      Entity.Builder updatedEntity = Entity.newBuilder(entity);
      updatedEntity.clearProperty();
      for (Property prop : entity.getPropertyList()) {
        boolean found = false;
        for (Property updatedProp : properties) {
          if (updatedProp.getName().equals(prop.getName())) {
            updatedEntity.addProperty(updatedProp);
            found = true;
          }
        }
        if (!found) {
          updatedEntity.addProperty(prop);
        }
      }
      CommitRequest request = CommitRequest.newBuilder()
          .setMode(CommitRequest.Mode.NON_TRANSACTIONAL).setMutation(Mutation.newBuilder()
          .addUpdate(updatedEntity)).build();
      CommitResponse response = datastore.commit(request);
      return response.getMutationResult().getIndexUpdates() > 0;
    }
    return false;
  }

  /**
   * Deletes an entity from the datastore.
   * @param datastore the datastore.
   * @param key the entity key.
   * @return boolean indicating the success.
   * @throws DatastoreException if there is a datastore error.
   */
  public static boolean deleteEntity(final Datastore datastore, final Key key)
      throws DatastoreException {

    CommitRequest request = CommitRequest.newBuilder()
        .setMode(CommitRequest.Mode.NON_TRANSACTIONAL).setMutation(Mutation.newBuilder()
        .addDelete(key)).build();
    CommitResponse response = datastore.commit(request);

    return response.getMutationResult().getIndexUpdates() > 0;
  }

  /**
   * Deletes an entity and its children from the datastore.
   * @param datastore the datastore.
   * @param key the entity key.
   * @return boolean indicating the success.
   * @throws DatastoreException if there is a datastore error.
   */
  public static boolean deleteEntityAndChildren(final Datastore datastore, final Key key)
      throws DatastoreException {

    // Transaction.
    BeginTransactionRequest.Builder transactionRequest = BeginTransactionRequest.newBuilder();
    BeginTransactionResponse transactionResponse =
        datastore.beginTransaction(transactionRequest.build());
    boolean comitting = false;
    boolean res = false;
    try {
      ReadOptions.Builder readOptions = ReadOptions.newBuilder()
          .setTransaction(transactionResponse.getTransaction());

      Query.Builder query = Query.newBuilder();
      Filter ancestorFilter = makeFilter("__key__", PropertyFilter.Operator.HAS_ANCESTOR,
          makeValue(key)).build();
      query.setFilter(makeFilter(ancestorFilter));
      query.addProjection(PropertyExpression.newBuilder().setProperty(PropertyReference.newBuilder()
          .setName("__key__")));
      // Note that this query include the ancestor itself in the results
      RunQueryRequest runQueryRequest = RunQueryRequest.newBuilder().setReadOptions(readOptions)
          .setQuery(query).build();

      List<Key> resultKeys = new ArrayList<>();
      for (EntityResult result : datastore.runQuery(runQueryRequest).getBatch()
          .getEntityResultList()) {
        resultKeys.add(result.getEntity().getKey());
      }

      CommitRequest request = CommitRequest.newBuilder()
          .setTransaction(transactionResponse.getTransaction())
          .setMutation(Mutation.newBuilder().addAllDelete(resultKeys)).build();
      comitting = true;
      CommitResponse response = datastore.commit(request);
      res = response.getMutationResult().getIndexUpdates() > 0;
    } finally {
      if (!comitting) {
        RollbackRequest.Builder rollback = RollbackRequest.newBuilder()
        .setTransaction(transactionResponse.getTransaction());
        datastore.rollback(rollback.build());
      }
    }

    return res;
  }

  /**
   * Gets an entity children from the datastore.
   * @param datastore the datastore.
   * @param key the entity key.
   * @param kind the entity kind. Can be null to return all kinds found.
   * @param includeAncestor if we want to include the ancestor in the results.
   * @return the list of children.
   * @throws DatastoreException if there is a datastore error.
   */
  public static List<Entity> getChildren(final Datastore datastore, final Key key,
                                         final String kind, final boolean includeAncestor)
      throws DatastoreException {
    Query.Builder query = Query.newBuilder();
    Filter ancestorFilter = makeFilter("__key__", PropertyFilter.Operator.HAS_ANCESTOR,
        makeValue(key)).build();
    if (kind != null) {
      query.addKindBuilder().setName(kind);
    }
    if (includeAncestor) {
      query.setFilter(makeFilter(ancestorFilter));
    } else {
      Filter keyFilter = makeFilter("__key__", PropertyFilter.Operator.GREATER_THAN,
          makeValue(key)).build();
      query.setFilter(makeFilter(ancestorFilter, keyFilter));
    }

    RunQueryRequest runQueryRequest = RunQueryRequest.newBuilder().setQuery(query).build();

    List<Entity> results = new ArrayList<>();
    for (EntityResult result : datastore.runQuery(runQueryRequest).getBatch()
        .getEntityResultList()) {
      results.add(result.getEntity());
    }
    return results;
  }

  /**
   * Returns the entity ID.
   * @param entity the entity.
   * @return the entity ID.
   */
  public static long getEntityId(final Entity entity) {
    checkNotNull(entity, "Entity passed was null thus has no key");
    return getEntityId(entity.getKey());
  }

  /**
   * Returns the entity ID.
   * @param entityKey the entity key.
   * @return the entity ID.
   */
  public static long getEntityId(final Key entityKey) {
    checkNotNull(entityKey, "EntityKey passed was null thus has no ID");
    return entityKey.getPathElement(entityKey.getPathElementCount() - 1).getId();
  }

}
