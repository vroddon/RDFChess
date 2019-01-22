package pgn2rdf.files;

/**
 * RDFStore interface
 * @author vroddon
 */
public interface RDFStore {
    
    public String readGame(String gameid);
}
