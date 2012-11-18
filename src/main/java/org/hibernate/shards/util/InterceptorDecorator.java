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
import java.util.Iterator;

/**
 * {@link Interceptor} implementation that delegates all calls to an inner
 * {@link Interceptor}.
 *
 * @author maxr@google.com (Max Ross)
 */
public class InterceptorDecorator implements Interceptor {

    protected final Interceptor delegate;

    public InterceptorDecorator(final Interceptor delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean onLoad(final Object entity,
                          final Serializable id,
                          final Object[] state,
                          final String[] propertyNames,
                          final Type[] types) throws CallbackException {

        return delegate.onLoad(entity, id, state, propertyNames, types);
    }

    @Override
    public boolean onFlushDirty(final Object entity,
                                final Serializable id,
                                final Object[] currentState,
                                final Object[] previousState,
                                final String[] propertyNames,
                                final Type[] types) throws CallbackException {

        return delegate.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
    }

    @Override
    public boolean onSave(final Object entity,
                          final Serializable id,
                          final Object[] state,
                          final String[] propertyNames,
                          final Type[] types) throws CallbackException {

        return delegate.onSave(entity, id, state, propertyNames, types);
    }

    @Override
    public void onDelete(final Object entity,
                         final Serializable id,
                         final Object[] state,
                         final String[] propertyNames,
                         final Type[] types) throws CallbackException {

        delegate.onDelete(entity, id, state, propertyNames, types);
    }

    @Override
    public void onCollectionRecreate(final Object collection, final Serializable key) throws CallbackException {
        delegate.onCollectionRecreate(collection, key);
    }

    @Override
    public void onCollectionRemove(final Object collection, final Serializable key) throws CallbackException {
        delegate.onCollectionRemove(collection, key);
    }

    @Override
    public void onCollectionUpdate(final Object collection, final Serializable key) throws CallbackException {
        delegate.onCollectionUpdate(collection, key);
    }

    @Override
    public void preFlush(final Iterator entities) throws CallbackException {
        delegate.preFlush(entities);
    }

    @Override
    public void postFlush(final Iterator entities) throws CallbackException {
        delegate.postFlush(entities);
    }

    @Override
    public Boolean isTransient(final Object entity) {
        return delegate.isTransient(entity);
    }

    @Override
    public int[] findDirty(final Object entity,
                           final Serializable id,
                           final Object[] currentState,
                           final Object[] previousState,
                           final String[] propertyNames,
                           final Type[] types) {

        return delegate.findDirty(entity, id, currentState, previousState, propertyNames, types);
    }

    @Override
    public Object instantiate(final String entityName,
                              final EntityMode entityMode,
                              final Serializable id) throws CallbackException {

        return delegate.instantiate(entityName, entityMode, id);
    }

    @Override
    public String getEntityName(final Object object) throws CallbackException {
        return delegate.getEntityName(object);
    }

    @Override
    public Object getEntity(final String entityName, final Serializable id) throws CallbackException {
        return delegate.getEntity(entityName, id);
    }

    @Override
    public void afterTransactionBegin(final Transaction tx) {
        delegate.afterTransactionBegin(tx);
    }

    @Override
    public void beforeTransactionCompletion(final Transaction tx) {
        delegate.beforeTransactionCompletion(tx);
    }

    @Override
    public void afterTransactionCompletion(final Transaction tx) {
        delegate.afterTransactionCompletion(tx);
    }

    @Override
    public String onPrepareStatement(final String sql) {
        return delegate.onPrepareStatement(sql);
    }

    public Interceptor getDelegate() {
        return delegate;
    }
}
