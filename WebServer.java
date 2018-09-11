import http.configuration.*;
import java.net.*;
import java.io.*;

public class WebServer {

    public static void main(String[] args) throws IOException {

        HttpdConf config = new HttpdConf("conf/httpd.conf");
        MimeTypes mimes = new MimeTypes("conf/mime.types");
        ServerSocket socket = new ServerSocket(config.getPort());

        while (true) {
            Socket client = socket.accept();
            //Worker workerThread = new Worker(client, config, mimes);
            outputRequest( client );
            client.close();
        }
    }

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

}
