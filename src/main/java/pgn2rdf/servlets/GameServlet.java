package pgn2rdf.servlets;


import org.apache.jena.vocabulary.RDF;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import pgn2rdf.chess.ChessECOManager;
import pgn2rdf.chess.Main;
import pgn2rdf.chess.PGNProcessor;
import pgn2rdf.chess.RDFChess;
import pgn2rdf.files.RDFTripleStore;
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

    public static boolean initialized = false;
    
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
        if (!initialized)
        {
 //           Fuseki.startEmbeddedFuseki("/data/rdfchess/dump/data.nq", "/RDFChess", 3030);
//            Fuseki.startEmbeddedFuseki("D:\\data\\rdfchess\\nq\\data.nq", "/RDFChess", 3030);
            initialized=true;
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
            body = body.replace("<!--TEMPLATE_TITLE-->", "\n" + "List of chess players");
            String tabla ="<table id=\"grid-data\" class=\"table table-condensed table-hover table-striped\">\n" +
            "        <thead>\n" +
            "                <tr>\n" +
            "                        <th data-column-id=\"chessplayer\" data-formatter=\"link\" data-order=\"desc\">Chess players</th>\n" +
            "                </tr>\n" +
            "        </thead>\n" +
            "</table>	\n" +
            "";
            body = body.replace("<!--TEMPLATE_PGN-->", "<br>" + tabla);

            response.getWriter().println(body);
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String gameid = request.getRequestURI().replace("/rdfchess/resource/", "");
        gameid = gameid.replace("/RDFChess/resource/", "");

        gameid = "http://salonica.dia.fi.upm.es:8080/rdfchess/resource/" + gameid;
        String ttl = RDFTripleStore.readGame(gameid);
        if (ttl.isEmpty()) {
            response.getWriter().println("Not found " + gameid + "\n" + gameid);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try (PrintWriter out = response.getWriter()) {
            if (isRDFXML(request)) {
                System.out.println("Serving RDF/XML for " + gameid);
                response.getWriter().println(RDFTripleStore.readXML(gameid));
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
                    String name2= ManagerGeonames.getName("http://sws.geonames.org/"+name+"");
                    String country=ManagerGeonames.getCountry("http://sws.geonames.org/"+name);
                    
                    String html="<h2>"+name2+"</h2>";
                    html+="Country: " + country;
                    body = body.replace("<!--TEMPLATE_PGN-->", html);                    
                    
                }
                if (titulo.equals("ChessGameOpening")) {
                    int ultimo = entidad.toString().lastIndexOf("/");
                    String eco = entidad.toString().substring(ultimo+1, entidad.toString().length());                
                    String s = "<h3>"+eco+" "+PGNProcessor.getNameFromOpening(model)+"</h3>";
                    String moves = ChessECOManager.getMoves(eco);
                    String parent = ChessECOManager.getParent(eco);
                    if (!parent.isEmpty())
                    {
                        String ecourl=RDFChess.DATA_URI+"opening/"+parent;
                        s+="Parent opening: <a href=\"" + ecourl+"\">" + parent + "</a><br/>" ;
                    }
                    List<String> children = ChessECOManager.getChildren(eco);
                    if (!children.isEmpty())
                    {
                        s+="Children openings: "; 
                        for(String child : children)
                        {
                            String ecourl=RDFChess.DATA_URI+"opening/"+child;
                            s+="<a href=\"" + ecourl+"\">" + child + "</a> " ;                            
                        }
                        s+="<br/>";
                    }
                    
                    NodeIterator ni7 = model.listObjectsOfProperty(entidad, model.createProperty("http://www.w3.org/2000/01/rdf-schema#seeAlso"));
                    String sr = "";
                    if (ni7.hasNext()) {
                        Resource clase = ni7.next().asResource();
                        if (clase.toString().startsWith("http://en.wikibooks") || clase.toString().startsWith("https://en.wikibooks")) {
                            s+="<p>";
                            s+=ManagerWikipedia.getAbstractFromWikiBook(clase.toString());
                            s+="</p>";
                        }
                    }
                     
                    String fen = PGNProcessor.getFEN(moves);
                    
                    //ACHTUNG! REMOVE IF IT DOES NOT WORK. ONLY TESTING
                    s += " <div id=\"board3\" class=\"board\"></div>\n"
                            + "      <p class=\"annot\"></p>\n"
                            + "      <pre id=\"apertura\">\n"
                            + moves
                            + "      </pre> ";
                    int ply = PGNProcessor.getPly(moves);
                    String jquery="loadChessGame( '#game3', { pgn : $('#apertura').html() }, function(chess) {\n chess.transitionTo("+ply+");\n});";
                    body = body.replace("//TEMPLATE_JQUERY", jquery);
                  
                    body=body.replace("<!--TEMPLATE_HEADER-->","<base href=\"http://vroddon.linkeddata.es/rdfchess/resource/\">\n");
                    
                    //END OF TESTING...
                    
                    s += "\n<hr><div style=\"overflow: hidden; width: 100%;\">\n";
                    List<String> partidas = RDFTripleStore.listGamesByOpening(eco);
                    s += "<h3>Some games</h3>";
                    for (String partida : partidas) {
                        s += "<a href=\"" + partida + "\">" + RDFTripleStore.summary(partida) + "</a><br>\n";
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
                    
                    String jquery="loadChessGame( '#game3', { pgn : $('#pgn-fischer-spassky').html()});";
                    body = body.replace("//TEMPLATE_JQUERY", jquery);
                    body=body.replace("<!--TEMPLATE_HEADER-->","<base href=\"http://salonica.dia.fi.upm.es:8080/rdfchess/\">\n");
                    
                    body = body.replace("<!--TEMPLATE_PGN-->", "\n" + superpgn);
                }
                if (titulo.equals("Agent")) {
                    NodeIterator ni7 = model.listObjectsOfProperty(entidad, model.createProperty("http://www.w3.org/2004/02/skos/core#closeMatch"));
                    String s = "";
                    if (ni7.hasNext()) {
                        Resource clase = ni7.next().asResource();
                        if (clase.toString().startsWith("http://dbpedia.org") || clase.toString().startsWith("http://es.dbpedia.org")) {
                        String abst = ManagerDBpedia.getAbstract(clase.toString());
                            s += "<h3>" + ManagerDBpedia.getLabel(clase.toString()) + "</h3>";
                            s += "<p><img style=\"float:left;margin:10px 10px;\" src=\"" + ManagerDBpedia.getThumbnailURL(clase.toString()) + "\" />";
                            s += "" + abst + "</p>";
                            s += "\n<hr><div style=\"overflow: hidden; width: 100%;\">\n";
                            List<String> partidas = RDFTripleStore.listGamesByChessPlayer(entidad.toString());
                            s += "<h3>Some games</h3>";
                            for (String partida : partidas) {
                                s += "<a href=\"" + partida + "\">" + RDFTripleStore.summary(partida) + "</a><br>\n";
                            }
                            s += "</div>";
                        }
                    }
                    body = body.replace("<!--TEMPLATE_PGN-->", "\n" + s);

                }

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
