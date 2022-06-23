package org.preprocess;

import java.io.FileReader;
import java.util.Iterator;
import java.io.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class readUserFromFreecodeJson {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JSONParser parser = new JSONParser();
		String root = "G:\\research\\tag_recommendation\\folksonomy\\freecode\\downloads\\";
		String output = "G:\\research\\tag_recommendation\\folksonomy\\freecode\\user_info.txt";
		File[] filesList = new File(root).listFiles(); 
		try {
		BufferedWriter bw = new BufferedWriter(new FileWriter(output));
		int count =1;
		for(File f : filesList){
	      
	        	try{
	            Object obj = parser.parse(new FileReader(
	                    f.getAbsolutePath()));
	 
	            JSONObject jsonObject = (JSONObject) obj;
	            JSONArray pros = (JSONArray)jsonObject.get("projects");
	            for(int i = 0 ; i < pros.size(); i++){
	            	JSONObject pro = (JSONObject)pros.get(i);
	            	pro = (JSONObject) pro.get("project");
	            	String name = (String)pro.get("name");
	            	JSONObject userIdObj = ((JSONObject)pro.get("user"));
	            	Object userId =  userIdObj.get("id");
		            
		            bw.write(f.getName()+"@"+name+".txt"+"\t"+userId+"\n");
	            }
	            System.out.println(count++ + f.getName());
	        	}catch(Exception e){
	        		e.printStackTrace();
	        	}
	        }
		bw.close();
		} catch (Exception e) {
	            e.printStackTrace();
	        }
	}

}
