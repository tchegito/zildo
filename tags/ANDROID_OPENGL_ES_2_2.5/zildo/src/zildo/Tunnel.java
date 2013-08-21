package zildo;

import zildo.monde.util.Point;
import zildo.monde.util.Pointf;

public class Tunnel {

	public final static int WIDTH = 40;
	final static int SIZE_X = 300 * 2;
	final static int SIZE_Y = SIZE_X;	// Could change later
	public final static int CENTER_X = 160;
	public final static int CENTER_Y = 100;
	final static int DEEPNESS = 20;
	final static int delay = 1000/4; 	// Delay between two new circles
	final float maxPasCamera = 1;
	
	public Point[][] points;
	public Point[][] screenPoints;
	public Pointf camera;
	public Pointf pasCamera;
	static public int  lastIndex;
	int spent;
	double beta;
	double dx=0, dy=0;
	
	public Tunnel() {
		points = new Point[DEEPNESS][WIDTH];
		screenPoints = new Point[DEEPNESS][WIDTH];
		for (int i=0;i<DEEPNESS;i++) {
			for (int j=0;j<WIDTH;j++) {
				points[i][j] = new Point(0,0);
				screenPoints[i][j] = new Point(0,0);
			}
			generateCircle(points[i]);
		}
		lastIndex = -1;
		spent = delay;
		camera = new Pointf(CENTER_X, CENTER_Y);
		pasCamera = new Pointf(0, 0);
	}
	
	public void update() {
		spent--;
		if (spent == 0) {
			spent = delay;
			lastIndex = (lastIndex+1) % DEEPNESS;
			generateCircle(points[lastIndex]);
		}
		
		updateScreenCoords();
	}
	
	private void updateScreenCoords() {
		double progress = ((double)spent / (double)delay);
		double z = DEEPNESS+1+progress;
		Pointf camLook = funcLateral(beta - (DEEPNESS * 0.9) * 0.1 - progress*0.1);
		camera.x = CENTER_X + camLook.x / 1;
		camera.y = CENTER_Y + camLook.y / 1;
		for (int i=0;i<DEEPNESS;i++) {
			if (z < 0) {
				throw new RuntimeException();
			}
			int ind = (lastIndex - i + DEEPNESS) % DEEPNESS;
			for (int j=0;j<WIDTH;j++) {
				Point p = points[ind][j];
				int x = (int) ((p.x - camera.x) / z + CENTER_X);
				int y = (int) ((p.y - camera.y) / z + CENTER_Y);
				screenPoints[ind][j].x = x;
				screenPoints[ind][j].y = y;
			}
			z-=1.1;
		}		
	}
	
	private void generateCircle(Point[] pts) {
		double alpha = 0;
		double pas = 2*Math.PI / WIDTH;

		Pointf p = funcLateral(beta);
		
		dx = p.x;
		dy = p.y;

		for (int i=0;i<WIDTH;i++) {
			int x = (int) (CENTER_X + dx + SIZE_X * Math.cos(alpha));
			int y = (int) (CENTER_Y + dy + SIZE_Y * Math.sin(alpha));
			alpha += pas;
			pts[i].x = x;
			pts[i].y = y;
		}
		beta += 0.1;
	}
	
	private Pointf funcLateral(double angle) {
		float x = (float) (3 * 128 * Math.cos(2*angle) * Math.sin(angle));
		float y = (float) (2 * 128 * Math.sin(1.7*angle) * 3*Math.cos(0.4*angle));
		return new Pointf(x, y);
	}

}
