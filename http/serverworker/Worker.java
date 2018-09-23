package http.serverworker;

import http.configuration.*;
import http.request.*;
import http.resource.*;
import http.response.*;
import http.logger.*;

import java.net.*;
import java.io.*;

public class Worker extends Thread {

    private Socket client;
    private HttpdConf config;
    private MimeTypes mimes;
    private Htaccess htaccess;
    private Htpassword htpassword;

    public Worker(Socket client, HttpdConf config, MimeTypes mimes) {
        this.client = client;
        this.config = config;
        this.mimes = mimes;
    }

    @Override
    public void run() {
        try {
            Request req = new Request(client);

            System.out.println( "-------------------------" );
            System.out.println("Method: " + req.getVerb());
            System.out.println("Uri: " + req.getUri());
            System.out.println("HTTP Version: " + req.getHttpVersion());
            System.out.println("Header: " + req.getHeaders());
            System.out.println( "-------------------------" );

            Resource resrc = new Resource(req.getUri(), config);

            ResponseFactory resFac = new ResponseFactory();
            Response res = resFac.getResponse(req, resrc);
            res.sendResponse(client);

            Logger logFile = new Logger(config.getLogFile());
            logFile.write(req, res, client);
            System.out.println( "-------------------------" );
            
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}