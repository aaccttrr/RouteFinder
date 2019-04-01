package application;

import java.util.ArrayList;

public class Settlement {

	private String placename;

	private double XPos, YPos;

	private ArrayList<Route> routes;

	public Settlement(String name, double x, double y){
		placename=name;
		XPos = x;
		YPos = y;
	}

	public String getPlacename() {
		return placename;
	}

	public Route[] getRoutes(){
		return (Route[]) routes.toArray();
	}
	public double getXPos() {
		return XPos;
	}

	public double getYPos() {
		return YPos;
	}

	public String toString(){
		return this.placename;
	}
}
