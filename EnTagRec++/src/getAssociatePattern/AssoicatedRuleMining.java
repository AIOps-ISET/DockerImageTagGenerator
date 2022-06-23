package getAssociatePattern;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AlgoAgrawalFaster94;
import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AssocRule;
import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AssocRules;
import ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth.AlgoFPGrowth;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemsets;

public class AssoicatedRuleMining {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String dir = "G:\\research\\tag_recommendation\\folksonomy\\AlltheFourDataset\\appleSource\\testcase\\0\\";
		//AssoicatedRuleMining arm = new AssoicatedRuleMining(dir);
		//arm.generateAssociatedRules(dir+"trainDataset_distr.csv");
		
	}
	
	static String RulesOuputFile = "rules.txt";
	static String TransactionFile = "tagsId.txt";
	static String TagIdMapFile = "tags_id_map.txt";
	HashMap<String, ArrayList<AssocRule>> RulesHashMap; 
	
	/*
	 * generate the assoicated rules from the test data
	 */
	
	public tagTransformation tagTrans ;
	// dir is used to store all intermediate files
	public String Dir;
	
	public double minsup = 0.0;
	public double minconf = 0.2;
	public double minlift = 0.2;
	private AssocRules assocR;
	
	public AssoicatedRuleMining(String projectDir, double minsup, double minconf, double minlift){
		this.Dir = projectDir;
		this.minsup = minsup;
		this.minconf = minconf;
		this.minlift = minlift;
		this.RulesHashMap = new HashMap();
	}
	
	
	
	public void generateAssociatedRules(String dataFile){
		// generate the intermediate files
		this.tagTrans = new tagTransformation(dataFile);
		this.tagTrans.transform(dataFile, Dir+TransactionFile);
		this.tagTrans.outputMap(Dir+TagIdMapFile);
		
		// mine associated rules and output to a file
		try {
			this.run(Dir+TransactionFile, Dir+RulesOuputFile, this.minsup,this.minconf,this.minlift) ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	// 
	public void run(String input, String output,double minsupp, double minconf, double minlift) throws IOException, IOException{
		//String input = fileToPath("contextIGB.txt");
		//String output = ".//output.txt";
		
		// STEP 1: Applying the FP-GROWTH algorithm to find frequent itemsets
		
		AlgoFPGrowth fpgrowth = new AlgoFPGrowth();
		Itemsets patterns = fpgrowth.runAlgorithm(input, null, minsupp);
	//	patterns.printItemsets(database.size());
		int databaseSize = fpgrowth.getDatabaseSize();
		fpgrowth.printStats();
		
		// STEP 2: Generating all rules from the set of frequent itemsets (based on Agrawal & Srikant, 94)
		 
		myAlgoAgrawalFaster94 algoAgrawal = new myAlgoAgrawalFaster94();
		this.assocR = algoAgrawal.runAlgorithm(patterns, null, databaseSize, minconf, minlift);
		
		
		algoAgrawal.printStats();
		
		
		// store the rules in a map
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(output));
		
		for(AssocRule r : this.assocR.rules){
			int[] set1 = r.getItemset1();
			// we skip the target set whose size >1
			if(r.getItemset2().length >1)
				continue;
			String tags = "";
			ArrayList<String> tagList = new ArrayList();
			for(int tagId : set1){
				tagList.add(this.tagTrans.getId_text_map().get(tagId));
			}
			// sort tags in alphabetical, make the order unique
			Collections.sort(tagList);
			
			for(String tag : tagList)
				tags += tag+" ";
			tags = tags.trim();
			if(this.RulesHashMap.containsKey(tags)){
				this.RulesHashMap.get(tags).add(r);
			}else{
				ArrayList<AssocRule> list = new ArrayList();
				list.add(r);
				
				this.RulesHashMap.put(tags, list);
			}
			bw.write(tags+"->"+r.getItemset2()[0]+":"+r.getConfidence()+"\n");
			
		}
	bw.close();
	
	}


}
