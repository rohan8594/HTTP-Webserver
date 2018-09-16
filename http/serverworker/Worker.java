package http.serverworker;

import http.configuration.*;
import http.request.*;
import http.resource.*;
import http.response.*;

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
            System.out.println("Absolute Path: " + resrc.absolutePath());
            //System.out.println(resrc.isProtected());

            ResponseFactory resfactory = new ResponseFactory();
            Response res = resfactory.getResponse(req, resrc);
            res.sendResponse(client);
            
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}