package org.hibernate.shards.session;

import org.hibernate.shards.Shard;

interface UpdateOperation {

	void update(Shard shard, Object object);

	void merge(Shard shard, Object object);
}
