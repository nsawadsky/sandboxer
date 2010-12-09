package ca.ubc.cs.sandboxer.core;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.Translator;

/**
 * This class uses Javassist to transform loaded classes that match sandbox policies.
 */
public class SandboxTranslator implements Translator {
    private List<SandboxPolicy> policies;
    private ClassLoader loader;
    private final static String SANDBOX_MANAGER = RuntimeSandboxManager.class.getName();

    public SandboxTranslator(List<SandboxPolicy> policies, ClassLoader loader) {
        this.policies = policies;
        this.loader = loader;
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
            processConstructors(className, ctc.getDeclaredConstructors(), matchingPolicies);
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
                    instrumentMethodForSandbox(className, method, policy);
                }
            }
        }
        
    }
    
    private void processConstructors(String className, CtConstructor[] constructors, List<SandboxPolicy> policies) 
                throws CannotCompileException {
        for (CtConstructor ctor: constructors) {
            // Make sure we *can* instrument the constructor (e.g. constructor is not native).
            if (ctor.getMethodInfo().getCodeAttribute() != null) {
                for (SandboxPolicy policy: policies) {
                    if (Modifier.isPublic(ctor.getModifiers()) || Modifier.isProtected(ctor.getModifiers())) {
                        instrumentMethodForSandbox(className, ctor, policy);
                    }
                    instrumentConstructorForSandbox(className, ctor, policy);
                }
            }
        }
    }
    
    private void instrumentConstructorForSandbox(String className, CtConstructor ctor, SandboxPolicy policy) throws CannotCompileException {
        String afterCode =
            "{" + 
                SANDBOX_MANAGER + ".getDefault().leaveConstructor(" + policy.getId() + ", \"" + className + "\", $0);" +
            "}";
        // Only executed if the constructor returns successfully (without throwing an exception).
        ctor.insertAfter(afterCode);
    }
    
    private void instrumentMethodForSandbox(String className, CtBehavior method, SandboxPolicy policy) throws CannotCompileException {
        String beforeCode = 
            "{" +
                SANDBOX_MANAGER + ".getDefault().enterMethod(" + policy.getId() + ", \"" + className + "\", \"" + method.getName() + "\");" + 
            "}";
        String afterCode = 
            "{" +
                SANDBOX_MANAGER + ".getDefault().leaveMethod(" + policy.getId() + ", \"" + className + "\", \"" + method.getName() + "\");" + 
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
                            new FieldInfo(className, field.getName(), loader));
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
