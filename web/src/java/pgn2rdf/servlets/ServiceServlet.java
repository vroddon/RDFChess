package pgn2rdf.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import pgn2rdf.files.RDFStore;

/**
 *
 * @author admin
 */
public class ServiceServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods. http://www.jquery-bootgrid.com/Examples
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            String uri = request.getRequestURI();
            PrintWriter archivo = new PrintWriter("d:\\test.txt");
            archivo.println(uri+request.getParameter("current"));
            archivo.close();
            if (uri.equals("/rdfchess/service/getChessplayers")) {
                //current=1&rowCount=10&sort[sender]=asc&searchPhrase=&id=b0df282a-0d67-40e5-8558-c9e93b7befed
                String offset = request.getParameter("current");
                String limit = request.getParameter("rowCount");
                int current = Integer.parseInt(offset);
                int total = RDFStore.countChessplayers();
                int ilimit = Integer.parseInt(limit);
                int init=(current-1)*ilimit;
                List<String> ls = RDFStore.listChessPlayers(init, ilimit);
                System.out.println(offset+" "+limit);
                String s = "{\n"
                        + "  \"current\": "+current+",\n"
                        + "  \"rowCount\": " +ilimit+",\n"
                        + "  \"rows\": [\n";
                int conta=0;
                for (String cp : ls) {
                    
                    int lasti = cp.lastIndexOf("/");
                    String nombre = cp.substring(lasti+1,cp.length());
                    nombre=URLDecoder.decode(nombre, "UTF-8");
                    if (conta!=0)
                        s+=",\n";
                    s += "    {\n"
                            + "      \"chessplayer\": \"" +nombre+"\",\n"
                            + "      \"chessplayerurl\": \""+cp+"\"\n"
                            + "    } ";
                    conta++;
                }
                
                s += "  ],\n"
                        + "  \"total\": "+total+"\n"
                        + "}    ";
                out.print(s);
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
                
            }
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
