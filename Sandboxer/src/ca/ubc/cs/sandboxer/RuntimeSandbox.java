package ca.ubc.cs.sandboxer;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A RuntimeSandbox is the runtime object associated with
 * a SandboxPolicy.
 */
public class RuntimeSandbox {
	/**
	 * The associated sandbox info.
	 */
	private SandboxPolicy policy;
	
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
	 * List of the static fields belonging to classes in this sandbox.
	 */
	private List<FieldInfo> staticFields = new CopyOnWriteArrayList<FieldInfo>();
	
	/**
	 * Create a runtime sandbox from the specified policy.
	 */
	public RuntimeSandbox(SandboxPolicy policy) {
		this.policy = policy;
	}
	
	/**
	 * Handler called when a thread enters a method of this sandbox.
	 */
	public void enterMethod(Class<?> cls, String methodName) {
		if (isQuarantined) {
			throw new QuarantineException(
					"Sandbox " + policy.getSandboxName() + " is quarantined, quarantine reason: " + quarantineReason);
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
	public void leaveMethod(Class<?> cls, String methodName) {
		Thread currThread = Thread.currentThread();
		ActiveThreadInfo info = activeThreads.get(currThread);
		if (info != null) {
			if (info.decrementEntryCount() == 0) {
				activeThreads.remove(currThread);	
			}
		}
	}
	
	/**
	 * Handler called when a new static field is discovered in a class being 
	 * loaded in to the sandbox.
	 */
	public void addStaticField(FieldInfo info) {
		staticFields.add(info);
	}
	
	/**
	 * Get the sandbox policy.
	 */
	public SandboxPolicy getPolicy() {
		return policy;
	}
	
	/**
	 * Method for monitors to gather information about threads currently in
	 * the sandbox.
	 */
	public Collection<ActiveThreadView> getActiveThreads() {
		return (Collection)Collections.unmodifiableCollection(activeThreads.values());
	}
	
	/**
	 * Method for monitors to gather information on the static fields associated
	 * with the sandbox.
	 */
	public List<FieldInfo> getStaticFields() {
		return Collections.unmodifiableList(staticFields);
	}
	
}
