package pgn2rdf.mappings;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author vroddon
 */
public class DBpediaSpotlight {
    
    public static void main(String[] args) {
        String xml = queryDBPedia("Sicilian defence");
        System.out.println(xml);
        String uri = parseDBPediaXML(xml, ""); ///chess/chess_player DBpedia:Place
        System.out.println(uri);
        
    }
    
    public static String getDBPediaResource(String consulta, String tipo, String muletilla)
    {
        String xml = queryDBPedia(consulta + " " + muletilla); //+" chess"
        String uri = parseDBPediaXML(xml, tipo);
        if (uri.isEmpty())
            uri=consulta;
        return uri;
    }
    
    public static String parseDBPediaXML(String xml, String tipo)
    {
        try{
            InputStream stream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));            
            Document response = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stream);
            NodeList tokenResponse = response.getElementsByTagName("Resource");
            int i;
            for (i = 0; i < tokenResponse.getLength(); i++) {
                Node tokenNode = tokenResponse.item(i);
                NamedNodeMap nnm = tokenNode.getAttributes();
                String suri=nnm.getNamedItem("URI").getNodeValue();
                String types = nnm.getNamedItem("types").getNodeValue();
//                if (uri.equals("http://dbpedia.org/resource/Computer_chess")) continue;
                if (!types.contains(tipo)) 
                    continue;
                return suri;
            }
        }catch(Exception e)
        {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return "";
        }
            return "";
    }
    

    public static String queryDBPedia(String termino) {
        try {
            String utermino = URLEncoder.encode(termino, "UTF-8");
            String request = "http://spotlight.dbpedia.org/rest/annotate?text=" + utermino;
            request += "&confidence=0.2&support=20"; //&types=Person,Organisation        
            HttpGet method = new HttpGet(request);
//            method.setHeader("Content-Type", "application/json");
            method.setHeader("Accept","text/xml");
            HttpClient client = new DefaultHttpClient();
            HttpResponse httpResponse = client.execute(method);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                String razon=httpResponse.getStatusLine().getReasonPhrase();
                System.out.println("MAL MUY MAL REMATADAMENTE MAL " + razon);
            }
            InputStream rstream = null;
            rstream = httpResponse.getEntity().getContent();                
            StringWriter writer = new StringWriter();
            IOUtils.copy(rstream, writer, "UTF-8");
            String theString = writer.toString();
            return theString;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return "";
    }
}
