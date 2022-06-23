package tagRecommend;

import java.util.ArrayList;

import Network.Node;

public class Doc extends  Node{
	
	private String tags;
	private ArrayList<String> taglist;
	public Doc(String id, String tags, String content) {
		super(id, 0);
		this.tags = tags;
		this.setWordSet(content);
	}
	public Doc(String id){
		super(id, 0);
		
	}
	public ArrayList<String> getTaglist() {
		return taglist;
	}
	public void setTaglist(ArrayList<String> taglist) {
		this.taglist = taglist;
	}
	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}



	/**
	 * @param args
	 */


}
