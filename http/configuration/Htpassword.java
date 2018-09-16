package http.configuration;

import http.configuration.Htaccess;

import java.net.*;
import java.io.*;
import java.util.HashMap;

public class Htpassword {

    private HashMap<String, String> users = new HashMap<>();

    public Htpassword(String filePath) throws IOException {
        this.load(filePath);
    }

    private void load(String filePathStr) throws IOException {

        String line;
        BufferedReader reader = new BufferedReader(new FileReader(filePathStr));

        while((line = reader.readLine()) != null) {
            String[] user = line.split(":");
            users.put(user[0], user[1].replace("{SHA}", ""));
        }
    }

    public HashMap<String, String> getUsers() {
        return users;
    }
}