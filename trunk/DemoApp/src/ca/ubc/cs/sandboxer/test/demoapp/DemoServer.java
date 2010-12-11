package ca.ubc.cs.sandboxer.test.demoapp;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import ca.ubc.cs.sandboxer.test.logger.Logger;

/**
 * Java RMI server to demonstrate capabilities of sandboxer.
 */
public class DemoServer implements DemoService {
    private static Logger logger;
    
    public static void main(String[] args) {
        DemoServer server = new DemoServer();
    
        try {
            DemoService service = (DemoService)UnicastRemoteObject.exportObject(server, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.bind(SERVICE_NAME, service);
            logger = new Logger("DemoDriverLog.txt");

            System.out.println("DemoServer started, hit Enter to exit ...");
            System.in.read();
            registry.unbind(SERVICE_NAME);
            System.out.println("DemoServer exiting");
            System.exit(0);
        } catch (Exception e) {
            System.out.println("Exception starting DemoServer: " + e);
        }
    }
    
    @Override
    public String doTask(String[] args) {
        WorkerTask task = new WorkerTask(logger);
        task.run();
        return null;
    }

}
