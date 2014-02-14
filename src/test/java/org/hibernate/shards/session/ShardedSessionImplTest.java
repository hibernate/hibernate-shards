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

import java.io.Serializable;
import java.sql.Connection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.SessionEventListener;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.procedure.ProcedureCall;
import org.hibernate.shards.Shard;
import org.hibernate.shards.ShardDefaultMock;
import org.hibernate.shards.ShardId;
import org.hibernate.shards.ShardedSessionFactoryDefaultMock;
import org.hibernate.shards.defaultmock.ClassMetadataDefaultMock;
import org.hibernate.shards.defaultmock.InterceptorDefaultMock;
import org.hibernate.shards.defaultmock.SessionDefaultMock;
import org.hibernate.shards.defaultmock.TypeDefaultMock;
import org.hibernate.shards.engine.ShardedSessionFactoryImplementor;
import org.hibernate.shards.strategy.ShardStrategy;
import org.hibernate.shards.strategy.ShardStrategyDefaultMock;
import org.hibernate.shards.strategy.selection.ShardSelectionStrategy;
import org.hibernate.shards.strategy.selection.ShardSelectionStrategyDefaultMock;
import org.hibernate.shards.util.InterceptorList;
import org.hibernate.shards.util.Lists;
import org.hibernate.shards.util.Pair;
import org.hibernate.shards.util.Sets;
import org.hibernate.type.Type;

/**
 * @author maxr@google.com (Max Ross)
 */
public class ShardedSessionImplTest extends TestCase {

	private static class MyShardedSessionImpl extends ShardedSessionImpl {

		public MyShardedSessionImpl() {
			super(
					new ShardedSessionFactoryDefaultMock() {
						@Override
						public Map<SessionFactoryImplementor, Set<ShardId>> getSessionFactoryShardIdMap() {
							return Collections.emptyMap();
						}
					},
					new ShardStrategyDefaultMock(),
					Collections.<Class<?>>emptySet(),
					true
			);
		}
	}

	public void testApplySaveOrUpdateOperation() {
		final List<ShardId> shardIdToReturn = Lists.newArrayList( new ShardId( 0 ) );
		final List<Shard> shardListToReturn = Lists.<Shard>newArrayList( new ShardDefaultMock() );
		final Serializable[] idToReturn = {null};
		final boolean[] saveCalled = {false};
		ShardedSessionImpl ssi = new MyShardedSessionImpl() {
			@Override
			public ShardId getShardIdForObject(Object obj) {
				return shardIdToReturn.get( 0 );
			}

			List<Shard> determineShardsObjectViaResolutionStrategy(Object object) {
				return shardListToReturn;
			}

			Serializable extractId(Object object) {
				return idToReturn[0];
			}

			public Serializable save(String entityName, Object object)
					throws HibernateException {
				saveCalled[0] = true;
				return null;
			}
		};

		final boolean[] saveOrUpdateCalled = {false};
		final boolean[] mergeCalled = {false};
		SaveOrUpdateOperation op = new SaveOrUpdateOperation() {
			public void saveOrUpdate(Shard shard, Object object) {
				saveOrUpdateCalled[0] = true;
			}

			public void merge(Shard shard, Object object) {
				mergeCalled[0] = true;
			}

		};
		ssi.applySaveOrUpdateOperation( op, null );
		assertTrue( saveOrUpdateCalled[0] );
		assertFalse( mergeCalled[0] );
		shardIdToReturn.set( 0, null );
		saveOrUpdateCalled[0] = false;

		ssi.applySaveOrUpdateOperation( op, null );
		assertTrue( saveOrUpdateCalled[0] );
		assertFalse( mergeCalled[0] );
		shardIdToReturn.set( 0, null );
		saveOrUpdateCalled[0] = false;

		shardListToReturn.add( new ShardDefaultMock() );
		ssi.applySaveOrUpdateOperation( op, null );
		assertFalse( saveOrUpdateCalled[0] );
		assertFalse( mergeCalled[0] );
		assertTrue( saveCalled[0] );

		//TODO(maxr) write test for when we call merge()
	}

	public void testClose() {
		ShardedSessionImpl ssi = new MyShardedSessionImpl();
		ssi.close();
	}

