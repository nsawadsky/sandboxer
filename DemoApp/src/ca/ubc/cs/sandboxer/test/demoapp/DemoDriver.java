package ca.ubc.cs.sandboxer.test.demoapp;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ca.ubc.cs.sandboxer.test.logger.Logger;

/**
 * Driver app to simulate OLTP-style workload.
 */
public class DemoDriver {

	private final static int EXPECTED_ARRIVAL_INTERVAL_MSECS = 30;

	public static void main(String[] args) {
		Logger logger = new Logger("DemoDriverLog.txt");
		
		Random random = new Random();
		
		ExecutorService threadPool = Executors.newCachedThreadPool();
		
		while (true) {
			WorkerTask task = new WorkerTask(logger);
			threadPool.submit(task);

			int uniformlyDistributed = random.nextInt(1000) + 1;
			int waitTimeMsecs = (int)(-EXPECTED_ARRIVAL_INTERVAL_MSECS * Math.log(uniformlyDistributed/1000.0));
			if (waitTimeMsecs < 0) {
				waitTimeMsecs = 0;
			}
			
			try {
				Thread.currentThread().sleep(waitTimeMsecs);
			} catch (InterruptedException e) {}
		}
	}

}
