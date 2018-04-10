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

import javax.transaction.Synchronization;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.resource.transaction.spi.TransactionStatus;

/**
 * @author Tomislav Nad
 */
public class ShardedTransactionDefaultMock implements ShardedTransaction {

	@Override
	public void setupTransaction(Session session) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void begin() throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void commit() throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void rollback() throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setRollbackOnly() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean getRollbackOnly() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isActive() throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public TransactionStatus getStatus() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void registerSynchronization(Synchronization synchronization)
			throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setTimeout(int seconds) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getTimeout() {
		throw new UnsupportedOperationException();
	}
}
