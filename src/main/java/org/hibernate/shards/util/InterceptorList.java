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
package org.hibernate.shards.util;

import org.hibernate.CallbackException;
import org.hibernate.EntityMode;
import org.hibernate.Interceptor;
import org.hibernate.Transaction;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * {@link Interceptor} implementation that delegates to multiple {@link Interceptor}s.
 *
 * @author maxr@google.com (Max Ross)
 */
public class InterceptorList implements Interceptor {

    private final Collection<Interceptor> interceptors;

    /**
     * Construct an InterceptorList
     *
     * @param interceptors the interceptors to which we'll delegate
     */
    public InterceptorList(final Collection<Interceptor> interceptors) {
        this.interceptors = Lists.newArrayList(interceptors);
    }

    /**
     * {@inheritDoc}
     *
     * @param entity        {@inheritDoc}
     * @param id            {@inheritDoc}
     * @param state         {@inheritDoc}
     * @param propertyNames {@inheritDoc}
     * @param types         {@inheritDoc}
     * @return true if any of the contained interceptors return true, false otherwise
     * @throws CallbackException {@inheritDoc}
     */
    @Override
    public boolean onLoad(final Object entity,
                          final Serializable id,
                          final Object[] state,
                          final String[] propertyNames,
                          final Type[] types) throws CallbackException {

        boolean result = false;
        for (final Interceptor interceptor : interceptors) {
            result |= interceptor.onLoad(entity, id, state, propertyNames, types);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * @param entity        {@inheritDoc}
     * @param id            {@inheritDoc}
     * @param currentState  {@inheritDoc}
     * @param previousState {@inheritDoc}
     * @param propertyNames {@inheritDoc}
     * @param types         {@inheritDoc}
     * @return true if any of the contained interceptors return true, false otherwise
     * @throws CallbackException {@inheritDoc}
     */
    @Override
    public boolean onFlushDirty(final Object entity,
                                final Serializable id,
                                final Object[] currentState,
                                final Object[] previousState,
                                final String[] propertyNames,
                                final Type[] types) throws CallbackException {

        boolean result = false;
        for (final Interceptor interceptor : interceptors) {
            result |= interceptor.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * @param entity        {@inheritDoc}
     * @param id            {@inheritDoc}
     * @param state         {@inheritDoc}
     * @param propertyNames {@inheritDoc}
     * @param types         {@inheritDoc}
     * @return true if any of the contained interceptors return true, false otherwise
     * @throws CallbackException {@inheritDoc}
     */
    @Override
    public boolean onSave(final Object entity,
                          final Serializable id,
                          final Object[] state,
                          final String[] propertyNames,
                          final Type[] types) throws CallbackException {

        boolean result = false;
        for (final Interceptor interceptor : interceptors) {
            result |= interceptor.onSave(entity, id, state, propertyNames, types);
        }
        return result;
    }

    @Override
    public void onDelete(final Object entity,
                         final Serializable id,
                         final Object[] state,
                         final String[] propertyNames,
                         final Type[] types) throws CallbackException {

        for (final Interceptor interceptor : interceptors) {
            interceptor.onDelete(entity, id, state, propertyNames, types);
        }
    }

    @Override
    public void onCollectionRecreate(final Object collection, final Serializable key) throws CallbackException {
        for (final Interceptor interceptor : interceptors) {
            interceptor.onCollectionRecreate(collection, key);
        }
    }

    @Override
    public void onCollectionRemove(final Object collection, final Serializable key) throws CallbackException {
        for (final Interceptor interceptor : interceptors) {
            interceptor.onCollectionRemove(collection, key);
        }
    }

    @Override
    public void onCollectionUpdate(final Object collection, final Serializable key) throws CallbackException {
        for (final Interceptor interceptor : interceptors) {
            interceptor.onCollectionUpdate(collection, key);
        }
    }

    @Override
    public void preFlush(final Iterator entities) throws CallbackException {
        for (final Interceptor interceptor : interceptors) {
            interceptor.preFlush(entities);
        }
    }

    @Override
    public void postFlush(final Iterator entities) throws CallbackException {
        for (final Interceptor interceptor : interceptors) {
            interceptor.postFlush(entities);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param entity {@inheritDoc}
     * @return the first non-null result returned by a contained interceptor, or
     *         null if none of the contained interceptors return a non-null result
     */
    @Override
    public Boolean isTransient(final Object entity) {
        for (final Interceptor interceptor : interceptors) {
            final Boolean result = interceptor.isTransient(entity);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @param entity        {@inheritDoc}
     * @param id            {@inheritDoc}
     * @param currentState  {@inheritDoc}
     * @param previousState {@inheritDoc}
     * @param propertyNames {@inheritDoc}
     * @param types         {@inheritDoc}
     * @return the first non-null result returned by a contained interceptor, or
     *         null if none of the contained interceptors return a non-null result
     */
    @Override
    public int[] findDirty(final Object entity,
                           final Serializable id,
                           final Object[] currentState,
                           final Object[] previousState,
                           final String[] propertyNames,
                           final Type[] types) {

        for (final Interceptor interceptor : interceptors) {
            final int[] result = interceptor.findDirty(entity, id, currentState, previousState, propertyNames, types);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @param entityName {@inheritDoc}
     * @param entityMode {@inheritDoc}
     * @param id         {@inheritDoc}
     * @return the first non-null result returned by a contained interceptor, or
     *         null if none of the contained interceptors return a non-null result
     * @throws CallbackException {@inheritDoc}
     */
    @Override
    public Object instantiate(final String entityName,
                              final EntityMode entityMode,
                              final Serializable id) throws CallbackException {

        for (final Interceptor interceptor : interceptors) {
            final Object result = interceptor.instantiate(entityName, entityMode, id);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @param object {@inheritDoc}
     * @return the first non-null result returned by a contained interceptor, or
     *         null if none of the contained interceptors return a non-null result
     * @throws CallbackException {@inheritDoc}
     */
    @Override
    public String getEntityName(final Object object) throws CallbackException {
        for (final Interceptor interceptor : interceptors) {
            final String result = interceptor.getEntityName(object);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @param entityName {@inheritDoc}
     * @param id         {@inheritDoc}
     * @return the first non-null result returned by a contained interceptor, or
     *         null if none of the contained interceptors return a non-null result
     * @throws CallbackException {@inheritDoc}
     */
    @Override
    public Object getEntity(final String entityName, final Serializable id) throws CallbackException {
        for (final Interceptor interceptor : interceptors) {
            final Object result = interceptor.getEntity(entityName, id);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    @Override
    public void afterTransactionBegin(final Transaction tx) {
        for (final Interceptor interceptor : interceptors) {
            interceptor.afterTransactionBegin(tx);
        }
    }

    @Override
    public void beforeTransactionCompletion(final Transaction tx) {
        for (final Interceptor interceptor : interceptors) {
            interceptor.beforeTransactionCompletion(tx);
        }
    }

    @Override
    public void afterTransactionCompletion(final Transaction tx) {
        for (final Interceptor interceptor : interceptors) {
            interceptor.afterTransactionCompletion(tx);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param sql {@inheritDoc}
     * @return the result of the first contained interceptor that modified the sql,
     *         or the original sql if none of the contained interceptors modified the sql.
     */
    @Override
    public String onPrepareStatement(final String sql) {
        for (final Interceptor interceptor : interceptors) {
            final String modified = interceptor.onPrepareStatement(sql);
            if (!sql.equals(modified)) {
                return modified;
            }
        }
        return sql;
    }

    public Collection<Interceptor> getInnerList() {
        return Collections.unmodifiableCollection(interceptors);
    }
}
