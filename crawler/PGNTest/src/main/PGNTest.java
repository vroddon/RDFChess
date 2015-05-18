package main;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class PGNTest {

private static final int BUFFER_SIZE = 0;
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
	
	public static void DownloadZipFile(String address, int k) throws IOException, InterruptedException
	{
		String link = address;
		//link = "http://www.theweekinchess.com/zips/twic1068g.zip";
		
		String path = "C:" + File.separator + "Users" + File.separator
				+ "Reihan" + File.separator + "Google Drive" + File.separator
				+ "E" + File.separator + "DataBase" + File.separator
				+ "PGN" + File.separator + "test" + File.separator + "Zip" + File.separator + "FIlE" + k + ".zip";
	    try {
	    	URL url = new URL(link);
	        URLConnection conn = url.openConnection();
	        conn.setDoOutput(true);
	        conn.setDoInput(true);
	        conn.setRequestProperty("content-type", "binary/data");
	        InputStream in = conn.getInputStream();
	       
	        
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
	    
	    String zipFilePath = path;
        String destDirectory = "C:/Users/Reihan/Google Drive/E/DataBase/PGN/test/UnZip";
	    	
        try {
		            unzip(zipFilePath, destDirectory, link);
		        } catch (Exception ex) {
		            // some errors occurred
		            ex.printStackTrace();
		        }    
	}
	
	public static void unzip(String zipFilePath, String destDirectory, String Link) throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        System.out.println("got to this point    " + Link);
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        System.out.println("heeeeeeeeeeeeeeeeeeeeeeeeeeeee");
        ZipEntry entry = zipIn.getNextEntry();
        System.out.println("heeeeeeeeeeeeeeeeeeeeeeeeeeeee2");
        // iterates over entries in the zip file
        while (entry != null) {
        	 System.out.println("heeeeeeeeeeeeeeeeeeeeeeeeeeeee3");
           String filePath = destDirectory + File.separator + entry.getName();
        	
            if (!entry.isDirectory()) {
                // if the entry is a file, extracts it
                int x = extractFile(zipIn, filePath, Link);
                
            } else {
                // if the entry is a directory, make the directory
                File dir = new File(filePath);
                dir.mkdir();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
        
        
        }
	
	 public static int extractFile(ZipInputStream zipIn, String filePath, String Link) throws IOException {
	        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
	        byte[] bytesIn = new byte[BUFFER_SIZE];
	        int read = 0;
	        System.out.println("heeeeeeeeeeeeeeeeeeeeeeeeeeeee4");
	        while ((read = zipIn.read(bytesIn)) != -1) {
	        	System.out.println("heeeeeeeeeeeeeeeeeeeeeeeeeeeee5");
	            bos.write(bytesIn, 0, read);
	            System.out.println("heeeeeeeeeeeeeeeeeeeeeeeeeeeee6");
	        }
	        bos.close();
	        return 1;
	 }
	//----------------------------------
	public static void main(String[] args) throws IOException, InterruptedException {
		
		
		ArrayList<String> allZipFiles = getAllFileAddress("C:/Users/Reihan/Google Drive/E/DataBase/PGN/zipFilesAddress.txt");
		for (int i = 0; i < allZipFiles.size(); i++) {
			System.out.println(allZipFiles.get(i));
		}


		for (int i = 0; i < 1 ; i++) {
			//each link in allZipFiles contains many ZipFile url, all of them stored into the tempArr
			ArrayList<String> tempArr = new ArrayList<String>(); 
			tempArr = getAllFileAddress(allZipFiles.get(i));	//all URLs in the first DOc.txt will be added to temoArr
			int k = 0;
			for(String zipFileAdd : tempArr)
			{
				DownloadZipFile(zipFileAdd , k);
				//source is zipFileAdd as a string
				System.out.println(zipFileAdd);
				k++;				
			}			
		}
		
	
		
		//--------------------
		
		
	}

}
