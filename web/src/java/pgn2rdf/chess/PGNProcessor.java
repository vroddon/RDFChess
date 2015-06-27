package pgn2rdf.chess;

import chesspresso.game.Game;
import chesspresso.move.Move;
import chesspresso.pgn.PGNReader;
import chesspresso.pgn.PGNWriter;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.mem.GraphMem;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.core.DatasetGraphFactory;
import com.hp.hpl.jena.sparql.core.DatasetGraphMaker;
import com.hp.hpl.jena.sparql.core.DatasetImpl;
import com.hp.hpl.jena.update.GraphStore;
import com.hp.hpl.jena.update.GraphStoreFactory;
import com.hp.hpl.jena.update.UpdateAction;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import pgn2rdf.files.RDFPrefixes;
import pgn2rdf.files.RDFStore;
import pgn2rdf.mappings.DBpediaSpotlight;
import pgn2rdf.mappings.ManagerDBpedia;
import pgn2rdf.mappings.ManagerGeonames;

/**
 * This class parses and processes PGN files. It makes extensive use of the
 * Chesspresso library. http://www.chesspresso.org/ Chesspresso is GNU Library
 * or Lesser General Public License version 2.0 (LGPLv2).
 *
 * @author vroddon
 */
public class PGNProcessor {

    /**
     * Enrichs a PGN game linking some of its values to some external Linked
     * Data resources
     *
     * @param pgn String with a PGN game
     * @return String with a PGN game, where the values have been replaced by
     * well known URIs
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
                if (blanco != null && !blanco.isEmpty()) {
                    blanco = DBpediaSpotlight.getDBPediaResource(blanco, "/chess/chess_player", "chess");
                }
                if (negro != null && !negro.isEmpty()) {
                    negro = DBpediaSpotlight.getDBPediaResource(negro, "/chess/chess_player", "chess");
                }

                if (site != null && !site.isEmpty()) {
                    site = ManagerGeonames.getMostLikelyResource(site);
                }
                g.setTag("White", blanco);
                g.setTag("Black", negro);
                g.setTag("Site", site);

                String sx = eco + " " + ChessECOManager.getName(eco) + " ";
                String ecouri = DBpediaSpotlight.getDBPediaResource(sx, "", "");
                if (ecouri.equals(sx)) {
                    ecouri = "";
                }
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
     *
     * @param pgn String with a PGN game
     * @lang A RDF flavor. Example: Lang.TTL, Lang.NTRIPLES
     */
    public static String getRDF(String pgn, org.apache.jena.riot.Lang lang) {
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
        } catch (Exception e) {
        }
        RDFPrefixes.addPrefixesIfNeeded(modelo);
        RDFDataMgr.write(sw, modelo, lang);
        return sw.toString();
    }

    /*
     String sparql ="SELECT DISTINCT ?g\n" +
     "WHERE {\n" +
     "  GRAPH ?g {\n" +
     "    ?s ?p ?o\n" +
     "  }\n" +
     "} ";

     String sparql2="SELECT *\n" +
     "WHERE {\n" +
     "    ?s ?p ?o .\n" +
     "} ";       
     */
    public static String uploadRDF(String pgn) {
        String id = RDFStore.writeGame("", pgn);
        return id;
    }

    private String execSELECT(Dataset dataxet, String sparql) {
        QueryExecution qexec = QueryExecutionFactory.create(QueryFactory.create(sparql), dataxet);
        ResultSet results = qexec.execSelect();
        for (; results.hasNext();) {
            System.out.println(results.next().toString());
        }
        return "";
    }

    /**
     * Applies the view expansion chess:e9fcb74a-301e-4dd4-854a-b98b33554dde
     */
    public static String expandRDF(String rdf) {
        System.out.println("A view expansion is going to take place");
        InputStream is = new ByteArrayInputStream(rdf.getBytes(StandardCharsets.UTF_8));

        //We read the id of the game
        Model model = ModelFactory.createDefaultModel();
        RDFDataMgr.read(model, is, Lang.TTL);
        try {
            is.reset();
        } catch (Exception e) {
        }
        String id = PGNProcessor.getChessId(model);
        System.out.println("The game id is: " + id);

        //We load the game as a graph
        DatasetGraphFactory.GraphMaker maker = new DatasetGraphFactory.GraphMaker() {
            public Graph create() {
                return new GraphMem();
            }
        };
        Dataset dataxet = DatasetImpl.wrap(new DatasetGraphMaker(maker));
        RDFDataMgr.read(dataxet, is, Lang.TTL);
        GraphStore graphStore = GraphStoreFactory.create(dataxet);

        //FIRST EXPANSION, WHITE PLAYER
        String blanco = PGNProcessor.getWhitePlayer(model);
        String dbblanco = PGNProcessor.getMappingDBpedia(blanco);
        
        
        if (!dbblanco.equals(blanco)) {
            String literal = blanco;
            String dbpedia = dbblanco;
            System.out.println("Expanding " + blanco + " to " + dbblanco);
            String newname = ManagerDBpedia.getLabel(dbblanco);
            String idw = RDFChess.DATA_URI + UUID.randomUUID().toString();
            String sparql = "PREFIX chess: <http://purl.org/NET/rdfchess/ontology/>\n"
                    + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                    + "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n"
                    + "DELETE { <" + id + "> chess:hasWhitePlayerName \"" + literal + "\" . }\n"
                    + "INSERT {\n<"
                    + id + "> chess:hasWhitePlayer <" + idw + "> .\n"
                    + "<" + idw + "> rdf:type chess:Agent .\n"
                    + "<" + idw + "> chess:hasName \"" + newname + "\" .\n"
                    + "<" + idw + "> skos:closeMatch <" + dbpedia + "> .\n"
                    + "}\n"
                    + "WHERE { <" + id + "> chess:hasWhitePlayerName \"" + literal + "\" }";
            UpdateAction.parseExecute(sparql, graphStore);         //DROP ALL
        }

        //SECOND EXPANSION, WHITE PLAYER
        String negro = PGNProcessor.getBlackPlayer(model);
        String dbnegro = PGNProcessor.getMappingDBpedia(negro);
        if (!negro.equals(dbnegro)) {
            System.out.println("Expanding " + negro + " to " + dbnegro);
            String literal = negro;
            String dbpedia = dbnegro;
            String newname = ManagerDBpedia.getLabel(dbnegro);
            String idw = RDFChess.DATA_URI + UUID.randomUUID().toString();
            String sparql = "PREFIX chess: <http://purl.org/NET/rdfchess/ontology/>\n"
                    + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                    + "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n"
                    + "DELETE { <" + id + "> chess:hasBlackPlayerName \"" + literal + "\" . }\n"
                    + "INSERT {\n<"
                    + id + "> chess:hasBlackPlayer <" + idw + "> .\n"
                    + "<" + idw + "> rdf:type chess:Agent .\n"
                    + "<" + idw + "> chess:hasName \"" + newname + "\" .\n"
                    + "<" + idw + "> skos:closeMatch <" + dbpedia + "> .\n"
                    + "}\n"
                    + "WHERE { <" + id + "> chess:hasBlackPlayerName \"" + literal + "\" }";
            UpdateAction.parseExecute(sparql, graphStore);         //DROP ALL
        }

        //THIRD EXPANSION, ECO OPENING
        String eco = PGNProcessor.getECO(model);
        String econame = ChessECOManager.getName(eco);
        System.out.println("ECO: " + eco + " " + econame);
        String sx = eco + " " + econame + " ";
        String loc = ChessECOManager.getLibraryOfCongress(eco);

        String dbpedia = PGNProcessor.getMappingDBpediaOpening(sx);
        if (dbpedia.equals(sx)) {
            dbpedia = "";
        }
        String literal = eco;
        String idw = RDFChess.DATA_URI + "opening/" + UUID.randomUUID().toString();
        String sparql = "PREFIX chess: <http://purl.org/NET/rdfchess/ontology/>\n"
                + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n"
                + "DELETE { <" + id + "> <http://purl.org/NET/rdfchess/ontology/hasECOOpening> \"" + literal + "\" . }\n"
                + "INSERT {\n<"
                + id + "> <http://purl.org/NET/rdfchess/ontology/hasChessGameOpening> <" + idw + "> .\n"
                + "<" + idw + "> rdf:type chess:ChessGameOpening .\n"
                + "<" + idw + "> chess:ECOID \"" + eco + "\" .\n"
                + "<" + idw + "> rdfs:label \"" + econame + "\" .\n"
                + "<" + idw + "> skos:closeMatch <" + loc + "> .\n"
                + "<" + idw + "> skos:closeMatch <" + dbpedia + "> .\n"
                + "}\n"
                + "WHERE { <" + id + "> <http://purl.org/NET/rdfchess/rdfontology/hasECOOpening> \"" + literal + "\" }";
        System.out.println("Expanding " + eco + " to " + econame);
        UpdateAction.parseExecute(sparql, graphStore);         //DROP ALL

        
        //FOURTH EXPANSION, GEONAMES
        String site = PGNProcessor.getSite(model);
        String site2 = PGNProcessor.getMappingGeonames(site);
        if (!site.isEmpty() && site2 != null && !site2.isEmpty() && !site2.equals(site)) {
            System.out.println("Expanding " + site + " to " + site2);
            idw = RDFChess.DATA_URI + UUID.randomUUID().toString();
            sparql = "PREFIX chess: <http://purl.org/NET/rdfchess/ontology/>\n"
                    + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                    + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                    + "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n"
                    + "DELETE { <" + id + "> <http://purl.org/NET/rdfchess/rdfontology/hasChessGameAtNamedPlace> \"" + site + "\" . }\n"
                    + "INSERT {\n<"
                    + id + "> <http://purl.org/NET/rdfchess/ontology/atPlace> <" + idw + "> .\n"
                    + "<" + idw + "> rdf:type chess:Place .\n"
                    + "<" + idw + "> chess:hasName \"" + site + "\" .\n"
                    + "<" + idw + "> skos:closeMatch <" + site2 + "> .\n"
                    + "}\n"
                    + "WHERE { <" + id + "> <http://purl.org/NET/rdfchess/ontology/hasChessGameAtNamedPlace> \"" + site + "\" }";
            System.out.println(sparql);
            UpdateAction.parseExecute(sparql, graphStore);
        }

        StringWriter sw = new StringWriter();
        RDFDataMgr.write(sw, dataxet, Lang.NQUADS);

        System.out.println(sw);

        return sw.toString();
    }

    //C:\Users\vroddon\AppData\Roaming\NetBeans\8.0.2\config\GF_4.1\domain1\config\log1.txt
    /**
     * Writes a PGN file into a file
     *
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

    /**
     * *************** PRIVATE METHODS ************************************
     */
    /**
     *
     */
    public static Model pgn2rdf(Game g) {

        Model modelo = ModelFactory.createDefaultModel();

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

                
        Property rtt = modelo.createProperty("http://purl.org/NET/rdfchess/ontology/atTime");
        modelo.add(r, rtt, g.getDate());
        
        
        
        Property r7 = modelo.createProperty("http://semanticweb.cs.vu.nl/2009/11/sem/subEventOf");

        String sround = g.getRound();
        Resource r8 = modelo.createResource("http://purl.org/NET/rdfchess/ontology/roundOfChessCompetition");
        Resource r9 = modelo.createResource(RDFChess.DATA_URI+"round/" + UUID.randomUUID().toString());
        modelo.add(r9, RDF.type, r8);
        modelo.add(r9, RDFS.label, sround);
        modelo.add(r, r7, r9);

        Resource r10 = modelo.createResource("http://purl.org/NET/rdfchess/ontology/ChessCompetition");
        Resource r11 = modelo.createResource(RDFChess.DATA_URI+"chessCompetition/" + UUID.randomUUID().toString());
        
        modelo.add(r11, RDFS.label,g.getEvent());
        
        modelo.add(r11, RDF.type, r10);
        modelo.add(r9, r7, r11);

        Property r12 = modelo.createProperty("http://purl.org/dc/terms/spatial");
        //MAPEO
        r12 = modelo.createProperty("http://purl.org/NET/rdfchess/ontology/hasChessGameAtNamedPlace");
        modelo.add(r, r12, rdfsite);

        Property r13 = modelo.createProperty("http://purl.org/NET/rdfchess/ontology/hasECOOpening");
        if (g.getECO() != null && !g.getECO().isEmpty()) {
            modelo.add(r, r13, g.getECO());
        }

        Property r14 = modelo.createProperty("http://purl.org/NET/rdfchess/ontology/nextHalfMove");
        Resource r15 = modelo.createResource("http://purl.org/NET/rdfchess/ontology/HalfMove");
        Resource r16 = modelo.createResource("http://purl.org/NET/rdfchess/ontology/firstMove");
        Resource r17 = modelo.createResource("http://purl.org/NET/rdfchess/ontology/lastMove");
        Property r18 = modelo.createProperty("http://purl.org/NET/rdfchess/ontology/halfMoveRecord");
        Property r19 = modelo.createProperty("http://purl.org/NET/rdfchess/ontology/hasPGNResult");

        g.gotoStart();
        Resource pm = null;
        while (g.hasNextMove()) {
            Move m = g.getNextMove();

            Resource rm = modelo.createResource(RDFChess.DATA_URI+"halfMove/" + UUID.randomUUID().toString());
            modelo.add(rm, RDF.type, r15);
            modelo.add(rm, r18, m.getSAN());
            if (pm != null) {
                modelo.add(pm, r14, rm);
            } else {
                modelo.add(rm, RDF.type, r16);
            }

            pm = rm;
            g.goForward();
        }
        modelo.add(pm, RDF.type, r17);
        String resultado = (g.getResultStr() == null) ? "" : g.getResultStr();
        modelo.add(r, r19, resultado);
        
        RDFPrefixes.addPrefixesIfNeeded(modelo);
        return modelo;
    }

    /**
     * Builds a PGN file form a Jena Model [Event "F/S Return Match"] [Site
     * "Belgrade , Serbia JUG"] [Date "1992.11.04"] [Round "29"] [White "Fischer
     * , Robert J."] [Black "Spassky , Boris V."] [Result "1/2-1/2"] [ECO "E83"]
     */
    public static String buildPGN(Model model, boolean html) {
        String pgn = "";

        String white = PGNProcessor.getWhitePlayer(model);
        String black = PGNProcessor.getBlackPlayer(model);
        String site = PGNProcessor.getSite(model);
        String game = PGNProcessor.getMoves(model);
        String eco = PGNProcessor.getECO(model);
        String date = PGNProcessor.getDate(model);
        String event = PGNProcessor.getEvent(model);
        String result = PGNProcessor.getResult(model);
        String round = PGNProcessor.getRound(model);
        if (html)
        {
            String wuri = PGNProcessor.getWhitePlayerURI(model);
            if (!wuri.isEmpty())
                white = "<a href=\""+wuri+"\">"+white+"</a>";
            String buri = PGNProcessor.getBlackPlayerURI(model);
            if (!buri.isEmpty())
                black = "<a href=\""+buri+"\">"+black+"</a>";
            String ecouri = PGNProcessor.getECOURI(model);
            if (!ecouri.isEmpty())
                eco = "<a href=\""+ecouri+"\">"+eco+"</a>";
        }

        pgn += "[Event \"" + event  +"\"]\n";
        pgn += "[Site \""  + site  +"\"]\n";
        pgn += "[Date \""  + date  +"\"]\n";
        pgn += "[Round \""  + round  +"\"]\n";
        pgn += "[White \""  + white  +"\"]\n";
        pgn += "[Black \""  + black  +"\"]\n";
        pgn += "[Result \""  + result  +"\"]\n";
        pgn += "[ECO \""  + eco  +"\"]\n";
        pgn+="\n"+game+" "+result;
        
        return pgn;
    }

    /**
     * Obtains the chessid for a given model
     */
    public static String getChessId(Model model) {
        String id = "";
        Resource r2 = model.createResource("http://purl.org/NET/rdfchess/ontology/ChessGame");
        ResIterator rit = model.listSubjectsWithProperty(RDF.type, r2);
        while (rit.hasNext()) {
            Resource r = rit.next();
            id = r.getURI();
        }
        return id;
    }

    /**
     * Obtains the chessid for a given model
     */
    public static String getWhitePlayer(Model model) {
        String id = "";
        Property r2 = model.createProperty("http://purl.org/NET/rdfchess/ontology/hasWhitePlayerName");
        NodeIterator nit = model.listObjectsOfProperty(r2);
        while (nit.hasNext()) {
            RDFNode r = nit.next();
            return r.asLiteral().toString();
        }
        //if the former fails we look in the full-model
        nit = model.listObjectsOfProperty(model.createProperty("http://purl.org/NET/rdfchess/ontology/hasWhitePlayer"));
        while (nit.hasNext()) {
            Resource r = nit.next().asResource();
            NodeIterator nit2 = model.listObjectsOfProperty(r, model.createProperty("http://purl.org/NET/rdfchess/ontology/hasName"));
            if (nit2.hasNext())
                return nit2.next().asLiteral().toString();
        }
        
        return "";
    }

    /**
     * Obtains the chessid for a given model
     */
    public static String getBlackPlayer(Model model) {
        String id = "";
        //First we try to see from the view
        Property r2 = model.createProperty("http://purl.org/NET/rdfchess/ontology/hasBlackPlayerName");
        NodeIterator nit = model.listObjectsOfProperty(r2);
        while (nit.hasNext()) {
            RDFNode r = nit.next();
            return r.asLiteral().toString();
        }
        //if the former fails we look in the full-model
        nit = model.listObjectsOfProperty(model.createProperty("http://purl.org/NET/rdfchess/ontology/hasBlackPlayer"));
        while (nit.hasNext()) {
            Resource r = nit.next().asResource();
            NodeIterator nit2 = model.listObjectsOfProperty(r, model.createProperty("http://purl.org/NET/rdfchess/ontology/hasName"));
            if (nit2.hasNext())
                return nit2.next().asLiteral().toString();
        }
        
        
        return "";
    }
    /**
     * Obtains the chessid for a given model
     */
    public static String getBlackPlayerURI(Model model) {
        String id = "";
        //if the former fails we look in the full-model
        NodeIterator nit = model.listObjectsOfProperty(model.createProperty("http://purl.org/NET/rdfchess/ontology/hasBlackPlayer"));
        while (nit.hasNext()) {
            Resource r = nit.next().asResource();
            return r.getURI();
        }
        return "";
    }
    public static String getWhitePlayerURI(Model model) {
        String id = "";
        //if the former fails we look in the full-model
        NodeIterator nit = model.listObjectsOfProperty(model.createProperty("http://purl.org/NET/rdfchess/ontology/hasWhitePlayer"));
        while (nit.hasNext()) {
            Resource r = nit.next().asResource();
            return r.getURI();
        }
        return "";
    }

    /**
     * Obtains the chessid for a given model
     */
    public static String getECO(Model model) {
        String id = "";
        Property r2 = model.createProperty("http://purl.org/NET/rdfchess/ontology/hasECOOpening");
        NodeIterator nit = model.listObjectsOfProperty(r2);
        while (nit.hasNext()) {
            RDFNode r = nit.next();
            return r.asLiteral().toString();
        }
        r2 = model.createProperty("http://purl.org/NET/rdfchess/ontology/hasChessGameOpening");
        nit = model.listObjectsOfProperty(r2);
        while (nit.hasNext()) {
            Resource r = nit.next().asResource();
            NodeIterator nit2 = model.listObjectsOfProperty(r, model.createProperty("http://purl.org/NET/rdfchess/ontology/ECOID"));
            if (nit2.hasNext())
                return nit2.next().asLiteral().toString();
        }
        return "";
    }
    public static String getECOURI(Model model) {
        String id = "";
        Property r2 = model.createProperty("http://purl.org/NET/rdfchess/ontology/hasChessGameOpening");
        NodeIterator nit = model.listObjectsOfProperty(r2);
        while (nit.hasNext()) {
            Resource r = nit.next().asResource();
            return r.getURI();
        }
        return "";
    }

    static String moves = "";
    static boolean gblancas = true;
    static int gconta = 1;

    /**
     * Obtains the chessid for a given model
     */
    public static String getMoves(Model model) {
        String id = "";
        gblancas = true;
        gconta = 1;
        Property r2 = model.createProperty("http://purl.org/NET/rdfchess/ontology/firstMove");
        ResIterator nit = model.listSubjectsWithProperty(RDF.type, r2);
        if (nit.hasNext()) {
            Resource r = nit.next().asResource();
            NodeIterator rit = model.listObjectsOfProperty(r, ModelFactory.createDefaultModel().createProperty("http://purl.org/NET/rdfchess/ontology/halfMoveRecord"));
            if (rit.hasNext()) {
                moves = "1. " + rit.next().asLiteral().getLexicalForm();
            }

            NodeIterator rit2 = model.listObjectsOfProperty(r, ModelFactory.createDefaultModel().createProperty("http://purl.org/NET/rdfchess/ontology/nextHalfMove"));
            if (rit2.hasNext()) {
                Resource rm = rit2.next().asResource();
                getMovesRecursive(model, rm);
            }

        }
        return moves;
    }

    private static void getMovesRecursive(Model model, Resource r) {
        String s = "";
        gblancas = !gblancas;
        NodeIterator rit = model.listObjectsOfProperty(r, ModelFactory.createDefaultModel().createProperty("http://purl.org/NET/rdfchess/ontology/halfMoveRecord"));
        if (rit.hasNext()) {
            if (gblancas) {
                gconta++;
                moves += " " + gconta + ". ";
            } else {
                moves += " ";
            }
            moves += rit.next().asLiteral().getLexicalForm();
        }
        NodeIterator rit2 = model.listObjectsOfProperty(r, ModelFactory.createDefaultModel().createProperty("http://purl.org/NET/rdfchess/ontology/nextHalfMove"));
        if (!rit2.hasNext()) {
            return;
        }
        getMovesRecursive(model, rit2.next().asResource());
        return;
    }

    /**
     * Obtains the chessid for a given model
     */
    public static String getSite(Model model) {
        String id = "";
        Property r2 = model.createProperty("http://purl.org/NET/rdfchess/ontology/hasChessGameAtNamedPlace");
        NodeIterator nit = model.listObjectsOfProperty(r2);
        while (nit.hasNext()) {
            RDFNode r = nit.next();
            return r.asLiteral().toString();
        }
        r2 = model.createProperty("http://purl.org/NET/rdfchess/ontology/atPlace");
        nit = model.listObjectsOfProperty(r2);
        while (nit.hasNext()) {
            Resource r = nit.next().asResource();
            NodeIterator nit2 = model.listObjectsOfProperty(r, model.createProperty("http://purl.org/NET/rdfchess/ontology/hasName"));
            if (nit2.hasNext())
                return nit2.next().asLiteral().toString();
        }
        return "";
    }

    
    
    
    
    
    
    
    /**
     * Obtains the chessid for a given model
     */
    public static String getDate(Model model) {
       String id = "";
        Property r2 = model.createProperty("http://semanticweb.cs.vu.nl/2009/11/sem/subEventOf");
        Resource rg = model.createResource(PGNProcessor.getChessId(model));
        NodeIterator nit = model.listObjectsOfProperty(rg,r2);
        String sevent="";
        while (nit.hasNext()) {
            RDFNode r = nit.next();
            Resource round = r.asResource(); //es un roundofchesscompetition
            NodeIterator nit2=model.listObjectsOfProperty(round,model.createProperty("http://purl.org/NET/rdfchess/ontology/atTime"));
            if (nit2.hasNext())
            {
                Literal revent = nit2.next().asLiteral(); 
                return revent.toString();
            }
        }
        return "";
    }
    
   /**
     * Obtains the chessid for a given model
     */
    public static String getRound(Model model) {
        String id = "";
        Property r2 = model.createProperty("http://semanticweb.cs.vu.nl/2009/11/sem/subEventOf");
        Resource rg = model.createResource(PGNProcessor.getChessId(model));
        NodeIterator nit = model.listObjectsOfProperty(rg,r2);
        String sround="";
        while (nit.hasNext()) {
            RDFNode r = nit.next();
            Resource round = r.asResource(); //es un roundofchesscompetition
            NodeIterator nit2=model.listObjectsOfProperty(round,RDFS.label );
            if (nit2.hasNext())
            {
                sround = nit2.next().asLiteral().getLexicalForm();
                return sround;
            }
        }
        return "";
    }
    /**
     * Obtains the chessid for a given model
     */
    public static String getEvent(Model model) {
        String id = "";
        Property r2 = model.createProperty("http://semanticweb.cs.vu.nl/2009/11/sem/subEventOf");
        Resource rg = model.createResource(PGNProcessor.getChessId(model));
        NodeIterator nit = model.listObjectsOfProperty(rg,r2);
        String sevent="";
        while (nit.hasNext()) {
            RDFNode r = nit.next();
            Resource round = r.asResource(); //es un roundofchesscompetition
            NodeIterator nit2=model.listObjectsOfProperty(round,r2 );
            if (nit2.hasNext())
            {
                Resource revent = nit2.next().asResource(); //chesscompetition
                NodeIterator nit3=model.listObjectsOfProperty(revent,RDFS.label );
                if (nit3.hasNext())
                    return nit3.next().asLiteral().getLexicalForm();
            }
        }
        return "";
    } 
    
    /**
     * Obtains the chessid for a given model
     */
    public static String getResult(Model model) {
        String id = "";
        Property r2 = model.createProperty("http://purl.org/NET/rdfchess/ontology/hasPGNResult");
        NodeIterator nit = model.listObjectsOfProperty(r2);
        while (nit.hasNext()) {
            RDFNode r = nit.next();
            return r.asLiteral().toString();
        }
        return "";
    } 
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        String input = new String(Files.readAllBytes(Paths.get("samples/test.pgn")));
        String rdf = PGNProcessor.getRDF(input, Lang.TTL);
        System.out.println(rdf);
        Model model = ModelFactory.createDefaultModel();
        InputStream is = new ByteArrayInputStream(rdf.getBytes(StandardCharsets.UTF_8));
        RDFDataMgr.read(model, is, Lang.TTL);
        String s = PGNProcessor.buildPGN(model, true);
        System.out.println(s);
