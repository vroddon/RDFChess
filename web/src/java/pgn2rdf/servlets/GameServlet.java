package pgn2rdf.servlets;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import static java.lang.System.in;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import pgn2rdf.chess.Main;
import pgn2rdf.chess.PGNProcessor;
import pgn2rdf.files.RDFStore;

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
                response.setContentType("application/rdf+xml;charset=utf-8");
            } else if (isRDFTTL(request)) {
                System.out.println("Serving TTL for " + gameid);
                response.getWriter().println(ttl);
                response.setContentType("text/turtle;charset=utf-8");
            } else {
                System.out.println("Serving HTML for " + gameid);
                InputStream is1 = GameServlet.class.getResourceAsStream("../../../../game.html");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is1));
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
                NodeIterator nit = model.listObjectsOfProperty(ModelFactory.createDefaultModel().createResource(gameid), RDF.type);
                String titulo = "Unknown type";
                if (nit.hasNext()) {
                    Resource clase = nit.next().asResource();
                    titulo = clase.getLocalName();
                }

                if (titulo.equals("ChessGame")) {

                    String pgn = PGNProcessor.buildPGN(model);
                    String superpgn = "      <div>\n"
                            + "        <a class=\"back\" href=\"#\">Back</a>\n"
                            + "        <a class=\"next\" href=\"#\">Next</a>\n"
                            + "        <a class=\"flip\" href=\"#\">Flip</a>\n"
                            + "      </div>";
                    superpgn += " <div id=\"board3\" class=\"board\"></div>\n"
                            + "      <p class=\"annot\"></p>\n"
                            + "      <pre id=\"pgn-fischer-spassky\">\n"
                            + pgn
                            + "      </pre> ";

                    body = body.replace("<!--TEMPLATE_PGN-->", "\n" + superpgn);
                }
                body = body.replace("<!--TEMPLATE_URI-->", "\n" + gameid);
                body = body.replace("<!--TEMPLATE_TITLE-->", "\n" + titulo);
                body = body.replace("<!--TEMPLATE_TTL-->", "<br>" + ttl2);

                /*response.getWriter().println("<html><head> <script src=\"https://google-code-prettify.googlecode.com/svn/loader/run_prettify.js\"></script></head>");
                 response.getWriter().println("<body><pre class=\"prettyprint\">");
                 response.getWriter().println(ttl2);
                 response.getWriter().println("</pre></body></html>");*/
                response.getWriter().println(body);

                response.setContentType("text/html;charset=utf-8");
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
