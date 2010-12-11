package ca.ubc.cs.sandboxer.core;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.lang.ref.WeakReference;

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
        String getEntryClassName();
        
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
        
        private String entryClassName;
        
        private String entryMethodName;

        ActiveThreadInfo(Thread thread, String className, String methodName) {
            entryTimeMillis = System.currentTimeMillis();
            activeThread = thread;
            entryClassName = className;
            entryMethodName = methodName;
            entryCount = 1;
        }
        
        public Thread getActiveThread() {
            return activeThread;
        }

        public long getEntryTimeMsecs() {
            return entryTimeMillis;
        }
        
        public String getEntryClassName() {
            return entryClassName;
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
     * Queue of references to allocated instances of classes within the sandbox.
     * This may contain multiple references to the same object, since a reference is
     * added for each call to a sandboxed constructor (and multiple such calls may occur
     * for a single object, if its inheritance tree includes multiple classes within 
     * the sandbox).
     */
    private ConcurrentLinkedQueue<WeakReference<Object>> references = new ConcurrentLinkedQueue<WeakReference<Object>>();
    
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
     * Handler called when a thread enters a method or constructor of this sandbox.
     */
    public void enterMethod(String className, String methodName) {
        if (isQuarantined) {
            throw new QuarantineException(
                    "Sandbox " + policy.getSandboxName() + " is quarantined, quarantine reason: " + quarantineReason);
        }
        Thread currThread = Thread.currentThread();
        ActiveThreadInfo info = activeThreads.get(currThread);
        if (info == null) {
            activeThreads.put(currThread, new ActiveThreadInfo(currThread, className, methodName));
        } else {
            info.incrementEntryCount();
        }
    }
    
    /**
     * Handler called when a thread leaves a method or constructor of this sandbox.
     */
    public void leaveMethod(String className, String methodName) {
        Thread currThread = Thread.currentThread();
        ActiveThreadInfo info = activeThreads.get(currThread);
        if (info != null) {
            if (info.decrementEntryCount() == 0) {
                activeThreads.remove(currThread);    
            }
        }
    }
    
    /**
     * Handler called when a constructor of a class within the sandbox is exited.
     */
    public void leaveConstructor(String className, Object newObject) {
        references.add(new WeakReference<Object>(newObject));
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
     * Refresh queue of allocated objects, returning set of those objects
     * which have not yet been freed.
     */
    public synchronized Set<Object> refreshAllocatedObjects() {
        IdentityHashMap<Object, Object> allocatedObjects = new IdentityHashMap<Object, Object>();
        WeakReference<Object> marker = new WeakReference<Object>(this);
        references.add(marker);
        WeakReference<Object> ref = references.remove();
        int objectsRemoved = 0;
        long startTimeMsecs = System.currentTimeMillis();
        while (ref != marker) {
            Object obj = ref.get();
            if (obj == null) {
                objectsRemoved++;
            } else {
                references.add(ref);
                allocatedObjects.put(obj, obj);
            }
            ref = references.remove();
        };
        long endTimeMsecs = System.currentTimeMillis();
        //System.out.println("Removed " + objectsRemoved + " objects in " + (endTimeMsecs - startTimeMsecs) + " msecs");
        return allocatedObjects.keySet();
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
