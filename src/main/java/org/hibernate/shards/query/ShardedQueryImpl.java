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

package org.hibernate.shards.query;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Parameter;
import javax.persistence.TemporalType;

import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.engine.spi.RowSelection;
import org.hibernate.query.ParameterMetadata;
import org.hibernate.query.Query;
import org.hibernate.query.QueryParameter;
import org.hibernate.query.QueryProducer;
import org.hibernate.shards.Shard;
import org.hibernate.shards.ShardOperation;
import org.hibernate.shards.strategy.access.ShardAccessStrategy;
import org.hibernate.shards.strategy.exit.ConcatenateListsExitStrategy;
import org.hibernate.shards.strategy.exit.FirstNonNullResultExitStrategy;
import org.hibernate.shards.util.Preconditions;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.type.Type;

/**
 * Concrete implementation of ShardedQuery provided by Hibernate Shards. This
 * implementation introduces limits to the HQL language; mostly around
 * limits and aggregation. Its approach is simply to execute the query on
 * each shard and compile the results in a list, or if a unique result is
 * desired, the fist non-null result is returned.
 * <p/>
 * The setFoo methods are implemented using a set of classes that implement
 * the QueryEvent interface and are called SetFooEvent. These query events
 * are used to call setFoo with the appropriate arguments on each Query that
 * is executed on a shard.
 *
 * @author Maulik Shah
 * @see org.hibernate.shards.query.QueryEvent
 * <p/>
 * {@inheritDoc}
 */
public class ShardedQueryImpl<R> implements ShardedQuery<R> {

	private final QueryId queryId;
	private final List<Shard> shards;
	private final QueryFactory queryFactory;
	private final ShardAccessStrategy shardAccessStrategy;

	/**
	 * The queryCollector is not used in ShardedQueryImpl as it would require
	 * this implementation to parse the query string and extract which exit
	 * operations would be appropriate. This member is a place holder for
	 * future development.
	 */
	private final ExitOperationsQueryCollector queryCollector;

	/**
	 * Constructor for ShardedQueryImpl
	 *
	 * @param queryId the id of the query
	 * @param shards list of shards on which this query will be executed
	 * @param queryFactory factory that knows how to create the actual query we'll execute
	 * @param shardAccessStrategy the shard strategy for this query
	 */
	public ShardedQueryImpl(
			final QueryId queryId,
			final List<Shard> shards,
			final QueryFactory queryFactory,
			final ShardAccessStrategy shardAccessStrategy) {

		this.queryId = queryId;
		this.shards = shards;
		this.queryFactory = queryFactory;
		this.shardAccessStrategy = shardAccessStrategy;
		this.queryCollector = new ExitOperationsQueryCollector();

		Preconditions.checkState( !shards.isEmpty() );
		for ( Shard shard : shards ) {
			Preconditions.checkNotNull( shard );
		}
	}

	@Override
	public QueryId getQueryId() {
		return queryId;
	}

	@Override
	public QueryFactory getQueryFactory() {
		return queryFactory;
	}

	@Override
	public String getQueryString() {
		return getOrEstablishSomeQuery().getQueryString();
	}

	@Override
	public ParameterMetadata getParameterMetadata() {
		return null;
	}

	@Override
	public Type[] getReturnTypes() throws HibernateException {
		return getOrEstablishSomeQuery().getReturnTypes();
	}

	@Override
	public String[] getReturnAliases() throws HibernateException {
		return getOrEstablishSomeQuery().getReturnAliases();
	}

	@Override
	public String[] getNamedParameters() throws HibernateException {
		return getOrEstablishSomeQuery().getNamedParameters();
	}

	/**
	 * This method currently wraps list().
	 * <p/>
	 * {@inheritDoc}
	 *
	 * @return an iterator over the results of the query
	 *
	 * @throws HibernateException
	 */
	@Override
	public Iterator<R> iterate() throws HibernateException {
		/*
		 * TODO(maulik) Hibernate in Action says these two methods are equivalent
		 * in what the content that they return but are implemented differently.
		 * We should figure out the difference and implement correctly.
		 */
		return list().iterator();
	}

