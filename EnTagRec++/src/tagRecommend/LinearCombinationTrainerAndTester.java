package tagRecommend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import queryExpansion.Query;
import Network.Node;

public class LinearCombinationTrainerAndTester {
	
	ArrayList<Query> data;
	HashMap<String, Doc> goldenSet;
	int topK;
	static float step = 0.1f;
	
	public LinearCombinationTrainerAndTester(ArrayList<Query> data , HashMap<String, Doc> goldenSet, int topK){
		this.data  = data;
		this.goldenSet = goldenSet;
		this.topK = topK;
	}
	//return recalls
	public HashMap<String, Double> performCrossValidation(int k){
		HashMap<String, Double> recalls =new  HashMap();
		ArrayList<Query> remainTest = (ArrayList<Query>) data.clone();
		int[] testCaseNumberPerCross = new int[10];
		int average = data.size() / k;

		// get the number of each cross test
		for (int i = 0; i < k; i++) {
			if (i < k - 1) {
				testCaseNumberPerCross[i] = average;
			} else
				testCaseNumberPerCross[i] = data.size() - average
						* (k - 1);
		}
		// perform k cross validation
		for (int i = 0; i < k; i++) {
			// get test data
			ArrayList<Query> test = new ArrayList();
			ArrayList<Query> train = new ArrayList();
			for(int j =0; j < testCaseNumberPerCross[i];j++){
				Random rn = new Random();
				int random =0;
				if(remainTest.size()-1==0)
					random =0;
				else
					random = rn.nextInt(remainTest.size()-1);
				Query q = remainTest.get(random);
				test.add(q);
				remainTest.remove(q);
			}
			// get train data
			for(Query q: data){
				if(!test.contains(q))
					train.add(q);
			}
			
			
			// train the paras
			double[] paras = trainParameters(test,2);
			
			System.out.println("iter"+i+"\t"+paras[0]+"\t"+paras[1]+"\t" + calculateObjectValue(paras,test));
			HashMap<String,Double> recallsFromTest = calculateRecalls(paras,test);
			// dump recallsFromTest to recalls
			for(String key : recallsFromTest.keySet()){
				recalls.put(key, recallsFromTest.get(key));
			}
		}
		
		
		return recalls;
	}
	
	// trianing parameter for entagrec
	public  double[] trainTwoParameters(ArrayList<Query> test, int paraNum){
		double[] paras = new double[paraNum];
		
		// initialize the paras
		for(int i=0; i < paras.length; i++)
			paras[i] = 0;
		
		
		
		//train
		double[] best= new double[paraNum];
		double bestObjectValue = 0;
		//int count = 1;
		for(int i =(int) ((1/step)/2); i < 1/step; i++){
			
			paras[0] = paras[0] + step; 
			paras[1] = 0;
			for(int j = 0; j < (1/step)/2; j++){
				paras[1] +=step; 
				
				double objectValue = calculateObjectValue(paras,  test);
				//System.out.println(paras[0] + "\t" + paras[1] + "\t" + objectValue);
				if(objectValue>bestObjectValue)
				{
					bestObjectValue = objectValue;
					best = paras.clone();
					//System.out.println(paras[0] + "\t" + paras[1] + "\t" + bestObjectValue);
				}
				
			}
			
		}
				
				
		
		return best;
	}
	
	
	public  double[] trainParameters(ArrayList<Query> test, int paraNum){
		double[] paras = new double[paraNum];
		
		// initialize the paras
		for(int i=0; i < paras.length; i++)
			paras[i] = 0;
		
		
		
		//train
		double[] best= new double[paraNum];
		double bestObjectValue = 0;
		//int count = 1;
		for(int i =(int) ((1/step)/2); i < 1/step; i++){
			
			paras[0] = paras[0] + step; 
			paras[1] = 0;
			for(int j = 0; j < (1/step)/2; j++){
				paras[1] +=step; 
				paras[2]=0;
				for(int x  =0; x < (1/step)/3; x++){
					//System.out.println("training:" + count++);
					paras[2] +=step; 
					double objectValue = calculateObjectValue(paras,  test);
					//System.out.println(paras[0] + "\t" + paras[1] + "\t" + objectValue);
					if(objectValue>bestObjectValue)
					{
						bestObjectValue = objectValue;
						best = paras.clone();
						//System.out.println(paras[0] + "\t" + paras[1] + "\t" + bestObjectValue);
					}
				}
			}
			
		}
				
				
		
		return best;
	}
	
