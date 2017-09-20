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

import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

public class LeaseManager {
    protected long leaseInterval;

    /**
     * Mapping from replica name to Lease
     */
    private ConcurrentHashMap<String, Lease> _currentLeases =
            new ConcurrentHashMap<String, Lease>();

    private TreeSet<Lease> _sortedLeases = new TreeSet<Lease>();

    LeaseManager(long interval) {
        leaseInterval = interval;
    }

    /**
     * Create a new lease for given replica
     * @param clientName
     * @return
     */
    public Lease newLease(String clientName) {
        Lease lease = new Lease(clientName);
        _currentLeases.put(clientName, lease);
        _sortedLeases.add(lease);
        return lease;
    }

    /**
     * Update lease for given replica
     * @param clientName
     */
    public void updateLease(String clientName) {
        if (_currentLeases.contains(clientName)) {
            Lease lease = _currentLeases.get(clientName);
            _sortedLeases.remove(lease);
            lease.renew();
            _currentLeases.put(clientName, lease);
            _sortedLeases.add(lease);
        }
        //TODO: NULL throw error exception
    }

    /**
     * Remove a lease which has expired
     * @param replicaName
     */
    public void removeLease(String replicaName) {
        _sortedLeases.remove(_currentLeases.get(replicaName));
        _currentLeases.remove(replicaName);
    }

    /**
     * De
     */
    class LeaseMonitor implements Runnable {
        public void run() {

        }
    }

    private void checkLeases() {

    }
}
