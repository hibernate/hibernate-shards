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

package org.hibernate.shards.strategy.exit;

import org.hibernate.criterion.Projection;
import org.hibernate.shards.util.Lists;
import org.hibernate.shards.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Maulik Shah
 */
public class ShardedAvgExitOperation implements ProjectionExitOperation {

    private final Logger log = LoggerFactory.getLogger(getClass());

    public ShardedAvgExitOperation(final Projection projection) {
        log.error("not ready to use!");
        throw new UnsupportedOperationException();
    }

    public List<Object> apply(final List<Object> results) {
        BigDecimal value = BigDecimal.ZERO;
        BigDecimal count = BigDecimal.ZERO;
        @SuppressWarnings("unchecked")
        List<Pair<Double, Integer>> pairList = (List<Pair<Double, Integer>>) (List) results;
        for (Pair<Double, Integer> pair : pairList) {
            // we know the order of the pair (avg, count) by convention of ShardedAvgProjection
            value = value.add(new BigDecimal(pair.first));
            count = count.add(new BigDecimal(pair.second));
        }
        return Lists.newArrayList((Object) value.divide(count));
    }
}
