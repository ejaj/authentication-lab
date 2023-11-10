package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RBACManager {
    public static Map<String, String> readHierarchyRolesFromFile(String filePath) {
        Map<String, String> hierarchyRoles = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                String role = parts[0].trim();
                String subordinateRole = parts[1].trim();
                hierarchyRoles.put(role, subordinateRole);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hierarchyRoles;
    }

    public static Map<String, String> readPermissionFromFile(String filePath) {
        Map<String, String> permissions = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                String role = parts[0].trim();
                String permission = parts[1].trim();
                permissions.put(role, permission);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return permissions;
    }

    public static Map<String, String> readUserRoleFromFile(String filePath) {
        Map<String, String> userRoles = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                String user = parts[0].trim();
                String role = parts[1].trim();
                userRoles.put(user, role);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userRoles;
    }
}
