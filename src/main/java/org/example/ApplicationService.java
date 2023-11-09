package org.example;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ApplicationService extends Remote {
    // Authenticate a user and return a boolean indicating success
    boolean authenticate(String username, String password) throws RemoteException;

    boolean isSessionValid(String sessionToken) throws RemoteException;

    String getSessionToken(String username) throws RemoteException;
}
