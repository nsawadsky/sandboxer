package ca.ubc.cs.sandboxer.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages sandboxes at runtime.
 */
public class RuntimeSandboxManager {
    private boolean isActivated = false;
    
    private Map<Integer, RuntimeSandbox> sandboxes = new HashMap<Integer, RuntimeSandbox>();
    
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
    public void enterMethod(int sandboxId, String className, String methodName) {
        RuntimeSandbox sandbox = sandboxes.get(sandboxId);
        if (sandbox != null) {
            sandbox.enterMethod(className, methodName);
        }
    }
    
    /**
     * Event handler invoked when a sandboxed method is exited.
     */
    public void leaveMethod(int sandboxId, String className, String methodName) {
        RuntimeSandbox sandbox = sandboxes.get(sandboxId);
        if (sandbox != null) {
            sandbox.leaveMethod(className, methodName);
        }
    }
    
    /**
     * Handler called when a constructor of a class within a sandbox is exited.
     */
    public void leaveConstructor(int sandboxId, String className, Object newObject) {
        RuntimeSandbox sandbox = sandboxes.get(sandboxId);
        if (sandbox != null) {
            sandbox.leaveConstructor(className, newObject);
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

    /**
     * Returns sandbox for a given sandbox-id
     */
    public RuntimeSandbox getSandbox( int sandboxId ) {
        return sandboxes.get( sandboxId );
    }
    
    /**
     * Given a sandbox (policy) name returns the sandbox or null if none found
     */
    public RuntimeSandbox getSandboxFromName( String name ) {
        for ( Map.Entry<Integer, RuntimeSandbox> it: sandboxes.entrySet() ) {
            if ( it.getValue().getPolicy().getSandboxName().equals( name ) ) {
                return it.getValue();
            }
        }
        return null;
    }
}
