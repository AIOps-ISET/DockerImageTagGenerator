package queryExpansion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import Network.Graph;
import Network.Node;

public class Query {
	private String inferredTagText;
	private String text;
	public String id;
	public String query_id;
	public ArrayList<String> trueTags;
	
	
	// llda infer
	public HashMap<String,Node> inferedTagsFromLLDA;
	// network spread
	public  HashMap<String,Node> extendedTags;
	// term tag index infer
	public HashMap<String, Node> termTagInferTags;
	
	public HashMap<String, Node> inferedTagsFromUser;
	
	public HashMap<String, Node> inferedTagsFromAsscoiatedRule;
	
	public double maxLLDATagScore;
	public double maxFreqTagScore;
	public double maxAsTagScore;
	public double maxUserTagScore;
	
	public Query(String original){
		this.inferredTagText = original;
		this.extendedTags = new HashMap();
		this.inferedTagsFromLLDA = new HashMap();
		this.termTagInferTags = new HashMap();
		this.inferedTagsFromUser = new HashMap();
		this.inferedTagsFromAsscoiatedRule = new HashMap();
	}
	
	public ArrayList<String> getTags(){
		ArrayList<String> tags = new ArrayList();
		
		for(Node n :inferedTagsFromLLDA.values()){
			tags.add(n.getName());
		}
		/*if(extendedTags !=null)
		{
			for(Node n : extendedTags.values()){
				if(!tags.contains(n.getName()))
					tags.add(n.getName());
			}
		}*/
		return tags;
	}
	
	
	public void setTrueTags(ArrayList<String> tags){
		this.trueTags = tags;
	}
	public void addInfTags(String tag, double weight) {
		// TODO Auto-generated method stub
		if(!this.inferedTagsFromLLDA.containsKey(tag)){
			Node n = new Node(tag,weight);
			this.inferedTagsFromLLDA.put(tag, n);
		}
	}
	
	public void addTermInfTags(String tag, double weight) {
		// TODO Auto-generated method stub
		if(!this.termTagInferTags.containsKey(tag)){
			Node n = new Node(tag,weight);
			this.termTagInferTags.put(tag, n);
		}
	}
	
	public void addAssocRuleTag(String tag, double weight) {
		// TODO Auto-generated method stub
		if(!this.inferedTagsFromAsscoiatedRule.containsKey(tag)){
			Node n = new Node(tag,weight);
			this.inferedTagsFromAsscoiatedRule.put(tag, n);
		}
	}
	
	public void addUserTags(String tag, double weight){
		if(!this.inferedTagsFromUser.containsKey(tag)){
			Node n = new Node(tag,weight);
			this.inferedTagsFromUser.put(tag, n);
		}
	}
	
	public void extendTagWithTagGraph(Graph g, int topK, int pluseSept){
		//  
		/*Graph newG = g.clone();
		for(String key : this.inferedTags.keySet()){
			newG.pluse(this.inferedTags.get(key),0);
		}
		ArrayList<Node> tags = newG.getTopKTags(topK);
		for(Node tag : tags){
			this.extendedTags.put(tag.getName(), tag);
		}
		newG.destory();*/
		for(String key : this.termTagInferTags.keySet()){
			//if(key.equals("mouse"))
			//	System.out.println();
			g.pluse(this.termTagInferTags.get(key),pluseSept);
		}
		ArrayList<Node> tags = g.getTopKTags(topK);
		for(Node tag : tags){
		//	this.extendedTags.put(tag.getName(), tag.clone());
			this.termTagInferTags.put(tag.getName(), tag);
		}
		
		
		g.cleanNodeWeight();
	}
	
