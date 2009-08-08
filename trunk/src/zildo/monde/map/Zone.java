package zildo.monde.map;

public class Zone {

	private int x1,y1;
	private int x2,y2;
	
	public int getX1() {
		return x1;
	}

	public void setX1(int x1) {
		this.x1 = x1;
	}

	public int getY1() {
		return y1;
	}

	public void setY1(int y1) {
		this.y1 = y1;
	}

	public int getX2() {
		return x2;
	}

	public void setX2(int x2) {
		this.x2 = x2;
	}

	public int getY2() {
		return y2;
	}

	public void setY2(int y2) {
		this.y2 = y2;
	}

	public Zone() {
		
	}
	
	public void incX1(int a) {
		x1+=a;
	}
	public void incX2(int a) {
		x2+=a;
	}
	public void incY1(int a) {
		y1+=a;
	}
	public void incY2(int a) {
		y2+=a;
	}
	public Zone(int x1, int y1, int x2, int y2) {
		this.x1=x1;
		this.x2=x2;
		this.y1=y1;
		this.y2=y2;
	}
}
