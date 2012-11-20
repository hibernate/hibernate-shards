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

package org.hibernate.shards.session;

import org.hibernate.Cache;
import org.hibernate.ConnectionReleaseMode;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.MappingException;
import org.hibernate.SessionFactory;
import org.hibernate.SessionFactoryObserver;
import org.hibernate.StatelessSession;
import org.hibernate.TypeHelper;
import org.hibernate.cache.QueryCache;
import org.hibernate.cache.Region;
import org.hibernate.cache.UpdateTimestampsCache;
import org.hibernate.cfg.Settings;
import org.hibernate.classic.Session;
import org.hibernate.connection.ConnectionProvider;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.SQLFunctionRegistry;
import org.hibernate.engine.FilterDefinition;
import org.hibernate.engine.NamedQueryDefinition;
import org.hibernate.engine.NamedSQLQueryDefinition;
import org.hibernate.engine.ResultSetMappingDefinition;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.engine.profile.FetchProfile;
import org.hibernate.engine.query.QueryPlanCache;
import org.hibernate.exception.SQLExceptionConverter;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.factory.IdentifierGeneratorFactory;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.proxy.EntityNotFoundDelegate;
import org.hibernate.shards.ShardId;
import org.hibernate.shards.engine.ShardedSessionFactoryImplementor;
import org.hibernate.shards.id.GeneratorRequiringControlSessionProvider;
import org.hibernate.shards.strategy.ShardStrategy;
import org.hibernate.shards.strategy.ShardStrategyFactory;
import org.hibernate.shards.util.Iterables;
import org.hibernate.shards.util.Lists;
import org.hibernate.shards.util.Maps;
import org.hibernate.shards.util.Preconditions;
import org.hibernate.shards.util.Sets;
import org.hibernate.stat.Statistics;
import org.hibernate.stat.ConcurrentStatisticsImpl;
import org.hibernate.stat.StatisticsImplementor;
import org.hibernate.type.Type;
import org.hibernate.type.TypeResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.transaction.TransactionManager;
import java.io.Serializable;
import java.sql.Connection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Shard-aware implementation of {@link SessionFactory}.
 *
 * @author maxr@google.com (Max Ross)
 */
public class ShardedSessionFactoryImpl implements ShardedSessionFactoryImplementor, ControlSessionProvider {

    // the id of the control shard
    private static final int CONTROL_SHARD_ID = 0;

    // the SessionFactoryImplementor objects to which we delegate
    private final List<SessionFactoryImplementor> sessionFactories;

    // All classes that cannot be directly saved
    private final Set<Class<?>> classesWithoutTopLevelSaveSupport;

    // map of SessionFactories used by this ShardedSessionFactory (might be a subset of all SessionFactories)
    private final Map<SessionFactoryImplementor, Set<ShardId>> sessionFactoryShardIdMap;

    // map of all existing SessionFactories, used when creating a new ShardedSessionFactory for some subset of shards
    private final Map<SessionFactoryImplementor, Set<ShardId>> fullSessionFactoryShardIdMap;

    // The strategy we use for all shard-related operations
    private final ShardStrategy shardStrategy;

    // Reference to the SessionFactory we use for functionality that expects
    // data to live in a single, well-known location (like distributed sequences)
    private final SessionFactoryImplementor controlSessionFactory;

    // flag to indicate whether we should do full cross-shard relationship
    // checking (very slow)
    private final boolean checkAllAssociatedObjectsForDifferentShards;

    // Statistics aggregated across all contained SessionFactories
    private final Statistics statistics = new ConcurrentStatisticsImpl(this);

