package org.hibernate.shards.session;

import org.hibernate.Session;
import org.hibernate.UnknownProfileException;

class EnableFetchProfileOpenSessionEvent implements OpenSessionEvent {

	private final String name;

	public EnableFetchProfileOpenSessionEvent(final String name) {
		this.name = name;
	}

	@Override
	public void onOpenSession(final Session session) {
		try {
			session.enableFetchProfile( name );
		}
		catch (UnknownProfileException e) {
			throw new UnsupportedOperationException( "fetch profile " + name + " is unknown to one session", e );
		}
	}
}
