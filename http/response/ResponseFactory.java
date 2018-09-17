package http.response;

import http.request.Request;
import http.resource.Resource;
import http.configuration.*;
import java.io.*;
import java.text.*;
import java.util.*;
import http.configuration.MimeTypes;
import java.nio.file.Files;

public class ResponseFactory {

    private Htaccess htaccess;
    private Htpassword htpassword;

    public ResponseFactory(){}

    public Response getResponse(Request request, Resource resource)
    {
        try {
            if(resource.isProtected()) {
                htaccess = new Htaccess(resource);
                htpassword = new Htpassword(htaccess.getAuthUserFile());

                if (request.getHeaders().containsKey("Authorization:")) {

                    String authInfo = request.getHeaders().get("Authorization:").get(0);

                    if(!(htpassword.isAuthorized(authInfo))) {
                        Response response = new Response(resource);
                        response.setCode(403);
                        response.setReasonPhrase("Forbidden");
                        return response;
                    }

                } else {
                    Response response = new Response(resource);
                    response.setCode(401);
                    response.setReasonPhrase("Unauthorized");
                    return response;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
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
        Response response = HEADrequest(request, resource);
        
        if(response.getCode() == 200)
        {
            File path = new File(resource.absolutePath());
            try{
                response.setBody(Files.readAllBytes(path.toPath()));
            }
            catch(IOException e)
            {
                return error500(resource);
            }
        }
        return response;
    }
    
    private Response HEADrequest(Request request, Resource resource)
    {
        Response response = new Response(resource);
        
        File path = new File(resource.absolutePath());
        long systemDate = path.lastModified();
        long HOUR = 3600*1000;
        systemDate = systemDate + HOUR * 7;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        Date returnDate = new Date(systemDate);
        response.addHeader("Last-Modified: " + dateFormat.format(returnDate) + " GMT");
        
        if(request.getHeaders().containsKey("Last-Modified:"));
        {
            String sentTime = request.getHeaders().get("Last-Modified:").get(1)
                    + " " + request.getHeaders().get("Last-Modified:").get(2)
                    + " " + request.getHeaders().get("Last-Modified:").get(3)
                    + " " + request.getHeaders().get("Last-Modified:").get(4);
            
            Date requestDate = new Date();
            try
            {
                requestDate = dateFormat.parse(sentTime);
            }
            catch (ParseException e)
            {
                return error500(resource);
            }
            
            if(systemDate < requestDate.getTime())
            {
                response.setCode(304);
                response.setReasonPhrase("Not Modified");
                return response;
            }
        }
        
        MimeTypes mtype = new MimeTypes("conf" + File.separator + "mime.types");
        int index = resource.absolutePath().indexOf(".");
        String fileType = resource.absolutePath().substring(index + 1);
        String contentType = mtype.lookUp(fileType);
        response.addHeader("Content-Type: " + contentType);
        response.addHeader("Content-Length: " + path.length());
        
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
