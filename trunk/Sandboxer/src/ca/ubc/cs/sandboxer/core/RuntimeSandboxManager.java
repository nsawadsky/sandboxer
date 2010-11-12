package ca.ubc.cs.sandboxer.core;

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
	
	private static RuntimeSandboxManager defaultInstance = new RuntimeSandboxManager();
	
	public static RuntimeSandboxManager getDefault() {
		return defaultInstance;
	}

	/**
	 * Activate the manager, creating runtime sandbox objects and associated monitors.
	 */
	public void activate(List<SandboxPolicy> policies) {
		if (isActivated) { 
			throw new SandboxerException("RuntimeSandboxManager already activated");
		}
		isActivated = true;
		for (SandboxPolicy policy: policies) {
			RuntimeSandbox sandbox = new RuntimeSandbox(policy);
			sandboxes.put(policy.getId(), sandbox);
			SandboxMonitor monitor = policy.getMonitor(sandbox);
			monitor.start();
		}
	}
	
	/**
	 * Event handler invoked when a sandboxed method is entered.
	 */
	public void enterMethod(int sandboxId, Class<?> cls, String methodName) {
		RuntimeSandbox sandbox = sandboxes.get(sandboxId);
		if (sandbox != null) {
			sandbox.enterMethod(cls, methodName);
		}
	}
	
	/**
	 * Event handler invoked when a sandboxed method is exited.
	 */
	public void leaveMethod(int sandboxId, Class<?> cls, String methodName) {
		RuntimeSandbox sandbox = sandboxes.get(sandboxId);
		if (sandbox != null) {
			sandbox.leaveMethod(cls, methodName);
		}
	}
	
	/**
	 * Event handler invoked when a new static field is discovered in a class being
	 * loaded into the sandbox.
	 */
	public void addStaticField(int sandboxId, FieldInfo fieldInfo) {
		RuntimeSandbox sandbox = sandboxes.get(sandboxId);
		if (sandbox != null) {
			sandbox.addStaticField(fieldInfo);
		}
	}
	
}
