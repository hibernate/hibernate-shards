package org.hibernate.shards.session;

import org.hibernate.shards.Shard;

interface DeleteOperation {
	void delete(Shard shard, Object object);
}
