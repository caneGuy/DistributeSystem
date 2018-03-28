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

package com.distribute.ddiaimpl.datastructure;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.io.RandomAccessFile;

/**
 * Key mapping to address on disk.
 */
public class FileHashTable {

  private transient FileHashEntry[] fileHashEntries;

  public FileHashTable(int initialSize) {
    fileHashEntries = new FileHashEntry[initialSize];
  }

  private void reash() {

  }

  private long hashFunc(String key) {
    HashFunction hf = Hashing.murmur3_128();
    return hf.hashBytes(key.getBytes()).asLong();
  }

  private static class FileHashEntry {
    RandomAccessFile file;
    int start;
    int end;
  }
}
