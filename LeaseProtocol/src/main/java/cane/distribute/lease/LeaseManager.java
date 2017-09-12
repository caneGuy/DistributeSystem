package cane.distribute.lease;

import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

public class LeaseManager {
    /**
     * Mapping from replica name to Lease
     */
    private ConcurrentHashMap<String, Lease> _currentLeases =
            new ConcurrentHashMap<String, Lease>();

    private TreeSet<Lease> _sortedLeases = new TreeSet<Lease>();

    /**
     * Create a new lease for given replica
     * @param replicaName
     * @return
     */
    public Lease newLease(String replicaName) {
        Lease lease = new Lease(replicaName);
        _currentLeases.put(replicaName, lease);
        _sortedLeases.add(lease);
        return lease;
    }

    /**
     * Update lease for given replica
     * @param replicaName
     */
    public void updateLease(String replicaName) {
        if (_currentLeases.contains(replicaName)) {
            Lease lease = _currentLeases.get(replicaName);
            _sortedLeases.remove(lease);
            lease.renew();
            _currentLeases.put(replicaName, lease);
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
