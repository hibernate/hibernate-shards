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

package org.hibernate.shards.integration;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.shards.engine.ShardedSessionFactoryImplementor;
import org.hibernate.shards.util.JdbcStrategy;
import org.hibernate.shards.util.JdbcUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

/**
 * @author maxr@google.com (Max Ross)
 */
public class DbAccessPermutedIntegrationTest extends BaseShardingIntegrationTestCase {

  public void testAccess() throws SQLException {
    Set<? extends SessionFactory> sfSet = ((ShardedSessionFactoryImplementor)sf).getSessionFactoryShardIdMap().keySet();
    for(SessionFactory sf : sfSet) {
      testShard(sf);
      testShard(sf);
      testShard(sf);
    }
  }

  private void testShard(SessionFactory sf) throws SQLException {
    Session session = sf.openSession();
    try {
      Connection conn = session.connection();
      insertRecord(conn);
      updateRecord(conn);
      selectRecord(conn);
      deleteRecord(conn);
    } finally {
      session.close();
    }
  }

  private void insertRecord(Connection conn) throws SQLException {
    assertEquals(1, JdbcUtil.executeUpdate(conn, "INSERT INTO sample_table(id, str_col) values (0, 'yam')", false));
  }

  private void updateRecord(Connection conn) throws SQLException {
    assertEquals(1, JdbcUtil.executeUpdate(conn, "UPDATE sample_table set str_col = 'max' where id = 0", false));
  }

  private void selectRecord(Connection conn) throws SQLException {
    JdbcStrategy strat = new JdbcStrategy() {
      public void extractData(ResultSet rs) throws SQLException {
        assertEquals(0, rs.getInt("id"));
        assertEquals("max", rs.getString("str_col"));
        assertFalse(rs.next());
      }
    };
    JdbcUtil.executeJdbcQuery(conn, "select id, str_col from sample_table where id = 0", strat, false);
  }

  private void deleteRecord(Connection conn) throws SQLException {
    assertEquals(1, JdbcUtil.executeUpdate(conn, "DELETE from sample_table where id = 0", false));
  }
}
