package ca.ubc.cs.sandboxer;

import javassist.ClassPool;
import javassist.Loader;

/**
 * This is an application which is responsible for loading another application
 * with specified sandboxing policies enforced.
 */
public class SandboxAppLoader {
	/**
	 * First argument is the full name of the main class of the app 
	 * to be loaded.  Remaining arguments are passed to the loaded app.
	 */
	public static void main(String[] args) {
		SandboxAppLoader loader = new SandboxAppLoader();
		loader.loadAppWithSandbox(args);	
	}

	public void loadAppWithSandbox(String[] args) {
		try {
			registerPolicies();
			
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
	 * Installs hard-coded sandboxing policies.  Eventually, policies should
	 * be config-driven.
	 */
	private void registerPolicies() {
		SandboxPolicy untrustedLoggerPolicy = new SandboxPolicy(
				new String[] { "ca.ubc.cs.sandboxer.test.logger." },
				0.90, 5, 10, SandboxPolicy.QuarantineBehavior.Exception);
		SandboxPolicyManager.getInstance().registerPolicy(untrustedLoggerPolicy);
		
	}
}
