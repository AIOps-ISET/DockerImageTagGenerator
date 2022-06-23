package getAssociatePattern;

import java.io.BufferedWriter;
import Network.Node;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AssocRule;

import queryExpansion.Query;
import tagRecommend.Doc;

public class InferenceWithAssocRules {
	
	private AssoicatedRuleMining arm ;
	private String dir;
	
	private double minsup;
	private double minconf;
	private double minlift;
	
	public int hit = 0;
	public int miss = 0;
	public HashMap<String, List<Node>> hitQuery = new HashMap();
	
	static int k=100;
	public InferenceWithAssocRules(String dir, double minsup, double minconf, double minlift){
		// 
		this.minsup = minsup;
		this.minconf = minconf;
		this.minlift = minlift;
		this.dir = dir;
		//this.exclusiveTags = new HashMap();
		
	}
	/*
	 * initialize the arm
	 */
	public void initialize(){
		arm = new AssoicatedRuleMining(dir, this.minsup,this.minconf,this.minlift);
		arm.generateAssociatedRules(dir+"trainDataset_distr.csv");
		
	}
	
	public void initialize(String file){
		arm = new AssoicatedRuleMining(dir, this.minsup,this.minconf,this.minlift);
		arm.generateAssociatedRules(dir+file );
		
	}
	
	
	public void infer(ArrayList<Query> queryListForTesting, HashMap<String,Doc> goldenSet, int givenTagN, boolean debug){
		for(Query q : queryListForTesting ){
			inferForQ(q, queryListForTesting,goldenSet, givenTagN, debug);
		}
	}
	
	
	public void infer(ArrayList<Query> queryListForTesting,
			HashMap<String, Doc> goldenSet,
			HashMap<String, List<String>> givenTags, boolean debug) {
		// TODO Auto-generated method stub
		for(Query q : queryListForTesting ){
			inferForQ(q, queryListForTesting,goldenSet, givenTags, debug);
		}
	}
	
	public void inferForQ(Query q, ArrayList<Query> listForTraining, 
			HashMap<String,Doc> goldenSet, HashMap<String, List<String>> givenTags , boolean debug) {
		// TODO Auto-generated method stub
		int givenTagN = givenTags.get(q.query_id).size();
		Doc doc = goldenSet.get(q.query_id);
		int size= doc.getTaglist().size();
		//
		List<String> tagsGiven = givenTags.get(q.query_id);
		List<String> remainGround = doc.getTaglist().subList(givenTagN <= size ? givenTagN : size, size);
		
		// sort tags to make the representation of tags are unique when the eles in the set are unique
		Collections.sort(tagsGiven);
		//this.exclusiveTags.put(q.query_id, topKTags);
		
		
		ArrayList<String> k_tags_from_other_two_list = q.getTopk(k);
		
		double p = 1;
		String tags = "";
		// the old of tags maters, two order for two orders.
		for(String tag : tagsGiven)
			tags += tag+" ";
		tags = tags.trim();
		if(arm.RulesHashMap.containsKey(tags)){
			
			for(AssocRule r : arm.RulesHashMap.get(tags)){	
				String inferTag = arm.tagTrans.getId_text_map().get(r.getItemset2()[0]);
				if(k_tags_from_other_two_list.contains(inferTag) )
					q.addAssocRuleTag(inferTag, r.getConfidence());
					if(debug)
						checkHitOrMiss(q,remainGround, inferTag, r.getConfidence());
			}
			
		}	
		
		
		
	}
	
	
	
	// p(ti|t1..tk) = p(ti|t1) * p(ti|t2)...*p(ti|tk)
	public void inferForQ(Query q, ArrayList<Query> listForTraining, 
			HashMap<String,Doc> goldenSet, int givenTagN, boolean debug) {
		// TODO Auto-generated method stub
		Doc doc = goldenSet.get(q.query_id);
		int size= doc.getTaglist().size();
		List<String> tagsGiven = doc.getTaglist().subList(0, givenTagN <= size ? givenTagN : size);
		List<String> remainGround = doc.getTaglist().subList(givenTagN <= size ? givenTagN : size, size);
		// sort tags to make the representation of tags are unique when the eles in the set are unique
		Collections.sort(tagsGiven);
		//this.exclusiveTags.put(q.query_id, topKTags);
		
		
		ArrayList<String> k_tags_from_other_two_list = q.getTopk(k);
		
		double p = 1;
		String tags = "";
		// the old of tags maters, two order for two orders.
		for(String tag : tagsGiven)
			tags += tag+" ";
		tags = tags.trim();
		if(arm.RulesHashMap.containsKey(tags)){
			
			for(AssocRule r : arm.RulesHashMap.get(tags)){	
				String inferTag = arm.tagTrans.getId_text_map().get(r.getItemset2()[0]);
				if(k_tags_from_other_two_list.contains(inferTag) )
					q.addAssocRuleTag(inferTag, r.getConfidence());
					if(debug)
						checkHitOrMiss(q,remainGround, inferTag, r.getConfidence());
			}
			
		}	
		
		
		
	}
	
	public void checkHitOrMiss(Query q, List<String> groundTruth, String tag, double confidence){
		if(groundTruth.contains(tag)){
			if(this.hitQuery.containsKey(q.query_id)){
				this.hitQuery.get(q.query_id).add(new Node(tag,confidence));
			}else{
				List<Node> list = new ArrayList();
				list.add(new Node(tag,confidence));
				this.hitQuery.put(q.query_id, list );
			}
		}
	}
	
	
	public static HashMap<String, List<String>> getExclusiveTags(HashMap<String,Doc> goldenSet, int givenTagN) {
		// TODO Auto-generated method stub
		HashMap<String, List<String>> exclusiveTags = new HashMap();	
		for(String query_id : goldenSet.keySet()){
			Doc doc = goldenSet.get(query_id);
			int size= doc.getTaglist().size();
			List<String> topKTags = doc.getTaglist().subList(0, givenTagN <= size ? givenTagN : size);
			// sort tags to make the representation of tags are unique when the eles in the set are unique
			Collections.sort(topKTags);
			exclusiveTags.put(query_id, topKTags);
		}
		return exclusiveTags;
	}
	
	
	public static HashMap<String, List<String>> getExclusiveTags(HashMap<String,Doc> goldenSet, int givenTagN, int whichOne) {
		// TODO Auto-generated method stub
		HashMap<String, List<String>> exclusiveTags = new HashMap();	
		for(String query_id : goldenSet.keySet()){
			Doc doc = goldenSet.get(query_id);
			int size= doc.getTaglist().size();
			List<String> topKTags = new ArrayList();
			int index = whichOne%doc.getTaglist().size();
			topKTags.add(doc.getTaglist().get(index));
			// sort tags to make the representation of tags are unique when the eles in the set are unique
			Collections.sort(topKTags);
			exclusiveTags.put(query_id, topKTags);
		}
		return exclusiveTags;
	}
	
	/**
	 * output the hit tag inferred by associate rule
	 * @param output
	 */
	public void outputHitSet(String output,ArrayList<Query> querys ){
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(output,true));
			for(int i = 0 ;i < querys.size(); i++){
				if(this.hitQuery.containsKey(querys.get(i).query_id)){
					bw.write("Q:"+querys.get(i).query_id+"\n");
					bw.write("groundtruth:" + querys.get(i).trueTags+"\n");
					for(Node n : this.hitQuery.get(querys.get(i).query_id)){
						bw.write(n.getName() + ":" + n.getWeight()+"\t");
					}
					bw.write("\n");
					this.hit++;
				}else{
					this.miss++;
				}
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
