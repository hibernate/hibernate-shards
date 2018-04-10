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
import java.util.List;
import java.util.Map;

import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.Filter;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.IdentifierLoadAccess;
import org.hibernate.LobHelper;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.MultiIdentifierLoadAccess;
import org.hibernate.NaturalIdLoadAccess;
import org.hibernate.Query;
import org.hibernate.ReplicationMode;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionEventListener;
import org.hibernate.SessionFactory;
import org.hibernate.SharedSessionBuilder;
import org.hibernate.SimpleNaturalIdLoadAccess;
import org.hibernate.Transaction;
import org.hibernate.TypeHelper;
import org.hibernate.UnknownProfileException;
import org.hibernate.jdbc.ReturningWork;
import org.hibernate.jdbc.Work;
import org.hibernate.procedure.ProcedureCall;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.query.internal.QueryImpl;
import org.hibernate.query.spi.NativeQueryImplementor;
import org.hibernate.query.spi.QueryImplementor;
import org.hibernate.stat.SessionStatistics;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.metamodel.Metamodel;

/**
 * @author maxr@google.com (Max Ross)
 */
public class SessionDefaultMock implements Session {

	@Override
	public <T> IdentifierLoadAccess<T> byId(Class<T> entityClass) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SharedSessionBuilder sessionWithOptions() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void refresh(String entityName, Object object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void refresh(String entityName, Object object, LockOptions lockOptions) {
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
	public <T> T doReturningWork(ReturningWork<T> work) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getTenantIdentifier() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void flush() throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setFlushMode(FlushModeType flushMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setFlushMode(FlushMode flushMode) {
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
	public void setCacheMode(CacheMode cacheMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public CacheMode getCacheMode() {
		throw new UnsupportedOperationException();
	}

	@Override
	public SessionFactory getSessionFactory() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void cancelQuery() throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isOpen() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isConnected() {
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
	public Serializable getIdentifier(Object object) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean contains(String entityName, Object object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean contains(Object object) {
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
	public void evict(Object object) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public <T> T load(Class<T> theClass, Serializable id, LockMode lockMode) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T load(Class<T> theClass, Serializable id, LockOptions lockOptions) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public Object load(String entityName, Serializable id, LockMode lockMode) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object load(String entityName, Serializable id, LockOptions lockOptions) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T load(Class<T> theClass, Serializable id) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object load(String entityName, Serializable id) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void load(Object object, Serializable id) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void replicate(Object object, ReplicationMode replicationMode) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void replicate(String entityName, Object object, ReplicationMode replicationMode) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Serializable save(Object object) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Serializable save(String entityName, Object object) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void saveOrUpdate(Object object) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void saveOrUpdate(String entityName, Object object) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void update(Object object) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void update(String entityName, Object object) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object merge(Object object) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object merge(String entityName, Object object) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void persist(Object object) throws HibernateException {
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
	public void persist(String entityName, Object object) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(Object object) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(String entityName, Object object) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void lock(Object object, LockMode lockMode) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void lock(String entityName, Object object, LockMode lockMode) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public LockRequest buildLockRequest(LockOptions lockOptions) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void refresh(Object object) throws HibernateException {
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

	@Deprecated
	@Override
	public void refresh(Object object, LockMode lockMode) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void refresh(Object object, LockOptions lockOptions) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public LockMode getCurrentLockMode(Object object) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Transaction beginTransaction() throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Transaction getTransaction() {
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
	public QueryImplementor createQuery(String queryString) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> org.hibernate.query.Query<T> createQuery(String queryString, Class<T> resultType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public org.hibernate.query.Query createNamedQuery(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> org.hibernate.query.Query<T> createQuery(CriteriaQuery<T> criteriaQuery) {
		throw new UnsupportedOperationException();
	}

	@Override
	public org.hibernate.query.Query createQuery(CriteriaUpdate updateQuery) {
		throw new UnsupportedOperationException();
	}

	@Override
	public org.hibernate.query.Query createQuery(CriteriaDelete deleteQuery) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> org.hibernate.query.Query<T> createNamedQuery(String name, Class<T> resultType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery createNativeQuery(String sqlString, Class resultClass) {
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
	public NativeQueryImplementor createSQLQuery(String queryString) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery createNativeQuery(String sqlString) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery createNativeQuery(String sqlString, String resultSetMapping) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery getNamedSQLQuery(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery getNamedNativeQuery(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public org.hibernate.query.Query createFilter(Object collection, String queryString) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public QueryImplementor getNamedQuery(String queryName) throws HibernateException {
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
	public <T> T get(Class<T> clazz, Serializable id) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public <T> T get(Class<T> clazz, Serializable id, LockMode lockMode) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T get(Class<T> clazz, Serializable id, LockOptions lockOptions) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object get(String entityName, Serializable id) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public Object get(String entityName, Serializable id, LockMode lockMode) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object get(String entityName, Serializable id, LockOptions lockOptions) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getEntityName(Object object) throws HibernateException {
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
	public void setReadOnly(Object entity, boolean readOnly) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void doWork(Work work) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Connection disconnect() throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void reconnect(Connection connection) throws HibernateException {
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
	public Session getSession() {
		throw new UnsupportedOperationException();
	}
}
