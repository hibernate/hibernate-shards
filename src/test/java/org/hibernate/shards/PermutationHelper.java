package org.hibernate.shards;

import java.util.List;

import org.hibernate.shards.integration.IdGenType;
import org.hibernate.shards.integration.Permutation;
import org.hibernate.shards.integration.ShardAccessStrategyType;
import org.hibernate.shards.util.Lists;

public class PermutationHelper {

	private static final int MIN_SHARDS = 1;
	private static final int MAX_SHARDS = 3;

	public static Iterable<Object[]> data() {

		final List<Object[]> parameters = Lists.newArrayList();

		for ( final Permutation permutation : buildPermutationList() ) {
			parameters.add( new Object[] {permutation} );
		}

		return parameters;
	}

	private static List<Permutation> buildPermutationList() {
		final List<Permutation> list = Lists.newArrayList();
		for ( final IdGenType idGenType : IdGenType.values() ) {
			for ( final ShardAccessStrategyType sast : ShardAccessStrategyType.values() ) {
				for ( int i = MIN_SHARDS; i <= MAX_SHARDS; i++ ) {
					list.add( new Permutation( idGenType, sast, i ) );
					if ( idGenType.getSupportsVirtualSharding() ) {
						list.add( new Permutation( idGenType, sast, i, 9, true ) );
						list.add( new Permutation( idGenType, sast, i, i, true ) );
					}
				}
			}
		}
		return list;
	}
}
