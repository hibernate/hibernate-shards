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
import org.hibernate.transform.ResultTransformer;
import org.hibernate.type.Type;

/**
 * @author Maulik Shah
 */
public class ShardedQueryDefaultMock implements ShardedQuery {

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
	public ParameterMetadata getParameterMetadata() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Type[] getReturnTypes() throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String[] getReturnAliases() throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String[] getNamedParameters() throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator iterate() throws HibernateException {
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
	public Optional uniqueResultOptional() {
		return Optional.empty();
	}

	@Override
	public Stream stream() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setParameter(String name, Instant value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setParameter(String name, LocalDateTime value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setParameter(String name, ZonedDateTime value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setParameter(String name, OffsetDateTime value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setParameter(int position, Instant value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setParameter(int position, LocalDateTime value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setParameter(int position, ZonedDateTime value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setParameter(int position, OffsetDateTime value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setParameter(Parameter param, OffsetDateTime value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setParameter(Parameter param, ZonedDateTime value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setParameter(Parameter param, LocalDateTime value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setParameter(Parameter param, Instant value, TemporalType temporalType) {
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
	public List list() throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object uniqueResult() throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public FlushMode getHibernateFlushMode() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int executeUpdate() throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setMaxResults(int maxResults) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setFirstResult(int firstResult) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setHint(String hintName, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, Object> getHints() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setParameter(Parameter param, Date value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setParameter(Parameter param, Calendar value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setParameter(Parameter param, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isReadOnly() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setReadOnly(boolean readOnly) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setHibernateFlushMode(FlushMode flushMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setCacheable(boolean cacheable) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setCacheRegion(String cacheRegion) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setTimeout(int timeout) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setFetchSize(int fetchSize) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setLockOptions(LockOptions lockOptions) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setLockMode(String alias, LockMode lockMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setComment(String comment) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setFlushMode(FlushMode flushMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setCacheMode(CacheMode cacheMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setParameter(int position, Object val, Type type) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setFlushMode(FlushModeType flushMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setLockMode(LockModeType lockMode) {
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
	public Query setParameter(String name, Object val, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setParameter(QueryParameter parameter, Object val, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setParameter(String name, Object val, Type type) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setParameter(String name, Calendar value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setParameter(String name, Date value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setParameter(int position, Object val) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setParameter(int position, Calendar value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setParameter(int position, Date value, TemporalType temporalType) {
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
	public Parameter<?> getParameter(int position) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isBound(Parameter<?> param) {
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
	public <T> T getParameterValue(Parameter<T> param) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> Parameter<T> getParameter(int position, Class<T> type) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> Parameter<T> getParameter(String name, Class<T> type) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setParameter(QueryParameter parameter, Object val, Type type) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setParameter(int position, Object val, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setParameter(QueryParameter parameter, Object val) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setParameter(String name, Object val) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setParameters(Object[] values, Type[] types) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setParameterList(String name, Collection vals, Type type) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public org.hibernate.Query setParameterList(int position, Collection values, Type type) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setParameterList(String name, Collection vals) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public org.hibernate.Query setParameterList(int position, Collection values) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setParameterList(String name, Object[] vals, Type type) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public org.hibernate.Query setParameterList(int position, Object[] values, Type type) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setParameterList(String name, Object[] vals) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public org.hibernate.Query setParameterList(int position, Object[] values) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setProperties(Object bean) throws HibernateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setEntity(int position, Object val) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setEntity(String name, Object val) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Type determineProperBooleanType(int position, Object value, Type defaultType) {
		return null;
	}

	@Override
	public Type determineProperBooleanType(String name, Object value, Type defaultType) {
		return null;
	}

	@Override
	public Query setResultTransformer(ResultTransformer transformer) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setProperties(Map bean) throws HibernateException {
		throw new UnsupportedOperationException();
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
	public Query addQueryHint(String hint) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setParameterList(QueryParameter parameter, Collection values) {
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
