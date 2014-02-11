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

package org.hibernate.shards.integration;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.shards.ShardId;
import org.hibernate.shards.ShardedConfiguration;
import org.hibernate.shards.cfg.ConfigurationToShardConfigurationAdapter;
import org.hibernate.shards.cfg.ShardConfiguration;
import org.hibernate.shards.integration.platform.DatabasePlatform;
import org.hibernate.shards.integration.platform.DatabasePlatformFactory;
import org.hibernate.shards.loadbalance.RoundRobinShardLoadBalancer;
import org.hibernate.shards.session.ShardAware;
import org.hibernate.shards.session.ShardedSession;
import org.hibernate.shards.session.ShardedSessionFactory;
import org.hibernate.shards.session.ShardedSessionImpl;
import org.hibernate.shards.strategy.ShardStrategy;
import org.hibernate.shards.strategy.ShardStrategyFactory;
import org.hibernate.shards.strategy.ShardStrategyImpl;
import org.hibernate.shards.strategy.access.ParallelShardAccessStrategy;
import org.hibernate.shards.strategy.access.SequentialShardAccessStrategy;
import org.hibernate.shards.strategy.access.ShardAccessStrategy;
import org.hibernate.shards.strategy.resolution.AllShardsShardResolutionStrategy;
import org.hibernate.shards.strategy.resolution.ShardResolutionStrategy;
import org.hibernate.shards.strategy.selection.RoundRobinShardSelectionStrategy;
import org.hibernate.shards.strategy.selection.ShardSelectionStrategy;
import org.hibernate.shards.util.DatabaseUtils;
import org.hibernate.shards.util.Lists;
import org.hibernate.shards.util.Maps;

/**
 * Base class for all sharding integration tests.
 * Sets up and tears down in-memory hypersonic dbs.
 * The rest is up to you.
 *
 * @author maxr@google.com (Max Ross)
 */
public abstract class BaseShardingIntegrationTestCase {

	private final Permutation perm;

	protected ShardedSessionFactory sf;
	protected ShardedSession session;

	/**
	 * Gosh, it sure seems expensive to be initializing a ThreadPoolExecutor
	 * for each test that needs it rather than initializing it once and just
	 * using it for any test that needs it.  So why do it this way?  Well, first
	 * read my novella in MemoryLeakPlugger.  Done?  Ok, welcome back.  So you
	 * should now recognize that the MemoryLeakPlugger is used to clear out the
	 * value of a specific, problematic ThreadLocal.  But what do you think happens
	 * if Hibernate and CGLib were doing their thing in some other thread?
	 * Exactly.  The MemoryLeakPlugger isn't going to be able to clear out the
	 * Callbacks that were initialized in different threads, and any test
	 * that does work in other threads is going to cause memory leaks.  The
	 * solution is to make sure the threads die (all ThreadLocals get gc'ed when
	 * a Thread dies), so we initialize and shutdown the ThreadPoolExecutor for
	 * every test that needs it.
	 */
	protected ThreadPoolExecutor executor;

	protected BaseShardingIntegrationTestCase(final Permutation perm) {
		this.perm = perm != null ? perm : Permutation.DEFAULT;
	}

	@Before
	public void beforeTest() throws Exception {
		this.setUp();
	}

	protected void setUp() throws Exception {
		for ( int i = 0; i < getNumDatabases(); i++ ) {
			DatabaseUtils.destroyDatabase( i, getIdGenType() );
			DatabaseUtils.createDatabase( i, getIdGenType() );
		}

		final Configuration prototypeConfig = buildPrototypeConfig();
		final List<ShardConfiguration> configurations = buildConfigurations();

		// now we use these configs to build our sharded config
		final ShardStrategyFactory shardStrategyFactory = buildShardStrategyFactory();
		final Map<Integer, Integer> virtualShardMap = buildVirtualShardToShardMap();

		// we base the configuration off of the shard0 config with the expectation
		// that all other configs will be the same
		final ShardedConfiguration shardedConfig = new ShardedConfiguration(
				prototypeConfig,
				configurations,
				shardStrategyFactory,
				virtualShardMap
		);
		sf = shardedConfig.buildShardedSessionFactory();

		session = openSession();
	}

	protected ShardedSession openSession() {
		return sf.openSession();
	}

	protected Map<Integer, Integer> buildVirtualShardToShardMap() {
		final Map<Integer, Integer> virtualShardToShardMap = Maps.newHashMap();
		if ( isVirtualShardingEnabled() ) {
			for ( int i = 0; i < getNumShards(); ++i ) {
				virtualShardToShardMap.put( i, i % getNumDatabases() );
			}
		}
		return virtualShardToShardMap;
	}

	private Configuration buildPrototypeConfig() {
		final DatabasePlatform dbPlatform = DatabasePlatformFactory.FACTORY.getDatabasePlatform();
		final String dbPlatformConfigDirectory = "platform/" + dbPlatform.getName().toLowerCase() + "/config/";
		final String configurationFile = dbPlatformConfigDirectory + "shard0.hibernate.cfg.xml";
		final IdGenType idGenType = getIdGenType();
		final Configuration config = createPrototypeConfiguration();
		config.configure( BaseShardingIntegrationTestCase.class.getResource( configurationFile ) );
		config.addURL( BaseShardingIntegrationTestCase.class.getResource( dbPlatformConfigDirectory + idGenType.getMappingFile() ) );
		return config;
	}

	/**
	 * You can override this if you want to return your own subclass of Configuration.
	 *
	 * @return The {@link Configuration} to use as the prototype
	 */
	protected Configuration createPrototypeConfiguration() {
		return new Configuration();
	}

