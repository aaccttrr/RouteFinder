package application;

import java.util.ArrayList;

public class Settlement {

	private String placename;
	private ArrayList<Route> routes;

	public Settlement(String name){
		placename=name;
	}

	public String getPlacename() {
		return placename;
	}

	public Route[] getRoutes(){
		return (Route[]) routes.toArray();
	}

	public String toString(){
		return this.placename;
	}
}
