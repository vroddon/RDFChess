package pgn2rdf.files;

import java.io.File;
import org.apache.commons.io.FileUtils;
import pgn2rdf.chess.RDFChessConfig;

/**
 * Very simple filestore just in case we dont have a fuseki running.
 *
 * @author vroddon
 */
public class RDFFileStore implements RDFStore {

    public static void init() {

    }

    @Override
    public String readGame(String gameid) {
        String nt = "";
        String dumpfolder = RDFChessConfig.get("dumpfolder", "E:\\Data\\RDFChess\\");
        File file = new File(RDFChessConfig.get(dumpfolder) + gameid);
        try {
            nt = FileUtils.readFileToString(file, "UTF-8");
        } catch (Exception e) {

        }
        return nt;
    }

    @Override
    public int sparqlInt(String sparql) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
