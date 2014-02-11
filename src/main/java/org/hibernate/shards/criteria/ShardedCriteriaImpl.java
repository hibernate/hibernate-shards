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

package org.hibernate.shards.criteria;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.cfg.NotYetImplementedException;
import org.hibernate.criterion.AvgProjection;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.shards.Shard;
import org.hibernate.shards.ShardOperation;
import org.hibernate.shards.strategy.access.ShardAccessStrategy;
import org.hibernate.shards.strategy.exit.ConcatenateListsExitStrategy;
import org.hibernate.shards.strategy.exit.FirstNonNullResultExitStrategy;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.ResultTransformer;

/**
 * Concrete implementation of the {@link ShardedCriteria} interface.
 *
 * @author maxr@google.com (Max Ross)
 */
public class ShardedCriteriaImpl implements ShardedCriteria {

	private static final Iterable<CriteriaEvent> NO_CRITERIA_EVENTS =
			Collections.unmodifiableList( new ArrayList<CriteriaEvent>() );

	// unique id for this ShardedCriteria
	private final CriteriaId criteriaId;

	// the shards we know about
	private final List<Shard> shards;

	// a factory that knows how to create actual Criteria objects
	private final CriteriaFactory criteriaFactory;

	// the shard access strategy we use when we execute the Criteria
	// across multiple shards
	private final ShardAccessStrategy shardAccessStrategy;

	// the criteria collector we use to process the results of executing
	// the Criteria across multiple shards
	private final ExitOperationsCriteriaCollector criteriaCollector;

	// the last value with which setFirstResult was called
	private int firstResult;

	// the last value with which maxResults was called
	private Integer maxResults;

	/**
	 * Construct a ShardedCriteriaImpl
	 *
	 * @param criteriaId unique id for this ShardedCriteria
	 * @param shards the shards that this ShardedCriteria is aware of
	 * @param criteriaFactory factory that knows how to create concrete {@link Criteria} objects
	 * @param shardAccessStrategy the access strategy we use when we execute this
	 * ShardedCriteria across multiple shards.
	 */
	public ShardedCriteriaImpl(
			final CriteriaId criteriaId,
			final List<Shard> shards,
			final CriteriaFactory criteriaFactory,
			final ShardAccessStrategy shardAccessStrategy) {

		this.criteriaId = criteriaId;
		this.shards = shards;
		this.criteriaFactory = criteriaFactory;
		this.shardAccessStrategy = shardAccessStrategy;
		this.criteriaCollector = new ExitOperationsCriteriaCollector();
		this.criteriaCollector.setSessionFactory( shards.get( 0 ).getSessionFactoryImplementor() );
	}

	@Override
	public CriteriaId getCriteriaId() {
		return criteriaId;
	}

	@Override
	public CriteriaFactory getCriteriaFactory() {
		return criteriaFactory;
	}

	@Override
	public String getAlias() {
		return getOrEstablishSomeCriteria().getAlias();
	}

	@Override
	public Criteria setProjection(final Projection projection) {
		criteriaCollector.addProjection( projection );
		if ( projection instanceof AvgProjection ) {
			setAvgProjection( projection );
		}

		// TODO - handle ProjectionList
		return this;
	}

	@Override
	public Criteria add(final Criterion criterion) {
		return setCriteriaEvent( new AddCriterionEvent( criterion ) );
	}

	@Override
	public Criteria addOrder(final Order order) {
		// Order applies to top-level object so we pass a null association path
		criteriaCollector.addOrder( null, order );
		return setCriteriaEvent( new AddOrderEvent( order ) );
	}

	@Override
	public Criteria setFetchMode(final String associationPath, final FetchMode mode) throws HibernateException {
		return setCriteriaEvent( new SetFetchModeEvent( associationPath, mode ) );
	}

	@Override
	public Criteria setLockMode(final LockMode lockMode) {
		return setCriteriaEvent( new SetLockModeEvent( lockMode ) );
	}

	@Override
	public Criteria setLockMode(final String alias, final LockMode lockMode) {
		return setCriteriaEvent( new SetLockModeEvent( lockMode, alias ) );
	}

	@Override
	public Criteria createAlias(final String associationPath, final String alias) throws HibernateException {
		return setCriteriaEvent( new CreateAliasEvent( associationPath, alias ) );
	}

	@Override
	public Criteria createAlias(final String associationPath, final String alias, final int joinType)
			throws HibernateException {
		return setCriteriaEvent( new CreateAliasEvent( associationPath, alias, joinType ) );
	}

	@Override
	public Criteria createAlias(
			final String associationPath,
			final String alias,
			final int joinType,
			final Criterion withClause) throws HibernateException {
		final SubcriteriaFactory factory = new SubcriteriaFactoryImpl( associationPath, alias, joinType, withClause );
		return createSubcriteria( factory, associationPath );
	}

	@Override
	public Criteria createCriteria(final String associationPath) throws HibernateException {
		final SubcriteriaFactory factory = new SubcriteriaFactoryImpl( associationPath );
		return createSubcriteria( factory, associationPath );
	}

