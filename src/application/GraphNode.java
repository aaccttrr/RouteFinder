package application;

import java.util.Arrays;

public class GraphNode<T> {

	private T data;
	private AdjacencyMatrix matrix;
	private int nodeId;

	public GraphNode(T data, AdjacencyMatrix matrix){
		this.data = data;
		this.matrix = matrix;
		matrix.getNodes()[nodeId=matrix.getNodeCount()]=this;
		matrix.setNodeCount(matrix.getNodeCount()+1);
	}

	public void connectToNode(GraphNode<T> destNode, int dist, int diff, int danger){
		matrix.getMatrix()[nodeId][destNode.nodeId]=matrix.getMatrix()[destNode.nodeId][nodeId]=new Route(dist,diff,danger);
	}

	public GraphNode[] getConnections(){
		Route[] dests = matrix.getMatrix()[nodeId];
		GraphNode[] connections = new GraphNode[dests.length];
		GraphNode[] nodes = matrix.getNodes();
		int pos=0;
		for(int i=0;i< dests.length;i++){
			if(dests[i]!=null){
				connections[pos++]=nodes[i];
			}
		}
		return Arrays.copyOf(connections,pos);
	}

	public T getData(){
		return data;
	}
}
