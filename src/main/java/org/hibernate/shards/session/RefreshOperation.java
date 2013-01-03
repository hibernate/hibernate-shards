package org.hibernate.shards.session;

import org.hibernate.shards.Shard;

interface RefreshOperation {
	void refresh(Shard shard, Object object);
}
