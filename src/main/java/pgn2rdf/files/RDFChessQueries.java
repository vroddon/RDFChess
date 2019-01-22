package pgn2rdf.files;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import java.util.Iterator;

/**
 *
 * @author vroddon
 */
public class RDFChessQueries {
    
    
    
    
        public static int countChessplayers() {
            
            
        String sparql = "SELECT (COUNT(DISTINCT ?p) AS ?count)\n"
                + "WHERE {\n"
                + "  GRAPH ?g {\n"
                + "    ?p a <http://purl.org/NET/rdfchess/ontology/Agent>\n"
                + "  }\n"
                + "}";
        
        int tot = RDFTripleStore.sparqlInt(sparql);

        return 0;
    }    
}
