package pgn2rdf.chess;

//JAVA
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeSet;

//LOG4J
import org.apache.log4j.Logger;

/**
 * Reads and writes parameters in a configfile
 * 
 * The file is <i>"RDFChess.config"</i> and must be located together with jar or at least in the java path.
 * @author Victor Rodriguez Doncel
 */
public class RDFChessConfig {
    
    private final static String CONFIGFILE = "rdfchess.config";
    static boolean loaded=false;
    
    //Propiedades
    static Properties prop = new Properties();

    /**
     * Obtiene el valor de una propiedad
     * @param p Propiedad
     */
    public static String get(String p) {
        return prop.getProperty(p, "null");
    }

    /**
     * Obtiene el valor de una propiedad, y si no lo tiene da un valor por defecto
     * @param p Propiedad
     * @param valor Valor por defecto
     * @return El valor leido o el de por defecto
     */
    public static String get(String p, String valor) {
        if (!loaded)
            Load();
        return prop.getProperty(p, valor);
    }

    /**
     * Estable el valor de una propiedad
     * @param p Propiedad
     * @param valor Valor 
     */
    public static void set(String p, String defvalue) {
        prop.setProperty(p, defvalue);
        Store();
    }
       
    /**
     * Carga los parametros de configuracion del archivo contenidos.config
     * @return True si todo fue bien
     */
    public static boolean Load() {
        InputStream is;
        try {
            is = new FileInputStream(CONFIGFILE);
            prop.load(is);
            Logger.getLogger("ldr").info("Config file read from " + CONFIGFILE);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace(); //todavía no está el logger
            return false;
        }
    }

    /**
     * Almacena los parámetros de configuración en el archivo
     * Este archivo no es configurable
     * El orden de almacenamiento en archivo es alfabético
     */
    public static void Store() {
        OutputStream os;
        try {
            Properties tmp = new Properties() {
                @Override
                public synchronized Enumeration<Object> keys() {
                    return Collections.enumeration(new TreeSet<Object>(super.keySet()));
                }
            };
            tmp.putAll(prop);
            tmp.store(new FileWriter(CONFIGFILE), null);
        } catch (Exception ex) {
            Logger.getLogger("ldr").error("Error opening config file" + ex.toString());
        }
    }
}
