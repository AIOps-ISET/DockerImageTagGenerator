package LabeledLDA;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.netlib.util.intW;

import scala.tools.nsc.doc.model.DocTemplateEntity;

public class clearData {

	/**
	 * @param args
	 */
	static int doc_tag_thres = 30;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//String root = "D:\\Shaowei\\research\\Folksonomies\\StackOverFlowForShaowe\i";
		//String root = "D:\\Shaowei\\research\\Folksonomies\\StackOverFlowForShaowei\\";
		//String root = "O:\\shaowei\\folksonomy\\freecode\\";
		String root = "/scratch4/shaowei/data/data_and_results/ChangeTagData/stackoverflow/";
		clearTags(root+"tag_doc.txt", root+"tag_doc"+"_"+doc_tag_thres+".txt",doc_tag_thres);
	}
	public static HashMap<String, ArrayList<String>> clearTags(String intput, String output, int tag_thres) {
		// TODO Auto-generated method stub
		HashMap<String, ArrayList<String>> docId_tag = new HashMap();
		try {
			BufferedReader br = new BufferedReader(new FileReader(intput));
			BufferedWriter bw = new BufferedWriter(new FileWriter(output));
			String line = null;
			while((line = br.readLine())!=null){
				String[] sparts = line.split(":");
				String tag = sparts[0];
				String[] docs = sparts[1].split(",");
				if(docs.length > doc_tag_thres){
					bw.write(line);
					bw.newLine();
				}
				for(String docId : docs){
					if(docId_tag.containsKey(docId)){
						docId_tag.get(docId).add(tag);
					}else{
						ArrayList<String> tagList = new ArrayList();
						tagList.add(tag);
						docId_tag.put(docId, tagList);
					}
				}
			}
			br.close();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return docId_tag;
	}
	
}
