package Network;

import java.util.Comparator;

public class compareNode implements  Comparator<Node>{

	@Override
	public int compare(Node n0, Node n1) {
		if(n0.getWeight() < n1.getWeight())
			return 1;
		else if(n0.getWeight() == n1.getWeight())
			return 0;
		else
			return -1;
	}

}