	public void extendTagWithTagGraph(Graph g, int topK){
		//  
		/*Graph newG = g.clone();
		for(String key : this.inferedTags.keySet()){
			newG.pluse(this.inferedTags.get(key),0);
		}
		ArrayList<Node> tags = newG.getTopKTags(topK);
		for(Node tag : tags){
			this.extendedTags.put(tag.getName(), tag);
		}
		newG.destory();*/
		for(String key : this.termTagInferTags.keySet()){
			//if(key.equals("mouse"))
			//	System.out.println();
			g.pluse(this.termTagInferTags.get(key),0);
		}
		ArrayList<Node> tags = g.getTopKTags(topK);
		for(Node tag : tags){
		//	this.extendedTags.put(tag.getName(), tag.clone());
			this.termTagInferTags.put(tag.getName(), tag);
		}
		
		
		g.cleanNodeWeight();
	}
	
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String print(){
		StringBuilder result = new StringBuilder();
		//append original text
		result.append(this.inferredTagText+" ");
		// append  extended tags
		for(String key : this.extendedTags.keySet())
			result.append(this.extendedTags.get(key).getName()+":"+this.extendedTags.get(key).getWeight());
		return result.toString();
	}

	public String getOriginalText() {
		return inferredTagText;
	}

	public void setOriginalText(String originalText) {
		this.inferredTagText = originalText;
	}

	public HashMap<String, Node> getInferedTagsFromLLDA() {
		return inferedTagsFromLLDA;
	}

	public void setInferedTagsFromLLDA(HashMap<String, Node> inferedTags) {
		this.inferedTagsFromLLDA = inferedTags;
	}

	public HashMap<String, Node> getExtendedTags() {
		return extendedTags;
	}

	public void setExtendedTags(HashMap<String, Node> extendedTags) {
		this.extendedTags = extendedTags;
	}

	public ArrayList<Node> getLinearCombineTopk(int topK, double[] paras) {
		// TODO Auto-generated method stub
		ArrayList<Node> result = new ArrayList();
		//
		Set<String> set = new HashSet();
		Set<String> set1 = this.termTagInferTags.keySet();
		Set<String> set2 = this.inferedTagsFromLLDA.keySet();
		Set<String> set3 = this.inferedTagsFromUser.keySet();
		Set<String> set4 = this.inferedTagsFromAsscoiatedRule.keySet();
		set.addAll(new ArrayList(set1));
		set.addAll(new ArrayList(set2));
		set.addAll(new ArrayList(set3));
		set.addAll(new ArrayList(set4));
		for(String tag : set){
		//for(String tag : set1){
			
			double w1 = 0;
			double w2 = 0;
			double w3 = 0;
			double w4 = 0;
			if(this.termTagInferTags != null && this.termTagInferTags.containsKey(tag)){
				w1 = this.termTagInferTags.get(tag).getWeight();
				if(new Double(w1).isNaN())
					w1 = 0;
			}
			if(this.inferedTagsFromLLDA!=null && this.inferedTagsFromLLDA.containsKey(tag)){
				w2 = this.inferedTagsFromLLDA.get(tag).getWeight();
				if(new Double(w2).isNaN())
					w2 = 0;
			}
			
			if(this.inferedTagsFromUser !=null && this.inferedTagsFromUser.containsKey(tag)){
				w3  = this.inferedTagsFromUser.get(tag).getWeight();
				if(new Double(w3).isNaN())
					w3 = 0;
			}
			
			if(this.inferedTagsFromAsscoiatedRule !=null && this.inferedTagsFromAsscoiatedRule.containsKey(tag)){
				w4  = this.inferedTagsFromAsscoiatedRule.get(tag).getWeight();
				if(new Double(w4).isNaN())
					w4 = 0;
			}
			
			double total = paras[0]*w1 + paras[1] * w2 + paras[2] * w3+ paras[3] *w4;
			
			result.add(new Node(tag,total));
		}
		ArrayList<Node> topK_tags = Graph.getTopKTags(topK, result);
		
		return topK_tags;
		
		
	}
	
