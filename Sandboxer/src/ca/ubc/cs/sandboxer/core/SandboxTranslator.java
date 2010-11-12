package ca.ubc.cs.sandboxer.core;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.Translator;

/**
 * This class uses Javassist to transform loaded classes that match sandbox policies.
 */
public class SandboxTranslator implements Translator {
	private List<SandboxPolicy> policies = new ArrayList<SandboxPolicy>();

	public SandboxTranslator(List<SandboxPolicy> policies) {
		this.policies = policies;
	}
	
	/** 
	 * Load a class, instrumenting it if necessary.
	 */
	public void onLoad(ClassPool pool, String className) throws NotFoundException,
			CannotCompileException {
		List<SandboxPolicy> matchingPolicies = getMatchingPolicies(className);
		if (!matchingPolicies.isEmpty()) {
			CtClass ctc = pool.get(className);
			processFields(className, ctc.getDeclaredFields(), matchingPolicies);
			processMethods(className, ctc.getDeclaredMethods(), matchingPolicies);
		}
		
	}

	@Override
	public void start(ClassPool arg0) throws NotFoundException,
			CannotCompileException {
		
	}
	
	private void processMethods(String className, CtMethod[] methods, List<SandboxPolicy> policies) 
				throws CannotCompileException {
		for (CtMethod method: methods) {
			// Include protected and public methods.
			if ((Modifier.isPublic(method.getModifiers()) || Modifier.isProtected(method.getModifiers())) &&
					// Make sure we *can* instrument the method (e.g. method is not native).
					method.getMethodInfo().getCodeAttribute() != null) {

				for (SandboxPolicy policy: policies) {
					instrumentMethodForSandbox(method, policy);
				}
			}
		}
		
	}
	
	private void instrumentMethodForSandbox(CtMethod method, SandboxPolicy policy) throws CannotCompileException {
		final String SANDBOX_MANAGER = RuntimeSandboxManager.class.getName();
		String beforeCode = 
			"{" +
			    SANDBOX_MANAGER + ".getDefault().enterMethod(" + policy.getId() + ", $class, \"" + method.getName() + "\");" + 
			"}";
		String afterCode = 
			"{" +
		    	SANDBOX_MANAGER + ".getDefault().leaveMethod(" + policy.getId() + ", $class, \"" + method.getName() + "\");" + 
			"}";
		method.insertBefore(beforeCode);
		method.insertAfter(afterCode, true);
	}
	
	private void processFields(String className, CtField[] fields, List<SandboxPolicy> policies) {
		for (CtField field: fields) {
			// Add static fields declared within this class to the matching sandboxes.
			if (Modifier.isStatic(field.getModifiers())) {
				for (SandboxPolicy policy: policies) {
					RuntimeSandboxManager.getDefault().addStaticField(policy.getId(), 
							new FieldInfo(className, field.getName()));
				}
			}
		}
	}
	
	private List<SandboxPolicy> getMatchingPolicies(String className) {
		List<SandboxPolicy> results = new ArrayList<SandboxPolicy>();
		for (SandboxPolicy policy: policies) {
			if (policy.doesClassMatchPolicy(className)) {
				results.add(policy);
			}
		}
		return results;
	}
}
