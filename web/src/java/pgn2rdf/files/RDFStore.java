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
import pgn2rdf.chess.RDFChessConfig;

/**
 * Accesor methods for the RDF Chess Fuseki data store
 *
 * @author vroddon
 */
public class RDFStore {

    public static void main(String[] args) throws Exception {

        String ttl = RDFStore.read("http://salonica.dia.fi.upm.es:8080/rdfchess/resource/45bee133-d88c-42e4-89bd-681c81170702");
        System.out.println(ttl);

    }

    /**
     * Reads a chess game from the store
     */
    public static String read(String gameid) {
        String serviceURI = RDFChessConfig.get("fuseki", "http://localhost:3030/RDFChess/data");
        try {
            DatasetAccessor dataAccessor = DatasetAccessorFactory.createHTTP(serviceURI);
            Model model = dataAccessor.getModel(gameid);
            if (model == null) {
                return "The game " + gameid + " does not exist";
            }
            StringWriter sw = new StringWriter();
            RDFDataMgr.write(sw, model, RDFFormat.TURTLE_PRETTY);
            return sw.toString();

        } catch (Exception e) {
            return "The game " + gameid + " could not be loaded as RDF <BR>" + e.getMessage();
        }
    }

    /**
     * Reads a chess game from the store
     */
    public static String readXML(String gameid) {
        String serviceURI = RDFChessConfig.get("fuseki", "http://localhost:3030/RDFChess/data");
        DatasetAccessor dataAccessor = DatasetAccessorFactory.createHTTP(serviceURI);
        Model model = dataAccessor.getModel(gameid);
        StringWriter sw = new StringWriter();
        RDFDataMgr.write(sw, model, RDFFormat.RDFXML);
        return sw.toString();
    }

    /**
     * Writes a chess game in the store
     * @param gameid Game id
     * @param game TTL serialization of a game
     */
    public static void write(String gameid, String game) {
        try {
            String serviceURI = RDFChessConfig.get("fuseki", "http://localhost:3030/RDFChess/data");
            DatasetAccessor dataAccessor = DatasetAccessorFactory.createHTTP(serviceURI);
            Model model = ModelFactory.createDefaultModel();
            InputStream stream = new ByteArrayInputStream(game.getBytes("UTF-8"));
            RDFDataMgr.read(model, stream, Lang.TTL);
            dataAccessor.putModel(gameid, model);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
