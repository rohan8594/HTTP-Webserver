import http.configuration.*;
import http.serverworker.Worker;

import java.net.*;
import java.io.*;

public class WebServer {

    public static final String CONFIG_PATH = "conf" + File.separator + "httpd.conf";
    public static final String MIME_TYPES_PATH = "conf" + File.separator + "mime.types";

    public static void start() throws IOException {

        int listenPort;
        HttpdConf config = new HttpdConf(CONFIG_PATH);
        MimeTypes mimes = new MimeTypes(MIME_TYPES_PATH);

        if (config.getPort() != 0) {
            listenPort = config.getPort();
        } else {
            listenPort = 8080;
        }

        ServerSocket socket = new ServerSocket(listenPort);
        System.out.println("Listening at port " + listenPort);

        while (true) {
            Socket client = socket.accept();
            if (client != null) {
                Worker workerThread = new Worker(client, config, mimes);
                workerThread.start();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        WebServer.start();
    }
  
}