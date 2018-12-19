package pgn2rdf.files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.jena.riot.Lang;
import pgn2rdf.chess.PGNProcessor;

/**
 * This class is not intended to be reused, it has been written ad-hoc for one
 * data-processing task. The purpose is to take the PGN files downloaded by
 * Reihan in May 2015 and available as .zip, to add them id and source metadata
 * and to sort them and compress them by letters, in this case considering the
 * first letter of the white player. The output is a.zip, b.zip, c.zip... etc.
 * The execution of these routines is lenghty. Assumptions -- All the games
 * start by [Event... -- All the games contain a white chessplayer with the name
 * starting by [A-Z, a-z].
 *
 * @author vroddon
 */
public class PGNFolderParser {

    /**
     * The main class executes the parsing letter by letter. This is not BY FAR
     * the best algorithm, but it was fast to program.
     */
    public static void main(String[] args) throws Exception {

        String pgn="";
        FileInputStream inputStream = new FileInputStream("D:\\svn\\RDFChess\\web\\samples\\Fischer.pgn");
        try {
            pgn = IOUtils.toString(inputStream);
        } finally {
            inputStream.close();
        }
 //       System.out.println(pgn);
        String rdf = PGNProcessor.getRDF(pgn, Lang.NTRIPLES);
//        System.out.println(rdf);
        byte[] data = rdf.getBytes();
        FileOutputStream out = new FileOutputStream("D:\\svn\\RDFChess\\web\\samples\\Fischer.nt");
        out.write(data);
        out.close();      

//        String zipini = "C:\\svn\\PGN\\ZipFiles";
//        String zipfin = "C:\\data\\pgn";
//        generateHash(zipfin);
//        fromPGNToRDF("F:\\data\\pgn-victor", "F:\\data\\pgn-victor");

    }

    private static void generateHash(String folder) throws Exception {
        char c = 'X';
        while (c <= 'X') {
            System.out.println("Parsing the letter " + c);
            final File sinf = new File(folder + "\\" + c + ".zip");
            FileInputStream fis = new FileInputStream(sinf);
            ZipInputStream zipIn = new ZipInputStream(fis);
            
            FileOutputStream hash = new FileOutputStream(new File("hash.txt"));
            
            while (true) {
                ZipEntry entry = zipIn.getNextEntry();
                if (entry == null) {
                    break;
                }
                StringWriter writer = new StringWriter();
                IOUtils.copy(zipIn, writer, "UTF-8");
                String pgn = writer.toString();                
                pgn = pgn.replaceAll("(?m)^\\[IdRDF.*", "");
                pgn+="\n";
                String md5 = MD5(pgn);
                byte[] data = md5.getBytes();
       //         System.out.println(md5);
                hash.write(data);
                c++;
            }
            hash.close();
            
        }

    }

