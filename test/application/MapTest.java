package application;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MapTest {

	@BeforeAll
	public static void setup(){
		InputOutput.readSettlements();
	}

	@AfterEach
	public void tearDown(){
		GraphNode<Settlement>[] waypoints = Map.getWaypoints().toArray(new GraphNode[0]);
		GraphNode<Settlement>[] exclusions = Map.getExclusions().toArray(new GraphNode[0]);
		for(GraphNode n:waypoints) Map.removeWaypoint(n);
		for(GraphNode n:exclusions) Map.removeExclusion(n);
	}

	@Test
	public void testFindOneValidPath(){
		Map.setMaxValidPaths(1);
		List<GraphNode<Settlement>> path = Map.findValidPaths(Map.lookupNode("King's Landing"), Map.lookupSettlement("Highgarden")).get(0);
		String[] expectedPath = {"Highgarden", "Cider Hall", "Longtable", "Grassy Vale", "Tumbleton", "King's Landing"};
		for(int i=0;i<expectedPath.length;i++){
			assertEquals(expectedPath[i], path.get(i).toString());
		}
	}

	@Test
	public void testFindMultipleValidPaths(){
		Map.setMaxValidPaths(3);
		List<List<GraphNode<Settlement>>> paths = Map.findValidPaths(Map.lookupNode("Lannisport"), Map.lookupSettlement("Highgarden"));
		String[][] expectedPaths = {{"Highgarden", "Old Oak", "Crakehall", "Lannisport"},
				{"Highgarden", "Goldengrove", "Red Lake", "Cornfield", "Lannisport"},
				{"Highgarden", "Old Oak", "Red Lake", "Cornfield", "Lannisport"}};
		for(int i=0;i<expectedPaths.length;i++){
			for(int j=0;j<expectedPaths[i].length;j++){
				assertEquals(expectedPaths[i][j], paths.get(i).get(j).toString());
			}
		}
	}

	@Test
	public void testFindMultipleValidPathsWithWaypoint(){
		Map.setMaxValidPaths(3);
		Map.addWaypoint(Map.lookupNode("Ashford"));
		List<List<GraphNode<Settlement>>> paths = Map.findValidPaths(Map.lookupNode("King's Landing"), Map.lookupSettlement("Highgarden"));
		String[][] expectedPaths = {{"Highgarden", "Nightsong", "Ashford", "Longtable", "Grassy Vale", "Tumbleton", "King's Landing"},
				{"Highgarden", "Cider Hall", "Ashford", "Longtable", "Grassy Vale", "Tumbleton", "King's Landing"},
				{"Highgarden", "Nightsong", "Ashford", "Longtable", "Bitterbridge", "Tumbleton", "King's Landing"}};
		for(int i=0;i<expectedPaths.length;i++){
			for(int j=0;j<expectedPaths[i].length;j++){
				assertEquals(expectedPaths[i][j], paths.get(i).get(j).toString());
			}
		}
	}

	@Test
	public void testFindMultipleValidPathsWithExclusion(){
		Map.setMaxValidPaths(3);
		Map.addExclusion(Map.lookupNode("Grassy Vale"));
		List<List<GraphNode<Settlement>>> paths = Map.findValidPaths(Map.lookupNode("King's Landing"), Map.lookupSettlement("Highgarden"));
		String[][] expectedPaths = {{"Highgarden", "Cider Hall", "Longtable", "Bitterbridge", "Tumbleton", "King's Landing"},
				{"Highgarden", "Nightsong", "Cider Hall", "Longtable", "Bitterbridge", "Tumbleton", "King's Landing"},
				{"Highgarden", "Goldengrove", "Cider Hall", "Longtable", "Bitterbridge", "Tumbleton", "King's Landing"}};
		for(int i=0;i<expectedPaths.length;i++){
			for(int j=0;j<expectedPaths[i].length;j++){
				assertEquals(expectedPaths[i][j], paths.get(i).get(j).toString());
			}
		}
	}

	@Test
	public void testFindMultipleValidPathsWithWaypointsAndExclusions(){
		Map.setMaxValidPaths(3);
		Map.addWaypoint(Map.lookupNode("Ashford"));
		Map.addExclusion(Map.lookupNode("Grassy Vale"));
		List<List<GraphNode<Settlement>>> paths = Map.findValidPaths(Map.lookupNode("King's Landing"), Map.lookupSettlement("Highgarden"));
		String[][] expectedPaths = {{"Highgarden", "Nightsong", "Ashford", "Longtable", "Bitterbridge", "Tumbleton", "King's Landing"},
				{"Highgarden", "Cider Hall", "Ashford", "Longtable", "Bitterbridge", "Tumbleton", "King's Landing"},
				{"Highgarden", "Nightsong", "Ashford", "Cider Hall", "Longtable", "Bitterbridge", "Tumbleton", "King's Landing"}};
		for(int i=0;i<expectedPaths.length;i++){
			for(int j=0;j<expectedPaths[i].length;j++){
				assertEquals(expectedPaths[i][j], paths.get(i).get(j).toString());
			}
		}
	}

	@Test
	public void testFindCheapestPath(){
		Map.setRoutePriority("distance");
		Settlement[] path = Map.findCheapestPath(Map.lookupNode("Lannisport"), Map.lookupSettlement("Sunspear"));
		String[] expectedPath = {"Lannisport", "Cornfield", "Red Lake", "Goldengrove", "Cider Hall", "Nightsong",
				"Tower of Joy", "Vulture's Roost", "Yronwood", "The Tor", "Ghost Hill", "Planky Town", "Sunspear"};
		for(int i=0;i<expectedPath.length;i++){
			assertEquals(expectedPath[i], path[i].toString());
		}
	}

	@Test
	public void testFindCheapestPathWithWaypoint(){
		Map.setRoutePriority("distance");
		Map.addWaypoint(Map.lookupNode("Blackhaven"));
		Settlement[] path = Map.findCheapestPath(Map.lookupNode("Lannisport"), Map.lookupSettlement("Sunspear"));
		String[] expectedPath = {"Lannisport", "Cornfield", "Red Lake", "Goldengrove", "Cider Hall", "Ashford",
				"Blackhaven", "Wyl", "Stonehelm", "Weeping Tower", "Ghost Hill", "Planky Town", "Sunspear"};
		for(int i=0;i<expectedPath.length;i++){
			assertEquals(expectedPath[i], path[i].toString());
		}
	}

	@Test
	public void testFindCheapestPathWithExclusion(){
		Map.setRoutePriority("distance");
		Map.addExclusion(Map.lookupNode("Vulture's Roost"));
		Settlement[] path = Map.findCheapestPath(Map.lookupNode("Lannisport"), Map.lookupSettlement("Sunspear"));
		String[] expectedPath = {"Lannisport", "Cornfield", "Red Lake", "Goldengrove", "Cider Hall", "Ashford",
				"Blackhaven", "Wyl", "Stonehelm", "Weeping Tower", "Ghost Hill", "Planky Town", "Sunspear"};
		for(int i=0;i<expectedPath.length;i++){
			assertEquals(expectedPath[i], path[i].toString());
		}
	}

	@Test
	public void testFindCheapestPathWithWaypointsAndExclusions(){
		Map.setRoutePriority("distance");
		Map.addWaypoint(Map.lookupNode("Nightsong"));
		Map.addExclusion(Map.lookupNode("Vulture's Roost"));
		Settlement[] path = Map.findCheapestPath(Map.lookupNode("Lannisport"), Map.lookupSettlement("Sunspear"));
		String[] expectedPath = {"Lannisport", "Cornfield", "Red Lake", "Goldengrove", "Cider Hall", "Nightsong",
				"Tower of Joy", "Kingsgrave", "Skyreach", "Yronwood", "The Tor", "Ghost Hill", "Planky Town", "Sunspear"};
		for(int i=0;i<expectedPath.length;i++){
			assertEquals(expectedPath[i], path[i].toString());
		}
	}
}