	public void testShardLock() {
		final ShardId shardIdToReturn = new ShardId( 0 );
		ShardedSessionImpl ssi = new MyShardedSessionImpl() {
			private boolean called = false;

			@Override
			ShardId getShardIdOfRelatedObject(Object obj) {
				if ( called ) {
					throw new UnsupportedOperationException();
				}
				called = true;
				return shardIdToReturn;
			}
		};
		ssi.lockShard();
		Object obj = new Object();
		assertSame( shardIdToReturn, ssi.selectShardIdForNewObject( obj ) );
		assertSame( shardIdToReturn, ssi.selectShardIdForNewObject( obj ) );
	}

	public void testLackingShardLock() {
		ShardedSessionFactoryImplementor ssf = new ShardedSessionFactoryDefaultMock() {
			@Override
			public Map<SessionFactoryImplementor, Set<ShardId>> getSessionFactoryShardIdMap() {
				return Collections.emptyMap();
			}
		};

		final ShardId shardIdToReturn = new ShardId( 33 );
		final ShardSelectionStrategy shardSelectionStrategy = new ShardSelectionStrategyDefaultMock() {
			@Override
			public ShardId selectShardIdForNewObject(Object obj) {
				return shardIdToReturn;
			}
		};
		ShardStrategy shardStrategy = new ShardStrategyDefaultMock() {
			@Override
			public ShardSelectionStrategy getShardSelectionStrategy() {
				return shardSelectionStrategy;
			}
		};
		Set<Class<?>> classesRequiringShardLocks =
				Sets.<Class<?>>newHashSet( Integer.class, String.class );
		ShardedSessionImpl ssi =
				new ShardedSessionImpl( ssf, shardStrategy, classesRequiringShardLocks, true ) {
					@Override
					ShardId getShardIdOfRelatedObject(Object obj) {
						return null;
					}
				};
		assertSame( shardIdToReturn, ssi.selectShardIdForNewObject( new Object() ) );
		try {
			ssi.selectShardIdForNewObject( 3 );
			fail( "expected he" );
		}
		catch (HibernateException he) {
			// good
		}
		try {
			ssi.selectShardIdForNewObject( "three" );
		}
		catch (HibernateException he) {
			// good
		}
	}

	public void testGetShardIdOfRelatedObjectWithNullAssociation() {
		// the mapping has an association, but the value for that association is null
		ShardedSessionImpl ssi = new MyShardedSessionImpl() {
			final ShardId[] shardIdForObjectToReturn = {new ShardId( 33 ), new ShardId( 33 )};
			int shardIdForObjectToReturnIndex = 0;

			@Override
			ClassMetadata getClassMetadata(Class<?> clazz) {
				return new ClassMetadataDefaultMock() {
					@Override
					public Type[] getPropertyTypes() {
						return new Type[] {new MyType( true, false ), new MyType( true, false )};
					}

					@Override
					public Object[] getPropertyValues(Object entity) {
						return new Object[] {null, null};
					}
				};
			}

			@Override
			public ShardId getShardIdForObject(Object obj) {
				return shardIdForObjectToReturn[shardIdForObjectToReturnIndex++];
			}
		};

		Object obj = new Object();
		// test an association
		assertNull( ssi.getShardIdOfRelatedObject( obj ) );
	}

	public void testGetShardIdOfRelatedObjectWithAssociation() {
		// the mapping has an assocation and the association is not null
		ShardedSessionImpl ssi = new MyShardedSessionImpl() {
			final ShardId[] shardIdForObjectToReturn = {new ShardId( 33 ), new ShardId( 33 )};
			int shardIdForObjectToReturnIndex = 0;

			@Override
			ClassMetadata getClassMetadata(Class<?> clazz) {
				return new ClassMetadataDefaultMock() {
					@Override
					public Type[] getPropertyTypes() {
						return new Type[] {new MyType( true, false ), new MyType( true, false )};
					}

					@Override
					public Object[] getPropertyValues(Object entity) {
						return new Object[] {"yam", "jam"};
					}
				};
			}

			@Override
			public ShardId getShardIdForObject(Object obj) {
				return shardIdForObjectToReturn[shardIdForObjectToReturnIndex++];
			}
		};

		Object obj = new Object();
		// test an association
		assertEquals( new ShardId( 33 ), ssi.getShardIdOfRelatedObject( obj ) );
	}

