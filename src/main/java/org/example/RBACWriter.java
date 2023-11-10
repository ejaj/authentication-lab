package org.example;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.HashMap;
import java.util.Map;

public class RBACWriter {
    public static void main(String[] args) {
        String hierarchyRolePath = Constants.ROLE_HIERARCHY_FILE_PATH;
        Path hierarchy_path = Paths.get(hierarchyRolePath);
        if (Files.exists(hierarchy_path)) {
            try {
                Files.delete(hierarchy_path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String userRolePath = Constants.USER_ROLES_FILE_PATH;
        Path user_role_path = Paths.get(userRolePath);
        if (Files.exists(user_role_path)) {
            try {
                Files.delete(user_role_path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String permissionsPath = Constants.PERMISSIONS_FILE_PATH;
        Path permission_path = Paths.get(permissionsPath);
        if (Files.exists(permission_path)) {
            try {
                Files.delete(permission_path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        createFileWithPermissions(hierarchyRolePath);
        createFileWithPermissions(userRolePath);
        createFileWithPermissions(permissionsPath);

        Map<String, String> hierarchy_roles = getHierarchyRolesStringMap();
        writeHierarchyRolesToFile(hierarchy_roles, hierarchyRolePath);

        Map<String, String> user_roles = getUserRolesStringMap();
        writeUserRolesToFile(user_roles, userRolePath);

        Map<String, String> permissions = getPermissionStringMap();
        writePermissionToFile(permissions, permissionsPath);

    }

    private static Map<String, String> getHierarchyRolesStringMap() {
        Map<String, String> hierarchy_roles = new HashMap<>();
        hierarchy_roles.put("Administrator", "ServiceTechnician");
        hierarchy_roles.put("ServiceTechnician", "PowerUser");
        hierarchy_roles.put("PowerUser", "OrdinaryUser");
        return hierarchy_roles;
    }

    private static Map<String, String> getUserRolesStringMap() {
        Map<String, String> user_roles = new HashMap<>();
        user_roles.put("alice", "Administrator");
        user_roles.put("bob", "ServiceTechnician");
        user_roles.put("cecilia", "PowerUser");
        user_roles.put("david", "OrdinaryUser");
        user_roles.put("erica", "OrdinaryUser");
        user_roles.put("fred", "OrdinaryUser");
        user_roles.put("george", "OrdinaryUser");
        return user_roles;
    }

    private static Map<String, String> getPermissionStringMap() {
        Map<String, String> permissions = new HashMap<>();
        permissions.put("Administrator", "print,queue,topQueue,start,stop,restart,status,readConfig,setConfig");
        permissions.put("ServiceTechnician", "start,stop,restart,status,readConfig,setConfig");
        permissions.put("PowerUser", "print,queue,topQueue,restart");
        permissions.put("OrdinaryUser", "print,queue");
        return permissions;
    }


    public static void writeHierarchyRolesToFile(Map<String, String> acl, String filePath) {
        try (FileWriter fileWriter = new FileWriter(filePath, true)) {
            for (Map.Entry<String, String> entry : acl.entrySet()) {
                fileWriter.write(entry.getKey() + ":" + entry.getValue() + "\n");
            }
            System.out.println("Hierarchy Roles Write to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeUserRolesToFile(Map<String, String> acl, String filePath) {
        try (FileWriter fileWriter = new FileWriter(filePath, true)) {
            for (Map.Entry<String, String> entry : acl.entrySet()) {
                fileWriter.write(entry.getKey() + ":" + entry.getValue() + "\n");
            }
            System.out.println("User Roles Write to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writePermissionToFile(Map<String, String> acl, String filePath) {
        try (FileWriter fileWriter = new FileWriter(filePath, true)) {
            for (Map.Entry<String, String> entry : acl.entrySet()) {
                fileWriter.write(entry.getKey() + ":" + entry.getValue() + "\n");
            }
            System.out.println("Permissions Write to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createFileWithPermissions(String filePath) {
        try {
            FileAttribute<?> permissions = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rw-------"));
            Files.createFile(Paths.get(filePath), permissions);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
