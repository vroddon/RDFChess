package pgn2rdf.mappings;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
 
//Geonames
import org.geonames.FeatureClass;
import org.geonames.PostalCode;
import org.geonames.PostalCodeSearchCriteria;
import org.geonames.Toponym;
import org.geonames.ToponymSearchCriteria;
import org.geonames.ToponymSearchResult;
import org.geonames.WebService;
import pgn2rdf.chess.Tutorial;
import static pgn2rdf.chess.Tutorial.REST;

/**
 * Wrapper class to access the Geonames API References:
 * http://www.geonames.org/source-code/
 *
 * @author Victor Rodriguez Doncel
 */
public class ManagerGeonames {

    /**
     * Gets the most likely resource given a certain entry
     */
    public static String getMostLikelyResource(String texto) {
        String res = "";
        try {
            WebService.setUserName("vroddon"); // add your username here
            ToponymSearchCriteria searchCriteria = new ToponymSearchCriteria();
            searchCriteria.setQ(texto);
            ToponymSearchResult searchResult = WebService.search(searchCriteria);
            for (Toponym toponym : searchResult.getToponyms()) {
                return "http://sws.geonames.org/" + toponym.getGeoNameId();
//                System.out.println(toponym.getName() + " " + toponym.getCountryName() + " " + toponym.getGeoNameId());
            }
        } catch (Exception e) {
        }
        return res;
    }

