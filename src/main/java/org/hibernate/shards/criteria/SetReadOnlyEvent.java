package org.hibernate.shards.criteria;

import org.hibernate.Criteria;

class SetReadOnlyEvent implements CriteriaEvent {

	private final boolean readOnly;

	public SetReadOnlyEvent(final boolean readOnly) {
		this.readOnly = readOnly;
	}

	@Override
	public void onEvent(final Criteria criteria) {
		criteria.setReadOnly( readOnly );
	}
}
