import http.configuration.*;
import http.request.*;

import java.net.*;
import java.io.*;

public class WebServer {

    public static final String CONFIG_PATH = "conf" + File.separator + "httpd.conf";
    public static final String MIME_TYPES_PATH = "conf" + File.separator + "mime.types";

    public static void start() throws IOException {

        HttpdConf config = new HttpdConf(CONFIG_PATH);
        MimeTypes mimes = new MimeTypes(MIME_TYPES_PATH);
        ServerSocket socket = new ServerSocket(config.getPort());
        System.out.println("Listening at port " + config.getPort());

        while (true) {
            Socket client = socket.accept();
            if (client != null) {
                Request req = new Request(client);

                System.out.println( "-------------------------" );
                System.out.println("Method: " + req.getVerb());
                System.out.println("Uri: " + req.getUri());
                System.out.println("HTTP Version: " + req.getHttpVersion());
                System.out.println("Header: " + req.getHeaders());
                System.out.println( "-------------------------" );

                client.close();
            }
        }
    }

    public static void main(String[] args) throws IOException {

        WebServer.start();
    }

}