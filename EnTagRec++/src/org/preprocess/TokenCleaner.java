package org.preprocess;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;

public class TokenCleaner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//tokenCleanForDir("O:\\shaowei\\folksonomy\\freecode\\rawdata", "O:\\shaowei\\folksonomy\\freecode\\descriptionCleaned");
		tokenCleanForDir("O:\\shaowei\\folksonomy\\AlltheFourDataset\\askubuntuSource\\rawdata", "O:\\shaowei\\folksonomy\\AlltheFourDataset\\askubuntuSource\\descriptionCleaned");
	}
	
	public static void tokenCleanForDir(String inDir, String outDir){
		File[] fileList = new File(inDir).listFiles();
		if(!new File(outDir).exists())
			new File(outDir).mkdirs();
		
			
		int count = 0;
		for(File f : fileList){
			
			tokenCleanerForFile(f.getAbsolutePath(), outDir+"\\"+f.getName());
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
			String result =  dataFilter.filter_NL(s);
			BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
			bw.write(result);
			bw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

}
