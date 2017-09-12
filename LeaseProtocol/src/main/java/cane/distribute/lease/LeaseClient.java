package cane.distribute.lease;

public class LeaseClient {

    private Lease _holdLease;

    public void requestLease(String host, int port) {

    }

    public void doCall(LeaseCallBack callBack) {
        //TODO: check lease is expired or not
        callBack.call();
    }

    private void renewLease(String host, int port) {

    }

    class LeaseUpdater implements Runnable {
        public void run() {

        }
    }
}
