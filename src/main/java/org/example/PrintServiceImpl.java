package org.example;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class PrintServiceImpl extends UnicastRemoteObject implements PrintService {
    private Map<String, String> userAcl;
    private boolean isServerRunning;
    private Map<Integer, String> printQueue;
    private boolean isSessionValid;
    private String userName;


    public PrintServiceImpl() throws IOException {
        super();
        isServerRunning = false;
        printQueue = new HashMap<>();
        isSessionValid = false;
        userAcl = ACLManager.readACLFromFile(Constants.ACL_FILE_PATH);
        userName = "";

    }

    @Override
    public void setCurrentUserName(String userName) throws RemoteException {
        this.userName = userName;
    }

    private boolean hasPermission(String currentUser, String operation) {
        // Check if the current user has permission for the specified operation
        System.out.println("Logged User: " + currentUser);
        String allowedOperations = userAcl.getOrDefault(currentUser, "");
        System.out.println("ACL: " + allowedOperations);
        return allowedOperations.contains(operation);
    }


    @Override
    public void print(String filename, String printer) throws RemoteException {
        if (isSessionValid && isServerRunning) {
            if (hasPermission(userName, "print")) {
                // Implement print logic (e.g., add the job to the print queue)
                printQueue.put(printQueue.size() + 1, filename);
                System.out.println("Print job '" + filename + "' added to the queue for printer: " + printer);
            } else {
                System.out.println("Sorry, You do not have the right to access the print.");
            }
        } else {
            System.out.println("Authentication failed or print server is not running.");
        }
    }

    @Override
    public String queue(String printer) throws RemoteException {
        // Implement logic to list the print queue for the specified printer
        if (hasPermission(userName, "queue")) {
            StringBuilder queueList = new StringBuilder();
            for (Map.Entry<Integer, String> entry : printQueue.entrySet()) {
                queueList.append(entry.getKey()).append("\t").append(entry.getValue()).append("\n");
            }
            System.out.println("You are in the queue.");
            return queueList.toString();
        } else {
            System.out.println("Sorry, You do not have the right to access the print.");
            return "Sorry, You do not have the right to access the print.";
        }

    }

    @Override
    public void topQueue(String printer, int job) throws RemoteException {
        if (isSessionValid && isServerRunning) {
            if (hasPermission(userName, "topQueue")) {
                if (printQueue.containsKey(job)) {
                    String jobDescription = printQueue.get(job);
                    printQueue.remove(job);
                    // Find the highest job number in the queue
                    int highestJobNumber = 1;
                    for (int existingJob : printQueue.keySet()) {
                        if (existingJob > highestJobNumber) {
                            highestJobNumber = existingJob;
                        }
                    }
                    // Place the job at the top of the queue
                    printQueue.put(highestJobNumber + 1, jobDescription);
                    System.out.println("Print job moved to the top of the queue: " + jobDescription);
                } else {
                    System.out.println("Job number not found in the queue.");
                }
            } else {
                System.out.println("Sorry, You do not have the right to access the topQueue of the printerl.");
            }
        } else {
            System.out.println("Authentication failed or print server is not running.");
        }
    }


    @Override
    public void start() throws RemoteException {
        if (hasPermission(userName, "start")) {
            isServerRunning = true;
            System.out.println("Print server started.");
        } else {
            System.out.println("Sorry, You do not have the right to start the printer.");
        }
    }

    @Override
    public void stop() throws RemoteException {
        if (hasPermission(userName, "stop")) {
            isServerRunning = false;
            printQueue.clear();
            System.out.println("Print server stopped and print queue cleared.");
        } else {
            System.out.println("Sorry, You do not have the right to stop printer.");
        }

    }

    @Override
    public void restart() throws RemoteException {
        if (hasPermission(userName, "restart")) {
            stop();
            start();
            System.out.println("Print server restarted.");
        } else {
            System.out.println("Sorry, You do not have the right to restart the printer.");
        }
    }

    @Override
    public String status(String printer) throws RemoteException {
        if (hasPermission(userName, "status")) {
            return "Printer status for " + printer + ": Online";
        } else {
            System.out.println("Sorry, You do not have the right to check the status of printer.");
            return "Sorry, You do not have the right to check the status of printer.";
        }
    }

    @Override
    public String readConfig(String parameter) throws RemoteException {
        if (hasPermission(userName, "readConfig")) {
            return "Configuration parameter '" + parameter + "': Value";
        } else {
            System.out.println("Sorry, You do not have the right to read the configuration of printer.");
            return "Sorry, You do not have the right to read the configuration of printer.";
        }
    }

    @Override
    public void setConfig(String parameter, String value) throws RemoteException {
        if (hasPermission(userName, "setConfig")) {
            System.out.println("Set configuration parameter '" + parameter + "' to: " + value);
        } else {
            System.out.println("Sorry, You do not have the right to setting the configuration of printer.");
        }

    }

    @Override
    public void setSessionValidity(boolean isSessionValid) throws RemoteException {
        this.isSessionValid = isSessionValid;
    }
}
