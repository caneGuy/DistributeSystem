package cane.distribute.lease;

/**
 * Protocol between LeaseManager and LeaseClient
 */
public class Lease implements Comparable {
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