	public ArrayList<Node> getLinearCombineTopk(int topK, double[] paras, boolean normalization) {
		// TODO Auto-generated method stub
		ArrayList<Node> result = new ArrayList();
		//
		Set<String> set = new HashSet();
		Set<String> set1 = this.termTagInferTags.keySet();
		Set<String> set2 = this.inferedTagsFromLLDA.keySet();
		Set<String> set3 = this.inferedTagsFromUser.keySet();
		Set<String> set4 = this.inferedTagsFromAsscoiatedRule.keySet();
		set.addAll(new ArrayList(set1));
		set.addAll(new ArrayList(set2));
		set.addAll(new ArrayList(set3));
		set.addAll(new ArrayList(set4));
		
		// normalization
		if(normalization){
			this.maxAsTagScore = getMax(this.getInferedTagsFromAsscoiatedRule());
			this.maxLLDATagScore = getMax(this.getInferedTagsFromLLDA());
			this.maxFreqTagScore = getMax(this.getTermTagInferTags());
			this.maxUserTagScore = getMax(this.getInferedTagsFromUser());
		}
		
		for(String tag : set){
		//for(String tag : set1){
			
			double w1 = 0;
			double w2 = 0;
			double w3 = 0;
			double w4 = 0;
			if(this.termTagInferTags != null && this.termTagInferTags.containsKey(tag)){
				w1 = this.termTagInferTags.get(tag).getWeight();
				//w1 = normalization? w1/this.maxFreqTagScore : w1; 
				if(new Double(w1).isNaN())
					w1 = 0;
			}
			if(this.inferedTagsFromLLDA!=null && this.inferedTagsFromLLDA.containsKey(tag)){
				w2 = this.inferedTagsFromLLDA.get(tag).getWeight();
				//w2 = normalization? w2/this.maxLLDATagScore : w2; 
				if(new Double(w2).isNaN())
					w2 = 0;
			}
			
			if(this.inferedTagsFromUser !=null && this.inferedTagsFromUser.containsKey(tag)){
				w3  = this.inferedTagsFromUser.get(tag).getWeight();
				//w3 = normalization? w3/this.maxUserTagScore : w3; 
				if(new Double(w3).isNaN())
					w3 = 0;
			}
			
			if(this.inferedTagsFromAsscoiatedRule !=null && this.inferedTagsFromAsscoiatedRule.containsKey(tag)){
				w4  = this.inferedTagsFromAsscoiatedRule.get(tag).getWeight();
				w4 = normalization? w4/this.maxFreqTagScore : w4; 
				if(new Double(w4).isNaN())
					w4 = 0;
			}
			
			double total = paras[0]*w1 + paras[1] * w2 + paras[2] * w3+ paras[3] *w4;
			
			result.add(new Node(tag,total));
		}
		ArrayList<Node> topK_tags = Graph.getTopKTags(topK, result);
		
		return topK_tags;
		
		
	}
	
	
	public double getMax(HashMap<String, Node> inferredTag){
		double max = 0;
		for(Node n : inferredTag.values()){
			if(n.getWeight() > max)
				max = n.getWeight();
		}
		
		return max;
	}

	public HashMap<String, Node> getTermTagInferTags() {
		return termTagInferTags;
	}

	public HashMap<String, Node> getInferedTagsFromUser() {
		return inferedTagsFromUser;
	}

	public void setInferedTagsFromUser(HashMap<String, Node> inferedTagsFromUser) {
		this.inferedTagsFromUser = inferedTagsFromUser;
	}

	public HashMap<String, Node> getInferedTagsFromAsscoiatedRule() {
		return inferedTagsFromAsscoiatedRule;
	}

	public void setInferedTagsFromAsscoiatedRule(
			HashMap<String, Node> inferedTagsFromAsscoiatedRule) {
		this.inferedTagsFromAsscoiatedRule = inferedTagsFromAsscoiatedRule;
	}

	public void setTermTagInferTags(HashMap<String, Node> termTagInferTags) {
		this.termTagInferTags = termTagInferTags;
	}
	
	// get kop 50 tags from  two list 
	public ArrayList<String> getTopk(int k) {
		// TODO Auto-generated method stub
		ArrayList<Node> list1 = new ArrayList();
		ArrayList<Node> list2 = new ArrayList();
		list1.addAll(this.inferedTagsFromLLDA.values());
		list2.addAll(this.termTagInferTags.values());
		ArrayList<Node> topKNode1 =  Graph.getTopKTags(k, list1);
		ArrayList<Node> topKNode2 =  Graph.getTopKTags(k, list2);
		
		ArrayList<String> results = new ArrayList();
		
		for(Node n : topKNode1){
			results.add(n.getName());
		}
		for(Node n : topKNode2){
			results.add(n.getName());
		}
 		return results;
	}

	
	
}
