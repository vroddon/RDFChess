package pgn2rdf.servlets;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import pgn2rdf.chess.ChessECOManager;
import pgn2rdf.chess.Main;
import pgn2rdf.chess.PGNProcessor;
import pgn2rdf.files.RDFStore;
import pgn2rdf.mappings.ManagerDBpedia;
import pgn2rdf.mappings.ManagerGeonames;
import pgn2rdf.mappings.ManagerWikipedia;

/**
 * This web processes queries of the style serving RDF Chess games as linked
 * data RDF chess games contain the string /rdfchess/resource/<id>
 * http://purl.org/NET/rdfchess/resource/45bee133-d88c-42e4-89bd-681c81170702
 *
 * @author vroddon
 */
public class GameServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Main.init();
        } catch (Exception e) {
        }
        String peticion = request.getRequestURI();
        if (peticion.equals("/rdfchess/resource/")) {                               //SERVING THE LIST OF GAMES.
            System.out.println("Serving HTML for general players");
            response.setContentType("text/html;charset=UTF-8");
            InputStream is1 = GameServlet.class.getResourceAsStream("../../../../game.html");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is1));
            StringBuilder outx = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                outx.append(line);
            }
            String body = outx.toString();
            
                body = body.replace("<!--TEMPLATE_URI-->", "\n" + "");
                body = body.replace("<!--TEMPLATE_TITLE-->", "\n" + "List of chess players");
                
                String lista="";
                List<String> ls = RDFStore.listChessPlayers();
                for(String s : ls)
                {
               //     String name = PGNProcessor.getChessPlayerName(s);     //too expensive!
                    int ultimo = s.lastIndexOf("/");
                    String name= s.substring(ultimo+1, s.length());
                    name = URLDecoder.decode(name, "UTF-8");
                    lista+="<a href=\"" + s +"\">"+name+"</a><br>";
                }
            body = body.replace("<!--TEMPLATE_PGN-->", "<br>" + lista);
            response.getWriter().println(body);
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String gameid = request.getRequestURI().replace("/rdfchess/resource/", "");
        gameid = gameid.replace("/RDFChess/resource/", "");

        gameid = "http://salonica.dia.fi.upm.es:8080/rdfchess/resource/" + gameid;
        String ttl = RDFStore.readGame(gameid);
        if (ttl.isEmpty()) {
            response.getWriter().println("Not found " + gameid + "\n" + gameid);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try (PrintWriter out = response.getWriter()) {
            if (isRDFXML(request)) {
                System.out.println("Serving RDF/XML for " + gameid);
                response.getWriter().println(RDFStore.readXML(gameid));
                response.setContentType("application/rdf+xml;charset=UTF-8");
            } else if (isRDFTTL(request)) {
                System.out.println("Serving TTL for " + gameid);
                response.getWriter().println(ttl);
                response.setContentType("text/turtle;charset=UTF-8");
            } else {
                response.setContentType("text/html;charset=UTF-8");
                response.setCharacterEncoding("UTF-8");
                System.out.println("Serving HTML for " + gameid);
                InputStream is1 = GameServlet.class.getResourceAsStream("../../../../game.html");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is1, "UTF-8"));
                StringBuilder outx = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    outx.append(line);
                }
                String body = outx.toString();
                String ttl2 = StringEscapeUtils.escapeHtml4(ttl);
                System.out.println("TTL has been escaped");
                Model model = ModelFactory.createDefaultModel();
                InputStream is = new ByteArrayInputStream(ttl.getBytes(StandardCharsets.UTF_8));
                RDFDataMgr.read(model, is, Lang.TTL);
                Resource entidad = ModelFactory.createDefaultModel().createResource(gameid);
                NodeIterator nit = model.listObjectsOfProperty(entidad, RDF.type);
                String titulo = "Unknown type";
                if (nit.hasNext()) {
                    Resource clase = nit.next().asResource();
                    titulo = clase.getLocalName();
                }
                if (titulo.equals("Place")) {
                    int ultimo = entidad.toString().lastIndexOf("/");
                    String name= entidad.toString().substring(ultimo+1, entidad.toString().length());    
                    System.out.println("Getting name of: " + name);
                    name= ManagerGeonames.getName("http://sws.geonames.org/"+name+"");
                    System.out.println(name);
                    body = body.replace("<!--TEMPLATE_PGN-->", "<h2>"+name+"</h2>");                    
                }
                if (titulo.equals("ChessGameOpening")) {
                    int ultimo = entidad.toString().lastIndexOf("/");
                    String eco = entidad.toString().substring(ultimo+1, entidad.toString().length());                
                    String s = "<h3>"+PGNProcessor.getNameFromOpening(model)+"</h3>";
                    s+= ChessECOManager.getMoves(eco);
                    NodeIterator ni7 = model.listObjectsOfProperty(entidad, model.createProperty("http://www.w3.org/2000/01/rdf-schema#seeAlso"));
                    String sr = "";
                    if (ni7.hasNext()) {
                        Resource clase = ni7.next().asResource();
                        if (clase.toString().startsWith("http://en.wikibooks")) {
                            s+="<p>";
                            s+=ManagerWikipedia.getAbstractFromWikiBook(ttl);
                            s+="</p>";
                        }
                    }
                    s += "\n<hr><div style=\"overflow: hidden; width: 100%;\">\n";
                    List<String> partidas = RDFStore.listGamesByOpening(eco);
                    s += "<h3>Some games</h3>";
                    for (String partida : partidas) {
                        s += "<a href=\"" + partida + "\">" + RDFStore.summary(partida) + "</a><br>\n";
                    }
                    s += "</div>";
                    body = body.replace("<!--TEMPLATE_PGN-->", "\n" + s);
                }

                if (titulo.equals("ChessGame")) {
                    String pgn = PGNProcessor.buildPGN(model, true);
                    String superpgn = "<div><center>\n"
                            + "        <a class=\"back\" href=\"#\">Back</a>\n"
                            + "        <a class=\"next\" href=\"#\">Next</a>\n"
                            + "        <a class=\"flip\" href=\"#\">Flip</a>\n"
                            + "      </center></div>";
                    superpgn += " <div id=\"board3\" class=\"board\"></div>\n"
                            + "      <p class=\"annot\"></p>\n"
                            + "      <pre id=\"pgn-fischer-spassky\">\n"
                            + pgn
                            + "      </pre> ";
                    body = body.replace("<!--TEMPLATE_PGN-->", "\n" + superpgn);
                }
                if (titulo.equals("Agent")) {
                    NodeIterator ni7 = model.listObjectsOfProperty(entidad, model.createProperty("http://www.w3.org/2004/02/skos/core#closeMatch"));
                    String s = "";
                    if (ni7.hasNext()) {
                        Resource clase = ni7.next().asResource();
                        if (clase.toString().startsWith("http://dbpedia.org") || clase.toString().startsWith("http://es.dbpedia.org")) {
                        String abst = ManagerDBpedia.getAbstract(clase.toString());
                        System.out.println(abst);
                        PrintWriter archivo = new PrintWriter("d:\\test.txt");
                        archivo.println(abst);
                        archivo.close();
                            
                            
                            s += "<h3>" + ManagerDBpedia.getLabel(clase.toString()) + "</h3>";
                            s += "<p><img style=\"float:left;margin:10px 10px;\" src=\"" + ManagerDBpedia.getThumbnailURL(clase.toString()) + "\" />";
                            s += "" + abst + "</p>";

                            s += "\n<hr><div style=\"overflow: hidden; width: 100%;\">\n";
                            List<String> partidas = RDFStore.listGamesByChessPlayer(entidad.toString());
                            s += "<h3>Some games</h3>";
                            for (String partida : partidas) {
                                s += "<a href=\"" + partida + "\">" + RDFStore.summary(partida) + "</a><br>\n";
                            }
                            s += "</div>";
                        }
                    }
                    body = body.replace("<!--TEMPLATE_PGN-->", "\n" + s);

                }

                body = body.replace("<!--TEMPLATE_URI-->", "\n" + gameid);
                body = body.replace("<!--TEMPLATE_TITLE-->", "\n" + titulo);
                body = body.replace("<!--TEMPLATE_TTL-->", "<br>" + ttl2);
                response.getWriter().println(body);
            }
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    public static boolean isRDFTTL(HttpServletRequest request) {
        String uri = request.getRequestURI();
        boolean human = true;
        Enumeration enume = request.getHeaderNames();
        while (enume.hasMoreElements()) {
            String hname = (String) enume.nextElement();
            Enumeration<String> enum2 = request.getHeaders(hname);
            //      System.out.print(hname + "\t");
            while (enum2.hasMoreElements()) {
                String valor = enum2.nextElement();
                if (hname.equalsIgnoreCase("Accept")) {
                    if (valor.equals("text/turtle")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determina si la petición ha de ser servida a un humano o directamente el
     * RDF
     *
     * @param request HTTP request
     */
    public static boolean isRDFXML(HttpServletRequest request) {
        String uri = request.getRequestURI();
        boolean human = true;
        Enumeration enume = request.getHeaderNames();
        while (enume.hasMoreElements()) {
            String hname = (String) enume.nextElement();
            Enumeration<String> enum2 = request.getHeaders(hname);
            //      System.out.print(hname + "\t");
            while (enum2.hasMoreElements()) {
                String valor = enum2.nextElement();
                if (valor.contains("application/rdf+xml")) {
                    return true;
                }
                if (hname.equalsIgnoreCase("Accept")) {
                    if (valor.contains("application/rdf+xml")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
