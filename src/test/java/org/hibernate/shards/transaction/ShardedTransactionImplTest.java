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

package org.hibernate.shards.transaction;

import java.util.List;
import javax.transaction.Synchronization;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.TransactionException;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.hibernate.shards.Shard;
import org.hibernate.shards.ShardDefaultMock;
import org.hibernate.shards.ShardedTransactionDefaultMock;
import org.hibernate.shards.defaultmock.SessionDefaultMock;
import org.hibernate.shards.defaultmock.ShardedSessionImplementorDefaultMock;
import org.hibernate.shards.util.Lists;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Tomislav Nad
 */
public class ShardedTransactionImplTest {

	private ShardedTransactionImpl sti;
	private TransactionStub transaction1;

	private class TransactionStub extends ShardedTransactionDefaultMock {
		public boolean fail = false;
		public boolean wasCommitted = false;

		@Override
		public void setupTransaction(org.hibernate.Session session) {
		}

		@Override
		public void begin() throws HibernateException {
			if ( fail ) {
				throw new TransactionException( "failed" );
			}
		}

		@Override
		public void commit() throws HibernateException {
			if ( fail ) {
				throw new TransactionException( "failed" );
			}
			wasCommitted = true;
		}

		@Override
		public void rollback() throws HibernateException {
			if ( fail ) {
				throw new TransactionException( "failed" );
			}
		}

		@Override
		public boolean isActive() throws HibernateException {
			return true;
		}

		@Override
		public void registerSynchronization(Synchronization synchronization)
				throws HibernateException {
		}

		@Override
		public void setTimeout(int seconds) {
		}
	}

	private static class MockSession extends SessionDefaultMock {
		private Transaction transaction;

		MockSession(Transaction t) {
			transaction = t;
		}

		@Override
		public Transaction getTransaction() {
			return transaction;
		}
	}

	private static class MockShard extends ShardDefaultMock {
		private Session session;

		MockShard(Session s) {
			session = s;
		}

		@Override
		public Session getSession() {
			return session;
		}
	}

	private static class MockShardedSessionImplementor extends ShardedSessionImplementorDefaultMock {
		private List<Shard> shards;

		MockShardedSessionImplementor(List<Shard> shards) {
			this.shards = shards;
		}

		@Override
		public List<Shard> getShards() {
			return shards;
		}
	}

	@Before
	public void setUp() {
		TransactionStub transaction1 = new TransactionStub();
		this.transaction1 = new TransactionStub();
		List<Shard> shards = Lists.newArrayList();
		shards.add( new MockShard( new MockSession( transaction1 ) ) );
		shards.add( new MockShard( new MockSession( this.transaction1 ) ) );
		sti = new ShardedTransactionImpl( new MockShardedSessionImplementor( shards ) );
	}

	@Test
	public void testBeginSimple() {
		sti.begin();
		assertTrue( sti.isActive() );
		assertFalse( sti.getStatus().isOneOf( TransactionStatus.COMMITTED ) );
		assertFalse( sti.getStatus().isOneOf( TransactionStatus.ROLLED_BACK ) );

		// test double begin
		sti.begin();
		assertTrue( sti.isActive() );
		assertFalse( sti.getStatus().isOneOf( TransactionStatus.COMMITTED ) );
		assertFalse( sti.getStatus().isOneOf( TransactionStatus.ROLLED_BACK ) );

		// test begin after commit failed
		transaction1.fail = true;
		try {
			sti.commit();
			fail();
		}
		catch (HibernateException he) {
			// good
		}
		transaction1.fail = false;
		try {
			sti.begin();
			fail();
		}
		catch (HibernateException he) {
			// good
		}
	}

	@Test
	public void testBeginWithOneFailedTransaction() {
		transaction1.fail = true;
		try {
			sti.begin();
			fail();
		}
		catch (HibernateException he) {
			assertFalse( sti.isActive() );
		}

		transaction1.fail = false;
		sti.begin();
		assertTrue( sti.isActive() );
		assertFalse( sti.getStatus().isOneOf( TransactionStatus.COMMITTED ) );
		assertFalse( sti.getStatus().isOneOf( TransactionStatus.ROLLED_BACK ) );
	}

	@Test
	public void testCommitSimple() {
		try {
			sti.commit();
			fail();
		}
		catch (HibernateException he) {
			// good
		}

		sti.begin();
		sti.commit();
		assertTrue( sti.getStatus().isOneOf( TransactionStatus.COMMITTED ) );
		assertFalse( sti.isActive() );
	}

	@Test
	public void testCommitWithOneFailedTransaction() {
		sti.begin();
		transaction1.fail = true;
		try {
			sti.commit();
			fail();
		}
		catch (HibernateException he) {
			assertFalse( sti.getStatus().isOneOf( TransactionStatus.COMMITTED ) );
			assertTrue( he.getCause() instanceof HibernateException );
		}
	}

	@Test
	public void testRollbackSimple() {
		try {
			sti.rollback();
			fail();
		}
		catch (HibernateException he) {
			// good
			assertFalse( sti.getStatus().isOneOf( TransactionStatus.ROLLED_BACK ) );
		}

		sti.begin();
		sti.rollback();
		assertTrue( sti.getStatus().isOneOf( TransactionStatus.ROLLED_BACK ) );

		sti.commit();
		try {
			sti.rollback();
			fail();
		}
		catch (HibernateException he) {
			// good
			assertTrue( sti.getStatus().isOneOf( TransactionStatus.ROLLED_BACK ) );
		}

		sti.begin();
		transaction1.fail = true;
		try {
			sti.commit();
		}
		catch (HibernateException he) {
			assertFalse( sti.getStatus().isOneOf( TransactionStatus.ROLLED_BACK ) );
			assertTrue( he.getCause() instanceof HibernateException );
			sti.rollback();
			assertTrue( sti.getStatus().isOneOf( TransactionStatus.ROLLED_BACK ) );
		}
	}

	@Test
	public void testRollbackWithOneFailedTransaction() {
		sti.begin();
		transaction1.fail = true;
		try {
			sti.rollback();
		}
		catch (HibernateException he) {
			assertFalse( sti.getStatus().isOneOf( TransactionStatus.ROLLED_BACK ) );
			assertTrue( he.getCause() instanceof HibernateException );
		}
	}

	@Test
	public void testMultipleIterations() {
		sti.begin();
		sti.commit();

		sti.begin();
		assertTrue( sti.isActive() );
		assertFalse( sti.getStatus().isOneOf( TransactionStatus.COMMITTED ) );
		sti.commit();
		assertTrue( sti.getStatus().isOneOf( TransactionStatus.COMMITTED ) );
	}
}