	protected List<ShardConfiguration> buildConfigurations() {
		final DatabasePlatform dbPlatform = DatabasePlatformFactory.FACTORY.getDatabasePlatform();
		final String dbPlatformConfigDirectory = "platform/" + dbPlatform.getName().toLowerCase() + "/config/";
		final List<ShardConfiguration> configs = Lists.newArrayList();
		for ( int i = 0; i < getNumDatabases(); i++ ) {
			final Configuration config = new Configuration();
			config.configure( BaseShardingIntegrationTestCase.class.getResource( dbPlatformConfigDirectory + "shard" + i + ".hibernate.cfg.xml" ) );
			configs.add( new ConfigurationToShardConfigurationAdapter( config ) );
		}
		return configs;
	}

	protected ShardStrategyFactory buildShardStrategyFactory() {
		return new ShardStrategyFactory() {
			public ShardStrategy newShardStrategy(List<ShardId> shardIds) {
				final RoundRobinShardLoadBalancer loadBalancer = new RoundRobinShardLoadBalancer( shardIds );
				final ShardSelectionStrategy sss = new RoundRobinShardSelectionStrategy( loadBalancer );
				final ShardResolutionStrategy srs = new AllShardsShardResolutionStrategy( shardIds );
				final ShardAccessStrategy sas = getShardAccessStrategy();
				return new ShardStrategyImpl( sss, srs, sas );
			}
		};
	}

	protected void commitAndResetSession() {
		session.getTransaction().commit();
		resetSession();
		session.beginTransaction();
	}

	protected void resetSession() {
		session.close();
		session = openSession();
	}

	@After
	public void afterTest() throws Exception {
		tearDown();
	}

	protected void tearDown() throws Exception {
		if ( executor != null ) {
			executor.shutdownNow();
			executor = null;
		}

		try {
			if ( session != null ) {
				session.close();
				session = null;
			}
		}
		finally {
			if ( sf != null ) {
				sf.close();
				sf = null;
			}
		}
		ShardedSessionImpl.setCurrentSubgraphShardId( null );
	}

	/**
	 * Override if you want more than the default
	 *
	 * @return the number of databases
	 */
	protected int getNumDatabases() {
		return perm.getNumDbs();
	}

	protected int getNumShards() {
		if ( isVirtualShardingEnabled() ) {
			return perm.getNumShards();
		}
		return getNumDatabases();
	}

	protected boolean isVirtualShardingEnabled() {
		return perm.isVirtualShardingEnabled();
	}

	protected IdGenType getIdGenType() {
		return perm.getIdGenType();
	}

	protected ShardAccessStrategyType getShardAccessStrategyType() {
		return perm.getSast();
	}

	protected <T> T reloadAssertNotNull(final T reloadMe) {
		final T result = reload( reloadMe );
		Assert.assertNotNull( result );
		return result;
	}

	protected <T> T reload(final T reloadMe) {
		return reload( session, reloadMe );
	}

	protected <T> T reloadAssertNotNull(final Session session, final T reloadMe) {
		final T result = reload( session, reloadMe );
		Assert.assertNotNull( result );
		return result;
	}

	@SuppressWarnings("unchecked")
	protected <T> T reload(final Session session, final T reloadMe) {
		final Class<?> clazz = reloadMe.getClass();
		final String className = clazz.getSimpleName();
		try {
			final Method m = clazz.getMethod( "get" + className + "Id" );
			return (T) get( session, clazz, (Serializable) m.invoke( reloadMe ) );
		}
		catch (NoSuchMethodException e) {
			throw new RuntimeException( e );
		}
		catch (IllegalAccessException e) {
			throw new RuntimeException( e );
		}
		catch (InvocationTargetException e) {
			throw new RuntimeException( e );
		}
	}

	protected ShardId getShardIdForObject(final Object obj) {
		final ShardId shardId = session.getShardIdForObject( obj );
		if ( obj instanceof ShardAware ) {
			Assert.assertEquals( ((ShardAware) obj).getShardId(), shardId );
		}
		return shardId;
	}

	private ShardAccessStrategy getShardAccessStrategy() {
		switch ( getShardAccessStrategyType() ) {
			case SEQUENTIAL:
				return new SequentialShardAccessStrategy();
			case PARALLEL:
				executor = buildThreadPoolExecutor();
				return new ParallelShardAccessStrategy( executor );
			default:
				throw new RuntimeException( "unsupported shard access strategy type" );
		}
	}

	private static final ThreadFactory FACTORY = new ThreadFactory() {
		private int nextThreadId = 0;

		@Override
		public Thread newThread(final Runnable r) {
			final Thread t = Executors.defaultThreadFactory().newThread( r );
			t.setDaemon( true );
			t.setName( "T" + (nextThreadId++) );
			return t;
		}
	};

	private ThreadPoolExecutor buildThreadPoolExecutor() {
		return new ThreadPoolExecutor( 10, 50, 60, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), FACTORY );
	}

	/**
	 * Catch all throwables so we get an opportunity to add info about the
	 * permutation under which the failure took place.
	 */
	/*
    @Override
    public void runBare() throws Throwable {
        try {
            super.runBare();
        } catch (Throwable t) {
            throw new RuntimeException(perm.getMessageWithPermutationPrefix(t.getMessage()), t);
            // TODO(maxr) handle assertion failure separately so they get properly reported
        }
    }
    */
	@SuppressWarnings("unchecked")
	protected <T> List<T> list(final Criteria crit) {
		return crit.list();
	}

	@SuppressWarnings("unchecked")
	protected <T> List<T> list(final Query query) {
		return query.list();
	}

	@SuppressWarnings("unchecked")
	protected <T> T uniqueResult(final Criteria crit) {
		return (T) crit.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	protected <T> T get(final Session session, final Class<?> clazz, final Serializable id) {
		return (T) session.get( clazz, id );
	}
}
