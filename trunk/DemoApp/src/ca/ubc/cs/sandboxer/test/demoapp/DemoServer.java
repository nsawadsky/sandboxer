package ca.ubc.cs.sandboxer.test.demoapp;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

import ca.ubc.cs.sandboxer.test.logger.Logger;
import ca.ubc.cs.sandboxer.core.QuarantineException;
import ca.ubc.cs.sandboxer.core.RuntimeSandboxManager;
import ca.ubc.cs.sandboxer.core.SandboxPolicy;

/**
 * Java RMI server to demonstrate capabilities of sandboxer.
 */
public class DemoServer implements DemoService {
    private static Logger logger;
    private static boolean isQuarantined = false;
    
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
        if (isQuarantined == false) {
            ServerTask task = new ServerTask(logger);
            try {
                task.run();
            } catch (QuarantineException e) {
                System.out.println("Quarantine exception: " + e.getMessage());
                isQuarantined = true;
            }
        }
        return null;
    }
}
