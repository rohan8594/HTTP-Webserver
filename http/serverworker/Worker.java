package http.serverworker;

import http.configuration.*;
import http.request.*;

import java.net.*;
import java.io.*;

public class Worker extends Thread {

    private Socket client;
    private HttpdConf config;
    private MimeTypes mimes;

    public Worker(Socket client, HttpdConf config, MimeTypes mimes) {
        this.client = client;
        this.config = config;
        this.mimes = mimes;
    }

    public void run() throws IOException {

    }
}