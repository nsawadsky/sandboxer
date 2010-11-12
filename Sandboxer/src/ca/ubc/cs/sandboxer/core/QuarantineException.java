package ca.ubc.cs.sandboxer.core;

/**
 * Policy can be configured so that if a set of packages must be quarantined, then
 * all subsequent calls to the packages throw QuarantineException.
 */
public class QuarantineException extends RuntimeException {
	private static final long serialVersionUID = 1;
	
	public QuarantineException(String message) {
		super(message);
	}
}
