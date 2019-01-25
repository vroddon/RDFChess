package pgn2rdf.files;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import org.apache.commons.io.FileUtils;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import pgn2rdf.chess.RDFChessConfig;

/**
 *
 * @author vroddon
 */
public class RDFJenaStore  {

    Model model = null;

    public static void main(String[] args) {
        String folder = RDFChessConfig.get("dumpfolder");
        RDFChessConfig.set("dumpfolder", "D:\\data\\rdfchess\\rawnt");
        folder = RDFChessConfig.get("dumpfolder");
        System.out.println(folder);

        RDFJenaStore jena = new RDFJenaStore();
        jena.init();

        String queryString = "SELECT (COUNT(DISTINCT ?p) AS ?count)\n"
                + "WHERE {\n"
                + "  GRAPH ?g {\n"
                + "    ?p a <http://purl.org/NET/rdfchess/ontology/Agent>\n"
                + "  }\n"
                + "}";
        jena.doQuery(queryString);

        System.out.println(RDFTripleStore.countChessplayers());
    }

    public void init() {
        try {

            Collection<File> files = FileUtils.listFiles(new File(RDFChessConfig.get("dumpfolder")), new String[]{"nt"}, true);
            System.out.println(files.size());
            model = ModelFactory.createMemModelMaker().createModel("rdfchess");
            for (File file : files) {
                System.out.println(file.getName());
 
                Model minimodel = ModelFactory.createMemModelMaker().createModel(file.getName());
                String nt = FileUtils.readFileToString(file, "UTF-8");
                InputStream is = new ByteArrayInputStream(nt.getBytes(StandardCharsets.UTF_8));
                RDFDataMgr.read(minimodel, is, Lang.NT);
                model.add(minimodel);
                is.close();
                
                StringWriter sw = new StringWriter();
                RDFDataMgr.write(sw, minimodel, Lang.TTL);

//                String nt2 = PGNProcessor.expandRDF(sw.toString());
//                System.out.println(nt2);
                if (true)
                    return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doQuery(String queryString) {
        // Create a new query
        try {
            Query query = QueryFactory.create(queryString);
            QueryExecution qe = QueryExecutionFactory.create(query, model);
            ResultSet results = qe.execSelect();
            ResultSetFormatter.out(System.out, results, query);
            qe.close();
        } catch (Exception e) {

        }
    }
    
    
    public int sparqlInt(String queryString)
    {
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query,model);
        ResultSet results = qexec.execSelect();
        for (; results.hasNext();) {
            QuerySolution soln = results.nextSolution();
            Iterator<String> it = soln.varNames();
            while(it.hasNext())
            {
                String col = it.next();
                System.out.println(col);
                Literal literal = soln.getLiteral(col);
                return Integer.parseInt(literal.getLexicalForm());
            }
        }   
        return 0;
    }    


    public String readGame(String gameid) {
        String nt = "";
        String dumpfolder = RDFChessConfig.get("dumpfolder", "E:\\Data\\RDFChess\\");
        File file = new File(RDFChessConfig.get(dumpfolder) + gameid);
        try {
            nt = FileUtils.readFileToString(file, "UTF-8");
        } catch (Exception e) {

        }
        return nt;
    }    

}
