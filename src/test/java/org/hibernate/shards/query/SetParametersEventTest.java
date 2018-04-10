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

package org.hibernate.shards.query;

import org.hibernate.query.Query;
import org.hibernate.shards.defaultmock.QueryDefaultMock;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Maulik Shah
 */
public class SetParametersEventTest {

	@Test
	public void testSetParametersEventValuesTypesWithNoValues() {
		SetParametersEvent event = new SetParametersEvent( null, null );
		final boolean[] called = { false };
		Query query = new QueryDefaultMock() {
			@Override
			public Query setParameters(Object[] values, Type[] types) {
				called[0] = true;
				return null;
			}
		};
		event.onEvent( query );
		assertFalse( called[0] );
	}

	@Test
	public void testSetParametersEventValuesTypesWithValues() {
		SetParametersEvent event = new SetParametersEvent( new Object[] { 1L }, new Type[] { LongType.INSTANCE } );
		final boolean[] called = { false };
		Query query = new QueryDefaultMock() {
			@Override
			public Query setParameter(int position, Object val, Type type) {
				// The mock is using the default implementation from Query interface.
				called[0] = true;
				return null;
			}
		};
		event.onEvent( query );
		assertTrue( called[0] );
	}
}
