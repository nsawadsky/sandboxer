package ca.ubc.cs.sandboxer.core;

import java.lang.reflect.Field;

/**
 * Encapsulates field information obtained during class loading (when Class object is 
 * not yet available).
 */
public class FieldInfo {
	private String className;
	private String fieldName;
	private Field field;
	
	public FieldInfo(String className, String fieldName) {
		this.className = className;
		this.fieldName = fieldName;
	}
	
	public String getClassName() {
		return className;
	}
	
	public String getFieldName() {
		return fieldName;
	}
	
	/**
	 * Get the associated java.lang.reflect.Field instance.  Returns null if loading of 
	 * associated class has not yet completed.
	 */
	public Field getField() {
		if (field == null) {
			synchronized (this) {
				if (field == null) {
					try {
						Class<?> cls = Class.forName(className);
						field = cls.getDeclaredField(fieldName);
					} catch (ClassNotFoundException e) {
						// We ignore this exception and return null. 
						// This could happen if getField() is called before loading
						// of the class has completed.
					} catch (NoSuchFieldException e) {
						// This will most likely represent a coding error of some kind.
						throw new SandboxerException(
								"Unable to find field " + fieldName + " in class " + className + ": " + e, 
								e);
					}
				}
			}
		}
		return field;
	}
}

