package zildo.monde;

public class Point {

	private int x, y;

	public Point() {
		
	}
	public Point(int x, int y) {
		this.x=x;
		this.y=y;
	}
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public void addX(int xPlus) {
		this.x+= xPlus;
	}

	public void addY(int yPlus) {
		this.y+= yPlus;
	}

}
