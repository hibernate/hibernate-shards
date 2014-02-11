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

import java.math.BigDecimal;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.shards.PermutationHelper;
import org.hibernate.shards.integration.BaseShardingIntegrationTestCase;
import org.hibernate.shards.integration.Permutation;
import org.hibernate.shards.model.Building;
import org.hibernate.shards.model.Floor;
import org.hibernate.shards.model.Office;
import org.hibernate.shards.util.Lists;

/**
 * @author maxr@google.com (Max Ross)
 */
@RunWith(Parameterized.class)
public class ModelCriteriaPermutedIntegrationTest extends BaseShardingIntegrationTestCase {

	private Building b1;
	private Floor b1f1;
	private Floor b1f2;
	private Floor b1f3;
	private Office b1f3o1;
	private Office b1f3o2;
	private Building b2;
	private Floor b2f1;
	private Office b2f1o1;

	public ModelCriteriaPermutedIntegrationTest(final Permutation perm) {
		super( perm );
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		session.beginTransaction();
		b1 = ModelDataFactory.building( "b1" );
		// because of the fuzziness in how avg gets computed on hsqldb
		// we need to make sure the per-shard avg is a round number, otherwise
		// our test will fail
		b1f1 = ModelDataFactory.floor( b1, 1, new BigDecimal( 10.00 ) );
		b1f2 = ModelDataFactory.floor( b1, 2, new BigDecimal( 20.00 ) );
		b1f3 = ModelDataFactory.floor( b1, 3, new BigDecimal( 30.00 ) );
		b1f3o1 = ModelDataFactory.office( "NOT LAHGE", b1f3 );
		b1f3o2 = ModelDataFactory.office( "LAHGE", b1f3 );
		session.save( b1 );
		session.getTransaction().commit();

		session.beginTransaction();
		b2 = ModelDataFactory.building( "b2" );
		b2f1 = ModelDataFactory.floor( b2, 1, new BigDecimal( 20.00 ) );
		b2f1o1 = ModelDataFactory.office( "LAHGE", b2f1 );
		session.save( b2 );
		session.getTransaction().commit();
		resetSession();
		b1 = reload( b1 );
		b1f1 = reload( b1f1 );
		b1f2 = reload( b1f2 );
		b1f3 = reload( b1f3 );
		b1f3o1 = reload( b1f3o1 );
		b1f3o2 = reload( b1f3o2 );
		b2 = reload( b2 );
		b2f1 = reload( b2f1 );
		b2f1o1 = reload( b2f1o1 );
	}

	@Override
	protected void tearDown() throws Exception {
		b1 = null;
		b1f1 = null;
		b1f2 = null;
		b1f3 = null;
		b1f3o1 = null;
		b1f3o2 = null;
		b2 = null;
		b2f1 = null;
		b2f1o1 = null;
		super.tearDown();
	}

	@Test
	public void testLoadAllBuildings() {
		final Criteria criteria = session.createCriteria( Building.class );
		final List<Building> buildings = list( criteria );
		Assert.assertEquals( 2, buildings.size() );
		Assert.assertTrue( buildings.contains( b1 ) );
		Assert.assertTrue( buildings.contains( b2 ) );
	}

	@Test
	public void testLoadAllBuildingsAfterForcingEarlyInit() {
		final Criteria criteria = session.createCriteria( Building.class );
		// forces us to initialize an actual Criteria object
		criteria.getAlias();
		final List<Building> buildings = list( criteria );
		Assert.assertEquals( 2, buildings.size() );
		Assert.assertTrue( buildings.contains( b1 ) );
		Assert.assertTrue( buildings.contains( b2 ) );
	}

	@Test
	public void testLoadBuildingByName() {
		final Criteria criteria = session.createCriteria( Building.class );
		criteria.add( Restrictions.eq( "name", "b2" ) );
		final Building b2Reloaded = uniqueResult( criteria );
		Assert.assertEquals( b2.getBuildingId(), b2Reloaded.getBuildingId() );
	}

	@Test
	public void testLoadBuildingByNameAfterForcingEarlyInit() {
		final Criteria criteria = session.createCriteria( Building.class ).add( Restrictions.eq( "name", "b2" ) );
		// forces us to initialize an actual Criteria object
		criteria.getAlias();
		Building b2Reloaded = uniqueResult( criteria );
		Assert.assertEquals( b2.getBuildingId(), b2Reloaded.getBuildingId() );
	}

	@Test
	public void testLoadBuildingsByLikeName() {
		final Criteria criteria = session.createCriteria( Building.class );
		criteria.add( Restrictions.in( "name", Lists.newArrayList( "b1", "b2" ) ) );
		final List<Building> buildings = list( criteria );
		Assert.assertEquals( 2, buildings.size() );
		Assert.assertTrue( buildings.contains( b1 ) );
		Assert.assertTrue( buildings.contains( b2 ) );
	}