	@Override
	public Criteria createCriteria(final String associationPath, final int joinType) throws HibernateException {
		final SubcriteriaFactory factory = new SubcriteriaFactoryImpl( associationPath, joinType );
		return createSubcriteria( factory, associationPath );
	}

	@Override
	public Criteria createCriteria(final String associationPath, final String alias) throws HibernateException {
		final SubcriteriaFactory factory = new SubcriteriaFactoryImpl( associationPath, alias );
		return createSubcriteria( factory, associationPath );
	}

	@Override
	public Criteria createCriteria(final String associationPath, final String alias, final int joinType)
			throws HibernateException {
		final SubcriteriaFactory factory = new SubcriteriaFactoryImpl( associationPath, alias, joinType );
		return createSubcriteria( factory, associationPath );
	}

	@Override
	public Criteria createCriteria(
			final String associationPath,
			final String alias,
			final int joinType,
			final Criterion withClause)
			throws HibernateException {
		final SubcriteriaFactory factory = new SubcriteriaFactoryImpl( associationPath, alias, joinType, withClause );
		return createSubcriteria( factory, associationPath );
	}

	@Override
	public Criteria setResultTransformer(final ResultTransformer resultTransformer) {
		return setCriteriaEvent( new SetResultTransformerEvent( resultTransformer ) );
	}

	/**
	 * A description of the trickyness that goes on with first result and max result:
	 * You can safely apply the maxResult on each individual shard so long as there
	 * is no firstResult specified.  If firstResult is specified you can't
	 * safely apply it on each shard but you can set maxResult to be the existing
	 * value of maxResult + firstResult.
	 */
	@Override
	public Criteria setMaxResults(final int maxResults) {
		// the criteriaCollector will use the maxResult value that was passed in
		criteriaCollector.setMaxResults( maxResults );
		this.maxResults = maxResults;
		int adjustedMaxResults = maxResults + firstResult;
		// the query executed against each shard will use maxResult + firstResult
		return setCriteriaEvent( new SetMaxResultsEvent( adjustedMaxResults ) );
	}

	@Override
	public Criteria setFirstResult(final int firstResult) {
		criteriaCollector.setFirstResult( firstResult );
		this.firstResult = firstResult;
		// firstResult cannot be safely applied to the Criteria that will be
		// executed against the Shard.  If a maxResult has been set we need to adjust
		// that to take the firstResult into account.  Just calling setMaxResults
		// will take care of this for us.
		if ( maxResults != null ) {
			setMaxResults( maxResults );
		}
		return this;
	}

