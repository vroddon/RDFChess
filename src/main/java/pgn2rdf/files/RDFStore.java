package pgn2rdf.files;

/**
 * RDFStore interface
 * @author vroddon
 */
public interface RDFStore {
    
    public String readGame(String gameid);

    
    public int sparqlInt(String sparql);
    
    /**
     * Launches a SPARQL query and returns an integer.
     */
//    public int sparqlInt(String sparql);
}
