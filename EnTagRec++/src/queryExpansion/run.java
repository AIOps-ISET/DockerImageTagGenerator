package queryExpansion;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import Network.Graph;
import topicinfer.TopicInfer;


public class run {
	
	// define the number of inferred number return
	final static int infer_topK = 10;
	final static int extend_topK =15;
   /* public static void main(String[] args) {
    	String root = "D:\\Shaowei\\research\\Folksonomies\\stackoverflow\\TopicModel tool\\";
    	String trainModelpath = root + "llda-cvb0-c4d11853-771-a669dfd0-30f9b365";
    	String queryPath = root+ "query.csv";
    	String TopicInferredOutPath = root+ "query-out.csv";
    	// load query
    	ArrayList<Query> queryList = loadQueryFromFile(queryPath);
    	
    	
    	
    	// infer topic 
    	System.out.println("start inferring....");
        TopicInfer.getTopKTopic(trainModelpath, queryPath, TopicInferredOutPath, infer_topK);
        // dump into query
        loadInferredTopic(TopicInferredOutPath, queryList);
        
        // load the tag hierarchy tree
        String graphInput = "D:\\Shaowei\\research\\Folksonomies\\stackoverflow\\mergedStackOverFlowIndex50_graph.txt";
        		//"D:\\Shaowei\\research\\Folksonomies\\stackoverflow\\mergedStackOverFlowIndex50\\resultGraph.txt";
        
        Graph g = new Graph();
        g.createFromEdgeFile(graphInput);
        
        // query expansion
        
        for(Query q : queryList){
        	// 
        	System.out.println(q.getOriginalText()+"starting expansion!");
        	q.extendTagWithTagGraph(g, extend_topK );
        	System.out.println(q.print());
        	
        }
        
    }*/
    
    public static void loadInferredTopic(String topicInferredOutPath,
			ArrayList<Query> queryList) {
		// TODO Auto-generated method stub
    	try {
			BufferedReader br = new BufferedReader(new FileReader(topicInferredOutPath));
			String line = null;
			
			while((line = br.readLine())!=null){
				String[] sparts = line.split(",");
				Query q = queryList.get(Integer.parseInt(sparts[0]));
				for(int i =1; i < sparts.length; i++){
					String[] strs = sparts[i].split(":");
					q.addInfTags(strs[0],Double.parseDouble(strs[1]));
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
    
	public static ArrayList<Query> loadQueryFromFile(String path, String datapath){
    	ArrayList<Query> ql = new ArrayList();
    	HashMap<String, String> map = lineNum_queryid(datapath);
    	try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			String line = null;
			
			while((line = br.readLine())!=null){
				String[] spart = line.split(",");
				String queryText = spart[1];
				Query q = new Query(queryText);
				q.id = spart[0];
				q.query_id = map.get(q.id);
				ql.add(q);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return ql;
    }
    
	public static HashMap<String, String> lineNum_queryid (String path){
		HashMap<String, String> result = new HashMap();
		try{
		BufferedReader br = new BufferedReader(new FileReader(path));
		String line = null;
		int id = 0;
		while((line = br.readLine())!=null){
			String[] sparts  = line.split(",");
			
			String queryid = sparts[0];
			result.put(String.valueOf(id), queryid);
			
			id++;
		}
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}