	@Override
	public boolean isReadOnlyInitialized() {
		for ( final Shard shard : shards ) {
			if ( shard.getCriteriaById( criteriaId ) != null ) {
				if ( !shard.getCriteriaById( criteriaId ).isReadOnlyInitialized() ) {
					// any one shard is not read only, we return false as a whole
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public boolean isReadOnly() {
		for ( final Shard shard : shards ) {
			final Criteria criteria = shard.getCriteriaById( criteriaId );
			if ( criteria != null ) {
				if ( criteria.isReadOnlyInitialized() && !criteria.isReadOnly() ) {
					// any one shard is not read only, we return false as a whole
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public Criteria setReadOnly(final boolean readOnly) {
		return setCriteriaEvent( new SetReadOnlyEvent( readOnly ) );
	}

	@Override
	public Criteria setFetchSize(final int fetchSize) {
		return setCriteriaEvent( new SetFetchSizeEvent( fetchSize ) );
	}

	@Override
	public Criteria setTimeout(final int timeout) {
		return setCriteriaEvent( new SetTimeoutEvent( timeout ) );
	}

	@Override
	public Criteria setCacheable(final boolean cacheable) {
		return setCriteriaEvent( new SetCacheableEvent( cacheable ) );
	}

	@Override
	public Criteria setCacheRegion(final String cacheRegion) {
		return setCriteriaEvent( new SetCacheRegionEvent( cacheRegion ) );
	}

	@Override
	public Criteria setComment(final String comment) {
		return setCriteriaEvent( new SetCommentEvent( comment ) );
	}

	@Override
	public Criteria setFlushMode(final FlushMode flushMode) {
		return setCriteriaEvent( new SetFlushModeEvent( flushMode ) );
	}

	@Override
	public Criteria setCacheMode(final CacheMode cacheMode) {
		return setCriteriaEvent( new SetCacheModeEvent( cacheMode ) );
	}

	/**
	 * Unsupported.  This is a scope decision, not a technical decision.
	 */
	@Override
	public ScrollableResults scroll() throws HibernateException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Unsupported.  This is a scope decision, not a technical decision.
	 */
	@Override
	public ScrollableResults scroll(final ScrollMode scrollMode) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public List list() throws HibernateException {

		// build a shard operation and apply it across all shards
		final ShardOperation<List<Object>> shardOp = new ShardOperation<List<Object>>() {

			@Override
			public List<Object> execute(final Shard shard) {
				shard.establishCriteria( ShardedCriteriaImpl.this );
				return shard.list( criteriaId );
			}

			@Override
			public String getOperationName() {
				return "list()";
			}
		};

		/**
		 * We don't support shard selection for criteria queries.  If you want
		 * custom shards, create a ShardedSession with only the shards you want.
		 * We're going to concatenate all our results and then use our
		 * criteria collector to do post processing.
		 */
		return shardAccessStrategy.apply(
				shards,
				shardOp,
				new ConcatenateListsExitStrategy(),
				criteriaCollector
		);
	}

	@Override
	public Object uniqueResult() throws HibernateException {

		// build a shard operation and apply it across all shards
		final ShardOperation<Object> shardOp = new ShardOperation<Object>() {

			@Override
			public Object execute(Shard shard) {
				shard.establishCriteria( ShardedCriteriaImpl.this );
				return shard.uniqueResult( criteriaId );
			}

			@Override
			public String getOperationName() {
				return "uniqueResult()";
			}
		};

		/**
		 * We don't support shard selection for criteria queries.  If you want
		 * custom shards, create a ShardedSession with only the shards you want.
		 * We're going to return the first non-null result we get from a shard.
		 */
		return shardAccessStrategy.apply(
				shards,
				shardOp,
				new FirstNonNullResultExitStrategy<Object>(),
				criteriaCollector
		);
	}

	ExitOperationsCriteriaCollector getCriteriaCollector() {
		return criteriaCollector;
	}

	/**
	 * @return any Criteria, or null if we don't have one
	 */
	private /*@Nullable*/ Criteria getSomeCriteria() {
		for ( final Shard shard : shards ) {
			final Criteria criteria = shard.getCriteriaById( criteriaId );
			if ( criteria != null ) {
				return criteria;
			}
		}
		return null;
	}

	/**
	 * @return any Criteria.  If no Criteria has been established we establish
	 * one and return it.
	 */
	private Criteria getOrEstablishSomeCriteria() {
		Criteria criteria = getSomeCriteria();
		if ( criteria == null ) {
			final Shard shard = shards.get( 0 );
			criteria = shard.establishCriteria( this );
		}
		return criteria;
	}

	private void setAvgProjection(final Projection projection) {

		// We need to modify the query to pull back not just the average but also
		// the count.  We'll do this by creating a ProjectionList with both the
		// average and the row count.
		final ProjectionList projectionList = Projections.projectionList()
				.add( projection )
				.add( Projections.rowCount() );

		setCriteriaEvent( new SetProjectionEvent( projectionList ) );
	}

	/**
	 * Creating sharded subcriteria is tricky.  We need to give the client a
	 * reference to a ShardedSubcriteriaImpl (which to the client just looks like
	 * a Criteria object).  Then, for each shard where the Criteria has already been
	 * established we need to create the actual subcriteria, and for each shard
	 * where the Criteria has not yet been established we need to register an
	 * event that will create the Subcriteria when the Criteria is established.
	 *
	 * @param factory the factory to use to create the subcriteria
	 * @param associationPath the association path to the property on which we're
	 * creating a subcriteria
	 *
	 * @return a new ShardedSubcriteriaImpl
	 */
	private ShardedSubcriteriaImpl createSubcriteria(final SubcriteriaFactory factory, final String associationPath) {

		final ShardedSubcriteriaImpl subcriteria =
				new ShardedSubcriteriaImpl( shards, this, criteriaCollector, associationPath );

		for ( final Shard shard : shards ) {
			final Criteria criteria = shard.getCriteriaById( criteriaId );
			if ( criteria != null ) {
				factory.createSubcriteria( criteria, NO_CRITERIA_EVENTS );
			}
			else {
				final CreateSubcriteriaEvent event =
						new CreateSubcriteriaEvent( factory, subcriteria.getSubcriteriaRegistrar( shard ) );
				shard.addCriteriaEvent( criteriaId, event );
			}
		}
		return subcriteria;
	}

	private ShardedCriteriaImpl setCriteriaEvent(final CriteriaEvent event) {
		for ( final Shard shard : shards ) {
			if ( shard.getCriteriaById( criteriaId ) != null ) {
				event.onEvent( shard.getCriteriaById( criteriaId ) );
			}
			else {
				shard.addCriteriaEvent( criteriaId, event );
			}
		}
		return this;
	}

	//todo impl these methods

	@Override
	public Criteria createAlias(String associationPath, String alias, JoinType joinType) throws HibernateException {
		return null;
	}

	@Override
	public Criteria createAlias(String associationPath, String alias, JoinType joinType, Criterion withClause)
			throws HibernateException {
		return null;
	}

	@Override
	public Criteria createCriteria(String associationPath, JoinType joinType) throws HibernateException {
		return null;
	}

	@Override
	public Criteria createCriteria(String associationPath, String alias, JoinType joinType) throws HibernateException {
		return null;
	}

	@Override
	public Criteria createCriteria(String associationPath, String alias, JoinType joinType, Criterion withClause)
			throws HibernateException {
		return null;
	}

	@Override
	public Criteria addQueryHint(String hint) {
		//todo may be UnsupportedOperationException
		throw new NotYetImplementedException();
	}
}
