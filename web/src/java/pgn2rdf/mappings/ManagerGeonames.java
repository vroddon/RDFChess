package pgn2rdf.mappings;

import java.util.ArrayList;
import java.util.List;

//Geonames
import org.geonames.FeatureClass;
import org.geonames.PostalCode;
import org.geonames.PostalCodeSearchCriteria;
import org.geonames.Toponym;
import org.geonames.ToponymSearchCriteria;
import org.geonames.ToponymSearchResult;
import org.geonames.WebService;


/**
 *
 * Referencias: http://www.geonames.org/source-code/
 * @author Victor
 */
public class ManagerGeonames {

    public static void main(String[] args) {
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
        }
    }
    
    public static String getMostLikelyResource(String texto)
    {
        String res="";
        try{
            WebService.setUserName("vroddon"); // add your username here
            ToponymSearchCriteria searchCriteria = new ToponymSearchCriteria();
            searchCriteria.setQ(texto);
            ToponymSearchResult searchResult = WebService.search(searchCriteria);
            for (Toponym toponym : searchResult.getToponyms()) {
                return "http://sws.geonames.org/"+toponym.getGeoNameId();
//                System.out.println(toponym.getName() + " " + toponym.getCountryName() + " " + toponym.getGeoNameId());
            }
    }catch(Exception e){}
        return res;
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
                int id=topo.getGeoNameId();
                double lat = topo.getLatitude();
                double lon = topo.getLongitude();
                System.out.println("Retrieved " + topo.getName() + " (" + lat + ", "+lon+") " + id);
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
            
//            List<Toponym> sitios1 = WebService.findNearbyPlaceName(lat, lon);
            String cadenas[]={"MTRO"};
            PostalCodeSearchCriteria psc=new PostalCodeSearchCriteria();
            psc.setLatitude(lat);
            psc.setLongitude(lon);
            psc.setMaxRows(1);
            String pobox="";
            List<PostalCode> lpc=WebService.findNearbyPostalCodes(psc);
            if(!lpc.isEmpty())
                pobox=lpc.get(0).getPostalCode();
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
                double reallat=top.getLatitude();
                double reallon=top.getLongitude();
                double dist=0;
//                double dist=Punto.distFrom((float)lat,(float)lon,(float)reallat, (float)reallon);
                System.out.println(top.getName() + "\t" + top.getFeatureClassName() + "\t" + top.getFeatureCodeName() + "\t" + top.getFeatureCode() + "\t" + top.getFeatureCodeName() + "\t" + top.getFeatureClassName() +" \t"+ pobox+"\t"+ dist);
//                return top.getName();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
}
