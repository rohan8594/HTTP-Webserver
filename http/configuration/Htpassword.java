package http.configuration;

import http.configuration.Htaccess;

import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Base64;
import java.nio.charset.Charset;
import java.security.MessageDigest;

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

    public boolean isAuthorized(String authInfo) {
        // authInfo is provided in the header received from the client
        // as a Base64 encoded string.
        String credentials = new String(
                Base64.getDecoder().decode(authInfo),
                Charset.forName("UTF-8")
        );

        // The string is the key:value pair username:password
        String[] tokens = credentials.split(":");

        // TODO: implement this
        return verifyPassword(tokens[0], tokens[1]);
    }

    private boolean verifyPassword(String username, String password) {
        // encrypt the password, and compare it to the password stored
        // in the password file (keyed by username)
        // TODO: implement this - note that the encryption step is provided as a
        // method, below
        String encryptedPwd = encryptClearPassword(password);

        if(users.containsKey(username) && users.get(username).equals(encryptedPwd)) {
            return true;
        } else {
            return false;
        }
    }

    private String encryptClearPassword(String password) {
        // Encrypt the cleartext password (that was decoded from the Base64 String
        // provided by the client) using the SHA-1 encryption algorithm
        try {
            MessageDigest mDigest = MessageDigest.getInstance("SHA-1");
            byte[] result = mDigest.digest(password.getBytes());

            return Base64.getEncoder().encodeToString(result);
        } catch(Exception e) {
            return "";
        }
    }
}