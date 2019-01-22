package pgn2rdf.chess;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.jena.riot.Lang;
import org.apache.log4j.BasicConfigurator;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.varia.NullAppender;
import pgn2rdf.files.GameUploader;
import pgn2rdf.files.RDFChessQueries;
import pgn2rdf.files.RDFTripleStore;

/**
 * http://stackoverflow.com/questions/15906842/solr-4-2-1-sslinitializationexception
 *
 * Important for debugging with Glassfish and Netbeans:
 * http://www.javahispano.org/java-ee/post/2433388
 *
 * @author Victor
 */
public class Main {

    public static boolean initialized = false;
    
    public static RDFChessQueries rdfchessqueries = new RDFChessQueries();

    public static void main(String[] args) throws IOException {
        BasicConfigurator.configure();
        Logger.getRootLogger().removeAllAppenders();
        Logger.getRootLogger().addAppender(new NullAppender());

        StringBuilder sb = new StringBuilder();
        CommandLineParser clparser = null;
        CommandLine cl = null;
        try {
            Options options = new Options();
            options.addOption("help", false, "shows this help (Help)");
            options.addOption("add", true, "Transforms, expands and uploads a PGN file");
            options.addOption("transform", true, "Transforms, a PGN file");
            options.addOption("out", true, "Specifies output file");
            options.addOption("count", false, "Counts the number of games");
            options.addOption("ver", false, "Sees the version of this software");
            clparser = new BasicParser();
            cl = clparser.parse(options, args);
            if (cl.hasOption("help")) {
                System.out.println("RDFChess command line tool");
                new HelpFormatter().printHelp(Main.class.getCanonicalName(), options);
            }
            if (cl.hasOption("count")) {
                int total =RDFTripleStore.countGames();
                System.out.println(total);
            }
            if (cl.hasOption("add")) {
                String sfile = cl.getOptionValue("add");
                GameUploader.uploadPGN(sfile);
            }
            if (cl.hasOption("transform")) {
                String sfile = cl.getOptionValue("transform");
                String sout = cl.getOptionValue("output");
                String input = new String(Files.readAllBytes(Paths.get(sfile)));
                String rdf = PGNProcessor.getRDF(input, Lang.TTL);
                if (sout.isEmpty()) {
                    System.out.println(rdf);
                } else {
                    FileUtils.writeStringToFile(new File(sout), rdf);
                }
            }
        } catch (Exception e) {
            System.err.println("error");
        }

    }

    public static void init() throws Exception {
        System.out.println("Initializing RDFChess");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
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
