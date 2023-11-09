package org.example;// PrintService.java

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PrintService extends Remote {
    void print(String filename, String printer) throws RemoteException;

    String queue(String printer) throws RemoteException;

    void topQueue(String printer, int job) throws RemoteException;

    void start() throws RemoteException;

    void stop() throws RemoteException;

    void restart() throws RemoteException;

    String status(String printer) throws RemoteException;

    String readConfig(String parameter) throws RemoteException;

    void setConfig(String parameter, String value) throws RemoteException;

    void setSessionValidity(boolean isSessionValid) throws RemoteException;

    void setCurrentUserName(String userName) throws RemoteException;
}
