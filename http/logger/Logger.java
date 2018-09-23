package http.logger;

import http.request.Request;
import http.response.Response;

import java.io.*;
import java.net.*;

public class Logger {
    private String fileName;

    public Logger(String fileName) throws IOException {
        this.fileName = fileName
    }

    public void write(Request request, Response response, Socket client) throws IOException {
        File logFile = new File(fileName);

        if (!logFile.exists()) {
            logFile.createNewFile();
        }

    }
}