	/// training first two para
	public  double[] trainParameters(ArrayList<Query> test){
		double[] paras = new double[4];
		
		// initialize the paras
		for(int i=0; i < paras.length; i++)
			paras[i] = 0;
		
		
		
		//train
		double[] best= new double[4];
		double bestObjectValue = 0;
		int count = 1;
		for(int i =0; i < 1/step; i++){
			
			paras[0] = paras[0] + step; 
			paras[1] = 0;
			for(int j = 0; j < (1/step); j++){
				paras[1] +=step; 
				double objectValue = calculateObjectValue(paras,  test);
				System.out.println(count++ +"\t"+paras[0] + "\t" + paras[1] + "\t" + objectValue);
				if(objectValue>bestObjectValue)
				{
					bestObjectValue = objectValue;
					best = paras.clone();
					//System.out.println(paras[0] + "\t" + paras[1] + "\t" + bestObjectValue);
				}
			}
		}
			
		
				
				
		
		return best;
	}


	private double calculateObjectValue(double[] paras,ArrayList<Query> test) {
		// TODO Auto-generated method stub
		double sum = 0;
		for(Query q : test){
			
			ArrayList<Node> topNode =  q.getLinearCombineTopk(topK, paras);
			ArrayList<String> topTags = new ArrayList();
			for(Node n : topNode)
				topTags.add(n.getName());
			String id = q.query_id;
			ArrayList<String> tags = this.goldenSet.get(id).getTaglist();
			int count = 0;
			for (String tag : tags) {
				
				if (topTags.contains(tag)) {
					count++;
				}
			}
			
			sum += (double) (count) / (double) tags.size();
		}
		return sum/ (double) test.size();
	}
	
////calculate the recalls exclude the tags
	public HashMap<String,Double> calculateRecalls(double[] paras,ArrayList<Query> test, 
			HashMap<String,List<String>> exclusiveTags) {
		// TODO Auto-generated method stub
		HashMap<String,Double> result = new HashMap();
		for(Query q : test){
			int exclusiveTagN = exclusiveTags.get(q.query_id).size();
			ArrayList<Node> topNode =  q.getLinearCombineTopk(exclusiveTagN+topK, paras,false);
			ArrayList<String> topTags = new ArrayList();
			for(Node n : topNode)
				topTags.add(n.getName());
			String id = q.query_id;
			if(id.equals("1801"))
				System.out.println();
			ArrayList<String> tags = this.goldenSet.get(id).getTaglist();
			
			if(tags.size() <= exclusiveTagN)
				continue;
			int count = 0;
			int i =0;
			int exceptExcluTags = 0;
			while(exceptExcluTags < topK && i < topTags.size()) {
				
				String tag = topTags.get(i++);
				
				//if tag in exclusiveTAgs, skip
				if(exclusiveTags!=null && exclusiveTags.get(id).contains(tag)){
					continue;
				}
				if (tags.contains(tag)) {
					count++;
					
				}
				exceptExcluTags++;
			
			}
			double recall = (double) (count) / (double) (tags.size());
			if(exclusiveTags !=null){
				 recall = tags.size() > exclusiveTags.get(id).size()? 
					(double) (count) / (double) (tags.size()-exclusiveTags.get(id).size()) : 0.0;
			}
			if(recall > 1)
				System.out.println();
			result.put(this.goldenSet.get(id).getName(),recall);
		}
		return result;
	}
	
	
	
	
	public HashMap<String,Double> calculateRecalls(double[] paras,ArrayList<Query> test) {
		// TODO Auto-generated method stub
		HashMap<String,Double> result = new HashMap();
		for(Query q : test){
			
			ArrayList<Node> topNode =  q.getLinearCombineTopk(topK, paras);
			ArrayList<String> topTags = new ArrayList();
			for(Node n : topNode)
				topTags.add(n.getName());
			String id = q.query_id;
			ArrayList<String> tags = this.goldenSet.get(id).getTaglist();
			int count = 0;
			for (String tag : tags) {
				//if tag in exclusiveTAgs, skip
				
				if (topTags.contains(tag)) {
					count++;
				}
			
			}
			
			result.put(this.goldenSet.get(id).getName(),(double) (count) / (double) tags.size());
		}
		return result;
	}
	
	public HashMap<String,Double> calculatPrecision(double[] paras,ArrayList<Query> test) {
		// TODO Auto-generated method stub
		HashMap<String,Double> result = new HashMap();
		for(Query q : test){
			
			ArrayList<Node> topNode =  q.getLinearCombineTopk(topK, paras);
			ArrayList<String> topTags = new ArrayList();
			for(Node n : topNode)
				topTags.add(n.getName());
			String id = q.query_id;
			ArrayList<String> tags = this.goldenSet.get(id).getTaglist();
			int count = 0;
			for (String tag : tags) {
				
				if (topTags.contains(tag)) {
					count++;
				}
			}
			
			result.put(this.goldenSet.get(id).getName(),(double) (count) / (double) topK);
		}
		return result;
	}
	
