package pgn2rdf.servlets;

import java.io.IOException;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.jena.fuseki.embedded.FusekiEmbeddedServer;
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
 *
 * @author vroddon
 */
public class Fuseki {

    public static FusekiEmbeddedServer server = null;

    public static void main(String[] args) throws IOException {

        String endpoint = RDFChess.sparqlq;
        String query = "SELECT *\n"
                + "WHERE {\n"
                + "  {  GRAPH ?graph{  ?x ?p ?o} .} \n"
                + "} LIMIT 100";
        System.out.println(query);

      //  Fuseki.startEmbeddedFuseki("D:\\data\\rdfchess\\nq\\data.nq", "/RDFChess", 3030);
        Fuseki.query(endpoint, query);
        System.out.println("bye bye guy");
    }

    public static void startEmbeddedFuseki(String filename, String endpoint, int port) {
        System.out.println("helo moto");
        //   BasicConfigurator.configure();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
//        Dataset dataset = RDFDataMgr.loadDataset("C:\\Users\\vroddon\\Desktop\\data.rdf");
        Dataset dataset = RDFDataMgr.loadDataset(filename);
        server = FusekiEmbeddedServer.create().setPort(port).add(endpoint, dataset.asDatasetGraph(), true).build();
        stopWatch.split();
        System.out.println("loaded " + stopWatch.toSplitString());
        server.start();
        System.out.println("bye");

    }

    public static void query(String serviceURI, String query) {
        QueryExecution q = QueryExecutionFactory.sparqlService(serviceURI,
                query);
        ResultSet results = q.execSelect();

        while (results.hasNext()) {
            QuerySolution soln = results.nextSolution();
            // assumes that you have an "?x" in your query
            RDFNode x = soln.get("x");
            System.out.println(x);
        }
    }
}
