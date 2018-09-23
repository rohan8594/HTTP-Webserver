package http.resource;

import http.configuration.*;
import http.request.Request;

import java.net.*;
import java.io.*;
import java.util.*;
//import java.util.regex.Matcher;

public class Resource {

    private String uri;
    private String modifiedUri;
    private String trueAlias;
    private String trueScriptAlias;
    private boolean isScript;
    private HttpdConf config;
    private Set<String> arrOfAliases;
    private Set<String> arrOfScriptAliases;
    private boolean htaccessExists;
    private boolean isDirectory;
    private String directoryUri;
    //private Htaccess htaccess;

    public Resource(String uri, HttpdConf config) {
        this.uri = uri;
        this.config = config;
        this.generateAbsolutePath();
    }

    private void generateAbsolutePath() {
        modifiedUri = uri;
        arrOfAliases = config.getAlias().keySet();
        arrOfScriptAliases = config.getScriptAlias().keySet();

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
        
        //modifiedUri = "public_html\\Test1.txt";
        //modifiedUri = "public_html/index.html";
        //modifiedUri = modifiedUri.replace("/", Matcher.quoteReplacement(File.separator));
    }

    public String absolutePath() {
        return modifiedUri;
    }

    public boolean isScript() {
        return isScript;
    }

    public boolean isProtected() throws IOException {
        String directoryPath = new File(modifiedUri).getParent();
        directoryPath = directoryPath + "/";

        try {
            htaccessExists = new File(directoryPath, config.getAccessFile()).exists();
        } catch (Exception e) {
            e.getMessage();
        }

        if (htaccessExists) {
            //htaccess = new Htaccess();
            return true;
        } else {
            return false;
        }
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