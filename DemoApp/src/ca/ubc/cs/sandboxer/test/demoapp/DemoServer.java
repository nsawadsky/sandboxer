package ca.ubc.cs.sandboxer.test.demoapp;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.atomic.AtomicBoolean;
import ca.ubc.cs.sandboxer.test.logger.Logger;

/**
 * Java RMI server to demonstrate capabilities of sandboxer.
 */
public class DemoServer implements DemoService {
    private Logger logger = new Logger("DemoDriverLog.txt");
    private boolean longRunningCall = false;
    
    public static void main(String[] args) {
        DemoServer server = new DemoServer();
    
        try {
            DemoService service = (DemoService)UnicastRemoteObject.exportObject(server, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.bind(SERVICE_NAME, service);

            System.out.println("DemoServer started, hit Enter to exit ...");
            boolean done = false; 
            while (!done) {
            	byte[] buf = new byte[1024];
            	System.in.read(buf);
            	String input = new String(buf);
            	input = input.trim();
	            if (input.toLowerCase().equals("busy")) {
	            	server.setLongRunningCall(true);
	            } else if (input.length() == 0) {
	            	done = true;
	            }
            }
            registry.unbind(SERVICE_NAME);
            System.out.println("DemoServer exiting");
            System.exit(0);
        } catch (Exception e) {
            System.out.println("Exception starting DemoServer: " + e);
        }
    }
    
    public void setLongRunningCall(boolean value) {
    	longRunningCall = value;
    }
    
    @Override
    public String doTask(String[] args) {
        ServerTask task = new ServerTask(logger, longRunningCall);
        task.run();
        return null;
    }
}
