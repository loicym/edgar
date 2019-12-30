package edgar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.FileReader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class GetFiles {
	private static int beginYear = 1999;
	private static int endYear = 2016;
	
	private static int beginQtr = 1;
	private static int endQtr = 4;
	
	protected static String myBaseDestination = "10K\\";
	
	public static void main(String[] args) throws IOException{
		runUrls(beginYear, endYear, beginQtr, endQtr);
	}
	
	public static void runUrls(int beginYear, int endYear, int beginQtr, int endQtr) throws IOException{
		String myBeginString = "idxFiles\\";
		String myEndingString = ".company.idx";
		String myStringFile = "";
		
		File myDestinationFile;
		
		for(int i = beginYear; i <= endYear; i++)
		{	
			for(int j = beginQtr; j <= endQtr;j ++)
			{
				myStringFile = myBeginString+String.valueOf(i)+"QTR"+String.valueOf(j)+myEndingString;
				//System.out.println(myStringFile);
				String content = new String(Files.readAllBytes(Paths.get(myStringFile)));
				//parse and download the matching files
				parseContent(content, i);
			}
		}
	}

	public static void parseContent(String content, int year)
	{
		// first get read of the first lines.);
		Scanner contentScanner = new Scanner(content);
		int lineCount = 0;
		while(contentScanner.hasNextLine()) {
			String next = contentScanner.nextLine();
			//ignore the first 9 lines
			if(lineCount > 9){
				System.out.println(next);
				//get the cik
	            String cik = next.substring(74,86).replaceAll("\\s","");   
	            //get the form type
	            String formType = next.substring(62,74).replaceAll("\\s","");;
	            //System.out.println(formType);
	            
	            if(formType.equals("10-K")){
	            	String urlToDownload = next.substring(98,150).replaceAll("\\s","");
	 	            System.out.println(urlToDownload);
	 	            downloadFiles("https://www.sec.gov/Archives/"+urlToDownload, new File(myBaseDestination+String.valueOf(year)+"/"+urlToDownload.replaceAll("/", ".")));
	 	            //alternative, use the function already defined in the G_getIdx class:
	 	            //G_getIdx.downloadIdx("https://www.sec.gov/Archives/"+urlToDownload, new File(myBaseDestination+String.valueOf(year)+"/"+urlToDownload.replaceAll("/", ".")));
	            }
			}
		    lineCount++;
		}
	}
	
	public static void downloadFiles(String urlToDownload, File destinationFile){
		URL url = null;
		try {
			url = new URL(urlToDownload);
		} 
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			FileUtils.copyURLToFile(url, destinationFile);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
