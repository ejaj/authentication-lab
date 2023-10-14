package org.example;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ApplicationService extends Remote {
    // Authenticate a user and return a boolean indicating success
    boolean authenticate(String username, String password) throws RemoteException;

    // Print a file on the specified printer
    void print(String filename, String printer) throws RemoteException;

    // List the print queue for a given printer
    String queue(String printer) throws RemoteException;

    // Move a print job to the top of the queue
    void topQueue(String printer, int job) throws RemoteException;

    // Start the print server
    void start() throws RemoteException;

    // Stop the print server
    void stop() throws RemoteException;

    // Restart the print server
    void restart() throws RemoteException;

    // Get the status of a printer
    String status(String printer) throws RemoteException;

    // Read a configuration parameter from the print server
    String readConfig(String parameter) throws RemoteException;

    // Set a configuration parameter in the print server
    void setConfig(String parameter, String value) throws RemoteException;
}
