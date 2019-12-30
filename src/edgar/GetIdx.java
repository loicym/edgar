package edgar;

import org.apache.commons.io.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class GetIdx 
{
	//define the first year of the index files
	private static int beginYear = 1999;
	//define the last year of the index files
	private static int endYear = 2018;
	
	//same for quarters
	private static int beginQtr = 1;
	private static int endQtr = 4;
	
	//program entry point
	public static void main(String[] args){
		runUrls(beginYear, endYear, beginQtr, endQtr);
	}
	
	//core method accessing every quarter year index file
	public static void runUrls(int beginYear, int endYear, int beginQtr, int endQtr){
		String myBeginString = "https://www.sec.gov/Archives/edgar/full-index/";
		String myEndingString = "/company.idx";
		String myStringUrl = "";
		String myBaseDestination = "idxFiles\\";
		
		File myDestinationFile;
		
		for(int i = beginYear; i <= endYear; i++){
			for(int j = beginQtr; j <= endQtr;j ++){
				myStringUrl = myBeginString+String.valueOf(i)+"/QTR"+String.valueOf(j)+myEndingString;
				System.out.println(myStringUrl);
				
				myDestinationFile = new File(myBaseDestination+String.valueOf(i)+"QTR"+String.valueOf(j)+".company.idx");

				System.out.println("downloading: "+String.valueOf(i)+"QTR"+String.valueOf(j)+".company.idx");
				downloadIdx(myStringUrl, myDestinationFile);	
			}
		}
	}
	
	//method called to download the files
	public static void downloadIdx(String baseUrl, File destinationFile){
		URL urlUrl = null;
		try {
			urlUrl = new URL(baseUrl);
		} 
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			FileUtils.copyURLToFile(urlUrl, destinationFile);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
