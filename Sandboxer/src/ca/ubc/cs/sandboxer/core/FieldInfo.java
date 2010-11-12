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
    
    // Loader responsible for the class containing the field.
    private ClassLoader loader;
    
    public FieldInfo(String className, String fieldName, ClassLoader loader) {
        this.className = className;
        this.fieldName = fieldName;
        this.loader = loader;
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
                        // For this to work, we are relying on the proper thread synchronization
                        // currently implemented in javassist.Loader.
                        Class<?> cls = Class.forName(className, true, loader);
                        field = cls.getDeclaredField(fieldName);
                    } catch (ClassNotFoundException e) {
                        // This will most likely represent a coding error of some kind.
                        throw new SandboxerException(
                                "Unable to load class " + className + ": " + e, e);
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

