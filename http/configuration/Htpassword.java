package http.configuration;

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

    public boolean isAuthorized(String authInfo) {
        String credentials = new String(
                Base64.getDecoder().decode(authInfo),
                Charset.forName("UTF-8")
        );

        // The credentials string is the key:value pair username:password
        String[] tokens = credentials.split(":");

        return verifyPassword(tokens[0], tokens[1]);
    }

    private boolean verifyPassword(String username, String password) {
        String encryptedPwd = encryptClearPassword(password);

        return (users.containsKey(username) && users.get(username).equals(encryptedPwd));
    }

    private String encryptClearPassword(String password) {
        try {
            MessageDigest mDigest = MessageDigest.getInstance("SHA-1");
            byte[] result = mDigest.digest(password.getBytes());

            return Base64.getEncoder().encodeToString(result);
        } catch(Exception e) {
            return "";
        }
    }
}