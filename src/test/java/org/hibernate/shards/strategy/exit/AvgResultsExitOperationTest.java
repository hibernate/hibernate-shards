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

import java.util.Collections;
import java.util.List;

import org.junit.Test;

import org.hibernate.shards.util.Lists;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * @author maxr@google.com (Max Ross)
 */
public class AvgResultsExitOperationTest {

	@Test
	public void testEmptyList() {
		final AvgResultsExitOperation op = new AvgResultsExitOperation();
		final List<Object> result = op.apply( Collections.emptyList() );
		assertEquals( 1, result.size() );
		assertNull( result.get( 0 ) );
	}

	@Test
	public void testSingleResult() {
		final AvgResultsExitOperation op = new AvgResultsExitOperation();

		Object[] objArr = {null, 3};
		List<Object> result = op.apply( Collections.singletonList( (Object) objArr ) );
		assertEquals( 1, result.size() );
		assertNull( result.get( 0 ) );

		objArr[0] = 9.0;
		result = op.apply( Collections.singletonList( (Object) objArr ) );
		assertEquals( 1, result.size() );
		assertEquals( 9.0, result.get( 0 ) );
	}

	@Test
	public void testMultipleResults() {
		final AvgResultsExitOperation op = new AvgResultsExitOperation();

		Object[] objArr1 = {null, 3};
		Object[] objArr2 = {2.5, 2};
		List<Object> result = op.apply( Lists.<Object>newArrayList( objArr1, objArr2 ) );
		assertEquals( 1, result.size() );
		assertEquals( 2.5, result.get( 0 ) );

		objArr1[0] = 2.0;
		result = op.apply( Lists.<Object>newArrayList( objArr1, objArr2 ) );
		assertEquals( 1, result.size() );
		assertEquals( 2.2, result.get( 0 ) );
	}

	@Test
	public void testBadInput() {
		final AvgResultsExitOperation op = new AvgResultsExitOperation();

		Object[] objArr = {null};
		try {
			op.apply( Collections.singletonList( (Object) objArr ) );
			fail( "expected IllegalStateException" );
		}
		catch (IllegalStateException rte) {
			// good
		}

		final Object obj = new Object();
		try {
			op.apply( Collections.singletonList( obj ) );
			fail( "expected IllegalStateException" );
		}
		catch (IllegalStateException rte) {
			// good
		}
	}
}