    public static String getName(String place) {
        String name = "";
        String rdf;
        try {
            rdf = Tutorial.REST(place,"", "application/rdf+xml");
            InputStream is = new ByteArrayInputStream(rdf.getBytes(StandardCharsets.UTF_8));        
            Model model = ModelFactory.createDefaultModel();
            RDFDataMgr.read(model, is, Lang.RDFXML);
            System.out.println(rdf);
            NodeIterator nit = model.listObjectsOfProperty(ModelFactory.createDefaultModel().createProperty("http://www.geonames.org/ontology#name"));
//            NodeIterator nit = model.listObjectsOfProperty(ModelFactory.createDefaultModel().createResource(place),ModelFactory.createDefaultModel().createProperty("http://www.geonames.org/ontology#name"));
            while(nit.hasNext())
            {
                return nit.next().asLiteral().getLexicalForm();
            }
//            System.out.println(rdf);
        } catch (IOException ex) {
            Logger.getLogger(ManagerGeonames.class.getName()).log(Level.SEVERE, null, ex);
        }
        return name;

    }
    /**
     * Encuentra las coordenadas de un punto conocido
     */
    public static void findCoordinates(String location) {
        WebService.setUserName("vroddon");
        try {
            String server = WebService.getGeoNamesServer();
            ToponymSearchCriteria tsc = new ToponymSearchCriteria();
            tsc.setMaxRows(1);
            tsc.setQ(location);
            ToponymSearchResult result = WebService.search(tsc);
            if (result != null && result.getTotalResultsCount() > 0) {
                Toponym topo = result.getToponyms().get(0);
                int id = topo.getGeoNameId();
                double lat = topo.getLatitude();
                double lon = topo.getLongitude();
                System.out.println("Retrieved " + topo.getName() + " (" + lat + ", " + lon + ") " + id);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        return;
    }

    /**
     * Encuentra lugares cerca de unas coordenadas dadas.
     */
    public static String findClosestLocations(double lat, double lon) {
        WebService.setUserName("vroddon");
        try {
            String server = WebService.getGeoNamesServer();
            String cadenas[] = {"MTRO"};
            PostalCodeSearchCriteria psc = new PostalCodeSearchCriteria();
            psc.setLatitude(lat);
            psc.setLongitude(lon);
            psc.setMaxRows(1);
            String pobox = "";
            List<PostalCode> lpc = WebService.findNearbyPostalCodes(psc);
            if (!lpc.isEmpty()) {
                pobox = lpc.get(0).getPostalCode();
            }
            List<Toponym> sitios1 = WebService.findNearby(lat, lon, FeatureClass.P, null);
            List<Toponym> sitios2 = WebService.findNearby(lat, lon, FeatureClass.S, null);
            List<Toponym> sitios3 = WebService.findNearby(lat, lon, FeatureClass.L, null);
            List<Toponym> sitios4 = WebService.findNearby(lat, lon, FeatureClass.S, cadenas);
            List<Toponym> sitios = new ArrayList();
            sitios.addAll(sitios1);
            sitios.addAll(sitios2);
            sitios.addAll(sitios3);
            sitios.addAll(sitios4);
            System.out.println("Contacted with : " + server);
            System.out.println("Encontrados:  " + sitios.size());
            for (Toponym top : sitios) {
                double reallat = top.getLatitude();
                double reallon = top.getLongitude();
                double dist = 0;
//                double dist=Punto.distFrom((float)lat,(float)lon,(float)reallat, (float)reallon);
                System.out.println(top.getName() + "\t" + top.getFeatureClassName() + "\t" + top.getFeatureCodeName() + "\t" + top.getFeatureCode() + "\t" + top.getFeatureCodeName() + "\t" + top.getFeatureClassName() + " \t" + pobox + "\t" + dist);
//                return top.getName();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static void main(String[] args) {
        
        String name=ManagerGeonames.getName("http://sws.geonames.org/2678205");
        System.out.println(name);
/*        
        try {
            WebService.setUserName("vroddon"); // add your username here
            ToponymSearchCriteria searchCriteria = new ToponymSearchCriteria();
            searchCriteria.setQ("Vienna");
            ToponymSearchResult searchResult = WebService.search(searchCriteria);
            for (Toponym toponym : searchResult.getToponyms()) {
                System.out.println(toponym.getName() + " " + toponym.getCountryName() + " " + toponym.getGeoNameId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    public static String getCountry(String place) {
        String name = "";
        String rdf;
        try {
            rdf = Tutorial.REST(place,"", "application/rdf+xml");
            InputStream is = new ByteArrayInputStream(rdf.getBytes(StandardCharsets.UTF_8));        
            Model model = ModelFactory.createDefaultModel();
            RDFDataMgr.read(model, is, Lang.RDFXML);
            System.out.println(rdf);
            NodeIterator nit = model.listObjectsOfProperty(ModelFactory.createDefaultModel().createProperty("http://www.geonames.org/ontology#countryCode"));
            while(nit.hasNext())
            {
                return nit.next().asLiteral().getLexicalForm();
            }
        } catch (IOException ex) {
            Logger.getLogger(ManagerGeonames.class.getName()).log(Level.SEVERE, null, ex);
        }
        return name;
    }

}

/**
 
 run:
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<rdf:RDF xmlns:cc="http://creativecommons.org/ns#" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:foaf="http://xmlns.com/foaf/0.1/" xmlns:gn="http://www.geonames.org/ontology#" xmlns:owl="http://www.w3.org/2002/07/owl#" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#" xmlns:wgs84_pos="http://www.w3.org/2003/01/geo/wgs84_pos#">
<gn:Feature rdf:about="http://sws.geonames.org/2678205/">
<rdfs:isDefinedBy rdf:resource="http://sws.geonames.org/2678205/about.rdf"/>
<gn:name>Skara Municipality</gn:name>
<gn:alternateName>Skara</gn:alternateName>
<gn:alternateName>Skara Kommun</gn:alternateName>
<gn:alternateName xml:lang="en">Skara Municipality</gn:alternateName>
<gn:featureClass rdf:resource="http://www.geonames.org/ontology#A"/>
<gn:featureCode rdf:resource="http://www.geonames.org/ontology#A.ADM2"/>
<gn:countryCode>SE</gn:countryCode>
<gn:population>18269</gn:population>
<wgs84_pos:lat>58.37759</wgs84_pos:lat>
<wgs84_pos:long>13.47548</wgs84_pos:long>
<gn:parentFeature rdf:resource="http://sws.geonames.org/3337386/"/>
<gn:parentCountry rdf:resource="http://sws.geonames.org/2661886/"/>
<gn:parentADM1 rdf:resource="http://sws.geonames.org/3337386/"/>
<gn:childrenFeatures rdf:resource="http://sws.geonames.org/2678205/contains.rdf"/>
<gn:locationMap rdf:resource="http://www.geonames.org/2678205/skara-kommun.html"/>
<gn:wikipediaArticle rdf:resource="http://en.wikipedia.org/wiki/Skara_Municipality"/>
<rdfs:seeAlso rdf:resource="http://dbpedia.org/resource/Skara_Municipality"/>
</gn:Feature>
<foaf:Document rdf:about="http://sws.geonames.org/2678205/about.rdf">
<foaf:primaryTopic rdf:resource="http://sws.geonames.org/2678205/"/>
<cc:license rdf:resource="http://creativecommons.org/licenses/by/3.0/"/>
<cc:attributionURL rdf:resource="http://sws.geonames.org/2678205/"/>
<cc:attributionName rdf:datatype="http://www.w3.org/2001/XMLSchema#string">GeoNames</cc:attributionName>
<dcterms:created rdf:datatype="http://www.w3.org/2001/XMLSchema#date">2006-01-15</dcterms:created>
<dcterms:modified rdf:datatype="http://www.w3.org/2001/XMLSchema#date">2014-04-30</dcterms:modified>
</foaf:Document>
</rdf:RDF>

BUILD SUCCESSFUL (total time: 1 second)
* 
 * 
 */
 