package ca.ubc.cs.sandboxer;

/**
 * General purpose exception class.
 */
public class SandboxerException extends RuntimeException {
	private final static long serialVersionUID = 1;
	
	public SandboxerException(String msg) {
		super(msg);
	}
	
	public SandboxerException(String msg, Throwable t) {
		super(msg, t);
	}
}
