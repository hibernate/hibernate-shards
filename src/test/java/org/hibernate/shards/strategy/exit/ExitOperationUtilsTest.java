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

package org.hibernate.shards.strategy.exit;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Maulik Shah
 */
public class ExitOperationUtilsTest {

	private class MyInt {

		private final Integer i;
		private final String name;
		private final String rank;
		private MyInt innerMyInt;

		public MyInt(int i, String name, String rank) {
			this.i = i;
			this.name = name;
			this.rank = rank;
		}

		// these private methods, while unused, are used to verify that the method
		// works for private methods
		private MyInt getInnerMyInt() {
			return innerMyInt;
		}

		private void setInnerMyInt(MyInt innerMyInt) {
			this.innerMyInt = innerMyInt;
		}

		private Number getValue() {
			return i;
		}

		private String getName() {
			return name;
		}

		protected String getRank() {
			return rank;
		}
	}

	private class MySubInt extends MyInt {

		public MySubInt(int i, String name, String rank) {
			super( i, name, rank );
		}
	}

	@Test(expected = RuntimeException.class)
	public void testGetPropertyValue() throws Exception {
		final MyInt myInt = new MySubInt( 1, "one", "a" );
		myInt.setInnerMyInt( new MySubInt( 5, "five", "b" ) );

		assertEquals( 1, ExitOperationUtils.getPropertyValue( myInt, "value" ) );
		assertEquals( "one", ExitOperationUtils.getPropertyValue( myInt, "name" ) );
		assertEquals( "a", ExitOperationUtils.getPropertyValue( myInt, "rank" ) );

		assertEquals( 5, ExitOperationUtils.getPropertyValue( myInt, "innerMyInt.value" ) );
		assertEquals( "five", ExitOperationUtils.getPropertyValue( myInt, "innerMyInt.name" ) );
		assertEquals( "b", ExitOperationUtils.getPropertyValue( myInt, "innerMyInt.rank" ) );

		ExitOperationUtils.getPropertyValue( myInt, "innerMyInt.doesNotExist" );
	}
}
