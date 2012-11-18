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

package org.hibernate.shards.strategy;

import org.hibernate.shards.strategy.access.ShardAccessStrategy;
import org.hibernate.shards.strategy.resolution.ShardResolutionStrategy;
import org.hibernate.shards.strategy.selection.ShardSelectionStrategy;

/**
 * Interface to specify Sharding behavior.
 * <p/>
 * This class determines through {@link ShardAccessStrategy}, {@link ShardResolutionStrategy}, and
 * {@link ShardSelectionStrategy} how to read, persist and update entities in the sharded collection of databases.
 * <p/>
 * <p>When given a query the {@link ShardAccessStrategy} will determine how the query will be distributed across
 * the known shards - in sequence, in parallel, or some combination of the two.
 * </p>
 * <p> When looking for an entity with a known Id and type {@link ShardResolutionStrategy} will return all possible shards
 * that this entity might reside on.
 * </p>
 * <p> When given an entity the {@link ShardSelectionStrategy} will determine which shard that object should be persisted
 * or updated on.
 * </p>
 *
 * @author maxr@google.com (Max Ross)
 */
public interface ShardStrategy {

    ShardSelectionStrategy getShardSelectionStrategy();

    ShardResolutionStrategy getShardResolutionStrategy();

    ShardAccessStrategy getShardAccessStrategy();
}
