package org.hibernate.shards.defaultmock;

import java.sql.Connection;
import java.util.TimeZone;

import org.hibernate.ConnectionReleaseMode;
import org.hibernate.FlushMode;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.SessionBuilder;
import org.hibernate.SessionEventListener;
import org.hibernate.engine.spi.SessionBuilderImplementor;
import org.hibernate.engine.spi.SessionOwner;
import org.hibernate.resource.jdbc.spi.PhysicalConnectionHandlingMode;
import org.hibernate.resource.jdbc.spi.StatementInspector;

public class SessionBuilderImplementorDefaultMock implements SessionBuilderImplementor {

	@Override
	public SessionBuilder owner(SessionOwner sessionOwner) {
		return this;
	}

	@Override
	public Session openSession() {
		throw new UnsupportedOperationException();
	}

	@Override
	public SessionBuilder interceptor(Interceptor interceptor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SessionBuilder noInterceptor() {
		throw new UnsupportedOperationException();
	}

	@Override
	public SessionBuilder statementInspector(StatementInspector statementInspector) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SessionBuilder connection(Connection connection) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SessionBuilder connectionReleaseMode(ConnectionReleaseMode connectionReleaseMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SessionBuilder connectionHandlingMode(PhysicalConnectionHandlingMode mode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SessionBuilder autoJoinTransactions(boolean autoJoinTransactions) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public SessionBuilder autoClose(boolean autoClose) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SessionBuilder autoClear(boolean autoClear) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SessionBuilder flushMode(FlushMode flushMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SessionBuilder tenantIdentifier(String tenantIdentifier) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SessionBuilder eventListeners(SessionEventListener... listeners) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SessionBuilder clearEventListeners() {
		throw new UnsupportedOperationException();
	}

	@Override
	public SessionBuilder jdbcTimeZone(TimeZone timeZone) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SessionBuilder setQueryParameterValidation(boolean enabled) {
		throw new UnsupportedOperationException();
	}
}
