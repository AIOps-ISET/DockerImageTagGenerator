package TermTagIndex;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import Network.Graph;
import Network.Node;

import queryExpansion.Query;

public class TermTagIndex {
	HashMap<String, Term> termTag;
	
	public static String INFER_UNIQUE_TOKEN = "INFER_UNIQUE_TOKEN";
	public static String INFER_NON_UNIQUE_TOKEN = "INFER_NON_UNIQUE_TOKEN";
	
	public TermTagIndex(){
		this.termTag = new HashMap();
	}
	
	public void LoadIndexFromFile(String path){
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			String line = null;
			while((line = br.readLine())!=null){
				String[] temp = line.split(":");
				String token = temp[0];
				Term term = new Term(token);
				HashMap<String, Integer> tags_occur_map = new HashMap();
				String[] tags = temp[1].split(",");
				for(String tag_occur : tags){
					String[] splitStr = tag_occur.split("@");
					tags_occur_map.put(splitStr[0], Integer.parseInt(splitStr[1]));
				}
				term.setTag_occur(tags_occur_map);
				this.termTag.put(token,term);
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void AssignTags(Query q, String approach, int Topk) {
		// TODO Auto-generated method stub
		HashMap<String, Node> tagCands = new HashMap();
		String queryText = q.getText();
		double max = 1;
		if(approach.equals(TermTagIndex.INFER_NON_UNIQUE_TOKEN)){
			String[] tokens = queryText.split(" +");
			for(String token : tokens){
				// construct tagCands
				Term t = this.termTag.get(token);
				if(t== null)
					continue;
				for(String tag: t.getTag_occur().keySet()){
					if(tagCands.containsKey(tag)){
						// Accumulate the total 
						//if(tag.equals("security"))
						//	System.out.println(t.getName() + "\t"+t.getTag_occur().get(tag));
						//double occur = tagCands.get(tag).getWeight() + Math.log10(t.getTag_occur().get(tag))*t.getTerm_IDF();
						//double occur = tagCands.get(tag).getWeight() + t.getTerm_IDF();
						double occur = tagCands.get(tag).getWeight() +1;
						if(occur > max)
							max = occur;
						tagCands.get(tag).setWeight(occur);
						
					}
					else{
						Node n = new Node(tag,t.getTag_occur().get(tag));
						n.setWeight(1);
						tagCands.put(tag, n);
					}
				}
			}
			
			
		}
		// normalize the score
		
		for(Node n : tagCands.values())
			n.setWeight(n.getWeight()/max);
		
		
		ArrayList<Node> tagList = new ArrayList( tagCands.values());
		ArrayList<Node> topK_tags = Graph.getTopKTags(Topk, tagList);
		
		for(Node tag: topK_tags){
			String name = tag.getName();
			q.addTermInfTags(name, tag.getWeight());
		}
		
	}
}
