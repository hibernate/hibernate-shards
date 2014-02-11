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
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.naming.NamingException;
import javax.naming.Reference;

import org.hibernate.Cache;
import org.hibernate.CustomEntityDirtinessStrategy;
import org.hibernate.EntityNameResolver;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.MappingException;
import org.hibernate.Session;
import org.hibernate.SessionFactoryObserver;
import org.hibernate.StatelessSession;
import org.hibernate.StatelessSessionBuilder;
import org.hibernate.TypeHelper;
import org.hibernate.cache.spi.QueryCache;
import org.hibernate.cache.spi.Region;
import org.hibernate.cache.spi.UpdateTimestampsCache;
import org.hibernate.cfg.Settings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.SQLFunctionRegistry;
import org.hibernate.engine.ResultSetMappingDefinition;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.engine.profile.FetchProfile;
import org.hibernate.engine.query.spi.QueryPlanCache;
import org.hibernate.engine.spi.FilterDefinition;
import org.hibernate.engine.spi.NamedQueryDefinition;
import org.hibernate.engine.spi.NamedSQLQueryDefinition;
import org.hibernate.engine.spi.SessionBuilderImplementor;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.exception.spi.SQLExceptionConverter;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.factory.IdentifierGeneratorFactory;
import org.hibernate.internal.NamedQueryRepository;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.proxy.EntityNotFoundDelegate;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.stat.Statistics;
import org.hibernate.stat.spi.StatisticsImplementor;
import org.hibernate.type.Type;
import org.hibernate.type.TypeResolver;

/**
 * @author maxr@google.com (Max Ross)
 */
public class SessionFactoryDefaultMock implements SessionFactoryImplementor {

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
	public void addObserver(SessionFactoryObserver observer) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SessionBuilderImplementor withOptions() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, EntityPersister> getEntityPersisters() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, CollectionPersister> getCollectionPersisters() {
		throw new UnsupportedOperationException();
	}

	@Override
	public JdbcServices getJdbcServices() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Region getNaturalIdCacheRegion(String regionName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SqlExceptionHelper getSQLExceptionHelper() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ServiceRegistryImplementor getServiceRegistry() {
		throw new UnsupportedOperationException();
	}

	@Override
	public CustomEntityDirtinessStrategy getCustomEntityDirtinessStrategy() {
		throw new UnsupportedOperationException();
	}

	@Override
	public CurrentTenantIdentifierResolver getCurrentTenantIdentifierResolver() {
		throw new UnsupportedOperationException();
	}

	@Override
	public SessionFactoryOptions getSessionFactoryOptions() {
		throw new UnsupportedOperationException();
	}

	@Override
	public StatelessSessionBuilder withStatelessOptions() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void registerNamedQueryDefinition(String name, NamedQueryDefinition definition) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void registerNamedSQLQueryDefinition(String name, NamedSQLQueryDefinition definition) {
		throw new UnsupportedOperationException();

	}

	@Override
	public NamedQueryRepository getNamedQueryRepository() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterable<EntityNameResolver> iterateEntityNameResolvers() {
		throw new UnsupportedOperationException();
	}
}
