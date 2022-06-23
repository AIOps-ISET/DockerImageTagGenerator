package TermTagIndex;

import java.util.HashMap;

public class Term {
	HashMap<String, Integer> tag_occur;
	private double term_IDF;
	String name;
	static int totalTag = 500; 
	
	public Term(String term){
		this.name = term;
		this.tag_occur = new HashMap();
	}
	
	public double getTerm_IDF(){
		if(this.term_IDF==0)
			this.term_IDF= Math.log10(this.totalTag/this.tag_occur.size());
		return this.term_IDF;
	}
	
	public HashMap<String, Integer> getTag_occur() {
		return tag_occur;
	}

	public void setTag_occur(HashMap<String, Integer> tag_occur) {
		this.tag_occur = tag_occur;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
