package application;


public class GraphNode<T> {

	private T data;
	private AdjacencyMatrix matrix;
	private int nodeId;
	private int nodeValue=Integer.MAX_VALUE;

	public GraphNode(T data, AdjacencyMatrix matrix){
		this.data = data;
		this.matrix = matrix;
		if((nodeId=matrix.getNodeCount())==matrix.getNodes().length){
			matrix.increaseSize();
		}
		matrix.getNodes()[nodeId]=this;
		matrix.setNodeCount(matrix.getNodeCount()+1);
	}

	public void connectToNode(GraphNode<T> destNode, int dist, int diff, int danger){
		matrix.getMatrix()[nodeId][destNode.nodeId] = matrix.getMatrix()[destNode.nodeId][nodeId] = new Route(dist, diff, danger);
	}

	public T getData(){
		return data;
	}

	public int getNodeId(){
		return nodeId;
	}

	public int getNodeValue(){
		return nodeValue;
	}

	public void setNodeValue(int nodeValue){
		this.nodeValue=nodeValue;
	}

	@Override
	public String toString() { //for displaying data in list view
		return data.toString();
	}
}
