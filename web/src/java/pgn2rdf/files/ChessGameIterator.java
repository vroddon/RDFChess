package pgn2rdf.files;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Iterator;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import static pgn2rdf.files.PGNFolderParser.MD5;

/**
 * This class browses the games present in a tar file The tar file is supposed
 * to have zip files The zip files are supposed to have ttl files
 *
 * @author vroddon
 */
public class ChessGameIterator implements Iterator {

    static String rdftar = "D:\\data\\chess\\rdfchess.tar";
    InputStream is = null;
    TarArchiveInputStream debInputStream = null;
    TarArchiveEntry tarentry = null;
    ZipEntry zentry = null;
    ZipInputStream zipIn = null;
    static Set<String> hashes = new HashSet();

    public static void main(String[] args) {

        Iterator it = new ChessGameIterator();
        while (it.hasNext()) {
            String rdf = (String) it.next();
            if (rdf.isEmpty())
                break;
            rdf=rdf.replace("http://purl.org/NET/chess", "http://purl.org/NET/rdfchess");
            try{
                Model model = ModelFactory.createDefaultModel();
                InputStream stream = new ByteArrayInputStream(rdf.getBytes("UTF-8"));
                RDFDataMgr.read(model, stream, Lang.TTL);
                Resource rgamec=model.createResource("http://purl.org/NET/rdfchess/ontology/ChessGame");
                ResIterator rit = model.listSubjectsWithProperty(RDF.type, rgamec);
                Resource rgame = null;
                if (rit.hasNext())
                    rgame=rit.next();
                if (rgame!=null)
                {
                    Property cwhite = model.createProperty("http://purl.org/NET/rdfchess/ontology/hasWhitePlayerName");
                    Property cblack = model.createProperty("http://purl.org/NET/rdfchess/ontology/hasBlackPlayerName");
                    Literal lita=null;
                    Literal litb=null;
                    NodeIterator rita = model.listObjectsOfProperty(rgame, cwhite);
                    if (rita.hasNext())
                        lita = rita.next().asLiteral();
                    NodeIterator rit2 = model.listObjectsOfProperty(rgame, cblack);
                    if (rit2.hasNext())
                        litb = rit2.next().asLiteral();
                    if (litb.toString().contains("Fischer"))
                    {
                        String moves = getMoves(model, rgame.getURI());
                        String md5 = MD5(moves);
                        int siz = hashes.size();
                        hashes.add(md5);
                        if (hashes.size()-siz==1)
                        {
                            System.out.println(lita.toString()+" - "+litb.toString());
                            System.out.println(moves+" "+ md5);
                            RDFStore.write(rgame.getURI(), rdf);
                        }
                    }
                }
                
            }catch(Exception e)
            {
                System.err.println("Could not be loaded");
            }
        }
    }
    
    public static String getMoves(Model model, String game)
    {
        String s ="";
        Resource rgame = model.createResource(game);
        ResIterator rf = model.listResourcesWithProperty(RDF.type, model.createResource("http://purl.org/NET/rdfchess/ontology/firstMove"));
        if (!rf.hasNext())
            return s;
        Resource fm = rf.next().asResource();

        int conta=0;
        do{
            NodeIterator it2 = model.listObjectsOfProperty(fm,model.createProperty("http://purl.org/NET/rdfchess/ontology/halfMoveRecord"));
            if (!it2.hasNext())
                return s;
            s+=it2.next().asLiteral().toString()+" ";
            NodeIterator it3 = model.listObjectsOfProperty(fm,model.createProperty("http://purl.org/NET/rdfchess/ontology/nextHalfMove"));
            if (!it3.hasNext())
                return s;
            fm = it3.next().asResource();
        }while(conta<200);
        
        return s;
    }
            
    

    @Override
    public boolean hasNext() {

        return true;
    }

    @Override
    public String next() {
        try {
            File inputFile = new File(rdftar);
            if (is == null) {
                is = new FileInputStream(inputFile);
            }
            if (debInputStream == null) {
                debInputStream = (TarArchiveInputStream) new ArchiveStreamFactory().createArchiveInputStream("tar", is);
            }

            if (tarentry == null) {
                tarentry = (TarArchiveEntry) debInputStream.getNextEntry();
                zipIn = new ZipInputStream(debInputStream);
            }
            do {
                zentry = zipIn.getNextEntry();
                if (zentry == null) {
                    tarentry = (TarArchiveEntry) debInputStream.getNextEntry();
                    zipIn = new ZipInputStream(debInputStream);
                    zentry = zipIn.getNextEntry();
                }
                if (zentry==null)
                {
                    tarentry = (TarArchiveEntry) debInputStream.getNextEntry();
                    System.out.println("reading next .zip file");
                    if (tarentry==null)
                        return "";
                    zipIn = new ZipInputStream(debInputStream);
                    zentry=zipIn.getNextEntry();
                    if (zentry==null)
                        return "";
                }
            } while (zentry.isDirectory());

            StringWriter writer = new StringWriter();
            IOUtils.copy(zipIn, writer, "UTF-8");
            String pgn = writer.toString();
            
            
            
//            System.out.println(zentry.getName());
            return pgn;
//            return pgn;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
