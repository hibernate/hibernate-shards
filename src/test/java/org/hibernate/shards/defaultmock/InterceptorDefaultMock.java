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

package org.hibernate.shards.defaultmock;

import java.io.Serializable;
import java.util.Iterator;

import org.hibernate.CallbackException;
import org.hibernate.EntityMode;
import org.hibernate.Interceptor;
import org.hibernate.Transaction;
import org.hibernate.type.Type;

/**
 * @author maxr@google.com (Max Ross)
 */
public class InterceptorDefaultMock implements Interceptor {

	@Override
	public boolean onLoad(
			Object entity,
			Serializable id,
			Object[] state,
			String[] propertyNames,
			Type[] types) throws CallbackException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean onFlushDirty(
			Object entity,
			Serializable id,
			Object[] currentState,
			Object[] previousState,
			String[] propertyNames,
			Type[] types) throws CallbackException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean onSave(
			Object entity,
			Serializable id,
			Object[] state,
			String[] propertyNames,
			Type[] types) throws CallbackException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onDelete(
			Object entity,
			Serializable id,
			Object[] state,
			String[] propertyNames,
			Type[] types) throws CallbackException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onCollectionRecreate(Object collection, Serializable key)
			throws CallbackException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onCollectionRemove(Object collection, Serializable key)
			throws CallbackException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onCollectionUpdate(Object collection, Serializable key)
			throws CallbackException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void preFlush(Iterator entities) throws CallbackException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void postFlush(Iterator entities) throws CallbackException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Boolean isTransient(Object entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int[] findDirty(
			Object entity,
			Serializable id,
			Object[] currentState,
			Object[] previousState,
			String[] propertyNames,
			Type[] types) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object instantiate(
			String entityName,
			EntityMode entityMode,
			Serializable id) throws CallbackException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getEntityName(Object object) throws CallbackException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getEntity(String entityName, Serializable id)
			throws CallbackException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void afterTransactionBegin(Transaction tx) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void beforeTransactionCompletion(Transaction tx) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void afterTransactionCompletion(Transaction tx) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String onPrepareStatement(String sql) {
		throw new UnsupportedOperationException();
	}
}
