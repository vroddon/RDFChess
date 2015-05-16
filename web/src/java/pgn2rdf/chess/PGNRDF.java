package pgn2rdf.chess;

import chesspresso.game.Game;
import chesspresso.move.Move;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import java.util.UUID;

/**
 *
 * @author vroddon
 */
public class PGNRDF {

    static Model pgn2rdf(Game g) {

        Model modelo = ModelFactory.createDefaultModel();
        modelo.setNsPrefix("dct", "http://purl.org/dc/terms/");
        modelo.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        modelo.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        modelo.setNsPrefix("ldr", "http://purl.oclc.org/NET/ldr/ns#");
        modelo.setNsPrefix("void", "http://rdfs.org/ns/void#");
        modelo.setNsPrefix("dcat", "http://www.w3.org/ns/dcat#");
        modelo.setNsPrefix("gr", "http://purl.org/goodrelations/");
        modelo.setNsPrefix("prov", "http://www.w3.org/ns/prov#");

        String id = UUID.randomUUID().toString();
        Resource r = modelo.createResource("http://purl.org/NET/chess/resource/" + id);
        Resource r2 = modelo.createResource("http://purl.org/NET/chess/ontology/ChessGame");
        modelo.add(r, RDF.type, r2);
        Property r96 = modelo.createProperty("http://purl.org/NET/chess/ontology/hasWhitePlayerName");
        Property r97 = modelo.createProperty("http://purl.org/NET/chess/ontology/hasBlackPlayerName");
        Property r98 = modelo.createProperty("http://purl.org/NET/chess/ontology/hasWhitePlayer");
        Property r99 = modelo.createProperty("http://purl.org/NET/chess/ontology/hasBlackPlayer");

        RDFNode rdfwhite = null;
        String swhite = g.getWhite();
        if (swhite.startsWith("http")) {
            rdfwhite = modelo.createResource(swhite);
            modelo.add(r, r98, rdfwhite);
        } else {
            rdfwhite = modelo.createLiteral(swhite);
            modelo.add(r, r96, rdfwhite);
        }
        RDFNode rdfblack = null;
        String sblack = g.getBlack();
        if (sblack.startsWith("http")) {
            rdfblack = modelo.createResource(sblack);
            modelo.add(r, r98, rdfwhite);
            
        } else {
            rdfblack = modelo.createLiteral(sblack);
            modelo.add(r, r97, rdfblack);
            
        }
        RDFNode rdfsite = null;
        String ssite = g.getSite();
        if (ssite.startsWith("http")) {
            rdfsite = modelo.createResource(ssite);
        } else {
            rdfsite = modelo.createLiteral(ssite);
        }        
        
        

        modelo.add(r, RDF.type, r2);

  /*      Resource r4 = modelo.createResource("http://purl.org/NET/chess/ontology/roleWhite");
        Resource r3 = modelo.createResource("http://www.ontologydesignpatterns.org/cp/owl/agentrole.owl#Agent");
        modelo.add(r4, RDF.type, r3);
        modelo.add(r4, RDFS.label, "White");
        Resource r5 = modelo.createResource("http://purl.org/NET/chess/ontology/roleBlack");
        modelo.add(r5, RDF.type, r3);
        modelo.add(r5, RDFS.label, "Black");
        Property r6 = modelo.createProperty("http://purl.org/NET/chess/ontology/isPerformedBy");
        modelo.add(r4, r6, rdfwhite);
        modelo.add(r5, r6, rdfblack);

*/
        
        
        Property r7 = modelo.createProperty("http://semanticweb.cs.vu.nl/2009/11/sem/subEventOf");
        
        String sround = g.getRound();
        Resource r8 = modelo.createResource("http://purl.org/NET/chess/ontology/roundOfChessCompetition");
        Resource r9 = modelo.createResource("http://purl.org/NET/chess/ontology/round"+UUID.randomUUID().toString());
        modelo.add(r9, RDF.type, r8);
        modelo.add(r9, RDFS.label, sround);
        modelo.add(r, r7, r9);

        Resource r10 = modelo.createResource("http://purl.org/NET/chess/ontology/ChessCompetition");
        Resource r11 = modelo.createResource("http://purl.org/NET/chess/ontology/atChessCompetition"+UUID.randomUUID().toString());
        modelo.add(r11, RDF.type, r10);
        modelo.add(r9, r7, r11);

        Property r12 = modelo.createProperty("http://purl.org/dc/terms/spatial");
        //MAPEO
        r12 = modelo.createProperty("http://purl.org/NET/chess/ontology/hasChessGameAtNamedPlace");
        modelo.add(r, r12, rdfsite);
        
        Property r13 = modelo.createProperty("http://purl.org/NET/chess/ontology/hasECOOpening");
        modelo.add(r, r13, g.getECO());
        
        Property r14 = modelo.createProperty("http://purl.org/NET/chess/ontology/nextHalfMove");
        Resource r15 = modelo.createResource("http://purl.org/NET/chess/ontology/HalfMove");
        Resource r16 = modelo.createResource("http://purl.org/NET/chess/ontology/firstMove");
        Resource r17 = modelo.createResource("http://purl.org/NET/chess/ontology/lastMove");
        Property r18 = modelo.createProperty("http://purl.org/NET/chess/ontology/halfMoveRecord");
        Property r19 = modelo.createProperty("http://purl.org/NET/chess/ontology/hasPGNResult");
        
        g.gotoStart();
        Resource pm = null;
        while (g.hasNextMove())
        {
            Move m = g.getNextMove();
            
            Resource rm = modelo.createResource("http://purl.org/NET/chess/resource/"+UUID.randomUUID().toString());
            modelo.add(rm, RDF.type, r15);
            modelo.add(rm, r18, m.getSAN());
            if (pm!=null)
            {
                modelo.add(pm, r14, rm);
            }
            else
                modelo.add(rm, RDF.type, r16);
            
            pm=rm;
            g.goForward();
        }
        modelo.add(pm, RDF.type,r17);
        
        String resultado = (g.getResultStr()==null) ? "" : g.getResultStr();

        modelo.add(r, r19, resultado);
        
        return modelo;
    }

}