	public void testGetShardIdOfRelatedObjectWithBadAssociation() {
		// the mapping has an association that is not null, and the shard id
		// for that assocation does not match
		final ShardId[] shardIdForObjectToReturn = {new ShardId( 33 ), new ShardId( 34 )};
		ShardedSessionImpl ssi = new MyShardedSessionImpl() {
			int shardIdForObjectToReturnIndex = 0;

			@Override
			ClassMetadata getClassMetadata(Class<?> clazz) {
				return new ClassMetadataDefaultMock() {
					@Override
					public Type[] getPropertyTypes() {
						return new Type[] {new MyType( true, false ), new MyType( true, false )};
					}

					@Override
					public Object[] getPropertyValues(Object entity) {
						return new Object[] {"yam", "jam"};
					}
				};
			}

			@Override
			public ShardId getShardIdForObject(Object obj) {
				return shardIdForObjectToReturn[shardIdForObjectToReturnIndex++];
			}
		};

		Object obj = new Object();
		// test a bad association (objects split across multiple shards)
		try {
			ssi.getShardIdOfRelatedObject( obj );
			fail( "expecte he" );
		}
		catch (HibernateException he) {
			// good
		}
	}

	public void testGetShardIdOfRelatedObjectWithNullCollection() {
		ShardedSessionImpl ssi = new MyShardedSessionImpl() {
			final ShardId[] shardIdForObjectToReturn = {new ShardId( 33 ), new ShardId( 33 )};
			int shardIdForObjectToReturnIndex = 0;

			@Override
			ClassMetadata getClassMetadata(Class<?> clazz) {
				return new ClassMetadataDefaultMock() {
					@Override
					public Type[] getPropertyTypes() {
						return new Type[] {new MyType( false, false ), new MyType( true, true )};
					}

					@Override
					public Object[] getPropertyValues(Object entity) {
						return new Object[] {null, null};
					}
				};
			}

			@Override
			public ShardId getShardIdForObject(Object obj) {
				return shardIdForObjectToReturn[shardIdForObjectToReturnIndex++];
			}
		};

		Object obj = new Object();
		// test a collection
		assertNull( ssi.getShardIdOfRelatedObject( obj ) );
	}

	public void testGetShardIdOfRelatedObjectWithCollection() {
		ShardedSessionImpl ssi = new MyShardedSessionImpl() {
			final ShardId[] shardIdForObjectToReturn = {new ShardId( 33 ), new ShardId( 33 )};
			int shardIdForObjectToReturnIndex = 0;

			@Override
			ClassMetadata getClassMetadata(Class<?> clazz) {
				return new ClassMetadataDefaultMock() {
					@Override
					public Type[] getPropertyTypes() {
						return new Type[] {new MyType( false, false ), new MyType( true, true )};
					}

					@Override
					public Object[] getPropertyValues(Object entity) {
						return new Object[] {null, Collections.singletonList( "yam" )};
					}
				};
			}

			@Override
			public ShardId getShardIdForObject(Object obj) {
				return shardIdForObjectToReturn[shardIdForObjectToReturnIndex++];
			}
		};

		Object obj = new Object();

		// test a collection
		assertEquals( new ShardId( 33 ), ssi.getShardIdOfRelatedObject( obj ) );
	}

	public void testGetShardIdOfRelatedObjectWithBadCollection() {
		final ShardId[] shardIdForObjectToReturn = {new ShardId( 33 ), new ShardId( 34 )};
		ShardedSessionImpl ssi = new MyShardedSessionImpl() {
			int shardIdForObjectToReturnIndex = 0;

			@Override
			ClassMetadata getClassMetadata(Class<?> clazz) {
				return new ClassMetadataDefaultMock() {
					@Override
					public Type[] getPropertyTypes() {
						return new Type[] {new MyType( true, true )};
					}

					@Override
					public Object[] getPropertyValues(Object entity) {
						return new Object[] {Lists.newArrayList( "jam", "yam" )};
					}
				};
			}

			@Override
			public ShardId getShardIdForObject(Object obj) {
				return shardIdForObjectToReturn[shardIdForObjectToReturnIndex++];
			}
		};

		Object obj = new Object();
		// test a bad association (objects split across multiple shards)
		try {
			ssi.getShardIdOfRelatedObject( obj );
			fail( "expecte he" );
		}
		catch (HibernateException he) {
			// good
		}
	}

