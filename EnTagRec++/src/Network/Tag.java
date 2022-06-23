package Network;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class Tag {
	private String name;
	private ArrayList<String> doclist;
	private String wordSet;
	private HashMap<String, String> docMap;
	private ArrayList<Date> createdDates;
	public Tag(String name, ArrayList<String> doclist)
	{
		this.name = name;
		this.doclist = doclist;
	}
	public Tag(String name,HashMap<String, String> docMap)
	{
		this.name = name;
		this.docMap = docMap;
	}
	

	public HashMap<String, String> getDocMap() {
		return docMap;
	}
	public void setDocMap(HashMap<String, String> docMap) {
		this.docMap = docMap;
	}
	public String getWordSet() {
		return wordSet;
	}



	public void setWordSet(String wordSet) {
		this.wordSet = wordSet;
	}



	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<String> getDoclist() {
		return doclist;
	}

	public void setDoclist(ArrayList<String> doclist) {
		this.doclist = doclist;
	}
}
