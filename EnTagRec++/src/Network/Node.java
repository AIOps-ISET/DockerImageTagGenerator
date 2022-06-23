package Network;

import java.util.ArrayList;



public class Node {
	static String OUT= "outgoing";
	static String IN = "incoming";
	private String name;
	private double Weight;
	private String wordSet;
	public boolean flag;
	private ArrayList<Node> incomeNodes;
	public ArrayList<Node> getIncomeNodes() {
		return incomeNodes;
	}
	public Node clone(){
		return new Node(this.name, this.Weight);
	}
	public void setIncomeNodes(ArrayList<Node> incomeNodes) {
		this.incomeNodes = incomeNodes;
	}

	public ArrayList<Node> getOutgoNodes() {
		return outgoNodes;
	}

	public void setOutgoNodes(ArrayList<Node> outgoNodes) {
		this.outgoNodes = outgoNodes;
	}



	private ArrayList<Node> outgoNodes;
	
	public Node(String name,double weight){
		this.name = name;
		this.Weight = weight;
		this.incomeNodes = new ArrayList(); 
		this.outgoNodes = new ArrayList();
		this.flag = false;
	}
	
	public void addNode(Node n, String type){
		if(type.equals(this.OUT))
			this.outgoNodes.add(n);
		else
			this.incomeNodes.add(n);
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


	public double getWeight() {
		return Weight;
	}



	public void setWeight(double weight) {
		Weight = weight;
	}


}
