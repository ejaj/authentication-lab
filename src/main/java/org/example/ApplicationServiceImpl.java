package org.example;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class ApplicationServiceImpl extends UnicastRemoteObject implements ApplicationService {
    private Map<String, String> userPasswords;
    private Map<Integer, String> printQueue;
    private boolean isServerRunning;

    public ApplicationServiceImpl() throws RemoteException {
        super();
        userPasswords = new HashMap<>();
        printQueue = new HashMap<>();
        isServerRunning = false;

        // Initialize user passwords (you would load these from a secure source)
        userPasswords.put("kazi", PasswordHash.hashPassword("123456"));
    }

    @Override
    public boolean authenticate(String username, String password) throws RemoteException {
        // Check if the provided password matches the stored hash
        String storedHash = userPasswords.get(username);
        return PasswordHash.validatePassword(password, storedHash);
    }

    @Override
    public void print(String filename, String printer) throws RemoteException {
        if (isServerRunning) {
            // Implement print logic (e.g., add the job to the print queue)
            printQueue.put(printQueue.size() + 1, filename);
            System.out.println("Print job '" + filename + "' added to the queue for printer: " + printer);
        } else {
            System.out.println("Print server is not running. Start the server before printing.");
        }
    }

    @Override
    public String queue(String printer) throws RemoteException {
        // Implement logic to list the print queue for the specified printer
        StringBuilder queueList = new StringBuilder();
        for (Map.Entry<Integer, String> entry : printQueue.entrySet()) {
            queueList.append(entry.getKey()).append("\t").append(entry.getValue()).append("\n");
        }
        return queueList.toString();
    }

    @Override
    public void topQueue(String printer, int job) throws RemoteException {
        // Implement logic to move a print job to the top of the queue
        if (printQueue.containsKey(job)) {
            String jobDescription = printQueue.get(job);
            printQueue.remove(job);
            printQueue.put(1, jobDescription);
            System.out.println("Print job moved to the top of the queue: " + jobDescription);
        }
    }

    @Override
    public void start() throws RemoteException {
        isServerRunning = true;
        System.out.println("Print server started.");
    }

    @Override
    public void stop() throws RemoteException {
        isServerRunning = false;
        printQueue.clear();
        System.out.println("Print server stopped and print queue cleared.");
    }

    @Override
    public void restart() throws RemoteException {
        stop();
        start();
        System.out.println("Print server restarted.");
    }

    @Override
    public String status(String printer) throws RemoteException {
        // Implement logic to retrieve the status of the specified printer
        // This can include information like the printer's current status and available paper/ink levels.
        return "Printer status for " + printer + ": Online";
    }

    @Override
    public String readConfig(String parameter) throws RemoteException {
        // Implement logic to read configuration parameters from the print server
        // You can have a map or configuration file for storing these settings.
        return "Configuration parameter '" + parameter + "': Value";
    }

    @Override
    public void setConfig(String parameter, String value) throws RemoteException {
        // Implement logic to set configuration parameters in the print server
        // You can update the map or configuration file with the new values.
        System.out.println("Set configuration parameter '" + parameter + "' to: " + value);
    }
}
