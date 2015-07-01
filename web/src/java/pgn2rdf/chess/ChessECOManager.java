package pgn2rdf.chess;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Class in charge of naming the ECO Chess Openings It contains the hard-coded
 * list of openings.
 *
 * @author Victor Rodriguez
 */
public class ChessECOManager {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
    
        String eco ="B96";
        System.out.println(eco);
        System.out.println(getName(eco));
        System.out.println(getMoves(eco));
        System.out.println(getLibraryOfCongress(eco));
        System.out.println(getSeeAlso(eco));
        
    }
    
    
    /**
     * Returns an English string naming a known opening.
     *
     * @param eco ECO code of opening. For examle: "C41"
     * @return En English string. For example: "Philidor Defense"
     */
    public static String getName(String eco) {
        String name = "";
        name = mapa.get(eco);
        return name;
    }
    public static String getMoves(String eco) {
        String name = "";
        name = mapamoves.get(eco);
        return name;
    }

    /**
     * Takes advantage of the chess opening book 
     * https://en.wikibooks.org/wiki/Chess_Opening_Theory/1._e4/1...e5/2._Nf3/2...Nc6/3._Bb5
     * https://en.wikibooks.org/wiki/Chess_Opening_Theory/1._e4/1...c5/2._Nf3/2...d6/3._d4/3...cxd4/4._Nxd4/4...Nf6/5._Nc3/5...a6/6._Bg5/6...e6
     *         1.e4 e5 2.Nf3
     *   1._e4/1...e5/2._Nf3
     */
    public static String getSeeAlso(String eco)
    {
        String also="";
        
        String m = getMoves(eco);
        int i=0;
        int c=1;
        while(true)
        {
            int b1 = m.indexOf('.',i);
            if (b1==-1)
                break;
            int b2 = m.indexOf(" ",b1);
            if (b2==-1)
                break;
            String mb = m.substring(b1+1, b2);
            also+=c+"._"+mb+"/";
            i=b2;
            int b3 = m.indexOf(' ',i+1);
            if (b3==-1)
                break;
            System.out.println(b2+" "+b3);
            String mn = m.substring(b2+1, b3);
            also+=c+"..."+mn+"/";
            c++;
        }
        also = "https://en.wikibooks.org/wiki/Chess_Opening_Theory/" + also;
        also = also.substring(0, also.length()-1);
        
        return also;
    }
    

    
    public static String getLibraryOfCongress(String eco) {
        String name = "";
        name = mapaloc.get(eco);
        return name;
    }

    /**
     * *********************** PRIVATE METHODS *******************************
     */
    private static Map<String, String> mapa = init();
    private static Map<String, String> mapamoves = initmoves();
    private static Map<String, String> mapaloc = initloc();

    private static Map<String, String> initloc() {
        Map<String, String> map = new HashMap();

        String str = "";
        
       //English
        for (int i = 10; i < 39; i++) {
            map.put("A" + i, "http://id.loc.gov/authorities/subjects/sh00005411");
        }

       //Budapest
        for (int i = 51; i < 52; i++) {
            map.put("A" + i, "http://id.loc.gov/authorities/subjects/sh99002614");
        }

       //Benoni
        for (int i = 60; i < 79; i++) {

        }
        
        
       //Sicilian
        for (int i = 20; i < 59; i++) {
            map.put("B" + i, "http://id.loc.gov/authorities/subjects/sh2001008186");
        }
              
       //Sicilian Dragon
        for (int i = 60; i < 79; i++) {
            map.put("B" + i, "http://id.loc.gov/authorities/subjects/sh200100818");
        }
        
       //Sicilian
        for (int i = 80; i < 99; i++) {
            map.put("B" + i, "http://id.loc.gov/authorities/subjects/sh2001008186");
        }
        
        //King's gambit
        for (int i = 30; i < 39; i++) {
            map.put("C" + i, "http://id.loc.gov/authorities/subjects/sh98003564");
        }
        
        //Spanish
        for (int i = 60; i < 99; i++) {
            map.put("C" + i, "http://id.loc.gov/authorities/subjects/sh98003603");
        }

        //Queen's gambit
        for (int i = 06; i < 69; i++) {
            map.put("D" + i, "http://id.loc.gov/authorities/subjects/sh98003550");
        }

        //Grünefeld
        for (int i = 70; i < 99; i++) {
            map.put("D" + i, "http://id.loc.gov/authorities/subjects/sh2004005503");
        }
        
        
        map.put("E83", "http://id.loc.gov/authorities/subjects/sh00008710");

        return map;
    }

    private static Map<String, String> initmoves() {
        Map<String, String> mapa = new HashMap();
        mapa.put("A00", "1.g4, a3, h3, etc.");
        mapa.put("A01", "1.b3");
        mapa.put("A02", "1.f4");
        mapa.put("A03", "1.f4 d5");
        mapa.put("A04", "1.Nf3");
        mapa.put("A05", "1.Nf3 Nf6");
        mapa.put("A06", "1.Nf3 d5");
        mapa.put("A07", "1.Nf3 d5 2.g3");
        mapa.put("A08", "1.Nf3 d5 2.g3 c5 3.Bg2");
        mapa.put("A09", "1.Nf3 d5 2.c4");
        mapa.put("A10", "1.c4");
        mapa.put("A11", "1.c4 c6");
        mapa.put("A12", "1.c4 c6 2.Nf3 d5 3.b3");
        mapa.put("A13", "1.c4 e6");
        mapa.put("A14", "1.c4 e6 2.Nf3 d5 3.g3 Nf6 4.Bg2 Be7 5.O-O");
        mapa.put("A15", "1.c4 Nf6");
        mapa.put("A16", "1.c4 Nf6 2.Nc3");
        mapa.put("A17", "1.c4 Nf6 2.Nc3 e6");
        mapa.put("A18", "1.c4 Nf6 2.Nc3 e6 3.e4");
        mapa.put("A19", "1.c4 Nf6 2.Nc3 e6 3.e4 c5");
        mapa.put("A20", "1.c4 e5");
        mapa.put("A21", "1.c4 e5 2.Nc3");
        mapa.put("A22", "1.c4 e5 2.Nc3 Nf6");
        mapa.put("A23", "1.c4 e5 2.Nc3 Nf6 3.g3 c6");
        mapa.put("A24", "1.c4 e5 2.Nc3 Nf6 3.g3 g6");
        mapa.put("A25", "1.c4 e5 2.Nc3 Nc6");
        mapa.put("A26", "1.c4 e5 2.Nc3 Nc6 3.g3 g6 4.Bg2 Bg7 5.d3 d6");
        mapa.put("A27", "1.c4 e5 2.Nc3 Nc6 3.Nf3");
        mapa.put("A28", "1.c4 e5 2.Nc3 Nc6 3.Nf3 Nf6");
        mapa.put("A29", "1.c4 e5 2.Nc3 Nc6 3.Nf3 Nf6 4.g3");
        mapa.put("A30", "1.c4 c5");
        mapa.put("A31", "1.c4 c5 2.Nf3 Nf6 3.d4");
        mapa.put("A32", "1.c4 c5 2.Nf3 Nf6 3.d4 cxd4 4.Nxd4 e6");
        mapa.put("A33", "1.c4 c5 2.Nf3 Nf6 3.d4 cxd4 4.Nxd4 e6 5.Nc3 Nc6");
        mapa.put("A34", "1.c4 c5 2.Nc3");
        mapa.put("A35", "1.c4 c5 2.Nc3 Nc6");
        mapa.put("A36", "1.c4 c5 2.Nc3 Nc6 3.g3");
        mapa.put("A37", "1.c4 c5 2.Nc3 Nc6 3.g3 g6 4.Bg2 Bg7 5.Nf3");
        mapa.put("A38", "1.c4 c5 2.Nc3 Nc6 3.g3 g6 4.Bg2 Bg7 5.Nf3 Nf6");
        mapa.put("A39", "1.c4 c5 2.Nc3 Nc6 3.g3 g6 4.Bg2 Bg7 5.Nf3 Nf6 6.O-O O-O 7.d4");
        mapa.put("A40", "1.d4");
        mapa.put("A41", "1.d4 d6");
        mapa.put("A42", "1.d4 d6 2.c4 g6 3.Nc3 Bg7 4.e4");
        mapa.put("A43", "1.d4 c5");
        mapa.put("A44", "1.d4 c5 2.d5 e5");
        mapa.put("A45", "1.d4 Nf6");
        mapa.put("A46", "1.d4 Nf6 2.Nf3");
        mapa.put("A47", "1.d4 Nf6 2.Nf3 b6");
        mapa.put("A48", "1.d4 Nf6 2.Nf3 g6");
        mapa.put("A49", "1.d4 Nf6 2.Nf3 g6 3.g3");
        mapa.put("A50", "1.d4 Nf6 2.c4");
        mapa.put("A51", "1.d4 Nf6 2.c4 e5");
        mapa.put("A52", "1.d4 Nf6 2.c4 e5 3.dxe5 Ng4");
        mapa.put("A53", "1.d4 Nf6 2.c4 d6");
        mapa.put("A54", "1.d4 Nf6 2.c4 d6 3.Nc3 e5 4.Nf3");
        mapa.put("A55", "1.d4 Nf6 2.c4 d6 3.Nc3 e5 4.Nf3 Nbd7 5.e4");
        mapa.put("A56", "1.d4 Nf6 2.c4 c5");
        mapa.put("A57", "1.d4 Nf6 2.c4 c5 3.d5 b5");
        mapa.put("A58", "1.d4 Nf6 2.c4 c5 3.d5 b5 4.cxb5 a6 5.bxa6");
        mapa.put("A59", "1.d4 Nf6 2.c4 c5 3.d5 b5 4.cxb5 a6 5.bxa6 Bxa6 6.Nc3 d6 7.e4");
        mapa.put("A60", "1.d4 Nf6 2.c4 c5 3.d5 e6");
        mapa.put("A61", "1.d4 Nf6 2.c4 c5 3.d5 e6 4.Nc3 exd5 5.cxd5 d6 6.Nf3 g6");
        mapa.put("A62", "1.d4 Nf6 2.c4 c5 3.d5 e6 4.Nc3 exd5 5.cxd5 d6 6.Nf3 g6 7.g3 Bg7 8.Bg2 O-O");
        mapa.put("A63", "1.d4 Nf6 2.c4 c5 3.d5 e6 4.Nc3 exd5 5.cxd5 d6 6.Nf3 g6 7.g3 Bg7 8.Bg2 O-O 9.O-O Nbd7");
        mapa.put("A64", "1.d4 Nf6 2.c4 c5 3.d5 e6 4.Nc3 exd5 5.cxd5 d6 6.Nf3 g6 7.g3 Bg7 8.Bg2 O-O 9.O-O Nbd7 10.Nd2 a6 11.a4 Re8");
        mapa.put("A65", "1.d4 Nf6 2.c4 c5 3.d5 e6 4.Nc3 exd5 5.cxd5 d6 6.e4");
        mapa.put("A66", "1.d4 Nf6 2.c4 c5 3.d5 e6 4.Nc3 exd5 5.cxd5 d6 6.e4 g6 7.f4");
        mapa.put("A67", "1.d4 Nf6 2.c4 c5 3.d5 e6 4.Nc3 exd5 5.cxd5 d6 6.e4 g6 7.f4 Bg7 8.Bb5+");
        mapa.put("A68", "1.d4 Nf6 2.c4 c5 3.d5 e6 4.Nc3 exd5 5.cxd5 d6 6.e4 g6 7.f4 Bg7 8.Nf3 O-O");
        mapa.put("A69", "1.d4 Nf6 2.c4 c5 3.d5 e6 4.Nc3 exd5 5.cxd5 d6 6.e4 g6 7.f4 Bg7 8.Nf3 O-O 9.Be2 Re8");
        mapa.put("A70", "1.d4 Nf6 2.c4 c5 3.d5 e6 4.Nc3 exd5 5.cxd5 d6 6.e4 g6 7.Nf3");
        mapa.put("A71", "1.d4 Nf6 2.c4 c5 3.d5 e6 4.Nc3 exd5 5.cxd5 d6 6.e4 g6 7.Nf3 Bg7 8.Bg5");
        mapa.put("A72", "1.d4 Nf6 2.c4 c5 3.d5 e6 4.Nc3 exd5 5.cxd5 d6 6.e4 g6 7.Nf3 Bg7 8.Be2 O-O");
        mapa.put("A73", "1.d4 Nf6 2.c4 c5 3.d5 e6 4.Nc3 exd5 5.cxd5 d6 6.e4 g6 7.Nf3 Bg7 8.Be2 O-O 9.O-O");
        mapa.put("A74", "1.d4 Nf6 2.c4 c5 3.d5 e6 4.Nc3 exd5 5.cxd5 d6 6.e4 g6 7.Nf3 Bg7 8.Be2 O-O 9.O-O a6 10.a4");
        mapa.put("A75", "1.d4 Nf6 2.c4 c5 3.d5 e6 4.Nc3 exd5 5.cxd5 d6 6.e4 g6 7.Nf3 Bg7 8.Be2 O-O 9.O-O a6 10.a4 Bg4");
        mapa.put("A76", "1.d4 Nf6 2.c4 c5 3.d5 e6 4.Nc3 exd5 5.cxd5 d6 6.e4 g6 7.Nf3 Bg7 8.Be2 O-O 9.O-O Re8");
        mapa.put("A77", "1.d4 Nf6 2.c4 c5 3.d5 e6 4.Nc3 exd5 5.cxd5 d6 6.e4 g6 7.Nf3 Bg7 8.Be2 O-O 9.O-O Re8 10.Nd2");
        mapa.put("A78", "1.d4 Nf6 2.c4 c5 3.d5 e6 4.Nc3 exd5 5.cxd5 d6 6.e4 g6 7.Nf3 Bg7 8.Be2 O-O 9.O-O Re8 10.Nd2 Na6");
        mapa.put("A79", "1.d4 Nf6 2.c4 c5 3.d5 e6 4.Nc3 exd5 5.cxd5 d6 6.e4 g6 7.Nf3 Bg7 8.Be2 O-O 9.O-O Re8 10.Nd2 Na6 11.f3");
        mapa.put("A80", "1.d4 f5");
        mapa.put("A81", "1.d4 f5 2.g3");
        mapa.put("A82", "1.d4 f5 2.e4");
        mapa.put("A83", "1.d4 f5 2.e4 fxe4 3.Nc3 Nf6 4.Bg5");
        mapa.put("A84", "1.d4 f5 2.c4");
        mapa.put("A85", "1.d4 f5 2.c4 Nf6 3.Nc3");
        mapa.put("A86", "1.d4 f5 2.c4 Nf6 3.g3");
        mapa.put("A87", "1.d4 f5 2.c4 Nf6 3.g3 g6 4.Bg2 Bg7 5.Nf3");
        mapa.put("A88", "1.d4 f5 2.c4 Nf6 3.g3 g6 4.Bg2 Bg7 5.Nf3 O-O 6.O-O d6 7.Nc3 c6");
        mapa.put("A89", "1.d4 f5 2.c4 Nf6 3.g3 g6 4.Bg2 Bg7 5.Nf3 O-O 6.O-O d6 7.Nc3 Nc6");
        mapa.put("A90", "1.d4 f5 2.c4 Nf6 3.g3 e6 4.Bg2");
        mapa.put("A91", "1.d4 f5 2.c4 Nf6 3.g3 e6 4.Bg2 Be7");
        mapa.put("A92", "1.d4 f5 2.c4 Nf6 3.g3 e6 4.Bg2 Be7 5.Nf3 O-O");
        mapa.put("A93", "1.d4 f5 2.c4 Nf6 3.g3 e6 4.Bg2 Be7 5.Nf3 O-O 6.O-O d5 7.b3");
        mapa.put("A94", "1.d4 f5 2.c4 Nf6 3.g3 e6 4.Bg2 Be7 5.Nf3 O-O 6.O-O d5 7.b3 c6 8.Ba3");
        mapa.put("A95", "1.d4 f5 2.c4 Nf6 3.g3 e6 4.Bg2 Be7 5.Nf3 O-O 6.O-O d5 7.Nc3 c6");
        mapa.put("A96", "1.d4 f5 2.c4 Nf6 3.g3 e6 4.Bg2 Be7 5.Nf3 O-O 6.O-O d6");
        mapa.put("A97", "1.d4 f5 2.c4 Nf6 3.g3 e6 4.Bg2 Be7 5.Nf3 O-O 6.O-O d6 7.Nc3 Qe8");
        mapa.put("A98", "1.d4 f5 2.c4 Nf6 3.g3 e6 4.Bg2 Be7 5.Nf3 O-O 6.O-O d6 7.Nc3 Qe8 8.Qc2");
        mapa.put("A99", "1.d4 f5 2.c4 Nf6 3.g3 e6 4.Bg2 Be7 5.Nf3 O-O 6.O-O d6 7.Nc3 Qe8 8.b3");
        mapa.put("B00", "1.e4");
        mapa.put("B01", "1.e4 d5");
        mapa.put("B02", "1.e4 Nf6");
        mapa.put("B03", "1.e4 Nf6 2.e5 Nd5 3.d4");
        mapa.put("B04", "1.e4 Nf6 2.e5 Nd5 3.d4 d6 4.Nf3");
        mapa.put("B05", "1.e4 Nf6 2.e5 Nd5 3.d4 d6 4.Nf3 Bg4");
        mapa.put("B06", "1.e4 g6");
        mapa.put("B07", "1.e4 d6 2.d4 Nf6");
        mapa.put("B08", "1.e4 d6 2.d4 Nf6 3.Nc3 g6 4.Nf3");
        mapa.put("B09", "1.e4 d6 2.d4 Nf6 3.Nc3 g6 4.f4");
        mapa.put("B10", "1.e4 c6");
        mapa.put("B11", "1.e4 c6 2.Nc3 d5 3.Nf3 Bg4");
        mapa.put("B12", "1.e4 c6 2.d4");
        mapa.put("B13", "1.e4 c6 2.d4 d5 3.exd5 cxd5");
        mapa.put("B14", "1.e4 c6 2.d4 d5 3.exd5 cxd5 4.c4 Nf6 5.Nc3 e6");
        mapa.put("B15", "1.e4 c6 2.d4 d5 3.Nc3");
        mapa.put("B16", "1.e4 c6 2.d4 d5 3.Nc3 dxe4 4.Nxe4 Nf6 5.Nxf6+ gxf6");
        mapa.put("B17", "1.e4 c6 2.d4 d5 3.Nc3 dxe4 4.Nxe4 Nd7");
        mapa.put("B18", "1.e4 c6 2.d4 d5 3.Nc3 dxe4 4.Nxe4 Bf5");
        mapa.put("B19", "1.e4 c6 2.d4 d5 3.Nc3 dxe4 4.Nxe4 Bf5 5.Ng3 Bg6 6.h4 h6 7.Nf3 Nd7");
        mapa.put("B20", "1.e4 c5");
        mapa.put("B21", "1.e4 c5 2.f4");
        mapa.put("B22", "1.e4 c5 2.c3");
        mapa.put("B23", "1.e4 c5 2.Nc3");
        mapa.put("B24", "1.e4 c5 2.Nc3 Nc6 3.g3");
        mapa.put("B25", "1.e4 c5 2.Nc3 Nc6 3.g3 g6 4.Bg2 Bg7 5.d3 d6");
        mapa.put("B26", "1.e4 c5 2.Nc3 Nc6 3.g3 g6 4.Bg2 Bg7 5.d3 d6 6.Be3");
        mapa.put("B27", "1.e4 c5 2.Nf3");
        mapa.put("B28", "1.e4 c5 2.Nf3 a6");
        mapa.put("B29", "1.e4 c5 2.Nf3 Nf6");
        mapa.put("B30", "1.e4 c5 2.Nf3 Nc6");
        mapa.put("B31", "1.e4 c5 2.Nf3 Nc6 3.Bb5 g6");
        mapa.put("B32", "1.e4 c5 2.Nf3 Nc6 3.d4 cxd4 4.Nxd4 e5");
        mapa.put("B33", "1.e4 c5 2.Nf3 Nc6 3.d4 cxd4 4.Nxd4");
        mapa.put("B34", "1.e4 c5 2.Nf3 Nc6 3.d4 cxd4 4.Nxd4 g6 5.Nxc6");
        mapa.put("B35", "1.e4 c5 2.Nf3 Nc6 3.d4 cxd4 4.Nxd4 g6 5.Nc3 Bg7 6.Be3 Nf6 7.Bc4");
        mapa.put("B36", "1.e4 c5 2.Nf3 Nc6 3.d4 cxd4 4.Nxd4 g6 5.c4");
        mapa.put("B37", "1.e4 c5 2.Nf3 Nc6 3.d4 cxd4 4.Nxd4 g6 5.c4 Bg7");
        mapa.put("B38", "1.e4 c5 2.Nf3 Nc6 3.d4 cxd4 4.Nxd4 g6 5.c4 Bg7 6.Be3");
        mapa.put("B39", "1.e4 c5 2.Nf3 Nc6 3.d4 cxd4 4.Nxd4 g6 5.c4 Bg7 6.Be3 Nf6 7.Nc3 Ng4");
        mapa.put("B40", "1.e4 c5 2.Nf3 e6");
        mapa.put("B41", "1.e4 c5 2.Nf3 e6 3.d4 cxd4 4.Nxd4 a6");
        mapa.put("B42", "1.e4 c5 2.Nf3 e6 3.d4 cxd4 4.Nxd4 a6 5.Bd3");
        mapa.put("B43", "1.e4 c5 2.Nf3 e6 3.d4 cxd4 4.Nxd4 a6 5.Nc3");
        mapa.put("B44", "1.e4 c5 2.Nf3 e6 3.d4 cxd4 4.Nxd4 Nc6");
        mapa.put("B45", "1.e4 c5 2.Nf3 e6 3.d4 cxd4 4.Nxd4 Nc6 5.Nc3");
        mapa.put("B46", "1.e4 c5 2.Nf3 e6 3.d4 cxd4 4.Nxd4 Nc6 5.Nc3 a6");
        mapa.put("B47", "1.e4 c5 2.Nf3 e6 3.d4 cxd4 4.Nxd4 Nc6 5.Nc3 Qc7");
        mapa.put("B48", "1.e4 c5 2.Nf3 e6 3.d4 cxd4 4.Nxd4 Nc6 5.Nc3 Qc7 6.Be3");
        mapa.put("B49", "1.e4 c5 2.Nf3 e6 3.d4 cxd4 4.Nxd4 Nc6 5.Nc3 Qc7 6.Be3 a6 7.Be2");
        mapa.put("B50", "1.e4 c5 2.Nf3 d6");
        mapa.put("B51", "1.e4 c5 2.Nf3 d6 3.Bb5+");
        mapa.put("B52", "1.e4 c5 2.Nf3 d6 3.Bb5+ Bd7");
        mapa.put("B53", "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Qxd4");
        mapa.put("B54", "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4");
        mapa.put("B55", "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.f3 e5 6.Bb5+");
        mapa.put("B56", "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3");
        mapa.put("B57", "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 Nc6 6.Bc4");
        mapa.put("B58", "1.e4 c5 2.Nf3 Nc6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 d6 6.Be2");
        mapa.put("B59", "1.e4 c5 2.Nf3 Nc6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 d6 6.Be2 e5 7.Nb3");
        mapa.put("B60", "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 Nc6 6.Bg5");
        mapa.put("B61", "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 Nc6 6.Bg5 Bd7 7.Qd2");
        mapa.put("B62", "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 Nc6 6.Bg5 e6");
        mapa.put("B63", "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 Nc6 6.Bg5 e6 7.Qd2");
        mapa.put("B64", "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 Nc6 6.Bg5 e6 7.Qd2 Be7 8.O-O-O O-O 9.f4");
        mapa.put("B65", "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 Nc6 6.Bg5 e6 7.Qd2 Be7 8.O-O-O O-O 9.f4 Nxd4 10.Qxd4");
        mapa.put("B66", "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 Nc6 6.Bg5 e6 7.Qd2 a6");
        mapa.put("B67", "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 Nc6 6.Bg5 e6 7.Qd2 a6 8.O-O-O Bd7");
        mapa.put("B68", "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 Nc6 6.Bg5 e6 7.Qd2 a6 8.O-O-O Bd7 9.f4 Be7");
        mapa.put("B69", "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 Nc6 6.Bg5 e6 7.Qd2 a6 8.O-O-O Bd7 9.f4 Be7 10.Nf3 b5 11.Bxf6");
        mapa.put("B70", "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 g6");
        mapa.put("B71", "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 g6 6.f4");
        mapa.put("B72", "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 g6 6.Be3");
        mapa.put("B73", "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 g6 6.Be3 Bg7 7.Be2 Nc6 8.O-O");
        mapa.put("B74", "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 g6 6.Be3 Bg7 7.Be2 Nc6 8.O-O O-O 9.Nb3");
        mapa.put("B75", "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 g6 6.Be3 Bg7 7.f3");
        mapa.put("B76", "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 g6 6.Be3 Bg7 7.f3 O-O");
        mapa.put("B77", "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 g6 6.Be3 Bg7 7.f3 O-O 8.Qd2 Nc6 9.Bc4");
        mapa.put("B78", "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 g6 6.Be3 Bg7 7.f3 O-O 8.Qd2 Nc6 9.Bc4 Bd7 10.O-O-O");
        mapa.put("B79", "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 g6 6.Be3 Bg7 7.f3 O-O 8.Qd2 Nc6 9.Bc4 Bd7 10.O-O-O Qa5 11.Bb3 Rfc8 12.h4");
        mapa.put("B80", "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 e6");
        mapa.put("B81", "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 e6 6.g4");
        mapa.put("B82", "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 e6 6.f4");
        mapa.put("B83", "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 e6 6.Be2");
        mapa.put("B84", "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 e6 6.Be2 a6");
        mapa.put("B85", "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 e6 6.Be2 a6 7.O-O Qc7 8.f4 Nc6");
        mapa.put("B86", "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 e6 6.Bc4");
        mapa.put("B87", "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 e6 6.Bc4 a6 7.Bb3 b5");
        mapa.put("B88", "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 e6 6.Bc4 Nc6");
        mapa.put("B89", "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 e6 6.Bc4 Nc6 7.Be3");
        mapa.put("B90", "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 a6");
        mapa.put("B91", "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 a6 6.g3");
        mapa.put("B92", "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 a6 6.Be2");
        mapa.put("B93", "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 a6 6.f4");
        mapa.put("B94", "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 a6 6.Bg5");
        mapa.put("B95", "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 a6 6.Bg5 e6");
        mapa.put("B96", "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 a6 6.Bg5 e6 7.f4");
        mapa.put("B97", "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 a6 6.Bg5 e6 7.f4 Qb6");
        mapa.put("B98", "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 a6 6.Bg5 e6 7.f4 Be7");
        mapa.put("B99", "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 a6 6.Bg5 e6 7.f4 Be7 8.Qf3 Qc7 9.O-O-O Nbd7");
        mapa.put("C00", "1.e4 e6");
        mapa.put("C01", "1.e4 e6 2.d4 d5 3.exd5 exd5 4.Nc3 Nf6 5.Bg5");
        mapa.put("C02", "1.e4 e6 2.d4 d5 3.e5");
        mapa.put("C03", "1.e4 e6 2.d4 d5 3.Nd2");
        mapa.put("C04", "1.e4 e6 2.d4 d5 3.Nd2 Nc6 4.Ngf3 Nf6");
        mapa.put("C05", "1.e4 e6 2.d4 d5 3.Nd2 Nf6");
        mapa.put("C06", "1.e4 e6 2.d4 d5 3.Nd2 Nf6 4.e5 Nfd7 5.Bd3 c5 6.c3 Nc6 7.Ne2 cxd4 8.cxd4");
        mapa.put("C07", "1.e4 e6 2.d4 d5 3.Nd2 c5");
        mapa.put("C08", "1.e4 e6 2.d4 d5 3.Nd2 c5 4.exd5 exd5");
        mapa.put("C09", "1.e4 e6 2.d4 d5 3.Nd2 c5 4.exd5 exd5 5.Ngf3 Nc6");
        mapa.put("C10", "1.e4 e6 2.d4 d5 3.Nc3");
        mapa.put("C11", "1.e4 e6 2.d4 d5 3.Nc3 Nf6");
        mapa.put("C12", "1.e4 e6 2.d4 d5 3.Nc3 Nf6 4.Bg5 Bb4");
        mapa.put("C13", "1.e4 e6 2.d4 d5 3.Nc3 Nf6 4.Bg5 Be7");
        mapa.put("C14", "1.e4 e6 2.d4 d5 3.Nc3 Nf6 4.Bg5 Be7 5.e5 Nfd7 6.Bxe7 Qxe7");
        mapa.put("C15", "1.e4 e6 2.d4 d5 3.Nc3 Bb4");
        mapa.put("C16", "1.e4 e6 2.d4 d5 3.Nc3 Bb4 4.e5");
        mapa.put("C17", "1.e4 e6 2.d4 d5 3.Nc3 Bb4 4.e5 c5");
        mapa.put("C18", "1.e4 e6 2.d4 d5 3.Nc3 Bb4 4.e5 c5 5.a3 Bxc3+ 6.bxc3");
        mapa.put("C19", "1.e4 e6 2.d4 d5 3.Nc3 Bb4 4.e5 c5 5.a3 Bxc3+ 6.bxc3 Ne7");
        mapa.put("C20", "1.e4 e5");
        mapa.put("C21", "1.e4 e5 2.d4 exd4");
        mapa.put("C22", "1.e4 e5 2.d4 exd4 3.Qxd4 Nc6");
        mapa.put("C23", "1.e4 e5 2.Bc4");
        mapa.put("C24", "1.e4 e5 2.Bc4 Nf6");
        mapa.put("C25", "1.e4 e5 2.Nc3");
        mapa.put("C26", "1.e4 e5 2.Nc3 Nf6");
        mapa.put("C27", "1.e4 e5 2.Nc3 Nf6 3.Bc4 Nxe4");
        mapa.put("C28", "1.e4 e5 2.Nc3 Nf6 3.Bc4 Nc6");
        mapa.put("C29", "1.e4 e5 2.Nc3 Nf6 3.f4");
        mapa.put("C30", "1.e4 e5 2.f4");
        mapa.put("C31", "1.e4 e5 2.f4 d5");
        mapa.put("C32", "1.e4 e5 2.f4 d5 3.exd5 e4 4.d3 Nf6");
        mapa.put("C33", "1.e4 e5 2.f4 exf4");
        mapa.put("C34", "1.e4 e5 2.f4 exf4 3.Nf3");
        mapa.put("C35", "1.e4 e5 2.f4 exf4 3.Nf3 Be7");
        mapa.put("C36", "1.e4 e5 2.f4 exf4 3.Nf3 d5");
        mapa.put("C37", "1.e4 e5 2.f4 exf4 3.Nf3 g5 4.Nc3");
        mapa.put("C38", "1.e4 e5 2.f4 exf4 3.Nf3 g5 4.Bc4 Bg7");
        mapa.put("C39", "1.e4 e5 2.f4 exf4 3.Nf3 g5 4.h4");
        mapa.put("C40", "1.e4 e5 2.Nf3");
        mapa.put("C41", "1.e4 e5 2.Nf3 d6");
        mapa.put("C42", "1.e4 e5 2.Nf3 Nf6");
        mapa.put("C43", "1.e4 e5 2.Nf3 Nf6 3.d4 exd4 4.e5 Ne4 5.Qxd4");
        mapa.put("C44", "1.e4 e5 2.Nf3 Nc6");
        mapa.put("C45", "1.e4 e5 2.Nf3 Nc6 3.d4 exd4 4.Nxd4");
        mapa.put("C46", "1.e4 e5 2.Nf3 Nc6 3.Nc3");
        mapa.put("C47", "1.e4 e5 2.Nf3 Nc6 3.Nc3 Nf6");
        mapa.put("C48", "1.e4 e5 2.Nf3 Nc6 3.Nc3 Nf6 4.Bb5");
        mapa.put("C49", "1.e4 e5 2.Nf3 Nc6 3.Nc3 Nf6 4.Bb5 Bb4");
        mapa.put("C50", "1.e4 e5 2.Nf3 Nc6 3.Bc4 Bc5");
        mapa.put("C51", "1.e4 e5 2.Nf3 Nc6 3.Bc4 Bc5 4.b4");
        mapa.put("C52", "1.e4 e5 2.Nf3 Nc6 3.Bc4 Bc5 4.b4 Bxb4 5.c3 Ba5");
        mapa.put("C53", "1.e4 e5 2.Nf3 Nc6 3.Bc4 Bc5 4.c3");
        mapa.put("C54", "1.e4 e5 2.Nf3 Nc6 3.Bc4 Bc5 4.c3 Nf6 5.d4 exd4 6.cxd4");
        mapa.put("C55", "1.e4 e5 2.Nf3 Nc6 3.Bc4 Nf6");
        mapa.put("C56", "1.e4 e5 2.Nf3 Nc6 3.Bc4 Nf6 4.d4 exd4 5.O-O Nxe4");
        mapa.put("C57", "1.e4 e5 2.Nf3 Nc6 3.Bc4 Nf6 4.Ng5");
        mapa.put("C58", "1.e4 e5 2.Nf3 Nc6 3.Bc4 Nf6 4.Ng5 d5 5.exd5 Na5");
        mapa.put("C59", "1.e4 e5 2.Nf3 Nc6 3.Bc4 Nf6 4.Ng5 d5 5.exd5 Na5 6.Bb5+ c6 7.dxc6 bxc6 8.Be2 h6");
        mapa.put("C60", "1.e4 e5 2.Nf3 Nc6 3.Bb5");
        mapa.put("C61", "1.e4 e5 2.Nf3 Nc6 3.Bb5 Nd4");
        mapa.put("C62", "1.e4 e5 2.Nf3 Nc6 3.Bb5 d6");
        mapa.put("C63", "1.e4 e5 2.Nf3 Nc6 3.Bb5 f5");
        mapa.put("C64", "1.e4 e5 2.Nf3 Nc6 3.Bb5 Bc5");
        mapa.put("C65", "1.e4 e5 2.Nf3 Nc6 3.Bb5 Nf6");
        mapa.put("C66", "1.e4 e5 2.Nf3 Nc6 3.Bb5 Nf6 4.O-O d6");
        mapa.put("C67", "1.e4 e5 2.Nf3 Nc6 3.Bb5 Nf6 4.O-O Nxe4");
        mapa.put("C68", "1.e4 e5 2.Nf3 Nc6 3.Bb5 a6 4.Bxc6");
        mapa.put("C69", "1.e4 e5 2.Nf3 Nc6 3.Bb5 a6 4.Bxc6 dc 5.O-O f6 6.d4");
        mapa.put("C70", "1.e4 e5 2.Nf3 Nc6 3.Bb5 a6 4.Ba4");
        mapa.put("C71", "1.e4 e5 2.Nf3 Nc6 3.Bb5 a6 4.Ba4 d6");
        mapa.put("C72", "1.e4 e5 2.Nf3 Nc6 3.Bb5 a6 4.Ba4 d6 5.O-O");
        mapa.put("C73", "1.e4 e5 2.Nf3 Nc6 3.Bb5 a6 4.Ba4 d6 5.Bxc6+ bxc6 6.d4");
        mapa.put("C74", "1.e4 e5 2.Nf3 Nc6 3.Bb5 a6 4.Ba4 d6 5.c3");
        mapa.put("C75", "1.e4 e5 2.Nf3 Nc6 3.Bb5 a6 4.Ba4 d6 5.c3 Bd7");
        mapa.put("C76", "1.e4 e5 2.Nf3 Nc6 3.Bb5 a6 4.Ba4 d6 5.c3 Bd7 6.d4 g6");
        mapa.put("C77", "1.e4 e5 2.Nf3 Nc6 3.Bb5 a6 4.Ba4 Nf6");
        mapa.put("C78", "1.e4 e5 2.Nf3 Nc6 3.Bb5 a6 4.Ba4 Nf6 5.O-O");
        mapa.put("C79", "1.e4 e5 2.Nf3 Nc6 3.Bb5 a6 4.Ba4 Nf6 5.O-O d6");
        mapa.put("C80", "1.e4 e5 2.Nf3 Nc6 3.Bb5 a6 4.Ba4 Nf6 5.O-O Nxe4");
        mapa.put("C81", "1.e4 e5 2.Nf3 Nc6 3.Bb5 a6 4.Ba4 Nf6 5.O-O Nxe4 6.d4 b5 7.Bb3 d5 8.dxe5 Be6");
        mapa.put("C82", "1.e4 e5 2.Nf3 Nc6 3.Bb5 a6 4.Ba4 Nf6 5.O-O Nxe4 6.d4 b5 7.Bb3 d5 8.dxe5 Be6 9.c3");
        mapa.put("C83", "1.e4 e5 2.Nf3 Nc6 3.Bb5 a6 4.Ba4 Nf6 5.O-O Nxe4 6.d4 b5 7.Bb3 d5 8.dxe5 Be6");
        mapa.put("C84", "1.e4 e5 2.Nf3 Nc6 3.Bb5 a6 4.Ba4 Nf6 5.O-O Be7");
        mapa.put("C85", "1.e4 e5 2.Nf3 Nc6 3.Bb5 a6 4.Ba4 Nf6 5.O-O Be7 6.Bxc6 dxc6");
        mapa.put("C86", "1.e4 e5 2.Nf3 Nc6 3.Bb5 a6 4.Ba4 Nf6 5.O-O Be7 6.Qe2");
        mapa.put("C87", "1.e4 e5 2.Nf3 Nc6 3.Bb5 a6 4.Ba4 Nf6 5.O-O Be7 6.Re1 d6");
        mapa.put("C88", "1.e4 e5 2.Nf3 Nc6 3.Bb5 a6 4.Ba4 Nf6 5.O-O Be7 6.Re1 b5 7.Bb3");
        mapa.put("C89", "1.e4 e5 2.Nf3 Nc6 3.Bb5 a6 4.Ba4 Nf6 5.O-O Be7 6.Re1 b5 7.Bb3 O-O 8.c3 d5");
        mapa.put("C90", "1.e4 e5 2.Nf3 Nc6 3.Bb5 a6 4.Ba4 Nf6 5.O-O Be7 6.Re1 b5 7.Bb3 O-O 8.c3 d6");
        mapa.put("C91", "1.e4 e5 2.Nf3 Nc6 3.Bb5 a6 4.Ba4 Nf6 5.O-O Be7 6.Re1 b5 7.Bb3 O-O 8.c3 d6 9.d4");
        mapa.put("C92", "1.e4 e5 2.Nf3 Nc6 3.Bb5 a6 4.Ba4 Nf6 5.O-O Be7 6.Re1 b5 7.Bb3 O-O 8.c3 d6 9.h3");
        mapa.put("C93", "1.e4 e5 2.Nf3 Nc6 3.Bb5 a6 4.Ba4 Nf6 5.O-O Be7 6.Re1 b5 7.Bb3 O-O 8.c3 d6 9.h3 h6");
        mapa.put("C94", "1.e4 e5 2.Nf3 Nc6 3.Bb5 a6 4.Ba4 Nf6 5.O-O Be7 6.Re1 b5 7.Bb3 O-O 8.c3 d6 9.h3 Nb8");
        mapa.put("C95", "1.e4 e5 2.Nf3 Nc6 3.Bb5 a6 4.Ba4 Nf6 5.O-O Be7 6.Re1 b5 7.Bb3 O-O 8.c3 d6 9.h3 Nb8 10.d4");
        mapa.put("C96", "1.e4 e5 2.Nf3 Nc6 3.Bb5 a6 4.Ba4 Nf6 5.O-O Be7 6.Re1 b5 7.Bb3 O-O 8.c3 d6 9.h3 Na5 10.Bc2");
        mapa.put("C97", "1.e4 e5 2.Nf3 Nc6 3.Bb5 a6 4.Ba4 Nf6 5.O-O Be7 6.Re1 b5 7.Bb3 O-O 8.c3 d6 9.h3 Na5 10.Bc2 c5 11.d4 Qc7");
        mapa.put("C98", "1.e4 e5 2.Nf3 Nc6 3.Bb5 a6 4.Ba4 Nf6 5.O-O Be7 6.Re1 b5 7.Bb3 O-O 8.c3 d6 9.h3 Na5 10.Bc2 c5 11.d4 Qc7 12.Nbd2 Nc6");
        mapa.put("C99", "1.e4 e5 2.Nf3 Nc6 3.Bb5 a6 4.Ba4 Nf6 5.O-O Be7 6.Re1 b5 7.Bb3 O-O 8.c3 d6 9.h3 Na5 10.Bc2 c5 11.d4 Qc7 12.Nbd2 cxd4 13.cxd4");
        mapa.put("D00", "1.d4 d5");
        mapa.put("D01", "1.d4 d5 2.Nc3 Nf6 3.Bg5");
        mapa.put("D02", "1.d4 d5 2.Nf3");
        mapa.put("D03", "1.d4 d5 2.Nf3 Nf6 3.Bg5");
        mapa.put("D04", "1.d4 d5 2.Nf3 Nf6 3.e3");
        mapa.put("D05", "1.d4 d5 2.Nf3 Nf6 3.e3 e6");
        mapa.put("D06", "1.d4 d5 2.c4");
        mapa.put("D07", "1.d4 d5 2.c4 Nc6");
        mapa.put("D08", "1.d4 d5 2.c4 e5");
        mapa.put("D09", "1.d4 d5 2.c4 e5 3.dxe5 d4 4.Nf3 Nc6 5.g3");
        mapa.put("D10", "1.d4 d5 2.c4 c6");
        mapa.put("D11", "1.d4 d5 2.c4 c6 3.Nf3");
        mapa.put("D12", "1.d4 d5 2.c4 c6 3.Nf3 Nf6 4.e3 Bf5");
        mapa.put("D13", "1.d4 d5 2.c4 c6 3.Nf3 Nf6 4.cxd5 cxd5");
        mapa.put("D14", "1.d4 d5 2.c4 c6 3.Nf3 Nf6 4.cxd5 cxd5 5.Nc3 Nc6 6.Bf4 Bf5");
        mapa.put("D15", "1.d4 d5 2.c4 c6 3.Nf3 Nf6 4.Nc3");
        mapa.put("D16", "1.d4 d5 2.c4 c6 3.Nf3 Nf6 4.Nc3 dxc4 5.a4");
        mapa.put("D17", "1.d4 d5 2.c4 c6 3.Nf3 Nf6 4.Nc3 dxc4 5.a4 Bf5");
        mapa.put("D18", "1.d4 d5 2.c4 c6 3.Nf3 Nf6 4.Nc3 dxc4 5.a4 Bf5 6.e3");
        mapa.put("D19", "1.d4 d5 2.c4 c6 3.Nf3 Nf6 4.Nc3 dxc4 5.a4 Bf5 6.e3 e6 7.Bxc4 Bb4 8.O-O O-O 9.Qe2");
        mapa.put("D20", "1.d4 d5 2.c4 dxc4");
        mapa.put("D21", "1.d4 d5 2.c4 dxc4 3.Nf3");
        mapa.put("D22", "1.d4 d5 2.c4 dxc4 3.Nf3 a6 4.e3 Bg4 5.Bxc4 e6 6.d5");
        mapa.put("D23", "1.d4 d5 2.c4 dxc4 3.Nf3 Nf6");
        mapa.put("D24", "1.d4 d5 2.c4 dxc4 3.Nf3 Nf6 4.Nc3");
        mapa.put("D25", "1.d4 d5 2.c4 dxc4 3.Nf3 Nf6 4.e3");
        mapa.put("D26", "1.d4 d5 2.c4 dxc4 3.Nf3 Nf6 4.e3 e6");
        mapa.put("D27", "1.d4 d5 2.c4 dxc4 3.Nf3 Nf6 4.e3 e6 5.Bxc4 c5 6.O-O a6");
        mapa.put("D28", "1.d4 d5 2.c4 dxc4 3.Nf3 Nf6 4.e3 e6 5.Bxc4 c5 6.O-O a6 7.Qe2");
        mapa.put("D29", "1.d4 d5 2.c4 dxc4 3.Nf3 Nf6 4.e3 e6 5.Bxc4 c5 6.O-O a6 7.Qe2 b5 8.Bb3 Bb7");
        mapa.put("D30", "1.d4 d5 2.c4 e6");
        mapa.put("D31", "1.d4 d5 2.c4 e6 3.Nc3");
        mapa.put("D32", "1.d4 d5 2.c4 e6 3.Nc3 c5");
        mapa.put("D33", "1.d4 d5 2.c4 e6 3.Nc3 c5 4.cxd5 exd5 5.Nf3 Nc6 6.g3");
        mapa.put("D34", "1.d4 d5 2.c4 e6 3.Nc3 c5 4.cxd5 exd5 5.Nf3 Nc6 6.g3 Nf6 7.Bg2 Be7");
        mapa.put("D35", "1.d4 d5 2.c4 e6 3.Nc3 Nf6");
        mapa.put("D36", "1.d4 d5 2.c4 e6 3.Nc3 Nf6 4.cxd5 exd5 5.Bg5 c6 6.Qc2");
        mapa.put("D37", "1.d4 d5 2.c4 e6 3.Nc3 Nf6 4.Nf3");
        mapa.put("D38", "1.d4 d5 2.c4 e6 3.Nc3 Nf6 4.Nf3 Bb4");
        mapa.put("D39", "1.d4 d5 2.c4 e6 3.Nc3 Nf6 4.Nf3 Bb4 5.Bg5 dxc4");
        mapa.put("D40", "1.d4 d5 2.c4 e6 3.Nc3 Nf6 4.Nf3 c5");
        mapa.put("D41", "1.d4 d5 2.c4 e6 3.Nc3 Nf6 4.Nf3 c5 5.cxd5");
        mapa.put("D42", "1.d4 d5 2.c4 e6 3.Nc3 Nf6 4.Nf3 c5 5.cxd5 Nxd5 6.e3 Nc6 7.Bd3");
        mapa.put("D43", "1.d4 d5 2.c4 e6 3.Nc3 Nf6 4.Nf3 c6");
        mapa.put("D44", "1.d4 d5 2.c4 e6 3.Nc3 Nf6 4.Nf3 c6 5.Bg5 dxc4");
        mapa.put("D45", "1.d4 d5 2.c4 e6 3.Nc3 Nf6 4.Nf3 c6 5.e3");
        mapa.put("D46", "1.d4 d5 2.c4 e6 3.Nc3 Nf6 4.Nf3 c6 5.e3 Nbd7 6.Bd3");
        mapa.put("D47", "1.d4 d5 2.c4 e6 3.Nc3 Nf6 4.Nf3 c6 5.e3 Nbd7 6.Bd3 dxc4 7.Bxc4");
        mapa.put("D48", "1.d4 d5 2.c4 e6 3.Nc3 Nf6 4.Nf3 c6 5.e3 Nbd7 6.Bd3 dxc4 7.Bxc4 b5 8.Bd3 a6");
        mapa.put("D49", "1.d4 d5 2.c4 e6 3.Nc3 Nf6 4.Nf3 c6 5.e3 Nbd7 6.Bd3 dxc4 7.Bxc4 b5 8.Bd3 a6 9.e4 c5 10.e5 cxd4 11.Nxb5");
        mapa.put("D50", "1.d4 d5 2.c4 e6 3.Nc3 Nf6 4.Bg5");
        mapa.put("D51", "1.d4 d5 2.c4 e6 3.Nc3 Nf6 4.Bg5 Nbd7");
        mapa.put("D52", "1.d4 d5 2.c4 e6 3.Nc3 Nf6 4.Bg5 Nbd7 5.e3 c6 6.Nf3");
        mapa.put("D53", "1.d4 d5 2.c4 e6 3.Nc3 Nf6 4.Bg5 Be7");
        mapa.put("D54", "1.d4 d5 2.c4 e6 3.Nc3 Nf6 4.Bg5 Be7 5.e3 O-O 6.Rc1");
        mapa.put("D55", "1.d4 d5 2.c4 e6 3.Nc3 Nf6 4.Bg5 Be7 5.e3 O-O 6.Nf3");
        mapa.put("D56", "1.d4 d5 2.c4 e6 3.Nc3 Nf6 4.Bg5 Be7 5.e3 O-O 6.Nf3 h6 7.Bh4");
        mapa.put("D57", "1.d4 d5 2.c4 e6 3.Nc3 Nf6 4.Bg5 Be7 5.e3 O-O 6.Nf3 h6 7.Bh4 Ne4 8.Bxe7 Qxe7");
        mapa.put("D58", "1.d4 d5 2.c4 e6 3.Nc3 Nf6 4.Bg5 Be7 5.e3 O-O 6.Nf3 h6 7.Bh4 b6");
        mapa.put("D59", "1.d4 d5 2.c4 e6 3.Nc3 Nf6 4.Bg5 Be7 5.e3 O-O 6.Nf3 h6 7.Bh4 b6 8.cxd5 Nxd5");
        mapa.put("D60", "1.d4 d5 2.c4 e6 3.Nc3 Nf6 4.Bg5 Be7 5.e3 O-O 6.Nf3 Nbd7");
        mapa.put("D61", "1.d4 d5 2.c4 e6 3.Nc3 Nf6 4.Bg5 Be7 5.e3 O-O 6.Nf3 Nbd7 7.Qc2");
        mapa.put("D62", "1.d4 d5 2.c4 e6 3.Nc3 Nf6 4.Bg5 Be7 5.e3 O-O 6.Nf3 Nbd7 7.Qc2 c5 8.cxd5");
        mapa.put("D63", "1.d4 d5 2.c4 e6 3.Nc3 Nf6 4.Bg5 Be7 5.e3 O-O 6.Nf3 Nbd7 7.Rc1");
        mapa.put("D64", "1.d4 d5 2.c4 e6 3.Nc3 Nf6 4.Bg5 Be7 5.e3 O-O 6.Nf3 Nbd7 7.Rc1 c6 8.Qc2");
        mapa.put("D65", "1.d4 d5 2.c4 e6 3.Nc3 Nf6 4.Bg5 Be7 5.e3 O-O 6.Nf3 Nbd7 7.Rc1 c6 8.Qc2 a6 9.cxd5");
        mapa.put("D66", "1.d4 d5 2.c4 e6 3.Nc3 Nf6 4.Bg5 Be7 5.e3 O-O 6.Nf3 Nbd7 7.Rc1 c6 8.Bd3");
        mapa.put("D67", "1.d4 d5 2.c4 e6 3.Nc3 Nf6 4.Bg5 Be7 5.e3 O-O 6.Nf3 Nbd7 7.Rc1 c6 8.Bd3 dxc4 9.Bxc4 Nd5");
        mapa.put("D68", "1.d4 d5 2.c4 e6 3.Nc3 Nf6 4.Bg5 Be7 5.e3 O-O 6.Nf3 Nbd7 7.Rc1 c6 8.Bd3 dxc4");
        mapa.put("D69", "1.d4 d5 2.c4 e6 3.Nc3 Nf6 4.Bg5 Be7 5.e3 O-O 6.Nf3 Nbd7 7.Rc1 c6 8.Bd3 dxc4");
        mapa.put("D70", "1.d4 Nf6 2.c4 g6 3.f3 d5");
        mapa.put("D71", "1.d4 Nf6 2.c4 g6 3.g3 d5");
        mapa.put("D72", "1.d4 Nf6 2.c4 g6 3.g3 d5 4.Bg2 Bg7 5.cxd5 Nxd5 6.e4 Nb6 7.Ne2");
        mapa.put("D73", "1.d4 Nf6 2.c4 g6 3.g3 d5 4.Bg2 Bg7 5.Nf3");
        mapa.put("D74", "1.d4 Nf6 2.c4 g6 3.g3 d5 4.Bg2 Bg7 5.Nf3 O-O 6.cxd5 Nxd5 7.O-O");
        mapa.put("D75", "1.d4 Nf6 2.c4 g6 3.g3 d5 4.Bg2 Bg7 5.Nf3 O-O 6.cxd5 Nxd5 7.O-O c5 8.dxc5");
        mapa.put("D76", "1.d4 Nf6 2.c4 g6 3.g3 d5 4.Bg2 Bg7 5.Nf3 O-O 6.cxd5 Nxd5 7.O-O Nb6");
        mapa.put("D77", "1.d4 Nf6 2.c4 g6 3.g3 d5 4.Bg2 Bg7 5.Nf3 O-O 6.O-O");
        mapa.put("D78", "1.d4 Nf6 2.c4 g6 3.g3 d5 4.Bg2 Bg7 5.Nf3 O-O 6.O-O c6");
        mapa.put("D79", "1.d4 Nf6 2.c4 g6 3.g3 d5 4.Bg2 Bg7 5.Nf3 O-O 6.O-O c6 7.cxd5 cxd5");
        mapa.put("D80", "1.d4 Nf6 2.c4 g6 3.Nc3 d5");
        mapa.put("D81", "1.d4 Nf6 2.c4 g6 3.Nc3 d5 4.Qb3");
        mapa.put("D82", "1.d4 Nf6 2.c4 g6 3.Nc3 d5 4.Bf4");
        mapa.put("D83", "1.d4 Nf6 2.c4 g6 3.Nc3 d5 4.Bf4 Bg7 5.e3 O-O");
        mapa.put("D84", "1.d4 Nf6 2.c4 g6 3.Nc3 d5 4.Bf4 Bg7 5.e3 O-O 6.cxd5 Nxd5 7.Nxd5 Qxd5 8.Bxc7");
        mapa.put("D85", "1.d4 Nf6 2.c4 g6 3.Nc3 d5 4.cxd5 Nxd5");
        mapa.put("D86", "1.d4 Nf6 2.c4 g6 3.Nc3 d5 4.cxd5 Nxd5 5.e4 Nxc3 6.bxc3 Bg7 7.Bc4");
        mapa.put("D87", "1.d4 Nf6 2.c4 g6 3.Nc3 d5 4.cxd5 Nxd5 5.e4 Nxc3 6.bxc3 Bg7 7.Bc4 O-O 8.Ne2 c5");
        mapa.put("D88", "1.d4 Nf6 2.c4 g6 3.Nc3 d5 4.cxd5 Nxd5 5.e4 Nxc3 6.bxc3 Bg7 7.Bc4 O-O 8.Ne2");
        mapa.put("D89", "1.d4 Nf6 2.c4 g6 3.Nc3 d5 4.cxd5 Nxd5 5.e4 Nxc3 6.bxc3 Bg7 7.Bc4 O-O 8.Ne2");
        mapa.put("D90", "1.d4 Nf6 2.c4 g6 3.Nc3 d5 4.Nf3");
        mapa.put("D91", "1.d4 Nf6 2.c4 g6 3.Nc3 d5 4.Nf3 Bg7 5.Bg5");
        mapa.put("D92", "1.d4 Nf6 2.c4 g6 3.Nc3 d5 4.Nf3 Bg7 5.Bf4");
        mapa.put("D93", "1.d4 Nf6 2.c4 g6 3.Nc3 d5 4.Nf3 Bg7 5.Bf4 O-O 6.e3");
        mapa.put("D94", "1.d4 Nf6 2.c4 g6 3.Nc3 d5 4.Nf3 Bg7 5.e3");
        mapa.put("D95", "1.d4 Nf6 2.c4 g6 3.Nc3 d5 4.Nf3 Bg7 5.e3 O-O 6.Qb3");
        mapa.put("D96", "1.d4 Nf6 2.c4 g6 3.Nc3 d5 4.Nf3 Bg7 5.Qb3");
        mapa.put("D97", "1.d4 Nf6 2.c4 g6 3.Nc3 d5 4.Nf3 Bg7 5.Qb3 dxc4 6.Qxc4 O-O 7.e4");
        mapa.put("D98", "1.d4 Nf6 2.c4 g6 3.Nc3 d5 4.Nf3 Bg7 5.Qb3 dxc4 6.Qxc4 O-O 7.e4 Bg4");
        mapa.put("D99", "1.d4 Nf6 2.c4 g6 3.Nc3 d5 4.Nf3 Bg7 5.Qb3 dxc4 6.Qxc4 O-O 7.e4 Bg4 8.Be3");
        mapa.put("E00", "1.d4 Nf6 2.c4 e6");
        mapa.put("E01", "1.d4 Nf6 2.c4 e6 3.g3 d5 4.Bg2");
        mapa.put("E02", "1.d4 Nf6 2.c4 e6 3.g3 d5 4.Bg2 dxc4 5.Qa4+");
        mapa.put("E03", "1.d4 Nf6 2.c4 e6 3.g3 d5 4.Bg2 dxc4 5.Qa4+ Nbd7 6.Qxc4");
        mapa.put("E04", "1.d4 Nf6 2.c4 e6 3.g3 d5 4.Bg2 dxc4 5.Nf3");
        mapa.put("E05", "1.d4 Nf6 2.c4 e6 3.g3 d5 4.Bg2 dxc4 5.Nf3 Be7");
        mapa.put("E06", "1.d4 Nf6 2.c4 e6 3.g3 d5 4.Bg2 Be7 5.Nf3");
        mapa.put("E07", "1.d4 Nf6 2.c4 e6 3.g3 d5 4.Bg2 Be7 5.Nf3 O-O 6.O-O Nbd7");
        mapa.put("E08", "1.d4 Nf6 2.c4 e6 3.g3 d5 4.Bg2 Be7 5.Nf3 O-O 6.O-O Nbd7 7.Qc2");
        mapa.put("E09", "1.d4 Nf6 2.c4 e6 3.g3 d5 4.Bg2 Be7 5.Nf3 O-O 6.O-O Nbd7 7.Qc2 c6 8.Nbd2");
        mapa.put("E10", "1.d4 Nf6 2.c4 e6 3.Nf3");
        mapa.put("E11", "1.d4 Nf6 2.c4 e6 3.Nf3 Bb4+");
        mapa.put("E12", "1.d4 Nf6 2.c4 e6 3.Nf3 b6");
        mapa.put("E13", "1.d4 Nf6 2.c4 e6 3.Nf3 b6 4.Nc3 Bb7 5.Bg5 h6 6.Bh4 Bb4");
        mapa.put("E14", "1.d4 Nf6 2.c4 e6 3.Nf3 b6 4.e3");
        mapa.put("E15", "1.d4 Nf6 2.c4 e6 3.Nf3 b6 4.g3");
        mapa.put("E16", "1.d4 Nf6 2.c4 e6 3.Nf3 b6 4.g3 Bb7 5.Bg2 Bb4+");
        mapa.put("E17", "1.d4 Nf6 2.c4 e6 3.Nf3 b6 4.g3 Bb7 5.Bg2 Be7");
        mapa.put("E18", "1.d4 Nf6 2.c4 e6 3.Nf3 b6 4.g3 Bb7 5.Bg2 Be7 6.O-O O-O 7.Nc3");
        mapa.put("E19", "1.d4 Nf6 2.c4 e6 3.Nf3 b6 4.g3 Bb7 5.Bg2 Be7 6.O-O O-O 7.Nc3 Ne4 8.Qc2 Nxc3");
        mapa.put("E20", "1.d4 Nf6 2.c4 e6 3.Nc3 Bb4");
        mapa.put("E21", "1.d4 Nf6 2.c4 e6 3.Nc3 Bb4 4.Nf3");
        mapa.put("E22", "1.d4 Nf6 2.c4 e6 3.Nc3 Bb4 4.Qb3");
        mapa.put("E23", "1.d4 Nf6 2.c4 e6 3.Nc3 Bb4 4.Qb3 c5 5.dxc5 Nc6");
        mapa.put("E24", "1.d4 Nf6 2.c4 e6 3.Nc3 Bb4 4.a3 Bxc3+ 5.bxc3");
        mapa.put("E25", "1.d4 Nf6 2.c4 e6 3.Nc3 Bb4 4.a3 Bxc3+ 5.bxc3 c5 6.f3 d5 7.cxd5");
        mapa.put("E26", "1.d4 Nf6 2.c4 e6 3.Nc3 Bb4 4.a3 Bxc3+ 5.bxc3 c5 6.e3");
        mapa.put("E27", "1.d4 Nf6 2.c4 e6 3.Nc3 Bb4 4.a3 Bxc3+ 5.bxc3 O-O");
        mapa.put("E28", "1.d4 Nf6 2.c4 e6 3.Nc3 Bb4 4.a3 Bxc3+ 5.bxc3 O-O 6.e3");
        mapa.put("E29", "1.d4 Nf6 2.c4 e6 3.Nc3 Bb4 4.a3 Bxc3+ 5.bxc3 O-O 6.e3 c5 7.Bd3 Nc6");
        mapa.put("E30", "1.d4 Nf6 2.c4 e6 3.Nc3 Bb4 4.Bg5");
        mapa.put("E31", "1.d4 Nf6 2.c4 e6 3.Nc3 Bb4 4.Bg5 h6 5.Bh4 c5 6.d5 d6");
        mapa.put("E32", "1.d4 Nf6 2.c4 e6 3.Nc3 Bb4 4.Qc2");
        mapa.put("E33", "1.d4 Nf6 2.c4 e6 3.Nc3 Bb4 4.Qc2 Nc6");
        mapa.put("E34", "1.d4 Nf6 2.c4 e6 3.Nc3 Bb4 4.Qc2 d5");
        mapa.put("E35", "1.d4 Nf6 2.c4 e6 3.Nc3 Bb4 4.Qc2 d5 5.cxd5 exd5");
        mapa.put("E36", "1.d4 Nf6 2.c4 e6 3.Nc3 Bb4 4.Qc2 d5 5.a3");
        mapa.put("E37", "1.d4 Nf6 2.c4 e6 3.Nc3 Bb4 4.Qc2 d5 5.a3 Bxc3+ 6.Qxc3 Ne4 7.Qc2");
        mapa.put("E38", "1.d4 Nf6 2.c4 e6 3.Nc3 Bb4 4.Qc2 c5");
        mapa.put("E39", "1.d4 Nf6 2.c4 e6 3.Nc3 Bb4 4.Qc2 c5 5.dxc5 O-O");
        mapa.put("E40", "1.d4 Nf6 2.c4 e6 3.Nc3 Bb4 4.e3");
        mapa.put("E41", "1.d4 Nf6 2.c4 e6 3.Nc3 Bb4 4.e3 c5");
        mapa.put("E42", "1.d4 Nf6 2.c4 e6 3.Nc3 Bb4 4.e3 c5 5.Ne2");
        mapa.put("E43", "1.d4 Nf6 2.c4 e6 3.Nc3 Bb4 4.e3 b6");
        mapa.put("E44", "1.d4 Nf6 2.c4 e6 3.Nc3 Bb4 4.e3 b6 5.Ne2");
        mapa.put("E45", "1.d4 Nf6 2.c4 e6 3.Nc3 Bb4 4.e3 b6 5.Ne2 Ba6");
        mapa.put("E46", "1.d4 Nf6 2.c4 e6 3.Nc3 Bb4 4.e3 O-O");
        mapa.put("E47", "1.d4 Nf6 2.c4 e6 3.Nc3 Bb4 4.e3 O-O 5.Bd3");
        mapa.put("E48", "1.d4 Nf6 2.c4 e6 3.Nc3 Bb4 4.e3 O-O 5.Bd3 d5");
        mapa.put("E49", "1.d4 Nf6 2.c4 e6 3.Nc3 Bb4 4.e3 O-O 5.Bd3 d5 6.a3 Bxc3+ 7.bxc3");
        mapa.put("E50", "1.d4 Nf6 2.c4 e6 3.Nc3 Bb4 4.e3 O-O 5.Nf3");
        mapa.put("E51", "1.d4 Nf6 2.c4 e6 3.Nc3 Bb4 4.e3 O-O 5.Nf3 d5");
        mapa.put("E52", "1.d4 Nf6 2.c4 e6 3.Nc3 Bb4 4.e3 O-O 5.Nf3 d5 6.Bd3 b6");
        mapa.put("E53", "1.d4 Nf6 2.c4 e6 3.Nc3 Bb4 4.e3 O-O 5.Nf3 d5 6.Bd3 c5");
        mapa.put("E54", "1.d4 Nf6 2.c4 e6 3.Nc3 Bb4 4.e3 O-O 5.Nf3 d5 6.Bd3 c5 7.O-O dxc4 8.Bxc4");
        mapa.put("E55", "1.d4 Nf6 2.c4 e6 3.Nc3 Bb4 4.e3 O-O 5.Nf3 d5 6.Bd3 c5 7.O-O dxc4 8.Bxc4 Nbd7");
        mapa.put("E56", "1.d4 Nf6 2.c4 e6 3.Nc3 Bb4 4.e3 O-O 5.Nf3 d5 6.Bd3 c5 7.O-O Nc6");
        mapa.put("E57", "1.d4 Nf6 2.c4 e6 3.Nc3 Bb4 4.e3 O-O 5.Nf3 d5 6.Bd3 c5 7.O-O Nc6 8.a3 dxc4 9.Bxc4");
        mapa.put("E58", "1.d4 Nf6 2.c4 e6 3.Nc3 Bb4 4.e3 O-O 5.Nf3 d5 6.Bd3 c5 7.O-O Nc6 8.a3 Bxc3 9.bxc3");
        mapa.put("E59", "1.d4 Nf6 2.c4 e6 3.Nc3 Bb4 4.e3 O-O 5.Nf3 d5 6.Bd3 c5 7.O-O Nc6 8.a3 Bxc3 9.bxc3 dxc4 10.Bxc4");
        mapa.put("E60", "1.d4 Nf6 2.c4 g6");
        mapa.put("E61", "1.d4 Nf6 2.c4 g6 3.Nc3");
        mapa.put("E62", "1.d4 Nf6 2.c4 g6 3.Nc3 Bg7 4.Nf3 d6 5.g3");
        mapa.put("E63", "1.d4 Nf6 2.c4 g6 3.Nc3 Bg7 4.Nf3 d6 5.g3 O-O 6.Bg2 Nc6 7.O-O a6");
        mapa.put("E64", "1.d4 Nf6 2.c4 g6 3.Nc3 Bg7 4.Nf3 d6 5.g3 O-O 6.Bg2 c5");
        mapa.put("E65", "1.d4 Nf6 2.c4 g6 3.Nc3 Bg7 4.Nf3 d6 5.g3 O-O 6.Bg2 c5 7.O-O");
        mapa.put("E66", "1.d4 Nf6 2.c4 g6 3.Nc3 Bg7 4.Nf3 d6 5.g3 O-O 6.Bg2 c5 7.O-O Nc6 8.d5");
        mapa.put("E67", "1.d4 Nf6 2.c4 g6 3.Nc3 Bg7 4.Nf3 d6 5.g3 O-O 6.Bg2 Nbd7");
        mapa.put("E68", "1.d4 Nf6 2.c4 g6 3.Nc3 Bg7 4.Nf3 d6 5.g3 O-O 6.Bg2 Nbd7 7.O-O e5 8.e4");
        mapa.put("E69", "1.d4 Nf6 2.c4 g6 3.Nc3 Bg7 4.Nf3 d6 5.g3 O-O 6.Bg2 Nbd7 7.O-O e5 8.e4 c6 9.h3");
        mapa.put("E70", "1.d4 Nf6 2.c4 g6 3.Nc3 Bg7 4.e4");
        mapa.put("E71", "1.d4 Nf6 2.c4 g6 3.Nc3 Bg7 4.e4 d6 5.h3");
        mapa.put("E72", "1.d4 Nf6 2.c4 g6 3.Nc3 Bg7 4.e4 d6 5.g3");
        mapa.put("E73", "1.d4 Nf6 2.c4 g6 3.Nc3 Bg7 4.e4 d6 5.Be2");
        mapa.put("E74", "1.d4 Nf6 2.c4 g6 3.Nc3 Bg7 4.e4 d6 5.Be2 O-O 6.Bg5 c5");
        mapa.put("E75", "1.d4 Nf6 2.c4 g6 3.Nc3 Bg7 4.e4 d6 5.Be2 O-O 6.Bg5 c5 7.d5 e6");
        mapa.put("E76", "1.d4 Nf6 2.c4 g6 3.Nc3 Bg7 4.e4 d6 5.f4");
        mapa.put("E77", "1.d4 Nf6 2.c4 g6 3.Nc3 Bg7 4.e4 d6 5.f4 O-O 6.Be2");
        mapa.put("E78", "1.d4 Nf6 2.c4 g6 3.Nc3 Bg7 4.e4 d6 5.f4 O-O 6.Be2 c5 7.Nf3");
        mapa.put("E79", "1.d4 Nf6 2.c4 g6 3.Nc3 Bg7 4.e4 d6 5.f4 O-O 6.Be2 c5 7.Nf3 cxd4 8.Nxd4 Nc6 9.Be3");
        mapa.put("E80", "1.d4 Nf6 2.c4 g6 3.Nc3 Bg7 4.e4 d6 5.f3");
        mapa.put("E81", "1.d4 Nf6 2.c4 g6 3.Nc3 Bg7 4.e4 d6 5.f3 O-O");
        mapa.put("E82", "1.d4 Nf6 2.c4 g6 3.Nc3 Bg7 4.e4 d6 5.f3 O-O 6.Be3 b6");
        mapa.put("E83", "1.d4 Nf6 2.c4 g6 3.Nc3 Bg7 4.e4 d6 5.f3 O-O 6.Be3 Nc6");
        mapa.put("E84", "1.d4 Nf6 2.c4 g6 3.Nc3 Bg7 4.e4 d6 5.f3 O-O 6.Be3 Nc6 7.Nge2 a6 8.Qd2 Rb8");
        mapa.put("E85", "1.d4 Nf6 2.c4 g6 3.Nc3 Bg7 4.e4 d6 5.f3 O-O 6.Be3 e5");
        mapa.put("E86", "1.d4 Nf6 2.c4 g6 3.Nc3 Bg7 4.e4 d6 5.f3 O-O 6.Be3 e5 7.Nge2 c6");
        mapa.put("E87", "1.d4 Nf6 2.c4 g6 3.Nc3 Bg7 4.e4 d6 5.f3 O-O 6.Be3 e5 7.d5");
        mapa.put("E88", "1.d4 Nf6 2.c4 g6 3.Nc3 Bg7 4.e4 d6 5.f3 O-O 6.Be3 e5 7.d5 c6");
        mapa.put("E89", "1.d4 Nf6 2.c4 g6 3.Nc3 Bg7 4.e4 d6 5.f3 O-O 6.Be3 e5 7.d5 c6 8.Nge2 cxd5");
        mapa.put("E90", "1.d4 Nf6 2.c4 g6 3.Nc3 Bg7 4.e4 d6 5.Nf3");
        mapa.put("E91", "1.d4 Nf6 2.c4 g6 3.Nc3 Bg7 4.e4 d6 5.Nf3 O-O 6.Be2");
        mapa.put("E92", "1.d4 Nf6 2.c4 g6 3.Nc3 Bg7 4.e4 d6 5.Nf3 O-O 6.Be2 e5");
        mapa.put("E93", "1.d4 Nf6 2.c4 g6 3.Nc3 Bg7 4.e4 d6 5.Nf3 O-O 6.Be2 e5 7.d5 Nbd7");
        mapa.put("E94", "1.d4 Nf6 2.c4 g6 3.Nc3 Bg7 4.e4 d6 5.Nf3 O-O 6.Be2 e5 7.O-O");
        mapa.put("E95", "1.d4 Nf6 2.c4 g6 3.Nc3 Bg7 4.e4 d6 5.Nf3 O-O 6.Be2 e5 7.O-O Nbd7 8.Re1");
        mapa.put("E96", "1.d4 Nf6 2.c4 g6 3.Nc3 Bg7 4.e4 d6 5.Nf3 O-O 6.Be2 e5 7.O-O Nbd7 8.Re1 c6 9.Bf1 a5");
        mapa.put("E97", "1.d4 Nf6 2.c4 g6 3.Nc3 Bg7 4.e4 d6 5.Nf3 O-O 6.Be2 e5 7.O-O Nc6");
        mapa.put("E98", "1.d4 Nf6 2.c4 g6 3.Nc3 Bg7 4.e4 d6 5.Nf3 O-O 6.Be2 e5 7.O-O Nc6 8.d5 Ne7 9.Ne1");
        mapa.put("E99", "1.d4 Nf6 2.c4 g6 3.Nc3 Bg7 4.e4 d6 5.Nf3 O-O 6.Be2 e5 7.O-O Nc6 8.d5 Ne7 9.Ne1 Nd7 10.f3 f5");

        return mapa;

    }

    private static Map<String, String> init() {
        Map<String, String> mapa = new HashMap();
        mapa.put("A00", "Uncommon Opening");
        mapa.put("A01", "Nimzovich-Larsen Attack");
        mapa.put("A02", "Bird's Opening");
        mapa.put("A03", "Bird's Opening");
        mapa.put("A04", "Reti Opening");
        mapa.put("A05", "Reti Opening");
        mapa.put("A06", "Reti Opening");
        mapa.put("A07", "King's Indian Attack");
        mapa.put("A08", "King's Indian Attack");
        mapa.put("A09", "Reti Opening");
        mapa.put("A10", "English");
        mapa.put("A11", "English, Caro-Kann Defensive System");
        mapa.put("A12", "English with b3");
        mapa.put("A13", "English");
        mapa.put("A14", "English");
        mapa.put("A15", "English");
        mapa.put("A16", "English");
        mapa.put("A17", "English");
        mapa.put("A18", "English, Mikenas-Carls");
        mapa.put("A19", "English, Mikenas-Carls, Sicilian Variation");
        mapa.put("A20", "English");
        mapa.put("A21", "English");
        mapa.put("A22", "English");
        mapa.put("A23", "English, Bremen System, Keres Variation");
        mapa.put("A24", "English, Bremen System with ...g6");
        mapa.put("A25", "English");
        mapa.put("A26", "English");
        mapa.put("A27", "English, Three Knights System");
        mapa.put("A28", "English");
        mapa.put("A29", "English, Four Knights, Kingside Fianchetto");
        mapa.put("A30", "English, Symmetrical");
        mapa.put("A31", "English, Symmetrical, Benoni Formation");
        mapa.put("A32", "English, Symmetrical Variation");
        mapa.put("A33", "English, Symmetrical");
        mapa.put("A34", "English, Symmetrical");
        mapa.put("A35", "English, Symmetrical");
        mapa.put("A36", "English");
        mapa.put("A37", "English, Symmetrical");
        mapa.put("A38", "English, Symmetrical");
        mapa.put("A39", "English, Symmetrical, Main line with d4");
        mapa.put("A40", "Queen's Pawn Game");
        mapa.put("A41", "Queen's Pawn Game (with ...d6)");
        mapa.put("A42", "Modern Defense, Averbakh System");
        mapa.put("A43", "Old Benoni");
        mapa.put("A44", "Old Benoni Defense");
        mapa.put("A45", "Queen's Pawn Game");
        mapa.put("A46", "Queen's Pawn Game");
        mapa.put("A47", "Queen's Indian");
        mapa.put("A48", "King's Indian");
        mapa.put("A49", "King's Indian, Fianchetto without c4");
        mapa.put("A50", "Queen's Pawn Game");
        mapa.put("A51", "Budapest Gambit");
        mapa.put("A52", "Budapest Gambit");
        mapa.put("A53", "Old Indian");
        mapa.put("A54", "Old Indian, Ukrainian Variation, 4.Nf3");
        mapa.put("A55", "Old Indian, Main line");
        mapa.put("A56", "Benoni Defense");
        mapa.put("A57", "Benko Gambit");
        mapa.put("A58", "Benko Gambit");
        mapa.put("A59", "Benko Gambit");
        mapa.put("A60", "Benoni Defense");
        mapa.put("A61", "Benoni");
        mapa.put("A62", "Benoni, Fianchetto Variation");
        mapa.put("A63", "Benoni, Fianchetto, 9...Nbd7");
        mapa.put("A64", "Benoni, Fianchetto, 11...Re8");
        mapa.put("A65", "Benoni, 6.e4");
        mapa.put("A66", "Benoni");
        mapa.put("A67", "Benoni, Taimanov Variation");
        mapa.put("A68", "Benoni, Four Pawns Attack");
        mapa.put("A69", "Benoni, Four Pawns Attack, Main line");
        mapa.put("A70", "Benoni, Classical with 7.Nf3");
        mapa.put("A71", "Benoni, Classical, 8.Bg5");
        mapa.put("A72", "Benoni, Classical without 9.O-O");
        mapa.put("A73", "Benoni, Classical, 9.O-O");
        mapa.put("A74", "Benoni, Classical, 9...a6, 10.a4");
        mapa.put("A75", "Benoni, Classical with ...a6 and 10...Bg4");
        mapa.put("A76", "Benoni, Classical, 9...Re8");
        mapa.put("A77", "Benoni, Classical, 9...Re8, 10.Nd2");
        mapa.put("A78", "Benoni, Classical with ...Re8 and ...Na6");
        mapa.put("A79", "Benoni, Classical, 11.f3");
        mapa.put("A80", "Dutch");
        mapa.put("A81", "Dutch");
        mapa.put("A82", "Dutch, Staunton Gambit");
        mapa.put("A83", "Dutch, Staunton Gambit");
        mapa.put("A84", "Dutch");
        mapa.put("A85", "Dutch, with c4 & Nc3");
        mapa.put("A86", "Dutch");
        mapa.put("A87", "Dutch, Leningrad, Main Variation");
        mapa.put("A88", "Dutch, Leningrad, Main Variation with c6");
        mapa.put("A89", "Dutch, Leningrad, Main Variation with Nc6");
        mapa.put("A90", "Dutch");
        mapa.put("A91", "Dutch Defense");
        mapa.put("A92", "Dutch");
        mapa.put("A93", "Dutch, Stonewall, Botvinnik Variation");
        mapa.put("A94", "Dutch, Stonewall with Ba3");
        mapa.put("A95", "Dutch, Stonewall");
        mapa.put("A96", "Dutch, Classical Variation");
        mapa.put("A97", "Dutch, Ilyin-Genevsky");
        mapa.put("A98", "Dutch, Ilyin-Genevsky Variation with Qc2");
        mapa.put("A99", "Dutch, Ilyin-Genevsky Variation with b3");
        mapa.put("B00", "Uncommon King's Pawn Opening");
        mapa.put("B01", "Scandinavian");
        mapa.put("B02", "Alekhine's Defense");
        mapa.put("B03", "Alekhine's Defense");
        mapa.put("B04", "Alekhine's Defense, Modern");
        mapa.put("B05", "Alekhine's Defense, Modern");
        mapa.put("B06", "Robatsch");
        mapa.put("B07", "Pirc");
        mapa.put("B08", "Pirc, Classical");
        mapa.put("B09", "Pirc, Austrian Attack");
        mapa.put("B10", "Caro-Kann");
        mapa.put("B11", "Caro-Kann, Two Knights, 3...Bg4");
        mapa.put("B12", "Caro-Kann Defense");
        mapa.put("B13", "Caro-Kann, Exchange");
        mapa.put("B14", "Caro-Kann, Panov-Botvinnik Attack");
        mapa.put("B15", "Caro-Kann");
        mapa.put("B16", "Caro-Kann, Bronstein-Larsen Variation");
        mapa.put("B17", "Caro-Kann, Steinitz Variation");
        mapa.put("B18", "Caro-Kann, Classical");
        mapa.put("B19", "Caro-Kann, Classical");
        mapa.put("B20", "Sicilian");
        mapa.put("B21", "Sicilian, 2.f4 and 2.d4");
        mapa.put("B22", "Sicilian, Alapin");
        mapa.put("B23", "Sicilian, Closed");
        mapa.put("B24", "Sicilian, Closed");
        mapa.put("B25", "Sicilian, Closed");
        mapa.put("B26", "Sicilian, Closed, 6.Be3");
        mapa.put("B27", "Sicilian");
        mapa.put("B28", "Sicilian, O'Kelly Variation");
        mapa.put("B29", "Sicilian, Nimzovich-Rubinstein");
        mapa.put("B30", "Sicilian");
        mapa.put("B31", "Sicilian, Rossolimo Variation");
        mapa.put("B32", "Sicilian");
        mapa.put("B33", "Sicilian");
        mapa.put("B34", "Sicilian, Accelerated Fianchetto");
        mapa.put("B35", "Sicilian, Accelerated Fianchetto, Modern Variation with Bc4");
        mapa.put("B36", "Sicilian, Accelerated Fianchetto");
        mapa.put("B37", "Sicilian, Accelerated Fianchetto");
        mapa.put("B38", "Sicilian, Accelerated Fianchetto, Maroczy Bind, 6.Be3");
        mapa.put("B39", "Sicilian, Accelerated Fianchetto, Breyer Variation");
        mapa.put("B40", "Sicilian");
        mapa.put("B41", "Sicilian, Kan");
        mapa.put("B42", "Sicilian, Kan");
        mapa.put("B43", "Sicilian, Kan, 5.Nc3");
        mapa.put("B44", "Sicilian");
        mapa.put("B45", "Sicilian, Taimanov");
        mapa.put("B46", "Sicilian, Taimanov Variation");
        mapa.put("B47", "Sicilian, Taimanov (Bastrikov) Variation");
        mapa.put("B48", "Sicilian, Taimanov Variation");
        mapa.put("B49", "Sicilian, Taimanov Variation");
        mapa.put("B50", "Sicilian");
        mapa.put("B51", "Sicilian, Canal-Sokolsky (Rossolimo) Attack");
        mapa.put("B52", "Sicilian, Canal-Sokolsky (Rossolimo) Attack");
        mapa.put("B53", "Sicilian");
        mapa.put("B54", "Sicilian");
        mapa.put("B55", "Sicilian, Prins Variation, Venice Attack");
        mapa.put("B56", "Sicilian");
        mapa.put("B57", "Sicilian");
        mapa.put("B58", "Sicilian");
        mapa.put("B59", "Sicilian, Boleslavsky Variation, 7.Nb3");
        mapa.put("B60", "Sicilian, Richter-Rauzer");
        mapa.put("B61", "Sicilian, Richter-Rauzer, Larsen Variation, 7.Qd2");
        mapa.put("B62", "Sicilian, Richter-Rauzer");
        mapa.put("B63", "Sicilian, Richter-Rauzer Attack");
        mapa.put("B64", "Sicilian, Richter-Rauzer Attack");
        mapa.put("B65", "Sicilian, Richter-Rauzer Attack, 7...Be7 Defense, 9...Nxd4");
        mapa.put("B66", "Sicilian, Richter-Rauzer Attack, 7...a6");
        mapa.put("B67", "Sicilian, Richter-Rauzer Attack, 7...a6 Defense, 8...Bd7");
        mapa.put("B68", "Sicilian, Richter-Rauzer Attack, 7...a6 Defense, 9...Be7");
        mapa.put("B69", "Sicilian, Richter-Rauzer Attack, 7...a6 Defense, 11.Bxf6");
        mapa.put("B70", "Sicilian, Dragon Variation");
        mapa.put("B71", "Sicilian, Dragon, Levenfish Variation");
        mapa.put("B72", "Sicilian, Dragon");
        mapa.put("B73", "Sicilian, Dragon, Classical");
        mapa.put("B74", "Sicilian, Dragon, Classical");
        mapa.put("B75", "Sicilian, Dragon, Yugoslav Attack");
        mapa.put("B76", "Sicilian, Dragon, Yugoslav Attack");
        mapa.put("B77", "Sicilian, Dragon, Yugoslav Attack");
        mapa.put("B78", "Sicilian, Dragon, Yugoslav Attack, 10.castle long");
        mapa.put("B79", "Sicilian, Dragon, Yugoslav Attack, 12.h4");
        mapa.put("B80", "Sicilian, Scheveningen");
        mapa.put("B81", "Sicilian, Scheveningen, Keres Attack");
        mapa.put("B82", "Sicilian, Scheveningen");
        mapa.put("B83", "Sicilian");
        mapa.put("B84", "Sicilian, Scheveningen");
        mapa.put("B85", "Sicilian, Scheveningen, Classical");
        mapa.put("B86", "Sicilian, Fischer-Sozin Attack");
        mapa.put("B87", "Sicilian, Fischer-Sozin with ...a6 and ...b5");
        mapa.put("B88", "Sicilian, Fischer-Sozin Attack");
        mapa.put("B89", "Sicilian");
        mapa.put("B90", "Sicilian, Najdorf");
        mapa.put("B91", "Sicilian, Najdorf, Zagreb (Fianchetto) Variation");
        mapa.put("B92", "Sicilian, Najdorf, Opocensky Variation");
        mapa.put("B93", "Sicilian, Najdorf, 6.f4");
        mapa.put("B94", "Sicilian, Najdorf");
        mapa.put("B95", "Sicilian, Najdorf, 6...e6");
        mapa.put("B96", "Sicilian, Najdorf");
        mapa.put("B97", "Sicilian, Najdorf");
        mapa.put("B98", "Sicilian, Najdorf");
        mapa.put("B99", "Sicilian, Najdorf, 7...Be7 Main line");
        mapa.put("C00", "French Defense");
        mapa.put("C01", "French, Exchange");
        mapa.put("C02", "French, Advance");
        mapa.put("C03", "French, Tarrasch");
        mapa.put("C04", "French, Tarrasch, Guimard Main line");
        mapa.put("C05", "French, Tarrasch");
        mapa.put("C06", "French, Tarrasch");
        mapa.put("C07", "French, Tarrasch");
        mapa.put("C08", "French, Tarrasch, Open, 4.ed ed");
        mapa.put("C09", "French, Tarrasch, Open Variation, Main line");
        mapa.put("C10", "French");
        mapa.put("C11", "French");
        mapa.put("C12", "French, McCutcheon");
        mapa.put("C13", "French");
        mapa.put("C14", "French, Classical");
        mapa.put("C15", "French, Winawer");
        mapa.put("C16", "French, Winawer");
        mapa.put("C17", "French, Winawer, Advance");
        mapa.put("C18", "French, Winawer");
        mapa.put("C19", "French, Winawer, Advance");
        mapa.put("C20", "King's Pawn Game");
        mapa.put("C21", "Center Game");
        mapa.put("C22", "Center Game");
        mapa.put("C23", "Bishop's Opening");
        mapa.put("C24", "Bishop's Opening");
        mapa.put("C25", "Vienna");
        mapa.put("C26", "Vienna");
        mapa.put("C27", "Vienna Game");
        mapa.put("C28", "Vienna Game");
        mapa.put("C29", "Vienna Gambit");
        mapa.put("C30", "King's Gambit Declined");
        mapa.put("C31", "King's Gambit Declined, Falkbeer Counter Gambit");
        mapa.put("C32", "King's Gambit Declined, Falkbeer Counter Gambit");
        mapa.put("C33", "King's Gambit Accepted");
        mapa.put("C34", "King's Gambit Accepted");
        mapa.put("C35", "King's Gambit Accepted, Cunningham");
        mapa.put("C36", "King's Gambit Accepted, Abbazia Defense");
        mapa.put("C37", "King's Gambit Accepted");
        mapa.put("C38", "King's Gambit Accepted");
        mapa.put("C39", "King's Gambit Accepted");
        mapa.put("C40", "King's Knight Opening");
        mapa.put("C41", "Philidor Defense");
        mapa.put("C42", "Petrov Defense");
        mapa.put("C43", "Petrov, Modern Attack");
        mapa.put("C44", "King's Pawn Game");
        mapa.put("C45", "Scotch Game");
        mapa.put("C46", "Three Knights");
        mapa.put("C47", "Four Knights");
        mapa.put("C48", "Four Knights");
        mapa.put("C49", "Four Knights");
        mapa.put("C50", "Giuoco Piano");
        mapa.put("C51", "Evans Gambit");
        mapa.put("C52", "Evans Gambit");
        mapa.put("C53", "Giuoco Piano");
        mapa.put("C54", "Giuoco Piano");
        mapa.put("C55", "Two Knights Defense");
        mapa.put("C56", "Two Knights");
        mapa.put("C57", "Two Knights");
        mapa.put("C58", "Two Knights");
        mapa.put("C59", "Two Knights");
        mapa.put("C60", "Ruy Lopez");
        mapa.put("C61", "Ruy Lopez, Bird's Defense");
        mapa.put("C62", "Ruy Lopez, Old Steinitz Defense");
        mapa.put("C63", "Ruy Lopez, Schliemann Defense");
        mapa.put("C64", "Ruy Lopez, Classical");
        mapa.put("C65", "Ruy Lopez, Berlin Defense");
        mapa.put("C66", "Ruy Lopez");
        mapa.put("C67", "Ruy Lopez");
        mapa.put("C68", "Ruy Lopez, Exchange");
        mapa.put("C69", "Ruy Lopez, Exchange, Gligoric Variation, 6.d4");
        mapa.put("C70", "Ruy Lopez");
        mapa.put("C71", "Ruy Lopez");
        mapa.put("C72", "Ruy Lopez, Modern Steinitz Defense, 5.O-O");
        mapa.put("C73", "Ruy Lopez, Modern Steinitz Defense");
        mapa.put("C74", "Ruy Lopez, Modern Steinitz Defense");
        mapa.put("C75", "Ruy Lopez, Modern Steinitz Defense");
        mapa.put("C76", "Ruy Lopez, Modern Steinitz Defense, Fianchetto Variation");
        mapa.put("C77", "Ruy Lopez");
        mapa.put("C78", "Ruy Lopez");
        mapa.put("C79", "Ruy Lopez, Steinitz Defense Deferred");
        mapa.put("C80", "Ruy Lopez, Open");
        mapa.put("C81", "Ruy Lopez, Open, Howell Attack");
        mapa.put("C82", "Ruy Lopez, Open");
        mapa.put("C83", "Ruy Lopez, Open");
        mapa.put("C84", "Ruy Lopez, Closed");
        mapa.put("C85", "Ruy Lopez, Exchange Variation Doubly Deferred (DERLD)");
        mapa.put("C86", "Ruy Lopez, Worrall Attack");
        mapa.put("C87", "Ruy Lopez");
        mapa.put("C88", "Ruy Lopez");
        mapa.put("C89", "Ruy Lopez, Marshall");
        mapa.put("C90", "Ruy Lopez, Closed");
        mapa.put("C91", "Ruy Lopez, Closed");
        mapa.put("C92", "Ruy Lopez, Closed");
        mapa.put("C93", "Ruy Lopez, Closed, Smyslov Defense");
        mapa.put("C94", "Ruy Lopez, Closed, Breyer Defense");
        mapa.put("C95", "Ruy Lopez, Closed, Breyer");
        mapa.put("C96", "Ruy Lopez, Closed");
        mapa.put("C97", "Ruy Lopez, Closed, Chigorin");
        mapa.put("C98", "Ruy Lopez, Closed, Chigorin");
        mapa.put("C99", "Ruy Lopez, Closed, Chigorin, 12...cd");
        mapa.put("D00", "Queen's Pawn Game");
        mapa.put("D01", "Richter-Veresov Attack");
        mapa.put("D02", "Queen's Pawn Game");
        mapa.put("D03", "Torre Attack (Tartakower Variation)");
        mapa.put("D04", "Queen's Pawn Game");
        mapa.put("D05", "Queen's Pawn Game");
        mapa.put("D06", "Queen's Gambit Declined");
        mapa.put("D07", "Queen's Gambit Declined, Chigorin Defense");
        mapa.put("D08", "Queen's Gambit Declined, Albin Counter Gambit");
        mapa.put("D09", "Queen's Gambit Declined, Albin Counter Gambit, 5.g3");
        mapa.put("D10", "Queen's Gambit Declined Slav");
        mapa.put("D11", "Queen's Gambit Declined Slav");
        mapa.put("D12", "Queen's Gambit Declined Slav");
        mapa.put("D13", "Queen's Gambit Declined Slav, Exchange Variation");
        mapa.put("D14", "Queen's Gambit Declined Slav, Exchange Variation");
        mapa.put("D15", "Queen's Gambit Declined Slav");
        mapa.put("D16", "Queen's Gambit Declined Slav");
        mapa.put("D17", "Queen's Gambit Declined Slav");
        mapa.put("D18", "Queen's Gambit Declined Slav, Dutch");
        mapa.put("D19", "Queen's Gambit Declined Slav, Dutch");
        mapa.put("D20", "Queen's Gambit Accepted");
        mapa.put("D21", "Queen's Gambit Accepted");
        mapa.put("D22", "Queen's Gambit Accepted");
        mapa.put("D23", "Queen's Gambit Accepted");
        mapa.put("D24", "Queen's Gambit Accepted");
        mapa.put("D25", "Queen's Gambit Accepted");
        mapa.put("D26", "Queen's Gambit Accepted");
        mapa.put("D27", "Queen's Gambit Accepted, Classical");
        mapa.put("D28", "Queen's Gambit Accepted, Classical");
        mapa.put("D29", "Queen's Gambit Accepted, Classical");
        mapa.put("D30", "Queen's Gambit Declined");
        mapa.put("D31", "Queen's Gambit Declined");
        mapa.put("D32", "Queen's Gambit Declined, Tarrasch");
        mapa.put("D33", "Queen's Gambit Declined, Tarrasch");
        mapa.put("D34", "Queen's Gambit Declined, Tarrasch");
        mapa.put("D35", "Queen's Gambit Declined");
        mapa.put("D36", "Queen's Gambit Declined, Exchange, Positional line, 6.Qc2");
        mapa.put("D37", "Queen's Gambit Declined");
        mapa.put("D38", "Queen's Gambit Declined, Ragozin Variation");
        mapa.put("D39", "Queen's Gambit Declined, Ragozin, Vienna Variation");
        mapa.put("D40", "Queen's Gambit Declined, Semi-Tarrasch");
        mapa.put("D41", "Queen's Gambit Declined, Semi-Tarrasch");
        mapa.put("D42", "Queen's Gambit Declined, Semi-Tarrasch, 7.Bd3");
        mapa.put("D43", "Queen's Gambit Declined Semi-Slav");
        mapa.put("D44", "Queen's Gambit Declined Semi-Slav");
        mapa.put("D45", "Queen's Gambit Declined Semi-Slav");
        mapa.put("D46", "Queen's Gambit Declined Semi-Slav");
        mapa.put("D47", "Queen's Gambit Declined Semi-Slav");
        mapa.put("D48", "Queen's Gambit Declined Semi-Slav, Meran");
        mapa.put("D49", "Queen's Gambit Declined Semi-Slav, Meran");
        mapa.put("D50", "Queen's Gambit Declined");
        mapa.put("D51", "Queen's Gambit Declined");
        mapa.put("D52", "Queen's Gambit Declined");
        mapa.put("D53", "Queen's Gambit Declined");
        mapa.put("D54", "Queen's Gambit Declined, Anti-Neo-Orthodox Variation");
        mapa.put("D55", "Queen's Gambit Declined");
        mapa.put("D56", "Queen's Gambit Declined");
        mapa.put("D57", "Queen's Gambit Declined, Lasker Defense");
        mapa.put("D58", "Queen's Gambit Declined, Tartakower (Makagonov-Bondarevsky) System");
        mapa.put("D59", "Queen's Gambit Declined, Tartakower");
        mapa.put("D60", "Queen's Gambit Declined, Orthodox Defense");
        mapa.put("D61", "Queen's Gambit Declined, Orthodox, Rubinstein Attack");
        mapa.put("D62", "Queen's Gambit Declined, Orthodox, Rubinstein Attack");
        mapa.put("D63", "Queen's Gambit Declined, Orthodox Defense");
        mapa.put("D64", "Queen's Gambit Declined, Orthodox, Rubinstein Attack");
        mapa.put("D65", "Queen's Gambit Declined, Orthodox, Rubinstein Attack, Main line");
        mapa.put("D66", "Queen's Gambit Declined, Orthodox Defense, Bd3 line");
        mapa.put("D67", "Queen's Gambit Declined, Orthodox Defense, Bd3 line");
        mapa.put("D68", "Queen's Gambit Declined, Orthodox Defense, Classical");
        mapa.put("D69", "Queen's Gambit Declined, Orthodox Defense, Classical, 13.de");
        mapa.put("D70", "Neo-Grunfeld Defense");
        mapa.put("D71", "Neo-Grunfeld");
        mapa.put("D72", "Neo-Grunfeld, 5.cd, Main line");
        mapa.put("D73", "Neo-Grunfeld, 5.Nf3");
        mapa.put("D74", "Neo-Grunfeld, 6.cd Nxd5, 7.O-O");
        mapa.put("D75", "Neo-Grunfeld, 6.cd Nxd5, 7.O-O c5, 8.dxc5");
        mapa.put("D76", "Neo-Grunfeld, 6.cd Nxd5, 7.O-O Nb6");
        mapa.put("D77", "Neo-Grunfeld, 6.O-O");
        mapa.put("D78", "Neo-Grunfeld, 6.O-O c6");
        mapa.put("D79", "Neo-Grunfeld, 6.O-O, Main line");
        mapa.put("D80", "Grunfeld");
        mapa.put("D81", "Grunfeld, Russian Variation");
        mapa.put("D82", "Grunfeld, 4.Bf4");
        mapa.put("D83", "Grunfeld, Grunfeld Gambit");
        mapa.put("D84", "Grunfeld, Grunfeld Gambit Accepted");
        mapa.put("D85", "Grunfeld");
        mapa.put("D86", "Grunfeld, Exchange");
        mapa.put("D87", "Grunfeld, Exchange");
        mapa.put("D88", "Grunfeld, Spassky Variation, Main line, 10...cd, 11.cd");
        mapa.put("D89", "Grunfeld");
        mapa.put("D90", "Grunfeld");
        mapa.put("D91", "Grunfeld, 5.Bg5");
        mapa.put("D92", "Grunfeld, 5.Bf4");
        mapa.put("D93", "Grunfeld, with Bf4 & e3");
        mapa.put("D94", "Grunfeld");
        mapa.put("D95", "Grunfeld");
        mapa.put("D96", "Grunfeld, Russian Variation");
        mapa.put("D97", "Grunfeld, Russian");
        mapa.put("D98", "Grunfeld, Russian");
        mapa.put("D99", "Grunfeld Defense, Smyslov");
        mapa.put("E00", "Queen's Pawn Game");
        mapa.put("E01", "Catalan, Closed");
        mapa.put("E02", "Catalan, Open, 5.Qa4");
        mapa.put("E03", "Catalan, Open");
        mapa.put("E04", "Catalan, Open, 5.Nf3");
        mapa.put("E05", "Catalan, Open, Classical line");
        mapa.put("E06", "Catalan, Closed, 5.Nf3");
        mapa.put("E07", "Catalan, Closed");
        mapa.put("E08", "Catalan, Closed");
        mapa.put("E09", "Catalan, Closed");
        mapa.put("E10", "Queen's Pawn Game");
        mapa.put("E11", "Bogo-Indian Defense");
        mapa.put("E12", "Queen's Indian");
        mapa.put("E13", "Queen's Indian, 4.Nc3, Main line");
        mapa.put("E14", "Queen's Indian");
        mapa.put("E15", "Queen's Indian");
        mapa.put("E16", "Queen's Indian");
        mapa.put("E17", "Queen's Indian");
        mapa.put("E18", "Queen's Indian, Old Main line, 7.Nc3");
        mapa.put("E19", "Queen's Indian, Old Main line, 9.Qxc3");
        mapa.put("E20", "Nimzo-Indian");
        mapa.put("E21", "Nimzo-Indian, Three Knights");
        mapa.put("E22", "Nimzo-Indian, Spielmann Variation");
        mapa.put("E23", "Nimzo-Indian, Spielmann");
        mapa.put("E24", "Nimzo-Indian, Samisch");
        mapa.put("E25", "Nimzo-Indian, Samisch");
        mapa.put("E26", "Nimzo-Indian, Samisch");
        mapa.put("E27", "Nimzo-Indian, Samisch Variation");
        mapa.put("E28", "Nimzo-Indian, Samisch Variation");
        mapa.put("E29", "Nimzo-Indian, Samisch");
        mapa.put("E30", "Nimzo-Indian, Leningrad");
        mapa.put("E31", "Nimzo-Indian, Leningrad, Main line");
        mapa.put("E32", "Nimzo-Indian, Classical");
        mapa.put("E33", "Nimzo-Indian, Classical");
        mapa.put("E34", "Nimzo-Indian, Classical, Noa Variation");
        mapa.put("E35", "Nimzo-Indian, Classical, Noa Variation, 5.cd ed");
        mapa.put("E36", "Nimzo-Indian, Classical");
        mapa.put("E37", "Nimzo-Indian, Classical");
        mapa.put("E38", "Nimzo-Indian, Classical, 4...c5");
        mapa.put("E39", "Nimzo-Indian, Classical, Pirc Variation");
        mapa.put("E40", "Nimzo-Indian, 4.e3");
        mapa.put("E41", "Nimzo-Indian");
        mapa.put("E42", "Nimzo-Indian, 4.e3 c5, 5.Ne2 (Rubinstein)");
        mapa.put("E43", "Nimzo-Indian, Fischer Variation");
        mapa.put("E44", "Nimzo-Indian, Fischer Variation, 5.Ne2");
        mapa.put("E45", "Nimzo-Indian, 4.e3, Bronstein (Byrne) Variation");
        mapa.put("E46", "Nimzo-Indian");
        mapa.put("E47", "Nimzo-Indian, 4.e3 O-O 5.Bd3");
        mapa.put("E48", "Nimzo-Indian, 4.e3 O-O 5.Bd3 d5");
        mapa.put("E49", "Nimzo-Indian, 4.e3, Botvinnik System");
        mapa.put("E50", "Nimzo-Indian, 4.e3 O-O 5.Nf3, without ...d5");
        mapa.put("E51", "Nimzo-Indian, 4.e3");
        mapa.put("E52", "Nimzo-Indian, 4.e3, Main line with ...b6");
        mapa.put("E53", "Nimzo-Indian, 4.e3");
        mapa.put("E54", "Nimzo-Indian, 4.e3, Gligoric System");
        mapa.put("E55", "Nimzo-Indian, 4.e3, Gligoric System, Bronstein Variation");
        mapa.put("E56", "Nimzo-Indian, 4.e3, Main line with 7...Nc6");
        mapa.put("E57", "Nimzo-Indian, 4.e3, Main line with 8...dc and 9...cd");
        mapa.put("E58", "Nimzo-Indian, 4.e3, Main line with 8...Bxc3");
        mapa.put("E59", "Nimzo-Indian, 4.e3, Main line");
        mapa.put("E60", "King's Indian Defense");
        mapa.put("E61", "King's Indian");
        mapa.put("E62", "King's Indian, Fianchetto");
        mapa.put("E63", "King's Indian, Fianchetto, Panno Variation");
        mapa.put("E64", "King's Indian, Fianchetto, Yugoslav System");
        mapa.put("E65", "King's Indian, Fianchetto, Yugoslav, 7.O-O");
        mapa.put("E66", "King's Indian, Fianchetto, Yugoslav Panno");
        mapa.put("E67", "King's Indian, Fianchetto");
        mapa.put("E68", "King's Indian, Fianchetto, Classical Variation, 8.e4");
        mapa.put("E69", "King's Indian, Fianchetto, Classical Main line");
        mapa.put("E70", "King's Indian");
        mapa.put("E71", "King's Indian, Makagonov System (5.h3)");
        mapa.put("E72", "King's Indian");
        mapa.put("E73", "King's Indian");
        mapa.put("E74", "King's Indian, Averbakh, 6...c5");
        mapa.put("E75", "King's Indian, Averbakh, Main line");
        mapa.put("E76", "King's Indian, Four Pawns Attack");
        mapa.put("E77", "King's Indian");
        mapa.put("E78", "King's Indian, Four Pawns Attack, with Be2 and Nf3");
        mapa.put("E79", "King's Indian, Four Pawns Attack, Main line");
        mapa.put("E80", "King's Indian, Samisch Variation");
        mapa.put("E81", "King's Indian, Samisch");
        mapa.put("E82", "King's Indian, Samisch, double Fianchetto Variation");
        mapa.put("E83", "King's Indian, Samisch");
        mapa.put("E84", "King's Indian, Samisch, Panno Main line");
        mapa.put("E85", "King's Indian, Samisch, Orthodox Variation");
        mapa.put("E86", "King's Indian, Samisch, Orthodox, 7.Nge2 c6");
        mapa.put("E87", "King's Indian, Samisch, Orthodox");
        mapa.put("E88", "King's Indian, Samisch, Orthodox, 7.d5 c6");
        mapa.put("E89", "King's Indian, Samisch, Orthodox Main line");
        mapa.put("E90", "King's Indian");
        mapa.put("E91", "King's Indian");
        mapa.put("E92", "King's Indian");
        mapa.put("E93", "King's Indian, Petrosian System");
        mapa.put("E94", "King's Indian, Orthodox");
        mapa.put("E95", "King's Indian, Orthodox, 7...Nbd7, 8.Re1");
        mapa.put("E96", "King's Indian, Orthodox, 7...Nbd7, Main line");
        mapa.put("E97", "King's Indian");
        mapa.put("E98", "King's Indian, Orthodox, Taimanov, 9.Ne1");
        mapa.put("E99", "King's Indian, Orthodox, Taimanov");

        return mapa;

    }

}
