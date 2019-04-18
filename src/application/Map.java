package application;

import javax.print.attribute.SetOfIntegerSyntax;
import java.util.LinkedList;
import java.util.Set;

public class Map {

	private static AdjacencyMatrix matrix;
	private static String routePriority;

	private static LinkedList<Settlement> waypoints = new LinkedList<>();
	private static LinkedList<Settlement> exclusions = new LinkedList<>();

	public static void initMap(int size){
		matrix = new AdjacencyMatrix(size);
	}

	public static void addSettlement(Settlement settlement){
		GraphNode<Settlement> node = matrix.addNode(settlement);
		/*for(String s:connections){
			node.connectToNode(matrix.lookupNodeByData(s));
		}*/
	}

	public static void addRoute(Settlement start, Settlement end, double distance, int difficulty, int danger){
		GraphNode<Settlement> startNode = (GraphNode<Settlement>) matrix.lookupNodeByData(start);
		GraphNode<Settlement> endNode = (GraphNode<Settlement>) matrix.lookupNodeByData(end);
		startNode.connectToNode(endNode, distance, difficulty, danger);
	}

	public static Settlement[] getSettlements(){
		GraphNode<?>[] nodes = matrix.getNodes();
		Settlement[] settlements = new Settlement[matrix.getNodeCount()];
		for(int i=0;i<settlements.length;i++){
			settlements[i] = (Settlement) nodes[i].getData();
		}
		return settlements;
	}

	public static Object[][] getRoutes(){
		LinkedList<Object[]> routeList = new LinkedList<>();
		for(int i=0;i<matrix.getNodeCount();i++){
			for(int j=i+1;j<matrix.getNodeCount();j++){
				if(matrix.getMatrix()[i][j]!=null){
					Object[] route = {matrix.getNodes()[i].getData(), matrix.getNodes()[j].getData(), matrix.getMatrix()[i][j]};
					routeList.add(route);
				}
			}
		}
		return routeList.toArray(new Object[routeList.size()][]);
	}

	public static Settlement lookupSettlement(String placename){
		GraphNode<Settlement>[] settlements = (GraphNode<Settlement>[])matrix.getNodes();
		for(GraphNode g:settlements){
			Settlement s = (Settlement) g.getData();
			if(s.getPlacename().equals(placename)){
				return s;
			}
		}
		return null;
	}

	public static boolean routeExists(Settlement start, Settlement end){
		GraphNode<Settlement> startNode = (GraphNode<Settlement>) matrix.lookupNodeByData(start);
		GraphNode<Settlement> endNode = (GraphNode<Settlement>) matrix.lookupNodeByData(end);
		return (matrix.getMatrix()[startNode.getNodeId()][endNode.getNodeId()]!=null);
	}

	public static void setRoutePriority(String routePriority) {
		Map.routePriority = routePriority;
	}

	public static void addWaypoint(Settlement settlement){
		waypoints.add(settlement);
	}

	public static LinkedList<Settlement> getWaypoints() {
		return waypoints;
	}

	public static void addExclusion(Settlement settlement){
		exclusions.add(settlement);
	}

	public static LinkedList<Settlement> getExclusions() {
		return exclusions;
	}
}