    // our lovely logger
    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Constructs a ShardedSessionFactoryImpl
     *
     * @param shardIds                 The ids of the shards with which this SessionFactory
     *                                 should be associated.
     * @param sessionFactoryShardIdMap Mapping of SessionFactories to shard ids.
     *                                 When using virtual shards, this map associates SessionFactories (physical
     *                                 shards) with virtual shards (shard ids).  Map cannot be empty.
     *                                 Map keys cannot be null.  Map values cannot be null or empty.
     * @param shardStrategyFactory     factory that knows how to create the {@link ShardStrategy}
     *                                 that will be used for all shard-related operations
     * @param classesWithoutTopLevelSaveSupport
     *                                 All classes that cannot be saved
     *                                 as top-level objects
     * @param checkAllAssociatedObjectsForDifferentShards
     *                                 Flag that controls
     *                                 whether or not we do full cross-shard relationshp checking (very slow)
     */
    public ShardedSessionFactoryImpl(final List<ShardId> shardIds,
                                     final Map<SessionFactoryImplementor, Set<ShardId>> sessionFactoryShardIdMap,
                                     final ShardStrategyFactory shardStrategyFactory,
                                     final Set<Class<?>> classesWithoutTopLevelSaveSupport,
                                     final boolean checkAllAssociatedObjectsForDifferentShards) {

        Preconditions.checkNotNull(sessionFactoryShardIdMap);
        Preconditions.checkArgument(!sessionFactoryShardIdMap.isEmpty());
        Preconditions.checkNotNull(shardStrategyFactory);
        Preconditions.checkNotNull(classesWithoutTopLevelSaveSupport);

        this.sessionFactories = Lists.newArrayList(sessionFactoryShardIdMap.keySet());
        this.sessionFactoryShardIdMap = Maps.newHashMap();
        this.fullSessionFactoryShardIdMap = sessionFactoryShardIdMap;
        this.classesWithoutTopLevelSaveSupport = Sets.newHashSet(classesWithoutTopLevelSaveSupport);
        this.checkAllAssociatedObjectsForDifferentShards = checkAllAssociatedObjectsForDifferentShards;

        final Set<ShardId> uniqueShardIds = Sets.newHashSet();
        SessionFactoryImplementor controlSessionFactoryToSet = null;
        for (final Map.Entry<SessionFactoryImplementor, Set<ShardId>> entry : sessionFactoryShardIdMap.entrySet()) {
            final SessionFactoryImplementor implementor = entry.getKey();
            Preconditions.checkNotNull(implementor);
            final Set<ShardId> shardIdSet = entry.getValue();
            Preconditions.checkNotNull(shardIdSet);
            Preconditions.checkArgument(!shardIdSet.isEmpty());
            for (final ShardId shardId : shardIdSet) {
                // TODO(tomislav): we should change it so we specify control shard in configuration
                if (shardId.getId() == CONTROL_SHARD_ID) {
                    controlSessionFactoryToSet = implementor;
                }
                if (!uniqueShardIds.add(shardId)) {
                    final String msg = String.format("Cannot have more than one shard with shard id %d.", shardId.getId());
                    log.error(msg);
                    throw new HibernateException(msg);
                }
                if (shardIds.contains(shardId)) {
                    if (!this.sessionFactoryShardIdMap.containsKey(implementor)) {
                        this.sessionFactoryShardIdMap.put(implementor, Sets.<ShardId>newHashSet());
                    }
                    this.sessionFactoryShardIdMap.get(implementor).add(shardId);
                }
            }
        }
        // make sure someone didn't associate a session factory with a shard id
        // that isn't in the full list of shards
        for (ShardId shardId : shardIds) {
            Preconditions.checkState(uniqueShardIds.contains(shardId));
        }
        controlSessionFactory = controlSessionFactoryToSet;
        // now that we have all our shard ids, construct our shard strategy
        this.shardStrategy = shardStrategyFactory.newShardStrategy(shardIds);
        setupIdGenerators();
    }

