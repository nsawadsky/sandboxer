package ca.ubc.cs.sandboxer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages sandboxes at runtime.
 */
public class RuntimeSandboxManager {
	private boolean isActivated = false;
	
	private Map<Integer, RuntimeSandbox> sandboxes = 
		new ConcurrentHashMap<Integer, RuntimeSandbox>();
	
	private List<LoadTimeSandboxInfo> sandboxInfos = new ArrayList<LoadTimeSandboxInfo>();
	
	private static RuntimeSandboxManager defaultInstance = new RuntimeSandboxManager();
	
	public static RuntimeSandboxManager getDefault() {
		return defaultInstance;
	}

	/**
	 * Adds info on a loaded sandbox to the manager.  This method cannot
	 * be called after the manager is activated.
	 */
	public void addLoadTimeSandboxInfo(LoadTimeSandboxInfo info) {
		if (isActivated) {
			throw new SandboxerException("Cannot add a sandbox after manager has been activated");
		}
		sandboxInfos.add(info);
	}

	/**
	 * Activate the manager, creating runtime sandbox objects.
	 */
	public void activate() {
		isActivated = true;
		for (LoadTimeSandboxInfo info: sandboxInfos) {
			sandboxes.put(info.getPolicy().getId(), new RuntimeSandbox(info));
		}
	}
	
	/**
	 * Event handler invoked when a sandboxed method is entered.
	 */
	public void enterMethod(int sandboxId) {
		RuntimeSandbox sandbox = sandboxes.get(sandboxId);
		if (sandbox != null) {
			sandbox.enterMethod();
		}
	}
	
	/**
	 * Event handler invoked when a sandboxed method is exited.
	 */
	public void leaveMethod(int sandboxId) {
		RuntimeSandbox sandbox = sandboxes.get(sandboxId);
		if (sandbox != null) {
			sandbox.leaveMethod();
		}
	}
	
}
