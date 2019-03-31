package application;

import java.io.*;
import java.util.LinkedList;

public class InputOutput {

	public static void readSettlements(){
		try {
			BufferedReader br = new BufferedReader(new FileReader("src/settlements.csv"));
			String line;
			LinkedList<Settlement> settlements = new LinkedList<>();
			while ((line = br.readLine()) != null){
				settlements.add(new Settlement(line));
			}
			Map.initMap(settlements.size());
			for(Settlement s:settlements){
				Map.addSettlement(s);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeSettlements(){

	}

	public static void readRoutes(){

	}

	public static void writeRoutes(){
		try {
			PrintWriter pw = new PrintWriter(new File("src/routes.csv"));
			StringBuilder sb = new StringBuilder();
			sb.append("start,");
			sb.append(',');
			sb.append("end,");
			sb.append(',');
			sb.append("distance,");
			sb.append(',');
			sb.append("difficulty,");
			sb.append(',');
			sb.append("danger");
			sb.append('\n');

			pw.write(sb.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
