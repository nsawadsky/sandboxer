package ca.ubc.cs.sandboxer.test.demoapp;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface exported by the demo service over Java RMI.
 */
public interface DemoService extends Remote {
    public final static String SERVICE_NAME = "DemoService";
    
    public String doTask(String[] args) throws RemoteException;
}