//        PrintWriter out = new PrintWriter("samples/test.ttl");
//        out.println(rdf);
    }

    static Map<String, String> jugadores = new HashMap();
    static Map<String, String> openings = new HashMap();
    static Map<String, String> locations = new HashMap();
    public static String getMappingDBpedia(String jugador)
    {
        String dbpedia = jugadores.get(jugador);
        if (dbpedia==null)
        {
            dbpedia = DBpediaSpotlight.getDBPediaResource(jugador, "/chess/chess_player", "chess");
            if (dbpedia==null) dbpedia="";
            jugadores.put(jugador, dbpedia);
        }
        return jugadores.get(jugador);
    }
    public static String getMappingDBpediaOpening(String sx)
    {
        String dbpedia = openings.get(sx);
        if (dbpedia==null)
        {
            String rec = DBpediaSpotlight.getDBPediaResource(sx, "", "");
            if (rec==null) rec="";
            openings.put(sx, rec);
        }
        return openings.get(sx);
    }
    public static String getMappingGeonames(String sx)
    {
        String dbpedia = locations.get(sx);
        if (dbpedia==null)
        {
            String rec = ManagerGeonames.getMostLikelyResource(sx);
            if (rec==null)
                rec="";
            locations.put(sx, rec);
        }
        return locations.get(sx);
    }
    

}
