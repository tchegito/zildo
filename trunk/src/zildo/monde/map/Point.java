package zildo.monde.map;

public class Point {

    public int x, y;

    public Point() {

    }

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
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

    public void addX(int xPlus) {
        this.x += xPlus;
    }

    public void addY(int yPlus) {
        this.y += yPlus;
    }

    public Point translate(int addX, int addY) {
        return new Point(x + addX, y + addY);
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
        return (float) Math.sqrt((float) c);
    }
}
