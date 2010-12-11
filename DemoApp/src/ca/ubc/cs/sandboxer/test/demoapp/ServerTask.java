package ca.ubc.cs.sandboxer.test.demoapp;

import java.util.Random;

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
		logger.log("Starting task");
		consumeCPU();
		logger.log("Phase 1 complete");
		consumeCPU();
		logger.log("Phase 2 complete");
		consumeCPU();
		logger.log("Phase 3 complete");
		consumeCPU();
		logger.log("Phase 4 complete");
		consumeCPU();
		logger.log("Task complete");
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
