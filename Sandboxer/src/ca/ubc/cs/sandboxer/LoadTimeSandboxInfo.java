package ca.ubc.cs.sandboxer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Contains sandbox policy as well as other information on the sandbox
 * gathered at load time.
 */
public class LoadTimeSandboxInfo {
	private SandboxPolicy policy;
	private List<Field> fields = new ArrayList<Field>();
	
	public LoadTimeSandboxInfo(SandboxPolicy policy) {
		this.policy = policy;
	}
	
	/**
	 * Add a static field found in one of the classes in the sandbox.
	 */
	public void addStaticField(Field field) {
		fields.add(field);
	}

	/**
	 * Get list of static fields in the sandbox's classes.
	 */
	public List<Field> getStaticFields() {
		return Collections.unmodifiableList(fields);
	}
	
	public SandboxPolicy getPolicy() {
		return policy;
	}
}
