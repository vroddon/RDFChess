package pgn2rdf.files;

import com.hp.hpl.jena.rdf.model.Model;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import pgn2rdf.chess.RDFChess;

/**
 *
 * @author Victor
 */
public class RDFPrefixes {

    private static Map<String, String> map = new HashMap();

    private static void init() {
        map.put("http://purl.org/dc/terms/", "dct");
        map.put("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf");
        map.put("http://www.w3.org/2000/01/rdf-schema#", "rdfs");
        map.put("http://semanticweb.cs.vu.nl/2009/11/sem/", "sem");
        map.put("http://purl.org/dc/terms/", "dct");
        map.put("http://www.w3.org/2004/02/skos/core#", "skos");
        map.put("http://www.geonames.org/ontology#", "gn");
        
        map.put(RDFChess.ONTOLOGY_URI, "chess-o");
        map.put(RDFChess.DATA_URI, "chess");

    }

    /**
     * Adds only the prefix that are required in a model
     */
    public static Model addPrefixesIfNeeded(Model modelo) {
        if (map.isEmpty()) {
            init();
        }
        StringWriter sw = new StringWriter();
        RDFDataMgr.write(sw, modelo, RDFFormat.TURTLE_PRETTY);
        String ttl = sw.toString();
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            String key = (String) e.getKey();
            if (ttl.contains(key))
            {
                modelo.setNsPrefix((String)e.getValue(), key);
            }
        }
        return modelo;
    }
}
