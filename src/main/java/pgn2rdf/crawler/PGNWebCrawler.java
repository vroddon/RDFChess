package pgn2rdf.crawler;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class PGNWebCrawler {

public static ArrayList<String> allAddressFiles;

	
	public static ArrayList<String> getAllFileAddress(String address) throws IOException
	{
		
		allAddressFiles = new ArrayList<String>();
		BufferedReader input = new BufferedReader(new FileReader(address)); // input

		String thisLine = null;
		
		while ((thisLine = input.readLine()) != null) {
		
			allAddressFiles.add(thisLine.toString());

		}
		return allAddressFiles;
	}
	public static void WriteIntoTextFile(String element, int k)
			throws IOException {
		
		//each DocFIles is related to a text file which contains lots of URL of zip files >> so the URLs are in the Doc files
		String path = "C:" + File.separator + "Users" + File.separator
				+ "Reihan" + File.separator + "Google Drive" + File.separator
				+ "E" + File.separator + "DataBase" + File.separator
				+ "PGN" + File.separator + "Doc "
				+ k + ".txt";


		File fileclass = new File(path); // if file doesn't exists, then
		// create it
		if (!fileclass.exists()) {
			fileclass.createNewFile();
		}
		FileWriter fc = new FileWriter(fileclass.getAbsoluteFile());
		BufferedWriter bc = new BufferedWriter(fc);
		bc.write(element + "\n");
		bc.close();

	}
	//go to the fisrt link and get the URL of all PGN zipFiles
	public static void FirstLink(String address, int k) throws IOException, InterruptedException
	{
		String link = address;
		StringBuilder paragraph = new StringBuilder();
		System.out.println("URL:      " + link);
					
		// Parse the HTML for this website
		Document doc = Jsoup.connect(link).timeout(0).get();

		Elements newsHeadlines = doc.select("tr td a");
		
		
		for (Element e : newsHeadlines) {
		
			Elements headline = e.select("a[href]");
			
			//exactly get the content of href attribute
			
			if (headline.size() > 0) {
				if(headline.iterator().next().text().toString().toLowerCase().contains("pgn")){					
					System.out.println(  headline.iterator().next().text().toString().toLowerCase());
				//paragraph.append(headline.iterator().next().text().toLowerCase() + "\n");
				paragraph.append(e.attr("href"));
				paragraph.append("\t");
				paragraph.append(System.getProperty("line.separator"));
				}
			}	
			else
			{
				System.exit(1);
				}
		}
		
		String stparagraph = paragraph.toString();
		WriteIntoTextFile(stparagraph, k);
		k++;

		
		// pause for awhile (10 seconds in this case) so we don't overwhelm
		// the server
		Thread.sleep(10000);
		}
	
	public static void SecondLink(String address, int k) throws IOException, InterruptedException
	{
		// I check the contents of each <a href> and if they contain pgn I selected them/ if all are not necessary; I also can add if they contain "zip" or not
		String link = address;
		StringBuilder paragraph = new StringBuilder();
		System.out.println("URL:      " + link);
					
		// Parse the HTML for this website
		Document doc = Jsoup.connect(link).timeout(0).get();

		Elements newsHeadlines = doc.select("tr td a");
	
		
		
		for (Element e : newsHeadlines) {
			
			//System.out.println("here   " + e.toString());
			Elements headline = e.select("a[href]");
			
			//exactly get the content of href attribute
			//System.out.println(e.attr("href") + "  ggggg");

			
			if (headline.size() > 0) {
				if(headline.iterator().next().text().toString().contains("pgn") && e.attr("href").toString().toLowerCase().contains(".zip")){					
				
				paragraph.append("http://www.pgnmentor.com/");
				paragraph.append(e.attr("href"));
				paragraph.append("\t");
				paragraph.append(System.getProperty("line.separator"));
				}
			}	
			else
			{
				System.exit(1);
				}
		}
		
		String stparagraph = paragraph.toString();
		//System.out.println(stparagraph);
		WriteIntoTextFile(stparagraph, k);
		k++;

		
		// pause for awhile (10 seconds in this case) so we don't overwhelm
		// the server
		Thread.sleep(10000);
		}

	public static void ThirdLink(String address, int k) throws IOException, InterruptedException
	{
		// I check the contents of each <a href> and if they contain pgn I selected them/ if all are not necessary; I also can add if they contain "zip" or not
		String link = address;
		StringBuilder paragraph = new StringBuilder();
		System.out.println("URL:      " + link);
					
		// Parse the HTML for this website
		Document doc = Jsoup.connect(link).timeout(0).get();

		// newHeadlines contain all info into a <a..> tag
		Elements newsHeadlines = doc.select("a");
		
		for (Element e : newsHeadlines) {
			
			Elements headline = e.select("a[href]");
		
			
			
			
			if (headline.size() > 0) {
				if(headline.attr("href").toString().toLowerCase().contains(".zip")){	
					//headline.iterator().next().text().>>> get the pharase between 2 a[href] tag
					//System.out.println("kkkkkkkkkkkk   " + headline.iterator().next().text().toString().toLowerCase());
					//System.out.println("link :   " + e.attr("href"));
		
					//Although there are many info in a <a> tag; but, e.attr("href") only give us the specific part of href		
					paragraph.append("http://www.angelfire.com/games3/smartbridge/");
					paragraph.append(headline.attr("href"));			
					paragraph.append("\t");				
					paragraph.append(System.getProperty("line.separator"));
				}
			}	
			else
			{
				System.exit(1);
				}
		}
		
		String stparagraph = paragraph.toString();
		WriteIntoTextFile(stparagraph, k);
		k++;

		
		// pause for awhile (10 seconds in this case) so we don't overwhelm
		// the server
		Thread.sleep(10000);
		}
	
	public static void ForthLink(String address, int k) throws IOException, InterruptedException
	{
		// I check the contents of each <a href> and if they contain pgn I selected them/ if all are not necessary; I also can add if they contain "zip" or not
		String link = address;
		StringBuilder paragraph = new StringBuilder();
		System.out.println("URL:      " + link);
					
		// Parse the HTML for this website
		Document doc = Jsoup.connect(link).timeout(0).get();

		// newHeadlines contain all info into a <a..> tag
		Elements newsHeadlines = doc.select("a");
		
		for (Element e : newsHeadlines) {
			
			Elements headline = e.select("a[href]");
		
			Elements rel = e.select("a[rel]");
			//System.out.print(" PPPP  " + headline.toString());

			//System.out.println(" ggggg  " + rel.attr("rel"));
			
			if (headline.size() > 0) {	
				if(rel.attr("rel").toString().toLowerCase().contains("nofollow")){	
		
					//Although there are many info in a <a> tag; but, e.attr("href") only give us the specific part of href		
					paragraph.append(headline.attr("href"));			
					paragraph.append("\t");				
					paragraph.append(System.getProperty("line.separator"));
				}
			}	
			else
			{
				System.exit(1);
				}
		}
		
		String stparagraph = paragraph.toString();
		WriteIntoTextFile(stparagraph, k);
		k++;

		
		// pause for awhile (10 seconds in this case) so we don't overwhelm
		// the server
		Thread.sleep(10000);
		}

	public static void FifthLink(String address, int k) throws IOException, InterruptedException
	{
		String link = address;
		StringBuilder paragraph = new StringBuilder();
		System.out.println("URL:      " + link);
					
		// Parse the HTML for this website
		Document doc = Jsoup.connect(link).timeout(0).get();

		Elements newsHeadlines = doc.select("tr td a");
	
		
		
		for (Element e : newsHeadlines) {
			
			Elements headline = e.select("a[href]");
			
			//exactly get the content of href attribute
			
			if (headline.size() > 0) {
				if(e.attr("href").toString().toLowerCase().contains(".zip")){					
				//System.out.println( e.attr("href").toString().toLowerCase());
				//paragraph.append(headline.iterator().next().text().toLowerCase() + "\n");
				paragraph.append("http://chessproblem.my-free-games.com/");
				paragraph.append(e.attr("href"));
				paragraph.append("\t");
				paragraph.append(System.getProperty("line.separator"));
				}
			}	
			else
			{
				System.exit(1);
				}
		}
		
		String stparagraph = paragraph.toString();
		WriteIntoTextFile(stparagraph, k);
		k++;

		// pause for awhile (10 seconds in this case) so we don't overwhelm
		// the server
		Thread.sleep(10000);
		}
	
	//this method download the zipfiles which their url address are stored as a string called address
	public static void DownloadZipFile(String address, int k) throws IOException, InterruptedException
	{
		String link = address;
		//link = "http://www.theweekinchess.com/zips/twic1068g.zip";

	    try {
	    	URL url = new URL(link);
	        URLConnection conn = url.openConnection();
	        conn.setDoOutput(true);
	        conn.setDoInput(true);
	        conn.setRequestProperty("content-type", "binary/data");
	        InputStream in = conn.getInputStream();
	       
	        String path = "C:" + File.separator + "Users" + File.separator
					+ "Reihan" + File.separator + "Google Drive" + File.separator
					+ "E" + File.separator + "DataBase" + File.separator
					+ "PGN" + File.separator + "ZipFiles" + File.separator + "File "
					+ k + ".zip";
	       // FileOutputStream out = new FileOutputStream("./tmp.zip");
	      
	        FileOutputStream out = new FileOutputStream(path);

	        byte[] b = new byte[1024];
	        int count;

	        while ((count = in.read(b)) > 0) {
	            out.write(b, 0, count);
	        }
	        out.close();
	        in.close();

	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
   
	//-----------------------------------------------------------------------	
	public static void main(String[] args) throws IOException, InterruptedException {
		ArrayList<String> addresses = getAllFileAddress("C:/Users/Reihan/Google Drive/E/DataBase/PGN/1.txt");
		//FirstLink(allAddressFiles.get(0), 0);
		//SecondLink(allAddressFiles.get(1), 1);
		//ThirdLink(allAddressFiles.get(2), 2);
		//ForthLink(allAddressFiles.get(3),3);
		//FifthLink(allAddressFiles.get(4),4);
		
		//there are many different txt file (Doc1.txt) each of which has many url address , allZipFiles contains the address of each of that taxt files
		ArrayList<String> allZipFiles = getAllFileAddress("C:/Users/Reihan/Google Drive/E/DataBase/PGN/zipFilesAddress.txt");
		int K = 0;
		
		
		for (int i = 0; i < allZipFiles.size(); i++) {
			//each link in allZipFiles contains many ZipFile url, all of them stored into the tempArr
			ArrayList<String> tempArr = new ArrayList<String>(); 
			tempArr = getAllFileAddress(allZipFiles.get(i));	//all URLs in the first DOc.txt will be added to temoArr
			
			for(String zipFileAdd : tempArr)
			{
				DownloadZipFile(zipFileAdd , K);
				K++;
			}
			
		}
    	
    	

    	
		
		
		

	}

}