	public HashMap<String,Double> calculatPrecision(double[] paras,ArrayList<Query> test, 
			HashMap<String, List<String>> exclusiveTags) {
		// TODO Auto-generated method stub
		HashMap<String,Double> result = new HashMap();
		for(Query q : test){
			
			int exclusiveTagN = exclusiveTags.get(q.query_id).size();
			ArrayList<Node> topNode =  q.getLinearCombineTopk(topK + exclusiveTagN, paras);
			ArrayList<String> topTags = new ArrayList();
			for(Node n : topNode)
				topTags.add(n.getName());
			String id = q.query_id;
			ArrayList<String> tags = this.goldenSet.get(id).getTaglist();
			
			// ground truth size <= exclusive Tag N
			if(exclusiveTagN >= tags.size())
				continue;
			
			int count = 0;
			int i =0;
			int exceptExcluTags = 0;
			while(exceptExcluTags < topK && i < topTags.size()) {
				
				String tag = topTags.get(i++);
				
				//if tag in exclusiveTAgs, skip
				if(exclusiveTags!=null && exclusiveTags.get(id).contains(tag)){
					continue;
				}
				if (tags.contains(tag)) {
					count++;
					
				}
				exceptExcluTags++;
			}
			
			result.put(this.goldenSet.get(id).getName(),(double) (count) / (double) topK);
		}
		return result;
	}
	
	
	public HashMap<String,Double> calculatPrecision(double[] paras,ArrayList<Query> test, 
			HashMap<String, List<String>> exclusiveTags, int exclusiveTagN) {
		// TODO Auto-generated method stub
		HashMap<String,Double> result = new HashMap();
		for(Query q : test){
			
			
			ArrayList<Node> topNode =  q.getLinearCombineTopk(topK + exclusiveTagN, paras);
			ArrayList<String> topTags = new ArrayList();
			for(Node n : topNode)
				topTags.add(n.getName());
			String id = q.query_id;
			ArrayList<String> tags = this.goldenSet.get(id).getTaglist();
			
			// ground truth size <= exclusive Tag N
			if(exclusiveTagN >= tags.size())
				continue;
			
			int count = 0;
			int i =0;
			int exceptExcluTags = 0;
			while(exceptExcluTags < topK && i < topTags.size()) {
				
				String tag = topTags.get(i++);
				
				//if tag in exclusiveTAgs, skip
				if(exclusiveTags!=null && exclusiveTags.get(id).contains(tag)){
					continue;
				}
				if (tags.contains(tag)) {
					count++;
					
				}
				exceptExcluTags++;
			}
			
			result.put(this.goldenSet.get(id).getName(),(double) (count) / (double) topK);
		}
		return result;
	}
	
	
	
	public HashMap<String,Double> calculateRecalls(double[] paras,ArrayList<Query> test, int cur_topk) {
		// TODO Auto-generated method stub
		HashMap<String,Double> result = new HashMap();
		for(Query q : test){
			
			ArrayList<Node> topNode =  q.getLinearCombineTopk(cur_topk, paras);
			ArrayList<String> topTags = new ArrayList();
			for(Node n : topNode)
				topTags.add(n.getName());
			String id = q.query_id;
			ArrayList<String> tags = (ArrayList<String>) this.goldenSet.get(id).getTaglist();
			int count = 0;
			for (String tag : tags) {
				
				if (topTags.contains(tag)) {
					count++;
				}
			}
			
			result.put(this.goldenSet.get(id).getName(),(double) (count) / (double) tags.size());
		}
		return result;
	}
	public double[] trainParametersWithAssoR(
			ArrayList<Query> queryListForTraining, int paraNum, HashMap<String,List<String>> exclusiveTags, 
			 boolean additionalTags, boolean userInfo) {
		// TODO Auto-generated method stub
		double[] paras = new double[paraNum];
		
		// initialize the paras
		for(int i=0; i < paras.length; i++)
			paras[i] = 0;
		
		paras[0]=1;
		paras[1] =0.1;
		
		//train
		double[] best= new double[paraNum];
		double bestObjectValue = 0;
		int count = 1;
		for(int i = 0; i < (1/step)/2; i++){
			
			
			paras[3] = 0;
			for(int j = 0; j < (1/step)/2; j++){
					
					System.out.println("training:" + count++);
					
					double objectValue = calculateObjectValue(paras,  queryListForTraining, exclusiveTags);
					//System.out.println(paras[2] + "\t" + paras[3] + "\t" + objectValue);
					if(objectValue>bestObjectValue)
					{
						bestObjectValue = objectValue;
						best = paras.clone();
						System.out.println(paras[0] + "\t" + paras[1] + "\t" + paras[2] + "\t" + paras[3] + "\t" + bestObjectValue);
					}
					if(!additionalTags)
						break;
					paras[3] +=step; 
			}
			if(!userInfo)
				break;
			paras[2] = paras[2] + step;
		}
		

		return best;
	}
	private double calculateObjectValue(double[] paras,
			ArrayList<Query> queryListForTraining, HashMap<String,List<String>> exclusiveTags) {
		// TODO Auto-generated method stub
		double sum =0;
		HashMap<String,Double> recalls = calculateRecalls(paras,
				queryListForTraining, exclusiveTags);
		for(double recall: recalls.values())
			sum += recall;
		
		return sum/(double) recalls.size();
	}
}
