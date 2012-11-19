package org.hibernate.shards.session;

import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.shards.util.Preconditions;
import org.hibernate.type.Type;

import java.io.Serializable;

/**
 * Copyright (C) 2007 Google Inc.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 */

/**
 * Interceptor that sets the {@link org.hibernate.shards.ShardId} of any object
 * that implements the {@link ShardAware} interface and does already know its
 * {@link org.hibernate.shards.ShardId} when the object is saved or loaded.
 *
 * @author maxr@google.com (Max Ross)
 */
public class ShardAwareInterceptor extends EmptyInterceptor {

    private final ShardIdResolver shardIdResolver;

    public ShardAwareInterceptor(final ShardIdResolver shardIdResolver) {
        Preconditions.checkNotNull(shardIdResolver);
        this.shardIdResolver = shardIdResolver;
    }

    @Override
    public boolean onLoad(final Object entity,
                          final Serializable id,
                          final Object[] state,
                          final String[] propertyNames, Type[] types) throws CallbackException {

        return setShardId(entity);
    }

    @Override
    public boolean onSave(final Object entity,
                          final Serializable id,
                          final Object[] state,
                          final String[] propertyNames, Type[] types) {

        return setShardId(entity);
    }

    boolean setShardId(final Object entity) {
        boolean result = false;
        if (entity instanceof ShardAware) {
            final ShardAware shardAware = (ShardAware) entity;
            if (shardAware.getShardId() == null) {
                shardAware.setShardId(shardIdResolver.getShardIdForObject(entity));
                result = true;
            }
        }
        return result;
    }
}
