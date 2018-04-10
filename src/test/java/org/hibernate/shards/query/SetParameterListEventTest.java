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

import java.util.Collection;

import org.hibernate.query.Query;
import org.hibernate.shards.defaultmock.QueryDefaultMock;
import org.hibernate.shards.util.Lists;
import org.hibernate.type.Type;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Maulik Shah
 */
public class SetParameterListEventTest extends Assert {

	@Test
	public void testSetParameterListEventNameValsCollType() {
		SetParameterListEvent event = new SetParameterListEvent( null, Lists.newArrayList(), null );
		final boolean[] called = { false };
		Query query = new QueryDefaultMock() {
			@Override
			public Query setParameterList(String name, Collection vals, Type type) {
				called[0] = true;
				return null;
			}
		};
		event.onEvent( query );
		assertTrue( called[0] );
	}

	@Test
	public void testSetParameterListEventNameValsColl() {
		SetParameterListEvent event = new SetParameterListEvent( null, Lists.newArrayList() );
		final boolean[] called = { false };
		Query query = new QueryDefaultMock() {
			@Override
			public Query setParameterList(String name, Collection vals) {
				called[0] = true;
				return null;
			}
		};
		event.onEvent( query );
		assertTrue( called[0] );
	}

	@Test
	public void testSetParameterListEventNameValsArr() {
		SetParameterListEvent event = new SetParameterListEvent( null, new Object[0] );
		final boolean[] called = { false };
		Query query = new QueryDefaultMock() {
			@Override
			public Query setParameterList(String name, Object[] vals) {
				called[0] = true;
				return null;
			}
		};
		event.onEvent( query );
		assertTrue( called[0] );
	}

	@Test
	public void testSetParameterListEventNameValsArrType() {
		SetParameterListEvent event = new SetParameterListEvent( null, new Object[0], null );
		final boolean[] called = { false };
		Query query = new QueryDefaultMock() {
			@Override
			public Query setParameterList(String name, Object[] vals, Type type) {
				called[0] = true;
				return null;
			}
		};
		event.onEvent( query );
		assertTrue( called[0] );
	}
}
