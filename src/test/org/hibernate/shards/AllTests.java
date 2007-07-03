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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.hibernate.shards.criteria.AddCriterionEventTest;
import org.hibernate.shards.criteria.AddOrderEventTest;
import org.hibernate.shards.criteria.CreateAliasEventTest;
import org.hibernate.shards.criteria.CreateSubcriteriaEventTest;
import org.hibernate.shards.criteria.CriteriaFactoryImplTest;
import org.hibernate.shards.criteria.SetFetchModeEventTest;
import org.hibernate.shards.criteria.SetProjectionEventTest;
import org.hibernate.shards.criteria.ShardedSubcriteriaImplTest;
import org.hibernate.shards.criteria.SubcriteriaFactoryImplTest;
import org.hibernate.shards.id.ShardedTableHiLoGeneratorTest;
import org.hibernate.shards.id.ShardedUUIDGeneratorTest;
import org.hibernate.shards.integration.PermutedIntegrationTests;
import org.hibernate.shards.integration.model.MemoryLeakTest;
import org.hibernate.shards.loadbalance.RoundRobinShardLoadBalancerTest;
import org.hibernate.shards.query.SetBigDecimalEventTest;
import org.hibernate.shards.query.SetBigIntegerEventTest;
import org.hibernate.shards.query.SetBinaryEventTest;
import org.hibernate.shards.query.SetBooleanEventTest;
import org.hibernate.shards.query.SetByteEventTest;
import org.hibernate.shards.query.SetCalendarDateEventTest;
import org.hibernate.shards.query.SetCalendarEventTest;
import org.hibernate.shards.query.SetCharacterEventTest;
import org.hibernate.shards.query.SetDateEventTest;
import org.hibernate.shards.query.SetDoubleEventTest;
import org.hibernate.shards.query.SetEntityEventTest;
import org.hibernate.shards.query.SetFloatEventTest;
import org.hibernate.shards.query.SetIntegerEventTest;
import org.hibernate.shards.query.SetLocaleEventTest;
import org.hibernate.shards.query.SetLongEventTest;
import org.hibernate.shards.query.SetParameterEventTest;
import org.hibernate.shards.query.SetParameterListEventTest;
import org.hibernate.shards.query.SetParametersEventTest;
import org.hibernate.shards.query.SetPropertiesEventTest;
import org.hibernate.shards.query.SetReadOnlyEventTest;
import org.hibernate.shards.query.SetSerializableEventTest;
import org.hibernate.shards.query.SetShortEventTest;
import org.hibernate.shards.query.SetStringEventTest;
import org.hibernate.shards.query.SetTextEventTest;
import org.hibernate.shards.query.SetTimeEventTest;
import org.hibernate.shards.query.SetTimestampEventTest;
import org.hibernate.shards.session.CrossShardRelationshipDetectingInterceptorDecoratorTest;
import org.hibernate.shards.session.CrossShardRelationshipDetectingInterceptorTest;
import org.hibernate.shards.session.DisableFilterOpenSessionEventTest;
import org.hibernate.shards.session.EnableFilterOpenSessionEventTest;
import org.hibernate.shards.session.SetCacheModeOpenSessionEventTest;
import org.hibernate.shards.session.SetFlushModeOpenSessionEventTest;
import org.hibernate.shards.session.SetReadOnlyOpenSessionEventTest;
import org.hibernate.shards.session.SetSessionOnRequiresSessionEventTest;
import org.hibernate.shards.session.ShardedSessionFactoryImplTest;
import org.hibernate.shards.session.ShardedSessionImplTest;
import org.hibernate.shards.strategy.access.ParallelShardAccessStrategyTest;
import org.hibernate.shards.strategy.access.ParallelShardOperationCallableTest;
import org.hibernate.shards.strategy.access.StartAwareFutureTaskTest;
import org.hibernate.shards.strategy.exit.AggregateExitOperationTest;
import org.hibernate.shards.strategy.exit.ExitOperationUtilsTest;
import org.hibernate.shards.strategy.exit.FirstResultExitOperationTest;
import org.hibernate.shards.strategy.exit.MaxResultExitOperationTest;
import org.hibernate.shards.strategy.exit.OrderExitOperationTest;
import org.hibernate.shards.strategy.exit.ProjectionExitOperationFactoryTest;
import org.hibernate.shards.strategy.exit.RowCountExitOperationTest;
import org.hibernate.shards.strategy.selection.LoadBalancedShardSelectionStrategyTest;
import org.hibernate.shards.transaction.ShardedTransactionImplTest;
import org.hibernate.shards.util.Lists;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class is really just here for IDE users who want to run their tests
 * directly instead of through Ant.  Almost guaranteed to fall out of
 * synch.
 * TODO(maxr) destroy
 *
 * @author Max Ross <maxr@google.com>
 */
public class AllTests extends TestSuite {

  public static final List<Class<? extends TestCase>> CLASSES = Collections.unmodifiableList(buildListOfClasses());

