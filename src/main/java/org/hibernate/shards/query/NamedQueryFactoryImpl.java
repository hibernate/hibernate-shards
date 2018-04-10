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

import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;

/**
 * Facilitates use of named queries when using ShardedQuery.
 *
 * @author maxr@google.com (Max Ross)
 */
public class NamedQueryFactoryImpl implements QueryFactory {

	private final String queryName;

	public NamedQueryFactoryImpl(final String queryName) {
		this.queryName = queryName;
	}

	@Override
	public Query createQuery(final Session session) {
		return session.getNamedQuery( queryName );
	}

	@Override
	public NativeQuery createSQLQuery(final Session session) {
		throw new UnsupportedOperationException( "no such thing as named SQLQuery" );
	}
}
