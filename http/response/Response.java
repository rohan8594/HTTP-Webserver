package http.response;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import http.resource.Resource;
import java.time.*;
import java.time.format.DateTimeFormatter;


public class Response {
    private int ResponseCode;
    private String ReasonPhrase;
    private Resource ResponseResource;
    private byte[] Body;
    private ArrayList<String> Headers;
    
    public Response(Resource rsrc)
    {
        this.ResponseResource = rsrc;
        this.Headers = new ArrayList<String>();
        
        Instant instant = Instant.now();
        String formatted = DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneOffset.UTC).format(instant);
        
        Headers.add("Date: " + formatted);
        Headers.add("Server: Rohan & Jake's Server");
    }
    
    public Resource getResource()
    {
        return this.ResponseResource;
    }
    
    public void setCode(int code)
    {
        this.ResponseCode = code;
    }
    
    public int getCode()
    {
        return this.ResponseCode;
    }
    
    public void setReasonPhrase(String reason)
    {
        this.ReasonPhrase = reason;
    }
    
    public void addHeader(String header)
    {
        this.Headers.add(header);
    }
    
    public void setBody(byte[] body)
    {
        this.Body = body;
    }
    
    public void sendResponse(Socket client)
    {
        //generate correct reponse pieces
        try
        {
            PrintWriter output = new PrintWriter(client.getOutputStream());
            
            output.print("HTTP/1.1 " + ResponseCode + " " + ReasonPhrase + "\r\n");
            for(int i = 0; i < Headers.size(); i ++)
            {
                output.print(Headers.get(i) + "\r\n");
            }
            
            if(Body != null)
            {
                output.print("\r\n");
                output.print(Body + "\r\n");
            }
            
            output.flush();
            output.close();
            client.close();
        }
        catch (IOException e)
        {
            
        }
    }
}
