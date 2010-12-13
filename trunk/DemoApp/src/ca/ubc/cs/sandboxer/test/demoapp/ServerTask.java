package ca.ubc.cs.sandboxer.test.demoapp;

import java.util.Random;

import ca.ubc.cs.sandboxer.core.QuarantineException;
import ca.ubc.cs.sandboxer.core.RuntimeSandbox;
import ca.ubc.cs.sandboxer.core.RuntimeSandboxManager;
import ca.ubc.cs.sandboxer.test.logger.Logger;

/**
 * Server task for simulating an OLTP-style workload.
 */
public class ServerTask implements Runnable {
	private Logger logger;
	private Random random = new Random();
	private RuntimeSandbox sandbox; // the logger sandbox
	
	public ServerTask(Logger logger) {
		this.logger = logger;
		this.sandbox = RuntimeSandboxManager.getDefault().getSandboxFromName( "UntrustedLoggerSandbox" );
	}
	
	@Override
	public void run() {
		cycle("Starting task");
		consumeCPU();
		cycle("Phase 1 complete");
		consumeCPU();
		cycle("Phase 2 complete");
		consumeCPU();
		cycle("Phase 3 complete");
		consumeCPU();
		cycle("Phase 4 complete");
		consumeCPU();
		cycle("Task complete");
	}

	/**
	 * Calls logger with quarantine protection
	 */
	private void cycle( String msg ) {
	    if ( isQuarantined() == false ) {
            try {
                logger.log( msg );
            } catch ( QuarantineException e ) {
                System.out.println( "Quarantine exception [Thread " + 
                        Thread.currentThread().getId() + "]: " + e.getMessage() );
            }
        }
	}

	/**
	 * Roughly designed to consume about 2 msecs of CPU time.
	 */
	private void consumeCPU() {
        for (int i = 0; i < 15000; i++) {
        	double result = Math.sqrt(random.nextDouble());
        }
	}
	
	/**
	 * Is instance in quarantined state?
	 * @return true for quarantined
	 */
	public boolean isQuarantined() {
		return sandbox.isQuarantined();
	}
}
