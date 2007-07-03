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

import org.hibernate.shards.strategy.ShardStrategy;
import org.hibernate.shards.strategy.ShardStrategyFactoryDefaultMock;
import org.hibernate.shards.session.ShardedSessionFactoryImpl;
import org.hibernate.shards.util.Lists;

import junit.framework.TestCase;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.OneToOne;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Table;

import java.util.Collections;
import java.util.List;

/**
 * @author maulik@google.com (Maulik Shah)
 */
public class ShardedConfigurationTest extends TestCase {

  private MyShardStrategyFactory shardStrategyFactory;
  private Configuration config;
  private ShardedConfiguration shardedConfiguration;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    shardStrategyFactory = new MyShardStrategyFactory();
    config = new Configuration();
    for(String prop : ShardedConfiguration.VARIABLE_PROPERTIES) {
      config.setProperty(prop, "33");
    }
    config.setProperty(Environment.DIALECT, HSQLDialect.class.getName());

    shardedConfiguration =
        new ShardedConfiguration(
            config,
            Collections.singletonList(config),
            shardStrategyFactory);
  }

  public void testBuildShardedSessionFactoryPreconditions() throws Exception {
    List<Configuration> configList = Lists.newArrayList(config);
    try {
      new ShardedConfiguration(null, configList, shardStrategyFactory);
      fail("Expected npe");
    } catch (NullPointerException npe) {
      // good
    }

    Configuration config = new Configuration();
    try {
      new ShardedConfiguration(config, null, shardStrategyFactory);
      fail("Expected npe");
    } catch (NullPointerException npe) {
      // good
    }

    configList.clear();
    try {
      new ShardedConfiguration(config, configList, shardStrategyFactory);
      fail("Expected iae");
    } catch (IllegalArgumentException iae) {
      // good
    }
  }

  public void testShardIdRequired() {
    Configuration config = new Configuration();
    try {
      shardedConfiguration.populatePrototypeWithVariableProperties(config);
      fail("expected npe");
    } catch (NullPointerException npe) {
      // good
    }
  }

  public void testCopyPropertyToPrototype() {
    Configuration prototype = new Configuration();
    String copyMe = "copyMe";
    config.setProperty(copyMe, "yamma");
    ShardedConfiguration.copyPropertyToPrototype(prototype, config, copyMe);
    assertEquals(config.getProperty(copyMe), prototype.getProperty(copyMe));
  }

  public void testBuildShardedSessionFactory() {
    ShardedSessionFactoryImpl ssfi = (ShardedSessionFactoryImpl)shardedConfiguration.buildShardedSessionFactory();
    assertNotNull(ssfi);
  }

  public void testRequiresShardLock() {
    Property property = new Property();
    assertFalse(shardedConfiguration.doesNotSupportTopLevelSave(property));
    ManyToOne mto = new ManyToOne(new Table());
    property.setValue(mto);
    assertFalse(shardedConfiguration.doesNotSupportTopLevelSave(property));
    OneToOne oto = new OneToOne(new Table(), new RootClass());
    property.setValue(oto);
    assertTrue(shardedConfiguration.doesNotSupportTopLevelSave(property));
  }

  private class MyShardStrategyFactory extends ShardStrategyFactoryDefaultMock {
    @Override
    public ShardStrategy newShardStrategy(List<ShardId> shardIds) {
      return null;
    }
  }
}
