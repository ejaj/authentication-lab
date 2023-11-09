package org.example;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class ApplicationServer {
    public static void main(String[] args) {
        try {
            ApplicationService applicationService = new ApplicationServiceImpl();

            // Create the RMI registry and bind the service
            LocateRegistry.createRegistry(1099); // Default RMI registry port

            // Enable TLS for RMI
            System.setProperty("javax.net.ssl.keyStore", "serverKeystore.jks");
            System.setProperty("javax.net.ssl.keyStorePassword", "kazi123456");
            System.setProperty("javax.net.ssl.trustStore", "clientTruststore.jks");
            System.setProperty("javax.net.ssl.trustStorePassword", "kazi123456");
            System.setProperty("java.rmi.server.hostname", "localhost");
            System.setProperty("java.rmi.server.useCodebaseOnly", "false");

            Naming.rebind("rmi://localhost/ApplicationService", applicationService);

            System.out.println("Server started and bound as 'ApplicationService'");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}