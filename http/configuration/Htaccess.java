package http.configuration;

import http.resource.*;

import java.net.*;
import java.io.*;

public class Htaccess {

    private String authUserFile;
    private String authType;
    private String authName;
    private String require;
    private Htpassword htpwd;

    public Htaccess(Resource resource) throws IOException {
        String directoryPath = new File(resource.absolutePath()).getParent();
        String htaccessPath = directoryPath + File.separator + resource.getConfig().getAccessFile();
        this.load(htaccessPath);
    }

    private void load(String htaccessPathStr) throws IOException {
        String line;
        BufferedReader reader = new BufferedReader(new FileReader(htaccessPathStr));

        while ((line = reader.readLine()) != null) {
            String[] elements = line.split(" ", 2);

            if (elements[0].equals("AuthUserFile")) {
                authUserFile = elements[1].replaceAll("\"","");
            }
            else if (elements[0].equals("AuthType")) {
                authType = elements[1];
            }
            else if (elements[0].equals("Require")) {
                require = elements[1];
            }
            else if (elements[0].equals("AuthName")) {
                authName = elements[1].replaceAll("\"","");
            }
        }
    }

    public String getAuthUserFile() {
        return authUserFile;
    }

    public String getAuthType() {
        return authType;
    }

    public String getAuthName() {
        return authName;
    }

    public String getRequire() {
        return require;
    }

}