    /**
    * Parses and converts from PGN to RDF systematically all the files, compressing them.
    */
    private static void fromPGNToRDF(String source, String dest) throws Exception {
        char c = 'A';
        while (c <= 'P') {

        
        String sinf = source + "\\"+c+".zip";
        final File fout = new File(source + "\\"+c+".nt.zip");
        final ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(fout));
        FileInputStream fis = new FileInputStream(new File(sinf));
        ZipInputStream zipIn = new ZipInputStream(fis);
        int count = 0;
        while (true) {
            ZipEntry entry = zipIn.getNextEntry();
            if (entry == null) {
                break;
            }
            StringWriter writer = new StringWriter();
            IOUtils.copy(zipIn, writer, "UTF-8");
            String pgnwithgame = writer.toString();
            String rdf = PGNProcessor.getRDF(pgnwithgame, Lang.NTRIPLES);
            String id = getId(pgnwithgame);
            ZipEntry e = new ZipEntry(id + ".nt");
            zout.putNextEntry(e);
            byte[] data = rdf.getBytes();
            zout.write(data, 0, data.length);
            zout.closeEntry();
            count++;
            if (count % 1000 == 0) {
                System.out.println(count + " processed games");
            }
        }
        zout.close();
        c++;
        }

    }

    private static void fromReihanToSource(String zipfileslocation) throws Exception {
        char c = 'A';
        while (c <= 'Z') {
            System.out.println("Parsing the letter: " + c);
            parseWithWhiteLetter(zipfileslocation, "" + c);
            c++;
        }
    }

    /**
     * This method parses the white chessplayers starting by the letter "l".
     *
     * @param l must be a character.
     * @param zipfile Is the folder where the zip files are
     */
    private static void parseWithWhiteLetter(String zipfile, String l) throws Exception {
        List<String> ls = loadFiles(zipfile, l);                                        //loads the enriched information
        final File fout = new File(l + ".zip");
        final ZipOutputStream out = new ZipOutputStream(new FileOutputStream(fout));
        final StringBuilder sb = new StringBuilder();
        for (String s : ls) {
            try {
                String id = getId(s);
//            System.zout.println(s);
                ZipEntry e = new ZipEntry(id + ".pgn");
                out.putNextEntry(e);
                byte[] data = s.getBytes();
                out.write(data, 0, data.length);
                out.closeEntry();
            } catch (Exception e) {
                System.err.println("EXcepcion: " + s);
            }
        }
        out.close();

    }
    
    /**
     * Adds the game identifier
     */
    private static String getId(String str)
    {
        String id ="";
        try{
        int index = str.indexOf("[IdRDF ");
        id = str.substring(index+8, index+44);
        }catch(Exception e){
            System.out.println(str);
            return "";
        }
        return id;
    }

    /**
     * Loads the internal sources file
     */
    private static List<String> loadSources()
    {
        List<String> ls = new ArrayList();
        try {
            InputStream in = pgn2rdf.files.PGNFolderParser.class.getResourceAsStream("sources");
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String str = "";
            while ((str = br.readLine()) != null) {
                ls.add(str);
            }
        } catch (Exception e) {
        }
        return ls;
    }

    /**
     * 
     */
    public static List<String> loadFiles(String sfolder, String letra) {
        List<String> pgns = new ArrayList();
        List<String> sources = loadSources();   //reads reihan files
        try {
            File folder = new File(sfolder);
            File[] listOfFiles = folder.listFiles();
            int count = 0;
            for (int i = 0; i < listOfFiles.length; i++) {
                String source = sources.get(i);
                if (listOfFiles[i].isFile()) {
                    FileInputStream fis = new FileInputStream(listOfFiles[i]);
                    ZipInputStream zipIn = new ZipInputStream(fis);
                    ZipEntry entry = zipIn.getNextEntry();
                    StringWriter writer = new StringWriter();
                    IOUtils.copy(zipIn, writer, "UTF-8");
                    String pgnwithgames = writer.toString();
                    int lastindex=0;
                    int index=0;
                    while(true)
                    {
                        index = pgnwithgames.indexOf("[Event ",lastindex+1);
                        if (index<0)
                        {
                            break;
                        }
                        String str = pgnwithgames.substring(lastindex, index);
                        lastindex=index;
                        String letter = getFirstLetter(str);
                        if (!letter.equalsIgnoreCase(letra)) {
                            continue;
                        }
                        str = enrich(str, source);          //adds the new information
                        pgns.add(str);
                        if (count % 1000 == 0) {
                            int percent = (i * 100 / listOfFiles.length);
                            System.out.println(percent + "% Parsed: " + count + " games");
                        }
                        count++;
                    }
                } else if (listOfFiles[i].isDirectory()) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pgns;
    }

    /**
     * Gets the first letter of the white chessplayer of the given game
     */
    private static String getFirstLetter(String pgn) {
        int index = pgn.indexOf("[White \"");
        if (index == -1) {
            return "-";
        } else {
            return pgn.substring(index + 8, index + 9);
        }
    }

    private static String enrich(String str, String source) {
        str = str.replace("\r\n", "\n"); //to handle equally UNIX and WINDOWS files.
        String adenda = "\n[IdRDF \"" + UUID.randomUUID().toString() + "\"]";
        adenda += "\n[Source \"" + source + "\"]\n";
        String out = str.replace("]\n\n", "]" + adenda + "\n\n");
        return out;
    }

    public static String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }
}
