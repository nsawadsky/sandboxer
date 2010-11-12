package ca.ubc.cs.sandboxer.core;

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
        long getEntryTimeMsecs();
        
        /** 
         * Get the associated thread.
         */
        Thread getActiveThread();
        
        /**
         * Get the class where the thread entered the sandbox.
         */
        Class<?> getEntryClass();
        
        /**
         * Get the method where the thread entered the sandbox.
         */
        String getEntryMethodName();
    }
    
    /**
     * The information stored internally for active threads.
     */
    public class ActiveThreadInfo implements ActiveThreadView {
        private long entryTimeMillis;
        
        private Thread activeThread;
        
        private int entryCount;
        
        private Class<?> entryClass;
        
        private String entryMethodName;

        ActiveThreadInfo(Thread thread, Class<?> cls, String methodName) {
            entryTimeMillis = System.currentTimeMillis();
            activeThread = thread;
            entryClass = cls;
            entryMethodName = methodName;
            entryCount = 1;
        }
        
        public Thread getActiveThread() {
            return activeThread;
        }

        public long getEntryTimeMsecs() {
            return entryTimeMillis;
        }
        
        public Class<?> getEntryClass() {
            return entryClass;
        }
        
        public String getEntryMethodName() {
            return entryMethodName;
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
    
    public void setQuarantined(String reason) {
        if (!isQuarantined) {
            isQuarantined = true;
            quarantineReason = reason;
        }
    }
    
    /**
     * Handler called when a thread enters a method of this sandbox.
     */
    public void enterMethod(Class<?> cls, String methodName) {
        System.out.println("Entering " + cls.getSimpleName() + "." + methodName);
        if (isQuarantined) {
            throw new QuarantineException(
                    "Sandbox " + policy.getSandboxName() + " is quarantined, quarantine reason: " + quarantineReason);
        }
        Thread currThread = Thread.currentThread();
        ActiveThreadInfo info = activeThreads.get(currThread);
        if (info == null) {
            activeThreads.put(currThread, new ActiveThreadInfo(currThread, cls, methodName));
        } else {
            info.incrementEntryCount();
        }
    }
    
    /**
     * Handler called when a thread leaves a method of this sandbox.
     */
    public void leaveMethod(Class<?> cls, String methodName) {
        System.out.println("Leaving " + cls.getSimpleName() + "." + methodName);
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
        System.out.println("Adding static field " + info.getClassName() + "." + info.getFieldName());
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
