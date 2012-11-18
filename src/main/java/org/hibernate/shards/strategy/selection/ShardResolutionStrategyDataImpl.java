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

package org.hibernate.shards.strategy.selection;

import java.io.Serializable;

/**
 * @author maxr@google.com (Max Ross)
 */
public final class ShardResolutionStrategyDataImpl implements ShardResolutionStrategyData {

    private final String entityName;
    private final Serializable id;

    public ShardResolutionStrategyDataImpl(final Class<?> clazz, final Serializable id) {
        this(clazz.getName(), id);
    }

    public ShardResolutionStrategyDataImpl(final String entityName, final Serializable id) {
        this.entityName = entityName;
        this.id = id;
    }

    @Override
    public String getEntityName() {
        return entityName;
    }

    @Override
    public Serializable getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ShardResolutionStrategyDataImpl that = (ShardResolutionStrategyDataImpl) o;

        if (!entityName.equals(that.entityName)) {
            return false;
        }
        if (!id.equals(that.id)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        result = entityName.hashCode();
        result = 31 * result + id.hashCode();
        return result;
    }
}
