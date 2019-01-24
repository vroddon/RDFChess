package pgn2rdf.chess;

/**
 * Some constants in the RDFChess game
 * 
 * @author vroddon
 */
public class RDFChess {

    public static final String DATA_URI = RDFChessConfig.get("datauri","http://vroddon.linkeddata.es/rdfchess/resource/");
    public static final String ONTOLOGY_URI = "http://purl.org/NET/rdfchess/ontology/";
    
//    public static final String sparqlu = "http://localhost:3030/RDFChess/update";
    public static final String sparqlu = "http://localhost:3030/RDFChess/update";
    public static final String sparqlq = "http://localhost:3030/RDFChess/query";

}
