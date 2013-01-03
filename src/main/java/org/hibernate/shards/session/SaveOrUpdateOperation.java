package org.hibernate.shards.session;

import org.hibernate.shards.Shard;

interface SaveOrUpdateOperation {

	void saveOrUpdate(Shard shard, Object object);

	void merge(Shard shard, Object object);
}
