package org.example;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class PrintRBACServiceImpl extends UnicastRemoteObject implements PrintRBACService {
    private Map<String, String> hierarchyRoles;
    private Map<String, String> permissions;
    private Map<String, String> userRoles;

    private boolean isServerRunning;
    private Map<Integer, String> printQueue;
    private boolean isSessionValid;
    private String userName;


    public PrintRBACServiceImpl() throws IOException {
        super();
        isServerRunning = false;
        printQueue = new HashMap<>();
        isSessionValid = false;
        hierarchyRoles = RBACManager.readHierarchyRolesFromFile(Constants.ROLE_HIERARCHY_FILE_PATH);
        permissions = RBACManager.readPermissionFromFile(Constants.PERMISSIONS_FILE_PATH);
        userRoles = RBACManager.readUserRoleFromFile(Constants.USER_ROLES_FILE_PATH);
        userName = "";

    }

    private boolean hasPermission(String role, String permission) {
        if (role != null) {
            // Check for the role itself
            if (permissions.containsKey(role) && permissions.get(role).contains(permission)) {
                return true;
            }

            // Check for the subordinate roles in the hierarchy
            String subordinateRole = hierarchyRoles.get(role);
            while (subordinateRole != null) {
                if (permissions.containsKey(subordinateRole) && permissions.get(subordinateRole).contains(permission)) {
                    return true;
                }
                subordinateRole = hierarchyRoles.get(subordinateRole);
            }
        }
        return false;
    }

    @Override
    public void setCurrentUserName(String userName) throws RemoteException {
        this.userName = userName;
    }

    @Override
    public void print(String filename, String printer) throws RemoteException {
        if (isSessionValid && isServerRunning) {

            if (hasPermission(userRoles.get(userName), "print")) {
                // Implement print logic (e.g., add the job to the print queue)
                printQueue.put(printQueue.size() + 1, filename);
                System.out.println("Print job '" + filename + "' added to the queue for printer: " + printer);
            } else {
                System.out.println("Sorry, You do not have the right to access the print.");
            }
        } else {
            System.out.println("Authentication failed or print server is not running");
        }
    }

    @Override
    public String queue(String printer) throws RemoteException {
        // Check if the user has the "queue" permission
        if (hasPermission(userRoles.get(userName), "queue")) {
            // Implement logic to list the print queue for the specified printer
            StringBuilder queueList = new StringBuilder();
            for (Map.Entry<Integer, String> entry : printQueue.entrySet()) {
                queueList.append(entry.getKey()).append("\t").append(entry.getValue()).append("\n");
            }
            System.out.println("You are in the queue.");
            return queueList.toString();
        } else {
            System.out.println("Sorry, you do not have the right to access the queue of the printer.");
            return "Permission denied for viewing the queue.";
        }
    }

    @Override
    public void topQueue(String printer, int job) throws RemoteException {
        if (isSessionValid && isServerRunning) {
            if (hasPermission(userRoles.get(userName), "topQueue")) {
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
                System.out.println("Sorry, you do not have the right to access the top queue of the printer.");
            }
        } else {
            System.out.println("Authentication failed or print server is not running.");
        }
    }


    @Override
    public void start() throws RemoteException {
        if (hasPermission(userRoles.get(userName), "start")) {
            isServerRunning = true;
            System.out.println("Print server started.");
        } else {
            System.out.println("Sorry, You do not have the right to start the printer.");
        }
    }

    @Override
    public void stop() throws RemoteException {
        if (hasPermission(userRoles.get(userName), "stop")) {
            isServerRunning = false;
            printQueue.clear();
            System.out.println("Print server stopped and print queue cleared.");
        } else {
            System.out.println("Sorry, You do not have the right to stop printer.");
        }
    }

    @Override
    public void restart() throws RemoteException {
        if (hasPermission(userRoles.get(userName), "restart")) {
            stop();
            start();
            System.out.println("Print server restarted.");
        } else {
            System.out.println("Sorry, You do not have the right to restart the printer.");
        }

    }

    @Override
    public String status(String printer) throws RemoteException {
        if (hasPermission(userRoles.get(userName), "status")) {
            return "Printer status for " + printer + ": Online";
        } else {
            System.out.println("Sorry, You do not have the right to check the status of printer.");
            return "Sorry, You do not have the right to check the status of printer.";
        }

    }

    @Override
    public String readConfig(String parameter) throws RemoteException {
        if (hasPermission(userRoles.get(userName), "readConfig")) {
            return "Configuration parameter '" + parameter + "': Value";
        } else {
            System.out.println("Sorry, You do not have the right to read the configuration of printer.");
            return "Sorry, You do not have the right to read the configuration of printer.";
        }

    }

    @Override
    public void setConfig(String parameter, String value) throws RemoteException {
        if (hasPermission(userRoles.get(userName), "setConfig")) {
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
