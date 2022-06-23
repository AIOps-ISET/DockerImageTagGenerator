package org.preprocess;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;

import sun.usagetracker.UsageTrackerClient;

public class rawTextDataPreprocessor {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//tokenCleanForDir("O:/shaowei/folksonomy/freecode/rawdata/", "O:/shaowei/folksonomy/freecode/htmlFilterred");
		//tokenCleanForDir("G:/research/tag_recommendation/data_and_results/ChangeTagData/rawdata", "G:/research/tag_recommendation/data_and_results/ChangeTagData/descriptionCleaned");
		if(args.length <1){
			System.out.println("usage [root of a dataset]" );
			return ;
		}
		
		String root = args[0];
		tokenCleanForDir(root + "rawdata",  root +"descriptionCleaned");
		
	}
	
	public static void tokenCleanForDir(String inDir, String outDir){
		File[] fileList = new File(inDir).listFiles();
		if(!new File(outDir).exists())
			new File(outDir).mkdirs();
		
			
		int count = 0;
		for(File f : fileList){
			
			tokenCleanerForFile(f.getAbsolutePath(), outDir+"/"+f.getName());
			System.out.println(count++);
		}
	}
	
	
	public static void tokenCleanerForFile(String inFile, String outFile){
		FileInputStream in;
		try {
			in = new FileInputStream(inFile);
			byte[] readBytes = new byte[in.available()];
			in.read(readBytes);
			in.close();
			String s = new String(readBytes); 
			s = s.replace("\n", " ").replace("\r", " ").replace(" +", " ");
			String result =  dataFilter.filter_Code(s);
			BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
			bw.write(result);
			bw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

}
