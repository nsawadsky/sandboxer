package ca.ubc.cs.sandboxer.core;

/**
 * This class provides a JNI interface to the JVM TI
 * plugin.  The JVM TI plugin provides memory tracking functionality
 * to the sandboxer.
 */
public class SandboxGuardian {
    static {
        // Load the associated JVM TI plugin.
        System.loadLibrary("SandboxerJVMTIPlugin");
    }

    /**
     * Get size of memory (in bytes) reachable from specified objects.
     */
    public native static long getReferencedSize(Object[] objects);
}
