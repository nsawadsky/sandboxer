package ca.ubc.cs.sandboxer.core;

/**
 * Class enforces sandbox policies by monitoring an instance of 
 * runtime sandbox.
 */
public class SandboxMonitor extends Thread {
    private SandboxPolicy policy;
    private RuntimeSandbox sandbox;
    
    public SandboxMonitor(SandboxPolicy policy, RuntimeSandbox sandbox) {
        this.policy = policy;
        this.sandbox = sandbox;
        
        setName("SandboxMonitor(" + policy.getSandboxName() + ")");
        setDaemon(true);
        setPriority(NORM_PRIORITY - 1);
    }
    
    public void run() {
        try {
            final int POLL_PERIOD_SECONDS = 2;
            
            while (true) {
                try {
                    Thread.sleep(1000 * POLL_PERIOD_SECONDS);
                } catch (InterruptedException e) {}
                
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
            }
        } catch (Exception e) {
            System.out.println("Exception in monitor for sandbox " + policy.getSandboxName() + ": " + e);
        }
    }
}
