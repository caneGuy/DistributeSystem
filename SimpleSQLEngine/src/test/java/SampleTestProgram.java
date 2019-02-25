/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import static org.junit.Assert.*;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Test class for the Calcite Adaptor
 *
 * @author cane
 *
 */
public class SampleTestProgram {

  final Logger logger = LoggerFactory.getLogger(SampleTestProgram.class);

  /**
   * Tests the simple functionality on a simple table.
   */
  @Test
  public void testSimpleQuery() {

    // Create test data
    User user1 = new User("Abishek", 29, "India");
    User user2 = new User("Kousik", 25, "Thailand");
    User user3 = new User("CP", 15, "Russia");
    User user4 = new User("Karthik", 29, "US");
    List<User> userList = new ArrayList<>();
    userList.add(user1);
    userList.add(user2);
    userList.add(user3);
    userList.add(user4);

    // Create a test Schema
    JavaBeanSchema schema = new JavaBeanSchema("TESTDB");
    schema.addAsTable("USERS", userList);

    // Execute a Query on schema
    JdbcQueryExecutor queryExec = new JdbcQueryExecutor(schema);
    String sql = "select \"Age\" from \"TESTDB\".\"USERS\" where \"Name\"='Abishek'";
    ResultSet result = queryExec.execute(sql);

    // Verify results
    if (result != null) {
      int output = -1;
      try {
        result.next();
        output = result.getInt("Age");
      } catch (SQLException e) {
        fail("Failed while iterating resultset");
      }
      assertEquals(output, 29);
    } else {
      fail("Null resultset");
    }
    queryExec.close();

  }
}
