package getAssociatePattern;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
public class tagTransformation {

	/**
	 * @param args
	 */
	
	
	private HashMap<String, Integer> text_id_map;
	public HashMap<String, Integer> getText_id_map() {
		return text_id_map;
	}
	public void setText_id_map(HashMap<String, Integer> text_id_map) {
		this.text_id_map = text_id_map;
	}
	public HashMap<Integer, String> getId_text_map() {
		return id_text_map;
	}
	public void setId_text_map(HashMap<Integer, String> id_text_map) {
		this.id_text_map = id_text_map;
	}


	private HashMap<Integer,String> id_text_map;
	// load all tags from a dataset.cvs file
	public tagTransformation(String dataFile){
		createMap(dataFile);
	}
	public void outputMap(String outputFile){
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
			for(String tag : this.text_id_map.keySet()){
				bw.write(tag+","+this.text_id_map.get(tag)+"\n");
			}
			bw.close();
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void transform(String inputFile, String outputFile){
		try{
			System.out.println("begin transform");
			BufferedReader br = new BufferedReader(new FileReader(inputFile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
			String line = null;
			while(br.ready()){
				line = br.readLine();
				String tagStr= line.split(",")[1];
				String[] tags = tagStr.trim().split(" +");
				StringBuilder sb = new StringBuilder();
				for(String tag : tags){
					sb.append(this.text_id_map.get(tag)+" ");
				}
				bw.write(sb.toString().trim()+"\n");
			}
			br.close();
			bw.close();
			System.out.println("finish transform");
		}catch(Exception e){
			e.printStackTrace();
			
		}
	}
	
	
	private void createMap(String dataFile){
		this.text_id_map = new HashMap();
		this.id_text_map = new HashMap();
		int id= 1;
		try{
			System.out.println("loading file");
			BufferedReader br = new BufferedReader(new FileReader(dataFile));
			String line = null;
			while(br.ready()){
				line = br.readLine();
				String tagStr= line.split(",")[1];
				String[] tags = tagStr.trim().split(" +");
				for(String tag : tags){
					if(!this.text_id_map.containsKey(tag)){
						this.text_id_map.put(tag, id);
						this.id_text_map.put(id, tag);
						id++;
					}
				}
			}
			System.out.println("finish loading");
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
