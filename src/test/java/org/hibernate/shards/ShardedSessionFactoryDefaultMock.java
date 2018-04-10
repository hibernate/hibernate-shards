/**
 * Copyright (C) 2007 Google Inc.
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 */

package org.hibernate.shards;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.Query;
import javax.persistence.SynchronizationType;
import javax.persistence.criteria.CriteriaBuilder;

import org.hibernate.CustomEntityDirtinessStrategy;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.MappingException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.SessionFactoryObserver;
import org.hibernate.StatelessSession;
import org.hibernate.StatelessSessionBuilder;
import org.hibernate.TypeHelper;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cfg.Settings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.dialect.function.SQLFunctionRegistry;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.profile.FetchProfile;
import org.hibernate.engine.query.spi.QueryPlanCache;
import org.hibernate.engine.spi.CacheImplementor;
import org.hibernate.engine.spi.FilterDefinition;
import org.hibernate.engine.spi.SessionBuilderImplementor;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.factory.IdentifierGeneratorFactory;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.proxy.EntityNotFoundDelegate;
import org.hibernate.query.spi.NamedQueryRepository;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.shards.engine.ShardedSessionFactoryImplementor;
import org.hibernate.shards.session.ShardedSession;
import org.hibernate.shards.session.ShardedSessionFactory;
import org.hibernate.shards.strategy.ShardStrategyFactory;
import org.hibernate.stat.spi.StatisticsImplementor;
import org.hibernate.type.Type;
import org.hibernate.type.TypeResolver;

/**
 * @author maxr@google.com (Max Ross)
 */
public class ShardedSessionFactoryDefaultMock implements ShardedSessionFactoryImplementor {

	@Override
	public Map<SessionFactoryImplementor, Set<ShardId>> getSessionFactoryShardIdMap() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ShardedSession openSession(final Interceptor interceptor) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public ShardedSession openSession() throws HibernateException {
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
	public ClassMetadata getClassMetadata(final String entityName) throws HibernateException {
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
	public StatisticsImplementor getStatistics() {
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
	public CacheImplementor getCache() {
		throw new UnsupportedOperationException();
	}

	@Override
	public PersistenceUnitUtil getPersistenceUnitUtil() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addNamedQuery(String name, Query query) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T unwrap(Class<T> cls) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> void addNamedEntityGraph(String graphName, EntityGraph<T> entityGraph) {
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
	public IdentifierGenerator getIdentifierGenerator(final String rootEntityName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsFactory(SessionFactoryImplementor factory) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<SessionFactory> getSessionFactories() {
		throw new UnsupportedOperationException();
	}

	@Override
	public TypeResolver getTypeResolver() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, Object> getProperties() {
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
	public Map getAllSecondLevelCacheRegions() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Settings getSettings() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> List<EntityGraph<? super T>> findEntityGraphsByType(Class<T> entityClass) {
		throw new UnsupportedOperationException();
	}

	@Override
	public EntityManager createEntityManager() {
		throw new UnsupportedOperationException();
	}

	@Override
	public EntityManager createEntityManager(Map map) {
		throw new UnsupportedOperationException();
	}

	@Override
	public EntityManager createEntityManager(SynchronizationType synchronizationType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public EntityManager createEntityManager(SynchronizationType synchronizationType, Map map) {
		throw new UnsupportedOperationException();
	}

	@Override
	public CriteriaBuilder getCriteriaBuilder() {
		throw new UnsupportedOperationException();
	}

	@Override
	public MetamodelImplementor getMetamodel() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isOpen() {
		throw new UnsupportedOperationException();
	}

	@Override
	public EntityGraph findEntityGraphByName(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Session openTemporarySession() throws HibernateException {
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
	public ShardedSessionFactory getSessionFactory(
			final List<ShardId> shardIds,
			final ShardStrategyFactory shardStrategyFactory) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addObserver(SessionFactoryObserver observer) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getUuid() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public SessionBuilderImplementor withOptions() {
		throw new UnsupportedOperationException();
	}

	@Override
	public JdbcServices getJdbcServices() {
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
	public DeserializationResolver getDeserializationResolver() {
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
	public NamedQueryRepository getNamedQueryRepository() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Type resolveParameterBindType(Object bindValue) {
		return null;
	}

	@Override
	public Type resolveParameterBindType(Class clazz) {
		throw new UnsupportedOperationException();
	}
}
