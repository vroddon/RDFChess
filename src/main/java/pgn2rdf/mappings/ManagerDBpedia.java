package pgn2rdf.mappings;

//JENA
/*import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Literal;*/
import java.io.IOException;
import pgn2rdf.chess.Tutorial;
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
 * Class to make some useful DBpedia queries
 * Ejemplo de consulta contra la dbpedia
 * SELECT ?pais ?lat ?long WHERE {
 * ?pais rdf:type <http://dbpedia.org/ontology/Country>.
 * ?pais geo:lat ?lat FILTER (datatype(?lat) = xsd:float && (?lat-0.2273363048115043) < 0.005 && (-0.2273363048115043-?lat) < 0.005).
 * ?pais geo:long ?long FILTER (datatype(?long) = xsd:float && (?long-78.892578125) < 0.005 && (-78.892578125-?long) < 0.005)
 * }
 * @author Victor Rodriguez Doncel 2011 (bd)
 * @todo the dbpedia sparql endpoint returns "bad gateway)
 * 
 */
public class ManagerDBpedia {

// static String endpoint="http://dbpedia.linkeddata.es:8898/sparql";
 static String endpoint="http://dbpedia.org/sparql";
//   static String endpoint="http://live.dbpedia.org/sparql";

  
    public static void main(String[] args) throws IOException {
        String s = getAbstract("http://dbpedia.org/resource/Viswanathan_Anand");
        System.out.println("Víctor\n"+s);
    }
    
    public static String getLabel(String resource)
    {
        resource=resource.replace(" ","_");
        String prefijos="PREFIX owl: <http://www.w3.org/2002/07/owl#> PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX foaf: <http://xmlns.com/foaf/0.1/> PREFIX dc: <http://purl.org/dc/elements/1.1/> PREFIX : <http://dbpedia.org/resource/> PREFIX dbpedia2: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/> PREFIX skos: <http://www.w3.org/2004/02/skos/core#> PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>\n";
        String sparql = prefijos + "select ?o \n" +
        "where {<"+resource+"> rdfs:label ?o} \n" +
        "LIMIT 100\n" +
        "";
        Query query = QueryFactory.create(sparql);
        QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query);
        try {
            ResultSet results = qexec.execSelect();
            for (;results.hasNext();) {
                QuerySolution qs=results.next();
                return qs.getLiteral("?o").getLexicalForm();
            }
        }catch(Exception e){
            
        }
        return "";
    }
    public static String getThumbnailURL(String resource)
    {
        String prefijos="PREFIX owl: <http://www.w3.org/2002/07/owl#> PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> \n";
        prefijos+="PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX foaf: <http://xmlns.com/foaf/0.1/> PREFIX dc: <http://purl.org/dc/elements/1.1/> PREFIX : <http://dbpedia.org/resource/> PREFIX dbpedia2: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/> PREFIX skos: <http://www.w3.org/2004/02/skos/core#> PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> \n";
        String sparql = prefijos + "select ?o \n" +
        "where {<"+resource+"> dbpedia-owl:thumbnail ?o} \n" +
        "LIMIT 100\n" +
        "";
        Query query = QueryFactory.create(sparql);
        QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query);
        try {
            ResultSet results = qexec.execSelect();
            for (;results.hasNext();) {
                QuerySolution qs=results.next();
                return qs.getResource("?o").toString();
            }
        }catch(Exception e){
            
        }
        return "";        
    }
    public static String getCountry(String resource)
    {
        String prefijos="PREFIX owl: <http://www.w3.org/2002/07/owl#> PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> \n";
        prefijos+="PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX foaf: <http://xmlns.com/foaf/0.1/> PREFIX dc: <http://purl.org/dc/elements/1.1/> PREFIX : <http://dbpedia.org/resource/> PREFIX dbpedia2: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/> PREFIX skos: <http://www.w3.org/2004/02/skos/core#> PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> \n";
        String sparql = prefijos + "select ?o \n" +
        "where {<"+resource+"> dbpedia-owl:country ?o} \n" +
        "LIMIT 100\n" +
        "";
        Query query = QueryFactory.create(sparql);
        QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query);
        try {
            ResultSet results = qexec.execSelect();
            for (;results.hasNext();) {
                QuerySolution qs=results.next();
                return qs.getResource("?o").toString();
            }
        }catch(Exception e){
            
        }
        return "";        
    }    
    public static String getAbstract2(String resource) throws IOException
    {
       
        String test = Tutorial.REST(resource, "", "text/html"); //application/rdf+xml
        return test;
    }
    
    public static String getAbstract(String resource)
    {
        resource = resource.replace(" ", "_");
        String prefijos="PREFIX owl: <http://www.w3.org/2002/07/owl#> PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> \n";
        prefijos+="PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX foaf: <http://xmlns.com/foaf/0.1/> PREFIX dc: <http://purl.org/dc/elements/1.1/> PREFIX : <http://dbpedia.org/resource/> PREFIX dbpedia2: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/> PREFIX skos: <http://www.w3.org/2004/02/skos/core#> PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> \n";
        String sparql = prefijos + "select ?o \n" +
        "where {<"+resource+"> dbpedia-owl:abstract ?o} \n" +
        "LIMIT 100\n" +
        "";
        Query query = QueryFactory.create(sparql);
        QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query);
        String res="";
        try {
            ResultSet results = qexec.execSelect();
            for (;results.hasNext();) {
                QuerySolution qs=results.next();
                Literal l = qs.getLiteral("?o");
                if (res.isEmpty())
                    res=l.getLexicalForm();
                if (l.getLanguage().equals("en"))
                {
                    res=l.getLexicalForm();
                }
            }
        }catch(Exception e){
            
        }
        return res;        
    }    
  }
