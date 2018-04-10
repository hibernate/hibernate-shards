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

package org.hibernate.shards.query;

import java.util.Collection;

import org.hibernate.Query;
import org.hibernate.shards.session.ShardedSessionException;
import org.hibernate.type.Type;

/**
 * @author Maulik Shah
 */
public class SetParameterListEvent implements QueryEvent {

	private enum CtorType {
		POS_VALS_OBJ_ARR,
		POS_VALS_OBJ_ARR_TYPE,
		NAME_VALS_COLL_TYPE,
		NAME_VALS_COLL,
		NAME_VALS_OBJ_ARR,
		NAME_VALS_OBJ_ARR_TYPE
	}

	private final CtorType ctorType;
	private final int position;
	private final String name;
	private final Collection valsColl;
	private final Object[] valsArr;
	private final Type type;

	private SetParameterListEvent(CtorType ctorType, int position, String name, Collection valsColl, Object[] valsArr, Type type) {
		this.position = position;
		this.ctorType = ctorType;
		this.name = name;
		this.valsColl = valsColl;
		this.valsArr = valsArr;
		this.type = type;
	}

	public SetParameterListEvent(String name, Collection vals, Type type) {
		this( CtorType.NAME_VALS_COLL_TYPE, -1, name, vals, null, type );
	}

	public SetParameterListEvent(String name, Collection vals) {
		this( CtorType.NAME_VALS_COLL, -1, name, vals, null, null );
	}

	public SetParameterListEvent(String name, Object[] vals) {
		this( CtorType.NAME_VALS_OBJ_ARR, -1, name, null, vals, null );
	}

	public SetParameterListEvent(String name, Object[] vals, Type type) {
		this( CtorType.NAME_VALS_OBJ_ARR_TYPE, -1, name, null, vals, type );
	}

	public SetParameterListEvent(int position, Object[] values) {
		this( CtorType.POS_VALS_OBJ_ARR, position, null, null, null, null );
	}

	public SetParameterListEvent(int position, Object[] values, Type type) {
		this( CtorType.POS_VALS_OBJ_ARR_TYPE, position, null, null, null, type );
	}

	public void onEvent(Query query) {
		switch ( ctorType ) {
			case POS_VALS_OBJ_ARR:
				query.setParameterList( position, valsArr );
				break;
			case POS_VALS_OBJ_ARR_TYPE:
				query.setParameterList( position, valsArr, type );
			case NAME_VALS_COLL_TYPE:
				query.setParameterList( name, valsColl, type );
				break;
			case NAME_VALS_COLL:
				query.setParameterList( name, valsColl );
				break;
			case NAME_VALS_OBJ_ARR:
				query.setParameterList( name, valsArr );
				break;
			case NAME_VALS_OBJ_ARR_TYPE:
				query.setParameterList( name, valsArr, type );
				break;
			default:
				throw new ShardedSessionException(
						"Unknown ctor type in SetParameterListEvent: " + ctorType
				);
		}
	}

}
