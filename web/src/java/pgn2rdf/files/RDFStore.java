package pgn2rdf.files;

import static com.hp.hpl.jena.enhanced.BuiltinPersonalities.model;
import com.hp.hpl.jena.query.DatasetAccessor;
import com.hp.hpl.jena.query.DatasetAccessorFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import pgn2rdf.chess.PGNProcessor;
import pgn2rdf.chess.RDFChessConfig;

/**
 * Accesor methods for the RDF Chess Fuseki data store
 *
 * @author vroddon
 */
public class RDFStore {

    public static void main(String[] args) throws Exception {

//        String ttl = RDFStore.read("http://salonica.dia.fi.upm.es:8080/rdfchess/resource/45bee133-d88c-42e4-89bd-681c81170702");
//        System.out.println(ttl);
     //   String rdf = RDFStore.readResource("http://salonica.dia.fi.upm.es:8080/rdfchess/resource/9f577224-f63c-4d2f-aa2f-5649ad7aa9be");
     //   System.out.println(rdf);
        listGames();
    }

    public static void listGames() {
        String sresults = "";
        String sparql = "SELECT DISTINCT ?g\n"
                + "WHERE {\n"
                + "  GRAPH ?g {\n"
                + "    ?s ?p ?o\n"
                + "  }\n"
                + "}";
        Query query = QueryFactory.create(sparql);
        String endpoint = "http://localhost:3030/RDFChess/query";
        QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query);
        ResultSet results = qexec.execSelect();
        int conta=9;
        for (; results.hasNext();) {
            QuerySolution soln = results.nextSolution();
            Resource p = soln.getResource("g");       // Get a result variable by name.
            System.out.println(p.toString());
            conta++;
        }
        System.out.println(conta);
        return ;
    }

    /**
     * Serves an arbitrary resource as linked data
     *
     * @param resource A valid URI, for example
     * http://salonica.dia.fi.upm.es:8080/rdfchess/resource/9f577224-f63c-4d2f-aa2f-5649ad7aa9be
     */
    private static String readResource(String resource) {
        String sresults = "";
        String sparql = "SELECT DISTINCT *\n"
                + "WHERE {\n"
                + "  GRAPH ?g {\n"
                + "    <" + resource + "> ?p ?o\n"
                + "  }\n"
                + "}";

        Query query = QueryFactory.create(sparql);
        String endpoint = "http://localhost:3030/RDFChess/query";
        QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query);
//        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        ResultSet results = qexec.execSelect();
        for (; results.hasNext();) {
            QuerySolution soln = results.nextSolution();
            Resource p = soln.getResource("p");       // Get a result variable by name.
            RDFNode o = soln.get("o"); // Get a result variable - must be a resource
            String so = "";
            if (o.isLiteral()) {
                so = "\"" + o.toString() + "\"";
            } else {
                so = "<" + o.toString() + ">";
            }

            sresults += "<" + resource + "> <" + p.toString() + "> " + so + " . \n";
        }

        return sresults;
    }

    /**
     * Reads a chess game from the store
     *
     * @return A String that contains Valid RDF
     */
    public static String readGame(String gameid) {
        String serviceURI = RDFChessConfig.get("fuseki", "http://localhost:3030/RDFChess/data");
        try {
            DatasetAccessor dataAccessor = DatasetAccessorFactory.createHTTP(serviceURI);
            Model model = dataAccessor.getModel(gameid);
            if (model == null) {
                System.out.println("The game " + gameid + " does not exist. Trying to seek for another resource...");
                String ntriples = readResource(gameid);
                if (ntriples.isEmpty()) {
                    return "The resource " + gameid + " does not exist";
                }

                //the requested uri is not a game, but seems to be a valid resource, though. 
                Model modelr = ModelFactory.createDefaultModel();
                InputStream stream = new ByteArrayInputStream(ntriples.getBytes("UTF-8"));
                RDFDataMgr.read(modelr, stream, Lang.NTRIPLES);
                modelr = RDFPrefixes.addPrefixesIfNeeded(modelr);
                StringWriter sw = new StringWriter();
                RDFDataMgr.write(sw, modelr, RDFFormat.TURTLE_PRETTY);
                return sw.toString();

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
     *
     * @param gameid Game id
     * @param game TTL serialization of a game
     */
    public static String writeGame(String gameid, String game) {
        try {
            String serviceURI = RDFChessConfig.get("fuseki", "http://localhost:3030/RDFChess/data");
            DatasetAccessor dataAccessor = DatasetAccessorFactory.createHTTP(serviceURI);
            Model model = ModelFactory.createDefaultModel();
            InputStream stream = new ByteArrayInputStream(game.getBytes("UTF-8"));
            RDFDataMgr.read(model, stream, Lang.TTL);
            if (gameid.isEmpty()) {
                gameid = PGNProcessor.getChessId(model);
            }
            dataAccessor.putModel(gameid, model); //gameid
            return gameid;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }
}
