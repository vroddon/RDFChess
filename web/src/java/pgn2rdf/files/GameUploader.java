package pgn2rdf.files;

import chesspresso.game.Game;
import chesspresso.pgn.PGNReader;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import pgn2rdf.chess.PGNProcessor;
import static pgn2rdf.files.ChessGameIterator.getMoves;
import static pgn2rdf.files.PGNFolderParser.MD5;

/**
 * This class uploads games into the Linked Data server
 *
 * @author Victor
 */
public class GameUploader {

    static Set<String> hashes = new HashSet();

    /**
     * Main method uploads Bobby fischer's games
     */
    public static void main(String[] args) {
//        uploadHugeCollection();
        uploadPGN("samples/London1872.pgn");
        uploadPGN("samples/NewYork1880.pgn");
        uploadPGN("samples/SovietChamp1968.pgn");
        uploadPGN("samples/WorldChamp1886.pgn");
        uploadPGN("samples/WorldChamp1892.pgn");
        uploadPGN("samples/WorldChamp1909.pgn");
        uploadPGN("samples/WorldChamp1921.pgn");
        uploadPGN("samples/WorldChamp1931.pgn");
        uploadPGN("samples/WorldChamp1958.pgn");
        uploadPGN("samples/WorldChamp2012.pgn");
        uploadPGN("samples/WorldChamp2013.pgn");
        uploadPGN("samples/WorldChamp2014.pgn");
        uploadPGN("samples/Fischer.pgn");
        uploadPGN("samples/Kasparov.pgn");
        uploadPGN("samples/Korchnoi.pgn");
        uploadPGN("samples/Keres.pgn");
        uploadPGN("samples/London1851.pgn");
        uploadPGN("samples/Philidor.pgn");
        uploadPGN("samples/Morphy.pgn");
        uploadPGN("samples/Tal.pgn");
        uploadPGN("samples/Tarrasch.pgn");
        uploadPGN("samples/Capablanca.pgn");
        uploadPGN("samples/Alekhine.pgn");
        uploadPGN("samples/Anand.pgn");
        uploadPGN("samples/Botvinnik.pgn");
        uploadPGN("samples/Karpov.pgn");
        uploadPGN("samples/Caruana.pgn");
 //       uploadPGN("samples/Petrosian.pgn");
    }

    private static int uploadPGN(String filename) {
        String ttl = "";
        int conta = 0;
        try {
            String pgn = FileUtils.readFileToString(new File(filename));
            Reader reader = new StringReader(pgn);
            PGNReader pgnreader = new PGNReader(reader, "web");
            Game g = null;
            try {
                while ((g = pgnreader.parseGame()) != null) {
                    StringWriter sw = new StringWriter();
                    Model m = PGNProcessor.pgn2rdf(g);
                    String id = PGNProcessor.getChessId(m);
                    RDFPrefixes.addPrefixesIfNeeded(m);
                    RDFDataMgr.write(sw, m, Lang.TURTLE);
                    String rdf=PGNProcessor.expandRDF(sw.toString());
                    
          //          System.out.println(rdf);
                    
                    RDFStore.writeGame(id, rdf);
                    System.out.println(conta+ " Uploaded " + id);
         //           System.out.println(rdf);
                    conta++;
        //            break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
        }
        return conta;
    }

    private static void uploadHugeCollection() {
        int total = 0;
        Iterator it = new ChessGameIterator();
        while (it.hasNext()) {
            String rdf = (String) it.next();
            if (rdf.isEmpty()) {
                break;
            }
            //This is to correct an error that was made while creating the dataset
            rdf = rdf.replace("http://purl.org/NET/chess", "http://purl.org/NET/rdfchess");

            //http://purl.org/NET/rdfchess/resource/
            rdf = rdf.replace("http://purl.org/NET/rdfchess/resource/", "http://salonica.dia.fi.upm.es:8080/rdfchess/resource/");
//            rdf = rdf.replace("http://purl.org/NET/rdfchess/resource/", "http://lider1.dia.fi.upm.es:8088/rdfchess/resource/");

            try {
                Model model = ModelFactory.createDefaultModel();
                InputStream stream = new ByteArrayInputStream(rdf.getBytes("UTF-8"));
                RDFDataMgr.read(model, stream, Lang.TTL);
                Resource rgamec = model.createResource("http://purl.org/NET/rdfchess/ontology/ChessGame");
                ResIterator rit = model.listSubjectsWithProperty(RDF.type, rgamec);
                Resource rgame = null;
                if (rit.hasNext()) {
                    rgame = rit.next();
                }
                if (rgame != null) {
                    Property cwhite = model.createProperty("http://purl.org/NET/rdfchess/ontology/hasWhitePlayerName");
                    Property cblack = model.createProperty("http://purl.org/NET/rdfchess/ontology/hasBlackPlayerName");
                    Literal lita = null;
                    Literal litb = null;
                    NodeIterator rita = model.listObjectsOfProperty(rgame, cwhite);
                    if (rita.hasNext()) {
                        lita = rita.next().asLiteral();
                    }
                    NodeIterator rit2 = model.listObjectsOfProperty(rgame, cblack);
                    if (rit2.hasNext()) {
                        litb = rit2.next().asLiteral();
                    }

                    System.out.println(lita.toString() + " - " + litb.toString());

                    if (litb.toString().contains("Fischer")) {
                        String moves = getMoves(model, rgame.getURI());
                        String md5 = MD5(moves);
                        int siz = hashes.size();
                        hashes.add(md5);
                        if (hashes.size() - siz == 1) {
                            System.out.println(moves + " " + md5);
                            RDFStore.writeGame(rgame.getURI(), rdf);
                            total++;
                            if (total == 1000) {
                                return;
                            }
                        }
                    }
                }

            } catch (Exception e) {
                System.err.println("Could not be loaded. " + e.toString());
            }
        }
    }
}
