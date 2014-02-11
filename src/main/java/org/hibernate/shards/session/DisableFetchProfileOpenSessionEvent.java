package org.hibernate.shards.session;

import org.hibernate.Session;
import org.hibernate.UnknownProfileException;

/**
 * OpenSessionEvent which disables specified fetch profile.
 *
 * @author maxr@google.com (Max Ross)
 */
class DisableFetchProfileOpenSessionEvent implements OpenSessionEvent {

	private final String name;

	public DisableFetchProfileOpenSessionEvent(final String name) {
		this.name = name;
	}

	@Override
	public void onOpenSession(final Session session) {
		try {
			session.disableFetchProfile( name );
		}
		catch (UnknownProfileException e) {
			throw new UnsupportedOperationException( "fetch profile " + name + " is unknown to one session", e );
		}
	}
}
