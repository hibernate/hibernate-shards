package org.hibernate.shards.defaultmock;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityGraph;
import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.ManagedType;

import org.hibernate.EntityNameResolver;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;

public class MetamodelImplementorDefaultMock implements MetamodelImplementor {

	@Override
	public SessionFactoryImplementor getSessionFactory() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <X> EntityType<X> entity(String entityName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getImportedClassName(String className) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String[] getImplementors(String entityName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<EntityNameResolver> getEntityNameResolvers() {
		throw new UnsupportedOperationException();
	}

	@Override
	public EntityPersister locateEntityPersister(Class byClass) {
		throw new UnsupportedOperationException();
	}

	@Override
	public EntityPersister locateEntityPersister(String byName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public EntityPersister entityPersister(Class entityClass) {
		throw new UnsupportedOperationException();
	}

	@Override
	public EntityPersister entityPersister(String entityName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, EntityPersister> entityPersisters() {
		throw new UnsupportedOperationException();
	}

	@Override
	public CollectionPersister collectionPersister(String role) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, CollectionPersister> collectionPersisters() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> getCollectionRolesByEntityParticipant(String entityName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String[] getAllEntityNames() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String[] getAllCollectionRoles() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> void addNamedEntityGraph(String graphName, EntityGraph<T> entityGraph) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> EntityGraph<T> findEntityGraphByName(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> List<EntityGraph<? super T>> findEntityGraphsByType(Class<T> entityClass) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <X> EntityType<X> entity(Class<X> cls) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <X> ManagedType<X> managedType(Class<X> cls) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <X> EmbeddableType<X> embeddable(Class<X> cls) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<ManagedType<?>> getManagedTypes() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<EntityType<?>> getEntities() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<EmbeddableType<?>> getEmbeddables() {
		throw new UnsupportedOperationException();
	}
}
