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
 * @author Max Ross <maxr@google.com>
 */
public class ShardDefaultMock implements Shard {

  public SessionFactoryImplementor getSessionFactoryImplementor() {
    throw new UnsupportedOperationException();
  }

  public Session getSession() {
    throw new UnsupportedOperationException();
  }

  public void addOpenSessionEvent(OpenSessionEvent event) {
    throw new UnsupportedOperationException();
  }

  public Session establishSession() {
    throw new UnsupportedOperationException();
  }

  public Criteria getCriteriaById(CriteriaId id) {
    throw new UnsupportedOperationException();
  }

  public void addCriteriaEvent(CriteriaId id, CriteriaEvent event) {
    throw new UnsupportedOperationException();
  }

  public Criteria establishCriteria(ShardedCriteria shardedCriteria) {
    throw new UnsupportedOperationException();
  }

  public Object get(Class<?> clazz, Serializable id) {
    throw new UnsupportedOperationException();
  }

  public Object get(Class<?> clazz, Serializable id, LockMode lockMode) {
    throw new UnsupportedOperationException();
  }

  public Object get(String entityName, Serializable id) {
    throw new UnsupportedOperationException();
  }

  public Object get(String entityName, Serializable id, LockMode lockMode) {
    throw new UnsupportedOperationException();
  }

  public List<Object> list(CriteriaId criteriaId) {
    throw new UnsupportedOperationException();
  }

  public Object uniqueResult(CriteriaId criteriaId) {
    throw new UnsupportedOperationException();
  }

  public Serializable save(Object obj) {
    throw new UnsupportedOperationException();
  }

  public Serializable save(String entityName, Object obj) {
    throw new UnsupportedOperationException();
  }

  public void saveOrUpdate(Object obj) {
    throw new UnsupportedOperationException();
  }

  public void saveOrUpdate(String entityName, Object obj) {
    throw new UnsupportedOperationException();
  }

  public void update(Object object) {
    throw new UnsupportedOperationException();
  }

  public void update(String entityName, Object obj) {
    throw new UnsupportedOperationException();
  }

  public void delete(Object object) {
    throw new UnsupportedOperationException();
  }

  public void delete(String entityName, Object object) {
    throw new UnsupportedOperationException();
  }

  public Set<ShardId> getShardIds() {
    throw new UnsupportedOperationException();
  }

  public Query getQueryById(QueryId queryId) {
    throw new UnsupportedOperationException();
  }

  public void addQueryEvent(QueryId id, QueryEvent event) {
    throw new UnsupportedOperationException();
  }

  public Query establishQuery(ShardedQuery shardedQuery) {
    throw new UnsupportedOperationException();
  }

  public List<Object> list(QueryId queryId) {
    throw new UnsupportedOperationException();
  }

  public Object uniqueResult(QueryId queryId) {
    throw new UnsupportedOperationException();
  }
}
