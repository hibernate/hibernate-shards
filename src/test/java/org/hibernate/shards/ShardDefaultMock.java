/**
 * Copyright (C) 2007 Google Inc.
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 */

package org.hibernate.shards;

import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.shards.criteria.CriteriaEvent;
import org.hibernate.shards.criteria.CriteriaId;
import org.hibernate.shards.criteria.ShardedCriteria;
import org.hibernate.shards.query.QueryEvent;
import org.hibernate.shards.query.QueryId;
import org.hibernate.shards.query.ShardedQuery;
import org.hibernate.shards.session.OpenSessionEvent;

/**
 * @author maxr@google.com (Max Ross)
 *         Tomislav Nad
 */
public class ShardDefaultMock implements Shard {

	@Override
	public SessionFactoryImplementor getSessionFactoryImplementor() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Session getSession() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addOpenSessionEvent(OpenSessionEvent event) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Session establishSession() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Criteria getCriteriaById(CriteriaId id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addCriteriaEvent(CriteriaId id, CriteriaEvent event) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Criteria establishCriteria(ShardedCriteria shardedCriteria) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Object> list(CriteriaId criteriaId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object uniqueResult(CriteriaId criteriaId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<ShardId> getShardIds() {
		throw new UnsupportedOperationException();
	}

	@Override
	public org.hibernate.query.Query getQueryById(QueryId queryId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addQueryEvent(QueryId id, QueryEvent event) {
		throw new UnsupportedOperationException();
	}

	@Override
	public org.hibernate.query.Query establishQuery(ShardedQuery shardedQuery) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <R> List<R> list(QueryId queryId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int executeUpdate(QueryId queryId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <R> R uniqueResult(QueryId queryId) {
		throw new UnsupportedOperationException();
	}
}
