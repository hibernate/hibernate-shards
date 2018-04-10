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

package org.hibernate.shards.defaultmock;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Selection;
import javax.persistence.metamodel.Metamodel;

import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.Filter;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.IdentifierLoadAccess;
import org.hibernate.Interceptor;
import org.hibernate.LobHelper;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.MultiIdentifierLoadAccess;
import org.hibernate.NaturalIdLoadAccess;
import org.hibernate.ReplicationMode;
import org.hibernate.ScrollMode;
import org.hibernate.SessionEventListener;
import org.hibernate.SharedSessionBuilder;
import org.hibernate.SimpleNaturalIdLoadAccess;
import org.hibernate.Transaction;
import org.hibernate.TypeHelper;
import org.hibernate.UnknownProfileException;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.jdbc.LobCreator;
import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
import org.hibernate.engine.spi.ActionQueue;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.ExceptionConverter;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SessionEventListenerManager;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.jdbc.ReturningWork;
import org.hibernate.jdbc.Work;
import org.hibernate.loader.custom.CustomQuery;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.procedure.ProcedureCall;
import org.hibernate.query.spi.NativeQueryImplementor;
import org.hibernate.query.spi.QueryImplementor;
import org.hibernate.query.spi.ScrollableResultsImplementor;
import org.hibernate.resource.jdbc.spi.JdbcSessionContext;
import org.hibernate.resource.transaction.spi.TransactionCoordinator;
import org.hibernate.stat.SessionStatistics;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

/**
 * @author maxr@google.com (Max Ross)
 */
public class SessionImplementorDefaultMock implements SessionImplementor {

	@Override
	public void setAutoClear(boolean enabled) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Interceptor getInterceptor() {
		throw new UnsupportedOperationException();
	}

