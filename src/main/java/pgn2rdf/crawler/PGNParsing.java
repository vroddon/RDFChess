package pgn2rdf.crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;

public class PGNParsing {

	public static int j = 2935061;

	public static void readPGNFile(File fin) throws Exception {

		//read first line and append it to string
		BufferedReader br = new BufferedReader(new FileReader(fin));
		String line;
		ArrayList<String> lines = new ArrayList<String>();
		StringBuilder st = new StringBuilder();
		PrintWriter out;
		st.append(br.readLine());
		st.append("\r\n");

		while ((line = br.readLine()) != null) {

			if (line.contains("[Event ")) {
				// write to file
				System.out
						.println("upper j >> even dide shod"
								+ j
								+ "##########################################################");
				// StringBuilder which was filled before meeting this line must
				// be printed to a file and a new stringBuilder must be
				// opened for this line and all lines after
				out = new PrintWriter(new File(
						"E:/PGNTextFiles/Z/file"
								+ j++ + ".txt"));
				out.println(st);

				out.close();

				st = new StringBuilder();
				st.append(line);
				st.append("\r\n");
			} else {
				//System.out.println(" lower j " + j);
				st.append(line);
				st.append("\r\n");
			}
		}

		out = new PrintWriter(new File(
				"E:/PGNTextFiles/Z/file"
						+ j++ + ".txt"));
		System.out.println(" last j " + j);
		out.println(st);
		out.close();

	}

	public static void main(String[] args) throws Exception {

		File folder = new File(
				"C:/Users/Reihan/Google Drive/E/DataBase/PGN/UnZipFiles/Z");
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			System.out.println(listOfFiles[i].toString());
		}

		
		for (File file : listOfFiles) {
			
			readPGNFile(file);

		}

	}

}
