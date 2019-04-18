package application;

public class Route {

	private double distance; //km
	private int difficulty; //1-10
	private int danger; //1-10

	public Route(double dist, int diff, int danger){
		distance=dist;
		difficulty=diff;
		this.danger=danger;
	}

	public int getDifficulty() {
		return difficulty;
	}

	public int getDanger() {
		return danger;
	}

	public double getDistance() {
		return distance;
	}

}