	public void testGetShardIdOfRelatedObjectWithBadCollections() {
		final ShardId[] shardIdForObjectToReturn = {new ShardId( 33 ), new ShardId( 34 )};
		ShardedSessionImpl ssi = new MyShardedSessionImpl() {
			int shardIdForObjectToReturnIndex = 0;

			@Override
			ClassMetadata getClassMetadata(Class<?> clazz) {
				return new ClassMetadataDefaultMock() {
					@Override
					public Type[] getPropertyTypes() {
						return new Type[] {new MyType( true, true ), new MyType( true, true )};
					}

					@Override
					public Object[] getPropertyValues(Object entity) {
						return new Object[] {Collections.singletonList( "jam" ), Collections.singletonList( "yam" )};
					}
				};
			}

			@Override
			public ShardId getShardIdForObject(Object obj) {
				return shardIdForObjectToReturn[shardIdForObjectToReturnIndex++];
			}
		};

		Object obj = new Object();
		// test a bad association (objects split across multiple shards)
		try {
			ssi.getShardIdOfRelatedObject( obj );
			fail( "expecte he" );
		}
		catch (HibernateException he) {
			// good
		}
	}

	public void testGetShardIdOfRelatedObjectWithAssociationAndCollection() {
		ShardedSessionImpl ssi = new MyShardedSessionImpl() {
			final ShardId[] shardIdForObjectToReturn = {new ShardId( 33 ), new ShardId( 33 )};
			int shardIdForObjectToReturnIndex = 0;

			@Override
			ClassMetadata getClassMetadata(Class<?> clazz) {
				return new ClassMetadataDefaultMock() {
					@Override
					public Type[] getPropertyTypes() {
						return new Type[] {new MyType( true, false ), new MyType( true, true )};
					}

					@Override
					public Object[] getPropertyValues(Object entity) {
						return new Object[] {"jam", Collections.singletonList( "yam" )};
					}
				};
			}

			@Override
			public ShardId getShardIdForObject(Object obj) {
				return shardIdForObjectToReturn[shardIdForObjectToReturnIndex++];
			}
		};

		Object obj = new Object();

		// test a collection
		assertEquals( new ShardId( 33 ), ssi.getShardIdOfRelatedObject( obj ) );
	}

	public void testGetShardIdOfRelatedObjectWithBadAssociationCollection() {
		final ShardId[] shardIdForObjectToReturn = {new ShardId( 33 ), new ShardId( 34 )};
		ShardedSessionImpl ssi = new MyShardedSessionImpl() {
			int shardIdForObjectToReturnIndex = 0;

			@Override
			ClassMetadata getClassMetadata(Class<?> clazz) {
				return new ClassMetadataDefaultMock() {
					@Override
					public Type[] getPropertyTypes() {
						return new Type[] {new MyType( true, false ), new MyType( true, true )};
					}

					@Override
					public Object[] getPropertyValues(Object entity) {
						return new Object[] {"yam", Collections.singletonList( "jam" )};
					}
				};
			}

			@Override
			public ShardId getShardIdForObject(Object obj) {
				return shardIdForObjectToReturn[shardIdForObjectToReturnIndex++];
			}
		};

		Object obj = new Object();
		// test a bad association (objects split across multiple shards)
		try {
			ssi.getShardIdOfRelatedObject( obj );
			fail( "expecte he" );
		}
		catch (HibernateException he) {
			// good
		}
	}

	public void testCheckForConflictingShardId() {
		final ShardId[] shardIdToReturn = new ShardId[1];
		ShardedSessionImpl ssi = new MyShardedSessionImpl() {
			@Override
			public ShardId getShardIdForObject(Object obj) {
				return shardIdToReturn[0];
			}
		};
		Object obj = new Object();
		assertNull( ssi.checkForConflictingShardId( null, Object.class, obj ) );
		ShardId shardId = new ShardId( 0 );
		shardIdToReturn[0] = shardId;
		assertSame( shardId, ssi.checkForConflictingShardId( null, Object.class, obj ) );
		assertSame( shardId, ssi.checkForConflictingShardId( shardId, Object.class, obj ) );
		ShardId anotherShardId = new ShardId( 1 );
		try {
			ssi.checkForConflictingShardId( anotherShardId, Object.class, obj );
			fail( "expected he" );
		}
		catch (HibernateException he) {
			// good
		}
	}

