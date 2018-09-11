package http.configuration;

import java.util.HashMap; 
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.ArrayList;

public class MimeTypes {
    HashMap<String, String> Dictionary; 
    String FileName;
    
    public MimeTypes(String fileName)
    {
        this.Dictionary = new HashMap<>();
        this.FileName = fileName;
        this.load();
    }
    
    public void load()
    {
        String line;
        StringTokenizer tokenizer;
        ArrayList<String> tokens;
        
        try
        {
            BufferedReader configReader = new BufferedReader(new FileReader(this.FileName));

            while((line = configReader.readLine()) != null)
            {
                tokenizer = new StringTokenizer(line);
                tokens = new ArrayList();
                
                while(tokenizer.hasMoreTokens())
                {
                    tokens.add(tokenizer.nextToken());
                }
                if(!tokens.isEmpty() && tokens.get(0).charAt(0) != '#')
                {
                    for(int i = 1; i < tokens.size(); i ++)
                    {
                        Dictionary.put(tokens.get(i), tokens.get(0));
                    }
                }
            }
        }
        catch(IOException e)
        {
            System.out.println("Error reading mime.TYPES: " + e);
        } 
    }
    
    public String lookUp(String extension)
    {
        return this.Dictionary.get(extension);
    }
}
