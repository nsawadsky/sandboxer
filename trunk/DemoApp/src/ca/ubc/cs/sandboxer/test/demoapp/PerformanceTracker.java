package ca.ubc.cs.sandboxer.test.demoapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Collects performance data, and periodically calculates and displays statistics.
 */
public class PerformanceTracker implements Runnable {
	private List<TaskCounters> workerCounterList = new ArrayList<TaskCounters>();
	
	// Interval over which statistics are calculated
	private final static int PERF_TRACKER_INTERVAL_SECS = 10;
	
	@Override
	public void run() {
	    long lastTimestampNanos = System.nanoTime();
	    
		while (true) {
			try {
				Thread.sleep(PERF_TRACKER_INTERVAL_SECS * 1000);
			} catch (InterruptedException e) {}
			
			List<TaskCounters> counterList = captureCounterList();
			long newTimestampNanos = System.nanoTime();
			long deltaNanos = newTimestampNanos - lastTimestampNanos;
			lastTimestampNanos = newTimestampNanos;
			
			if (counterList.size() == 0) {
                System.out.println("*** Performance Tracker Statistics ***");
                System.out.println("No tasks completed");
                System.out.println();
			} else {
    			Collections.sort(
    					counterList, new Comparator<TaskCounters>() {
    
    						@Override
    						public int compare(TaskCounters o1, TaskCounters o2) {
    							long diff = o1.getLatencyNanos() - o2.getLatencyNanos();
    							if (diff > 0) {
    								return 1;
    							} else if (diff < 0) {
    								return -1;
    							}
    							return 0;
    						} 
    					
    					});
    			
    			double latencySumMsecs = 0.0;
    			for (TaskCounters counters: counterList) {
    			    latencySumMsecs += counters.getLatencyMsecs();
    			}
    			double latencyAverageMsecs = latencySumMsecs / counterList.size();
    			
    			double latencyMaxMsecs = counterList.get(counterList.size()-1).getLatencyMsecs();
    			
    			double tasksPerSec = counterList.size() / (deltaNanos/1.0E+09);
    			
    			System.out.println("*** Client Performance Tracker ***");
    			System.out.format("Tasks per second: %.3f\n", tasksPerSec);
    			System.out.format("Average latency (msecs): %.3f\n", latencyAverageMsecs);
    			System.out.format("Max latency (msecs): %.3f\n", latencyMaxMsecs);
    			System.out.println();
			}
		}
	}
	
	public synchronized void addWorkerCounters(TaskCounters counters) {
		workerCounterList.add(counters);
	}
	
	/**
	 * Retrieves the current counter list, replacing it with an empty one.
	 */
	private synchronized List<TaskCounters> captureCounterList() {
		List<TaskCounters> result = workerCounterList;
		workerCounterList = new ArrayList<TaskCounters>();
		return result;
	}

}
