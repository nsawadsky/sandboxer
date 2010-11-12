package ca.ubc.cs.sandboxer.test.logger;

public class BaseLogger {
    private static BaseLogger instance = new BaseLogger();
    
    public void baseLog(String message) {
        System.out.println("BaseLogger.baseLog(" + message + ")");
    }
}
