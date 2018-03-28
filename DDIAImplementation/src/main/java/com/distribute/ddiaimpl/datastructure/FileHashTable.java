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
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;

/**
 * Key mapping to address on disk.
 */
public class FileHashTable {

  private transient HashMap<String, FileHashEntry> fileHashEntries;

  public FileHashTable(int initialSize) {
    fileHashEntries = new HashMap<>(initialSize);
  }

  private long hashFunc(String key) {
    HashFunction hf = Hashing.murmur3_128();
    return hf.hashBytes(key.getBytes()).asLong();
  }

  private void readThroughRandom(FileHashEntry hashEntry) {
    // MappedByteBuffer + RandomAccessFile
    try {
      FileChannel fci = hashEntry.file.getChannel();
      long size = fci.size();
      MappedByteBuffer mbbi = fci.map(FileChannel.MapMode.READ_ONLY, 0, size);
      long start = System.currentTimeMillis();
      for (int i = 0; i < size; i++) {
        byte b = mbbi.get(i);
      }
    } catch (Exception e) {
      // TODO
    }
  }

  private static class FileHashEntry {
    RandomAccessFile file;
    int start;
    int end;
  }
}
