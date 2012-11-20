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

import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.Filter;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.LobHelper;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.ReplicationMode;
import org.hibernate.SQLQuery;
import org.hibernate.SessionException;
import org.hibernate.Transaction;
import org.hibernate.TransientObjectException;
import org.hibernate.TypeHelper;
import org.hibernate.UnknownProfileException;
import org.hibernate.UnresolvableObjectException;
import org.hibernate.classic.Session;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.jdbc.Work;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.shards.CrossShardAssociationException;
import org.hibernate.shards.Shard;
import org.hibernate.shards.ShardId;
import org.hibernate.shards.ShardImpl;
import org.hibernate.shards.ShardOperation;
import org.hibernate.shards.ShardedTransaction;
import org.hibernate.shards.criteria.CriteriaFactoryImpl;
import org.hibernate.shards.criteria.CriteriaId;
import org.hibernate.shards.criteria.ShardedCriteriaImpl;
import org.hibernate.shards.engine.ShardedSessionFactoryImplementor;
import org.hibernate.shards.engine.ShardedSessionImplementor;
import org.hibernate.shards.id.ShardEncodingIdentifierGenerator;
import org.hibernate.shards.query.AdHocQueryFactoryImpl;
import org.hibernate.shards.query.ExitOperationsQueryCollector;
import org.hibernate.shards.query.NamedQueryFactoryImpl;
import org.hibernate.shards.query.QueryId;
import org.hibernate.shards.query.ShardedQueryImpl;
import org.hibernate.shards.query.ShardedSQLQueryImpl;
import org.hibernate.shards.stat.ShardedSessionStatistics;
import org.hibernate.shards.strategy.ShardStrategy;
import org.hibernate.shards.strategy.exit.FirstNonNullResultExitStrategy;
import org.hibernate.shards.strategy.selection.ShardResolutionStrategyData;
import org.hibernate.shards.strategy.selection.ShardResolutionStrategyDataImpl;
import org.hibernate.shards.transaction.ShardedTransactionImpl;
import org.hibernate.shards.util.InterceptorList;
import org.hibernate.shards.util.Iterables;
import org.hibernate.shards.util.Lists;
import org.hibernate.shards.util.Maps;
import org.hibernate.shards.util.Pair;
import org.hibernate.shards.util.Preconditions;
import org.hibernate.shards.util.Sets;
import org.hibernate.stat.SessionStatistics;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Concrete implementation of a ShardedSession, and also the central component of
 * Hibernate Shards' internal implementation. This class exposes two interfaces;
 * ShardedSession itself, to the application, and ShardedSessionImplementor, to
 * other components of Hibernate Shards. This class is not threadsafe.
 *
 * @author maxr@google.com (Max Ross)
 *         Tomislav Nad
 */
public class ShardedSessionImpl implements ShardedSession, ShardedSessionImplementor, ShardIdResolver {

    private static ThreadLocal<ShardId> currentSubgraphShardId = new ThreadLocal<ShardId>();

    private final ShardedSessionFactoryImplementor shardedSessionFactory;

    private final List<Shard> shards;

    private final Map<ShardId, Shard> shardIdsToShards;

    private final ShardStrategy shardStrategy;

    private final Set<Class<?>> classesWithoutTopLevelSaveSupport;

    private final boolean checkAllAssociatedObjectsForDifferentShards;

    private ShardedTransaction transaction;

    private boolean closed = false;

    private boolean lockedShard = false;

    private ShardId lockedShardId;

    // access to sharded session is single-threaded so we can use a non-atomic
    // counter for criteria ids or query ids
    private int nextCriteriaId = 0;
    private int nextQueryId = 0;

    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Constructor used for openSession(...) processing.
     *
     * @param shardedSessionFactory The factory from which this session was obtained
     * @param shardStrategy         The shard strategy for this session
     * @param classesWithoutTopLevelSaveSupport
     *                              The set of classes on which top-level save can not be performed
     * @param checkAllAssociatedObjectsForDifferentShards
     *                              Should we check for cross-shard relationships
     */
    ShardedSessionImpl(final ShardedSessionFactoryImplementor shardedSessionFactory,
                       final ShardStrategy shardStrategy,
                       final Set<Class<?>> classesWithoutTopLevelSaveSupport,
                       final boolean checkAllAssociatedObjectsForDifferentShards) {

        this(null, shardedSessionFactory, shardStrategy, classesWithoutTopLevelSaveSupport,
                checkAllAssociatedObjectsForDifferentShards);
    }

    /**
     * Constructor used for openSession(...) processing.
     *
     * @param interceptor           The interceptor to be applied to this session
     * @param shardedSessionFactory The factory from which this session was obtained
     * @param shardStrategy         The shard strategy for this session
     * @param classesWithoutTopLevelSaveSupport
     *                              The set of classes on which top-level save can not be performed
     * @param checkAllAssociatedObjectsForDifferentShards
     *                              Should we check for cross-shard relationships
     */
    ShardedSessionImpl(final /*@Nullable*/ Interceptor interceptor,
                       final ShardedSessionFactoryImplementor shardedSessionFactory,
                       final ShardStrategy shardStrategy,
                       final Set<Class<?>> classesWithoutTopLevelSaveSupport,
                       final boolean checkAllAssociatedObjectsForDifferentShards) {

        this.shardedSessionFactory = shardedSessionFactory;
        this.shards = buildShardListFromSessionFactoryShardIdMap(
                shardedSessionFactory.getSessionFactoryShardIdMap(),
                checkAllAssociatedObjectsForDifferentShards,
                this,
                interceptor);

        this.shardIdsToShards = buildShardIdsToShardsMap();
        this.shardStrategy = shardStrategy;
        this.classesWithoutTopLevelSaveSupport = classesWithoutTopLevelSaveSupport;
        this.checkAllAssociatedObjectsForDifferentShards = checkAllAssociatedObjectsForDifferentShards;
    }

    private Map<ShardId, Shard> buildShardIdsToShardsMap() {
        final Map<ShardId, Shard> map = Maps.newHashMap();
        for (final Shard shard : shards) {
            for (final ShardId shardId : shard.getShardIds()) {
                map.put(shardId, shard);
            }
        }
        return map;
    }

    static List<Shard> buildShardListFromSessionFactoryShardIdMap(
            final Map<SessionFactoryImplementor, Set<ShardId>> sessionFactoryShardIdMap,
            final boolean checkAllAssociatedObjectsForDifferentShards,
            final ShardIdResolver shardIdResolver,
            final /*@Nullable*/ Interceptor interceptor) {

        final List<Shard> shardList = Lists.newArrayList();
        for (final Map.Entry<SessionFactoryImplementor, Set<ShardId>> entry : sessionFactoryShardIdMap.entrySet()) {
            final Pair<InterceptorList, SetSessionOnRequiresSessionEvent> pair = buildInterceptorList(
                    interceptor,
                    shardIdResolver,
                    checkAllAssociatedObjectsForDifferentShards);
            final Shard shard = new ShardImpl(entry.getValue(), entry.getKey(), pair.first);
            shardList.add(shard);
            if (pair.second != null) {
                shard.addOpenSessionEvent(pair.second);
            }
        }
        return shardList;
    }

