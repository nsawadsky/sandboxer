package ca.ubc.cs.sandboxer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A RuntimeSandbox is the runtime object associated with
 * a SandboxPolicy.
 */
public class RuntimeSandbox {
	/**
	 * The associated policy.
	 */
	private SandboxPolicy policy;
	
	/**
	 * The set of threads currently executing within this sandbox.
	 */
	private Map<Thread, Thread> activeThreads = new ConcurrentHashMap<Thread, Thread>();

	/**
	 * Create a runtime sandbox from the specified policy.
	 */
	public RuntimeSandbox(SandboxPolicy policy) {
		this.policy = policy;
	}
	
	/**
	 * Get the unique ID associated with this sandbox.  This ID
	 * is identical to that of the matching policy.
	 */
	public int getId() {
		return policy.getId();
	}
	
	/**
	 * Add specified thread to this sandbox.
	 */
	public void addThread(Thread thread) {
		activeThreads.put(thread, thread);
	}
	
	/**
	 * Remove specified thread from this sandbox.
	 */
	public void removeThread(Thread thread) {
		activeThreads.remove(thread);	
	}
	
}
