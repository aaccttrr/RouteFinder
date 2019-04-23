package application;


public class Settlement {

	private String placename;

	private double XPos, YPos;

	public Settlement(String name, double x, double y){
		placename=name;
		XPos = x;
		YPos = y;
	}

	public String getPlacename() {
		return placename;
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
