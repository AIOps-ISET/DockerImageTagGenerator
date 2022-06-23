package org.preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import scala.actors.threadpool.Arrays;

public class collectInformationFromPosthistory {

	/**
	 * @param args
	 */
	private HashMap<Integer, Integer> postId_docId_map;
	// store the original tags
	private HashMap<String, ArrayList<String>> docId_tags;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String projectDir = "F:\\shaowei\\research\\tag_recommendation\\folksonomy\\AlltheFourDataset\\appleSource\\";
		String postFile = projectDir + "PostHistory.xml";
		String metaFile = projectDir + "meta.txt";
		String datasetFile = projectDir + "dataset.csv";
		String outputFile = projectDir +"dataset_origTag.csv";
		collectInformationFromPosthistory factory = new collectInformationFromPosthistory();
		try{
			factory.getRowIdToFileMap(metaFile);
			factory.collectOriginalTags(postFile);
			factory.outputOrignalTags(datasetFile, outputFile);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		
	}
	
	public collectInformationFromPosthistory(){
		this.postId_docId_map = new HashMap();
		this.docId_tags = new HashMap();	
		
	}
	
	
	public void getRowIdToFileMap(String metaFile) throws IOException{
		System.out.println("load map from: " + metaFile);
		BufferedReader br = new BufferedReader(new FileReader(metaFile));
		this.postId_docId_map = new HashMap<Integer, Integer>();
		while(br.ready()){
			String line  = br.readLine();
			String[] tmp = line.split("\t");
			Integer docId = Integer.parseInt(tmp[0]);
			Integer questionId = Integer.parseInt(tmp[1]);
			postId_docId_map.put( questionId, docId);
			
		}
		br.close();
		
		
	}
	
	public void outputOrignalTags(String datasetFile, String outputFile) throws IOException{
		System.out.println("collect orig tags.........");
		BufferedReader br = new BufferedReader(new FileReader(datasetFile));
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
		String tmp =null;
		int differentTagN = 1;
		while(br.ready()){
			tmp = br.readLine();
			String[] ele = tmp.split(",");
			String docId = String.valueOf(ele[0]);
			if(docId.equals("2"))
				System.out.println();
			ArrayList<String> finalTags = new ArrayList<String>();
			finalTags.addAll(Arrays.asList(ele[1].split(" +")));
			if(ele[0].equals("9971"))
				System.out.println();
			if(this.docId_tags.containsKey(docId)){
				if(isSameTags(finalTags,this.docId_tags.get(docId))){
					bw.write(tmp+"\n");
				}else{
					// fill the tags with 
					String str = "";
					for(String tag : this.docId_tags.get(docId))
						str += tag+" ";
					bw.write(ele[0]+","+str.trim()+","+ele[2]+"\n");
							differentTagN++;
					System.out.println("different id:" + ele[0]);
				}
			}
			
			else{
				bw.write(tmp+"\n");
				System.out.println("bug: docid "+ docId);
				
			}
		}
		
		br.close();
		bw.close();
		
		System.out.println(differentTagN);
	}
	
	
	static private boolean isSameTags(ArrayList<String> tags1,
			ArrayList<String> tags2) {
		// TODO Auto-generated method stub
		
		if(tags1 ==null || tags2 == null)
			return false;
		if(tags1.size()==tags2.size()){
			for(int i =0 ; i < tags1.size(); i++){
				if(!tags2.contains(tags1.get(i)))
					return false;
			}
		}else{
			return false;
		}
		
		return true;
	}

	public  void collectOriginalTags(String postFile) throws IOException{
		System.out.println("Start parsing xml file............");
		BufferedReader br = new BufferedReader(new FileReader(postFile));
		int readlineN = 100000;
		String temp = null;
		int j = 0;
		int fileNum = 1;
		
		
		while (((temp = br.readLine()) != null) ) 
		
		{
			j++;
			if (j == 1 || j == 2) {
				continue;
			}
			System.out.println(j);
			if(fileNum>readlineN)
				break;
			

			StringBuffer sb = new StringBuffer();

			sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
			sb.append("<posts>");
			sb.append(temp);
			sb.append("</posts>");
			Document doc = null;
			try {
				doc = DocumentHelper.parseText(sb.toString());
				Element rootElt = doc.getRootElement();

				Element ele = rootElt.element("row");
				int postId = Integer.parseInt(ele.attribute("PostId").getText());
				
				if(ele.attribute("PostHistoryTypeId").getText().equals("6")){
					String  tags = ele.attribute("Text").getText().replace("<", "").replace(">", " ") ;
					ArrayList<String> tagList = new ArrayList<String>();
					tagList.addAll(Arrays.asList(tags.split(" +")));
					String docId = String.valueOf( this.postId_docId_map.get(postId));
					if(!this.docId_tags.containsKey(docId)){
						this.docId_tags.put(docId, tagList);
						fileNum++;
					}
				}
				
				
					
			}catch(Exception e){
				e.printStackTrace();
				System.err.println(j);
			}
		}
		
		br.close();

	}

}
