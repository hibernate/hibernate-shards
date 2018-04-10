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

package org.hibernate.shards.id;

import java.io.Serializable;

import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.enhanced.TableGenerator;
import org.hibernate.shards.session.ControlSessionProvider;

/**
 * {@link TableGenerator} which uses control shard to store table with hi values.
 *
 * @author Tomislav Nad
 * @see org.hibernate.id.enhanced.TableGenerator
 */
public class ShardedTableGenerator extends TableGenerator implements GeneratorRequiringControlSessionProvider {

	private ControlSessionProvider controlSessionProvider;

	@Override
	public Serializable generate(final SharedSessionContractImplementor session, final Object obj) {
		Serializable id;
		try (SessionImplementor controlSession = controlSessionProvider.openControlSession()) {
			id = superGenerate( controlSession, obj );
		}
		return id;
	}

	@Override
	public void setControlSessionProvider(final ControlSessionProvider provider) {
		controlSessionProvider = provider;
	}

	Serializable superGenerate(final SessionImplementor controlSession, final Object obj) {
		return super.generate( controlSession, obj );
	}
}
