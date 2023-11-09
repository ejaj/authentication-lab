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

public class ACLWriter {
    public static void main(String[] args) {
        String filePath = Constants.ACL_FILE_PATH;
        Path path = Paths.get(filePath);
        if (Files.exists(path)) {
            try {
                Files.delete(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        createFileWithPermissions(filePath); // Create the file with specific permissions

        // Write acl to the file, creating it if it doesn't exist
        Map<String, String> acl = getStringStringMap();
        writeACLToFile(acl, filePath);

    }

    private static Map<String, String> getStringStringMap() {
        Map<String, String> acl = new HashMap<>();
        acl.put("ejaj", "print,queue,topQueue,start,stop,restart,status,readConfig,setConfig");
        acl.put("kazi", "print,queue,topQueue,start,stop,restart,status,readConfig,setConfig");
        acl.put("alice", "print,queue,topQueue,start,stop,restart,status,readConfig,setConfig");
        acl.put("bob", "start,stop,restart,status,readConfig,setConfig");
        acl.put("cecilia", "print,queue,topQueue,restart");
        acl.put("david", "print,queue");
        acl.put("erica", "print,queue");
        acl.put("fred", "print,queue");
        acl.put("george", "print,queue");
        return acl;
    }

    public static void writeACLToFile(Map<String, String> acl, String filePath) {
        try (FileWriter fileWriter = new FileWriter(filePath, true)) {
            for (Map.Entry<String, String> entry : acl.entrySet()) {
                fileWriter.write(entry.getKey() + "=" + entry.getValue() + "\n");
            }
            System.out.println("ACL written to " + filePath);
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
