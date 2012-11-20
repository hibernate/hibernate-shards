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

import org.hibernate.Cache;
import org.hibernate.ConnectionReleaseMode;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.MappingException;
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
import org.hibernate.stat.Statistics;
import org.hibernate.stat.StatisticsImplementor;
import org.hibernate.type.Type;
import org.hibernate.type.TypeResolver;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.transaction.TransactionManager;
import java.io.Serializable;
import java.sql.Connection;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author maxr@google.com (Max Ross)
 */
public class SessionFactoryDefaultMock implements SessionFactoryImplementor {

    @Override
    public Session openSession(Connection connection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Session openSession(Interceptor interceptor) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Session openSession(Connection connection, Interceptor interceptor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Session openSession() throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Session getCurrentSession() throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ClassMetadata getClassMetadata(final Class persistentClass) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ClassMetadata getClassMetadata(String entityName) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public CollectionMetadata getCollectionMetadata(final String roleName) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, ClassMetadata> getAllClassMetadata() throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map getAllCollectionMetadata() throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Statistics getStatistics() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isClosed() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Cache getCache() {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void evict(final Class persistentClass) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void evict(final Class persistentClass, final Serializable id) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void evictEntity(final String entityName) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void evictEntity(final String entityName, final Serializable id) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void evictCollection(final String roleName) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void evictCollection(final String roleName, final Serializable id) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void evictQueries() throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void evictQueries(final String cacheRegion) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public StatelessSession openStatelessSession() {
        throw new UnsupportedOperationException();
    }

    @Override
    public StatelessSession openStatelessSession(final Connection connection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set getDefinedFilterNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public FilterDefinition getFilterDefinition(final String filterName) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsFetchProfileDefinition(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TypeHelper getTypeHelper() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Reference getReference() throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override
    public TypeResolver getTypeResolver() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Properties getProperties() {
        throw new UnsupportedOperationException();
    }

    @Override
    public EntityPersister getEntityPersister(final String entityName) throws MappingException {
        throw new UnsupportedOperationException();
    }

    @Override
    public CollectionPersister getCollectionPersister(final String role) throws MappingException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Dialect getDialect() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Interceptor getInterceptor() {
        throw new UnsupportedOperationException();
    }

    @Override
    public QueryPlanCache getQueryPlanCache() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Type[] getReturnTypes(final String queryString) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getReturnAliases(final String queryString) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ConnectionProvider getConnectionProvider() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getImplementors(final String className) throws MappingException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getImportedClassName(final String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TransactionManager getTransactionManager() {
        throw new UnsupportedOperationException();
    }

    @Override
    public QueryCache getQueryCache() {
        throw new UnsupportedOperationException();
    }

    @Override
    public QueryCache getQueryCache(final String regionName) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public UpdateTimestampsCache getUpdateTimestampsCache() {
        throw new UnsupportedOperationException();
    }

    @Override
    public StatisticsImplementor getStatisticsImplementor() {
        throw new UnsupportedOperationException();
    }

    @Override
    public NamedQueryDefinition getNamedQuery(final String queryName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NamedSQLQueryDefinition getNamedSQLQuery(final String queryName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResultSetMappingDefinition getResultSetMapping(final String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IdentifierGenerator getIdentifierGenerator(final String rootEntityName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Region getSecondLevelCacheRegion(final String regionName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map getAllSecondLevelCacheRegions() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SQLExceptionConverter getSQLExceptionConverter() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Settings getSettings() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Session openTemporarySession() throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Session openSession(final Connection connection,
                               final boolean flushBeforeCompletionEnabled,
                               final boolean autoCloseSessionEnabled,
                               final ConnectionReleaseMode connectionReleaseMode) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> getCollectionRolesByEntityParticipant(final String entityName) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Type getIdentifierType(final String className) throws MappingException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getIdentifierPropertyName(final String className) throws MappingException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Type getReferencedPropertyType(final String className, final String propertyName) throws MappingException {
        throw new UnsupportedOperationException();
    }

    @Override
    public EntityNotFoundDelegate getEntityNotFoundDelegate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SQLFunctionRegistry getSqlFunctionRegistry() {
        throw new UnsupportedOperationException();
    }

    @Override
    public FetchProfile getFetchProfile(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SessionFactoryObserver getFactoryObserver() {
        throw new UnsupportedOperationException();
    }
}
