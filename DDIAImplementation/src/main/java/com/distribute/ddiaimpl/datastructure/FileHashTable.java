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
 * Keys mapping to address on disk and support read and write through given key.
 * Some design points should take into consideration:
 * 1. how to read and write file on disk with good performance
 * 2. how to improve a single operation of file access
 * 3. how to choose hash algorithm
 * We can use solution like bitcask which use read and write lock for active file.
 * And read performance can use older files to improve.
 */
public class FileHashTable {

  private transient HashMap<Long, FileHashEntry> fileHashEntries;

  public FileHashTable(int initialSize) {
    fileHashEntries = new HashMap<>(initialSize);
  }

  public ByteString[] getContent(String key) {
    long entryKey = hashFunc(key);
    try {
      return readThroughRandom(fileHashEntries.get(entryKey));
    } catch (Exception e) {
      return null;
    }
  }

  private long hashFunc(String key) {
    HashFunction hf = Hashing.murmur3_128();
    return hf.hashBytes(key.getBytes()).asLong();
  }

  private ByteString[] readThroughRandom(FileHashEntry hashEntry)
          throws Exception {
    // MappedByteBuffer + RandomAccessFile
    try {
      FileChannel fci = hashEntry.file.getChannel();
      MappedByteBuffer mbbi = fci.map(FileChannel.MapMode.READ_ONLY,
              hashEntry.position, hashEntry.size);
      for (int i = hashEntry.position; i < hashEntry.size; i++) {
        byte b = mbbi.get(i);
      }
    } catch (Exception e) {
      throw e;
    }
  }

  private static class FileHashEntry {
    RandomAccessFile file;
    int size;
    int position;
  }
}
