package TagInferBasedonUser;

import java.util.ArrayList;
import java.util.HashMap;

import org.preprocess.QuestionInformationManager;

import queryExpansion.Query;
import tagRecommend.Doc;

public class InferBasedOnUser {
	public QuestionInformationManager qiManager;
	
	private String projctDir;
	public InferBasedOnUser(String projectDir){
		this.projctDir = projectDir;
		//String projectDir = "F:\\shaowei\\research\\tag_recommendation\\folksonomy\\AlltheFourDataset\\appleSource\\";
		String postFile = projectDir + "posts.xml";
		String metaFile = projectDir + "meta.txt";
		String userInfoFile = projectDir + "user_info.txt";
		int readLineN = 1000000;
		//QuestionInformationManager fac = new QuestionInformationManager();
		//fac.questionId_docId_map = fac.getRowIdToFileMap(metaFile);
		
		qiManager = new QuestionInformationManager();
		try{
			if(projectDir.contains("freecode")){
				this.qiManager.getContentAndTag(userInfoFile);
			}else{
				this.qiManager.getRowIdToFileMap(metaFile);
				this.qiManager.getContentAndTag(postFile, projectDir, readLineN);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void infer(ArrayList<Query> queryListForTraining, ArrayList<Query> queryListForTesting, HashMap<String,Doc> goldenSet){
		HashMap<String, ArrayList<Integer>> user_query_index_on_training = this.buildeIndexFromUserToQuestion(queryListForTraining);
		for(Query q : queryListForTesting ){
			inferForQ(q, user_query_index_on_training, queryListForTraining,goldenSet);
		}
	}
	
	
	public void inferForQ(Query q,
			HashMap<String, ArrayList<Integer>> user_query_index_on_training,
			ArrayList<Query> listForTraining, HashMap<String,Doc> goldenSet) {
		// TODO Auto-generated method stub
		// get the tags of userid in training data
		HashMap<String,Integer> tag_count = new HashMap();
		
		int sum = 0;
		String userId = this.qiManager.getDocId_User_Map().get(q.query_id);
		if(userId ==null)
			return;
		ArrayList<Integer> queryIds = user_query_index_on_training.get(userId);
		if(queryIds ==null)
			return;
		for(int id : queryIds){
			Query q_Training = listForTraining.get(id);
			ArrayList<String> tags = goldenSet.get(q_Training.query_id).getTaglist();
			for(String tag : tags){
				sum +=1;
				if(tag_count.containsKey(tag)){
					tag_count.put(tag, tag_count.get(tag)+1);
				}else{
					tag_count.put(tag, 1);
				}
			}
			
		}
		// calculate the score for each tag
		
		// only when the tag appear in top k of other two tag candidates set
		
	    // get the top k from other two candidate sets
		int k=600;
		ArrayList<String> k_tags_from_other_two_list = q.getTopk(k);
		//#tag/#total tag
		for(String tag : tag_count.keySet())
		{
			if(k_tags_from_other_two_list.contains(tag))
				q.addUserTags(tag, (double)tag_count.get(tag)/(double)sum);
		}
		
	}

	public HashMap<String, ArrayList<Integer>> buildeIndexFromUserToQuestion(ArrayList<Query> queryList){
		HashMap<String, ArrayList<Integer>> index  = new HashMap();
		
		for(int i = 0; i < queryList.size();i++){
			String userId = this.qiManager.getDocId_User_Map().get(queryList.get(i).query_id);
			if(userId == null)
				continue;
			if(index.containsKey(userId)){
				index.get(userId).add(i);
			}else{
				ArrayList<Integer> list = new ArrayList();
				list.add(i);
				index.put(userId, list);
			}
		}
		
		return index;
		
	}
	
}
