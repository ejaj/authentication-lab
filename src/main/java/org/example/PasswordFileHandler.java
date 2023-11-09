package org.example;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.HashMap;
import java.util.Map;

public class PasswordFileHandler {

    public static void main(String[] args) {
        String filePath = Constants.PASSWORD_FILE_PATH;
        createFileWithPermissions(filePath); // Create the file with specific permissions
        Map<String, String> passwords = new HashMap<>();
        passwords.put("kazi", PasswordManager.hashPassword("123456"));
        passwords.put("ejaj", PasswordManager.hashPassword("kazi"));

        // Write passwords to the file, creating it if it doesn't exist
        writePasswordsToFile(passwords, filePath);

        // Call readPasswordsFromFile to verify reading passwords
        Map<String, String> loadedPasswords = PasswordManager.readPasswordsFromFile(filePath);
        System.out.println("Loaded passwords: " + loadedPasswords);
    }

    public static void writePasswordsToFile(Map<String, String> passwords, String filePath) {
        try (FileWriter fileWriter = new FileWriter(filePath, true)) {
            for (Map.Entry<String, String> entry : passwords.entrySet()) {
                fileWriter.write(entry.getKey() + ":" + entry.getValue() + "\n");
            }
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
