package org.hibernate.shards.query;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.MappingException;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
import org.hibernate.engine.spi.RowSelection;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.ParameterMetadata;
import org.hibernate.query.Query;
import org.hibernate.query.QueryParameter;
import org.hibernate.query.QueryProducer;
import org.hibernate.shards.Shard;
import org.hibernate.shards.strategy.access.ShardAccessStrategy;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.type.Type;

import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Parameter;
import javax.persistence.TemporalType;

/**
 * @author aviadl@sentrigo.com (Aviad Lichtenstadt)
 */
public class ShardedSQLQueryImpl<R> extends ShardedQueryImpl<R> implements ShardedSQLQuery<R> {

	public ShardedSQLQueryImpl(
			final QueryId queryId,
			final List<Shard> shards,
			final QueryFactory queryFactory,
			final ShardAccessStrategy shardAccessStrategy) {
		super( queryId, shards, queryFactory, shardAccessStrategy );
	}

	@Override
	public NativeQuery<R> addEntity(final String entityName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> addEntity(final Class entityClass) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> addEntity(final String alias, final String entityName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> addEntity(final String tableAlias, final String entityName, final LockMode lockMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> addEntity(final String alias, final Class entityClass) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> addEntity(final String tableAlias, final Class entityName, final LockMode lockMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public FetchReturn addFetch(final String tableAlias, final String ownerTableAlias, final String joinPropertyName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> addJoin(final String alias, final String path) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> addJoin(final String tableAlias, final String ownerTableAlias, final String joinPropertyName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> addJoin(final String tableAlias, final String path, final LockMode lockMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public FlushMode getHibernateFlushMode() {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setHibernateFlushMode(FlushMode flushMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ParameterMetadata getParameterMetadata() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query<R> setParameterList(int position, Collection values) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query<R> setParameterList(int position, Collection values, Type type) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query<R> setParameterList(int position, Object[] values, Type type) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query<R> setParameterList(int position, Object[] values) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<R> getResultList() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Stream<R> getResultStream() {
		throw new UnsupportedOperationException();
	}

	@Override
	public R getSingleResult() {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setFlushMode(FlushModeType flushMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setHint(String hintName, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setLockMode(LockModeType lockMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T unwrap(Class<T> cls) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setParameter(String name, Calendar value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setParameter(String name, Date value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setParameter(int position, Calendar value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setParameter(int position, Date value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public QueryProducer getProducer() {
		throw new UnsupportedOperationException();
	}

	@Override
	public RowSelection getQueryOptions() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Optional<R> uniqueResultOptional() {
		return Optional.empty();
	}

	@Override
	public Stream<R> stream() {
		return null;
	}

	@Override
	public NativeQuery<R> setParameter(Parameter<Instant> param, Instant value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setParameter(Parameter<LocalDateTime> param, LocalDateTime value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setParameter(Parameter<ZonedDateTime> param, ZonedDateTime value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setParameter(Parameter<OffsetDateTime> param, OffsetDateTime value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setParameter(String name, Instant value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setParameter(String name, LocalDateTime value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setParameter(String name, ZonedDateTime value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setParameter(String name, OffsetDateTime value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setParameter(int position, Instant value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setParameter(int position, LocalDateTime value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setParameter(int position, ZonedDateTime value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setParameter(int position, OffsetDateTime value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<Parameter<?>> getParameters() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Parameter<?> getParameter(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> Parameter<T> getParameter(String name, Class<T> type) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Parameter<?> getParameter(int position) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> Parameter<T> getParameter(int position, Class<T> type) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isBound(Parameter<?> param) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T getParameterValue(Parameter<T> param) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getParameterValue(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getParameterValue(int position) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setParameter(Parameter param, Date value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setParameter(Parameter param, Calendar value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setParameterList(QueryParameter parameter, Collection values) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setParameter(int position, Object val, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setParameter(String name, Object val, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setParameter(QueryParameter parameter, Object val, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setParameter(QueryParameter parameter, Object val, Type type) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setParameter(Parameter param, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setParameter(QueryParameter parameter, Object val) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> addScalar(final String columnAlias) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> addScalar(final String columnAlias, final Type type) {
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
	public NativeQuery<R> addSynchronizedEntityClass(final Class entityClass) throws MappingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isCallable() {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> addSynchronizedEntityName(final String entityName) throws MappingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> addSynchronizedQuerySpace(final String querySpace) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setFlushMode(FlushMode flushMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setResultSetMapping(final String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<NativeSQLQueryReturn> getQueryReturns() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<String> getSynchronizedQuerySpaces() {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> addQueryHint(String hint) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setProperties(Object bean) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setProperties(Map map) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setFirstResult(int firstResult) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setCacheable(boolean cacheable) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setCacheRegion(String cacheRegion) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setFetchSize(int fetchSize) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setComment(String comment) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setCacheMode(CacheMode cacheMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query<R> setEntity(int position, Object val) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query<R> setEntity(String name, Object val) {
		throw new UnsupportedOperationException();
	}

	@Override
	public QueryId getQueryId() {
		throw new UnsupportedOperationException();
	}

	@Override
	public QueryFactory getQueryFactory() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getQueryString() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<R> iterate() throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public ScrollableResults scroll() throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public ScrollableResults scroll(ScrollMode scrollMode) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<R> list() throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public R uniqueResult() throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int executeUpdate() throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setMaxResults(int maxResults) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setReadOnly(boolean readOnly) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setTimeout(int timeout) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setLockOptions(LockOptions lockOptions) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setLockMode(String alias, LockMode lockMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setParameter(int position, Object val, Type type) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setParameter(String name, Object val, Type type) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setParameter(int position, Object val) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setParameter(String name, Object val) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setParameterList(String name, Collection vals, Type type) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setParameterList(String name, Collection vals) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setParameterList(String name, Object[] vals, Type type) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NativeQuery<R> setParameterList(String name, Object[] vals) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query<R> setResultTransformer(ResultTransformer transformer) {
		throw new UnsupportedOperationException();
	}
}
