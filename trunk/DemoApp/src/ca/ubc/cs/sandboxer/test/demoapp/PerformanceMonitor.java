package ca.ubc.cs.sandboxer.test.demoapp;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Collects performance data, and periodically calculates and displays statistics.
 */
public class PerformanceMonitor implements Runnable {
	private List<WorkerCounters> workerCounterList = new LinkedList<WorkerCounters>();
	
	// Interval over which statistics are calculated
	private final static int PERF_MONITOR_INTERVAL_SECS = 15;
	
	@Override
	public void run() {
		while (true) {
			try {
				Thread.currentThread().sleep(PERF_MONITOR_INTERVAL_SECS * 1000);
			} catch (InterruptedException e) {}
			
			List<WorkerCounters> counterList = captureCounterList();
			Collections.sort(
					counterList, new Comparator<WorkerCounters>() {

						@Override
						public int compare(WorkerCounters o1, WorkerCounters o2) {
							double diff = o1.getLatencyMicros() - o2.getLatencyMicros();
							if (diff > 0) {
								return 1;
							} else if (diff < 0) {
								return -1;
							}
							return 0;
						} 
					
					});
			
		}
	}
	
	public synchronized void addWorkerStats(WorkerCounters counters) {
		workerCounterList.add(counters);
	}
	
	/**
	 * Retrieves the current counter list, replacing it with an empty one.
	 */
	private synchronized List<WorkerCounters> captureCounterList() {
		List<WorkerCounters> result = workerCounterList;
		workerCounterList = new LinkedList<WorkerCounters>();
		return result;
	}

}