	@Test
	public void testLoadHighFloors() {
		final Criteria criteria = session.createCriteria( Floor.class );
		criteria.add( Restrictions.ge( "number", 3 ) );
		final List<Floor> floors = list( criteria );
		Assert.assertEquals( 1, floors.size() );
		Assert.assertTrue( floors.contains( b1f3 ) );
	}

	@Test
	public void testLoadBuildingsWithHighFloorsViaTopLevelCriteria() {
		final Criteria criteria = session.createCriteria( Building.class );
		final Criteria floorCriteria = criteria.createCriteria( "floors" );
		floorCriteria.add( Restrictions.ge( "number", 3 ) );
		final List<Building> l = list( criteria );
		Assert.assertEquals( 1, l.size() );
	}

	@Test
	public void testLoadBuildingsWithHighFloorsViaTopLevelCriteriaAfterForcingEarlyInitOnTopLevelCriteria() {
		final Criteria criteria = session.createCriteria( Building.class );
		final Criteria floorCriteria = criteria.createCriteria( "floors" );
		floorCriteria.add( Restrictions.ge( "number", 3 ) );
		// forces us to initialize an actual Criteria object
		criteria.getAlias();
		final List<Building> l = list( criteria );
		Assert.assertEquals( 1, l.size() );
	}

	@Test
	public void testLoadBuildingsWithHighFloorsViaTopLevelCriteriaAfterForcingEarlyInitOnSubcriteria() {
		final Criteria criteria = session.createCriteria( Building.class );
		final Criteria floorCriteria = criteria.createCriteria( "floors" );
		floorCriteria.add( Restrictions.ge( "number", 3 ) );
		// forces us to initialize an actual Criteria object
		floorCriteria.getAlias();
		final List<Building> l = list( criteria );
		Assert.assertEquals( 1, l.size() );
	}

	@Test
	public void testLoadBuildingsWithHighFloorsViaSubcriteriaAfterForcingEarlyInitOnSubcriteria() {
		final Criteria criteria = session.createCriteria( Building.class );
		final Criteria floorCriteria = criteria.createCriteria( "floors" );
		floorCriteria.add( Restrictions.ge( "number", 3 ) );
		// forces us to initialize an actual Criteria object
		floorCriteria.getAlias();
		final List<Building> l = list( floorCriteria );
		Assert.assertEquals( 1, l.size() );
	}

	@Test
	public void testLoadBuildingsWithHighFloorsViaSubcriteriaAfterForcingEarlyInitOnTopLevelCriteria() {
		final Criteria criteria = session.createCriteria( Building.class );
		final Criteria floorCriteria = criteria.createCriteria( "floors" );
		floorCriteria.add( Restrictions.ge( "number", 3 ) );
		// forces us to initialize an actual Criteria object
		criteria.getAlias();
		final List<Building> l = list( floorCriteria );
		Assert.assertEquals( 1, l.size() );
	}

	@Test
	public void testLoadBuildingsWithHighFloorsViaSubcriteria() {
		final Criteria criteria = session.createCriteria( Building.class );
		final Criteria floorCriteria = criteria.createCriteria( "floors" );
		floorCriteria.add( Restrictions.ge( "number", 3 ) );
		// note how we execute the query via the floorCriteria
		final List<Building> l = list( floorCriteria );
		Assert.assertEquals( 1, l.size() );
	}

	@Test
	public void testLoadBuildingsWithLargeOfficesViaTopLevelCriteria() {
		final Criteria criteria = session.createCriteria( Building.class );
		final Criteria floorCriteria = criteria.createCriteria( "floors" );
		final Criteria officeCriteria = floorCriteria.createCriteria( "offices" );
		officeCriteria.add( Restrictions.eq( "label", "LAHGE" ) );
		final List<Building> l = list( criteria );
		Assert.assertEquals( 2, l.size() );
	}

	@Test
	public void testLoadBuildingsWithLargeOfficesViaSubcriteria() {
		final Criteria criteria = session.createCriteria( Building.class );
		final Criteria floorCriteria = criteria.createCriteria( "floors" );
		Criteria officeCriteria = floorCriteria.createCriteria( "offices" );
		officeCriteria.add( Restrictions.eq( "label", "LAHGE" ) );
		// now how we execute the query via the floorcrit
		final List<Building> l = list( officeCriteria );
		Assert.assertEquals( 2, l.size() );
	}

