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

package org.hibernate.shards;

import org.hibernate.shards.integration.PermutedIntegrationTests;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;

import java.util.List;
import java.util.ArrayList;

/**
 * @author maxr@google.com (Max Ross)
 */
public class AllFastTests extends TestSuite {

  public static Test suite() {
    TestSuite suite = new TestSuite();
    for(Class<? extends TestCase> testClass : getAllFastTestClasses()) {
      suite.addTestSuite(testClass);
    }
    return suite;
  }

  private static List<Class<? extends TestCase>> getAllFastTestClasses() {
    List<Class<? extends TestCase>> allFastTestClasses = new ArrayList<Class<? extends TestCase>>();
    for(Class<? extends TestCase> testClass : AllTests.CLASSES) {
      allFastTestClasses.add(testClass);
    }
    allFastTestClasses.removeAll(PermutedIntegrationTests.CLASSES);
    return allFastTestClasses;
  }
}