  private static List<Class<? extends TestCase>> buildListOfClasses() {
    Set<Class<? extends TestCase>> classes = new HashSet<Class<? extends TestCase>>();
    add(classes, BaseHasShardIdListTest.class);
    add(classes, ShardImplTest.class);
    add(classes, SetBigDecimalEventTest.class);
    add(classes, SetBigIntegerEventTest.class);
    add(classes, SetBinaryEventTest.class);
    add(classes, SetBooleanEventTest.class);
    add(classes, SetByteEventTest.class);
    add(classes, org.hibernate.shards.query.SetCacheModeEventTest.class);
    add(classes, org.hibernate.shards.query.SetCacheRegionEventTest.class);
    add(classes, org.hibernate.shards.query.SetCacheableEventTest.class);
    add(classes, SetCalendarDateEventTest.class);
    add(classes, SetCalendarEventTest.class);
    add(classes, SetCharacterEventTest.class);
    add(classes, SetSessionOnRequiresSessionEventTest.class);
    add(classes, org.hibernate.shards.query.SetCommentEventTest.class);
    add(classes, SetDateEventTest.class);
    add(classes, SetDoubleEventTest.class);
    add(classes, SetEntityEventTest.class);
    add(classes, org.hibernate.shards.query.SetFetchSizeEventTest.class);
    add(classes, org.hibernate.shards.query.SetFirstResultEventTest.class);
    add(classes, SetFloatEventTest.class);
    add(classes, org.hibernate.shards.query.SetFlushModeEventTest.class);
    add(classes, SetIntegerEventTest.class);
    add(classes, SetLocaleEventTest.class);
    add(classes, org.hibernate.shards.query.SetLockModeEventTest.class);
    add(classes, SetLongEventTest.class);
    add(classes, org.hibernate.shards.query.SetMaxResultsEventTest.class);
    add(classes, SetParameterEventTest.class);
    add(classes, SetParameterListEventTest.class);
    add(classes, SetParametersEventTest.class);
    add(classes, SetPropertiesEventTest.class);
    add(classes, SetReadOnlyEventTest.class);
    add(classes, org.hibernate.shards.query.SetResultTransformerEventTest.class);
    add(classes, SetSerializableEventTest.class);
    add(classes, SetShortEventTest.class);
    add(classes, SetStringEventTest.class);
    add(classes, SetTextEventTest.class);
    add(classes, SetTimeEventTest.class);
    add(classes, org.hibernate.shards.query.SetTimeoutEventTest.class);
    add(classes, SetTimestampEventTest.class);
    add(classes, ShardedConfigurationTest.class);
    add(classes, ShardedSessionFactoryImplTest.class);
    add(classes, AddCriterionEventTest.class);
    add(classes, AddOrderEventTest.class);
    add(classes, CreateAliasEventTest.class);
    add(classes, CriteriaFactoryImplTest.class);
    add(classes, org.hibernate.shards.criteria.SetCacheModeEventTest.class);
    add(classes, org.hibernate.shards.criteria.SetCacheRegionEventTest.class);
    add(classes, org.hibernate.shards.criteria.SetCacheableEventTest.class);
    add(classes, org.hibernate.shards.criteria.SetCommentEventTest.class);
    add(classes, SetFetchModeEventTest.class);
    add(classes, org.hibernate.shards.criteria.SetFetchSizeEventTest.class);
    add(classes, org.hibernate.shards.criteria.SetFirstResultEventTest.class);
    add(classes, org.hibernate.shards.criteria.SetFlushModeEventTest.class);
    add(classes, org.hibernate.shards.criteria.SetLockModeEventTest.class);
    add(classes, org.hibernate.shards.criteria.SetMaxResultsEventTest.class);
    add(classes, SetProjectionEventTest.class);
    add(classes, org.hibernate.shards.criteria.SetResultTransformerEventTest.class);
    add(classes, org.hibernate.shards.criteria.SetTimeoutEventTest.class);
    add(classes, CreateSubcriteriaEventTest.class);
    add(classes, SubcriteriaFactoryImplTest.class);
    add(classes, ShardedSubcriteriaImplTest.class);
    add(classes, ShardedUUIDGeneratorTest.class);
    add(classes, ShardedTableHiLoGeneratorTest.class);
    add(classes, MemoryLeakTest.class);
    add(classes, RoundRobinShardLoadBalancerTest.class);
    add(classes, DisableFilterOpenSessionEventTest.class);
    add(classes, EnableFilterOpenSessionEventTest.class);
    add(classes, SetCacheModeOpenSessionEventTest.class);
    add(classes, SetFlushModeOpenSessionEventTest.class);
    add(classes, SetReadOnlyOpenSessionEventTest.class);
    add(classes, ShardedSessionImplTest.class);
    add(classes, CrossShardRelationshipDetectingInterceptorDecoratorTest.class);
    add(classes, CrossShardRelationshipDetectingInterceptorTest.class);
    add(classes, ParallelShardAccessStrategyTest.class);
    add(classes, StartAwareFutureTaskTest.class);
    add(classes, ParallelShardOperationCallableTest.class);
    add(classes, AggregateExitOperationTest.class);
    add(classes, FirstResultExitOperationTest.class);
    add(classes, MaxResultExitOperationTest.class);
    add(classes, ProjectionExitOperationFactoryTest.class);
    add(classes, RowCountExitOperationTest.class);
    add(classes, ExitOperationUtilsTest.class);
    add(classes, OrderExitOperationTest.class);
    add(classes, LoadBalancedShardSelectionStrategyTest.class);
    add(classes, ShardedTransactionImplTest.class);
    add(classes, InstanceShardStrategyImplTest.class);

    return Lists.newArrayList(classes);
  }

  private static void add(Set<Class<? extends TestCase>> classes,
      Class<? extends TestCase> aClass) {
    if(!classes.add(aClass)) {
      throw new RuntimeException("Class " + aClass.getName() + " is listed more than once.");
    }
  }

  public static Test suite() {
    TestSuite suite = new TestSuite();
    for(Class<? extends TestCase> testClass : CLASSES) {
      suite.addTestSuite(testClass);
    }
    suite.addTest(PermutedIntegrationTests.suite());
    return suite;
  }
}
