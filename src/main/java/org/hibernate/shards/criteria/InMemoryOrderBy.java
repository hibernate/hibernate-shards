/**
 * Copyright (C) 2008 Google Inc.
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

import org.hibernate.criterion.Order;

/**
 * Describes an 'order by' that we're going to apply in memory
 *
 * @author maxr@google.com (Max Ross)
 */
public class InMemoryOrderBy {

    // This is the full path to the property we're sorting by.
    // For example, if the criteria is on Building and we're sorting by numFloors
    // the expression is just 'numFloors.'  However, if the criteria is on Building
    // and we're sorting by floor number (Building has 0 to n Floors and each Floor
    // has a 'number' member) then the expression is 'floors.number'
    private final String expression;
    private final boolean isAscending;

    /**
     * Constructs an InMemoryOrderBy instance
     *
     * @param associationPath The association path leading to the object to which
     *                        the provided {@link Order} parameter applies.  Null if the {@link Order}
     *                        parameter applies to the top level object
     * @param order           A standard Hibernate {@link Order} object.
     */
    public InMemoryOrderBy(final String associationPath, final Order order) {
        this.expression = getAssociationPrefix(associationPath) + getSortingProperty(order);
        this.isAscending = isAscending(order);
    }

    private static String getAssociationPrefix(final String associationPath) {
        return associationPath == null ? "" : associationPath + ".";
    }

    private static boolean isAscending(final Order order) {
        return order.toString().toUpperCase().endsWith("ASC");
    }

    public String getExpression() {
        return expression;
    }

    public boolean isAscending() {
        return isAscending;
    }

    private static String getSortingProperty(final Order order) {
        /**
         * This method relies on the format that Order is using:
         * propertyName + ' ' + (ascending?"asc":"desc")
         */
        final String str = order.toString();
        return str.substring(0, str.indexOf(' '));
    }
}
