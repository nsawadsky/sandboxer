package ca.ubc.cs.sandboxer.test.demoapp;

import java.util.Random;

import ca.ubc.cs.sandboxer.core.QuarantineException;
import ca.ubc.cs.sandboxer.test.logger.Logger;

/**
 * Server task for simulating an OLTP-style workload.
 */
public class ServerTask implements Runnable {
	private Logger logger;
	private Random random = new Random();
	
	public ServerTask(Logger logger) {
		this.logger = logger;
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
        try {
            logger.log( msg );
        } catch ( QuarantineException e ) {
            // exception is silently ignored, fall-back code can be placed here
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
}
