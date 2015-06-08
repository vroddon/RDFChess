package pgn2rdf.files;

import com.hp.hpl.jena.query.DatasetAccessor;
import com.hp.hpl.jena.query.DatasetAccessorFactory;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

/**
 * Accesor methods for the RDF Chess Fuseki data store
 * @author vroddon
 */
public class RDFStore {
    
    /**
     * Reads a chess game from the store
     */
    public static String read(String gameid)
    {
        String serviceURI = "http://localhost:3030/RDFChess/data";
        DatasetAccessor dataAccessor = DatasetAccessorFactory.createHTTP(serviceURI);
        Model model = dataAccessor.getModel();
        StringWriter sw = new StringWriter();
        RDFDataMgr.write(sw, model, RDFFormat.TURTLE_PRETTY);
        return sw.toString();
    }
    
    /**
     * Writes a chess game in the store
     */
    public static void write(String gameid, String game)
    {
        try{
            //localhost
        String serviceURI = "http://lider1.dia.fi.upm.es:3030/RDFChess/data";
        DatasetAccessor dataAccessor = DatasetAccessorFactory.createHTTP(serviceURI);
        Model model = ModelFactory.createDefaultModel();
        InputStream stream = new ByteArrayInputStream(game.getBytes("UTF-8"));
        RDFDataMgr.read(model, stream, Lang.TTL);       
        /*        
        Resource s = model.createResource("http://ex/s");
        Property p = model.createProperty("http://ex/p");
        Literal o = model.createLiteral("o");
        Resource o2 = model.createResource("http://ex/o2");
        model.add(s,p,o);
        model.add(s,p,o2);
        */
        dataAccessor.putModel(gameid, model);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        
    }    
}
