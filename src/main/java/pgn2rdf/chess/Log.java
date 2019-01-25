package pgn2rdf.chess;

import java.io.File;
import org.apache.commons.io.FileUtils;

/**
 * Some day I will write a book about the shitty logging frameworks.
 * @author vroddon
 */
public class Log {
    
    public static final String FILENAME =  "/etc/fuseki/rdfchesslog.txt";
    
    public static void log(String msg)
    {
        try{
            FileUtils.writeStringToFile(new File(FILENAME), msg+"\n",true);
        }catch(Exception e23)
        {
        }                
    }
}
