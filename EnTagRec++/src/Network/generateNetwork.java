package Network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class generateNetwork {

	/**
	 * @param args
	 */
	private String datasetPath;

	private HashMap<String, ArrayList<String>> tag_docs;
	private HashMap<String , Float> similarities;
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public generateNetwork(String path) {
		this.datasetPath = path;
		this.similarities = new HashMap();
		this.tag_docs = new HashMap();
	}

	public void getNetwork() {
		load();
		// dump the tags into arraylist
		ArrayList<Tag> tagList = new ArrayList();
	
		for(String tag : this.tag_docs.keySet()){
			Tag t = new Tag(tag, this.tag_docs.get(tag));
			tagList.add(t);
		}
		
		// calculate and output
		for(int i =0; i < tagList.size() -1 ; i++){
			Tag t1 = tagList.get(i);
			for(int j =i+1; j < tagList.size(); j++){
				Tag t2 = tagList.get(j);
				float similar = calculated_commomFiles(t1.getDoclist(), t2.getDoclist());
				String key = t1.getName() + "\t" + t2.getName();
				similarities.put(key, similar);
				//System.out.println(i+"\t"+j+"\t"+key+"\t"+similar);
			}
		}

	}
	
	public void output(String outputPath){
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath));
			for(String key : this.similarities.keySet()){
				bw.write(key+"\t"+this.similarities.get(key)+"\n");
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void load() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					this.datasetPath));
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] sparts = line.split(",");
				String docId = sparts[0];
				String[] tagstr = sparts[1].split(" ");
				for (String tag : tagstr) {
					if (this.tag_docs.containsKey(tag)) {
						this.tag_docs.get(tag).add(docId);
					} else {
						ArrayList<String> docList = new ArrayList();
						docList.add(docId);
						this.tag_docs.put(tag, docList);
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static private float calculated_commomFiles(ArrayList<String> tag1doclist,
			ArrayList<String> tag2doclist) {
		float result = 0;
		int commonfileNum = 0;
		for (String doc : tag1doclist)
			if (tag2doclist.contains(doc))
				commonfileNum++;
		result = (float) commonfileNum
				/ (float) (tag2doclist.size() + tag1doclist.size() - commonfileNum);
		return result;
	}

}
