package pgn2rdf.chess;

import chesspresso.game.Game;
import chesspresso.move.Move;
import chesspresso.pgn.PGNReader;
import chesspresso.pgn.PGNWriter;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import pgn2rdf.files.RDFStore;
import pgn2rdf.mappings.DBpediaSpotlight;
import pgn2rdf.mappings.ManagerGeonames;

/**
 * This class parses and processes PGN files. 
 * It makes extensive use of the Chesspresso library. http://www.chesspresso.org/
 * Chesspresso is GNU Library or Lesser General Public License version 2.0 (LGPLv2). 
 * @author vroddon
 */
public class PGNProcessor {

    /**
     * Enrichs a PGN game linking some of its values to some external Linked Data resources
     * @param pgn String with a PGN game
     * @return String with a PGN game, where the values have been replaced by well known URIs
     */
    public static String enrichPGN(String pgn) {
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
                g.setTag("White", blanco);
                g.setTag("Black", negro);
                g.setTag("Site", site);
                
                String sx = eco + " " + ChessECOManager.getName(eco)+ " ";
                String ecouri = DBpediaSpotlight.getDBPediaResource(sx, "", "");
                if (ecouri.equals(sx))
                    ecouri="";
                g.setTag("ECO", sx + ecouri);
                pw.write(g.getModel());
            }
        } catch (Exception e) {
            return "Ooops! I don't understand the input format. " + e.getMessage();
        }
        return sw.toString();
    }
    
    /**
     * Transforms a PGN into the RDF pattern proposed
     * @param pgn String with a PGN game
     * @lang A RDF flavor. Example: Lang.TTL, Lang.NTRIPLES
     */
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
                Model m = PGNProcessor.pgn2rdf(g);
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
        modelo.setNsPrefix("chess-o", RDFChess.ONTOLOGY_URI);
        modelo.setNsPrefix("chess", RDFChess.DATA_URI);
        RDFDataMgr.write(sw, modelo, lang);
        return sw.toString();       
    }
    
    //C:\Users\vroddon\AppData\Roaming\NetBeans\8.0.2\config\GF_4.1\domain1\config\log1.txt
    /**
     * Writes a PGN file into a file
     * @param pgn PGN game
     * @param filename Filename
     */
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

    /***************** PRIVATE METHODS *************************************/
    
    private static Model pgn2rdf(Game g) {

        Model modelo = ModelFactory.createDefaultModel();
        modelo.setNsPrefix("dct", "http://purl.org/dc/terms/");
        modelo.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        modelo.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        modelo.setNsPrefix("ldr", "http://purl.oclc.org/NET/ldr/ns#");
        modelo.setNsPrefix("void", "http://rdfs.org/ns/void#");
        modelo.setNsPrefix("dcat", "http://www.w3.org/ns/dcat#");
        modelo.setNsPrefix("gr", "http://purl.org/goodrelations/");
        modelo.setNsPrefix("prov", "http://www.w3.org/ns/prov#");

        String id = UUID.randomUUID().toString();
        Resource r = modelo.createResource(RDFChess.DATA_URI + id);
        Resource r2 = modelo.createResource("http://purl.org/NET/rdfchess/ontology/ChessGame");
        modelo.add(r, RDF.type, r2);
        Property r96 = modelo.createProperty("http://purl.org/NET/rdfchess/ontology/hasWhitePlayerName");
        Property r97 = modelo.createProperty("http://purl.org/NET/rdfchess/ontology/hasBlackPlayerName");
        Property r98 = modelo.createProperty("http://purl.org/NET/rdfchess/ontology/hasWhitePlayer");
        Property r99 = modelo.createProperty("http://purl.org/NET/rdfchess/ontology/hasBlackPlayer");

        RDFNode rdfwhite = null;
        String swhite = g.getWhite();
        if (swhite.startsWith("http")) {
            rdfwhite = modelo.createResource(swhite);
            modelo.add(r, r98, rdfwhite);
        } else {
            rdfwhite = modelo.createLiteral(swhite);
            modelo.add(r, r96, rdfwhite);
        }
        RDFNode rdfblack = null;
        String sblack = g.getBlack();
        if (sblack.startsWith("http")) {
            rdfblack = modelo.createResource(sblack);
            modelo.add(r, r98, rdfwhite);
            
        } else {
            rdfblack = modelo.createLiteral(sblack);
            modelo.add(r, r97, rdfblack);
            
        }
        RDFNode rdfsite = null;
        String ssite = g.getSite();
        if (ssite.startsWith("http")) {
            rdfsite = modelo.createResource(ssite);
        } else {
            rdfsite = modelo.createLiteral(ssite);
        }        

        modelo.add(r, RDF.type, r2);
        
        Property r7 = modelo.createProperty("http://semanticweb.cs.vu.nl/2009/11/sem/subEventOf");
        
        String sround = g.getRound();
        Resource r8 = modelo.createResource("http://purl.org/NET/chess/ontology/roundOfChessCompetition");
        Resource r9 = modelo.createResource("http://purl.org/NET/chess/ontology/round"+UUID.randomUUID().toString());
        modelo.add(r9, RDF.type, r8);
        modelo.add(r9, RDFS.label, sround);
        modelo.add(r, r7, r9);

        Resource r10 = modelo.createResource("http://purl.org/NET/chess/ontology/ChessCompetition");
        Resource r11 = modelo.createResource("http://purl.org/NET/chess/ontology/atChessCompetition"+UUID.randomUUID().toString());
        modelo.add(r11, RDF.type, r10);
        modelo.add(r9, r7, r11);

        Property r12 = modelo.createProperty("http://purl.org/dc/terms/spatial");
        //MAPEO
        r12 = modelo.createProperty("http://purl.org/NET/chess/ontology/hasChessGameAtNamedPlace");
        modelo.add(r, r12, rdfsite);
        
        Property r13 = modelo.createProperty("http://purl.org/NET/chess/ontology/hasECOOpening");
        if (g.getECO()!=null && !g.getECO().isEmpty())
            modelo.add(r, r13, g.getECO());
        
        Property r14 = modelo.createProperty("http://purl.org/NET/rdfchess/ontology/nextHalfMove");
        Resource r15 = modelo.createResource("http://purl.org/NET/rdfchess/ontology/HalfMove");
        Resource r16 = modelo.createResource("http://purl.org/NET/rdfchess/ontology/firstMove");
        Resource r17 = modelo.createResource("http://purl.org/NET/rdfchess/ontology/lastMove");
        Property r18 = modelo.createProperty("http://purl.org/NET/rdfchess/ontology/halfMoveRecord");
        Property r19 = modelo.createProperty("http://purl.org/NET/rdfchess/ontology/hasPGNResult");
        
        g.gotoStart();
        Resource pm = null;
        while (g.hasNextMove())
        {
            Move m = g.getNextMove();
            
            Resource rm = modelo.createResource(RDFChess.DATA_URI + UUID.randomUUID().toString());
            modelo.add(rm, RDF.type, r15);
            modelo.add(rm, r18, m.getSAN());
            if (pm!=null)
            {
                modelo.add(pm, r14, rm);
            }
            else
                modelo.add(rm, RDF.type, r16);
            
            pm=rm;
            g.goForward();
        }
        modelo.add(pm, RDF.type,r17);
        String resultado = (g.getResultStr()==null) ? "" : g.getResultStr();
        modelo.add(r, r19, resultado);
        return modelo;
    }    
    
    public static String uploadRDF(String pgn) {
        String id = RDFStore.write("", pgn);
        return id;
    }
    
    public static String getChessId(Model model)
    {
        String id="";
        Resource r2 = model.createResource("http://purl.org/NET/rdfchess/ontology/ChessGame");
        ResIterator rit = model.listSubjectsWithProperty(RDF.type, r2);
        while(rit.hasNext())
        {
            Resource r = rit.next();
            id=r.getURI();
        }
        return id;
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        String input = new String(Files.readAllBytes(Paths.get("samples/test.pgn")));
        String output = PGNProcessor.getRDF(input, Lang.TTL);
        PrintWriter out = new PrintWriter("samples/test.ttl");
        out.println(output);
    }

    

}
