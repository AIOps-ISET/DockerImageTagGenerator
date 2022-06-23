package evaluate_EnTagRec_U;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import queryExpansion.Query;
import Network.Node;

public class IOManager {
	// output the querylist to a file specified in path
	public static void writeToLogFile(String file,
			ArrayList<Query> queryListForTesting, int topK) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
			for (Query q : queryListForTesting) {
				bw.write(q.query_id + "\n");
				bw.write("true tags:");
				int count = 0;
				for (String tag : q.trueTags) {
					bw.write(tag + "\t");
					count++;
					if (count > topK)
						break;
				}
				bw.write("\n");
				bw.write("termTagInfer tags:");
				count = 0;
				
				for (Node n : q.termTagInferTags.values()) {
					bw.write(n.getName() + ":" + n.getWeight() + "\t");
					count++;
					if (count > topK)
						break;
				}
				bw.write("\n");

				bw.write("inferedTagsFromLLDA tags:");
				count = 0;
				for (Node n : q.inferedTagsFromLLDA.values()) {
					bw.write(n.getName() + ":" + n.getWeight() + "\t");
					count++;
					if (count > topK)
						break;
				}
				bw.write("\n");
				bw.write("inferedTagsFromUser tags:");
				count = 0;
				for (Node n : q.inferedTagsFromUser.values()) {
					bw.write(n.getName() + ":" + n.getWeight() + "\t");
					count++;
					if (count > topK)
						break;
				}
				bw.write("\n");

				bw.write("inferedTagsFromAsscoiatedRule tags:");
				count = 0;
				for (Node n : q.inferedTagsFromAsscoiatedRule.values()) {
					bw.write(n.getName() + ":" + n.getWeight() + "\t");
					count++;
					if (count > topK)
						break;
				}
				bw.write("\n");

			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	static public void outputHashMap(HashMap<String, Double> map, String output){
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(output));
			for(String key : map.keySet()){
				bw.write(key +","+map.get(key)+"\n");
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
