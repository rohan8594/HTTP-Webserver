import http.configuration.*;
import http.request.*;

import java.net.*;
import java.io.*;

public class WebServer {

    public static void main(String[] args) throws IOException {

        HttpdConf config = new HttpdConf("conf/httpd.conf");
        MimeTypes mimes = new MimeTypes("conf/mime.types");
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

    /*
    protected static void outputRequest( Socket client ) throws IOException {
        String line;

        BufferedReader reader = new BufferedReader(
                new InputStreamReader( client.getInputStream() )
        );

        while( true ) {
            line = reader.readLine();
            System.out.println( "> " + line );

            // Why do we need to do this?
            if( line.contains( "END" ) ) {
                break;
            }
        }
        System.out.println( "-------------------------" );
    }
    */

}