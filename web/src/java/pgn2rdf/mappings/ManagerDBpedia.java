package pgn2rdf.mappings;

//JENA
import com.hp.hpl.jena.query.*;

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
// static String endpoint="http://dbpedia.org/sparql";
   static String endpoint="http://live.dbpedia.org/sparql";

  
    public static void main(String[] args) {
        System.out.println(getLabel("http://dbpedia.org/resource/Boris_Spassky"));
    }
    
    public static String getLabel(String resource)
    {
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
  }
