package pgn2rdf.servlets;

import java.io.IOException;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.jena.fuseki.FusekiLogging;
import org.apache.jena.fuseki.embedded.FusekiServer;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.RDFDataMgr;
import pgn2rdf.chess.RDFChess;

/**
 * This is an embedded Fuseki answering SPARQL questions.
 * It is actually too slow with reasonably big datasets.
 * @author vroddon
 */
public class Fuseki {

    private static FusekiServer server = null;

    public static void main(String[] args) throws IOException {
       //Shows the subject in the first 100 triples
       String endpoint = RDFChess.sparqlq;
        String query = "SELECT *\n"
                + "WHERE {\n"
                + "  {  GRAPH ?graph{  ?x ?p ?o} .} \n"
                + "} LIMIT 100";
//        FusekiLogging.setLogging();
        Fuseki.startEmbeddedFuseki("c:\\v\\data.nq", "/RDFChess", 3330);
        String ret = Fuseki.query("http://localhost:3330/RDFChess/query", query);
        System.out.println(ret);
    }

    public static void startEmbeddedFuseki(String filename, String endpoint, int port) {
        System.out.println("We are starting Fuseki embedded! fyeah");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Dataset dataset = RDFDataMgr.loadDataset(filename);
        server = FusekiServer.create().setPort(port).add("/RDFChess", dataset).build();
//        server = FusekiServer.create().setPort(port).add(endpoint, dataset.asDatasetGraph(), true).build();
        stopWatch.split();
        System.out.println("loaded " + stopWatch.toSplitString());
        server.start();
    }

    public static String query(String serviceURI, String query) {
        QueryExecution q = QueryExecutionFactory.sparqlService(serviceURI,query);
        ResultSet results = q.execSelect();
        String str="";
        while (results.hasNext()) {
            QuerySolution soln = results.nextSolution();
            // assumes that you have an "?x" in your query
            RDFNode x = soln.get("x");
            str +=x.toString()+"\n";
        }
        return str;
    }
}
