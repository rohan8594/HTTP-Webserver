package http.resource;

import http.configuration.*;
import http.request.Request;

import java.net.*;
import java.io.*;
import java.util.*;

public class Resource {

    private String uri;
    private String modifiedUri;
    private String trueAlias;
    private String trueScriptAlias;
    private boolean isScript;
    private HttpdConf config;
    private Set<String> arrOfAliases;
    private Set<String> arrOfScriptAliases;

    public Resource(String uri, HttpdConf config) {
        this.uri = uri;
        this.config = config;
    }

    public String absolutePath() {
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
            modifiedUri = modifiedUri + "index.html";
        }

        return modifiedUri;
    }

    public boolean isScript() {
        return isScript;
    }
}