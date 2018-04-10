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

import java.util.List;

import org.hibernate.criterion.Projections;
import org.hibernate.shards.util.Lists;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Maulik Shah
 */
public class RowCountExitOperationTest {

	@Test(expected = IllegalStateException.class)
	public void testCtor() {
		new RowCountExitOperation( Projections.avg( "foo" ) );
	}

	@Test
	public void testApplyCount() {
		RowCountExitOperation exitOp = new RowCountExitOperation( Projections.rowCount() );

		List<Object> list = Lists.<Object>newArrayList( 1, 2, null, 3 );
		assertEquals( 3, (int) (Integer) exitOp.apply( list ).get( 0 ) );
	}
}
