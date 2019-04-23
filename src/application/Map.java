package application;

import java.util.*;

public class Map {

	private static AdjacencyMatrix matrix;
	private static String routePriority;

	private static int maxValidPaths = 25;

	private static ArrayList<GraphNode<Settlement>> waypoints = new ArrayList<>(), exclusions = new ArrayList<>();

	public static void initMap(int size){
		matrix = new AdjacencyMatrix(size);
	}

	public static void addSettlement(Settlement settlement){
		matrix.addNode(settlement);
	}

	public static void addRoute(Settlement start, Settlement end, int distance, int difficulty, int danger){
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

	public static Object[][] getRoutes(){ //returns an array of routeInfo arrays
		LinkedList<Object[]> routeList = new LinkedList<>();
		for(int i=0;i<matrix.getNodeCount();i++){
			for(int j=i+1;j<matrix.getNodeCount();j++){
				if(matrix.getMatrix()[i][j]!=null){
					Object[] routeInfo = {matrix.getNodes()[i].getData(), matrix.getNodes()[j].getData(), matrix.getMatrix()[i][j]}; //routeInfo={start, end, routeObject}
					routeList.add(routeInfo);
				}
			}
		}
		return routeList.toArray(new Object[routeList.size()][]);
	}

	public static Settlement lookupSettlement(String placename){
		for(GraphNode n:matrix.getNodes()){
			if(((Settlement)n.getData()).getPlacename().equals(placename)){
				return (Settlement)n.getData();
			}
		}
		return null;
	}

	public static GraphNode<Settlement> lookupNode(String placename){
		for(GraphNode n:matrix.getNodes()){
			if(((Settlement)n.getData()).getPlacename().equals(placename)){
				return n;
			}
		}
		return null;
	}

	public static Route getRoute(Settlement start, Settlement end){
		GraphNode<Settlement> startNode = (GraphNode<Settlement>) matrix.lookupNodeByData(start);
		GraphNode<Settlement> endNode = (GraphNode<Settlement>) matrix.lookupNodeByData(end);
		return matrix.getMatrix()[startNode.getNodeId()][endNode.getNodeId()];
	}

	public static List<List<GraphNode<Settlement>>> findValidPaths(GraphNode<Settlement> startNode, Settlement lookingFor){ //interface for findValidPaths
		List<List<GraphNode<Settlement>>> agenda=new ArrayList<>(), agendaEncountered=new ArrayList<>(), resultPaths=new ArrayList<>();
		List<GraphNode<Settlement>> firstAgendaPath=new ArrayList<>(), firstAgendaEncountered=new ArrayList<>(exclusions);
		firstAgendaPath.add(startNode);
		firstAgendaEncountered.add(startNode);
		agenda.add(firstAgendaPath);
		agendaEncountered.add(firstAgendaEncountered);
		resultPaths= findValidPaths(agenda, agendaEncountered, lookingFor, resultPaths);
		return resultPaths;
	}

	public static List<List<GraphNode<Settlement>>> findValidPaths(List<List<GraphNode<Settlement>>> agenda,
																   List<List<GraphNode<Settlement>>> agendaEncountered, //a list of independent encounter lists for each partial path
																   Settlement lookingFor, List<List<GraphNode<Settlement>>> results){ //find an amount of valid paths <= maxValidPaths using breadth-first search
		if(agenda.isEmpty()) return results;
		if(agenda.size()>5000) return results; //to prevent stack overflow
		List<GraphNode<Settlement>> nextPath=agenda.remove(0);
		GraphNode<Settlement> currentNode=nextPath.get(0);
		List<GraphNode<Settlement>> encountered = agendaEncountered.remove(0); //get the corresponding encountered list for the next path
		if(currentNode.getData().equals(lookingFor) && nextPath.containsAll(waypoints)){
			results.add(nextPath);
			if(agenda.isEmpty() || results.size()==maxValidPaths) return results;
		}
		encountered.add(currentNode);
		for(int i=0;i<matrix.getNodeCount();i++){
			Route r = matrix.getMatrix()[currentNode.getNodeId()][i];
			if(r!=null){
				GraphNode<Settlement> node = (GraphNode<Settlement>) matrix.getNodes()[i];
				if(!encountered.contains(node)){
					List<GraphNode<Settlement>> newPath = new ArrayList<>(nextPath);
					List<GraphNode<Settlement>> newEncountered = new ArrayList<>(encountered);
					newPath.add(0,node);
					agenda.add(newPath);
					agendaEncountered.add(newEncountered);
				}
			}
		}
		return findValidPaths(agenda, agendaEncountered, lookingFor, results);
	}

	public static Settlement[] findCheapestPath(GraphNode<Settlement> startNode, Settlement lookingFor){
		ArrayList<GraphNode<Settlement>> waypoints = new ArrayList<>(Map.waypoints); //a checklist of waypoints to hit
		LinkedList<Settlement> path = new LinkedList<>();
		List<GraphNode<Settlement>> encountered = new ArrayList<>(exclusions), unencountered = new ArrayList<>();
		startNode.setNodeValue(0);
		unencountered.add(startNode);
		GraphNode<Settlement> currentNode;
		do{
			currentNode=unencountered.remove(0);
			encountered.add(currentNode);
			if(waypoints.contains(currentNode) || (waypoints.isEmpty() && currentNode.getData().equals(lookingFor))){ //found a waypoint or (all waypoints found and found lookingFor)
				GraphNode<Settlement> waypoint = currentNode; //reference to current node may be needed if waypoints remain
				int insertionPoint = path.size(); //point to insert settlements after any previous such that path is correctly built
				path.add(currentNode.getData());
				while(currentNode!=startNode){
					for(int i=0;i<matrix.getNodeCount();i++){
						Route r = matrix.getMatrix()[currentNode.getNodeId()][i];
						if(r!=null){
							GraphNode<Settlement> node = (GraphNode<Settlement>) matrix.getNodes()[i];
							if(node.getNodeValue()==currentNode.getNodeValue()-getRouteWeight(r)){
								int lastPrev = (insertionPoint>0) ? insertionPoint-1 : 0; //check that the last settlement on the previous path, if it exists, is not being repeated
								if (!path.get(lastPrev).equals(node.getData())) path.add(insertionPoint, node.getData());
								currentNode = node;
								break;
							}
						}
					}
				}
				for (GraphNode n : encountered) n.setNodeValue(Integer.MAX_VALUE);
				for (GraphNode n : unencountered) n.setNodeValue(Integer.MAX_VALUE);
				if(waypoints.isEmpty()){
					return path.toArray(new Settlement[0]);
				}
				else {
					encountered.clear(); //clear lists for new paths
					unencountered.clear();
					encountered.add(waypoint);
					waypoints.remove(waypoint); //remove waypoint from checklist
					currentNode = startNode = waypoint;
				}
			}
			for(int i=0;i<matrix.getNodeCount();i++){
				Route r = matrix.getMatrix()[currentNode.getNodeId()][i];
				if(r!=null){
					GraphNode<Settlement> node = (GraphNode<Settlement>) matrix.getNodes()[i];
					if(!encountered.contains(node)){
						node.setNodeValue(Integer.min(node.getNodeValue(), currentNode.getNodeValue()+getRouteWeight(r)));
						unencountered.add(node);
					}
				}
			}
			unencountered.sort(Comparator.comparingInt(GraphNode::getNodeValue));
		}while(!unencountered.isEmpty());
		return null;
	}

	private static int getRouteWeight(Route route){
		switch(routePriority){
			case "distance": return route.getDistance();
			case "difficulty": return route.getDifficulty();
			case "danger": return route.getDanger();
		}
		return -1;
	}

	public static void setRoutePriority(String routePriority) {
		Map.routePriority = routePriority;
	}

	public static void addWaypoint(GraphNode<Settlement> node){
		waypoints.add(node);
	}

	public static ArrayList<GraphNode<Settlement>> getWaypoints() {
		return waypoints;
	}

	public static void removeWaypoint(GraphNode<Settlement> node){
		waypoints.remove(node);
	}

	public static void addExclusion(GraphNode<Settlement> node){
		exclusions.add(node);
	}

	public static ArrayList<GraphNode<Settlement>> getExclusions() {
		return exclusions;
	}

	public static void removeExclusion(GraphNode<Settlement> node){
		exclusions.remove(node);
	}

	public static int getNumSettlements(){
		return matrix.getNodeCount();
	}

	public static void setMaxValidPaths(int maxValidPaths) {
		Map.maxValidPaths = maxValidPaths;
	}
}
