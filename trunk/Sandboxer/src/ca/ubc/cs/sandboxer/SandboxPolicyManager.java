package ca.ubc.cs.sandboxer;

import java.util.HashMap;
import java.util.Map;

public class SandboxPolicyManager {
	private static SandboxPolicyManager instance = new SandboxPolicyManager();
	
	private Map<Integer, SandboxPolicy> policies = new HashMap<Integer, SandboxPolicy>();
	
	/**
	 * Next ID to allocate.
	 */
	private int currentId = 1;
	
	public static SandboxPolicyManager getInstance() {
		return instance;
	}
	
	/** 
	 * Gets a new, unique policy identifier.  Method needs to be synchronized
	 * since it reads and increments the current ID.
	 */
	public synchronized int getNewPolicyId() {
		return currentId++;
	}
	/**
	 * Registers a policy.  This method needs to be synchronized 
	 * because it writes to the policies map.
	 */
	public synchronized void registerPolicy(SandboxPolicy policy) {
		policies.put(policy.getId(), policy);
	}
	
	/**
	 * Get registered policy.  This method needs to be synchronized, since it 
	 * reads the policies map.
	 * 
	 * @return The policy associated with the specified ID, or null if ID not found.
	 */
	public synchronized SandboxPolicy getPolicy(int id) {
		return policies.get(id);	
	}
	
	/**
	 * Gets array of registered policies.  Method needs to be synchronized since
	 * it reads the policy map.
	 */
	public synchronized SandboxPolicy[] getPolicies() {
		return policies.values().toArray(new SandboxPolicy[policies.size()]);
	}
}
