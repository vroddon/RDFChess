package pgn2rdf.mappings;

import java.io.IOException;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author admin
 */
public class ManagerWikipedia {

    public static void main(String[] args) throws IOException {
//        String url = "http://en.wikipedia.org/wiki/Garry_Kasparov";
//        String url ="http://en.wikipedia.org/wiki/Queen%27s_Gambit_Declined";
        String url="https://en.wikibooks.org/wiki/Chess_Opening_Theory/1._e4";
//        infoBox(url);
        getAbstractFromWikiBook(url);
    }

    public static String getAbstractFromWikiBook(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        String title = doc.title();
        System.out.println(title);
        Element content = doc.getElementById("mw-content-text");
        
        Elements hijos = content.getElementsByTag("p");
        for(Element hijo : hijos)
        {
            String t = hijo.text();
            if (t.length()>64)
            {
//                System.out.println(hijo.text());
                return t;
 //               break;
            }
        }
   //    String s = content.text();
   //     System.out.println(s);
        return "";
    }

    public static String infoBox(String url) throws IOException {
        Response res = Jsoup.connect(url).execute();
        String html = res.body();
        Document doc2 = Jsoup.parseBodyFragment(html);
        Element body = doc2.body();
        Elements tables = body.getElementsByTag("table");
        for (Element table : tables) {
            if (table.className().contains("infobox") == true) {
                System.out.println(table.outerHtml());
                break;
            }
        }
        return "";
    }

}
