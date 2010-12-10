package ca.ubc.cs.sandboxer.test.demoapp;

/** 
 * Contains performance counters for a given worker.
 */
public class WorkerCounters {
	private long latencyNanos;
	
	public WorkerCounters(long latencyNanos) {
		this.latencyNanos = latencyNanos;
	}
	
	public double getLatencyMicros() {
		return latencyNanos/1000.0;
	}
}
