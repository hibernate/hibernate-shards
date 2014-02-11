package org.hibernate.shards.query;

import java.util.Collection;
import java.util.List;

import org.hibernate.LockMode;
import org.hibernate.MappingException;
import org.hibernate.SQLQuery;
import org.hibernate.cfg.NotYetImplementedException;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
import org.hibernate.shards.Shard;
import org.hibernate.shards.strategy.access.ShardAccessStrategy;
import org.hibernate.type.Type;

/**
 * @author aviadl@sentrigo.com (Aviad Lichtenstadt)
 */
public class ShardedSQLQueryImpl extends ShardedQueryImpl implements ShardedSQLQuery {

	public ShardedSQLQueryImpl(
			final QueryId queryId,
			final List<Shard> shards,
			final QueryFactory queryFactory,
			final ShardAccessStrategy shardAccessStrategy) {
		super( queryId, shards, queryFactory, shardAccessStrategy );
	}

	@Override
	public SQLQuery addEntity(final String entityName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SQLQuery addEntity(final Class entityClass) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SQLQuery addEntity(final String alias, final String entityName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SQLQuery addEntity(final String tableAlias, final String entityName, final LockMode lockMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SQLQuery addEntity(final String alias, final Class entityClass) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SQLQuery addEntity(final String tableAlias, final Class entityName, final LockMode lockMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public FetchReturn addFetch(final String tableAlias, final String ownerTableAlias, final String joinPropertyName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SQLQuery addJoin(final String alias, final String path) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SQLQuery addJoin(final String tableAlias, final String ownerTableAlias, final String joinPropertyName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SQLQuery addJoin(final String tableAlias, final String path, final LockMode lockMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SQLQuery addScalar(final String columnAlias) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SQLQuery addScalar(final String columnAlias, final Type type) {
		throw new UnsupportedOperationException();
	}

	@Override
	public RootReturn addRoot(final String tableAlias, final String entityName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public RootReturn addRoot(final String tableAlias, final Class entityType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SQLQuery addSynchronizedEntityClass(final Class entityClass) throws MappingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public SQLQuery addSynchronizedEntityName(final String entityName) throws MappingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public SQLQuery addSynchronizedQuerySpace(final String querySpace) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SQLQuery setResultSetMapping(final String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isCallable() {
		//todo may be UnsupportedOperationException
		throw new NotYetImplementedException();
	}

	@Override
	public List<NativeSQLQueryReturn> getQueryReturns() {
		//todo may be UnsupportedOperationException
		throw new NotYetImplementedException();
	}

	@Override
	public Collection<String> getSynchronizedQuerySpaces() {
		//todo may be UnsupportedOperationException
		throw new NotYetImplementedException();
	}
}
