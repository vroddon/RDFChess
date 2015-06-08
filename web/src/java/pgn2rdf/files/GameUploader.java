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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import static pgn2rdf.files.ChessGameIterator.getMoves;
import static pgn2rdf.files.PGNFolderParser.MD5;

/**
 * This class uploads games into the Linked Data server
 * @author Victor
 */
public class GameUploader {
    
    
static Set<String> hashes = new HashSet();

    /**
     * Main method uploads Bobby fischer's games
     */
    public static void main(String[] args) {

        int total = 0;
        
        Iterator it = new ChessGameIterator();
        while (it.hasNext()) {
            String rdf = (String) it.next();
            if (rdf.isEmpty())
                break;
            
            //This is to correct an error that was made while creating the dataset
            rdf=rdf.replace("http://purl.org/NET/chess", "http://purl.org/NET/rdfchess");
            
            //http://purl.org/NET/rdfchess/resource/
            rdf = rdf.replace("http://purl.org/NET/rdfchess/resource/", "http://salonica.dia.fi.upm.es:8080/rdfchess/resource/");
//            rdf = rdf.replace("http://purl.org/NET/rdfchess/resource/", "http://lider1.dia.fi.upm.es:8088/rdfchess/resource/");
            
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
                    
                    System.out.println(lita.toString()+" - "+litb.toString());
                    
                    if (litb.toString().contains("Fischer"))
                    {
                        String moves = getMoves(model, rgame.getURI());
                        String md5 = MD5(moves);
                        int siz = hashes.size();
                        hashes.add(md5);
                        if (hashes.size()-siz==1)
                        {
                            System.out.println(moves+" "+ md5);
                            RDFStore.write(rgame.getURI(), rdf);
                            total++;
                            if (total==1000)
                                return;
                        }
                    }
                }
                
            }catch(Exception e)
            {
                System.err.println("Could not be loaded. " + e.toString());
            }
        }
    }    
}
