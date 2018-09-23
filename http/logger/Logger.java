package http.logger;

import http.request.Request;
import http.response.Response;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

public class Logger {
    private String fileName;

    public Logger(String fileName) throws IOException {
        this.fileName = fileName;
    }

    public void write(Request request, Response response, Socket client) throws IOException {
        File logFile = new File(fileName);

        if (!logFile.exists()) {
            logFile.createNewFile();
        }

        String clientIPAddr = client.getInetAddress().toString().replace("/", "");
        String clientName = client.getInetAddress().getHostName();
        SimpleDateFormat dateFortmat = new SimpleDateFormat("dd/M/yyyy:HH:mm:ss Z");
        String date = dateFortmat.format(new Date());
        String requestLine = request.getVerb() + " " + request.getUri() + " " + request.getHttpVersion();
        int statusCode = response.getCode();
        String bytesReturned = "-";

        if (response.getHeaders() != null && response.isContentLengthPresent() == true) {
            bytesReturned = Long.toString(response.getContentLength());
        }

        String outputStr = String.format("%s - %s [%s] \"%s\" %d %s\n", clientIPAddr, clientName, date, requestLine,
                statusCode, bytesReturned);

        //System.out.println(outputStr);
        FileWriter writer = new FileWriter(logFile.getAbsoluteFile(),true);
        BufferedWriter buffWriter = new BufferedWriter(writer);
        buffWriter.write(outputStr);

        buffWriter.close();

    }
}