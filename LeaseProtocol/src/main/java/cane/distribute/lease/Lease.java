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

package cane.distribute.lease;

import java.io.Serializable;

/**
 * Protocol between LeaseManager and LeaseClient
 *
 */
public class Lease implements Comparable,Serializable {
    /**
     * The latest update time of the Lease
     */
    private Long _lastUpdateTime;

    /**
     * Lease holder which can indicate
     */
    private String _holder;

    public Lease(String holder) {
        this._holder = holder;
        renew();
    }

    public Long getLastUpdateTime() {
        return _lastUpdateTime;
    }

    public String getHolder() {
        return _holder;
    }

    /**
     * Update the timestamp of _lastUpdateTime
     */
    public void renew() {
        this._lastUpdateTime = System.currentTimeMillis();
    }

    public int compareTo(Object o) {
        return this._lastUpdateTime > ((Lease)o).getLastUpdateTime() ? 1 : 0;
    }
}
