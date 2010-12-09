package ca.ubc.cs.sandboxer.core;

import java.util.Set;

/**
 * Class enforces sandbox policies by monitoring an instance of 
 * runtime sandbox.
 */
public class SandboxMonitor extends Thread {
    private SandboxPolicy policy;
    private RuntimeSandbox sandbox;
    private long pollIterations = 0;
    
    private final static int POLL_PERIOD_SECONDS = 2;
    private final static int HEAP_POLL_PERIOD_SECONDS = 10;
    private final static int HEAP_POLL_PERIOD_CYCLES = HEAP_POLL_PERIOD_SECONDS / POLL_PERIOD_SECONDS;

    public SandboxMonitor(SandboxPolicy policy, RuntimeSandbox sandbox) {
        this.policy = policy;
        this.sandbox = sandbox;
        
        setName("SandboxMonitor(" + policy.getSandboxName() + ")");
        setDaemon(true);
        //setPriority(NORM_PRIORITY - 1);
    }
    
    public void run() {
        try {
            
            while (true) {
                try {
                    Thread.sleep(1000 * POLL_PERIOD_SECONDS);
                } catch (InterruptedException e) {}
                
                pollIterations++;

                long currentTimeMsecs = System.currentTimeMillis();
                for (RuntimeSandbox.ActiveThreadView threadView: sandbox.getActiveThreads()) {
                    if ((currentTimeMsecs - threadView.getEntryTimeMsecs()) > 
                            policy.getCallTimeoutMsecs()) {
                        
                        // Quarantine the sandbox.
                        String quarantineReason = "long-running call to " +
                                threadView.getClass().getName() + "." + threadView.getEntryMethodName();
                        sandbox.setQuarantined(quarantineReason);
                        
                        // Pop the thread out of the sandbox.
                        threadView.getActiveThread().stop(
                                new QuarantineException(
                                        "Sandbox " + policy.getSandboxName() + " quarantined due to " + quarantineReason));
                    }
                }

                if (pollIterations % HEAP_POLL_PERIOD_CYCLES == 0) {
                    System.out.println("*** Counting allocated objects ...");
                    Set<Object> allocatedObjects = sandbox.refreshAllocatedObjects();
                    System.out.println("*** Current count of allocated objects = " + allocatedObjects.size());
                    long startTimeMsecs = System.currentTimeMillis();
                    long heapUsage = SandboxGuardian.getReferencedSize(allocatedObjects.toArray());
                    long endTimeMsecs = System.currentTimeMillis();
                    System.out.println("Total heap usage = " + heapUsage + " bytes");
                    System.out.println("Calculated heap usage in " + (endTimeMsecs - startTimeMsecs) + " msecs");
                }
            }
        } catch (Exception e) {
            System.out.println("Exception in monitor for sandbox " + policy.getSandboxName() + ": " + e);
        }
    }
}
