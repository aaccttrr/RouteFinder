package application;

public class Route {

	private int distance; //km
	private int difficulty; //1-10
	private int danger; //1-10

	public Route(int dist, int diff, int danger){
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

	public int getDistance() {
		return distance;
	}

}
