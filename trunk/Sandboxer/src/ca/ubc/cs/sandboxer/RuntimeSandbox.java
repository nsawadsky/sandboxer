package ca.ubc.cs.sandboxer;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A RuntimeSandbox is the runtime object associated with
 * a SandboxPolicy.
 */
public class RuntimeSandbox {
	/**
	 * The associated sandbox info.
	 */
	private LoadTimeSandboxInfo sandboxInfo;
	
	/**
	 * Is this sandbox quarantined?
	 */
	private boolean isQuarantined = false;
	
	/**
	 * If quarantined, why?
	 */
	private String quarantineReason = null;

	/**
	 * The view of active threads available to monitors.
	 */
	public interface ActiveThreadView {
		/**
		 * Get the time when the thread entered the sandbox.
		 */
		long getEntryTimeMillis();
	}
	
	/**
	 * The information stored internally for active threads.
	 */
	public class ActiveThreadInfo implements ActiveThreadView {
		private long entryTimeMillis;
		
		private Thread activeThread;
		
		private int entryCount;

		ActiveThreadInfo(Thread thread) {
			entryTimeMillis = System.currentTimeMillis();
			activeThread = thread;
			entryCount = 1;
		}
		
		public Thread getActiveThread() {
			return activeThread;
		}

		public long getEntryTimeMillis() {
			return entryTimeMillis;
		}
		
		public int incrementEntryCount() {
			return ++entryCount;
		}
		
		public int decrementEntryCount() {
			return --entryCount;
		}
	}
	
	/**
	 * Info on the threads currently executing within this sandbox.
	 */
	private Map<Thread, ActiveThreadInfo> activeThreads = new ConcurrentHashMap<Thread, ActiveThreadInfo>();

	/**
	 * Create a runtime sandbox from the specified policy.
	 */
	public RuntimeSandbox(LoadTimeSandboxInfo info) {
		this.sandboxInfo = info;
	}
	
	/**
	 * Handler called when a thread enters a method of this sandbox.
	 */
	public void enterMethod() {
		if (isQuarantined) {
			throw new QuarantineException(
					"Sandbox " + sandboxInfo.getPolicy().getSandboxName() + " is quarantined, quarantine reason: " + quarantineReason);
		}
		Thread currThread = Thread.currentThread();
		ActiveThreadInfo info = activeThreads.get(currThread);
		if (info == null) {
			activeThreads.put(currThread, new ActiveThreadInfo(currThread));
		} else {
			info.incrementEntryCount();
		}
	}
	
	/**
	 * Handler called when a thread leaves a method of this sandbox.
	 */
	public void leaveMethod() {
		Thread currThread = Thread.currentThread();
		ActiveThreadInfo info = activeThreads.get(currThread);
		if (info != null) {
			if (info.decrementEntryCount() == 0) {
				activeThreads.remove(currThread);	
			}
		}
	}
	
	/**
	 * Get the load-time information on the sandbox.
	 */
	public LoadTimeSandboxInfo getLoadTimeSandboxInfo() {
		return sandboxInfo;
	}
	
	/**
	 * Method for monitors to gather information about threads currently in
	 * the sandbox.
	 */
	public Collection<ActiveThreadView> getActiveThreads() {
		return (Collection)Collections.unmodifiableCollection(activeThreads.values());
	}
	
}
