package http.resource;

import http.configuration.HttpdConf;

public class Resource {
    private String AbsolutePath;
    private boolean isScript;
    private boolean isProtected;
    
    public Resource(String uri, HttpdConf config)
    {
        
    }
    
    public boolean isProtected()
    {
        return this.isProtected;
    }
    
    public String absolutePath()
    {
        return this.AbsolutePath;
    }
    
    public boolean isScript()
    {
        return isScript;
    }
}
