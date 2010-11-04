package ca.ubc.cs.sandboxer;

/**
 * Encapsulates a particular sandboxing policy.  The long-term plan is that
 * instances of SandboxPolicy will be created based on a configuration file.
 */
public class SandboxPolicy {
	private String[] packagePrefixes;
	private double maxCpuUsageRatio;
	private int cpuUsageWindowSeconds;
	private int maxHeapMegabytes;
	private QuarantineBehavior quarantineBehavior;
	private int id;

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
		 * or default value (for methods that return primitives).
		 */
		NullOrDefault
	}
	
	/**
	 * Instantiates a policy.
	 * 
	 * @param packagePrefixes The list of Java package name prefixes that are managed by the policy.
	 *        Packages whose names begin with the specified prefixes will be managed 
	 *        according to this policy.
	 * @param maxCpuUsageRatio The maximum average CPU usage for a thread executing
	 *        in one of the specified packages.  This returns fractional CPU usage,
	 *        i.e. a value between 0 and 1 representing the fraction of available
	 *        processor time (on a single CPU) that is used by a thread.
	 * @param cpuUsageWindowSeconds The window over which CPU usage is averaged.  If 
	 *        average CPU usage exceeds maxCpuUsageRatio for more than this number of 
	 *        seconds, the set of packages is quarantined. 
	 * @param maxHeapMegabytes The maximum amount of heap that may be used by all 
	 *        packages associated with the policy.
	 * @param quarantineBehavior The behavior to be enforced once a set of packages
	 *        has been quarantined.
	 */
	public SandboxPolicy(String[] packagePrefixes, 
			double maxCpuUsageRatio, int cpuUsageWindowSeconds,
			int maxHeapMegabytes, QuarantineBehavior behavior) {
		this.packagePrefixes = packagePrefixes;
		this.maxCpuUsageRatio = maxCpuUsageRatio;
		this.cpuUsageWindowSeconds = cpuUsageWindowSeconds;
		this.maxHeapMegabytes = maxHeapMegabytes;
		this.quarantineBehavior = behavior;
		this.id = SandboxPolicyManager.getInstance().getNewPolicyId();
		
	}
	
	public String[] getPackagePrefixes() {
		return packagePrefixes;
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
	
	public int getId() {
		return id;
	}
}