    /**
     * Construct an {@link InterceptorList} with all the interceptors we'll want
     * to register when we create a {@link ShardedSessionImpl}.
     *
     * @param providedInterceptor the {@link Interceptor} passed in by the client
     * @param shardIdResolver     knows how to resolve a {@link ShardId} from an object
     * @param checkAllAssociatedObjectsForDifferentShards
     *                            true if cross-shard
     *                            relationship detection is enabled
     * @return
     */
    static Pair<InterceptorList, SetSessionOnRequiresSessionEvent> buildInterceptorList(
            Interceptor providedInterceptor,
            final ShardIdResolver shardIdResolver,
            final boolean checkAllAssociatedObjectsForDifferentShards) {

        // everybody gets a ShardAware interceptor
        final List<Interceptor> interceptorList = Lists.<Interceptor>newArrayList(new ShardAwareInterceptor(shardIdResolver));
        if (checkAllAssociatedObjectsForDifferentShards) {
            // cross shard association checks during updates are handled using interceptors
            final CrossShardRelationshipDetectingInterceptor csrdi =
                    new CrossShardRelationshipDetectingInterceptor(shardIdResolver);
            interceptorList.add(csrdi);
        }

        SetSessionOnRequiresSessionEvent openSessionEvent = null;
        if (providedInterceptor != null) {
            // user-provided an interceptor
            if (providedInterceptor instanceof StatefulInterceptorFactory) {
                // it's stateful so we need to create a new one for each shard
                providedInterceptor = ((StatefulInterceptorFactory) providedInterceptor).newInstance();
                if (providedInterceptor instanceof RequiresSession) {
                    openSessionEvent = new SetSessionOnRequiresSessionEvent((RequiresSession) providedInterceptor);
                }
            }
            interceptorList.add(providedInterceptor);
        }

        return Pair.of(new InterceptorList(interceptorList), openSessionEvent);
    }

    private Object applyGetOperation(final ShardOperation<Object> shardOp, final ShardResolutionStrategyData srsd) {
        final List<ShardId> shardIds = selectShardIdsFromShardResolutionStrategyData(srsd);
        return shardStrategy.getShardAccessStrategy().apply(
                shardIdListToShardList(shardIds),
                shardOp,
                new FirstNonNullResultExitStrategy<Object>(),
                new ExitOperationsQueryCollector());
    }

    private List<Shard> shardIdListToShardList(final List<ShardId> shardIds) {
        final Set<Shard> shards = Sets.newHashSet();
        for (final ShardId shardId : shardIds) {
            shards.add(shardIdsToShards.get(shardId));
        }
        return Lists.newArrayList(shards);
    }

    @Override
    public List<Shard> getShards() {
        return Collections.unmodifiableList(shards);
    }

    @Override
    public Object get(final Class clazz, final Serializable id) throws HibernateException {
        final ShardOperation<Object> shardOp = new ShardOperation<Object>() {
            public Object execute(Shard shard) {
                return shard.establishSession().get(clazz, id);
            }

            public String getOperationName() {
                return "get(Class class, Serializable id)";
            }
        };

        return applyGetOperation(shardOp, new ShardResolutionStrategyDataImpl(clazz, id));
    }

    @Deprecated
    @Override
    public Object get(final Class clazz, final Serializable id, final LockMode lockMode) throws HibernateException {
        return get(clazz, id, new LockOptions(lockMode));
    }

    @Override
    public Object get(final Class clazz, final Serializable id, final LockOptions lockOptions) throws HibernateException {

        final ShardOperation<Object> shardOp = new ShardOperation<Object>() {

            @Override
            public Object execute(Shard shard) {
                return shard.establishSession().get(clazz, id, lockOptions);
            }

            @Override
            public String getOperationName() {
                return "get(Class clazz, Serializable id, LockOptions lockOptions)";
            }
        };

        return applyGetOperation(shardOp, new ShardResolutionStrategyDataImpl(clazz, id));
    }

    @Override
    public Object get(final String entityName, final Serializable id) throws HibernateException {

        final ShardOperation<Object> shardOp = new ShardOperation<Object>() {

            @Override
            public Object execute(Shard shard) {
                return shard.establishSession().get(entityName, id);
            }

            @Override
            public String getOperationName() {
                return "get(String entityName, Serializable id)";
            }
        };

        return applyGetOperation(shardOp, new ShardResolutionStrategyDataImpl(entityName, id));
    }

    @Deprecated
    @Override
    public Object get(final String entityName, final Serializable id, final LockMode lockMode)
            throws HibernateException {

        return get(entityName, id, new LockOptions(lockMode));
    }

    @Override
    public Object get(final String entityName, final Serializable id, final LockOptions lockOptions)
            throws HibernateException {

        final ShardOperation<Object> shardOp = new ShardOperation<Object>() {

            @Override
            public Object execute(final Shard shard) {
                return shard.establishSession().get(entityName, id, lockOptions);
            }

            @Override
            public String getOperationName() {
                return "get(String entityName, Serializable id, LockOptions lockOptions)";
            }
        };

        // we're not letting people customize shard selection by lockMode
        return applyGetOperation(shardOp, new ShardResolutionStrategyDataImpl(entityName, id));
    }

    private Session getSomeSession() {
        for (final Shard shard : shards) {
            if (shard.getSession() != null) {
                return shard.getSession();
            }
        }
        return null;
    }

    @Override
    public EntityMode getEntityMode() {
        // assume they all have the same EntityMode
        Session someSession = getSomeSession();
        if (someSession == null) {
            someSession = shards.get(0).establishSession();
        }
        return someSession.getEntityMode();
    }

