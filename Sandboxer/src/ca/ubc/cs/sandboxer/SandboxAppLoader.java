package ca.ubc.cs.sandboxer;

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
			List<SandboxPolicy> policies = getPolicies();
			
			SandboxTranslator translator = new SandboxTranslator();
			
			ClassPool pool = ClassPool.getDefault();
			
			Loader loader = new Loader();
	
			loader.addTranslator(pool, translator);
			
			loader.run(args);
			
		} catch (Throwable e) {
			System.out.println("SandboxAppLoader caught exception: " + e);
		}
	}
	
	/**
	 * Retrieves hard-coded sandboxing policies.  Eventually, policies should
	 * be config-driven.  
	 */
	private List<SandboxPolicy> getPolicies() {
		SandboxPolicy untrustedLoggerPolicy = new SandboxPolicy(
				"UntrustedLoggerSandbox",
				Arrays.asList("ca.ubc.cs.sandboxer.test.logger."),
				0.90, 5, 10, SandboxPolicy.QuarantineBehavior.Exception);
		return Arrays.asList(untrustedLoggerPolicy);	
	}
	
	/**
	 * Test function for JNI interface to JVM TI plugin.
	 */
	private native static void printMessage(String msg);
}
