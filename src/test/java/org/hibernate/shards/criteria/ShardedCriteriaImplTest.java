/**
 * Copyright (C) 2008 Google Inc.
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
package org.hibernate.shards.criteria;

import java.util.List;

import junit.framework.TestCase;

import org.hibernate.Criteria;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.shards.Shard;
import org.hibernate.shards.ShardDefaultMock;
import org.hibernate.shards.strategy.access.ShardAccessStrategy;
import org.hibernate.shards.strategy.access.ShardAccessStrategyDefaultMock;
import org.hibernate.shards.util.Lists;

/**
 * @author maxr@google.com (Max Ross)
 */
public class ShardedCriteriaImplTest extends TestCase {

	public void testSetFirstResultAfterMaxResult() {
		CriteriaId id = new CriteriaId( 0 );
		final List<CriteriaEvent> events = Lists.newArrayList();
		Shard shard = new ShardDefaultMock() {
			@Override
			public SessionFactoryImplementor getSessionFactoryImplementor() {
				return null;
			}

			@Override
			public Criteria getCriteriaById(CriteriaId id) {
				return null;
			}

			public void addCriteriaEvent(CriteriaId id, CriteriaEvent event) {
				events.add( event );
			}
		};

		final List<Shard> shards = Lists.newArrayList( shard );
		final CriteriaFactory cf = new CriteriaFactoryDefaultMock();
		final ShardAccessStrategy sas = new ShardAccessStrategyDefaultMock();
		final ShardedCriteriaImpl crit = new ShardedCriteriaImpl( id, shards, cf, sas );
		crit.setMaxResults( 2 );
		assertEquals( 2, crit.getCriteriaCollector().getMaxResults().intValue() );
		assertNull( crit.getCriteriaCollector().getFirstResult() );
		assertEquals( 1, events.size() );
		assertEquals( 2, ((SetMaxResultsEvent) events.get( 0 )).getMaxResults() );

		crit.setMaxResults( 5 );
		assertEquals( 5, crit.getCriteriaCollector().getMaxResults().intValue() );
		assertNull( crit.getCriteriaCollector().getFirstResult() );
		assertEquals( 2, events.size() );
		assertEquals( 2, ((SetMaxResultsEvent) events.get( 0 )).getMaxResults() );
		assertEquals( 5, ((SetMaxResultsEvent) events.get( 1 )).getMaxResults() );

		crit.setFirstResult( 2 );
		assertEquals( 5, crit.getCriteriaCollector().getMaxResults().intValue() );
		assertEquals( 2, crit.getCriteriaCollector().getFirstResult().intValue() );
		assertEquals( 3, events.size() );
		assertEquals( 2, ((SetMaxResultsEvent) events.get( 0 )).getMaxResults() );
		assertEquals( 5, ((SetMaxResultsEvent) events.get( 1 )).getMaxResults() );
		assertEquals( 7, ((SetMaxResultsEvent) events.get( 2 )).getMaxResults() );

		crit.setFirstResult( 2 );
		assertEquals( 5, crit.getCriteriaCollector().getMaxResults().intValue() );
		assertEquals( 2, crit.getCriteriaCollector().getFirstResult().intValue() );
		assertEquals( 4, events.size() );
		assertEquals( 2, ((SetMaxResultsEvent) events.get( 0 )).getMaxResults() );
		assertEquals( 5, ((SetMaxResultsEvent) events.get( 1 )).getMaxResults() );
		assertEquals( 7, ((SetMaxResultsEvent) events.get( 2 )).getMaxResults() );
		assertEquals( 7, ((SetMaxResultsEvent) events.get( 3 )).getMaxResults() );

		crit.setFirstResult( 1 );
		assertEquals( 5, crit.getCriteriaCollector().getMaxResults().intValue() );
		assertEquals( 1, crit.getCriteriaCollector().getFirstResult().intValue() );
		assertEquals( 5, events.size() );
		assertEquals( 2, ((SetMaxResultsEvent) events.get( 0 )).getMaxResults() );
		assertEquals( 5, ((SetMaxResultsEvent) events.get( 1 )).getMaxResults() );
		assertEquals( 7, ((SetMaxResultsEvent) events.get( 2 )).getMaxResults() );
		assertEquals( 7, ((SetMaxResultsEvent) events.get( 3 )).getMaxResults() );
		assertEquals( 6, ((SetMaxResultsEvent) events.get( 4 )).getMaxResults() );

		crit.setFirstResult( 0 );
		assertEquals( 5, crit.getCriteriaCollector().getMaxResults().intValue() );
		assertEquals( 0, crit.getCriteriaCollector().getFirstResult().intValue() );
		assertEquals( 6, events.size() );
		assertEquals( 2, ((SetMaxResultsEvent) events.get( 0 )).getMaxResults() );
		assertEquals( 5, ((SetMaxResultsEvent) events.get( 1 )).getMaxResults() );
		assertEquals( 7, ((SetMaxResultsEvent) events.get( 2 )).getMaxResults() );
		assertEquals( 7, ((SetMaxResultsEvent) events.get( 3 )).getMaxResults() );
		assertEquals( 6, ((SetMaxResultsEvent) events.get( 4 )).getMaxResults() );
		assertEquals( 5, ((SetMaxResultsEvent) events.get( 5 )).getMaxResults() );
	}

	public void testSetMaxResultAfterFirstResult() {
		CriteriaId id = new CriteriaId( 0 );
		final List<CriteriaEvent> events = Lists.newArrayList();
		Shard shard = new ShardDefaultMock() {
			@Override
			public SessionFactoryImplementor getSessionFactoryImplementor() {
				return null;
			}

			@Override
			public Criteria getCriteriaById(CriteriaId id) {
				return null;
			}

			public void addCriteriaEvent(CriteriaId id, CriteriaEvent event) {
				events.add( event );
			}
		};
		List<Shard> shards = Lists.newArrayList( shard );
		CriteriaFactory cf = new CriteriaFactoryDefaultMock();
		ShardAccessStrategy sas = new ShardAccessStrategyDefaultMock();
		ShardedCriteriaImpl crit = new ShardedCriteriaImpl( id, shards, cf, sas );

		crit.setFirstResult( 3 );
		assertEquals( 3, crit.getCriteriaCollector().getFirstResult().intValue() );
		assertNull( crit.getCriteriaCollector().getMaxResults() );
		assertEquals( 0, events.size() );

		crit.setFirstResult( 5 );
		assertEquals( 5, crit.getCriteriaCollector().getFirstResult().intValue() );
		assertNull( crit.getCriteriaCollector().getMaxResults() );
		assertEquals( 0, events.size() );

		crit.setMaxResults( 2 );
		crit.setMaxResults( 3 );
		assertEquals( 5, crit.getCriteriaCollector().getFirstResult().intValue() );
		assertEquals( 3, crit.getCriteriaCollector().getMaxResults().intValue() );
		assertEquals( 2, events.size() );
		assertEquals( 7, ((SetMaxResultsEvent) events.get( 0 )).getMaxResults() );
		assertEquals( 8, ((SetMaxResultsEvent) events.get( 1 )).getMaxResults() );
	}
}
