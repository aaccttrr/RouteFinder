package application;

import java.lang.reflect.Array;
import java.util.Arrays;

public class AdjacencyMatrix {

	private Route[][] matrix;
	private GraphNode<?>[] nodes;

	private int nodeCount=0;

	public AdjacencyMatrix(int size){
		matrix = new Route[size][size];
		nodes = (GraphNode<?>[]) Array.newInstance(GraphNode.class, size);
	}

	public <T> GraphNode<T> addNode(T data){
		return new GraphNode<>(data,this);
	}

	public void increaseSize(){ //increase matrix size as number of nodes increase
		nodes = Arrays.copyOf(nodes, nodes.length+1);
		Route[][] newMatrix = new Route[matrix.length+1][matrix.length+1];
		for(int i=0;i<matrix.length;i++){
			newMatrix[i] = Arrays.copyOf(matrix[i], matrix.length+1);
		}
		matrix = newMatrix;
	}

	public GraphNode<?> lookupNodeByData(Object data){
		for(GraphNode<?> node : nodes) if(node.getData().equals(data)) return node;
		return null;
	}

	public GraphNode<?>[] getNodes() {
		return nodes;
	}

	public int getNodeCount() {
		return nodeCount;
	}

	public void setNodeCount(int nodeCount) {
		this.nodeCount = nodeCount;
	}

	public Route[][] getMatrix() {
		return matrix;
	}
}
