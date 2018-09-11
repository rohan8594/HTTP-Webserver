package http.request;

import java.util.HashMap; 
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Request {
    private String Uri;
    private String Verb;
    private byte[] Body;
    private String httpVersion;
    private HashMap<String, ArrayList<String>> Headers;
    private Socket Client; 
    
    public Request(Socket client)
    {
        this.Client = client;
        this.Headers = new HashMap<>();
        this.parse();
    }
    
    private void parse()
    {
        String line;
        StringTokenizer tokenizer;
        ArrayList<String> tokens;
        boolean body = false;
        boolean requestLine = true;
        
        try
        {
            BufferedReader requestReader = new BufferedReader(new InputStreamReader(Client.getInputStream()));

            while((line = requestReader.readLine()) != null)
            {
                if (!body)
                {
                    tokenizer = new StringTokenizer(line);
                    tokens = new ArrayList();

                    while(tokenizer.hasMoreTokens())
                    {
                        tokens.add(tokenizer.nextToken());
                    }
                    if(requestLine)
                    {
                        this.Verb = tokens.get(0);
                        if(tokens.size() < 3)
                        {
                            this.Uri = null;
                            this.httpVersion = tokens.get(1);
                        }
                        else
                        {
                            this.Uri = tokens.get(1);
                            this.httpVersion = tokens.get(2);
                        }
                    }
                    else if(tokens.isEmpty())
                    {
                        body = true;
                    }
                    else
                    {
                        ArrayList<String> headerValues = new ArrayList<>();
                        for(int i = 1; i < tokens.size(); i++)
                        {
                            headerValues.add(tokens.get(i));
                        }
                        
                        this.Headers.put(tokens.get(0), headerValues);
                    }
                    requestLine = false;
                }
                else if (body)
                {
                    Body = line.getBytes();
                }
            }
        }
        catch(IOException e)
        {
            System.out.println("Error reading parsing request (404):   " + e);
        } 
    }
    
    public String getUri()
    {
        return this.Uri;
    }
    
    public String getVerb()
    {
        return this.Verb;
    }
    
    public byte[] getBody()
    {
        return this.Body;
    }
    
    public String getHttpVersion()
    {
        return this.httpVersion;
    }
    
    public HashMap<String, ArrayList<String>> getHeaders()
    {
        return this.Headers;
    }
}
