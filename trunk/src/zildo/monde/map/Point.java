package zildo.monde.map;

public class Point {

    public int x, y;

    public Point() {

    }
    
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point(float x, float y) {
    	this.x = (int)x;
    	this.y = (int)y;
    }
    
    // Copy constructor
    public Point(Point p) {
        this.x = p.x;
        this.y = p.y;
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

    public void add(Point p_point) {
    	add(p_point.x, p_point.y);
    }
    
    public void add(int p_xPlus, int p_yPlus) {
        this.x += p_xPlus;
        this.y += p_yPlus;
    }

    public Point translate(int addX, int addY) {
        return new Point(x + addX, y + addY);
    }
    
    public Point translate(Point p_pointAdd) {
        return translate(p_pointAdd.x, p_pointAdd.y);
    }

    public Point multiply(float factor) {
    	return new Point(x*factor, y*factor);
    }
    
    public String toString() {
    	return "("+x+", "+y+")";
    }
    
    /**
     * Returns the distance between the current point and a given one.
     * @param p_other
     * @return float
     */
    public float distance(Point p_other) {
        int c = Math.abs(x - p_other.x);
        int d = Math.abs(y - p_other.y);
        c = c * c;
        c += d * d;
        return (float) Math.sqrt(c);
    }
    
    /**
     * Returns TRUE if given point have same coordinates as current one.
     */
    public boolean equals(Object p_other) {
		if (!p_other.getClass().equals(Point.class)) {
    		return false;
    	}
    	Point p=(Point) p_other;
    	return p.x == x && p.y == y;
    }
}
