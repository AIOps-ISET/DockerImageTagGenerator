package LabeledLDA;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.netlib.util.doubleW;

import com.sun.org.glassfish.external.statistics.AverageRangeStatistic;

public class generateDataset {

	/**
	 * @param args
	 * @throws IOException 
	 */
	
	static int docNumberInTag = 50;
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		//String root = "D:\\Shaowei\\research\\Folksonomies\\stackoverflow\\";
		//String root = "D:\\Shaowei\\research\\Folksonomies\\stackoverflow\\";
		//String root = "O:\\shaowei\\folksonomy\\freecode\\";
		//String root = "O:\\shaowei\\folksonomy\\AlltheFourDataset\\appleSource\\";
		//String root = "G:/research/tag_recommendation/data_and_results/AlltheFourDataset/SuperUserSource/";
		
		if(args.length < 1){
			System.out.println("usage [root path]");
			return ;
		}
		String root = args[0];
		
		HashMap<String, ArrayList<String>> tag_docs = loadTags_doc(root+"tag_doc_50.txt");
		st(tag_docs);
		
		generateCVSDataSet(root);
		
	}
	
	public static void  st(HashMap<String, ArrayList<String>> id_tags){
		int sum = 0;
		int max = 0;
		for(ArrayList<String> docsArrayList : id_tags.values()){
			sum += docsArrayList.size();
			if(docsArrayList.size() >max){
				max = docsArrayList.size();
			}
		}
		
		System.out.println((double)sum/(double)id_tags.size());
		System.out.println(max);
	}
	
	public static void  generateCVSDataSet(String root){
		//String root = "G:/research/tag_recommendation/data_and_results/ChangeTagData/stackoverflow/";
		System.out.println("start loading....");
		HashMap<String, ArrayList<String>> id_tags = loadTags(root+"tag_doc_50.txt");
		try{
			BufferedWriter outputCSV = new BufferedWriter(new FileWriter(root+"dataset.csv"));
			String docDir = root+"descriptionCleaned";
			
			File[] docList = new File(docDir).listFiles();
			
			for(File f : docList){
				System.out.println(f.getAbsolutePath());
				String id = f.getName();
				String content = readContentIntoOneLine(f.getAbsolutePath()).trim().replace(" +", " ");
				ArrayList<String> tagList = id_tags.get(id);
				if(tagList ==null || content.trim().equals(""))
					continue;
				String tags = "";
				for(String tag :tagList){
					tag = tag.replace(" ", "_");
					tags += tag+" ";
				}
				tags = tags.trim();
				outputCSV.write(id+","+tags+","+content);
				outputCSV.write("\n");
			}
			outputCSV.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	

	private static HashMap<String, ArrayList<String>> loadTags(String file) {
		// TODO Auto-generated method stub
		HashMap<String, ArrayList<String>> docId_tag = new HashMap();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = null;
			while((line = br.readLine())!=null){
				String[] sparts = line.split(":");
				String tag = sparts[0];
				String[] docs = sparts[1].split(",");
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return docId_tag;
	}
	
	private static HashMap<String, ArrayList<String>> loadTags_doc(String file) {
		// TODO Auto-generated method stub
		HashMap<String, ArrayList<String>> tag_docs = new HashMap();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = null;
			while((line = br.readLine())!=null){
				String[] sparts = line.split(":");
				String tag = sparts[0];
				String[] docs = sparts[1].split(",");
				ArrayList<String> docsArrayList = new ArrayList<>();
				for(String docId : docs){
					docsArrayList.add(docId);
					
				}
				tag_docs.put(tag, docsArrayList);
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tag_docs;
	}
	
	
	public static String readContentIntoOneLine(String file){
		StringBuilder sb = new StringBuilder();
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
			String line = null;
			while((line = br.readLine())!=null){
				sb.append(line+" ");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static void generateCSVDataset(String root, String finalCSVDataset) {
		// TODO Auto-generated method stub
		//String root = "G:/research/tag_recommendation/data_and_results/ChangeTagData/stackoverflow/";
				System.out.println("start loading....");
				HashMap<String, ArrayList<String>> id_tags = loadTags(root+"tag_doc_50.txt");
				try{
					BufferedWriter outputCSV = new BufferedWriter(new FileWriter(root+finalCSVDataset));
					String docDir = root+"descriptionCleaned";
					
					File[] docList = new File(docDir).listFiles();
					
					for(File f : docList){
						System.out.println(f.getAbsolutePath());
						String id = f.getName();
						String content = readContentIntoOneLine(f.getAbsolutePath()).trim().replace(" +", " ");
						ArrayList<String> tagList = id_tags.get(id);
						if(tagList ==null || content.trim().equals(""))
							continue;
						String tags = "";
						for(String tag :tagList){
							tag = tag.replace(" ", "_");
							tags += tag+" ";
						}
						tags = tags.trim();
						outputCSV.write(id+","+tags+","+content);
						outputCSV.write("\n");
					}
					outputCSV.close();
				}catch(Exception e){
					e.printStackTrace();
				}
	}
}
