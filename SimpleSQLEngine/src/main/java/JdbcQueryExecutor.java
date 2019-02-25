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

import org.apache.calcite.jdbc.CalciteConnection;
import org.apache.calcite.schema.SchemaPlus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * This class executes a sql query over an Calcite Schema
 *
 * @author cane
 *
 */
public class JdbcQueryExecutor {

  final Logger logger = LoggerFactory.getLogger(JdbcQueryExecutor.class);
  private Connection connection;
  private Statement statement;

  /**
   * Constructor to instantiate a JdbcQueryExecutor
   *
   * @param schema
   *          The schema to execute queries.
   */
  public JdbcQueryExecutor(JavaBeanSchema schema) {
    try {
      Class.forName("org.apache.calcite.jdbc.Driver");
      connection = DriverManager.getConnection("jdbc:calcite:");
      CalciteConnection calciteConnection = connection
              .unwrap(CalciteConnection.class);
      SchemaPlus rootSchema = calciteConnection.getRootSchema();
      rootSchema.add(schema.getName(), schema);
      logger.info("Created connection to schema: " + schema.getName());
    } catch (Exception e) {
      logger.error("Could not create Calcite Connection");
    }
  }

  /**
   * Executes a SQL query.
   *
   * @param sql
   *          SQL query in string.
   * @return JDBC result set.
   */
  public ResultSet execute(String sql) {
    ResultSet results = null;
    try {
      logger.debug("Creating a statement");
      statement = connection.createStatement();
      logger.debug("Going to execute query: " + sql);
      results = statement.executeQuery(sql);
      logger.debug("Execution complete");
    } catch (SQLException e) {
      logger.error("Could not create a statement.  " + e);
    }
    return results;
  }

  /**
   * Closed the connection and statement used for executing query.
   */
  public void close() {
    if (connection != null) {
      try {
        connection.close();
      } catch (SQLException e) {
        logger.error("Could not close Calcite connection");
      }
      if (statement != null) {
        try {
          statement.close();
        } catch (SQLException e) {
          logger.error("Could not close Calcite statement");
        }
      }
    }
  }

}
