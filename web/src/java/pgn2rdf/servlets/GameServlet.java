package pgn2rdf.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringEscapeUtils;
import pgn2rdf.chess.Main;
import pgn2rdf.files.RDFStore;

/**
 * This web processes queries of the style serving RDF Chess games as linked data
 * RDF chess games contain the string /rdfchess/resource/<id>
 * http://purl.org/NET/rdfchess/resource/45bee133-d88c-42e4-89bd-681c81170702
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
       try{ Main.init();}catch(Exception e){}
        String gameid = request.getRequestURI().replace("/rdfchess/resource/", "");
        gameid = gameid.replace("/RDFChess/resource/", "");
        
        gameid = "http://salonica.dia.fi.upm.es:8080/rdfchess/resource/"+gameid;
        String ttl = RDFStore.readGame(gameid);
        if (ttl.isEmpty())
        {
            response.getWriter().println("Not found " + gameid + "\n" + gameid);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        
        try (PrintWriter out = response.getWriter()) {
            
            if(isRDFXML(request))
            {
                System.out.println("Serving RDF/XML for " + gameid);
                response.getWriter().println(RDFStore.readXML(gameid));
                response.setContentType("application/rdf+xml;charset=utf-8");
            }
            else if (isRDFTTL(request))
            {
                System.out.println("Serving TTL for " + gameid);
                response.getWriter().println(ttl);
                response.setContentType("text/turtle;charset=utf-8");
            }
            else{
                System.out.println("Serving HTML for " + gameid);

                response.getWriter().println("<html><head> <script src=\"https://google-code-prettify.googlecode.com/svn/loader/run_prettify.js\"></script></head>");
                response.getWriter().println("<body><pre class=\"prettyprint\">");
                String ttl2= StringEscapeUtils.escapeHtml4(ttl);
                response.getWriter().println(ttl2);
                response.getWriter().println("</pre></body></html>");
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
                if (hname.equalsIgnoreCase("Accept") ) {
                    if (valor.equals("text/turtle")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    
    /**
     * Determina si la petición ha de ser servida a un humano o directamente el RDF
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
                    if (valor.contains("application/rdf+xml")) 
                        return true;
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
