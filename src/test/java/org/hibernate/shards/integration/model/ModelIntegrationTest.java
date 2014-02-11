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
package org.hibernate.shards.integration.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import org.hibernate.shards.PermutationHelper;
import org.hibernate.shards.integration.BaseShardingIntegrationTestCase;
import org.hibernate.shards.integration.Permutation;
import org.hibernate.shards.model.Building;
import org.hibernate.shards.model.IdIsBaseType;

import static org.hibernate.shards.integration.model.ModelDataFactory.building;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author maxr@google.com (Max Ross)
 */
@RunWith(Parameterized.class)
public class ModelIntegrationTest extends BaseShardingIntegrationTestCase {

	public ModelIntegrationTest(final Permutation perm) {
		super( perm );
	}

	@Test
	public void testSaveIdIsBaseType() {
		IdIsBaseType hli = new IdIsBaseType();
		session.beginTransaction();
		hli.setValue( "yamma" );
		session.save( hli );
		commitAndResetSession();
		hli = reload( hli );
		assertNotNull( hli );
	}

	@Test
	public void testSaveOrUpdateIdIsBasetype() {
		IdIsBaseType hli = new IdIsBaseType();
		session.beginTransaction();
		hli.setValue( "yamma" );
		session.saveOrUpdate( hli );
		commitAndResetSession();
		hli = reload( hli );
		assertNotNull( hli );
	}

	@Test
	public void testUpdateIdIsBasetype() {
		IdIsBaseType hli = new IdIsBaseType();
		session.beginTransaction();
		hli.setValue( "yamma" );
		session.save( hli );
		session.getTransaction().commit();
		session.evict( hli );
		resetSession();
		session.beginTransaction();
		session.saveOrUpdate( hli );
		commitAndResetSession();
		hli = reload( hli );
		assertNotNull( hli );
	}

	@Test
	public void testShardAware() {
		final Building b = building( "yam" );
		assertNull( b.getShardId() );
		session.beginTransaction();
		session.save( b );
		assertNotNull( b.getShardId() );
		commitAndResetSession();
		final Building bReloaded = reload( b );
		assertEquals( b.getShardId(), bReloaded.getShardId() );
	}

	@Parameterized.Parameters()
	public static Iterable<Object[]> data() {
		return PermutationHelper.data();
	}
}