	public void testBuildShardListFromSessionFactoryShardIdMap() {
		Map<SessionFactoryImplementor, Set<ShardId>> sessionFactoryShardIdMap = new HashMap<SessionFactoryImplementor, Set<ShardId>>();
		ShardIdResolver resolver = new ShardIdResolverDefaultMock();

		assertTrue(
				ShardedSessionImpl.buildShardListFromSessionFactoryShardIdMap(
						sessionFactoryShardIdMap,
						false,
						resolver,
						null
				).isEmpty()
		);
		assertTrue(
				ShardedSessionImpl.buildShardListFromSessionFactoryShardIdMap(
						sessionFactoryShardIdMap,
						true,
						resolver,
						null
				).isEmpty()
		);

		Interceptor interceptor = new InterceptorDefaultMock();
		assertTrue(
				ShardedSessionImpl.buildShardListFromSessionFactoryShardIdMap(
						sessionFactoryShardIdMap,
						false,
						resolver,
						interceptor
				).isEmpty()
		);
		assertTrue(
				ShardedSessionImpl.buildShardListFromSessionFactoryShardIdMap(
						sessionFactoryShardIdMap,
						true,
						resolver,
						interceptor
				).isEmpty()
		);
	}

	public void testFinalizeOnOpenSession() throws Throwable {
		final boolean[] closeCalled = {false};
		ShardedSessionImpl ssi = new MyShardedSessionImpl() {
			@Override
			public Connection close() throws HibernateException {
				closeCalled[0] = true;
				return super.close();
			}
		};
		ssi.finalize();
		assertTrue( closeCalled[0] );
	}

	public void testFinalizeOnClosedSession() throws Throwable {
		final boolean[] closeCalled = {false};
		ShardedSessionImpl ssi = new MyShardedSessionImpl() {
			@Override
			public Connection close() throws HibernateException {
				closeCalled[0] = true;
				return super.close();
			}
		};
		ssi.close();
		assertTrue( closeCalled[0] );
		closeCalled[0] = false;
		ssi.finalize();
		assertFalse( closeCalled[0] );
	}

	public void testBuildInterceptorList_NoInterceptorProvided_CrossShardDisabled() {
		Pair<InterceptorList, SetSessionOnRequiresSessionEvent> result =
				ShardedSessionImpl.buildInterceptorList( null, new ShardIdResolverDefaultMock(), false );
		assertNotNull( result.first );
		assertNull( result.second );
		assertEquals( 1, result.first.getInnerList().size() );
		assertTrue( result.first.getInnerList().iterator().next() instanceof ShardAwareInterceptor );
	}

	public void testBuildInterceptorList_NoInterceptorProvided_CrossShardEnabled() {
		Pair<InterceptorList, SetSessionOnRequiresSessionEvent> result =
				ShardedSessionImpl.buildInterceptorList( null, new ShardIdResolverDefaultMock(), true );
		assertNotNull( result.first );
		assertNull( result.second );
		assertEquals( 2, result.first.getInnerList().size() );
		Iterator<Interceptor> innerListIter = result.first.getInnerList().iterator();
		assertTrue( innerListIter.next() instanceof ShardAwareInterceptor );
		assertTrue( innerListIter.next() instanceof CrossShardRelationshipDetectingInterceptor );
	}

	public void testBuildInterceptorList_StatelessInterceptorProvided_CrossShardEnabled() {
		InterceptorDefaultMock interceptor = new InterceptorDefaultMock();
		Pair<InterceptorList, SetSessionOnRequiresSessionEvent> result =
				ShardedSessionImpl.buildInterceptorList( interceptor, new ShardIdResolverDefaultMock(), true );
		assertNotNull( result.first );
		assertNull( result.second );
		assertEquals( 3, result.first.getInnerList().size() );
		Iterator<Interceptor> innerListIter = result.first.getInnerList().iterator();
		assertTrue( innerListIter.next() instanceof ShardAwareInterceptor );
		assertTrue( innerListIter.next() instanceof CrossShardRelationshipDetectingInterceptor );
		assertSame( interceptor, innerListIter.next() );
	}


