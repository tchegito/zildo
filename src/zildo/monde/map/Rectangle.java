/**
 *
 */
package zildo.monde.map;

/**
 * @author tchegito
 */
public class Rectangle {

    private Point[] coordinates;
    private Point size;
    
    /**
     * Create a square with a given center and a size.
     * @param p_center
     * @param p_size
     */
    public Rectangle(Point p_center, Point p_size) {
        Point cornerTopLeft = new Point(p_center.x - p_size.x / 2, p_center.y - p_size.y / 2);
        Point cornerTopRight = cornerTopLeft.translate(p_size.x, 0);
        Point cornerBottomLeft = cornerTopLeft.translate(0, p_size.y);
        Point cornerBottomRight = cornerBottomLeft.translate(p_size.x, 0);
        coordinates = new Point[4];
        coordinates[0] = cornerTopLeft;
        coordinates[1] = cornerTopRight;
        coordinates[2] = cornerBottomLeft;
        coordinates[3] = cornerBottomRight;
        size=p_size;
    }

    public boolean isInside(Point p_point) {
        return (p_point.x >= coordinates[0].x && p_point.y >= coordinates[0].y && p_point.x <= coordinates[3].x && p_point.y <= coordinates[3].y);
    }

    /**
     * Check if the two rectangles are colliding.
     * @param p_other
     * @return boolean
     */
    public boolean isCrossing(Rectangle p_other) {
        for (int i = 0; i < 4; i++) {
            if (isInside(p_other.coordinates[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the given circle is colliding with our rectangle.
     * @param p_center
     * @param p_radius
     * @return boolean
     */
    public boolean isCrossingCircle(Point p_center, int p_radius) {
        for (int i = 0; i < 4; i++) {
            Point p = coordinates[i];
            int c = Math.abs(p.x - p_center.x);
            int d = Math.abs(p.y - p_center.y);
            if (c < 50 && d < 50) {
                c = c * c;
                c += d * d;
                c = (int) Math.sqrt((float) c);
                if (c < p_radius) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public Point getSize() {
    	return size;
    }
    
    public Point getCornerTopLeft() {
    	return coordinates[0];
    }
}
