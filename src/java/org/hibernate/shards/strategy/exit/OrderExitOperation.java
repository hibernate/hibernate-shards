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

import org.hibernate.shards.criteria.InMemoryOrderBy;
import org.hibernate.shards.util.Lists;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Maulik Shah
 */
public class OrderExitOperation implements ExitOperation {

  private final List<InMemoryOrderBy> orderByList;

  private static final Comparator<Object> EQUALS = new Comparator<Object>() {
    public int compare(Object o, Object o1) {
      return 0;
    }
  };

  public OrderExitOperation(List<InMemoryOrderBy> orderByList) {
    this.orderByList = Lists.newArrayList(orderByList);
    // need to reverse the list so we build the comparator from the inside out
    Collections.reverse(this.orderByList);
  }

  public List<Object> apply(List<Object> results) {
    List<Object> nonNullList = ExitOperationUtils.getNonNullList(results);

    Comparator<Object> comparator = buildComparator();
    Collections.sort(nonNullList, comparator);

    return nonNullList;
  }

  private Comparator<Object> buildComparator() {
    // the most-inner comparator is one that returns 0 for everything.
    Comparator<Object> inner = EQUALS;
    for(InMemoryOrderBy order : orderByList) {
      inner = new PropertyComparator(order.getExpression(), inner);
      if(!order.isAscending()) {
        inner = Collections.reverseOrder(inner);
      }
    }
    return inner;
  }

  private static final class PropertyComparator implements Comparator<Object> {

    private final String propertyName;
    private final Comparator<Object> tieBreaker;

    public PropertyComparator(String propertyName, Comparator<Object> tieBreaker) {
      this.propertyName = propertyName;
      this.tieBreaker = tieBreaker;
    }

    public int compare(Object o1, Object o2) {
      int result;
      if (o1 == o2) {
        result = 0;
      } else {
        Comparable<Object> o1Value = ExitOperationUtils.getPropertyValue(o1, propertyName);
        Comparable<Object> o2Value = ExitOperationUtils.getPropertyValue(o2, propertyName);
        if (o1Value == null) {
          result = -1;
        } else {
          result = o1Value.compareTo(o2Value);
        }
      }
      if(result == 0) {
        result = tieBreaker.compare(o1, o2);
      }
      return result;
    }
  }
}
