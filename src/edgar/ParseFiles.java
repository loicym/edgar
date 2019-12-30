package edgar;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.*;
import org.apache.commons.io.*;
import org.apache.commons.*;


public class ParseFiles {
	// 10K All Years, a folder with all downloaded files sorted to parse.
	
	private static final String sortedPath = "10K\\1999\\";
	private static final String csvOutputPath = "outputSentiment.csv";
	private static final File negativeDictionary = new File("dictionary\\negativeWords.txt");
	private static final File positiveDictionary = new File("dictionary\\positiveWords.txt");
	//private static final File positiveDictionary = new File("C:\\Users\\aerial\\javaWorkspace\\dictionary\\positiveWords.txt");
	
	private static String myOutputContent;
	
	private static List<String> positiveWords = new ArrayList<String>();
    private static List<String> negativeWords = new ArrayList<String>();
	
	private static String[] nameArray;
	private static String[] dateArray;
	private static String[] cikArray;
	
	//two arrays for the positive and negative score
	private static double[] positiveScoreArray;
	private static double[] negativeScoreArray;
	
	public static void main(String[] args) throws IOException {	
		//load the positive and negative words array lists with the dictionaryReader method
		negativeWords = dictionaryReader(negativeDictionary);
		positiveWords = dictionaryReader(positiveDictionary);
		
		//load all files from the path
		File listOfFiles[] = fileLoader(new File(sortedPath));
		System.out.println(listOfFiles);

		nameArray = new String[listOfFiles.length];
		dateArray = new String[listOfFiles.length];
		cikArray = new String[listOfFiles.length];
		positiveScoreArray = new double[listOfFiles.length];
		negativeScoreArray = new double[listOfFiles.length];
		
		
		for (int i = 0; i < listOfFiles.length; i++){
			System.out.println("index parsed is: "+i+" out of: "+String.valueOf(listOfFiles.length));
			File file = listOfFiles[i];
			System.out.println(file);
			myOutputContent = fileReader(file);
			System.out.println("fileReader is done");
			fileParser(myOutputContent, i);
			System.out.println("fileParser is done");
			getSentiment(myOutputContent, i);
			System.out.println("getSentiment is done");
		}
		infoWriter(nameArray, dateArray, cikArray, negativeScoreArray, positiveScoreArray);
	}

	//read all the files of the folders / subfolders. and return them.
	public static File[] fileLoader(File folderPath){
		File[] listOfFiles = folderPath.listFiles();
		return (listOfFiles);
	}	
	
	//returns the final complete text file in the form of a single string.
	public static String fileReader(File file) throws IOException{
		String myOutputContent = null;
		
		if (file.isFile() && file.getName().endsWith(".txt")){
			String myCharSet = null;
			myOutputContent = FileUtils.readFileToString(file, myCharSet);
		  }
		  return(myOutputContent);
	}
	
	//parse the content of the file string and isolate company names and cik using the regex abilities of Java.
	public static void fileParser(String fileContent, int index) throws IOException{
		Pattern myBeginPattern = Pattern.compile("COMPANY\\s*CONFORMED\\s*NAME\\s*:\\s*");
		Pattern myEndPattern = Pattern.compile("CENTRAL\\s*INDEX\\s*KEY\\s*:\\s*");
		
		Matcher myBeginMatcher = myBeginPattern.matcher(fileContent);
		Matcher myEndMatcher = myEndPattern.matcher(fileContent);	
		
	    int beginChar = (myBeginMatcher.find() ? myBeginMatcher.end() : -1);
		int endChar = (myEndMatcher.find() ? myEndMatcher.start() : -1);
		
		String myCompanyName = fileContent.substring(beginChar,endChar);
		myCompanyName = myCompanyName.trim().replaceAll("\n", "");
		
		System.out.println(myCompanyName);
		
		myBeginPattern = Pattern.compile("CONFORMED\\s*PERIOD\\s*OF\\s*REPORT\\s*:\\s*");
		myEndPattern = Pattern.compile("FILED\\s*AS\\s*OF\\s*DATE\\s*:\\s*");
		
		myBeginMatcher = myBeginPattern.matcher(fileContent);
		myEndMatcher = myEndPattern.matcher(fileContent);	
		
	    beginChar = (myBeginMatcher.find() ? myBeginMatcher.end() : -1);
		endChar = (myEndMatcher.find() ? myEndMatcher.start() : -1);
		
		String myFiscalEndYear = fileContent.substring(beginChar,endChar);
		myFiscalEndYear = myFiscalEndYear.trim().replaceAll("\n", "");
		
		System.out.println(myFiscalEndYear);
		
		myBeginPattern = Pattern.compile("CENTRAL\\s*INDEX\\s*KEY\\s*:\\s*");
		myEndPattern = Pattern.compile("STANDARD\\s*INDUSTRIAL\\s*CLASSIFICATION\\s*:\\s*");
		
		myBeginMatcher = myBeginPattern.matcher(fileContent);
		myEndMatcher = myEndPattern.matcher(fileContent);	
		
	    beginChar = (myBeginMatcher.find() ? myBeginMatcher.end() : -1);
		endChar = (myEndMatcher.find() ? myEndMatcher.start() : -1);
		
		String cik = fileContent.substring(beginChar,endChar);
		cik = cik.trim().replaceAll("\n", "");
		
		System.out.println(cik);
		
		nameArray[index] = myCompanyName;
		dateArray[index] = myFiscalEndYear;
		cikArray[index] = cik;
	}
	
	private static void getSentiment(String textFile, int indexFile) throws IOException{
		for (int i = 0; i < positiveWords.size(); i++)
		{
			if (textFile.toLowerCase().contains(positiveWords.get(i).toLowerCase()))
				positiveScoreArray[indexFile]++;
		}
		
		for (int i = 0; i < negativeWords.size(); i++)
		{
			if (textFile.toLowerCase().contains(negativeWords.get(i).toLowerCase()))
				negativeScoreArray[indexFile]++;
		}	
    }
	
	public static List<String> dictionaryReader(File dictionary) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(dictionary));
	    String line;
	    List<String> wordList = new ArrayList<String>();
	    while ((line = br.readLine()) != null){
	    	wordList.add(line);
	    	System.out.println(line);
		}
		return wordList;
	}
	
	public static void infoWriter(String[] nameArray, String[] dateArray, String[] cikArray, double[] negativeScoreArray, double[] positiveScoreArray) throws IOException{
		FileWriter writer = new FileWriter(csvOutputPath,false);
	        	        
        writer.append("company name");
        writer.append(',');
        writer.append("date");
        writer.append(',');
        writer.append("cik");
        writer.append(',');
        writer.append("negative score");
        writer.append(',');
        writer.append("positive score");
        writer.append(',');
        writer.append("normalized overall score");
        
        writer.append('\n');
	        
        for(int i = 0; i<nameArray.length; i++)
        {
        	writer.append(nameArray[i]);
    	    writer.append(',');
    	    writer.append(dateArray[i]);
 	        writer.append(',');
 	        writer.append(cikArray[i]);
 	        writer.append(',');
 	        writer.append(String.valueOf(negativeScoreArray[i]));
 	        writer.append(',');
 	        writer.append(String.valueOf(positiveScoreArray[i]));
 	        writer.append(',');
 	        writer.append(String.valueOf((positiveScoreArray[i]-negativeScoreArray[i])/(positiveScoreArray[i]+negativeScoreArray[i])));
   	                   
   	        writer.append('\n');
        }
	        
        writer.flush();
        writer.close();
	}
}
