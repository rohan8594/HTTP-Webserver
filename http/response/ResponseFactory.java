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
    private MimeTypes mimes;

    public ResponseFactory(MimeTypes mime)
    {
        this.mimes = mime;
    }

    public Response getResponse(Request request, Resource resource)
    {
        try {
            if(resource.isProtected()) {
                htaccess = new Htaccess(resource);
                htpassword = new Htpassword(htaccess.getAuthUserFile());

                if (request.getHeaders().containsKey("authorization:")) {

                    String authInfo = request.getHeaders().get("authorization:").get(1);

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
                    response.addHeader("WWW-Authenticate: " + htaccess.getAuthType() + " realm=" + htaccess.getAuthName());
                    return response;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        File path = new File(resource.absolutePath());
        if(!path.exists())
        {
            if(request.getVerb().equals("PUT"))
            {
                path = new File(resource.getDirectory());
                if(path.exists())
                    return PUTrequest(request, resource);
            }
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
                return error400(resource);
        }
    }

    private Response error400(Resource resource) {

        Response response = new Response(resource);
        response.setCode(400);
        response.setReasonPhrase("Bad Request");

        return response;
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

        File script = new File(resource.absolutePath());
        
        try 
            {
                BufferedReader reader = new BufferedReader(new FileReader(script));

                List<String> args;
                args = getArguments(reader, resource);

                ProcessBuilder processBuilder = new ProcessBuilder(args);

                setEnvironmentVariables(request, processBuilder);

                Process process = processBuilder.start();
                OutputStream scriptIn = process.getOutputStream();

                if (request.getBody() != null) {
                    scriptIn.write(request.getBody());
                }

                BufferedReader scriptOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line, scriptResponse = "", scriptHeader = "";
                while((line = scriptOut.readLine()) != null)
                {
                    if (line.equals("")) {
                        scriptResponse += "\r\n";
                        break;
                    }

                    scriptHeader += line;
                    response.addHeader(scriptHeader);
                }

                while((line = scriptOut.readLine()) != null)
                {
                    scriptResponse += line;
                }
                
                response.setIsScript();
                response.setScriptResponse(scriptResponse);
                
                response.setCode(200);
                response.setReasonPhrase("OK");
                return response;
            } 
            catch (IOException e) 
            {
                return error500(resource);
            }
    }

    private void setEnvironmentVariables(Request request, ProcessBuilder processBuilder) {
        processBuilder.environment().put("SERVER_PROTOCOL", "HTTP/1.1");
        if(request.hasQuery())
        {
            processBuilder.environment().put("QUERY_STRING", request.getQuery());
        }
        HashMap<String, ArrayList<String>> map = request.getHeaders();
        for(String key : map.keySet())
        {
            String headerArgs = "";
            for(int i = 0; i < map.get(key).size(); i++)
            {
                headerArgs += " " + map.get(key).get(i).toUpperCase();
            }
            processBuilder.environment().put("HTTP_" + key.toUpperCase(), headerArgs);
        }
    }

    private List<String> getArguments(BufferedReader reader, Resource resource) throws IOException {

        List<String> args = new ArrayList<>();

        String line = reader.readLine();
        String[] scriptPath = line.split(" ");
        String path = scriptPath[0].replace("#!", "");

        args.add(path);
        args.add(scriptPath[1]);
        args.add(resource.absolutePath());
        return args;
    }

    private Response PUTrequest(Request request, Resource resource)
    {
        Response response = new Response(resource);
        File path = new File(resource.absolutePath());

        try
        {
            FileOutputStream outStream = new FileOutputStream(path, false);
            outStream.write(request.getBody());
            outStream.close();
        }
        catch (IOException e)
        {
            return error500(resource);
        }
        
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
        
            File path;
        
        if(resource.isDirectory())
        {
            path = new File(resource.getDirectory() + "NewPostFile.txt");
            response.addHeader("Content-Location: " + resource.getDirectory() + "NewPostFile.txt");
        }
        else
        {
            path = new File(resource.absolutePath());
        }
        
        try
        {
            FileOutputStream outStream = new FileOutputStream(path, true);
            outStream.write(request.getBody());
            outStream.close();
        }
        catch (IOException e)
        {
            return error500(resource);
        }
        
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

        // Caching
        try {

            if(request.getHeaders().containsKey("if-modified-since:"))
            {
                String sentTime = request.getHeaders().get("if-modified-since:").get(0)
                        + " " + request.getHeaders().get("if-modified-since:").get(1)
                        + " " + request.getHeaders().get("if-modified-since:").get(2)
                        + " " + request.getHeaders().get("if-modified-since:").get(3);

                Date requestDate;
                try
                {
                    requestDate = dateFormat.parse(sentTime);
                }
                catch (ParseException e)
                {
                    return error500(resource);
                }

                if(systemDate == requestDate.getTime())
                {
                    response.setCode(304);
                    response.setReasonPhrase("Not Modified");
                    return response;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        int index = resource.absolutePath().indexOf(".");
        String fileType = resource.absolutePath().substring(index + 1);
        String contentType = mimes.lookUp(fileType);
        response.addHeader("Content-Type: " + contentType);
        response.addHeader("Content-Length: " + path.length());
        response.setContentLength(path.length());
        response.setContentLengthPresent(true);
        
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
