package FormatData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;



import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import org.apache.commons.csv.CSVRecord;

import org.apache.commons.io.*;
import org.netlib.util.booleanW;
import org.netlib.util.intW;
import org.preprocess.htmlFilter;
import org.preprocess.rawTextDataPreprocessor;

import evaluate_EnTagRec_UA.RunKtimesForEffectSizeTest_onTagChangeData_old;

import LabeledLDA.clearData;
import LabeledLDA.generateDataset;
import TermTagIndex.POS;
import TermTagIndex.TermTagIndexBuilder;

import scala.actors.threadpool.Arrays;
import tagRecommend.Doc;

public class GenerateDataForTagChange {
	
	
	static String rawdata = "rawdata/";
	static String cleandata= "descriptionCleaned/";
	static String htmlFreeData = "htmlFilterred/";
	static String posData = "posdata/";
	static String finalCSVDataset = "dataset.csv";
	
	static boolean rawDataGeneration = true;
	static boolean cleanDataGeneration = true;
	static boolean htmlFreeDataGeneration = true;
	static boolean pos = true;
	static boolean termIndexGenration = true;
	static boolean finalCSVDataGeneration = true;
	
	
	
	static public void main(String[] args){
//		String inputData = "/scratch4/shaowei/data/data_and_results/ChangeTagData/askDifferent/originalData.csv";
//
//		String root = "/scratch4/shaowei/data/data_and_results/ChangeTagData/askDifferent/";
		if(args.length <1)
		{
			System.out.println("usage [root]");
			return;
		}
		
		String root = args[0];
		String inputData = root+ "/originalData.csv";

		// generate raw data
		
		if(rawDataGeneration){
			FormatDataFromTagChangeData.formatFromCSV(root, inputData,50);
			//clearData.clearTags(root+"tag_doc.txt", root+"tag_doc"+"_"+50+".txt", 50);
			System.out.println("raw data generation done");
			
		}
		
		// prepare tags assoication rule input data
		getTagsForAR(inputData,root+RunKtimesForEffectSizeTest_onTagChangeData_old.ARinputFile);
		System.out.println("generate the tags file for assoication rule");
		
		// preprocess the text 
		if(cleanDataGeneration)
		{
			rawTextDataPreprocessor.tokenCleanForDir(root+rawdata, root+cleandata);
			System.out.println("clean data generation done");
		}
		
		// remove html
		if(htmlFreeDataGeneration){
			htmlFilter.tokenCleanForDir(root+rawdata, root+htmlFreeData);
			System.out.println("html tag free data generation done");
		}
		
		
		
		//pos tagger
		
		if(pos){
			POS.posTagForFolder(root+htmlFreeData , root + posData);
			System.out.println("pos tagging done");
			}
		
		if(termIndexGenration){
			TermTagIndexBuilder.runOnglobal(root);
			System.out.println("term index generated!");
		}
		
		// generate csv for experiment
		
		if(finalCSVDataGeneration){
			generateDataset.generateCSVDataset(root,finalCSVDataset);
		}
		
		
	}
	
	
	
