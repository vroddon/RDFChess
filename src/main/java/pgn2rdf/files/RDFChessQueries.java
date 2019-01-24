package pgn2rdf.files;
/*
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;*/
import java.util.Iterator;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;


/**
 *
 * @author vroddon
 */
public class RDFChessQueries {

    RDFStore store = new RDFJenaStore();
    
    
    public int countChessplayers() {
        String sparql = "SELECT (COUNT(DISTINCT ?p) AS ?count)\n"
                + "WHERE {\n"
                + "  GRAPH ?g {\n"
                + "    ?p a <http://purl.org/NET/rdfchess/ontology/Agent>\n"
                + "  }\n"
                + "}";
//        int tot = RDFTripleStore.sparqlInt(sparql);
        int tot = store.sparqlInt(sparql);
        return tot;
    }
}
