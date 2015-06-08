package pgn2rdf.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
        response.setContentType("text/html;charset=UTF-8");
        String gameid = request.getRequestURI().replace("/rdfchess/resource/", "");
        gameid = gameid.replace("/RDFChess/resource/", "");
        
        gameid = "http://salonica.dia.fi.upm.es:8080/rdfchess/resource/"+gameid;
        String ttl = RDFStore.read(gameid);
        if (ttl.isEmpty())
        {
            response.getWriter().println("Not found " + gameid + "\n" + gameid);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        
        try (PrintWriter out = response.getWriter()) {
            response.getWriter().println(ttl);
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/turtle;charset=utf-8");
            
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

}
