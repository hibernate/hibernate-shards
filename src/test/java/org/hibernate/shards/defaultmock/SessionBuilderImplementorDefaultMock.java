package org.hibernate.shards.defaultmock;

import org.hibernate.ConnectionReleaseMode;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.SessionBuilder;
import org.hibernate.SessionEventListener;
import org.hibernate.engine.spi.SessionBuilderImplementor;
import org.hibernate.engine.spi.SessionOwner;

import java.sql.Connection;

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
	public SessionBuilder connection(Connection connection) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SessionBuilder connectionReleaseMode(ConnectionReleaseMode connectionReleaseMode) {
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
	public SessionBuilder flushBeforeCompletion(boolean flushBeforeCompletion) {
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
}