	@Override
	public QueryProducer getProducer() {
		return getOrEstablishSomeQuery().getProducer();
	}

	@Override
	public RowSelection getQueryOptions() {
		return getOrEstablishSomeQuery().getQueryOptions();
	}

	@Override
	public Optional<R> uniqueResultOptional() {
		return Optional.empty();
	}

	@Override
	public Stream<R> stream() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query<R> setParameter(Parameter<Instant> param, Instant value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query<R> setParameter(Parameter<LocalDateTime> param, LocalDateTime value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query<R> setParameter(Parameter<ZonedDateTime> param, ZonedDateTime value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query<R> setParameter(Parameter<OffsetDateTime> param, OffsetDateTime value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query<R> setParameter(String name, Instant value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query<R> setParameter(String name, LocalDateTime value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query<R> setParameter(String name, ZonedDateTime value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query<R> setParameter(String name, OffsetDateTime value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query<R> setParameter(int position, Instant value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query<R> setParameter(int position, LocalDateTime value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query<R> setParameter(int position, ZonedDateTime value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query<R> setParameter(int position, OffsetDateTime value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Scrolling is unsupported. Current implementation throws an
	 * UnsupportedOperationException. A dumb implementation of scroll might be
	 * possible; however it would provide no performance benefit. An intelligent
	 * implementation would require re-querying shards frequently and a
	 * deterministic way to compile results.
	 */
	@Override
	public ScrollableResults scroll() throws HibernateException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Scrolling is unsupported. Current implementation throws an
	 * UnsupportedOperationException. A dumb implementation of scroll might be
	 * possible; however it would provide no performance benefit. An intelligent
	 * implementation would require re-querying shards frequently and a
	 * deterministic way to compile results.
	 */
	@Override
	public ScrollableResults scroll(final ScrollMode scrollMode) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	/**
	 * The implementation executes the query on each shard and concatenates the
	 * results.
	 *
	 * @return a list containing the concatenated results of executing the
	 * query on all shards
	 *
	 * @throws HibernateException
	 */
	@Override
	public List<R> list() throws HibernateException {
		final ShardOperation<List<Object>> shardOp = new ShardOperation<List<Object>>() {

			@Override
			public List<Object> execute(final Shard shard) {
				shard.establishQuery( ShardedQueryImpl.this );
				return shard.list( queryId );
			}

			@Override
			public String getOperationName() {
				return "list()";
			}
		};

		/*
		 * We don't support shard selection for HQL queries.  If you want
		 * custom shards, create a ShardedSession with only the shards you want.
		 */
		return (List<R>) shardAccessStrategy.apply(
				shards,
				shardOp,
				new ConcatenateListsExitStrategy(),
				queryCollector
		);
	}

	/**
	 * The implementation executes the query on each shard and returns the first
	 * non-null result.
	 * <p/>
	 * {@inheritDoc}
	 *
	 * @return the first non-null result, or null if no non-null result found
	 *
	 * @throws HibernateException
	 */
	@Override
	public R uniqueResult() throws HibernateException {
		final ShardOperation<R> shardOp = new ShardOperation<R>() {

			@Override
			public R execute(final Shard shard) {
				shard.establishQuery( ShardedQueryImpl.this );
				return shard.uniqueResult( queryId );
			}

			@Override
			public String getOperationName() {
				return "uniqueResult()";
			}
		};

		/*
		 * We don't support shard selection for HQL queries.  If you want
		 * custom shards, create a ShardedSession with only the shards you want.
		 */
		return shardAccessStrategy.apply(
				shards,
				shardOp,
				new FirstNonNullResultExitStrategy<>(),
				queryCollector
		);
	}

	@Override
	public FlushMode getHibernateFlushMode() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @throws HibernateException
	 */
	@Override
	public int executeUpdate() throws HibernateException {
		final ShardOperation<List<Object>> shardOp = new ShardOperation<List<Object>>() {

			@Override
			public List<Object> execute(final Shard shard) {
				shard.establishQuery( ShardedQueryImpl.this );
				int tmp = shard.executeUpdate( queryId );
				return Collections.singletonList( tmp );
			}

			@Override
			public String getOperationName() {
				return "executeUpdate()";
			}
		};

		final List<Object> rets = shardAccessStrategy.apply(
				shards, shardOp, new ConcatenateListsExitStrategy(),
				queryCollector
		);

		int sum = 0;

		for ( final Object i : rets ) {
			sum += (Integer) i;
		}

		return sum;
	}

	@Override
	public Query<R> setMaxResults(final int maxResults) {
		queryCollector.setMaxResults( maxResults );
		return this;
	}

	@Override
	public Query<R> setFirstResult(final int firstResult) {
		queryCollector.setFirstResult( firstResult );
		return this;
	}

	@Override
	public Query<R> setHint(String hintName, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, Object> getHints() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> Query<R> setParameter(Parameter<T> param, T value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query<R> setParameter(Parameter<Calendar> param, Calendar value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query<R> setParameter(Parameter<Date> param, Date value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isReadOnly() {
		for ( final Shard shard : shards ) {
			if ( shard.getQueryById( queryId ) != null ) {
				if ( !shard.getQueryById( queryId ).isReadOnly() ) {
					//any one shard is not read only, we return false as a whole
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public Query<R> setReadOnly(final boolean readOnly) {
		return setQueryEvent( new SetReadOnlyEvent( readOnly ) );
	}

	@Override
	public Query<R> setHibernateFlushMode(FlushMode flushMode) {
		return setQueryEvent( new SetFlushModeEvent( flushMode ) );
	}

	@Override
	public Query<R> setCacheable(final boolean cacheable) {
		return setQueryEvent( new SetCacheableEvent( cacheable ) );
	}

	@Override
	public Query<R> setCacheRegion(final String cacheRegion) {
		return setQueryEvent( new SetCacheRegionEvent( cacheRegion ) );
	}

	@Override
	public Query<R> setTimeout(final int timeout) {
		return setQueryEvent( new SetTimeoutEvent( timeout ) );
	}

	@Override
	public Query<R> setFetchSize(final int fetchSize) {
		return setQueryEvent( new SetFetchSizeEvent( fetchSize ) );
	}

	@Override
	public Query<R> setLockOptions(final LockOptions lockOptions) {
		return setQueryEvent( new SetLockOptionsEvent( lockOptions ) );
	}

	@Override
	public Query<R> setLockMode(final String alias, final LockMode lockMode) {
		return setQueryEvent( new SetLockModeEvent( alias, lockMode ) );
	}

	@Override
	public Query<R> setComment(final String comment) {
		return setQueryEvent( new SetCommentEvent( comment ) );
	}

	@Override
	public Query<R> setCacheMode(final CacheMode cacheMode) {
		return setQueryEvent( new SetCacheModeEvent( cacheMode ) );
	}

	@Override
	public Query<R> setParameter(final int position, final Object val, final Type type) {
		return setQueryEvent( new SetParameterEvent( position, val, type ) );
	}

	@Override
	public <P> Query<R> setParameter(QueryParameter<P> parameter, P val, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <P> Query<R> setParameter(String name, P val, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query<R> setFlushMode(FlushModeType flushMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query<R> setLockMode(LockModeType lockMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public LockModeType getLockMode() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T unwrap(Class<T> cls) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query<R> setParameter(final String name, final Object val, final Type type) {
		return setQueryEvent( new SetParameterEvent( name, val, type ) );
	}

	@Override
	public Query<R> setParameter(String name, Calendar value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query<R> setParameter(String name, Date value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query<R> setParameter(final int position, final Object val) throws HibernateException {
		return setQueryEvent( new SetParameterEvent( position, val ) );
	}

	@Override
	public Query<R> setParameter(int position, Calendar value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query<R> setParameter(int position, Date value, TemporalType temporalType) {
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
	public <T> Query<R> setParameter(QueryParameter<T> parameter, T val) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <P> Query<R> setParameter(int position, P val, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <P> Query<R> setParameter(QueryParameter<P> parameter, P val, Type type) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query<R> setParameter(final String name, final Object val) throws HibernateException {
		return setQueryEvent( new SetParameterEvent( name, val ) );
	}

	@Override
	public Query<R> setParameterList(final String name, final Collection vals, final Type type)
			throws HibernateException {
		return setQueryEvent( new SetParameterListEvent( name, vals, type ) );
	}

	@Override
	public org.hibernate.Query<R> setParameterList(int position, Collection values, Type type) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query<R> setParameterList(final String name, final Collection vals) throws HibernateException {
		return setQueryEvent( new SetParameterListEvent( name, vals ) );
	}

	@Override
	public org.hibernate.Query<R> setParameterList(int position, Collection values) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query<R> setParameterList(final String name, final Object[] vals, final Type type)
			throws HibernateException {
		return setQueryEvent( new SetParameterListEvent( name, vals, type ) );
	}

	@Override
	public org.hibernate.Query<R> setParameterList(int position, Object[] values, Type type) {
		return setQueryEvent( new SetParameterListEvent( position, values, type ) );
	}

	@Override
	public Query<R> setParameterList(final String name, final Object[] vals) throws HibernateException {
		return setQueryEvent( new SetParameterListEvent( name, vals ) );
	}

	@Override
	public org.hibernate.Query<R> setParameterList(int position, Object[] values) {
		return setQueryEvent( new SetParameterListEvent( position, values ) );
	}

	@Override
	public Query<R> setProperties(final Object bean) throws HibernateException {
		return setQueryEvent( new SetPropertiesEvent( bean ) );
	}

	@Override
	public Query<R> setEntity(final int position, final Object val) {
		return setQueryEvent( new SetParameterEvent( position, val ) );
	}

	@Override
	public Query<R> setEntity(final String name, final Object val) {
		return setQueryEvent( new SetParameterEvent( name, val ) );
	}

	@Override
	public Type determineProperBooleanType(int position, Object value, Type defaultType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Type determineProperBooleanType(String name, Object value, Type defaultType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query<R> setResultTransformer(final ResultTransformer transformer) {
		return setQueryEvent( new SetResultTransformerEvent( transformer ) );
	}

	@Override
	public Query<R> setProperties(Map map) throws HibernateException {
		return setQueryEvent( new SetPropertiesEvent( map ) );
	}

	private Query getSomeQuery() {
		for ( final Shard shard : shards ) {
			Query query = shard.getQueryById( queryId );
			if ( query != null ) {
				return query;
			}
		}
		return null;
	}

	private Query getOrEstablishSomeQuery() {
		Query query = getSomeQuery();
		if ( query == null ) {
			Shard shard = shards.get( 0 );
			query = shard.establishQuery( this );
		}
		return query;
	}

	private Query<R> setQueryEvent(final QueryEvent queryEvent) throws HibernateException {
		for ( final Shard shard : shards ) {
			if ( shard.getQueryById( queryId ) != null ) {
				queryEvent.onEvent( shard.getQueryById( queryId ) );
			}
			else {
				shard.addQueryEvent( queryId, queryEvent );
			}
		}
		return this;
	}

	@Override
	public int getMaxResults() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getFirstResult() {
		throw new UnsupportedOperationException();
	}

	@Override
	public LockOptions getLockOptions() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getComment() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query<R> addQueryHint(String hint) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <P> Query<R> setParameterList(QueryParameter<P> parameter, Collection<P> values) {
		throw new UnsupportedOperationException();
	}

	@Override
	public FlushModeType getFlushMode() {
		throw new UnsupportedOperationException();
	}

	@Override
	public CacheMode getCacheMode() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isCacheable() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getCacheRegion() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Integer getTimeout() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Integer getFetchSize() {
		throw new UnsupportedOperationException();
	}
}