	@Test
	public void testRowCountProjection() {
		final Criteria criteria = session.createCriteria( Building.class ).setProjection( Projections.rowCount() );
		final Criteria floorCriteria = criteria.createCriteria( "floors" );
		final Criteria officeCriteria = floorCriteria.createCriteria( "offices" );
		officeCriteria.add( Restrictions.eq( "label", "LAHGE" ) );
		// now how we execute the query via the floorcrit
		final List<Integer> result = list( officeCriteria );
		Assert.assertEquals( 1, result.size() );
		int total = 0;
		for ( final int shardTotal : result ) {
			total += shardTotal;
		}
		Assert.assertEquals( 2, total );
	}

	@Test
	public void testAvgProjection() {
		final Criteria criteria = session.createCriteria( Floor.class )
				.setProjection( Projections.avg( "squareFeet" ) );
		final List<Double> result = list( criteria );
		Assert.assertEquals( 1, result.size() );
		Assert.assertEquals( Double.valueOf( 20.0 ), result.get( 0 ) );
	}

	@Test
	public void testMaxResults() throws Exception {
		final Criteria criteria = session.createCriteria( Building.class ).setMaxResults( 1 );
		Assert.assertEquals( 1, list( criteria ).size() );
	}

	@Test
	public void testFirstAndMaxResults() {
		Building b3 = ModelDataFactory.building( "b3" );
		Building b4 = ModelDataFactory.building( "b4" );
		Building b5 = ModelDataFactory.building( "b5" );
		session.beginTransaction();
		session.save( b5 );
		session.save( b3 );
		session.save( b4 );
		commitAndResetSession();

		final Criteria criteria = session.createCriteria( Building.class )
				.addOrder( Order.desc( "name" ) )
				.setFirstResult( 2 )
				.setMaxResults( 2 );

		final List<Building> buildings = list( criteria );
		Assert.assertEquals( 2, buildings.size() );
		Assert.assertEquals( b3, buildings.get( 0 ) );
		Assert.assertEquals( b2, buildings.get( 1 ) );
	}

	@Test
	public void testFirstAndMaxResultsWithSubCrit() {
		Building b3 = ModelDataFactory.building( "b3" );
		Floor b3f1 = ModelDataFactory.floor( b3, 1 );
		Building b4 = ModelDataFactory.building( "b4" );
		Floor b4f1 = ModelDataFactory.floor( b4, 1 );
		Building b5 = ModelDataFactory.building( "b5" );
		Floor b5f1 = ModelDataFactory.floor( b5, 1 );
		session.beginTransaction();
		session.save( b5 );
		session.save( b3 );
		session.save( b4 );
		commitAndResetSession();

		final Criteria criteria = session.createCriteria( Building.class )
				.addOrder( Order.desc( "name" ) )
				.createCriteria( "floors" )
				.add( Restrictions.eq( "number", 1 ) )
				.setFirstResult( 2 )
				.setMaxResults( 2 );

		final List<Building> buildings = list( criteria );
		Assert.assertEquals( 2, buildings.size() );
		Assert.assertEquals( b3, buildings.get( 0 ) );
		Assert.assertEquals( b2, buildings.get( 1 ) );
	}

	@Test
	public void testAggregateProjection() throws Exception {
		final Criteria criteria = session.createCriteria( Floor.class ).setProjection( Projections.sum( "number" ) );
		final List<BigDecimal> l = list( criteria );
		Assert.assertEquals( 1, l.size() );
		Assert.assertEquals( BigDecimal.valueOf( 7 ), l.get( 0 ) );
	}

	@Test
	public void testMultiExitOperations() throws Exception {
		session.beginTransaction();
		final Building b = ModelDataFactory.building( "Only Has Floors from 199-210" );
		ModelDataFactory.floor( b, 199 );
		ModelDataFactory.floor( b, 200 );
		ModelDataFactory.floor( b, 201 );
		session.save( b );
		session.getTransaction().commit();
		resetSession();

		final Criteria criteria = session.createCriteria( Floor.class )
				.addOrder( Order.asc( "number" ) )
				.setFirstResult( 2 )
				.setMaxResults( 3 )
				.setProjection( Projections.sum( "number" ) );
		final List<BigDecimal> results = list( criteria );
		Assert.assertEquals( 1, results.size() );
		Assert.assertEquals( BigDecimal.valueOf( 204 ), results.get( 0 ) );
	}

	@Test
	public void testMultiOrdering() throws Exception {

		final Criteria criteria = session.createCriteria( Office.class )
				.addOrder( Order.asc( "label" ) )
				.createCriteria( "floor" )
				.createCriteria( "building" )
				.addOrder( Order.desc( "name" ) );
		final List<Office> listResult = list( criteria );
		final List<Office> answer = Lists.newArrayList( b2f1o1, b1f3o2, b1f3o1 );
		Assert.assertTrue( answer.equals( listResult ) );
	}

	@Parameterized.Parameters()
	public static Iterable<Object[]> data() {
		return PermutationHelper.data();
	}
}
