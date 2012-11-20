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

package org.hibernate.shards.criteria;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.shards.session.ShardedSessionException;

/**
 * Event that allows an alias to be lazily added to a Criteria.
 *
 * @author maxr@google.com (Max Ross)
 * @see Criteria#createAlias(String, String)
 * @see Criteria#createAlias(String, String, int)
 * @see Criteria#createAlias(String, String, int)
 */
class CreateAliasEvent implements CriteriaEvent {

    private enum MethodSig {
        ASSOC_PATH_AND_ALIAS,
        ASSOC_PATH_AND_ALIAS_AND_JOIN_TYPE,
        ASSOC_PATH_AND_ALIAS_AND_JOIN_TYPE_AND_CRITERION,
    }

    // the signature of the createAlias method we're going to invoke when
    // the event fires
    private final MethodSig methodSig;

    // the association path
    private final String associationPath;

    // the name of the alias we're creating
    private final String alias;

    // the join type - we look at method sig to see if we should use it
    private final int joinType;

    // the criterion
    private final Criterion criterion;

    /**
     * Construct a CreateAliasEvent
     *
     * @param associationPath the association path of the alias we're creating.
     * @param alias           the name of the alias we're creating.
     */
    public CreateAliasEvent(final String associationPath, final String alias) {
        this(MethodSig.ASSOC_PATH_AND_ALIAS, associationPath, alias, 0, null);
    }

    /**
     * Construct a CreateAliasEvent
     *
     * @param associationPath the association path of the alias we're creating.
     * @param alias           the name of the alias we're creating.
     * @param joinType        the join type of the alias we're creating.
     */
    public CreateAliasEvent(final String associationPath, final String alias, final int joinType) {
        this(MethodSig.ASSOC_PATH_AND_ALIAS_AND_JOIN_TYPE, associationPath, alias, joinType, null);
    }

    /**
     * Construct a CreateAliasEvent
     *
     * @param associationPath the association path of the alias we're creating.
     * @param alias           the name of the alias we're creating.
     * @param joinType        the join type of the alias we're creating.
     */
    public CreateAliasEvent(final String associationPath, final String alias, final int joinType, final Criterion criterion) {
        this(MethodSig.ASSOC_PATH_AND_ALIAS_AND_JOIN_TYPE_AND_CRITERION, associationPath, alias, joinType, criterion);
    }

    /**
     * Construct a CreateAliasEvent
     *
     * @param methodSig       the signature of the createAlias method we're going to invoke
     *                        when the event fires
     * @param associationPath the association path of the alias we're creating.
     * @param alias           the name of the alias we're creating.
     * @param joinType        the join type of the alias we're creating.  Can be null.
     * @param criterion       the criterion.  Can be null.
     */
    private CreateAliasEvent(final MethodSig methodSig, final String associationPath, final String alias,
                             final int joinType, final Criterion criterion) {

        this.methodSig = methodSig;
        this.associationPath = associationPath;
        this.alias = alias;
        this.joinType = joinType;
        this.criterion = criterion;
    }

    @Override
    public void onEvent(final Criteria criteria) {

        switch (methodSig) {
            case ASSOC_PATH_AND_ALIAS:
                criteria.createAlias(associationPath, alias);
                break;
            case ASSOC_PATH_AND_ALIAS_AND_JOIN_TYPE:
                criteria.createAlias(associationPath, alias, joinType);
                break;
            case ASSOC_PATH_AND_ALIAS_AND_JOIN_TYPE_AND_CRITERION:
                criteria.createAlias(associationPath, alias, joinType, criterion);
                break;
            default:
                throw new ShardedSessionException("Unknown ctor type in CreateAliasEvent: " + methodSig);
        }
    }
}
