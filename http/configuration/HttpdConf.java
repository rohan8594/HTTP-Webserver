package http.configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class HttpdConf {

    public static final String SERVER_ROOT = "ServerRoot";
    public static final String DOCUMENT_ROOT = "DocumentRoot";
    public static final String PORT = "Listen";
    public static final String LOG_FILE = "LogFile";
    public static final String SCRIPT_ALIAS = "ScriptAlias";
    public static final String ALIAS = "Alias";
    public static final String ACCESS_FILE = "AccessFile";
    public static final String DIRECTORY_INDEX = "DirectoryIndex";

    int port;
    String serverRoot;
    String docRoot;
    String logFile;
    String accessFile;
    String directoryIndex;
    HashMap<String, String> scriptAlias = new HashMap<>();
    HashMap<String, String> alias = new HashMap<>();

    String fileName;

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
                } else if (ele[0].equals(ACCESS_FILE)) {
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