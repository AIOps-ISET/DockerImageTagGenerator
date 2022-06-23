package Network;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class Graph {
	private HashMap<String, Node> index;
	private HashMap<String, Edge> edgeList;
	// define the pluse range,  PLUSESTEP from the starting node
	private int PLUSE_STEP = 1;
	private boolean pluseValue = true;
	public Graph(int pluseV) {
		this.index = new HashMap();
		this.edgeList = new HashMap();
		this.PLUSE_STEP = pluseV;
	}
	public Graph() {
		this.index = new HashMap();
		this.edgeList = new HashMap();
		
	}

	public void addEdge(String start, String end, double score) {
		Node startNode = index.get(start);
		Node endNode = index.get(end);
		// update incoming and outgoing nodes
		startNode.addNode(endNode, Node.OUT);
		endNode.addNode(startNode, Node.IN);
		String key = startNode.getName() + "->" + endNode.getName();
		if (!this.edgeList.containsKey(key)) {
			Edge e = new Edge(startNode.getName(), endNode.getName());
			e.setWeight(score);
			this.edgeList.put(key, e);
		}

	}

	public void addNode(String tagName) {
		if (!this.index.containsKey(tagName)) {
			Node n = new Node(tagName, 0);
			index.put(tagName, n);
		}
	}

	public void createFromEdgeFile(String path) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			String line = null;
			while ((line = br.readLine()) != null) {
				String[] sparts = line.split("\t");
				String startNode = sparts[0];
				String endNode = sparts[1];
				double weight = Double.parseDouble(sparts[2]);
				addNode(startNode);
				addNode(endNode);
				addEdge(startNode, endNode, weight);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Graph clone() {
		Graph g = new Graph();

		for (String key : this.edgeList.keySet()) {
			Edge e = this.edgeList.get(key);
			String startNode = e.getStart();
			String endNode = e.getEnd();
			double weight = e.getWeight();
			g.addNode(startNode);
			g.addNode(endNode);
			g.addEdge(startNode, endNode, weight);
		}

		return g;
	}

	public void destory() {
		this.index.clear();
		this.edgeList.clear();
		System.gc();
	}
	
	// pluse from the initial node
	public void pluse(Node currentNode, int pluse_step) {
		// return if satisfy 
		if(!this.index.containsKey(currentNode.getName()) || pluse_step > this.PLUSE_STEP)
			return;
		// update the current node in graph
		Node nodeInIndex = this.index.get(currentNode.getName());
		// skip the node vist before  to avoid flood
		if(currentNode.getWeight() > nodeInIndex.getWeight()){
			nodeInIndex.setWeight(currentNode.getWeight());
			
		}
		/*if(currentNode.getWeight() > 0)
		{
			double newWeight = currentNode.getWeight() + nodeInIndex.getWeight();
			nodeInIndex.setWeight(newWeight);
		}*/
		// pluse to the outgoing nodes
		ArrayList<Node> outgoingNodes = nodeInIndex.getOutgoNodes();
		for(int i = 0; i < outgoingNodes.size();i++){
			Node outgoingN = outgoingNodes.get(i);
			String edgeKey = nodeInIndex.getName()+"->"+outgoingN.getName();
			double edgeWeight = 0.9;
			if(this.pluseValue)
				edgeWeight = this.edgeList.get(edgeKey).getWeight();
			double cloneN_Weight = edgeWeight * nodeInIndex.getWeight();
			//stop
			if(cloneN_Weight == 0)
				return;
			// keep plusing
			else{
				//clone a node from outgoingN
				Node cloneN = new Node(outgoingN.getName(),cloneN_Weight );
				pluse(cloneN, pluse_step+1);
			}
		}
		
		// pluse to the incoming nodes
		ArrayList<Node> incomingNodes = nodeInIndex.getIncomeNodes();
		for(int i = 0; i < incomingNodes.size();i++){
			Node incomingN = incomingNodes.get(i);
			String edgeKey = incomingN.getName() +"->" + nodeInIndex.getName();
			double edgeWeight = 0.9;
			if(this.pluseValue)
				edgeWeight = this.edgeList.get(edgeKey).getWeight();
			double cloneN_Weight = edgeWeight * nodeInIndex.getWeight();
			//stop
			
			if(cloneN_Weight == 0)
				return;
			// keep plusing
			else{
				//clone a node from outgoingN
				Node cloneN = new Node(incomingN.getName(),cloneN_Weight );
				pluse(cloneN, pluse_step+1);
			}
		}
	}
	
	public ArrayList<Node> getTopKTags(int topK){
	//	for(Node n : this.index.values())
	//		System.out.println(n.getName() + ":" +n.getWeight());
		try{
		compareNode comparator = new compareNode();
		ArrayList<Node> orlist = new ArrayList( this.index.values());
		ArrayList<Node> list = new ArrayList();
		for(Node n :orlist)
		{
			list.add(n.clone());
		}
		Collections.sort(list, comparator); 
		if(topK<list.size())
			return new ArrayList(list.subList(0, topK));
		else
			return new ArrayList(list);
		}catch(Exception e){
			e.printStackTrace();
			return new ArrayList();
		}
	
		
	}
	
	public static ArrayList<Node> getTopKTags(int topK, ArrayList<Node> list){
		//	for(Node n : this.index.values())
		//		System.out.println(n.getName() + ":" +n.getWeight());
		try{
			compareNode comparator = new compareNode();
			//ArrayList<Node> list = new ArrayList( this.index.values());
			Collections.sort(list, comparator); 
			if(list.size() >topK)
				return new ArrayList(list.subList(0, topK));
			else
				return list;
			}catch(Exception e){
				e.printStackTrace();
				return new ArrayList();
			}
		}
	
	public void cleanNodeWeight() {
		// TODO Auto-generated method stub
		for(String key : this.index.keySet()){
			this.index.get(key).setWeight(0);
		}
	}
	

}
