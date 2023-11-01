package org.example;

import java.rmi.Naming;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.LogRecord;


public class Client {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";

    public static void main(String[] args) {

        Logger successLogger = Logger.getLogger("SuccessLogger");
        Logger errorLogger = Logger.getLogger("ErrorLogger");

        try {
            // Enable TLS for RMI on the client side
            System.setProperty("javax.net.ssl.trustStore", "clientTruststore.jks");
            System.setProperty("javax.net.ssl.trustStorePassword", "kazi123456");

            ApplicationService applicationService = (ApplicationService) Naming.lookup("rmi://localhost/ApplicationService");
            Scanner scanner = new Scanner(System.in);

            boolean authenticated = false;
            String sessionToken;

            // Create log files and handlers for successful and failed authentication
            FileHandler successFileHandler = new FileHandler("access_log.txt", true);
            FileHandler errorFileHandler = new FileHandler("error_log.txt", true);

            // Set a custom formatter for both loggers
            SimpleFormatter customFormatter = new SimpleFormatter() {
                @Override
                public synchronized String format(LogRecord record) {
                    // Customize the log record format
                    String timestamp = getColoredTimestamp();
                    String message = getColoredMessage(record.getMessage(), ANSI_RESET);
                    return timestamp + " " + message + "\n";
                }
            };

            successFileHandler.setFormatter(customFormatter);
            errorFileHandler.setFormatter(customFormatter);

            // Add the file handlers to the loggers
            successLogger.addHandler(successFileHandler);
            errorLogger.addHandler(errorFileHandler);

            while (!authenticated) {
                System.out.print("Enter username: ");
                String username = scanner.nextLine();

                System.out.print("Enter password: ");
                String password = scanner.nextLine();

                // Authenticate the user
                if (applicationService.authenticate(username, password)) {
                    authenticated = true;
                    successLogger.info(getColoredMessage("[Client] Enter username: " + username, ANSI_GREEN));
                    successLogger.info(getColoredMessage("[Client] Enter password: " + password, ANSI_GREEN));

                    successLogger.info(getColoredMessage("[Server] Authentication successful", ANSI_GREEN));
                    applicationService.start(); // Start the print server

                    // Get the session token for the user
                    sessionToken = applicationService.getSessionToken(username);
                    successLogger.info(getColoredMessage("[Server] Session token" + sessionToken, ANSI_GREEN));

                    // Example print operation
                    String filename = "document.pdf";
                    String printer = "printer1";
                    applicationService.print(sessionToken, filename, printer);
                    successLogger.info(getColoredMessage("[Server] Print job '" + filename + "' added to the queue for printer: " + printer, ANSI_GREEN));

                    // Example listing the print queue for a printer
                    String printerQueue = applicationService.queue(printer);
                    successLogger.info(getColoredMessage("[Server] Print Queue for " + printer + ":\n" + printerQueue, ANSI_GREEN));

                    // Example moving a job to the top of the queue
                    int jobNumber = 1; // Job number to move to the top
                    applicationService.topQueue(sessionToken, printer, jobNumber);
                    successLogger.info(getColoredMessage("[Client] Moving job #" + jobNumber + " to the top of the queue", ANSI_GREEN));

                    // Example reading a configuration parameter
                    String parameter = "printer_paper_size";
                    String configValue = applicationService.readConfig(parameter);
                    successLogger.info(getColoredMessage("[Server] Configuration value for '" + parameter + "': " + configValue, ANSI_GREEN));

                    applicationService.stop(); // Stop the print server
                    successLogger.info(getColoredMessage("[Server] Authentication successful", ANSI_GREEN));
                } else {
                    errorLogger.info(getColoredMessage("[Client] Enter username: " + username, ANSI_RED));
                    errorLogger.info(getColoredMessage("[Client] Enter password: " + password, ANSI_RED));

                    errorLogger.info(getColoredMessage("[Server] Authentication failed", ANSI_RED));
                    System.out.println("Authentication failed");
                }
            }
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper method to get the current timestamp
    private static String getColoredTimestamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return getColoredMessage(dateFormat.format(date), ANSI_RESET);
    }

    private static String getColoredMessage(String message, String color) {
        return color + message + ANSI_RESET;
    }
}
