package http.response;

import http.request.Request;
import http.resource.Resource;
import java.io.*;

public class ResponseFactory {
    public ResponseFactory(){}
    
    public Response getResponse(Request request, Resource resource)
    {
        if(resource.isProtected())
        {
            /*
            if 401 auth header check
            {
                response.setCode(401);
                response.setReasonPhrase("Unauthorized");
                return response;
            }
            if 403 valid password check
            {
                response.setCode(403);
                response.setReasonPhrase("Forbidden");
                return response;
            }
            */
        }
        
        File path = new File(resource.absolutePath());
        if(!path.exists())
        {
            return notFound(resource);
        }
        
        if(resource.isScript())
        {
            return scriptRequest(request, resource);
        }
        
        switch (request.getVerb()) {
            case "PUT":
                return PUTrequest(request, resource);
            case "DELETE":
                return DELETErequest(resource);
            case "POST":
                return POSTrequest(request, resource);
            case "GET":
                return GETrequest(request, resource);
            case "HEAD":
                return HEADrequest(request, resource);
            default:
                return error500(resource);
        }
    }
    
    private Response notFound(Resource resource)
    {
        Response response = new Response(resource);
        response.setCode(404);
        response.setReasonPhrase("Not Found");
        return response;
    }
    
    private Response scriptRequest(Request request, Resource resource)
    {
        Response response = new Response(resource);
        
        try 
            {
                //HANDLE IN SEPERATE CLASS
                ProcessBuilder processBuilder = new ProcessBuilder();
                processBuilder.directory(new File(resource.absolutePath()));
                //need to convert all headers in request to environment variable
                processBuilder.command();
                Process process = processBuilder.start();
                
                response.setCode(200);
                response.setReasonPhrase("OK");
                return response;
            } 
            catch (IOException e) 
            {
                return error500(resource);
            }
    }
    
    private Response PUTrequest(Request request, Resource resource)
    {
        Response response = new Response(resource);
        //generate 201 for new file, 200 for replace old file, 204 for replace old file with empty file
        //body into stdin for server scripts?
        response.setCode(201);
        response.setReasonPhrase("Created");
        response.addHeader("Content-Location: " + resource.absolutePath());
        
        return response;
    }
    
    private Response DELETErequest(Resource resource)
    {
        Response response = new Response(resource);
        File path = new File(resource.absolutePath());
        
        if(path.delete())
        {
            response.setCode(204);
            response.setReasonPhrase("No Content");
        }
        else
        {
            response.setCode(500);
            response.setReasonPhrase("Internal Server Error");
        }
        
        return response;
    }
    
    private Response POSTrequest(Request request, Resource resource)
    {
        Response response = new Response(resource);
        //Send data to server
        response.setCode(200);
        response.setReasonPhrase("OK");
        
        return response;
    }
    
    private Response GETrequest(Request request, Resource resource)
    {
        Response response = new Response(resource);
        //return 304 if unmodified in client's cache, 
        //otherwise replace with request body and return 200
        //compare last modified request header to file
        if()
        response.setCode(200);
        response.setReasonPhrase("OK");
        response.setCode(304);
        response.setReasonPhrase("Not Modified");
        
        return response;
    }
    
    private Response HEADrequest(Request request, Resource resource)
    {
        Response response = new Response(resource);
        //only send headers of get request, not body
        response.setCode(200);
        response.setReasonPhrase("OK");
        
        return response;
    }
    
    private Response error500(Resource resource)
    {
        Response response = new Response(resource);
        response.setCode(500);
        response.setReasonPhrase("Internal Server Error");
        
        return response;
    }
}