	private static class Factory extends InterceptorDefaultMock implements StatefulInterceptorFactory {
		private final Interceptor interceptorToReturn;


		public Factory(Interceptor interceptorToReturn) {
			this.interceptorToReturn = interceptorToReturn;
		}

		public Interceptor newInstance() {
			return interceptorToReturn;
		}
	}

	public void testBuildInterceptorList_StatefulInterceptorProvided_CrossShardEnabled() {
		Interceptor interceptorToReturn = new InterceptorDefaultMock();
		Interceptor factory = new Factory( interceptorToReturn );
		Pair<InterceptorList, SetSessionOnRequiresSessionEvent> result =
				ShardedSessionImpl.buildInterceptorList( factory, new ShardIdResolverDefaultMock(), true );
		assertNotNull( result.first );
		assertNull( result.second );
		assertEquals( 3, result.first.getInnerList().size() );
		Iterator<Interceptor> innerListIter = result.first.getInnerList().iterator();
		assertTrue( innerListIter.next() instanceof ShardAwareInterceptor );
		assertTrue( innerListIter.next() instanceof CrossShardRelationshipDetectingInterceptor );
		assertSame( interceptorToReturn, innerListIter.next() );
	}

	public void testBuildInterceptorList_StatefulInterceptorRequiresSessionProvided_CrossShardEnabled() {
		class RequiresSessionInterceptor extends InterceptorDefaultMock implements RequiresSession {
			Session setSessionCalledWith;

			public void setSession(Session session) {
				this.setSessionCalledWith = session;
			}
		}
		Interceptor interceptorToReturn = new RequiresSessionInterceptor();
		Interceptor factory = new Factory( interceptorToReturn );
		Pair<InterceptorList, SetSessionOnRequiresSessionEvent> result =
				ShardedSessionImpl.buildInterceptorList( factory, new ShardIdResolverDefaultMock(), true );
		assertNotNull( result.first );
		assertNotNull( result.second );
		assertEquals( 3, result.first.getInnerList().size() );
		Iterator<Interceptor> innerListIter = result.first.getInnerList().iterator();
		assertTrue( innerListIter.next() instanceof ShardAwareInterceptor );
		assertTrue( innerListIter.next() instanceof CrossShardRelationshipDetectingInterceptor );
		assertSame( interceptorToReturn, innerListIter.next() );
	}

	private static final class MyType extends TypeDefaultMock {
		private final boolean isAssociation;
		private final boolean isCollection;

		public MyType(boolean association, boolean collection) {
			isAssociation = association;
			isCollection = collection;
		}

		@Override
		public boolean isAssociationType() {
			return isAssociation;
		}

		@Override
		public boolean isCollectionType() {
			return isCollection;
		}
	}

	public void testIsOpen() {
		ShardedSessionFactoryImplementor ssf = new ShardedSessionFactoryDefaultMock() {
			@Override
			public Map<SessionFactoryImplementor, Set<ShardId>> getSessionFactoryShardIdMap() {
				return Collections.emptyMap();
			}
		};

		ShardStrategy shardStrategy = new ShardStrategyDefaultMock();
		Set<Class<?>> classesRequiringShardLocks = Sets.newHashSet();
		ShardedSessionImpl ssi =
				new ShardedSessionImpl( ssf, shardStrategy, classesRequiringShardLocks, true );
		assertTrue( ssi.isOpen() );
		ssi.close();
		assertFalse( ssi.isOpen() );
	}

	public void testDisconnectWithNullSessions() {

		ShardedSessionImpl ssi = new MyShardedSessionImpl() {

			public List<Shard> getShards() {
				Shard shard1 = new ShardDefaultMock() {
					public org.hibernate.Session getSession() {
						return new SessionDefaultMock() {
							public Connection disconnect() throws HibernateException {
								return null;
							}
						};
					}
				};
				Shard shard2 = new ShardDefaultMock() {

					public org.hibernate.Session getSession() {
						return null;
					}
				};
				return Lists.newArrayList( shard1, shard2 );
			}
		};
		ssi.disconnect();
	}
}
