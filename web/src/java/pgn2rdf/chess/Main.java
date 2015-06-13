package pgn2rdf.chess;

import java.security.KeyStoreException;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * http://stackoverflow.com/questions/15906842/solr-4-2-1-sslinitializationexception
 *
 * @author Victor
 */
public class Main {

    public static boolean initialized = false;

    public static void init() throws Exception {
        /*if (initialized == true) {
            return;
        }

        SSLContextBuilder builder = new SSLContextBuilder();
        builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                builder.build());
        CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(
                sslsf).build();

        DefaultHttpClient httpClient = new DefaultHttpClient();
        httpClient.getConnectionManager().getSchemeRegistry()
                .register(new Scheme("https", 443,
                                SSLSocketFactory.getSystemSocketFactory()));
        initialized = true;
        */
    }
}
