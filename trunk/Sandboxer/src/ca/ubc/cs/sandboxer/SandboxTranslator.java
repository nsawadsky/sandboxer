package ca.ubc.cs.sandboxer;

import java.lang.reflect.Modifier;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.Translator;
import javassist.expr.NewExpr;
import javassist.expr.ExprEditor;
import javassist.expr.NewArray;

public class SandboxTranslator implements Translator {

	@Override
	public void onLoad(ClassPool pool, String className) throws NotFoundException,
			CannotCompileException {
		final String UNTRUSTED_PKG = "ca.ubc.cs.sandbox.test.logger.";
		if (className.startsWith(UNTRUSTED_PKG)) {
			CtClass ctc = pool.get(className);
			CtMethod[] methods = ctc.getMethods();
			for (CtMethod method: methods) {
				if (method.getDeclaringClass().getName().startsWith(UNTRUSTED_PKG) && 
						Modifier.isPublic(method.getModifiers()) && 
						method.getMethodInfo().getCodeAttribute() != null) {
					int lastDot = className.lastIndexOf(".");
					String shortClassName = className.substring(lastDot+1);
					final String classAndMethod = shortClassName + "." + method.getName();

					method.instrument( new ExprEditor() { 
						public void edit(NewExpr expr) {
							try {
								expr.replace("{ ca.ubc.cs.sandbox.SandboxTranslator.handleNew( $type, \"" + classAndMethod + "\"); $_ = $proceed($$); }");
							} catch (Exception e) {
								System.out.println("ExprEditor.edit(NewExpr) caught exception: " + e);
							}
						}
						
						public void edit(NewArray expr) {
							try {
								expr.replace("{ ca.ubc.cs.sandbox.SandboxTranslator.handleNewArray( $type, \"" + classAndMethod + "\"); $_ = $proceed($$); }");
							} catch (Exception e) {
								System.out.println("ExprEditor.edit(NewArray) caught exception: " + e);
							}
						}
					
					});
					
					method.insertBefore("{ ca.ubc.cs.sandbox.SandboxTranslator.handleEnterPublicMethod(\"" + classAndMethod + "\"); }");
					method.insertAfter("{ ca.ubc.cs.sandbox.SandboxTranslator.handleExitPublicMethod(\"" + classAndMethod + "\"); }");
				}
			}
		}
		
	}

	@Override
	public void start(ClassPool arg0) throws NotFoundException,
			CannotCompileException {
		
	}
	
	public static void handleEnterPublicMethod(String classAndMethod) {
		System.out.println("Translator: entering public method: " + classAndMethod);
	}
	
	public static void handleExitPublicMethod(String classAndMethod) {
		System.out.println("Translator: exiting public method: " + classAndMethod);
	}
	
	public static void handleNew(Class<?> newType, String classAndMethod) {
		System.out.println("Translator: new " + newType.getSimpleName() + ": " + classAndMethod);
	}
	
	public static void handleNewArray(Class<?> arrType, String classAndMethod) {
		System.out.println("Translator: new " + arrType.getSimpleName() + "[]: " + classAndMethod);
	}
}
