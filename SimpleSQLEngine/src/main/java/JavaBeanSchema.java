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

import com.google.common.collect.ImmutableMap;
import org.apache.calcite.schema.Table;
import org.apache.calcite.schema.impl.AbstractSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JavaBeanSchema is a type of Calcite Schema that contains a list of tables. A
 * table is a List of JavaBean Objects of the same type.
 *
 * @author cane
 */
public class JavaBeanSchema extends AbstractSchema {

  static final Logger logger = LoggerFactory.getLogger(JavaBeanSchema.class);
  private String schemaName;
  private Map<String, List> javaBeanListMap = new HashMap<>();

  /**
   * Constructor
   *
   * @param schemaName
   * The schema name which is like database name.
   */
  public JavaBeanSchema(String schemaName) {
    super();
    this.schemaName = schemaName;
  }

  /**
   * Adds a table to the schema.
   *
   * @param tableName
   * The name of the table, has to be unique else will overwrite.
   * @param javaBeanList
   * A List of JavaBeans of same type that's to be seen as table.
   */
  public <E> void addAsTable(String tableName, List<E> javaBeanList) {
    javaBeanListMap.put(tableName, javaBeanList);
    logger.info("Added table: " + tableName + " to Schema: " + schemaName);
  }

  /**
   * @return The name of the schema
   */
  public String getName() {
    return schemaName;
  }

  @Override
  protected Map<String, Table> getTableMap() {
    final ImmutableMap.Builder<String, Table> builder = ImmutableMap.builder();
    for (String tableName : javaBeanListMap.keySet()) {
      Table javaBeanTable = new JavaBeanTable(javaBeanListMap.get(tableName));
      builder.put(tableName, javaBeanTable);
      logger.debug("Initialized JavaBeanTable for: " + tableName);
    }
    return builder.build();
  }

}
