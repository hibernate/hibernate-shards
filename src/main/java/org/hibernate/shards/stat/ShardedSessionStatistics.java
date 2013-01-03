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

package org.hibernate.shards.stat;

import java.util.Set;

import org.hibernate.Session;
import org.hibernate.engine.spi.CollectionKey;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.shards.Shard;
import org.hibernate.shards.engine.ShardedSessionImplementor;
import org.hibernate.shards.session.OpenSessionEvent;
import org.hibernate.shards.util.Sets;
import org.hibernate.stat.SessionStatistics;

/**
 * Sharded implementation of the SessionStatistics that aggregates the
 * statistics of all underlying individual SessionStatistics.
 *
 * @author Tomislav Nad
 */
public class ShardedSessionStatistics implements SessionStatistics {

	private final Set<SessionStatistics> sessionStatistics;

	public ShardedSessionStatistics(final ShardedSessionImplementor session) {
		sessionStatistics = Sets.newHashSet();
		for ( final Shard s : session.getShards() ) {
			if ( s.getSession() != null ) {
				sessionStatistics.add( s.getSession().getStatistics() );
			}
			else {
				final OpenSessionEvent ose = new OpenSessionEvent() {
					@Override
					public void onOpenSession(Session session) {
						sessionStatistics.add( session.getStatistics() );
					}
				};
				s.addOpenSessionEvent( ose );
			}
		}
	}

	@Override
	public int getEntityCount() {
		int count = 0;
		for ( final SessionStatistics s : sessionStatistics ) {
			count += s.getEntityCount();
		}
		return count;
	}

	@Override
	public int getCollectionCount() {
		int count = 0;
		for ( final SessionStatistics s : sessionStatistics ) {
			count += s.getCollectionCount();
		}
		return count;
	}

	@Override
	public Set<EntityKey> getEntityKeys() {
		final Set<EntityKey> entityKeys = Sets.newHashSet();
		for ( SessionStatistics s : sessionStatistics ) {
			@SuppressWarnings("unchecked")
			final Set<EntityKey> shardEntityKeys = (Set<EntityKey>) s.getEntityKeys();
			entityKeys.addAll( shardEntityKeys );
		}
		return entityKeys;
	}

	@Override
	public Set<CollectionKey> getCollectionKeys() {
		final Set<CollectionKey> collectionKeys = Sets.newHashSet();
		for ( final SessionStatistics s : sessionStatistics ) {
			@SuppressWarnings("unchecked")
			final Set<CollectionKey> shardCollectionKeys = (Set<CollectionKey>) s.getCollectionKeys();
			collectionKeys.addAll( shardCollectionKeys );
		}
		return collectionKeys;
	}
}
