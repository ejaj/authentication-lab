package org.example;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class ApplicationServiceImpl extends UnicastRemoteObject implements ApplicationService {
    private Map<String, String> userPasswords;
    private Map<Integer, String> printQueue;
    private boolean isServerRunning;

    private Map<String, String> userSessions;
    private Map<String, Long> sessionCreationTimes; // Store session creation times
    private long sessionTimeout = 30 * 60 * 1000; // 30 minutes in milliseconds
    private Timer sessionTimer = new Timer(); // Timer for session expiration

    public ApplicationServiceImpl() throws RemoteException {
        super();
        userPasswords = new HashMap<>();
        printQueue = new HashMap<>();
        isServerRunning = false;
        userSessions = new HashMap<>();
        sessionCreationTimes = new HashMap<>();

        // Initialize user passwords (you would load these from a secure source)
        // userPasswords.put("kazi", PasswordHash.hashPassword("123456"));
        userPasswords = PasswordManager.readPasswordsFromFile(Constants.PASSWORD_FILE_PATH);

        // Start a timer to periodically check and expire sessions
        sessionTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                expireSessions();
            }
        }, sessionTimeout, sessionTimeout);
    }

    @Override
    public boolean authenticate(String username, String password) throws RemoteException {
        // Check if the provided password matches the stored hash
        String storedHash = userPasswords.get(username);
        if (storedHash != null && PasswordManager.validatePassword(password, storedHash)) {
            // Generate and return a session token
            String sessionToken = generateSessionToken();
            userSessions.put(username, sessionToken);
            sessionCreationTimes.put(sessionToken, System.currentTimeMillis()); // Store session creation time
            return true; // Return true for successful authentication
        }
        return false; // Return false for failed authentication
    }


    @Override
    public void print(String sessionToken, String filename, String printer) throws RemoteException {
        if (isSessionValid(sessionToken) && isServerRunning) {
            // Implement print logic (e.g., add the job to the print queue)
            printQueue.put(printQueue.size() + 1, filename);
            System.out.println("Print job '" + filename + "' added to the queue for printer: " + printer);
        } else {
            System.out.println("Authentication failed or print server is not running.");
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
    public void topQueue(String sessionToken, String printer, int job) throws RemoteException {
        if (isSessionValid(sessionToken) && isServerRunning) {
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
            System.out.println("Authentication failed or print server is not running.");
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

    @Override
    public boolean isSessionValid(String sessionToken) throws RemoteException {
        // Check if the session token is valid (not expired)
        Long creationTime = sessionCreationTimes.get(sessionToken);
        if (creationTime != null) {
            long currentTime = System.currentTimeMillis();
            return currentTime - creationTime <= sessionTimeout;
        }
        return false;
    }

    // Helper method to generate a session token
    private String generateSessionToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public String getSessionToken(String username) throws RemoteException {
        String sessionToken = userSessions.get(username);
        if (sessionToken != null) {
            sessionCreationTimes.put(sessionToken, System.currentTimeMillis()); // Update the creation time
        }
        return sessionToken;
    }

    // Helper method to expire sessions
    private void expireSessions() {
        long currentTime = System.currentTimeMillis();
        Iterator<Map.Entry<String, Long>> iterator = sessionCreationTimes.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Long> entry = iterator.next();
            if (currentTime - entry.getValue() > sessionTimeout) {
                iterator.remove();
            }
        }
    }
}
