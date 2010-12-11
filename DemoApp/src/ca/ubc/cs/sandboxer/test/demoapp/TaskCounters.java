package ca.ubc.cs.sandboxer.test.demoapp;

/** 
 * Contains performance counters for a given client task.
 */
public class TaskCounters {
	private long latencyNanos;
	
	public TaskCounters(long latencyNanos) {
		this.latencyNanos = latencyNanos;
	}
	
	public long getLatencyNanos() {
	    return latencyNanos;
	}
	
	public double getLatencyMsecs() {
	    return latencyNanos/1.0E+06;
	}
}
