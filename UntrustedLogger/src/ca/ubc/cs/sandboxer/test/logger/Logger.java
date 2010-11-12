package ca.ubc.cs.sandboxer.test.logger;

public class Logger extends BaseLogger {
	private static Logger instance = new Logger();
	
	public static Logger getInstance() {
		return instance;
	}
	
	public void log(String msg) {
		internalLog(msg);
	}
	
	public String toString() {
		System.out.println("Logger.toString");
		return super.toString();
	}
	
	public void longRunningCall() {
		System.out.println("Logger entering longRunningCall");
		internalLongRunningCall();
		System.out.println("Logger leaving longRunningCall");
	}
	
	protected void internalLog(String msg) {
		System.out.println("Logger.log(" + msg + ")");
	}
	
	protected void internalLongRunningCall() {
		try {
			Thread.sleep(7000);
		} catch (InterruptedException e) {}
	}
}
