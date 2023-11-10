package org.example;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class ApplicationServiceImpl extends UnicastRemoteObject implements ApplicationService {
    private Map<String, String> userPasswords;
    private Map<String, String> userSessions;
    private Map<String, Long> sessionCreationTimes; // Store session creation times
    private long sessionTimeout = 30 * 60 * 1000; // 30 minutes in milliseconds
    private Timer sessionTimer = new Timer(); // Timer for session expiration

    public ApplicationServiceImpl() throws RemoteException {
        super();
        userPasswords = new HashMap<>();
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
