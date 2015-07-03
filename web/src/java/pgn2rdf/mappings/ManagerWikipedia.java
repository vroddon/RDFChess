package pgn2rdf.mappings;

import java.io.IOException;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Evaluator.Tag;

/**
 * Gets direct access to the Wikipedia pages, making use of the JSoup library-
 *
 * @author admin
 */
public class ManagerWikipedia {

    public static void main(String[] args) throws IOException {
//        String url = "http://en.wikipedia.org/wiki/Garry_Kasparov";
//        String url ="http://en.wikipedia.org/wiki/Queen%27s_Gambit_Declined";
        String url = "https://en.wikibooks.org/wiki/Chess_Opening_Theory/1._e4/1...c5/2._Nf3";
//        infoBox(url);
        String s = getAbstractFromWikiBook(url);
        System.out.println(s);
    }

    /**
     * Gets the text of the first section under a Wikibook page
     *
     * @param url URL of a wikibook
     */
    public static String getAbstractFromWikiBook(String url) throws IOException {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (Exception e) {
            return "";
        }
        String title = doc.title();
        Element content = doc.getElementById("mw-content-text");

        Elements hijos = content.getElementsByTag("p");
        String res = "";
        int count = 0;
        for (Element hijo : hijos) {
            count++;
            org.jsoup.parser.Tag tag = hijo.tag();
            if (tag.toString().equals("p") || tag.toString().equals("dl") || tag.toString().equals("dd")) {
                count++;
                if (count > 2) {
                    res += "<p>" + hijo.text() + "</p>";
                }
            }
            if (tag.toString().equals("h1")) {
                break;
            }
            if (tag.toString().equals("h2")) {
                break;
            }
        }
        res = res.replace("<p>When contributing to this Wikibook, please follow the Conventions for organization.</p>", "");
        res = res.replace("For explanation of theory tables see theory table and for notation see algebraic notation", "");
        
        res = res.replace("<p></p>","");
        return res;
    }

    /**
     * Gets the infobox from a given Wikipedia web page
     */
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
