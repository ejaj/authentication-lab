package org.example;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class UserManagement {
    private Map<String, String> usersRoles = new HashMap<>();
    private Map<String, String> users = new HashMap<>();

    public boolean checkUser(String filePath, String username) {
        if (Files.exists(Paths.get(filePath))) {
            readUserFromFile(filePath);
            return users.containsKey(username);
        } else {
            System.out.println("User file not found at: " + filePath);
            return false;
        }
    }

    public void initializeUsersRoles(String filePath) {
        if (Files.exists(Paths.get(filePath))) {
            readUserRolesFromFile(filePath);
        } else {
            System.out.println("User roles file not found at: " + filePath);
        }
    }

    public void addUserRole(String newUser, String newRole) {
        boolean userExists = checkUser(Constants.PASSWORD_FILE_PATH, newUser);
        if (userExists) {
            usersRoles.put(newUser, newRole);
            System.out.println(newUser + " now has the role: " + newRole);
            writeUserRolesToFile(usersRoles, Constants.USER_ROLES_FILE_PATH);
        } else {
            System.out.println(newUser + ", This user not found in the password file list, please create the user first by running PasswordFileHandlerFile");
        }
    }

    public void updateExistingUserRoles(String promotedUser, String newRole, String removeUser) {
        boolean promoted = checkUser(Constants.PASSWORD_FILE_PATH, promotedUser);
        boolean remove = checkUser(Constants.PASSWORD_FILE_PATH, removeUser);

        if (promoted && remove) {
            // Remove the specified user roles
            if (removeUser != null && usersRoles.containsKey(removeUser)) {
                usersRoles.remove(removeUser);
                System.out.println(removeUser + " removed from the roles.");
            }

            if (promotedUser != null && usersRoles.containsKey(promotedUser)) {
                // Remove the specified user roles
                usersRoles.remove(promotedUser);
                System.out.println(promotedUser + " removed from the roles.");
            }

            // Insert the user with the new role
            usersRoles.put(promotedUser, newRole);
            System.out.println(promotedUser + " now has the role: " + newRole);

            // Update the roles in the file
            writeUserRolesToFile(usersRoles, Constants.USER_ROLES_FILE_PATH);
        } else {
            System.out.println(promotedUser + " or " + removeUser + " not found in the password file list. Please create the user first by running PasswordFileHandlerFile");
        }
    }

    public void printUserRoles() {
        for (Map.Entry<String, String> entry : usersRoles.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
    }

    private void readUserRolesFromFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String username = parts[0];
                    String role = parts[1];
                    usersRoles.put(username, role);
                } else {
                    System.err.println("Invalid line in the file: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readUserFromFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String username = parts[0];
                    String role = parts[1];
                    users.put(username, role);
                } else {
                    System.err.println("Invalid line in the file: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeUserRolesToFile(Map<String, String> acl, String filePath) {
        try (FileWriter fileWriter = new FileWriter(filePath, false)) {
            for (Map.Entry<String, String> entry : acl.entrySet()) {
                fileWriter.write(entry.getKey() + ":" + entry.getValue() + "\n");
            }
            System.out.println("User Roles Write to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        UserManagement userManagement = new UserManagement();
        userManagement.initializeUsersRoles(Constants.USER_ROLES_FILE_PATH);

        // Call the method with and without the removeUser parameter
        userManagement.updateExistingUserRoles("george", Constants.SERVICE_TECHNICIAN, "bob");
        userManagement.printUserRoles();

        userManagement.addUserRole("henry", Constants.ORDINARY_USER);
        userManagement.addUserRole("ida", Constants.POWER_USER);
        userManagement.printUserRoles();
    }
}
