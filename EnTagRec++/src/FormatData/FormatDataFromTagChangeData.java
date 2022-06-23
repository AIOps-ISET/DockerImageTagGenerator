package FormatData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;

import org.apache.commons.csv.CSVRecord;

import org.apache.commons.io.*;
import org.netlib.util.intW;

import scala.actors.threadpool.Arrays;

public class FormatDataFromTagChangeData {
	
	static int tagN = 50;
	static String ContentOutput = "rawdata";
	static public void main(String[] args){
		
		if(args.length <1)
		{
			System.out.println("usage [root]");
		}
		
		String root = args[0];
		String inputData = root+ "/originalData.csv";

		formatFromCSV(root, inputData,tagN);
		System.out.println("done");
		
		
		
		
	}
	
	
	
	static public void formatFromCSV(String root, String inputData, int tagN){
		try {
			
			String output = root + "/" + ContentOutput;
			if(!new File(output).isDirectory())
				new File(output).mkdirs();
			
			HashMap<String, ArrayList<String>> tag_doc = new HashMap();
			
			Reader in = new InputStreamReader(new FileInputStream(inputData));
			CSVParser parser = CSVFormat.EXCEL.withHeader().parse(in);
			Iterator it = parser.iterator();
			
			String metaOutput = root +"/meta.txt";
			BufferedWriter fs = new BufferedWriter(new FileWriter(metaOutput));
			HashMap<String, String> docId_userId = new HashMap<>();
			HashMap<String, String> docId_tags = new HashMap<>();
			//it.next();
			while(it.hasNext()){
				 
				CSVRecord csvRecord = (CSVRecord) it.next();
		        String body = csvRecord.get("body");
		        String title = csvRecord.get("title");
		        String tags = csvRecord.get("etext");
		        String docId = csvRecord.get("id");
		        String userId = csvRecord.get("ownerUserId");
		        if(tags.trim().equals(""))
		        	continue;
		        if(docId.equals("90638"))
		        	System.out.println();
		        //docId_content.put(docId   ,title+" "+ body);
		        docId_userId.put(docId, userId);
		        tags = tags.replace("<", "");
		        addToTagHashMap(tags, String.valueOf(docId), tag_doc, ">");
		        
		        // 
		        // outputText(output, docId, title+" "+body);
		        
		        
			}
			parser.close();
			
			// output to tag_doc file
			ArrayList<String> removedTagArrayList = new ArrayList<>();
			BufferedWriter bw = new BufferedWriter(new FileWriter(root+"/tag_doc_50.txt"));
			for(String tag: tag_doc.keySet()){
				
				StringBuilder sb = new StringBuilder();
				ArrayList<String> docs = tag_doc.get(tag);
				if(docs.size() >= tagN){
					for(int i =0 ; i < docs.size(); i++){
						
						sb.append(docs.get(i)+",");
						//output
						
						
					}
					bw.write(tag+":"+sb.toString()+"\n");
				}else{
					removedTagArrayList.add(tag);
				}
			}
			bw.close();
			
			in = new InputStreamReader(new FileInputStream(inputData));
			parser = CSVFormat.EXCEL.withHeader().parse(in);
			it = parser.iterator();
			
			
			
			 CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator("\n");

			              
             //initialize CSVPrinter object
 
			 CSVPrinter csvFilePrinter = new CSVPrinter(new FileWriter(inputData.replace(".csv", "_tag50.csv")), csvFileFormat);

             //Create CSV file header
			 String[] FILE_HEADER = {"id","otext","etext","title","body","odate","edate","ownerUserId","postid"};
             csvFilePrinter.printRecord(FILE_HEADER);

			
		
			while(it.hasNext()){
				 
				CSVRecord csvRecord = (CSVRecord) it.next();
		        String body = csvRecord.get("body");
		        String title = csvRecord.get("title");
		        String tags = csvRecord.get("etext");
		        String initTag = csvRecord.get("otext");
		        String docId = csvRecord.get("id");
		        String userId = csvRecord.get("ownerUserId");
		        //String postid = csvRecord.get("postid");
		        String odate = csvRecord.get("odate");
		        String edate = csvRecord.get("edate");
		        
		       
		        //docId_content.put(docId   ,title+" "+ body);
		       initTag = removeTags(initTag, removedTagArrayList);
		       tags = removeTags(tags, removedTagArrayList);
		        
		        
		        if(!tags.trim().equals("") && !initTag.trim().equals("")){
			        List<String> recordList = new ArrayList<>();
			        recordList.add(docId);
			        recordList.add(initTag);
			        recordList.add(tags);
			        recordList.add(title);
			        recordList.add(body);
			        recordList.add(odate);
			        recordList.add(edate);
			        recordList.add(userId);
			       // recordList.add(postid);
			        csvFilePrinter.printRecord(recordList);
			        
			        
			        // output to raw folder
			        outputText(output, docId, title+" "+body);
			        fs.write(docId+"\t"+docId+"\t"+userId+"\t"+tags.replace(">", "\t")+"\n");
				       
		        }
		       
		        
			}
			fs.close();
			 csvFilePrinter.close();
			 in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static String removeTags(String initTag, ArrayList<String> removedTagArrayList ){
		String[] tagStrs = initTag.replace(">",	 "").replace("<", " ").trim().split(" +");
	       String newTagString = "";
	        for(String tag : tagStrs){
	        	if(!removedTagArrayList.contains(tag))
	        	{
	        		newTagString += "<"+tag + ">";
	        	}
	        	
	        }
	        newTagString = newTagString.trim();
	        return newTagString;
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
		tags = tags.replace(delimeter, " ");
		tags = tags.trim();
		String[] tagArray = tags.split(" +");
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
	 */
	public static void getGivenAndExclusiveTags(String inputData,
			HashMap<String, List<String>> givenTags,
			HashMap<String, List<String>> exclusiveTagOnEvaluation) {
		// TODO Auto-generated method stub
		try{
			File file = new File(inputData);
			String fileContent = FileUtils.readFileToString(file);
			CSVParser parser = CSVParser.parse(fileContent,CSVFormat.EXCEL);
			Iterator it = parser.iterator();
			
			it.next();
			while(it.hasNext()){
				 
				CSVRecord csvRecord = (CSVRecord) it.next();
		        String id= csvRecord.get(0);
	//	        String package_name = tokens[1];

		        String finalTagstr = csvRecord.get(2);

		        finalTagstr = finalTagstr.replace("<", "");
		        String[] exclusiveTags = finalTagstr.split(">");
		        exclusiveTagOnEvaluation.put(id, Arrays.asList(exclusiveTags));
		        String originalTagStr = csvRecord.get(1);
		        originalTagStr = originalTagStr.replace("<", "");
		        String[] originalTags = originalTagStr.split(">");
		        givenTags.put(id, Arrays.asList(originalTags));
		         
		        
		        
	
		        
			}
			parser.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}
}
