package org.example;

import java.rmi.Naming;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
            ApplicationService applicationService = (ApplicationService) Naming.lookup("rmi://localhost/ApplicationService");
            Scanner scanner = new Scanner(System.in);

            boolean authenticated = false;

            while (!authenticated) {
                System.out.print("Enter username: ");
                String username = scanner.nextLine();

                System.out.print("Enter password: ");
                String password = scanner.nextLine();

                // String username = "user1";
                // String password = "password1";

                // Authenticate the user
                if (applicationService.authenticate(username, password)) {
                    authenticated = true;
                    System.out.println("Authentication successful.");
                    applicationService.start(); // Start the print server

                    // Example print operation
                    String filename = "document.pdf";
                    String printer = "printer1";
                    applicationService.print(filename, printer);

                    // Example listing the print queue for a printer
                    String printerQueue = applicationService.queue(printer);
                    System.out.println("Print Queue for " + printer + ":\n" + printerQueue);

                    // Example moving a job to the top of the queue
                    int jobNumber = 1; // Job number to move to the top
                    applicationService.topQueue(printer, jobNumber);

                    // Example reading a configuration parameter
                    String parameter = "printer_paper_size";
                    String configValue = applicationService.readConfig(parameter);
                    System.out.println("Configuration value for '" + parameter + "': " + configValue);

                    applicationService.stop(); // Stop the print server
                } else {
                    System.out.println("Authentication failed.");
                }

            }
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
