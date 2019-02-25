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

import org.apache.calcite.linq4j.Enumerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * JavaBeanEnumerator converts a JavaBean List into rows. A Row is an Object
 * array of columns. The iterator over every row is called rows.
 *
 * @author cane
 *
 */
public class JavaBeanEnumerator implements Enumerator<Object> {

  static final Logger logger = LoggerFactory
          .getLogger(JavaBeanEnumerator.class);
  private Object current;
  private Iterator<Object[]> rowIterator;

  /**
   * Constructor - forms the row iterator.
   *
   * @param javaBeanList
   */
  public <E> JavaBeanEnumerator(List<E> javaBeanList) {
    List<Object[]> rows = new ArrayList<>();
    for (Object javaBean : javaBeanList) {
      rows.add(getRow(javaBean));
    }
    rowIterator = rows.iterator();
    logger.debug("Created an iterator for the enumerator");
  }

  private Object[] getRow(Object javaBean) {
    List<Object> row = new ArrayList<>();
    Class clazz = javaBean.getClass();
    Method[] methods = clazz.getMethods();
    for (Method method : methods) {
      if (JavaBeanInspector.checkMethodEligiblity(method)) {
        try {
          row.add(method.invoke(javaBean));
        } catch (IllegalAccessException e) {
          logger.error("Unable to invoke method via reflection");
        } catch (IllegalArgumentException e) {
          logger.error("Unable to invoke method via reflection");
        } catch (InvocationTargetException e) {
          logger.error("Unable to invoke method via reflection");
        }
      }
    }
    logger.debug("Formed row is: " + row);
    return row.toArray();
  }

  @Override
  public void close() {
    // Nothing to do
  }

  @Override
  public Object current() {
    if (current == null) {
      this.moveNext();
    }
    return current;
  }

  @Override
  public boolean moveNext() {
    if (this.rowIterator.hasNext()) {
      final Object[] row = this.rowIterator.next();
      current = row;
      return true;
    } else {
      current = null;
      return false;
    }
  }

  @Override
  public void reset() {
    throw new UnsupportedOperationException();
  }

}