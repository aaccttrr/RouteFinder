package application;

import java.util.LinkedList;
import java.util.Set;

public class Map {

	private static AdjacencyMatrix matrix;
	private static Route[][] routes;
	private static String routePriority;

	private static LinkedList<Settlement> waypoints;

	public static void initMap(int size){
		matrix = new AdjacencyMatrix(size);
		waypoints = new LinkedList<>();
	}

	public static void addSettlement(Settlement settlement){
		GraphNode<Settlement> node = matrix.addNode(settlement);
		/*for(String s:connections){
			node.connectToNode(matrix.lookupNodeByData(s));
		}*/
	}

	public static Settlement[] getSettlements(){
		GraphNode<?>[] nodes = matrix.getNodes();
		Settlement[] settlements = new Settlement[matrix.getNodeCount()];
		for(int i=0;i<settlements.length;i++){
			settlements[i] = (Settlement) nodes[i].getData();
		}
		return settlements;
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

	public static void setRoutePriority(String routePriority) {
		Map.routePriority = routePriority;
	}

	public static void addWaypoint(Settlement settlement){
		waypoints.add(settlement);
	}

	public static LinkedList<Settlement> getWaypoints() {
		return waypoints;
	}
}
