package pgn2rdf.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import javax.servlet.http.*;
import org.apache.jena.riot.Lang;
import pgn2rdf.chess.PGNProcessor;
import pgn2rdf.mappings.DBpediaSpotlight;

/**
 * This is the servlet in charge of the conversion
 */
public class Pgn2rdfServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain");
        String palabra = "victor";
        resp.getWriter().println("{ \"name\": \"" + palabra + "\" }");
    }
    
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BufferedReader br=req.getReader();
        String s="";
        String tot="";
        while ((s=br.readLine())!=null)
        {
            tot=tot+s+"\n";
        }
        int i = tot.indexOf("&action=");
        String pgn=tot.substring(4, i);
        PGNProcessor.log(pgn, "log1.txt");

        pgn = java.net.URLDecoder.decode(pgn, "UTF-8");
        String action = tot.substring(i+8, tot.length()-1);
        
//        String pgn = req.getParameter("pgn"); BUG BUG BUG
//        String action = req.getParameter("action"); BUG BUG BUG
        if (pgn == null || pgn.isEmpty()) {
            resp.getWriter().println("");
            return;
        }
//        if (queryString!=null)
//            PGNProcessor.log(queryString, "log0.txt");
        PGNProcessor.log(pgn, "log2.txt");
 //       pgn = java.net.URLDecoder.decode(pgn, "UTF-8");
//        PGNProcessor.log(pgn, "log2.txt");
        
        //esto mata los comentarios pero hace que todo funcione bien
        pgn = pgn.replaceAll("\\{.*?\\}", "");
        
        
        String rdf="Ooops";
        if (action.contains("enrich"))
            rdf = PGNProcessor.enrichPGN(pgn);
        else
            rdf= PGNProcessor.getRDF(pgn, Lang.TTL);
        resp.getWriter().println(rdf);
        resp.setContentType("text/plain");
    }    
}
