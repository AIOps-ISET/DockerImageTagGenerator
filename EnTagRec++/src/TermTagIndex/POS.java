package TermTagIndex;




import java.io.File;
import java.io.IOException;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.*;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class POS {
	
	public static void main(String[] args) {
		//String root = "O:\\shaowei\\folksonomy\\freecode\\";
		//String root = "O:\\shaowei\\folksonomy\\AlltheFourDataset\\appleSource\\";
		
		if(args.length < 1){
			System.out.println("usage [root path]");
			return ;
		}
		String root = args[0];
		
		String rawData = root +"htmlFilterred/";
		String posData = root +"posdata/";
		
		if(!new File(posData).isDirectory())
			new File(posData).mkdirs();
		
		MaxentTagger tagger = new MaxentTagger(
                "tools/stanford-postagger-2013-06-20/models/english-left3words-distsim.tagger");
		
		File[] files = new File(rawData).listFiles();
		
		for(File f : files){
			try {
				String content  = FileUtils.readFileToString(f);
				
				String tagged = posTag(content,tagger);
				
				String output = posData+f.getName();
				
				FileUtils.writeStringToFile(new File(output), tagged, false);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("done");
		
		
	}
	
	public static void posTagCMD(String posData, String rawData){
		String POSroot = new File("tools\\stanford-postagger-2013-06-20\\").getAbsolutePath()+"\\";
		
		if(!new File(posData).isDirectory())
			new File(posData).mkdirs();
		
		String prefixcmd = "java -mx300m -classpath "+POSroot+
				"stanford-postagger.jar edu.stanford.nlp.tagger.maxent.MaxentTagger -model "+
				POSroot+"models\\english-left3words-distsim.tagger -textFile ";
		//int nrOfProcessors = Runtime.getRuntime().availableProcessors();
		ExecutorService eservice = Executors.newFixedThreadPool(5);
		CompletionService < Object > cservice = new ExecutorCompletionService < Object > (eservice);
	
		
		File[] files = new File(rawData).listFiles();
		int id = 0;
		for(File f : files){
			System.out.println(id++);
			
			String input = f.getAbsolutePath();
			String output = posData+f.getName();
			if(new File(output).exists()&& new File(output).length() >0)
				continue;
			System.out.println(id++);
			String cmd  ="cmd /c " + prefixcmd +"\"" +input +"\""+">"+"\"" + output +"\"";
			System.out.println(cmd);
			cservice.submit(new cmdProcessor(cmd,id));
		}
	}
	
	
	public static String posTag(String input, MaxentTagger tagger){
		 // Initialize the tagger

        // The tagged string
        String tagged = tagger.tagString(input);
 
        // Output the result
        
        
        return tagged;
	}

	public static void posTagForFolder(String rawData, String posData) {
		// TODO Auto-generated method stub
		
		
		if(!new File(posData).isDirectory())
			new File(posData).mkdirs();
		
		MaxentTagger tagger = new MaxentTagger(
                "tools/stanford-postagger-2013-06-20/models/english-left3words-distsim.tagger");
		
		File[] files = new File(rawData).listFiles();
		
		for(File f : files){
			try {
				String content  = FileUtils.readFileToString(f);
				
				String tagged = posTag(content,tagger);
				
				String output = posData+f.getName();
				
				FileUtils.writeStringToFile(new File(output), tagged, false);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("done");
	}
}
