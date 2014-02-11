/**
 * Copyright (C) 2007 Google Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 */
package org.hibernate.shards.session;

import junit.framework.TestCase;

import org.hibernate.shards.ShardId;

/**
 * @author maxr@google.com (Max Ross)
 */
public class ShardAwareInterceptorTest extends TestCase {

	public void testOnLoadNotShardAware() {
		ShardAwareInterceptor interceptor =
				new ShardAwareInterceptor( new ShardIdResolverDefaultMock() );

		interceptor.onLoad( new Object(), null, null, null, null );
		// doesn't blow up
	}

	public void testOnLoadShardAware() {
		final ShardId shardId = new ShardId( 33 );
		ShardAwareInterceptor interceptor =
				new ShardAwareInterceptor(
						new ShardIdResolverDefaultMock() {
							public ShardId getShardIdForObject(Object obj) {
								return shardId;
							}
						}
				);

		MyShardAware msa = new MyShardAware();
		interceptor.onLoad( msa, null, null, null, null );
		assertSame( shardId, msa.getShardId() );
	}

	public void testOnSaveNotShardAware() {
		ShardAwareInterceptor interceptor =
				new ShardAwareInterceptor( new ShardIdResolverDefaultMock() );

		interceptor.onSave( new Object(), null, null, null, null );
		// doesn't blow up
	}

	public void testOnSaveShardAware() {
		final ShardId shardId = new ShardId( 33 );
		ShardAwareInterceptor interceptor =
				new ShardAwareInterceptor(
						new ShardIdResolverDefaultMock() {
							public ShardId getShardIdForObject(Object obj) {
								return shardId;
							}
						}
				);

		MyShardAware msa = new MyShardAware();
		interceptor.onSave( msa, null, null, null, null );
		assertSame( shardId, msa.getShardId() );
	}

	private class MyShardAware implements ShardAware {

		private ShardId shardId;

		public void setShardId(ShardId shardId) {
			this.shardId = shardId;
		}

		public ShardId getShardId() {
			return shardId;
		}
	}
}
