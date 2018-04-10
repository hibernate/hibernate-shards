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

package org.hibernate.shards.strategy.exit;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.criterion.AvgProjection;
import org.hibernate.criterion.Projections;
import org.hibernate.shards.util.Lists;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Maulik Shah
 */
public class AggregateExitOperationTest {

	private List<Object> data;

	private class MyInt implements Comparable {
		private final Integer i;

		MyInt(int i) {
			this.i = i;
		}

		public Number getValue() {
			return i;
		}

		@Override
		public int compareTo(Object o) {
			MyInt i = (MyInt) o;
			return (Integer) getValue() - (Integer) i.getValue();
		}
	}

	@Before
	public void setUp() {
		data = Lists.newArrayList();
		for ( int i = 0; i < 6; i++ ) {
			if ( i == 4 ) {
				data.add( null );
			}
			else {
				data.add( new MyInt( i ) );
			}
		}
	}

	@Test
	public void testCtor() {
		try {
			new AggregateExitOperation( new AvgProjection( "foo" ) );
			fail();
		}
		catch (IllegalArgumentException e) {
			// good
		}
		try {
			new AggregateExitOperation( new AvgProjection( "foo" ) );
			fail();
		}
		catch (IllegalArgumentException e) {
			// good
		}

		new AggregateExitOperation( Projections.max( "foo" ) );
		new AggregateExitOperation( Projections.min( "foo" ) );
		new AggregateExitOperation( Projections.sum( "foo" ) );
	}

	@Test
	public void testSum() {
		AggregateExitOperation exitOp = new AggregateExitOperation( Projections.sum( "value" ) );

		List<Object> result = exitOp.apply( data );
		assertEquals( new BigDecimal( 11.0 ), result.get( 0 ) );
	}

	@Test
	public void testMax() {
		AggregateExitOperation exitOp = new AggregateExitOperation( Projections.max( "value" ) );

		List<Object> result = exitOp.apply( data );
		assertEquals( 5, ( (MyInt) result.get( 0 ) ).getValue() );
	}

	@Test
	public void testMin() {
		AggregateExitOperation exitOp = new AggregateExitOperation( Projections.min( "value" ) );

		List<Object> result = exitOp.apply( data );
		assertEquals( 0, ( (MyInt) result.get( 0 ) ).getValue() );
	}
}
