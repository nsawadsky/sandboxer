package ca.ubc.cs.sandboxer.core;

import java.util.Collections;
import java.util.List;

/**
 * Encapsulates a particular sandboxing policy.  The long-term plan is that
 * instances of SandboxPolicy will be created based on a configuration file.
 */
public class SandboxPolicy {
	/**
	 * The next policy ID to be allocated.
	 */
	private static int nextPolicyId = 1;
	
	private int id;
	private String sandboxName;
	private List<String> packagePrefixes;
	private int callTimeoutMsecs;
	private int maxHeapMegabytes;
	private QuarantineBehavior quarantineBehavior;

	/**
	 * Enum defining behavior once library is quarantined.
	 */
	public enum QuarantineBehavior {
		/** 
		 * Once library is quarantined, throw QuarantineException for all subsequent
		 * calls in to library.
		 */
		Exception,
		
		/** 
		 * Once library is quarantined, all methods return null (for methods that return objects)
		 * or default value (for methods that return primitives).  (Not yet implemented.)
		 */
		//NullOrDefault
	}
	
	/**
	 * Instantiates a policy.
	 * 
	 * @param sandboxName Name of the sandbox this policy governs.
	 * @param packagePrefixes The list of Java package name prefixes that are managed by the policy.
	 *        Packages whose names begin with the specified prefixes will be managed 
	 *        according to this policy.
	 * @param callTimeoutMsecs Timeout in milliseconds for all calls in to the sandbox.
	 * @param maxHeapMegabytes The maximum amount of heap that may be used by all 
	 *        packages associated with the policy.
	 * @param quarantineBehavior The behavior to be enforced once a set of packages
	 *        has been quarantined.
	 */
	public SandboxPolicy(String sandboxName, List<String> packagePrefixes, 
			int callTimeoutMsecs, int maxHeapMegabytes, QuarantineBehavior behavior) {
		this.id = getNextPolicyId();
		this.sandboxName = sandboxName;
		this.packagePrefixes = packagePrefixes;
		this.callTimeoutMsecs = callTimeoutMsecs;
		this.maxHeapMegabytes = maxHeapMegabytes;
		this.quarantineBehavior = behavior;
		
	}
	
	/**
	 * Check if the given class matches one of the package prefixes configured
	 * for this policy.
	 */
	public boolean doesClassMatchPolicy(String className) {
		for (String packagePrefix: packagePrefixes) {
			if (className.startsWith(packagePrefix)) {
				return true;
			}
		}
		return false;
	}
	
	public String getSandboxName() {
		return sandboxName;
	}
	
	public int getId() {
		return id;
	}
	
	public List<String> getPackagePrefixes() {
		return Collections.unmodifiableList(packagePrefixes);
	}
	
	public int getCallTimeoutMsecs() {
		return callTimeoutMsecs;
	}
	
	public int getMaxHeapMegabytes() {
		return maxHeapMegabytes;
	}
	
	public QuarantineBehavior getQuarantineBehavior() {
		return quarantineBehavior;
	}
	
	public SandboxMonitor getMonitor(RuntimeSandbox sandbox) {
		return new SandboxMonitor(this, sandbox);
	}
	
	private synchronized static int getNextPolicyId() {
		return nextPolicyId++;
	}
}
