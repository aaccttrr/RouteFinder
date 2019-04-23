package application;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;

public class InputOutput {

	public static void readSettlements(){
		try {
			BufferedReader br = new BufferedReader(new FileReader("src/data/settlements.csv"));
			String line;
			LinkedList<Settlement> settlements = new LinkedList<>();
			br.readLine();
			while ((line = br.readLine()) != null){
				String[] data = line.split(",");
				settlements.add(new Settlement(data[0],Double.parseDouble(data[1]),Double.parseDouble(data[2])));
			}
			Map.initMap(settlements.size());
			for(Settlement s:settlements){
				Map.addSettlement(s);
			}
			readRoutes();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeSettlements(){
		try(PrintWriter pw = new PrintWriter(new File("src/data/settlements.csv"))){
			Settlement[] settlements = Map.getSettlements();
			StringBuilder sb = new StringBuilder();
			sb.append("placename,");
			sb.append("XPos,");
			sb.append("YPos");
			sb.append('\n');
			for(Settlement s:settlements){
				sb.append(s.getPlacename());
				sb.append(',');
				sb.append(s.getXPos());
				sb.append(',');
				sb.append(s.getYPos());
				sb.append('\n');
			}
			pw.write(sb.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void readRoutes(){
		try {
			BufferedReader br = new BufferedReader(new FileReader("src/data/routes.csv"));
			String line;
			br.readLine();
			while ((line = br.readLine()) != null){
				String[] data = line.split(",");
				Map.addRoute(Map.lookupSettlement(data[0]), Map.lookupSettlement(data[1]),
						Integer.parseInt(data[2]),Integer.parseInt(data[3]),Integer.parseInt(data[4]));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		readExclusions();
	}

	public static void writeRoutes(){
		try(PrintWriter pw = new PrintWriter(new File("src/data/routes.csv"))){
			Object[][] routes = Map.getRoutes();
			StringBuilder sb = new StringBuilder();
			sb.append("start,");
			sb.append("end,");
			sb.append("distance,");
			sb.append("difficulty,");
			sb.append("danger");
			sb.append('\n');
			for(Object[] r:routes){
				sb.append(((Settlement)r[0]).getPlacename());
				sb.append(',');
				sb.append(((Settlement)r[1]).getPlacename());
				sb.append(',');
				sb.append(((Route)r[2]).getDistance());
				sb.append(',');
				sb.append(((Route)r[2]).getDifficulty());
				sb.append(',');
				sb.append(((Route)r[2]).getDanger());
				sb.append('\n');
			}
			pw.write(sb.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void readExclusions(){
		try {
			BufferedReader br = new BufferedReader(new FileReader("src/data/exclusions.csv"));
			String line;
			br.readLine();
			while ((line = br.readLine()) != null){
				String[] data = line.split(",");
				Map.addExclusion(Map.lookupNode(data[0]));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeExclusions(){
		try(PrintWriter pw = new PrintWriter(new File("src/data/exclusions.csv"))){
			ArrayList<GraphNode<Settlement>> exclusions = Map.getExclusions();
			StringBuilder sb = new StringBuilder();
			sb.append("placename");
			sb.append('\n');
			for(GraphNode<Settlement> n:exclusions){
				sb.append(n.toString());
				sb.append('\n');
			}
			pw.write(sb.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
