package pgn2rdf.files;
import java.io.InputStream;
import java.util.Iterator;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.sparql.core.Quad;
import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.tdb.TDBLoader;
import com.hp.hpl.jena.tdb.base.file.Location;
import com.hp.hpl.jena.tdb.store.DatasetGraphTDB;
import com.hp.hpl.jena.tdb.sys.TDBInternal;
import com.hp.hpl.jena.util.FileManager;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

/**
 *
 * @author vroddon
 */
public class PGNStore {

    
    public static void main(String[] args) {
//        FileManager fm = FileManager.get();
//        fm.addLocatorClassLoader(PGNStore.class.getClassLoader());
//        InputStream in = fm.open("d:\\data\\chess\\q.nt\\00a3e640-c214-4e85-bf06-4a307c668ef7.nt");
//        RDFDataMgr.read(ds, in, Lang.TTL);

       /* Dataset ds = TDBFactory.createDataset("games");
        Model namedModel = ds.getNamedModel("games");
        StmtIterator it = namedModel.listStatements();
        while(it.hasNext())
        {
            Statement st=it.next();
            System.out.println(st);
        }*/
        
        
        Dataset ds = TDBFactory.createDataset("games");
        Model model = ds.getNamedModel("NG1");      
        RDFDataMgr.read(model, "d:\\data\\chess\\q.nt\\00a3e640-c214-4e85-bf06-4a307c668ef7.nt");
        try {
            model.enterCriticalSection(Lock.WRITE);
            // write triples to model
            model.commit();
            TDB.sync(model);
        } finally {
                model.leaveCriticalSection();
        }        
        StmtIterator it = model.listStatements();
        while(it.hasNext())
        {
            Statement st=it.next();
            System.out.println(st);
        }        
        
        
/*        Location location = new Location ("games");
        // Load some initial data
        DatasetGraph ds = TDBFactory.createDatasetGraph(location);
        DatasetGraphTDB dsg = TDBInternal.getBaseDatasetGraphTDB(ds);
        TDBLoader.load(dsg, in, false);
        Dataset dataset = TDBFactory.createDataset(location);
        dataset.begin(ReadWrite.READ);
        try {
            Iterator<Quad> iter = dataset.asDatasetGraph().find();
            while ( iter.hasNext() ) {
                Quad quad = iter.next();
                quad.
                System.out.println("Subject: "  + quad.getSubject());
                System.out.println("Graph: "  + quad.getGraph());
                System.out.println(quad);
            }
        } finally {
            dataset.end();
        }*/
    }    
    
}