	@Override
	public SessionImplementor getSession() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isTransactionInProgress() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Transaction accessTransaction() {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public LockOptions getLockRequest(LockModeType lockModeType, Map<String, Object> properties) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void initializeCollection(PersistentCollection collection, boolean writing) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object internalLoad(
			String entityName, Serializable id, boolean eager,
			boolean nullable) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object immediateLoad(String entityName, Serializable id) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getTimestamp() {
		throw new UnsupportedOperationException();
	}

	@Override
	public SessionFactoryImplementor getFactory() {
		throw new UnsupportedOperationException();
	}

	@Override
	public EntityPersister getEntityPersister(String entityName, Object object) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getEntityUsingInterceptor(EntityKey key) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Serializable getContextEntityIdentifier(Object object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String bestGuessEntityName(Object object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String guessEntityName(Object entity) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object instantiate(String entityName, Serializable id) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getDontFlushFromFind() {
		throw new UnsupportedOperationException();
	}

	@Override
	public PersistenceContext getPersistenceContext() {
		throw new UnsupportedOperationException();
	}

	@Override
	public JdbcCoordinator getJdbcCoordinator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public JdbcServices getJdbcServices() {
		throw new UnsupportedOperationException();
	}

	@Override
	public CacheMode getCacheMode() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setCacheMode(CacheMode cm) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isOpen() {
		throw new UnsupportedOperationException();
	}

	@Override
	public EntityManagerFactory getEntityManagerFactory() {
		throw new UnsupportedOperationException();
	}

	@Override
	public CriteriaBuilder getCriteriaBuilder() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Metamodel getMetamodel() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> EntityGraph<T> createEntityGraph(Class<T> rootType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public EntityGraph<?> createEntityGraph(String graphName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public EntityGraph<?> getEntityGraph(String graphName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> List<EntityGraph<? super T>> getEntityGraphs(Class<T> entityClass) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isConnected() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Transaction beginTransaction() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Transaction getTransaction() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ProcedureCall getNamedProcedureCall(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ProcedureCall createStoredProcedureCall(String procedureName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ProcedureCall createStoredProcedureCall(String procedureName, Class... resultClasses) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ProcedureCall createStoredProcedureCall(String procedureName, String... resultSetMappings) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Criteria createCriteria(Class persistentClass) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Criteria createCriteria(Class persistentClass, String alias) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Criteria createCriteria(String entityName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Criteria createCriteria(String entityName, String alias) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Integer getJdbcBatchSize() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setJdbcBatchSize(Integer jdbcBatchSize) {
		throw new UnsupportedOperationException();
	}

	@Override
	public FlushModeType getFlushMode() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void lock(Object entity, LockModeType lockMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void lock(Object entity, LockModeType lockMode, Map<String, Object> properties) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setHibernateFlushMode(FlushMode flushMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public FlushMode getHibernateFlushMode() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setFlushMode(FlushMode fm) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Connection connection() {
		throw new UnsupportedOperationException();
	}

	@Override
	public SharedSessionBuilder sessionWithOptions() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void flush() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setFlushMode(FlushModeType flushMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SessionFactoryImplementor getSessionFactory() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void cancelQuery() throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isDirty() throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isDefaultReadOnly() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setDefaultReadOnly(boolean readOnly) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Serializable getIdentifier(Object object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean contains(String entityName, Object object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void evict(Object object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T load(Class<T> theClass, Serializable id, LockMode lockMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T load(Class<T> theClass, Serializable id, LockOptions lockOptions) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object load(String entityName, Serializable id, LockMode lockMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object load(String entityName, Serializable id, LockOptions lockOptions) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T load(Class<T> theClass, Serializable id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object load(String entityName, Serializable id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void load(Object object, Serializable id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void replicate(Object object, ReplicationMode replicationMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void replicate(String entityName, Object object, ReplicationMode replicationMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Serializable save(Object object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Serializable save(String entityName, Object object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void saveOrUpdate(Object object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void saveOrUpdate(String entityName, Object object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void update(Object object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void update(String entityName, Object object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object merge(Object object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object merge(String entityName, Object object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void persist(Object object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void remove(Object entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T find(Class<T> entityClass, Object primaryKey) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode, Map<String, Object> properties) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T getReference(Class<T> entityClass, Object primaryKey) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void persist(String entityName, Object object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(Object object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(String entityName, Object object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void lock(Object object, LockMode lockMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void lock(String entityName, Object object, LockMode lockMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public LockRequest buildLockRequest(LockOptions lockOptions) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void refresh(Object object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void refresh(Object entity, Map<String, Object> properties) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void refresh(Object entity, LockModeType lockMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void refresh(Object entity, LockModeType lockMode, Map<String, Object> properties) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void refresh(String entityName, Object object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void refresh(Object object, LockMode lockMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void refresh(Object object, LockOptions lockOptions) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void refresh(String entityName, Object object, LockOptions lockOptions) {
		throw new UnsupportedOperationException();
	}

	@Override
	public LockMode getCurrentLockMode(Object object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public org.hibernate.query.Query createFilter(Object collection, String queryString) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void detach(Object entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean contains(Object entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public LockModeType getLockMode(Object entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setProperty(String propertyName, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, Object> getProperties() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T get(Class<T> entityType, Serializable id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T get(Class<T> entityType, Serializable id, LockMode lockMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T get(Class<T> entityType, Serializable id, LockOptions lockOptions) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object get(String entityName, Serializable id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object get(String entityName, Serializable id, LockMode lockMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object get(String entityName, Serializable id, LockOptions lockOptions) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getEntityName(Object object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IdentifierLoadAccess byId(String entityName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> MultiIdentifierLoadAccess<T> byMultipleIds(Class<T> entityClass) {
		throw new UnsupportedOperationException();
	}

	@Override
	public MultiIdentifierLoadAccess byMultipleIds(String entityName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> IdentifierLoadAccess<T> byId(Class<T> entityClass) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NaturalIdLoadAccess byNaturalId(String entityName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> NaturalIdLoadAccess<T> byNaturalId(Class<T> entityClass) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SimpleNaturalIdLoadAccess bySimpleNaturalId(String entityName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> SimpleNaturalIdLoadAccess<T> bySimpleNaturalId(Class<T> entityClass) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Filter enableFilter(String filterName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Filter getEnabledFilter(String filterName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void disableFilter(String filterName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SessionStatistics getStatistics() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isReadOnly(Object entityOrProxy) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setReadOnly(Object entityOrProxy, boolean readOnly) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void doWork(Work work) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T doReturningWork(ReturningWork<T> work) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Connection disconnect() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void reconnect(Connection connection) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isFetchProfileEnabled(String name) throws UnknownProfileException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void enableFetchProfile(String name) throws UnknownProfileException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void disableFetchProfile(String name) throws UnknownProfileException {
		throw new UnsupportedOperationException();
	}

	@Override
	public TypeHelper getTypeHelper() {
		throw new UnsupportedOperationException();
	}

	@Override
	public LobHelper getLobHelper() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addEventListeners(SessionEventListener... listeners) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isFlushBeforeCompletionEnabled() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ActionQueue getActionQueue() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object instantiate(EntityPersister persister, Serializable id) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void forceFlush(EntityEntry e) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public QueryImplementor createQuery(String queryString) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> QueryImplementor<T> createQuery(String queryString, Class<T> resultType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> QueryImplementor<T> createNamedQuery(String name, Class<T> resultType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public QueryImplementor createNamedQuery(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQueryImplementor createNativeQuery(String sqlString) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQueryImplementor createNativeQuery(String sqlString, Class resultClass) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQueryImplementor createNativeQuery(String sqlString, String resultSetMapping) {
		throw new UnsupportedOperationException();
	}

	@Override
	public StoredProcedureQuery createNamedStoredProcedureQuery(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public StoredProcedureQuery createStoredProcedureQuery(String procedureName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public StoredProcedureQuery createStoredProcedureQuery(String procedureName, Class... resultClasses) {
		throw new UnsupportedOperationException();
	}

	@Override
	public StoredProcedureQuery createStoredProcedureQuery(String procedureName, String... resultSetMappings) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void joinTransaction() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isJoinedToTransaction() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T unwrap(Class<T> cls) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getDelegate() {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQueryImplementor getNamedNativeQuery(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public QueryImplementor getNamedQuery(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQueryImplementor getNamedSQLQuery(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQueryImplementor createSQLQuery(String queryString) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> QueryImplementor<T> createQuery(CriteriaQuery<T> criteriaQuery) {
		throw new UnsupportedOperationException();
	}

	@Override
	public QueryImplementor createQuery(CriteriaUpdate updateQuery) {
		throw new UnsupportedOperationException();
	}

	@Override
	public QueryImplementor createQuery(CriteriaDelete deleteQuery) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> QueryImplementor<T> createQuery(String jpaqlString, Class<T> resultClass, Selection selection, QueryOptions queryOptions) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void merge(String entityName, Object object, Map copiedAlready) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void persist(String entityName, Object object, Map createdAlready) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void persistOnFlush(String entityName, Object object, Map copiedAlready) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void refresh(String entityName, Object object, Map refreshedAlready) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(String entityName, Object child, boolean isCascadeDeleteEnabled, Set transientEntities) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeOrphanBeforeUpdates(String entityName, Object child) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isEventSource() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void afterScrollOperation() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean shouldAutoClose() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isAutoCloseSessionEnabled() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isClosed() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void checkOpen(boolean markForRollbackIfClosed) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void markForRollbackOnly() {
		throw new UnsupportedOperationException();
	}

	@Override
	public LoadQueryInfluencers getLoadQueryInfluencers() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ExceptionConverter getExceptionConverter() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getTenantIdentifier() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() {
		throw new UnsupportedOperationException();
	}

	@Override
	public UUID getSessionIdentifier() {
		throw new UnsupportedOperationException();
	}

	@Override
	public JdbcSessionContext getJdbcSessionContext() {
		throw new UnsupportedOperationException();
	}

	@Override
	public JdbcConnectionAccess getJdbcConnectionAccess() {
		throw new UnsupportedOperationException();
	}

	@Override
	public EntityKey generateEntityKey(Serializable id, EntityPersister persister) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List list(String query, QueryParameters queryParameters) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator iterate(String query, QueryParameters queryParameters) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public ScrollableResultsImplementor scroll(String query, QueryParameters queryParameters) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public ScrollableResultsImplementor scroll(Criteria criteria, ScrollMode scrollMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List listFilter(Object collection, String filter, QueryParameters queryParameters)
			throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator iterateFilter(Object collection, String filter, QueryParameters queryParameters)
			throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public List listCustomQuery(CustomQuery customQuery, QueryParameters queryParameters) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public ScrollableResultsImplementor scrollCustomQuery(CustomQuery customQuery, QueryParameters queryParameters) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public List list(NativeSQLQuerySpecification spec, QueryParameters queryParameters) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public ScrollableResultsImplementor scroll(NativeSQLQuerySpecification spec, QueryParameters queryParameters) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int executeUpdate(String query, QueryParameters queryParameters) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int executeNativeUpdate(NativeSQLQuerySpecification specification, QueryParameters queryParameters)
			throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public TransactionCoordinator getTransactionCoordinator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void afterTransactionBegin() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void beforeTransactionCompletion() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void afterTransactionCompletion(boolean successful, boolean delayed) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void flushBeforeTransactionCompletion() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T execute(Callback<T> callback) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List list(Criteria criteria) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SessionEventListenerManager getEventListenerManager() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean shouldAutoJoinTransaction() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean useStreamForLobBinding() {
		throw new UnsupportedOperationException();
	}

	@Override
	public LobCreator getLobCreator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public SqlTypeDescriptor remapSqlTypeDescriptor(SqlTypeDescriptor sqlTypeDescriptor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public TimeZone getJdbcTimeZone() {
		throw new UnsupportedOperationException();
	}
}