    /**
     * Unsupported.  This is a scope decision, not a technical decision.
     */
    @Override
    public Session getSession(final EntityMode entityMode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void flush() throws HibernateException {
        for (final Shard shard : shards) {
            // unopened sessions won't have anything to flush
            if (shard.getSession() != null) {
                shard.getSession().flush();
            }
        }
    }

    @Override
    public void setFlushMode(final FlushMode flushMode) {
        setOpenSessionEvent(new SetFlushModeOpenSessionEvent(flushMode));
    }

    @Override
    public FlushMode getFlushMode() {
        // all shards must have the same flush mode
        Session someSession = getSomeSession();
        if (someSession == null) {
            someSession = shards.get(0).establishSession();
        }
        return someSession.getFlushMode();
    }

    @Override
    public void setCacheMode(final CacheMode cacheMode) {
        setOpenSessionEvent(new SetCacheModeOpenSessionEvent(cacheMode));
    }

    @Override
    public CacheMode getCacheMode() {
        // all shards must have the same cache mode
        Session someSession = getSomeSession();
        if (someSession == null) {
            someSession = shards.get(0).establishSession();
        }
        return someSession.getCacheMode();
    }

    @Override
    public ShardedSessionFactoryImplementor getSessionFactory() {
        return shardedSessionFactory;
    }

    /**
     * @deprecated
     */
    @Override
    @Deprecated
    public Connection connection() throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Connection close() throws HibernateException {
        List<Throwable> thrown = null;
        for (final Shard shard : shards) {
            if (shard.getSession() != null) {
                try {
                    shard.getSession().close();
                } catch (Throwable t) {
                    if (thrown == null) {
                        thrown = Lists.newArrayList();
                    }
                    thrown.add(t);
                    // we're going to try and close everything that was
                    // opened
                }
            }
        }

        shards.clear();
        shardIdsToShards.clear();
        classesWithoutTopLevelSaveSupport.clear();

        if (thrown != null && !thrown.isEmpty()) {
            // we'll just throw the first one
            final Throwable first = thrown.get(0);
            if (HibernateException.class.isAssignableFrom(first.getClass())) {
                throw (HibernateException) first;
            }
            throw new HibernateException(first);
        }
        closed = true;

        // TODO(maxr) what should I return here?
        return null;
    }

    @Override
    public void cancelQuery() throws HibernateException {
        // cancel across all shards
        for (final Shard shard : shards) {
            if (shard.getSession() != null) {
                shard.getSession().cancelQuery();
            }
        }
    }

    @Override
    public boolean isOpen() {
        return !closed;
    }

    @Override
    public boolean isConnected() {
        // one connected shard means the session as a whole is connected
        for (final Shard shard : shards) {
            if (shard.getSession() != null && shard.getSession().isConnected()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isDirty() throws HibernateException {
        // one dirty shard is all it takes
        for (final Shard shard : shards) {
            if (shard.getSession() != null && shard.getSession().isDirty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isDefaultReadOnly() {
        // one read only by default shard is all it takes
        for (final Shard shard : shards) {
            if (shard.getSession() != null && shard.getSession().isDefaultReadOnly()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setDefaultReadOnly(final boolean readOnly) {
        setOpenSessionEvent(new SetDefaultReadOnlyOpenSessionEvent(readOnly));
    }

    @Override
    public Serializable getIdentifier(final Object object) throws HibernateException {
        for (final Shard shard : shards) {
            if (shard.getSession() != null) {
                try {
                    return shard.getSession().getIdentifier(object);
                } catch (TransientObjectException e) {
                    // Object is transient or is not associated with this session.
                }
            }
        }

        throw new TransientObjectException("Instance is transient or associated with a defferent Session");
    }

    @Override
    public boolean contains(final Object object) {
        for (final Shard shard : shards) {
            if (shard.getSession() != null) {
                if (shard.getSession().contains(object)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void evict(final Object object) throws HibernateException {
        for (final Shard shard : shards) {
            if (shard.getSession() != null) {
                shard.getSession().evict(object);
            }
        }
    }

    @Deprecated
    @Override
    public Object load(final Class clazz, final Serializable id, final LockMode lockMode) throws HibernateException {
        return load(clazz, id, new LockOptions(lockMode));
    }

    @Override
    public Object load(final Class theClass, final Serializable id, final LockOptions lockOptions) throws HibernateException {

        final List<ShardId> shardIds = selectShardIdsFromShardResolutionStrategyData(
                new ShardResolutionStrategyDataImpl(theClass, id));

        if (shardIds.size() == 1) {
            return shardIdsToShards.get(shardIds.get(0)).establishSession().load(theClass, id, lockOptions);
        }

        final Object result = get(theClass, id, lockOptions);
        if (result == null) {
            shardedSessionFactory.getEntityNotFoundDelegate().handleEntityNotFound(theClass.getName(), id);
        }
        return result;
    }

    @Deprecated
    @Override
    public Object load(final String entityName, final Serializable id, final LockMode lockMode)
            throws HibernateException {

        return load(entityName, id, new LockOptions(lockMode));
    }

    @Override
    public Object load(final String entityName, final Serializable id, final LockOptions lockOptions)
            throws HibernateException {

        final List<ShardId> shardIds = selectShardIdsFromShardResolutionStrategyData(
                new ShardResolutionStrategyDataImpl(entityName, id));

        if (shardIds.size() == 1) {
            return shardIdsToShards.get(shardIds.get(0)).establishSession().load(entityName, id, lockOptions);
        }

        final Object result = get(entityName, id, lockOptions);
        if (result == null) {
            shardedSessionFactory.getEntityNotFoundDelegate().handleEntityNotFound(entityName, id);
        }
        return result;
    }

    @Override
    public Object load(final Class clazz, final Serializable id) throws HibernateException {

        final List<ShardId> shardIds = selectShardIdsFromShardResolutionStrategyData(
                new ShardResolutionStrategyDataImpl(clazz, id));

        if (shardIds.size() == 1) {
            return shardIdsToShards.get(shardIds.get(0)).establishSession().load(clazz, id);
        }

        final Object result = get(clazz, id);
        if (result == null) {
            shardedSessionFactory.getEntityNotFoundDelegate().handleEntityNotFound(clazz.getName(), id);
        }
        return result;
    }

    @Override
    public Object load(final String entityName, final Serializable id) throws HibernateException {

        final List<ShardId> shardIds = selectShardIdsFromShardResolutionStrategyData(
                new ShardResolutionStrategyDataImpl(entityName, id));

        if (shardIds.size() == 1) {
            return shardIdsToShards.get(shardIds.get(0)).establishSession().load(entityName, id);
        }

        final Object result = get(entityName, id);
        if (result == null) {
            shardedSessionFactory.getEntityNotFoundDelegate().handleEntityNotFound(entityName, id);
        }
        return result;
    }

    @Override
    public void load(final Object object, final Serializable id) throws HibernateException {

        final List<ShardId> shardIds = selectShardIdsFromShardResolutionStrategyData(
                new ShardResolutionStrategyDataImpl(object.getClass(), id));

        if (shardIds.size() == 1) {
            shardIdsToShards.get(shardIds.get(0)).establishSession().load(object, id);
        }

        final Object result = get(object.getClass(), id);
        if (result == null) {
            shardedSessionFactory.getEntityNotFoundDelegate().handleEntityNotFound(object.getClass().getName(), id);
        } else {
            Shard objectShard = getShardForObject(result, shardIdListToShardList(shardIds));
            evict(result);
            objectShard.establishSession().load(object, id);
        }
    }

    @Override
    public void replicate(final Object object, final ReplicationMode replicationMode) throws HibernateException {
        replicate(null, object, replicationMode);
    }

    @Override
    public void replicate(final String entityName, final Object object, final ReplicationMode replicationMode)
            throws HibernateException {

        final Serializable id = extractId(object);
        final List<ShardId> shardIds = selectShardIdsFromShardResolutionStrategyData(
                new ShardResolutionStrategyDataImpl(object.getClass(), id));

        if (shardIds.size() == 1) {
            setCurrentSubgraphShardId(shardIds.get(0));
            shardIdsToShards.get(shardIds.get(0)).establishSession().replicate(entityName, object, replicationMode);
        } else {
            Object result = null;
            if (id != null) {
                result = get(object.getClass(), id);
            }

            if (result == null) {  // non-persisted object
                final ShardId shardId = selectShardIdForNewObject(object);
                setCurrentSubgraphShardId(shardId);
                shardIdsToShards.get(shardId).establishSession().replicate(entityName, object, replicationMode);
            } else {
                final Shard objectShard = getShardForObject(result, shardIdListToShardList(shardIds));
                evict(result);
                objectShard.establishSession().replicate(entityName, object, replicationMode);
            }
        }
    }

    @Override
    public Serializable save(final String entityName, final Object object) throws HibernateException {
        // TODO(tomislav): what if we have detached instance?
        ShardId shardId = getShardIdForObject(object);
        if (shardId == null) {
            shardId = selectShardIdForNewObject(object);
        }
        Preconditions.checkNotNull(shardId);
        setCurrentSubgraphShardId(shardId);
        log.debug(String.format("Saving object of type %s to shard %s", object.getClass(), shardId));
        return shardIdsToShards.get(shardId).establishSession().save(entityName, object);
    }

    ShardId selectShardIdForNewObject(final Object obj) {
        if (lockedShardId != null) {
            return lockedShardId;
        }
        ShardId shardId;
    /*
     * Someone is trying to save this object, and that's wonderful, but if
     * this object references or is referenced by any other objects that have already been
     * associated with a session it's important that this object end up
     * associated with the same session.  In order to make sure that happens,
     * we're going to look at the metadata for this object and see what
     * references we have, and then use those to determine the proper shard.
     * If we can't find any references we'll leave it up to the shard selection
     * strategy.
     */
        shardId = getShardIdOfRelatedObject(obj);
        if (shardId == null) {
            checkForUnsupportedTopLevelSave(obj.getClass());
            shardId = shardStrategy.getShardSelectionStrategy().selectShardIdForNewObject(obj);
        }
        // lock has been requested but shard has not yet been selected - lock it in
        if (lockedShard) {
            lockedShardId = shardId;
        }
        log.debug(String.format("Selected shard %d for object of type %s", shardId.getId(), obj.getClass().getName()));
        return shardId;
    }

    /*
     * We already know that we don't have a shardId locked in for this session,
     * and we already know that this object can't grab its session from some
     * other object (we looked).  If this class is in the set of classes
     * that don't support top-level saves, it's an error.
     * This is to prevent clients from accidentally splitting their object graphs
     * across multiple shards.
     */
    private void checkForUnsupportedTopLevelSave(final Class<?> clazz) {
        if (classesWithoutTopLevelSaveSupport.contains(clazz)) {
            final String msg = String.format("Attempt to save object of type %s as a top-level object.",
                    clazz.getName());
            log.error(msg);
            throw new HibernateException(msg);
        }
    }

    /**
     * TODO(maxr) I can see this method benefitting from a cache that lets us quickly
     * see which properties we might need to look at.
     */
    ShardId getShardIdOfRelatedObject(final Object obj) {
        final ClassMetadata cmd = getClassMetadata(obj.getClass());
        Type[] types = cmd.getPropertyTypes();
        // TODO(maxr) fix hard-coded entity mode
        final Object[] values = cmd.getPropertyValues(obj, EntityMode.POJO);
        ShardId shardId = null;
        List<Collection<Object>> collections = null;
        for (final Pair<Type, Object> pair : CrossShardRelationshipDetectingInterceptor.buildListOfAssociations(types, values)) {
            if (pair.getFirst().isCollectionType()) {
                /**
                 * collection types are more expensive to evaluate (might involve
                 * lazy-loading the contents of the collection from the db), so
                 * let's hold off until the end on the chance that we can fail
                 * quickly.
                 */
                if (collections == null) {
                    collections = Lists.newArrayList();
                }
                @SuppressWarnings("unchecked")
                Collection<Object> coll = (Collection<Object>) pair.getSecond();
                collections.add(coll);
            } else {
                shardId = checkForConflictingShardId(shardId, obj.getClass(), pair.getSecond());
                /**
                 * if we're not checking for different shards, return as soon as we've
                 * got one
                 */
                if (shardId != null && !checkAllAssociatedObjectsForDifferentShards) {
                    return shardId;
                }
            }
        }
        if (collections != null) {
            for (final Object collEntry : Iterables.concat(collections)) {
                shardId = checkForConflictingShardId(shardId, obj.getClass(), collEntry);
                if (shardId != null && !checkAllAssociatedObjectsForDifferentShards) {
                    /**
                     * if we're not checking for different shards, return as soon as we've
                     * got one
                     */
                    return shardId;
                }
            }
        }
        return shardId;
    }

    ShardId checkForConflictingShardId(ShardId existingShardId,
                                       final Class<?> newObjectClass, final Object associatedObject) {

        final ShardId localShardId = getShardIdForObject(associatedObject);
        if (localShardId != null) {
            if (existingShardId == null) {
                existingShardId = localShardId;
            } else if (!localShardId.equals(existingShardId)) {
                final String msg = String.format(
                        "Object of type %s is on shard %d but an associated object of type %s is on shard %d.",
                        newObjectClass.getName(),
                        existingShardId.getId(),
                        associatedObject.getClass().getName(),
                        localShardId.getId());
                log.error(msg);
                throw new CrossShardAssociationException(msg);
            }
        }
        return existingShardId;
    }

    ClassMetadata getClassMetadata(final Class<?> clazz) {
        return getSessionFactory().getClassMetadata(clazz);
    }

    @Override
    public Serializable save(final Object object) throws HibernateException {
        return save(null, object);
    }

    @Override
    public void saveOrUpdate(final Object object) throws HibernateException {
        applySaveOrUpdateOperation(SAVE_OR_UPDATE_SIMPLE, object);
    }

    @Override
    public void saveOrUpdate(final String entityName, Object object) throws HibernateException {

        final SaveOrUpdateOperation op = new SaveOrUpdateOperation() {

            @Override
            public void saveOrUpdate(final Shard shard, final Object object) {
                shard.establishSession().saveOrUpdate(entityName, object);
            }

            @Override
            public void merge(final Shard shard, final Object object) {
                shard.establishSession().merge(entityName, object);
            }
        };

        applySaveOrUpdateOperation(op, object);
    }

    void applySaveOrUpdateOperation(final SaveOrUpdateOperation op, final Object object) {

        ShardId shardId = getShardIdForObject(object);
        if (shardId != null) {
            // attached object
            op.saveOrUpdate(shardIdsToShards.get(shardId), object);
            return;
        }

        final List<Shard> potentialShards = determineShardsObjectViaResolutionStrategy(object);
        if (potentialShards.size() == 1) {
            op.saveOrUpdate(potentialShards.get(0), object);
            return;
        }

        /**
         * Too bad, we've got a detached object that could be on more than 1 shard.
         * The only safe way to handle this is to try and lookup the object, and if
         * it exists, do a merge, and if it doesn't, do a save.
         */
        final Serializable id = extractId(object);
        if (id != null) {
            Object persistent = get(object.getClass(), id);
            if (persistent != null) {
                shardId = getShardIdForObject(persistent);
            }
        }

        if (shardId != null) {
            op.merge(shardIdsToShards.get(shardId), object);
        } else {
            save(object);
        }
    }

    Serializable extractId(final Object object) {
        final ClassMetadata cmd = shardedSessionFactory.getClassMetadata(object.getClass());
        Preconditions.checkState(cmd != null);
        // I'm just guessing about the EntityMode
        return cmd.getIdentifier(object, EntityMode.POJO);
    }

    private static final UpdateOperation SIMPLE_UPDATE_OPERATION = new UpdateOperation() {

        @Override
        public void update(final Shard shard, final Object object) {
            shard.establishSession().update(object);
        }

        @Override
        public void merge(final Shard shard, final Object object) {
            shard.establishSession().merge(object);
        }
    };

    private void applyUpdateOperation(final UpdateOperation op, final Object object) {

        ShardId shardId = getShardIdForObject(object);
        if (shardId != null) {
            // attached object
            op.update(shardIdsToShards.get(shardId), object);
            return;
        }

        final List<Shard> potentialShards = determineShardsObjectViaResolutionStrategy(object);
        if (potentialShards.size() == 1) {
            op.update(potentialShards.get(0), object);
            return;
        }

        /**
         * Too bad, we've got a detached object that could be on more than 1 shard.
         * The only safe way to perform the update is to load the object and then
         * do a merge.
         */
        final Serializable id = extractId(object);
        if (id != null) {
            final Object persistent = get(object.getClass(), extractId(object));
            if (persistent != null) {
                shardId = getShardIdForObject(persistent);
            }
        }

        if (shardId == null) {
            /**
             * This is an error condition.  In order to provide the same behavior
             * as a non-sharded session we're just going to dispatch the update
             * to a random shard (we know it will fail because either we don't have
             * an id or the lookup returned).
             */
            op.update(getShards().get(0), object);
            // this call may succeed but the commit will fail
        } else {
            op.merge(shardIdsToShards.get(shardId), object);
        }
    }

    public void update(final Object object) throws HibernateException {
        applyUpdateOperation(SIMPLE_UPDATE_OPERATION, object);
    }

    @Override
    public void update(final String entityName, final Object object) throws HibernateException {
        final UpdateOperation op = new UpdateOperation() {

            @Override
            public void update(final Shard shard, final Object object) {
                shard.establishSession().update(entityName, object);
            }

            @Override
            public void merge(final Shard shard, final Object object) {
                shard.establishSession().merge(entityName, object);
            }
        };

        applyUpdateOperation(op, object);
    }

    List<Shard> determineShardsObjectViaResolutionStrategy(final Object object) {
        final Serializable id = extractId(object);
        if (id == null) {
            return Collections.emptyList();
        }
        final ShardResolutionStrategyData srsd = new ShardResolutionStrategyDataImpl(object.getClass(), id);
        final List<ShardId> shardIds = selectShardIdsFromShardResolutionStrategyData(srsd);
        return shardIdListToShardList(shardIds);
    }

    @Override
    public Object merge(final Object object) throws HibernateException {
        return merge(null, object);
    }

    @Override
    public Object merge(final String entityName, final Object object) throws HibernateException {
        final Serializable id = extractId(object);
        final List<ShardId> shardIds = selectShardIdsFromShardResolutionStrategyData(new
                ShardResolutionStrategyDataImpl(object.getClass(), id));

        if (shardIds.size() == 1) {
            setCurrentSubgraphShardId(shardIds.get(0));
            return shardIdsToShards.get(shardIds.get(0)).establishSession().merge(entityName, object);
        } else {
            Object result = null;
            if (id != null) {
                result = get(object.getClass(), id);
            }
            if (result == null) {  // non-persisted object
                final ShardId shardId = selectShardIdForNewObject(object);
                setCurrentSubgraphShardId(shardId);
                return shardIdsToShards.get(shardId).establishSession().merge(entityName, object);
            } else {
                final Shard objectShard = getShardForObject(result, shardIdListToShardList(shardIds));
                return objectShard.establishSession().merge(entityName, object);
            }
        }
    }

    @Override
    public void persist(final Object object) throws HibernateException {
        persist(null, object);
    }

    @Override
    public void persist(final String entityName, final Object object) throws HibernateException {

        // TODO(tomislav): what if we have detached object?
        ShardId shardId = getShardIdForObject(object);
        if (shardId == null) {
            shardId = selectShardIdForNewObject(object);
        }
        Preconditions.checkNotNull(shardId);
        setCurrentSubgraphShardId(shardId);
        log.debug(String.format("Persisting object of type %s to shard %s", object.getClass(), shardId));
        shardIdsToShards.get(shardId).establishSession().persist(entityName, object);
    }

    private void applyDeleteOperation(final DeleteOperation op, final Object object) {
        ShardId shardId = getShardIdForObject(object);
        if (shardId != null) {
            // attached object
            op.delete(shardIdsToShards.get(shardId), object);
            return;
        }
        /**
         * Detached object.
         * We can't just try to delete on each shard because if you have an
         * object associated with Session x and you try to delete that object in
         * Session y, and if that object has persistent collections, Hibernate will
         * blow up because it will try to associate the persistent collection with
         * a different Session as part of the cascade.  In order to avoid this we
         * need to be precise about the shard on which we perform the delete.
         *
         * First let's see if we can derive the shard just from the object's id.
         */
        final List<Shard> potentialShards = determineShardsObjectViaResolutionStrategy(object);
        if (potentialShards.size() == 1) {
            op.delete(potentialShards.get(0), object);
            return;
        }
        /**
         * Too bad, we've got a detached object that could be on more than 1 shard.
         * The only safe way to perform the delete is to load the object before
         * deleting.
         */
        final Object persistent = get(object.getClass(), extractId(object));
        shardId = getShardIdForObject(persistent);
        op.delete(shardIdsToShards.get(shardId), persistent);
    }

    private static final DeleteOperation SIMPLE_DELETE_OPERATION = new DeleteOperation() {
        @Override
        public void delete(final Shard shard, final Object object) {
            shard.establishSession().delete(object);
        }
    };

    @Override
    public void delete(final Object object) throws HibernateException {
        applyDeleteOperation(SIMPLE_DELETE_OPERATION, object);
    }

    @Override
    public void delete(final String entityName, final Object object) throws HibernateException {
        final DeleteOperation op = new DeleteOperation() {
            @Override
            public void delete(Shard shard, Object object) {
                shard.establishSession().delete(entityName, object);
            }
        };
        applyDeleteOperation(op, object);
    }

    @Deprecated
    @Override
    public void lock(final Object object, final LockMode lockMode) throws HibernateException {
        buildLockRequest(new LockOptions(lockMode)).lock(object);
    }

    @Deprecated
    @Override
    public void lock(final String entityName, final Object object, final LockMode lockMode) throws HibernateException {
        buildLockRequest(new LockOptions(lockMode)).lock(entityName, object);
    }

    @Override
    public LockRequest buildLockRequest(final LockOptions lockOptions) {
        return new ShardedLockRequest(this, lockOptions);
    }

    private void applyRefreshOperation(final RefreshOperation op, final Object object) {
        final ShardId shardId = getShardIdForObject(object);
        if (shardId != null) {
            op.refresh(shardIdsToShards.get(shardId), object);
        } else {
            final List<Shard> candidateShards = determineShardsObjectViaResolutionStrategy(object);
            if (candidateShards.size() == 1) {
                op.refresh(candidateShards.get(0), object);
            } else {
                for (final Shard shard : candidateShards) {
                    try {
                        op.refresh(shard, object);
                        return;
                    } catch (UnresolvableObjectException uoe) {
                        // ignore
                    }
                }
                op.refresh(shards.get(0), object);
            }
        }
    }

    @Override
    public void refresh(final Object object) throws HibernateException {
        final RefreshOperation op = new RefreshOperation() {

            @Override
            public void refresh(Shard shard, Object object) {
                shard.establishSession().refresh(object);
            }
        };

        applyRefreshOperation(op, object);
    }

    @Deprecated
    @Override
    public void refresh(final Object object, final LockMode lockMode) throws HibernateException {
        refresh(object, new LockOptions(lockMode));
    }

    @Override
    public void refresh(final Object object, final LockOptions lockOptions) throws HibernateException {

        final RefreshOperation op = new RefreshOperation() {

            @Override
            public void refresh(Shard shard, Object object) {
                shard.establishSession().refresh(object, lockOptions);
            }
        };

        applyRefreshOperation(op, object);
    }

    @Override
    public LockMode getCurrentLockMode(final Object object) throws HibernateException {
        final ShardOperation<LockMode> invoker = new ShardOperation<LockMode>() {
            public LockMode execute(final Shard s) {
                return s.establishSession().getCurrentLockMode(object);
            }

            public String getOperationName() {
                return "getCurrentLockmode(Object object)";
            }
        };
        return invokeOnShardWithObject(invoker, object);
    }

    @Override
    public Transaction beginTransaction() throws HibernateException {
        errorIfClosed();
        final Transaction result = getTransaction();
        result.begin();
        return result;
    }

    @Override
    public Transaction getTransaction() {
        errorIfClosed();
        if (transaction == null) {
            transaction = new ShardedTransactionImpl(this);
        }
        return transaction;
    }

    @Override
    public Criteria createCriteria(final Class persistentClass) {
        return new ShardedCriteriaImpl(
                new CriteriaId(nextCriteriaId++),
                shards,
                new CriteriaFactoryImpl(persistentClass),
                shardStrategy.getShardAccessStrategy());
    }

    @Override
    public Criteria createCriteria(final Class persistentClass, final String alias) {
        return new ShardedCriteriaImpl(
                new CriteriaId(nextCriteriaId++),
                shards,
                new CriteriaFactoryImpl(persistentClass, alias),
                shardStrategy.getShardAccessStrategy());
    }

    @Override
    public Criteria createCriteria(final String entityName) {
        return new ShardedCriteriaImpl(
                new CriteriaId(nextCriteriaId++),
                shards,
                new CriteriaFactoryImpl(entityName),
                shardStrategy.getShardAccessStrategy());
    }

    public Criteria createCriteria(final String entityName, final String alias) {
        return new ShardedCriteriaImpl(
                new CriteriaId(nextCriteriaId++),
                shards,
                new CriteriaFactoryImpl(entityName, alias),
                shardStrategy.getShardAccessStrategy());
    }

    @Override
    public Query createQuery(String queryString) throws HibernateException {
        return new ShardedQueryImpl(new QueryId(nextQueryId++),
                shards,
                new AdHocQueryFactoryImpl(queryString),
                shardStrategy.getShardAccessStrategy());
    }

    @Override
    public SQLQuery createSQLQuery(final String queryString) throws HibernateException {
        return new ShardedSQLQueryImpl(new QueryId(nextQueryId++),
                shards,
                new AdHocQueryFactoryImpl(queryString),
                shardStrategy.getShardAccessStrategy());
    }

    /**
     * The {@link org.hibernate.impl.SessionImpl#createFilter(Object, String)} implementation
     * requires that the collection that is passed in is a persistent collection.
     * Since we don't support cross-shard relationships, if we receive a persistent
     * collection that collection is guaranteed to be associated with a single
     * shard.  If we can figure out which shard the collection is associated with
     * we can just delegate this operation to that shard.
     */
    @Override
    public Query createFilter(final Object collection, final String queryString) throws HibernateException {
        final Shard shard = getShardForCollection(collection, shards);
        Session session;
        if (shard == null) {
            // collection not associated with any of our shards, so just delegate to
            // a random shard.  We'll end up failing, but we'll fail with the
            // error that users typically get.
            session = getSomeSession();
            if (session == null) {
                session = shards.get(0).establishSession();
            }
        } else {
            session = shard.establishSession();
        }
        return session.createFilter(collection, queryString);
    }

    @Override
    public Query getNamedQuery(final String queryName) throws HibernateException {
        return new ShardedQueryImpl(new QueryId(nextQueryId++),
                shards,
                new NamedQueryFactoryImpl(queryName),
                shardStrategy.getShardAccessStrategy());
    }

    @Override
    public void clear() {
        for (final Shard shard : shards) {
            if (shard.getSession() != null) {
                shard.getSession().clear();
            }
        }
    }

    @Override
    public String getEntityName(final Object object) throws HibernateException {

        final ShardOperation<String> invoker = new ShardOperation<String>() {

            @Override
            public String execute(Shard s) {
                return s.establishSession().getEntityName(object);
            }

            @Override
            public String getOperationName() {
                return "getEntityName(Object object)";
            }
        };

        return invokeOnShardWithObject(invoker, object);
    }

    @Override
    public Filter enableFilter(final String filterName) {
        setOpenSessionEvent(new EnableFilterOpenSessionEvent(filterName));

        // TODO(maxr) what do we return here?  A sharded filter?
        return null;
    }

    @Override
    public Filter getEnabledFilter(final String filterName) {
        // all session have same filters
        for (final Shard shard : shards) {
            if (shard.getSession() != null) {
                final Filter filter = shard.getSession().getEnabledFilter(filterName);
                if (filter != null) {
                    return filter;
                }
            }
        }
        // TODO(maxr) what do we return here?
        return null;
    }

    @Override
    public void disableFilter(final String filterName) {
        setOpenSessionEvent(new DisableFilterOpenSessionEvent(filterName));
    }

    @Override
    public SessionStatistics getStatistics() {
        return new ShardedSessionStatistics(this);
    }

    @Override
    public boolean isReadOnly(Object entityOrProxy) {
        for (final Shard shard : shards) {
            if (shard.getSession() != null && shard.getSession().contains(entityOrProxy)) {
                return shard.getSession().isReadOnly(entityOrProxy);
            }
        }

        return false;
    }

    @Override
    public void setReadOnly(final Object entity, final boolean readOnly) {
        setOpenSessionEvent(new SetReadOnlyOpenSessionEvent(entity, readOnly));
    }

    @Override
    public void doWork(final Work work) throws HibernateException {
        for (final Shard s : getShards()) {
            final Session session = s.getSession();
            if (session != null) {
                session.doWork(work);
            }
        }
    }

    @Override
    public Connection disconnect() throws HibernateException {
        for (final Shard s : getShards()) {
            final Session session = s.getSession();
            if (session != null) {
                session.disconnect();
            }
        }

        // we do not allow application-supplied connections, so we can always return null
        return null;
    }

    /**
     * @deprecated
     */
    @Override
    @Deprecated
    public void reconnect() throws HibernateException {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported.  This is a technical decision.
     */
    @Override
    public void reconnect(final Connection connection) throws HibernateException {
        throw new UnsupportedOperationException("Cannot reconnect a sharded session");
    }

    @Override
    public boolean isFetchProfileEnabled(final String name) throws UnknownProfileException {
        for (final Shard shard : shards) {
            if (shard.getSession() != null && shard.getSession().isFetchProfileEnabled(name)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void enableFetchProfile(final String name) throws UnknownProfileException {
        setOpenSessionEvent(new EnableFetchProfileOpenSessionEvent(name));
    }

    @Override
    public void disableFetchProfile(String name) throws UnknownProfileException {
        setOpenSessionEvent(new DisableFetchProfileOpenSessionEvent(name));
    }

    @Override
    public TypeHelper getTypeHelper() {
        // all shards must have the same type helper
        Session someSession = getSomeSession();
        if (someSession == null) {
            someSession = shards.get(0).establishSession();
        }
        return someSession.getTypeHelper();
    }

    @Override
    public LobHelper getLobHelper() {
        // all shards must have the same type helper
        Session someSession = getSomeSession();
        if (someSession == null) {
            someSession = shards.get(0).establishSession();
        }
        return someSession.getLobHelper();
    }

    /**
     * All methods below fulfill the org.hibernate.classic.Session interface.
     * These methods are all deprecated, and since we don't really have any
     * legacy Hibernate code at Google so we're simply not going to support them.
     */

    /**
     * @deprecated
     */
    @Override
    @Deprecated
    public Object saveOrUpdateCopy(final Object object) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated
     */
    @Override
    @Deprecated
    public Object saveOrUpdateCopy(final Object object, final Serializable id) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated
     */
    @Override
    @Deprecated
    public Object saveOrUpdateCopy(final String entityName, final Object object) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated
     */
    @Override
    @Deprecated
    public Object saveOrUpdateCopy(final String entityName, final Object object, final Serializable id)
            throws HibernateException {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated
     */
    @Override
    @Deprecated
    public List find(final String query) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated
     */
    @Override
    @Deprecated
    public List find(final String query, final Object value, final Type type) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated
     */
    @Override
    @Deprecated
    public List find(final String query, final Object[] values, final Type[] types) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated
     */
    @Override
    @Deprecated
    public Iterator iterate(final String query) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated
     */
    @Override
    @Deprecated
    public Iterator iterate(final String query, final Object value, final Type type) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated
     */
    @Override
    @Deprecated
    public Iterator iterate(final String query, final Object[] values, final Type[] types) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated
     */
    @Override
    @Deprecated
    public Collection filter(final Object collection, final String filter) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated
     */
    @Override
    @Deprecated
    public Collection filter(final Object collection, final String filter, final Object value, final Type type)
            throws HibernateException {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated
     */
    @Override
    @Deprecated
    public Collection filter(final Object collection, final String filter, final Object[] values, final Type[] types)
            throws HibernateException {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated
     */
    @Override
    @Deprecated
    public int delete(final String query) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated
     */
    @Override
    @Deprecated
    public int delete(final String query, final Object value, final Type type) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated
     */
    @Override
    @Deprecated
    public int delete(final String query, final Object[] values, final Type[] types) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated
     */
    @Override
    @Deprecated
    public Query createSQLQuery(final String sql, final String returnAlias, final Class returnClass) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated
     */
    @Override
    @Deprecated
    public Query createSQLQuery(final String sql, final String[] returnAliases, final Class[] returnClasses) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated
     */
    @Override
    @Deprecated
    public void save(final Object object, final Serializable id) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated
     */
    @Override
    @Deprecated
    public void save(final String entityName, final Object object, final Serializable id) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated
     */
    @Override
    @Deprecated
    public void update(final Object object, final Serializable id) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated
     */
    @Override
    @Deprecated
    public void update(final String entityName, final Object object, final Serializable id) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    void errorIfClosed() {
        if (closed) {
            throw new SessionException("Session is closed!");
        }
    }

    private static final SaveOrUpdateOperation SAVE_OR_UPDATE_SIMPLE = new SaveOrUpdateOperation() {

        @Override
        public void saveOrUpdate(final Shard shard, final Object object) {
            shard.establishSession().saveOrUpdate(object);
        }

        @Override
        public void merge(final Shard shard, final Object object) {
            shard.establishSession().merge(object);
        }
    };

    private Shard getShardForObject(final Object obj, final List<Shard> shardsToConsider) {
        // TODO(maxr) optimize this by keeping an identity map of objects to shardId
        for (final Shard shard : shardsToConsider) {
            if (shard.getSession() != null && shard.getSession().contains(obj)) {
                return shard;
            }
        }
        return null;
    }

    private Session getSessionForObject(final Object obj, final List<Shard> shardsToConsider) {
        final Shard shard = getShardForObject(obj, shardsToConsider);
        if (shard == null) {
            return null;
        }
        return shard.getSession();
    }

    @Override
    public Session getSessionForObject(final Object obj) {
        return getSessionForObject(obj, shards);
    }

    public ShardId getShardIdForObject(final Object obj, final List<Shard> shardsToConsider) {
        // TODO(maxr)
        // Also, wouldn't it be faster to first see if there's just a single shard
        // id mapped to the shard?
        final Shard shard = getShardForObject(obj, shardsToConsider);
        if (shard == null) {
            return null;
        } else if (shard.getShardIds().size() == 1) {
            return shard.getShardIds().iterator().next();
        } else {
            String className;
            if (obj instanceof HibernateProxy) {
                className = ((HibernateProxy) obj).getHibernateLazyInitializer().getPersistentClass().getName();
            } else {
                className = obj.getClass().getName();
            }

            final IdentifierGenerator idGenerator = shard.getSessionFactoryImplementor().getIdentifierGenerator(className);
            if (idGenerator instanceof ShardEncodingIdentifierGenerator) {
                return ((ShardEncodingIdentifierGenerator) idGenerator).extractShardId(getIdentifier(obj));
            } else {
                // HSHARDS-64
                final ShardResolutionStrategyData srsd = new ShardResolutionStrategyDataImpl(obj.getClass(), getIdentifier(obj));
                final List<ShardId> shardIds = selectShardIdsFromShardResolutionStrategyData(srsd);
                if (shardIds != null && shardIds.size() == 1) {
                    return shardIds.get(0);
                }
                // HSHARDS-64

                // ========================
                // TODO(tomislav): also use shard resolution strategy if it returns only 1 shard; throw this error in config instead of here
                throw new HibernateException("Can not use virtual sharding with non-shard resolving id gen");
            }
        }
    }

    @Override
    public ShardId getShardIdForObject(final Object obj) {
        return getShardIdForObject(obj, shards);
    }

    private Shard getShardForCollection(final Object coll, final List<Shard> shardsToConsider) {
        for (final Shard shard : shardsToConsider) {
            if (shard.getSession() != null) {
                final SessionImplementor si = ((SessionImplementor) shard.getSession());
                if (si.getPersistenceContext().getCollectionEntryOrNull(coll) != null) {
                    return shard;
                }
            }
        }
        return null;
    }

    @Override
    public void lockShard() {
        lockedShard = true;
    }

    public boolean getCheckAllAssociatedObjectsForDifferentShards() {
        return checkAllAssociatedObjectsForDifferentShards;
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            if (!closed) {
                log.warn("ShardedSessionImpl is being garbage collected but it was never properly closed.");
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

    public static ShardId getCurrentSubgraphShardId() {
        return currentSubgraphShardId.get();
    }

    public static void setCurrentSubgraphShardId(final ShardId shardId) {
        currentSubgraphShardId.set(shardId);
    }

    private void setOpenSessionEvent(final OpenSessionEvent sessionEvent) {
        for (final Shard shard : shards) {
            if (shard.getSession() != null) {
                sessionEvent.onOpenSession(shard.getSession());
            } else {
                shard.addOpenSessionEvent(sessionEvent);
            }
        }
    }

    /**
     * Helper method we can use when we need to find the Shard with which a
     * specified object is associated and invoke the method on that Shard.
     * If the object isn't associated with a Session we just invoke it on a
     * random Session with the expectation that this will cause an error.
     */
    <T> T invokeOnShardWithObject(final ShardOperation<T> so, final Object object) throws HibernateException {
        final ShardId shardId = getShardIdForObject(object);
        Shard shardToUse;
        if (shardId == null) {
            // just ask this question of a random shard so we get the proper error
            shardToUse = shards.get(0);
        } else {
            shardToUse = shardIdsToShards.get(shardId);
        }
        return so.execute(shardToUse);
    }

    List<ShardId> selectShardIdsFromShardResolutionStrategyData(final ShardResolutionStrategyData srsd) {
        final IdentifierGenerator idGenerator = shardedSessionFactory.getIdentifierGenerator(srsd.getEntityName());
        if ((idGenerator instanceof ShardEncodingIdentifierGenerator) && (srsd.getId() != null)) {
            return Collections.singletonList(((ShardEncodingIdentifierGenerator) idGenerator).extractShardId(srsd.getId()));
        }
        return shardStrategy.getShardResolutionStrategy().selectShardIdsFromShardResolutionStrategyData(srsd);
    }

    private static class ShardedLockRequest implements LockRequest {

        private final LockOptions lockOptions;
        private final ShardedSessionImpl session;

        public ShardedLockRequest(final ShardedSessionImpl session, final LockOptions lockOptions) {
            this.session = session;
            this.lockOptions = lockOptions;
        }

        @Override
        public LockMode getLockMode() {
            return lockOptions.getLockMode();
        }

        @Override
        public boolean getScope() {
            return lockOptions.getScope();
        }

        @Override
        public int getTimeOut() {
            return lockOptions.getTimeOut();
        }

        @Override
        public LockRequest setLockMode(final LockMode lockMode) {
            lockOptions.setLockMode(lockMode);
            return this;
        }

        @Override
        public LockRequest setScope(final boolean scope) {
            lockOptions.setScope(scope);
            return this;
        }

        @Override
        public LockRequest setTimeOut(final int timeout) {
            lockOptions.setTimeOut(timeout);
            return this;
        }

        @Override
        public void lock(final Object object) {
            final ShardOperation<Void> op = new ShardOperation<Void>() {

                @Override
                public Void execute(Shard s) {
                    s.establishSession().buildLockRequest(lockOptions).lock(object);
                    return null;
                }

                @Override
                public String getOperationName() {
                    return "lock(Object object, LockMode lockMode)";
                }
            };

            session.invokeOnShardWithObject(op, object);
        }

        @Override
        public void lock(final String entityName, final Object object) {

            final ShardOperation<Void> op = new ShardOperation<Void>() {

                @Override
                public Void execute(final Shard s) {
                    s.establishSession().buildLockRequest(lockOptions).lock(entityName, object);
                    return null;
                }

                @Override
                public String getOperationName() {
                    return "lock(String entityName, Object object, LockMode lockMode)";
                }
            };

            session.invokeOnShardWithObject(op, object);
        }
    }
}
