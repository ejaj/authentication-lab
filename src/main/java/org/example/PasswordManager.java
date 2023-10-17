package org.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import static java.util.Base64.*;

public class PasswordManager {
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            return getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 hashing algorithm not available.");
        }
    }

    public static boolean validatePassword(String plainPassword, String hashedPassword) {
        String hashedInput = hashPassword(plainPassword);
        return hashedInput.equals(hashedPassword);
    }

    public static void updatePasswordInFile(String username, String newPassword, String filePath) {
        Map<String, String> passwords = readPasswordsFromFile(filePath);

        if (passwords.containsKey(username)) {
            passwords.put(username, newPassword);
            PasswordFileHandler.writePasswordsToFile(passwords, filePath);
            System.out.println("Password updated for " + username);
        } else {
            System.out.println("User not found in the password file.");
        }
    }

    public static Map<String, String> readPasswordsFromFile(String filePath) {
        Map<String, String> passwords = new HashMap<>();

        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            System.err.println("File not found or access is protected. " + filePath);
            return passwords;
        }

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String username = parts[0];
                    String hashedPassword = parts[1];
                    passwords.put(username, hashedPassword);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return passwords;
    }

    public static String getPasswordForUser(String username, String filePath) {
        Map<String, String> passwords = readPasswordsFromFile(filePath);
        return passwords.get(username);
    }
}
