package ca.ubc.cs.sandboxer.test.demoapp;

import java.rmi.RemoteException;

/**
 * Task on client side which invokes server-side worker and records performance counters.
 */
public class ClientTask implements Runnable {
    private DemoService service;
    private PerformanceTracker tracker;
    
    public ClientTask(DemoService service, PerformanceTracker tracker) {
        this.service = service;
        this.tracker = tracker;
    }
    
    @Override
    public void run() {
        long startTimeNanos = System.nanoTime();
        try {
            service.doTask(null);
        } catch (RemoteException e) {
            System.out.println("ClientTask caught exception: " + e);
        }
        long endTimeNanos = System.nanoTime();
        tracker.addWorkerCounters(new TaskCounters(endTimeNanos - startTimeNanos));
    }

}
