package pgn2rdf.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import pgn2rdf.chess.RDFChessConfig;

/**
 * This Servlet serves the RDF dump hosted in a different folder 
 * (not within the .war file)
 *
 * @author Victor Rodriguez Doncel
 */
public class DumpServlet extends HttpServlet {

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
        response.setHeader("Content-Disposition", "attachment;filename=data.tar");
        String mimeType = "application/x-tar";
        response.setContentType(mimeType);
        String dumpfolder = RDFChessConfig.get("dumpfolder", "F:\\data\\rdfchess\\");
        String sfile = request.getRequestURI().replace("/RDFChess/dump/", dumpfolder);
//        sfile = "d:\\data\\test.nq";
        InputStream in = null;
        OutputStream out = null;
        try {
            FileInputStream fis = new FileInputStream(new File(sfile));
            out = response.getOutputStream();
            IOUtils.copy(fis, out);
        } catch(Exception e)
        {
            System.err.println("Error processing request");
            e.printStackTrace();
        }
            finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
        response.setStatus(HttpServletResponse.SC_FOUND);
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
