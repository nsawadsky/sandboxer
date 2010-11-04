package ca.ubc.cs.sandboxer.test.logger;

public class Logger {
	private static Logger instance = new Logger();
	
	public static Logger getInstance() {
		return instance;
	}
	
	public void log(String msg) {
		System.out.println("Logger.log(" + msg + ")");
	}
	
	public String toString() {
		System.out.println("Logger.toString");
		return super.toString();
	}
}
