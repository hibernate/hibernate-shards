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

package org.hibernate.shards;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.cfg.Mappings;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.OneToOne;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Table;
import org.hibernate.shards.cfg.ShardConfiguration;
import org.hibernate.shards.session.ShardedSessionFactoryImpl;
import org.hibernate.shards.strategy.ShardStrategy;
import org.hibernate.shards.strategy.ShardStrategyFactoryDefaultMock;
import org.hibernate.shards.util.Lists;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Maulik Shah
 */
public class ShardedConfigurationTest {

	private MyShardStrategyFactory shardStrategyFactory;
	private ShardConfiguration shardConfig;
	private ShardedConfiguration shardedConfiguration;

	@Before
	public void setUp() throws Exception {

		shardStrategyFactory = new MyShardStrategyFactory();
		Configuration protoConfig = new Configuration();
		protoConfig.setProperty( Environment.DIALECT, HSQLDialect.class.getName() );
		shardConfig = new MyShardConfig(
				"sa",
				"jdbc:hsqldb:mem:shard0",
				"",
				"sfname",
				"prefix",
				33,
				"org.hsqldb.jdbcDriver",
				"org.hibernate.dialect.HSQLDialect"
		);

		shardedConfiguration = new ShardedConfiguration(
				protoConfig,
				Collections.singletonList( shardConfig ),
				shardStrategyFactory
		);
	}

	@Test
	public void testBuildShardedSessionFactoryPreconditions() throws Exception {
		final List<ShardConfiguration> shardConfigs = Lists.newArrayList( shardConfig );
		try {
			new ShardedConfiguration( null, shardConfigs, shardStrategyFactory );
			fail( "Expected npe" );
		}
		catch (IllegalArgumentException npe) {
			// good
		}

		final Configuration config = new Configuration();
		try {
			new ShardedConfiguration( config, null, shardStrategyFactory );
			fail( "Expected npe" );
		}
		catch (IllegalArgumentException npe) {
			// good
		}

		shardConfigs.clear();
		try {
			new ShardedConfiguration( config, shardConfigs, shardStrategyFactory );
			fail( "Expected iae" );
		}
		catch (IllegalArgumentException iae) {
			// good
		}
	}

	@Test(expected = NullPointerException.class)
	public void testShardIdRequired() {
		final ShardConfiguration config = new MyShardConfig(
				"user",
				"url",
				"pwd",
				"sfname",
				null,
				null,
				"org.hsqldb.jdbcDriver",
				"org.hibernate.dialect.HSQLDialect"
		);
		shardedConfiguration.populatePrototypeWithVariableProperties( config );
	}

	@Test
	public void testBuildShardedSessionFactory() {
		final ShardedSessionFactoryImpl ssfi = (ShardedSessionFactoryImpl) shardedConfiguration.buildShardedSessionFactory();
		assertNotNull( ssfi );
		// make sure the session factory contained in the sharded session factory
		// has the number of session factories we expect
		List<SessionFactory> sfList = ssfi.getSessionFactories();
		assertEquals( 1, sfList.size() );
	}

	public void testRequiresShardLock() {
		final Mappings mappingsMock = Mockito.mock( Mappings.class );
		final Property property = new Property();
		assertFalse( shardedConfiguration.doesNotSupportTopLevelSave( property ) );
		final ManyToOne mto = new ManyToOne( mappingsMock, new Table() );
		property.setValue( mto );
		assertFalse( shardedConfiguration.doesNotSupportTopLevelSave( property ) );
		final OneToOne oto = new OneToOne( mappingsMock, new Table(), new RootClass() );
		property.setValue( oto );
		assertTrue( shardedConfiguration.doesNotSupportTopLevelSave( property ) );
	}

	private class MyShardStrategyFactory extends ShardStrategyFactoryDefaultMock {
		@Override
		public ShardStrategy newShardStrategy(List<ShardId> shardIds) {
			return null;
		}
	}

	private static final class MyShardConfig implements ShardConfiguration {

		private final String user;
		private final String url;
		private final String password;
		private final String sessionFactoryName;
		private final String cacheRegionPrefix;
		private final Integer shardId;
		private final String driverClass;
		private final String dialect;

		public MyShardConfig(
				String user,
				String url,
				String password,
				String sessionFactoryName,
				String cacheRegionPrefix,
				Integer shardId,
				String driverClass,
				String dialect) {
			this.user = user;
			this.url = url;
			this.password = password;
			this.sessionFactoryName = sessionFactoryName;
			this.cacheRegionPrefix = cacheRegionPrefix;
			this.shardId = shardId;
			this.driverClass = driverClass;
			this.dialect = dialect;
		}

		@Override
		public String getShardUser() {
			return user;
		}

		@Override
		public String getShardUrl() {
			return url;
		}

		@Override
		public String getShardPassword() {
			return password;
		}

		@Override
		public String getShardSessionFactoryName() {
			return sessionFactoryName;
		}

		@Override
		public Integer getShardId() {
			return shardId;
		}

		@Override
		public String getShardDatasource() {
			return null;
		}

		@Override
		public String getShardCacheRegionPrefix() {
			return cacheRegionPrefix;
		}

		@Override
		public String getDriverClassName() {
			return driverClass;
		}

		@Override
		public String getHibernateDialect() {
			return dialect;
		}
	}
}
