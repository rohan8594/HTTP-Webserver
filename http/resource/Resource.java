package http.resource;

import http.configuration.*;
import java.io.*;
import java.util.*;

public class Resource {

    private String uri;
    private String modifiedUri;
    private boolean isScript;
    private HttpdConf config;
    private boolean htaccessExists;
    private boolean isDirectory;
    private String directoryUri;

    public Resource(String uri, HttpdConf config) {
        this.uri = uri;
        this.config = config;
        this.generateAbsolutePath();
    }

    private void generateAbsolutePath() {
        Set<String> arrOfAliases;
        Set<String> arrOfScriptAliases;

        modifiedUri = uri;
        arrOfAliases = config.getAlias().keySet();
        arrOfScriptAliases = config.getScriptAlias().keySet();

        getTrueAliases(arrOfAliases, arrOfScriptAliases);

        if (!(modifiedUri.contains(config.getDocRoot())) && (modifiedUri.charAt(0) == '/')) {
            modifiedUri = modifiedUri.replaceFirst("/","");
        }

        if (!(modifiedUri.contains(config.getDocRoot()))) {
            modifiedUri = config.getDocRoot() + modifiedUri;
        }

        if (modifiedUri.charAt(modifiedUri.length() - 1) == '/') {
            this.isDirectory = true;
            this.directoryUri = modifiedUri;
          
            if(config.getDirectoryIndex() != null) {
                modifiedUri = modifiedUri + config.getDirectoryIndex();
            } else {
                modifiedUri = modifiedUri + "index.html";
            }
        }
        else
        {
            this.isDirectory = false;
            this.directoryUri = modifiedUri.substring(0, modifiedUri.lastIndexOf("/"));
        }

    }

    private void getTrueAliases(Set<String> arrOfAliases, Set<String> arrOfScriptAliases) {
        String trueAlias;
        String trueScriptAlias;
        for (String currentAlias : arrOfAliases) {
            if (uri.contains(currentAlias)) {
                trueAlias = config.getAlias().get(currentAlias);
                modifiedUri = uri.replace(currentAlias, trueAlias);
                break;
            }
        }

        for (String currentScriptAlias : arrOfScriptAliases) {
            if (uri.contains(currentScriptAlias)) {
                trueScriptAlias = config.getScriptAlias().get(currentScriptAlias);
                modifiedUri = uri.replace(currentScriptAlias, trueScriptAlias);
                isScript = true;
                break;
            }
        }
    }

    public String absolutePath() {
        return modifiedUri;
    }

    public boolean isScript() {
        return isScript;
    }

    public boolean isProtected() {
        String directoryPath = new File(modifiedUri).getParent();
        directoryPath = directoryPath + "/";

        try {
            htaccessExists = new File(directoryPath, config.getAccessFile()).exists();
        } catch (Exception e) {
            e.getMessage();
        }

        return htaccessExists;
    }

    public HttpdConf getConfig() {
        return config;
    }
    
    public String getDirectory()
    {
        return this.directoryUri;
    }
    
    public boolean isDirectory()
    {
        return this.isDirectory;
    }
}