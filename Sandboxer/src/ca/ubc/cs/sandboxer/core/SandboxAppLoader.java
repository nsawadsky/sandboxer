package ca.ubc.cs.sandboxer.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javassist.ClassPool;
import javassist.Loader;

/**
 * This is an application which is responsible for loading another application
 * with specified sandboxing policies enforced.
 */
public class SandboxAppLoader {
    static {
        // Load the associated JVM TI plugin.
        System.loadLibrary("SandboxerJVMTIPlugin");
    }
    
    /**
     * First argument is the full name of the main class of the app 
     * to be loaded.  Remaining arguments are passed to the loaded app.
     */
    public static void main(String[] args) {
        //printMessage("Testing JVM TI plugin");
        SandboxAppLoader loader = new SandboxAppLoader();
        loader.loadAppWithSandbox(args);
    }

    public void loadAppWithSandbox(String[] args) {
        try {
            List<SandboxPolicy> policies = parseCommandlinePolicies( args ); //getPolicies();
            
            RuntimeSandboxManager.getDefault().activate(policies);
            
            Loader loader = new Loader();
            
            SandboxTranslator translator = new SandboxTranslator(policies, loader);
            
            ClassPool pool = ClassPool.getDefault();
            
            loader.addTranslator(pool, translator);
            
            // Ensure all sandboxer classes are loaded by parent class loader 
            // (i.e. so they are not instrumented).
            loader.delegateLoadingOf(SandboxAppLoader.class.getPackage().getName() + ".");
            
            loader.run(args);
            
        } catch (Throwable e) {
            System.out.println("SandboxAppLoader caught exception: " + e);
        }
    }
    
    // returns a list of policies from commandline
    // a policy is defined as -policy=name,list:of:prefixes,maxtime,maxheap
    private List<SandboxPolicy> parseCommandlinePolicies( String[] args ) {
        final int NAME = 0;
        final int PREFIXES = 1;
        final int TIMEOUT = 2;
        final int HEAPSIZE = 3;

        List<SandboxPolicy> policies = new ArrayList<SandboxPolicy>();
        for ( String a: args ) {
            if ( a.startsWith( "-policy=" ) ) {

                String[] param = a.substring( 8 ).split( "," );
                if ( param.length >= PREFIXES+1 ) {
                    
                    String name = param[ NAME ];
                    List<String> prefixes = Arrays.asList( param[ PREFIXES ].split( ":" ) );
                    int maxtime = 0; 
                    int maxheap = 0;

                    if ( param.length >= TIMEOUT+1 ) {
                        maxtime = Integer.parseInt( param[ TIMEOUT ] );
                    }
                    if ( param.length == HEAPSIZE+1 ) {
                        maxheap = Integer.parseInt( param[ HEAPSIZE ] );
                    }
                    policies.add( new SandboxPolicy( name, prefixes, maxtime, 
                            maxheap, SandboxPolicy.QuarantineBehavior.Exception) );
                }
            }
        }

        return policies;
    }

    /**
     * Retrieves hard-coded sandboxing policies.  Eventually, policies should
     * be config-driven.  
     */
/*    private List<SandboxPolicy> getPolicies() {
        SandboxPolicy untrustedLoggerPolicy = new SandboxPolicy(
                "UntrustedLoggerSandbox",
                Arrays.asList("ca.ubc.cs.sandboxer.test.logger."),
                3000, 100, SandboxPolicy.QuarantineBehavior.Exception);
        return Arrays.asList(untrustedLoggerPolicy);    
    }
*/
    
    /**
     * Test function for JNI interface to JVM TI plugin.
     */
    private native static void printMessage(String msg);
}
