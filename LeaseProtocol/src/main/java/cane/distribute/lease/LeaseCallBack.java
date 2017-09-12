package cane.distribute.lease;

public interface LeaseCallBack {
    /**
     * Should be implemented by sub-class
     * which is actual operation when Lease is
     * still valid.
     */
    public void call();
}
