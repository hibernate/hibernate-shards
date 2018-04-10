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

package org.hibernate.shards.id;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.shards.defaultmock.SessionImplementorDefaultMock;
import org.hibernate.shards.session.ControlSessionProvider;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * @author maxr@google.com (Max Ross)
 */
public class ShardedTableGeneratorTest {

	@Test
	public void testGenerate() {
		final SessionImplementor controlSessionToReturn = new MySession();
		ControlSessionProvider provider = () -> controlSessionToReturn;
		final SessionImplementor session = new SessionImplementorDefaultMock();
		ShardedTableGenerator gen = new ShardedTableGenerator() {
			@Override
			Serializable superGenerate(SessionImplementor controlSession, Object obj) {
				assertSame( controlSessionToReturn, controlSession );
				return 33;
			}
		};
		gen.setControlSessionProvider( provider );
		assertEquals( 33, gen.generate( session, null ) );
	}

	private static final class MySession extends SessionImplementorDefaultMock {

		@Override
		public void close() throws HibernateException {
		}
	}
}
