package pgn2rdf.files;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import org.apache.commons.io.FileUtils;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import pgn2rdf.chess.PGNProcessor;
import pgn2rdf.chess.RDFChessConfig;

/**
 *
 * @author vroddon
 */
public class RDFJenaStore {

    Model model = null;

    public static void main(String[] args) {
        String folder = RDFChessConfig.get("dumpfolder");
        RDFChessConfig.set("dumpfolder", "D:\\data\\rdfchess\\");
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

                String nt2 = PGNProcessor.expandRDF(sw.toString());
                System.out.println(nt2);
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

}