    /**
     * Constructs a ShardedSessionFactoryImpl
     *
     * @param sessionFactoryShardIdMap Mapping of SessionFactories to shard ids.
     *                                 When using virtual shards, this map associates SessionFactories (physical
     *                                 shards) with virtual shards (shard ids).  Map cannot be empty.
     *                                 Map keys cannot be null.  Map values cannot be null or empty.
     * @param shardStrategyFactory     factory that knows how to create the {@link ShardStrategy}
     *                                 that will be used for all shard-related operations
     * @param classesWithoutTopLevelSaveSupport
     *                                 All classes that cannot be saved
     *                                 as top-level objects
     * @param checkAllAssociatedObjectsForDifferentShards
     *                                 Flag that controls
     *                                 whether or not we do full cross-shard relationshp checking (very slow)
     */
    public ShardedSessionFactoryImpl(
            Map<SessionFactoryImplementor, Set<ShardId>> sessionFactoryShardIdMap,
            ShardStrategyFactory shardStrategyFactory,
            Set<Class<?>> classesWithoutTopLevelSaveSupport,
            boolean checkAllAssociatedObjectsForDifferentShards) {
        this(Lists.newArrayList(Iterables.concat(sessionFactoryShardIdMap.values())),
                sessionFactoryShardIdMap,
                shardStrategyFactory,
                classesWithoutTopLevelSaveSupport,
                checkAllAssociatedObjectsForDifferentShards);
    }

    /**
     * Sets the {@link ControlSessionProvider} on id generators that implement the
     * {@link GeneratorRequiringControlSessionProvider} interface
     */
    private void setupIdGenerators() {
        for (SessionFactoryImplementor sfi : sessionFactories) {
            for (Object obj : sfi.getAllClassMetadata().values()) {
                ClassMetadata cmd = (ClassMetadata) obj;
                EntityPersister ep = sfi.getEntityPersister(cmd.getEntityName());
                if (ep.getIdentifierGenerator() instanceof GeneratorRequiringControlSessionProvider) {
                    ((GeneratorRequiringControlSessionProvider) ep.getIdentifierGenerator()).setControlSessionProvider(this);
                }
            }
        }
    }

    @Override
    public Map<SessionFactoryImplementor, Set<ShardId>> getSessionFactoryShardIdMap() {
        return sessionFactoryShardIdMap;
    }

    /**
     * Unsupported.  This is a technical decision.  We would need a
     * ShardedConnection in order to make this work, but since this method is
     * exposed on the public api we can't force clients to provide it.  And
     * at any rate, exposing a ShardedConnection somewhat defeats the purpose
     * of tucking away all the sharding intelligence.
     */
    @Override
    public Session openSession(final Connection connection) {
        throw new UnsupportedOperationException(
                "Cannot open a sharded session with a user provided connection.");
    }

    /**
     * Warning: this interceptor will be shared across all shards, so be very
     * careful about using a stateful implementation.
     */
    @Override
    public ShardedSession openSession(Interceptor interceptor) throws HibernateException {
        return new ShardedSessionImpl(
                interceptor,
                this,
                shardStrategy,
                classesWithoutTopLevelSaveSupport,
                checkAllAssociatedObjectsForDifferentShards);
    }

    /**
     * Unsupported.  This is a technical decision.  See {@link ShardedSessionFactoryImpl#openSession(Connection)}
     * for an explanation.
     */
    @Override
    public Session openSession(final Connection connection, final Interceptor interceptor) {
        throw new UnsupportedOperationException("Cannot open a sharded session with a user provided connection.");
    }

    @Override
    public ShardedSession openSession() throws HibernateException {
        return new ShardedSessionImpl(
                this,
                shardStrategy,
                classesWithoutTopLevelSaveSupport,
                checkAllAssociatedObjectsForDifferentShards);
    }

