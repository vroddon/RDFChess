package pgn2rdf.chess;

import chesspresso.game.Game;
import chesspresso.move.Move;
import chesspresso.pgn.PGNReader;
import chesspresso.pgn.PGNWriter;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.mem.GraphMem;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
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
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
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
import java.util.UUID;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import pgn2rdf.files.RDFStore;
import pgn2rdf.mappings.DBpediaSpotlight;
import pgn2rdf.mappings.ManagerDBpedia;
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
    
    
    private String execSELECT(Dataset dataxet, String sparql)
    {
        QueryExecution qexec = QueryExecutionFactory.create(QueryFactory.create(sparql),dataxet);
        ResultSet results = qexec.execSelect() ;
        for ( ; results.hasNext() ; )
        {   
            System.out.println(results.next().toString());
        }
        return "";
    }
    
    /**
     * Applies the view expansion
     * chess:e9fcb74a-301e-4dd4-854a-b98b33554dde
     */ 
    public static String expandRDF(String rdf) {
        System.out.println("A view expansion is going to take place");
        InputStream is = new ByteArrayInputStream(rdf.getBytes(StandardCharsets.UTF_8));
        
        //We read the id of the game
        Model model = ModelFactory.createDefaultModel();
        RDFDataMgr.read(model, is, Lang.TTL);
        try{is.reset();}catch(Exception e){}
        String id=PGNProcessor.getChessId(model);
        System.out.println("The game id is: " + id);
        
        //We load the game as a graph
        DatasetGraphFactory.GraphMaker maker = new DatasetGraphFactory.GraphMaker() {
            public Graph create() {
                return new GraphMem();
            }
        };
        Dataset dataxet = DatasetImpl.wrap(new DatasetGraphMaker(maker));
        RDFDataMgr.read(dataxet, is, Lang.TTL);
        GraphStore graphStore = GraphStoreFactory.create(dataxet) ;
        
        //FIRST EXPANSION, WHITE PLAYER
        String blanco = PGNProcessor.getWhitePlayerFromView(model);
        String dbblanco = DBpediaSpotlight.getDBPediaResource(blanco, "/chess/chess_player", "chess");
        if (!dbblanco.equals(blanco))
        {
            String literal = blanco;
            String dbpedia = dbblanco;
            System.out.println("Expanding " + blanco +" to " + dbblanco);
            String newname = ManagerDBpedia.getLabel(dbblanco);        
            String idw = RDFChess.DATA_URI + UUID.randomUUID().toString();
            String sparql="PREFIX chess: <http://purl.org/NET/rdfchess/ontology/>\n" +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" +
                "DELETE { <" + id +"> chess:hasWhitePlayerName \""+literal+"\" . }\n" +
                "INSERT {\n<" +
                id + "> chess:hasWhitePlayer <"+idw + "> .\n" +
                "<"+ idw +"> rdf:type chess:Agent .\n" +
                "<"+ idw +"> chess:hasName \""+newname+"\" .\n" +
                "<"+ idw +"> skos:closeMatch <"+dbpedia+"> .\n" +
                "}\n" +
                "WHERE { <" + id + "> chess:hasWhitePlayerName \""+literal+"\" }";
            UpdateAction.parseExecute(sparql,graphStore);         //DROP ALL
        }

        //SECOND EXPANSION, WHITE PLAYER
        String negro = PGNProcessor.getBlackPlayerFromView(model);
        String dbnegro = DBpediaSpotlight.getDBPediaResource(negro, "/chess/chess_player", "chess");
        if (!negro.equals(dbnegro))
        {
            System.out.println("Expanding " + negro +" to " + dbnegro);
            String literal = negro;
            String dbpedia = dbnegro;
            String newname = ManagerDBpedia.getLabel(dbnegro);        
            String idw = RDFChess.DATA_URI + UUID.randomUUID().toString();
            String sparql="PREFIX chess: <http://purl.org/NET/rdfchess/ontology/>\n" +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" +
                "DELETE { <" + id +"> chess:hasBlackPlayerName \""+literal+"\" . }\n" +
                "INSERT {\n<" +
                id + "> chess:hasBlackPlayer <"+idw + "> .\n" +
                "<"+ idw +"> rdf:type chess:Agent .\n" +
                "<"+ idw +"> chess:hasName \""+newname+"\" .\n" +
                "<"+ idw +"> skos:closeMatch <"+dbpedia+"> .\n" +
                "}\n" +
                "WHERE { <" + id + "> chess:hasBlackPlayerName \""+literal+"\" }";
            UpdateAction.parseExecute(sparql,graphStore);         //DROP ALL
        }
        
        //THIRD EXPANSION, ECO OPENING
        String eco = PGNProcessor.getECO(model);
        String econame = ChessECOManager.getName(eco);
        System.out.println("ECO: " + eco +" " + econame);
        String sx = eco + " " + econame + " ";
        String loc=ChessECOManager.getLibraryOfCongress(eco);

        String dbpedia = DBpediaSpotlight.getDBPediaResource(sx, "", "");
        if (dbpedia.equals(sx))
            dbpedia="";
        String literal = eco;
        String idw = RDFChess.DATA_URI + UUID.randomUUID().toString();
        String sparql="PREFIX chess: <http://purl.org/NET/rdfchess/ontology/>\n" +
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
            "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" +
            "DELETE { <" + id +"> <http://purl.org/NET/chess/ontology/hasECOOpening> \""+literal+"\" . }\n" +
            "INSERT {\n<" +
            id + "> <http://purl.org/NET/chess/ontology/hasChessGameOpening> <"+idw + "> .\n" +
            "<"+ idw +"> rdf:type chess:ChessGameOpening .\n" +
            "<"+ idw +"> chess:ECOID \""+eco +"\" .\n" +
            "<"+ idw +"> rdfs:label \""+econame +"\" .\n" +
            "<"+ idw +"> skos:closeMatch <"+loc+"> .\n" +
            "<"+ idw +"> skos:closeMatch <"+dbpedia+"> .\n" +
            "}\n" +
            "WHERE { <" + id + "> <http://purl.org/NET/chess/ontology/hasECOOpening> \""+literal+"\" }";
        System.out.println("Expanding " + eco +" to " + econame);
        UpdateAction.parseExecute(sparql,graphStore);         //DROP ALL
        
        //FOURTH EXPANSION, GEONAMES
        String site =  PGNProcessor.getSite(model);
        String site2 = ManagerGeonames.getMostLikelyResource(site);        
        if (!site.isEmpty() && site2!=null && !site2.isEmpty() && !site2.equals(site))
        {
            System.out.println("Expanding " + site +" to " + site2);
            idw = RDFChess.DATA_URI + UUID.randomUUID().toString();
            sparql="PREFIX chess: <http://purl.org/NET/rdfchess/ontology/>\n" +
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
            "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" +
            "DELETE { <" + id +"> <http://purl.org/NET/chess/ontology/hasChessGameAtNamedPlace> \""+site+"\" . }\n" +
            "INSERT {\n<" +
            id + "> <http://purl.org/NET/chess/ontology/atPlace> <"+idw + "> .\n" +
            "<"+ idw +"> rdf:type chess:Place .\n" +
            "<"+ idw +"> chess:hasName \""+site +"\" .\n" +
            "<"+ idw +"> skos:closeMatch <"+site2+"> .\n" +
            "}\n" +
            "WHERE { <" + id + "> <http://purl.org/NET/chess/ontology/hasChessGameAtNamedPlace> \""+site+"\" }";
            System.out.println(sparql);
            UpdateAction.parseExecute(sparql,graphStore);               
        }
        
        
        
        StringWriter sw = new StringWriter();
        RDFDataMgr.write(sw, dataxet, Lang.NQUADS);
        
        System.out.println(sw);
        
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
    
    /**
     * 
     */
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
    


    
    /**
     * Obtains the chessid for a given model
     */
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
     * Obtains the chessid for a given model
     */
    public static String getWhitePlayerFromView(Model model)
    {
        String id="";
        Property r2 = model.createProperty("http://purl.org/NET/rdfchess/ontology/hasWhitePlayerName");
        NodeIterator nit = model.listObjectsOfProperty(r2);
        while(nit.hasNext())
        {
            RDFNode r = nit.next();
            return r.asLiteral().toString();
        }
        return "";
    }
    
    /**
     * Obtains the chessid for a given model
     */
    public static String getBlackPlayerFromView(Model model)
    {
        String id="";
        Property r2 = model.createProperty("http://purl.org/NET/rdfchess/ontology/hasBlackPlayerName");
        NodeIterator nit = model.listObjectsOfProperty(r2);
        while(nit.hasNext())
        {
            RDFNode r = nit.next();
            return r.asLiteral().toString();
        }
        return "";
    }
    
    /**
     * Obtains the chessid for a given model
     */
    public static String getECO(Model model)
    {
        String id=""; 
        Property r2 = model.createProperty("http://purl.org/NET/chess/ontology/hasECOOpening");
        NodeIterator nit = model.listObjectsOfProperty(r2);
        while(nit.hasNext())
        {
            RDFNode r = nit.next();
            return r.asLiteral().toString();
        }
        return "";
    }
    
    static String moves = "";
    static boolean gblancas = true;
    static int gconta=1;
    /**
     * Obtains the chessid for a given model
     */
    public static String getMoves(Model model)
    {
        String id=""; 
        gblancas=true;
        gconta=1;
        Property r2 = model.createProperty("http://purl.org/NET/rdfchess/ontology/firstMove");
        ResIterator nit = model.listSubjectsWithProperty(RDF.type, r2);
        if(nit.hasNext())
        {
            Resource r = nit.next().asResource();            
            NodeIterator rit = model.listObjectsOfProperty(r, ModelFactory.createDefaultModel().createProperty("http://purl.org/NET/rdfchess/ontology/halfMoveRecord"));
            if (rit.hasNext())
                moves="1. "+ rit.next().asLiteral().getLexicalForm();
            
            NodeIterator rit2 =  model.listObjectsOfProperty(r, ModelFactory.createDefaultModel().createProperty("http://purl.org/NET/rdfchess/ontology/nextHalfMove"));
            if (rit2.hasNext())
            {
                Resource rm = rit2.next().asResource();
                getMovesRecursive(model, rm);
            }
            
        }
        return moves;
    }
    
    private static void getMovesRecursive(Model model, Resource r)
    {
        String s ="";
        gblancas = !gblancas;
        NodeIterator rit = model.listObjectsOfProperty(r, ModelFactory.createDefaultModel().createProperty("http://purl.org/NET/rdfchess/ontology/halfMoveRecord"));
        if (rit.hasNext())
        {
            if (gblancas)
            {
                gconta++;
                moves+=" "+gconta+". ";
            }
            else
                moves+=" ";
            moves+=rit.next().asLiteral().getLexicalForm();
        }
        NodeIterator rit2 =  model.listObjectsOfProperty(r, ModelFactory.createDefaultModel().createProperty("http://purl.org/NET/rdfchess/ontology/nextHalfMove"));
        if (!rit2.hasNext())
            return;
        getMovesRecursive(model, rit2.next().asResource());
        
        return;
    }
    
    
    
    /**
     * Obtains the chessid for a given model
     */
    public static String getSite(Model model)
    {
        String id=""; 
        Property r2 = model.createProperty("http://purl.org/NET/chess/ontology/hasChessGameAtNamedPlace");
        NodeIterator nit = model.listObjectsOfProperty(r2);
        while(nit.hasNext())
        {
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
        String s = PGNProcessor.getMoves(model);       
        System.out.println(s);
        PrintWriter out = new PrintWriter("samples/test.ttl");
        out.println(rdf);
    }


    

}
