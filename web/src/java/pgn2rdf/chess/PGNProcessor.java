package pgn2rdf.chess;

import chesspresso.game.Game;
import chesspresso.game.GameHeaderModel;
import chesspresso.move.Move;
import chesspresso.pgn.PGNReader;
import chesspresso.pgn.PGNWriter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import pgn2rdf.mappings.DBpediaSpotlight;
import pgn2rdf.mappings.ManagerGeonames;

/**
 *
 * @author vroddon
 */
public class PGNProcessor {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        String input = new String(Files.readAllBytes(Paths.get("samples/Steinitz.pgn")));
//        System.out.println(input);
        String output = PGNProcessor.getTTL(input);
        PrintWriter out = new PrintWriter("samples/Steinitz_out.ttl");
        out.println(output);
        
    }

    public static String enrichPGN(String pgn) {
        //log(pgn);
        
//        pgn=pgn.replace("\n{", "{");
//        pgn=pgn.replace("\n}", "}");
//        pgn=pgn.replace("", "");
        
        Reader reader = new StringReader(pgn);
        PGNReader pgnreader = new PGNReader(reader, "web");
        StringWriter sw = new StringWriter();
        PGNWriter pw = new PGNWriter(sw);
        Game g = null;
        try {
            while ((g = pgnreader.parseGame()) != null) {
                String negro = g.getBlack();
                String blanco = g.getWhite();
                String site = g.getSite();
                String eco = g.getECO();
                if (blanco!=null && !blanco.isEmpty())
                    blanco = DBpediaSpotlight.getDBPediaResource(blanco, "/chess/chess_player", "chess");
                if (negro!=null && !negro.isEmpty())
                    negro = DBpediaSpotlight.getDBPediaResource(negro, "/chess/chess_player", "chess");
                
                if (site!=null && !site.isEmpty())
                    site = ManagerGeonames.getMostLikelyResource(site);
//                site = DBpediaSpotlight.getDBPediaResource(site, "/DBpedia:Place", "city location place venue village");
                g.setTag("White", blanco);
                g.setTag("Black", negro);
                g.setTag("Site", site);
                
                String sx = eco + " " + ChessECOManager.getName(eco)+ " ";
                String ecouri = DBpediaSpotlight.getDBPediaResource(sx, "", "");
                if (ecouri.equals(sx))
                    ecouri="";
                
                g.setTag("ECO", sx + ecouri);
                
                //          System.out.println(blanco + "-" + negro + " (" + evento + ")");
                pw.write(g.getModel());
            }
        } catch (Exception e) {
            return "Ooops! I don't understand the input format. " + e.getMessage();
        }
        return sw.toString();
    }
    public static String getRDF(String pgn, org.apache.jena.riot.Lang lang)
    {
         String ttl = "";
        Reader reader = new StringReader(pgn);
        PGNReader pgnreader = new PGNReader(reader, "web");
        StringWriter sw = new StringWriter();
        Game g = null;
        Model modelo = ModelFactory.createDefaultModel();
        try {
            while ((g = pgnreader.parseGame()) != null) {
                Model m = PGNRDF.pgn2rdf(g);
                modelo.add(m);
            }
        }catch(Exception e){}
        
        modelo.setNsPrefix("dct", "http://purl.org/dc/terms/");
        modelo.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        modelo.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        modelo.setNsPrefix("ldr", "http://purl.oclc.org/NET/ldr/ns#");
        modelo.setNsPrefix("void", "http://rdfs.org/ns/void#");
        modelo.setNsPrefix("dcat", "http://www.w3.org/ns/dcat#");
        modelo.setNsPrefix("gr", "http://purl.org/goodrelations/");
        modelo.setNsPrefix("prov", "http://www.w3.org/ns/prov#");
        modelo.setNsPrefix("sem", "http://semanticweb.cs.vu.nl/2009/11/sem/");
        modelo.setNsPrefix("chess", "http://purl.org/NET/chess/ontology/");
        modelo.setNsPrefix("ex", "http://purl.org/NET/chess/resource/");
        RDFDataMgr.write(sw, modelo, lang);
        return sw.toString();       
    }

    public static String getTTL(String pgn) {
        return getRDF(pgn, Lang.TTL);

    }
    //C:\Users\vroddon\AppData\Roaming\NetBeans\8.0.2\config\GF_4.1\domain1\config\log1.txt
    public static void log(String pgn, String filename) {
        try {
            File file = new File(filename);
            if (!file.exists()) {
                file.createNewFile();
            }
            System.out.println(file.getAbsolutePath());
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(pgn);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
