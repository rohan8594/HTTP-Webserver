package http.configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class HttpdConf {

    private static final String SERVER_ROOT = "ServerRoot";
    private static final String DOCUMENT_ROOT = "DocumentRoot";
    private static final String PORT = "Listen";
    private static final String LOG_FILE = "LogFile";
    private static final String SCRIPT_ALIAS = "ScriptAlias";
    private static final String ALIAS = "Alias";
    private static final String ACCESS_FILE_NAME = "AccessFileName";
    private static final String DIRECTORY_INDEX = "DirectoryIndex";

    private int port;
    private String serverRoot;
    private String docRoot;
    private String logFile;
    private String accessFile = ".htaccess";
    private String directoryIndex;
    private HashMap<String, String> scriptAlias = new HashMap<>();
    private HashMap<String, String> alias = new HashMap<>();

    private String fileName;

    public HttpdConf(String fileName) {
        this.fileName = fileName;
        this.load();
    }

    private void load() {

        try {
            String httpdConf_content = new String(Files.readAllBytes(Paths.get(fileName)));
            String[] lines = httpdConf_content.split("\n");

            for(String line : lines) {
                String[] ele = line.split(" ");

                if (ele[0].equals(SERVER_ROOT)) {

                    serverRoot = ele[1].replaceAll("\"", "");

                } else if (ele[0].equals(DOCUMENT_ROOT)) {

                    docRoot = ele[1].replaceAll("\"","");

                } else if (ele[0].equals(PORT)) {

                    port = Integer.parseInt(ele[1]);

                } else if (ele[0].equals(LOG_FILE)) {

                    logFile = ele[1].replaceAll("\"","");

                } else if (ele[0].equals(SCRIPT_ALIAS)) {

                    scriptAlias.put(ele[1], ele[2].replaceAll("\"",""));

                } else if (ele[0].equals(ALIAS)) {

                    alias.put(ele[1], ele[2].replaceAll("\"",""));

                } else if (ele[0].equals(ACCESS_FILE_NAME)) {

                    accessFile = ele[1].replaceAll("\"","");

                } else if (ele[0].equals(DIRECTORY_INDEX)) {

                    directoryIndex = ele[1].replaceAll("\"","");

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getServerRoot() {
        return serverRoot;
    }

    public String getDocRoot() {
        return docRoot;
    }

    public int getPort() {
        return port;
    }

    public String getLogFile() {
        return logFile;
    }

    public HashMap<String, String> getScriptAlias() {
        return scriptAlias;
    }

    public HashMap<String, String> getAlias() {
        return alias;
    }

    public String getAccessFile() {
        return accessFile;
    }

    public String getDirectoryIndex() {
        return directoryIndex;
    }
}