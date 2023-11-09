package org.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ACLManager {
    public static Map<String, String> readACLFromFile(String filePath) {
        Map<String, String> acl = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=");
                String username = parts[0];
                String operations = parts[1];
                acl.put(username, operations);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return acl;
    }
}
