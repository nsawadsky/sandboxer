package ca.ubc.cs.sandboxer.test.logger;

import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class Logger extends BaseLogger {
    private static Logger instance = new Logger();
    private Map<String, String> messageMap = new HashMap<String, String>();
    private String fileName; 
    
    private final static String END_OF_LINE = System.getProperty("line.separator");
    
    public static Logger getInstance() {
        return instance;
    }
    
    public Logger() {
    }
    
    public Logger(String fileName) {
    	this.fileName = fileName;
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
        //System.out.println("Logger.log(" + msg + ")");
    	messageMap.put(msg, msg);
    	if (fileName != null) {
    		try {
    			FileWriter writer = new FileWriter(fileName, true);
    			writer.write(msg);
    			writer.write(END_OF_LINE);
    			writer.close();
    		} catch (Exception e) {}
    	}
    }
    
    protected void internalLongRunningCall() {
        do {} while (true);
    }
}