	static public void formatFromCSV(String root, String inputData){
		try {
			
			String output = root + "/" + rawdata;
			if(!new File(output).isDirectory())
				new File(output).mkdirs();
			
			HashMap<String, ArrayList<String>> tag_doc = new HashMap();
			
			File file = new File(inputData);
			String fileContent = FileUtils.readFileToString(file);
			CSVParser parser = CSVParser.parse(fileContent,CSVFormat.EXCEL);
			Iterator it = parser.iterator();
			
			it.next();
			while(it.hasNext()){
				 
				CSVRecord csvRecord = (CSVRecord) it.next();
		        String id= csvRecord.get(0);
//		        String package_name = tokens[1];
		        String title = csvRecord.get(3);
		        String content = csvRecord.get(4);
		        String finalTag = csvRecord.get(2);
		        String originalTag = csvRecord.get(1);
		        finalTag = finalTag.replace("<", "");
		        addToTagHashMap(finalTag,id,tag_doc,">");
//		        
		        outputText(output,id,title+" " + content);
		        
			}
			parser.close();
			// output to tag_doc file
			BufferedWriter bw = new BufferedWriter(new FileWriter(root+"/tag_doc.txt"));
			for(String tag: tag_doc.keySet()){
				StringBuilder sb = new StringBuilder();
				ArrayList<String> docs = tag_doc.get(tag);
				for(int i =0 ; i < docs.size(); i++){
					sb.append(docs.get(i)+",");
				}
				bw.write(tag+":"+sb.toString()+"\n");
			}
			bw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void outputText(String root, String fileName, String content) {
		// TODO Auto-generated method stub
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(root+"/"+fileName));
			bw.write(content);
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	static public void addToTagHashMap(String tags, String docId, HashMap<String, ArrayList<String>> map, String delimeter){
		String[] tagArray = tags.split(delimeter);
		for(String tag : tagArray){
			if(map.containsKey(tag)){
				if(!map.get(tag).contains(docId))
					map.get(tag).add(docId);
			}
			else{
				ArrayList<String> list = new ArrayList();
				list.add(docId);
				map.put(tag, list);
			}
		}
	}


	/***
	 * load the giventags and exclusive tags to two hashmaps
	 * @param inputData
	 * @param givenTags
	 * @param exclusiveTagOnEvaluation
	 * @param goldenSet 
	 */
	public static void getTagsForAR(String inputData, String outputData){
		try{
			File file = new File(inputData);
			String fileContent = FileUtils.readFileToString(file);
			CSVParser parser = CSVParser.parse(fileContent,CSVFormat.EXCEL);
			Iterator it = parser.iterator();
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputData));
			
			it.next();
			while(it.hasNext()){
				 
				CSVRecord csvRecord = (CSVRecord) it.next();
		        String id= csvRecord.get(0);
	//	        String package_name = tokens[1];

		        String finalTagstr = csvRecord.get(2);
		        
		        finalTagstr = finalTagstr.replace("<", "").replace(">", " ").trim();
				
		        bw.write(id+","+finalTagstr+"\n");
		         
		        
		        
	
		        
			}
			parser.close();
			bw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void getGivenAndExclusiveTags(String inputData,
			HashMap<String, List<String>> givenTags,
			HashMap<String, List<String>> exclusiveTagOnEvaluation, HashMap<String, Doc> goldenSet) {
		// TODO Auto-generated method stub
		try{
			File file = new File(inputData);
			String fileContent = FileUtils.readFileToString(file);
			CSVParser parser = CSVParser.parse(fileContent,CSVFormat.EXCEL);
			Iterator it = parser.iterator();
			
			it.next();
			int count =0;
			while(it.hasNext()){
				 
				CSVRecord csvRecord = (CSVRecord) it.next();
		        String id= csvRecord.get(0);
	//	        String package_name = tokens[1];

		        String finalTagstr = csvRecord.get(2);
		        
		        String originalTagStr = csvRecord.get(1);
				 originalTagStr = originalTagStr.replace("<", "");
				 String[] originalTags = originalTagStr.split(">");
				 ArrayList<String> givenList = new ArrayList<>();
				 ArrayList<String> exclusiveList = new ArrayList<>();
				
				 for (int i = 0; i < originalTags.length; i++) {
					givenList.add(originalTags[i]);
					
					if(goldenSet.containsKey(id)&& goldenSet.get(id).getTaglist().contains(originalTags[i]) )
						exclusiveList.add(originalTags[i]);
				}
				givenTags.put(id, givenList);
				
				
				
				
				exclusiveTagOnEvaluation.put(id, exclusiveList);
		        
		         
		        
		        count++;
	
		        
			}
			System.out.println(count);
			parser.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}
}
