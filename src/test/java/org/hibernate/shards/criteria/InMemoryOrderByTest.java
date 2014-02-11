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
package org.hibernate.shards.criteria;

import junit.framework.TestCase;

import org.hibernate.criterion.Order;

/**
 * @author maxr@google.com (Max Ross)
 */
public class InMemoryOrderByTest extends TestCase {

	public void testTopLevel() {
		InMemoryOrderBy imob = new InMemoryOrderBy( null, Order.asc( "yam" ) );
		assertEquals( "yam", imob.getExpression() );
		assertTrue( imob.isAscending() );

		imob = new InMemoryOrderBy( null, Order.desc( "yam" ) );
		assertEquals( "yam", imob.getExpression() );
		assertFalse( imob.isAscending() );
	}

	public void testSubObject() {
		InMemoryOrderBy imob = new InMemoryOrderBy( "a.b.c", Order.asc( "yam" ) );
		assertEquals( "a.b.c.yam", imob.getExpression() );
		assertTrue( imob.isAscending() );

		imob = new InMemoryOrderBy( "a.b.c", Order.desc( "yam" ) );
		assertEquals( "a.b.c.yam", imob.getExpression() );
		assertFalse( imob.isAscending() );
	}
}
