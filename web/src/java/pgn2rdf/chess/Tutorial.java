package pgn2rdf.chess;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateProcessor;
import com.hp.hpl.jena.update.UpdateRequest;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import pgn2rdf.files.RDFStore;

/**
 * Class with different methods to learn and test the code
 * @author vroddon
 */
public class Tutorial {
    
    public static void main(String[] args) throws IOException {
//        testRDF();
        clearAll();
        //   RDFStore.listGamesByChessPlayer("http://salonica.dia.fi.upm.es:8080/rdfchess/resource/chessplayer/Anatoly+Karpov");
 //       RDFStore.listChessPlayers();
    }
    
    /**
     * Deletes all the graphs in the Linked Data Server
     */
    public static void clearAll()
    {
        String endpoint = "http://localhost:3030/RDFChess/update";
        UpdateRequest request = UpdateFactory.create() ;
        request.add("DROP ALL");      
        UpdateProcessor qexec=UpdateExecutionFactory.createRemoteForm(request,endpoint);
        qexec.execute();
        
    }
    
    
    public static void testRDF() throws IOException
    {
        String rdf="";//RDFStore.readGame("http://salonica.dia.fi.upm.es:8080/rdfchess/resource/46ae5300-efd6-4bf7-8867-20b9a1b9dcfa");
        rdf=REST("http://salonica.dia.fi.upm.es:8080/rdfchess/resource/46ae5300-efd6-4bf7-8867-20b9a1b9dcfa","");
        System.out.println(rdf);
        PGNProcessor.expandRDF(rdf);
        InputStream is = new ByteArrayInputStream(rdf.getBytes(StandardCharsets.UTF_8));        
        Model model = ModelFactory.createDefaultModel();
        RDFDataMgr.read(model, is, Lang.TTL);
        String html=PGNProcessor.buildPGN(model, true);
        System.out.println(html);
    }
 
    public static String REST(String surl, String params) throws MalformedURLException, IOException  {
        URL url = new URL(surl);
        String query = "";

        //make connection
        URLConnection urlc = url.openConnection();
        urlc.setRequestProperty("Accept", "text/turtle");
        //use post mode
        urlc.setDoOutput(true);
        urlc.setAllowUserInteraction(false);

        //send query
        PrintStream ps = new PrintStream(urlc.getOutputStream());
        ps.print(query);
        ps.close();

        //get result
        BufferedReader br = new BufferedReader(new InputStreamReader(urlc
            .getInputStream()));
        String l = null;
        String s="";
        while ((l=br.readLine())!=null) {
            s+=l+"\n";
        }
        br.close();
        return s;
    }    
    
}
