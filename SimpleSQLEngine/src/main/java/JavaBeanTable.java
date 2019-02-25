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


import org.apache.calcite.adapter.enumerable.EnumerableConvention;
import org.apache.calcite.adapter.java.AbstractQueryableTable;
import org.apache.calcite.linq4j.Enumerator;
import org.apache.calcite.linq4j.QueryProvider;
import org.apache.calcite.linq4j.Queryable;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.TableScan;
import org.apache.calcite.rel.metadata.RelMetadataQuery;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.TranslatableTable;
import org.apache.calcite.schema.impl.AbstractTableQueryable;
import org.apache.calcite.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * JavaBeanTable is an Calcite table that accepts a List of JavaBeans. JavaBeans
 * can have only fields of eligible type / class defined in
 * JavaBeanInspector.checkMethodEligibility.
 *
 * @author cane
 *
 * @param <E>
 * Table contains items for a specific Class E
 */
public class JavaBeanTable<E> extends AbstractQueryableTable implements
        TranslatableTable {

  static final Logger logger = LoggerFactory.getLogger(JavaBeanTable.class);
  private List<E> javaBeanList;

  /**
   * Constructor
   *
   * @param javaBeanList
   * A JavaBean List
   */
  public JavaBeanTable(List<E> javaBeanList) {
    super(Object[].class);
    this.javaBeanList = javaBeanList;
  }

  @Override
  public RelDataType getRowType(RelDataTypeFactory typeFactory) {
    List<String> names = new ArrayList<>();
    List<RelDataType> types = new ArrayList<>();
    if ((javaBeanList != null) && (javaBeanList.size() > 0)) {
      Class sample = javaBeanList.get(0).getClass();
      Method[] methods = sample.getMethods();
      for (Method method : methods) {
        if (JavaBeanInspector.checkMethodEligiblity(method)) {
          String name = method.getName().substring(3);
          Class type = method.getReturnType();
          names.add(name);
          types.add(typeFactory.createJavaType(type));
          logger.info("Added field name: " + name + " of type: "
                  + type.getSimpleName());
        }
      }
    }
    return typeFactory.createStructType(Pair.zip(names, types));
  }

  @Override
  public <T> Queryable<T> asQueryable(QueryProvider queryProvider,
                                      SchemaPlus schema, String tableName) {
    logger.info("Got query request for: " + tableName);
    return new AbstractTableQueryable<T>(queryProvider, schema, this, tableName) {
      public Enumerator<T> enumerator() {
        // noinspection unchecked
        try {
          JavaBeanEnumerator enumerator = new JavaBeanEnumerator(javaBeanList);
          return (Enumerator<T>) enumerator;
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    };
  }

  @Override
  public RelNode toRel(RelOptTable.ToRelContext context, RelOptTable relOptTable) {
    return new TableScan(context.getCluster(), context
            .getCluster().traitSetOf(EnumerableConvention.INSTANCE), relOptTable) {
      @Override
      public double estimateRowCount(RelMetadataQuery mq) {
        return super.estimateRowCount(mq);
      }
    };
  }

}
