/**
 * Copyright (C) 2007 Google Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 */

package org.hibernate.shards;

import org.hibernate.shards.criteria.CriteriaEvent;
import org.hibernate.shards.criteria.CriteriaId;
import org.hibernate.shards.criteria.ShardedCriteria;
import org.hibernate.shards.query.QueryEvent;
import org.hibernate.shards.query.QueryId;
import org.hibernate.shards.query.ShardedQuery;
import org.hibernate.shards.session.OpenSessionEvent;

import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.classic.Session;
import org.hibernate.engine.SessionFactoryImplementor;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Interface representing a Shard.  A shard is a physical partition (as opposed
 * to a virtual partition).  Shards know how to lazily instantiate Sessions
 * and apply {@link OpenSessionEvent}s, {@link CriteriaEvent}s, and {@link QueryEvent}s.
 * Anybody else have a nagging suspicion this can get folded into the Session
 * itself?
 *
 * @author Max Ross <maxr@google.com>
 */
public interface Shard {

  /**
   * @return the SessionFactoryImplementor that owns the Session associated with this Shard
   */
  SessionFactoryImplementor getSessionFactoryImplementor();

  /**
   * @return the Session associated with this Shard.  Will return null if
   * the Session has not yet been established.
   */
  /* @Nullable */ Session getSession();

  /**
   * @param event the event to add
   */
  void addOpenSessionEvent(OpenSessionEvent event);

  /**
   * @return establish a Session using the SessionFactoryImplementor associated
   * with this Shard and apply any OpenSessionEvents that have been added.  If
   * the Session has already been established just return it.
   */
  Session establishSession();

  /**
   * @param id the id of the Criteria
   * @return the Critieria uniquely identified by the given id (unique to the Shard)
   */
  Criteria getCriteriaById(CriteriaId id);

  /**
   * @param id the id of the Criteria with which the event should be associated
   * @param event the event to add
   */
  void addCriteriaEvent(CriteriaId id, CriteriaEvent event);

  /**
   * @param shardedCriteria  the ShardedCriteria for which this Shard should
   * create an actual {@link Criteria} object.
   * @return a Criteria for the given ShardedCriteria
   */
  Criteria establishCriteria(ShardedCriteria shardedCriteria);

  /**
   * @see Session#get(Class, Serializable)
   */
  Object get(Class<?> clazz, Serializable id);

  /**
   * @see Session#get(Class, Serializable, LockMode)
   */
  Object get(Class<?> clazz, Serializable id, LockMode lockMode);

  /**
   * @see Session#get(String, Serializable)
   */
  Object get(String entityName, Serializable id);

  /**
   * @see Session#get(String, Serializable, LockMode)
   */
  Object get(String entityName, Serializable id, LockMode lockMode);

  /**
   * @see Criteria#list()
   */
  List<Object> list(CriteriaId criteriaId);

  /**
   * @see Criteria#uniqueResult()
   */
  Object uniqueResult(CriteriaId criteriaId);

  /**
   * @see Session#save(String, Object)
   */
  Serializable save(String entityName, Object obj);

  /**
   * @see Session#saveOrUpdate(Object)
   */
  void saveOrUpdate(Object obj);

  /**
   * @see Session#saveOrUpdate(String, Object)
   */
  void saveOrUpdate(String entityName, Object obj);

  /**
   * @see Session#update(Object)
   */
  void update(Object object);

  /**
   * @see Session#update(String, Object)
   */
  void update(String entityName, Object obj);

  /**
   * @see Session#delete(Object)
   */
  void delete(Object object);

  /**
   * @see Session#delete(String, Object)
   */
  void delete(String entityName, Object object);

  /**
   * @return the ids of the virtual shards that are mapped to this physical shard.
   * The returned Set is unmodifiable.
   */
  Set<ShardId> getShardIds();

  /**
   * @param queryId the id of the Query
   * @return the Query uniquely identified by the given id (unique to the Shard)
   */
  Query getQueryById(QueryId queryId);

  /**
   * @param id the id of the Query with which the event should be associated
   * @param event the event to add
   */
  void addQueryEvent(QueryId id, QueryEvent event);

  /**
   * @param shardedQuery  the ShardedQuery for which this Shard should
   * create an actual {@link Query} object.
   * @return a Query for the given ShardedQuery
   */
  Query establishQuery(ShardedQuery shardedQuery);

  /**
   * @see Query#list()
   */
  List<Object> list(QueryId queryId);

  /**
   * @see Query#uniqueResult()
   */
  Object uniqueResult(QueryId queryId);
}
