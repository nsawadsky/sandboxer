package ca.ubc.cs.sandboxer.test.demoapp;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Driver app to simulate OLTP-style workload.  Communicates with server via
 * Java RMI.
 */
public class DemoClient {

	private final static int EXPECTED_ARRIVAL_DELAY_MSECS = 30;

	public static void main(String[] args) {
	    try {
            System.out.println("Starting DemoClient");
            Thread.currentThread().setPriority(Thread.NORM_PRIORITY + 1);
	        
    		Registry registry = LocateRegistry.getRegistry(null);
            DemoService service = (DemoService)registry.lookup(DemoServer.SERVICE_NAME);
            
    		Random random = new Random();
    		
    		ExecutorService threadPool = Executors.newCachedThreadPool();

    		PerformanceTracker tracker = new PerformanceTracker();
    		Thread trackerThread = new Thread(tracker);
    		trackerThread.start();
    		
    		while (true) {
    		    ClientTask clientTask = new ClientTask(service, tracker);
    			threadPool.submit(clientTask);
    
    			// Poisson distribution with expected arrival delay as specified.
    			int uniformlyDistributed = random.nextInt(1000) + 1;
    			int waitTimeMsecs = (int)(-EXPECTED_ARRIVAL_DELAY_MSECS * Math.log(uniformlyDistributed/1000.0));
    			if (waitTimeMsecs < 0) {
    				waitTimeMsecs = 0;
    			}
    			
    			try {
    				Thread.sleep(waitTimeMsecs);
    			} catch (InterruptedException e) {}
    		}
	    } catch (Exception e) {
	        System.out.println("Unexpected exception in DemoClient: " + e);
	    }
	}

}