    /**
     * Unsupported.  This is a project decision.  We'll get to it later.
     */
    @Override
    public StatelessSession openStatelessSession() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Unsupported.  This is a technical decision.  See {@link ShardedSessionFactoryImpl#openSession(Connection)}
     * for an explanation.
     */
    @Override
    public StatelessSession openStatelessSession(final Connection connection) {
        throw new UnsupportedOperationException(
                "Cannot open a stateless sharded session with a user provided connection");
    }

    @Override
    public Session getCurrentSession() throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ClassMetadata getClassMetadata(final Class persistentClass) throws HibernateException {
        // assumption is that all session factories are configured the same way,
        // so it doesn't matter which session factory answers this question
        return getAnyFactory().getClassMetadata(persistentClass);
    }

    @Override
    public ClassMetadata getClassMetadata(final String entityName) throws HibernateException {
        // assumption is that all session factories are configured the same way,
        // so it doesn't matter which session factory answers this question
        return getAnyFactory().getClassMetadata(entityName);
    }

    @Override
    public CollectionMetadata getCollectionMetadata(final String roleName) throws HibernateException {
        // assumption is that all session factories are configured the same way,
        // so it doesn't matter which session factory answers this question
        return getAnyFactory().getCollectionMetadata(roleName);
    }

    @Override
    public Map<String, ClassMetadata> getAllClassMetadata() throws HibernateException {
        // assumption is that all session factories are configured the same way,
        // so it doesn't matter which session factory answers this question
        return getAnyFactory().getAllClassMetadata();
    }

    @Override
    public Map getAllCollectionMetadata() throws HibernateException {
        // assumption is that all session factories are configured the same way,
        // so it doesn't matter which session factory answers this question
        return getAnyFactory().getAllCollectionMetadata();
    }

    /**
     * Unsupported.  This is a scope decision, not a technical decision.
     */
    @Override
    public Statistics getStatistics() {
        return statistics;
    }

    @Override
    public void close() throws HibernateException {
        for (final SessionFactory sf : sessionFactories) {
            sf.close();
        }
        sessionFactories.clear();
        if (classesWithoutTopLevelSaveSupport != null) {
            classesWithoutTopLevelSaveSupport.clear();
        }
        if (sessionFactoryShardIdMap != null) {
            sessionFactoryShardIdMap.clear();
        }
        if (fullSessionFactoryShardIdMap != null) {
            fullSessionFactoryShardIdMap.clear();
        }
        statistics.clear();
    }

    @Override
    public boolean isClosed() {
        // a ShardedSessionFactory is closed if any of its SessionFactories are closed
        for (final SessionFactory sf : sessionFactories) {
            if (sf.isClosed()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Cache getCache() {
        // assumption is that all session factories are configured the same way,
        // so it doesn't matter which session factory answers this question
        return getAnyFactory().getCache();
    }

    @Override
    @Deprecated
    public void evict(final Class persistentClass) throws HibernateException {
        for (final SessionFactory sf : sessionFactories) {
            sf.getCache().evictEntityRegion(persistentClass);
        }
    }

    @Override
    @Deprecated
    public void evict(final Class persistentClass, final Serializable id) throws HibernateException {
        for (final SessionFactory sf : sessionFactories) {
            sf.getCache().evictEntity(persistentClass, id);
        }
    }

    @Override
    @Deprecated
    public void evictEntity(final String entityName) throws HibernateException {
        for (final SessionFactory sf : sessionFactories) {
            sf.getCache().evictEntityRegion(entityName);
        }
    }

    @Override
    @Deprecated
    public void evictEntity(final String entityName, final Serializable id) throws HibernateException {
        for (final SessionFactory sf : sessionFactories) {
            sf.getCache().evictEntity(entityName, id);
        }
    }

    @Override
    @Deprecated
    public void evictCollection(final String roleName) throws HibernateException {
        for (final SessionFactory sf : sessionFactories) {
            sf.getCache().evictCollectionRegion(roleName);
        }
    }

    @Override
    @Deprecated
    public void evictCollection(final String roleName, final Serializable id) throws HibernateException {
        for (final SessionFactory sf : sessionFactories) {
            sf.getCache().evictCollection(roleName, id);
        }
    }

    @Override
    @Deprecated
    public void evictQueries() throws HibernateException {
        for (final SessionFactory sf : sessionFactories) {
            sf.getCache().evictQueryRegions();
        }
    }

    @Override
    @Deprecated
    public void evictQueries(final String cacheRegion) throws HibernateException {
        for (final SessionFactory sf : sessionFactories) {
            sf.getCache().evictQueryRegion(cacheRegion);
        }
    }

    @Override
    public Set getDefinedFilterNames() {
        // assumption is that all session factories are configured the same way,
        // so it doesn't matter which session factory answers this question
        return getAnyFactory().getDefinedFilterNames();
    }

    @Override
    public FilterDefinition getFilterDefinition(final String filterName) throws HibernateException {
        // assumption is that all session factories are configured the same way,
        // so it doesn't matter which session factory answers this question
        return getAnyFactory().getFilterDefinition(filterName);
    }

    @Override
    public boolean containsFetchProfileDefinition(final String name) {
        for (final SessionFactory sf : sessionFactories) {
            if (sf.containsFetchProfileDefinition(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public TypeHelper getTypeHelper() {
        // assumption is that all session factories are configured the same way,
        // so it doesn't matter which session factory answers this question
        return getAnyFactory().getTypeHelper();
    }

    /**
     * Unsupported.  This is a scope decision, not a technical one.
     */
    @Override
    public Reference getReference() throws NamingException {
        throw new UnsupportedOperationException("Sharded session factories do not support References (sorry).");
    }

    @Override
    public IdentifierGenerator getIdentifierGenerator(final String rootEntityName) {
        // since all configs are same, we return any
        return getAnyFactory().getIdentifierGenerator(rootEntityName);
    }

    @Override
    public SessionImplementor openControlSession() {
        Preconditions.checkState(controlSessionFactory != null);
        Session session = controlSessionFactory.openSession();
        return (SessionImplementor) session;
    }

    @Override
    public boolean containsFactory(final SessionFactoryImplementor factory) {
        return sessionFactories.contains(factory);
    }

    private SessionFactoryImplementor getAnyFactory() {
        return sessionFactories.get(0);
    }

    @Override
    public List<SessionFactory> getSessionFactories() {
        return Collections.<SessionFactory>unmodifiableList(sessionFactories);
    }

    /**
     * Constructs a ShardedSessionFactory that operates on the given list of
     * shardIds. This operation is relatively lightweight as the returned
     * ShardedSessionFactory reuses existing shards. Most common use will be to
     * provide a ShardedSessionFactory that manages a subset of the application's
     * shards.
     *
     * @param shardIds
     * @param shardStrategyFactory
     * @return ShardedSessionFactory
     */
    @Override
    public ShardedSessionFactory getSessionFactory(final List<ShardId> shardIds,
                                                   final ShardStrategyFactory shardStrategyFactory) {
        return new SubsetShardedSessionFactoryImpl(
                shardIds,
                fullSessionFactoryShardIdMap,
                shardStrategyFactory,
                classesWithoutTopLevelSaveSupport,
                checkAllAssociatedObjectsForDifferentShards);
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            // try to be helpful to apps that don't clean up properly
            if (!isClosed()) {
                log.warn("ShardedSessionFactoryImpl is being garbage collected but it was never properly closed.");
                try {
                    close();
                } catch (Exception e) {
                    log.warn("Caught exception trying to close.", e);
                }
            }
        } finally {
            super.finalize();
        }
    }

    @Override
    public TypeResolver getTypeResolver() {
        // assumption is that all session factories are configured the same way,
        // so it doesn't matter which session factory answers this question
        return getAnyFactory().getTypeResolver();
    }

    @Override
    public Properties getProperties() {
        // assumption is that all session factories are configured the same way,
        // so it doesn't matter which session factory answers this question
        return getAnyFactory().getProperties();
    }

    @Override
    public EntityPersister getEntityPersister(final String entityName) throws MappingException {
        // assumption is that all session factories are configured the same way,
        // so it doesn't matter which session factory answers this question
        return getAnyFactory().getEntityPersister(entityName);
    }

    @Override
    public CollectionPersister getCollectionPersister(final String role) throws MappingException {
        // assumption is that all session factories are configured the same way,
        // so it doesn't matter which session factory answers this question
        return getAnyFactory().getCollectionPersister(role);
    }

    @Override
    public Dialect getDialect() {
        // assumption is that all session factories are configured the same way,
        // so it doesn't matter which session factory answers this question
        return getAnyFactory().getDialect();
    }

    @Override
    public Interceptor getInterceptor() {
        // assumption is that all session factories are configured the same way,
        // so it doesn't matter which session factory answers this question
        return getAnyFactory().getInterceptor();
    }

    @Override
    public QueryPlanCache getQueryPlanCache() {
        // assumption is that all session factories are configured the same way,
        // so it doesn't matter which session factory answers this question
        return getAnyFactory().getQueryPlanCache();
    }

    @Override
    public Type[] getReturnTypes(final String queryString) throws HibernateException {
        // assumption is that all session factories are configured the same way,
        // so it doesn't matter which session factory answers this question
        return getAnyFactory().getReturnTypes(queryString);
    }

    @Override
    public String[] getReturnAliases(final String queryString) throws HibernateException {
        // assumption is that all session factories are configured the same way,
        // so it doesn't matter which session factory answers this question
        return getAnyFactory().getReturnAliases(queryString);
    }

    @Override
    public ConnectionProvider getConnectionProvider() {
        // assumption is that all session factories are configured the same way,
        // so it doesn't matter which session factory answers this question
        return getAnyFactory().getConnectionProvider();
    }

    @Override
    public String[] getImplementors(final String className) throws MappingException {
        // assumption is that all session factories are configured the same way,
        // so it doesn't matter which session factory answers this question
        return getAnyFactory().getImplementors(className);
    }

    @Override
    public String getImportedClassName(final String name) {
        // assumption is that all session factories are configured the same way,
        // so it doesn't matter which session factory answers this question
        return getAnyFactory().getImportedClassName(name);
    }

    @Override
    public TransactionManager getTransactionManager() {
        // assumption is that all session factories are configured the same way,
        // so it doesn't matter which session factory answers this question
        return getAnyFactory().getTransactionManager();
    }

    @Override
    public QueryCache getQueryCache() {
        // assumption is that all session factories are configured the same way,
        // so it doesn't matter which session factory answers this question
        return getAnyFactory().getQueryCache();
    }

    @Override
    public QueryCache getQueryCache(final String regionName) throws HibernateException {
        // assumption is that all session factories are configured the same way,
        // so it doesn't matter which session factory answers this question
        return getAnyFactory().getQueryCache(regionName);
    }

    @Override
    public UpdateTimestampsCache getUpdateTimestampsCache() {
        // assumption is that all session factories are configured the same way,
        // so it doesn't matter which session factory answers this question
        return getAnyFactory().getUpdateTimestampsCache();
    }

    @Override
    public StatisticsImplementor getStatisticsImplementor() {
        // assumption is that all session factories are configured the same way,
        // so it doesn't matter which session factory answers this question
        return getAnyFactory().getStatisticsImplementor();
    }

    @Override
    public NamedQueryDefinition getNamedQuery(final String queryName) {
        // assumption is that all session factories are configured the same way,
        // so it doesn't matter which session factory answers this question
        return getAnyFactory().getNamedQuery(queryName);
    }

    @Override
    public NamedSQLQueryDefinition getNamedSQLQuery(final String queryName) {
        // assumption is that all session factories are configured the same way,
        // so it doesn't matter which session factory answers this question
        return getAnyFactory().getNamedSQLQuery(queryName);
    }

    @Override
    public ResultSetMappingDefinition getResultSetMapping(final String name) {
        // assumption is that all session factories are configured the same way,
        // so it doesn't matter which session factory answers this question
        return getAnyFactory().getResultSetMapping(name);
    }

    @Override
    public Region getSecondLevelCacheRegion(final String regionName) {
        // assumption is that all session factories are configured the same way,
        // so it doesn't matter which session factory answers this question
        return getAnyFactory().getSecondLevelCacheRegion(regionName);
    }

    @Override
    public Map getAllSecondLevelCacheRegions() {
        // assumption is that all session factories are configured the same way,
        // so it doesn't matter which session factory answers this question
        return getAnyFactory().getAllSecondLevelCacheRegions();
    }

    @Override
    public SQLExceptionConverter getSQLExceptionConverter() {
        // assumption is that all session factories are configured the same way,
        // so it doesn't matter which session factory answers this question
        return getAnyFactory().getSQLExceptionConverter();
    }

    @Override
    public Settings getSettings() {
        // assumption is that all session factories are configured the same way,
        // so it doesn't matter which session factory answers this question
        return getAnyFactory().getSettings();
    }

    @Override
    public Session openTemporarySession() throws HibernateException {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported.  This is a technical decision.  See {@link ShardedSessionFactoryImpl#openSession(Connection)}
     * for an explanation.
     */
    @Override
    public Session openSession(final Connection connection,
                               final boolean flushBeforeCompletionEnabled,
                               final boolean autoCloseSessionEnabled,
                               final ConnectionReleaseMode connectionReleaseMode) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> getCollectionRolesByEntityParticipant(final String entityName) {
        // assumption is that all session factories are configured the same way,
        // so it doesn't matter which session factory answers this question
        return getAnyFactory().getCollectionRolesByEntityParticipant(entityName);
    }

    @Override
    @Deprecated
    public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
        // assumption is that all session factories are configured the same way,
        // so it doesn't matter which session factory answers this question
        return getAnyFactory().getIdentifierGeneratorFactory();
    }

    @Override
    public Type getIdentifierType(final String className) throws MappingException {
        // assumption is that all session factories are configured the same way,
        // so it doesn't matter which session factory answers this question
        return getAnyFactory().getIdentifierType(className);
    }

    @Override
    public String getIdentifierPropertyName(final String className) throws MappingException {
        // assumption is that all session factories are configured the same way,
        // so it doesn't matter which session factory answers this question
        return getAnyFactory().getIdentifierPropertyName(className);
    }

    @Override
    public Type getReferencedPropertyType(final String className, final String propertyName) throws MappingException {
        // assumption is that all session factories are configured the same way,
        // so it doesn't matter which session factory answers this question
        return getAnyFactory().getReferencedPropertyType(className, propertyName);
    }

    @Override
    public EntityNotFoundDelegate getEntityNotFoundDelegate() {
        // assumption is that all session factories are configured the same way,
        // so it doesn't matter which session factory answers this question
        return getAnyFactory().getEntityNotFoundDelegate();
    }

    @Override
    public SQLFunctionRegistry getSqlFunctionRegistry() {
        // assumption is that all session factories are configured the same way,
        // so it doesn't matter which session factory answers this question
        return getAnyFactory().getSqlFunctionRegistry();
    }

    @Override
    public FetchProfile getFetchProfile(String name) {
        // assumption is that all session factories are configured the same way,
        // so it doesn't matter which session factory answers this question
        return getAnyFactory().getFetchProfile(name);
    }

    @Override
    public SessionFactoryObserver getFactoryObserver() {
        // assumption is that all session factories are configured the same way,
        // so it doesn't matter which session factory answers this question
        return getAnyFactory().getFactoryObserver();
    }
}

