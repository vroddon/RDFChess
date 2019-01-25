package pgn2rdf.chess;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import pgn2rdf.files.RDFTripleStore;
import pgn2rdf.mappings.DBpediaSpotlight;
import pgn2rdf.mappings.ManagerDBpedia;

/**
 * Class to disambiguate approximate names of chessplayers
 * @author admin
 */
public class ChessPlayerDisambiguator {
    
    private static Map<String, String> jugadores = initJugadores();

    /**
     * Gets the dbpedia URL resource for the given string name.
     * @param player An approximate name for the chess player
     * @return The precise, disambiguated DBpedia resource
     */
    public static String getMappingDBpedia(String player) {
        String dbpedia = jugadores.get(player);
        if (dbpedia == null) {
            System.out.println("Query in external endpoint for " + player);
            dbpedia = DBpediaSpotlight.getDBPediaResource(player, "ChessPlayer", "chess");
            if (dbpedia.equals(player)) {
                int coma=player.indexOf(",");
                if (coma!=-1)
                {
                    String nom = player.substring(coma+2, player.length());
                    String cognom = player.substring(0,coma);
                    String url = "http://dbpedia.org/resource/"+nom+"_"+cognom;
                    url = url.replace(" ", "_");
                    String country=ManagerDBpedia.getAbstract(url);
                    System.out.println(country);
                    if (!country.isEmpty() && country.contains("chess"))
                        dbpedia=url;
                }
            }
            jugadores.put(player, dbpedia);
        }
        return jugadores.get(player);
    }
        //makes some corrections to makehey are
    public static Map<String, String> initJugadores()
    {
        Map<String, String> mapa = new HashMap();
        mapa.put("Timman, Jan H", "http://dbpedia.org/resource/Jan_Timman");
        mapa.put("Steinitz, William", "http://dbpedia.org/resource/Wilhelm_Steinitz");
        mapa.put("Pachman, Ludek", "http://dbpedia.org/resource/Lud%C4%9Bk_Pachman");
        mapa.put("Tal, Mihail", "http://dbpedia.org/resource/Mikhail_Tal");
        mapa.put("Marshall, Frank James", "http://dbpedia.org/resource/Frank_Marshall");
        mapa.put("Kortschnoj, Viktor", "http://dbpedia.org/resource/Viktor_Korchnoi");
        mapa.put("Petrosian, Tigran V", "http://dbpedia.org/resource/Tigran_Petrosian");
        mapa.put("Panno, Oscar", "http://dbpedia.org/resource/Oscar_Panno");
        mapa.put("Loewenthal, Johann Jacob", "http://dbpedia.org/resource/Johann_Löwenthal");
        mapa.put("Benko, Pal C", "http://dbpedia.org/resource/Pal_Benko");
        
        
        
        
        return mapa;
    }
    
    public static String getChessPlayerName(String s)
    {
        String name="";
        String rdf=RDFTripleStore.readGame(s);
        Model model = ModelFactory.createDefaultModel();
        InputStream is = new ByteArrayInputStream(rdf.getBytes(StandardCharsets.UTF_8));
        RDFDataMgr.read(model, is, Lang.TTL);
        NodeIterator nit = model.listObjectsOfProperty(model.createProperty("http://purl.org/NET/rdfchess/ontology/hasName"));
        while (nit.hasNext()) {
            Literal l = nit.next().asLiteral();
            name = l.getLexicalForm();
        }
        if (name.isEmpty())
            name=s;
        return name;
    }
    
    
    public static void main(String[] args) throws IOException {

        String db=ChessPlayerDisambiguator.getMappingDBpedia("Fox, Maurice");
        System.out.println(db);
    }

}
