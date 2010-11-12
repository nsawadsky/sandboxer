package ca.ubc.cs.sandboxer;

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
	private double maxCpuUsageRatio;
	private int cpuUsageWindowSeconds;
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
	 * @param maxCpuUsageRatio The maximum average CPU usage for a thread executing
	 *        in one of the specified packages.  This returns fractional CPU usage,
	 *        i.e. a value between 0 and 1 representing the fraction of available
	 *        processor time (on a single CPU) that is used by a thread.
	 * @param cpuUsageWindowSeconds The window over which CPU usage is averaged.  If 
	 *        average CPU usage exceeds maxCpuUsageRatio for this number of 
	 *        seconds, the set of packages is quarantined. 
	 * @param maxHeapMegabytes The maximum amount of heap that may be used by all 
	 *        packages associated with the policy.
	 * @param quarantineBehavior The behavior to be enforced once a set of packages
	 *        has been quarantined.
	 */
	public SandboxPolicy(String sandboxName, List<String> packagePrefixes, 
			double maxCpuUsageRatio, int cpuUsageWindowSeconds,
			int maxHeapMegabytes, QuarantineBehavior behavior) {
		this.id = getNextPolicyId();
		this.sandboxName = sandboxName;
		this.packagePrefixes = packagePrefixes;
		this.maxCpuUsageRatio = maxCpuUsageRatio;
		this.cpuUsageWindowSeconds = cpuUsageWindowSeconds;
		this.maxHeapMegabytes = maxHeapMegabytes;
		this.quarantineBehavior = behavior;
		
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
	
	public double getMaxCpuUsageRatio() {
		return maxCpuUsageRatio;
	}
	
	public int getCpuUsageWindowSeconds() {
		return cpuUsageWindowSeconds;
	}
	
	public int getMaxHeapMegabytes() {
		return maxHeapMegabytes;
	}
	
	public QuarantineBehavior getQuarantineBehavior() {
		return quarantineBehavior;
	}
	
	private synchronized static int getNextPolicyId() {
		return nextPolicyId++;
	}
}
