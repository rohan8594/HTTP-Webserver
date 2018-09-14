package http.response;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import http.resource.Resource;

public class Response {
    private int ResponseCode;
    private String ReasonPhrase;
    private Resource ResponseResource;
    private Byte[] Body;
    private ArrayList<String> Headers;
    
    public Response(Resource rsrc)
    {
        this.ResponseResource = rsrc;
        this.Headers = new ArrayList<String>();
    }
    
    public Resource getResource()
    {
        return this.ResponseResource;
    }
    
    public void setCode(int code)
    {
        this.ResponseCode = code;
    }
    
    public void setReasonPhrase(String reason)
    {
        this.ReasonPhrase = reason;
    }
    
    public void addHeader(String header)
    {
        this.Headers.add(header);
    }
    
    public void setBody(Byte[] body)
    {
        this.Body = body;
    }
    
    public void sendResponse(Socket client)
    {
        //generate correct reponse pieces
        try{
            PrintWriter output = new PrintWriter(client.getOutputStream());
            
            
            output.flush();
            output.close();
            client.close();
        }
        catch (IOException e)
        {
            
        }
    }
}
