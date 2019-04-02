package application;

import java.lang.reflect.Array;

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

	public void increaseSize(){
		int newSize = (int) Math.sqrt(matrix.length);
		Route[][] newMatrix = new Route[newSize][newSize];
		for(int i=0;i<newSize-1;i++){
			newMatrix[i] = matrix[i];
		}
		matrix = newMatrix;
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
