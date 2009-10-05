package zildo.monde.collision;

import zildo.monde.map.Point;

public class Line {

	Point a;
	Point b;
	
	public Line(Point p_a, Point p_b) {
		a=p_a;
		b=p_b;
	}
	
	public boolean isVertical() {
		return a.x == b.x;
	}
	
	/**
	 * Returns the line's slope. BE CAREFUL to vertical line ! (division by zero) 
	 * @return float
	 */
	public float getSlope() {
		float deltaY=b.y - a.y;
		float deltaX=b.x - a.x;
		return deltaY / deltaX;
	}
	
	/**
     * Returns the intersecting point between the current line and the given one.
     * @param p_other
     * @return Point (NULL if lines never cross)
     */
    public Point intersect(Line p_other) {
        float interX, interY;
        if (isVertical() && p_other.isVertical()) {
            // Two lines are vertical
            if (a.x != p_other.a.x) {
                return null; // Never crossed
            } else {
                return a; // Arbitrary point
            }
        } else if (isVertical()) {
            return p_other.intersect(this);
        }
        float slope = getSlope();
        float add = a.y - slope * a.x;
        if (p_other.isVertical()) { // No need to calculate the x coordinate : line is vertical
            interX = p_other.a.x;
        } else { // Calculate the intersection x coordinate
            float slopeOther = p_other.getSlope();
            float addOther = p_other.a.y - slopeOther * p_other.a.x;

            if (slope == slopeOther) { // Lines are parallel
                if (add != addOther) {
                    return null; // Never crossed
                } else {
                    return a; // Arbitrary point
                }
            }

            interX = (addOther - add) / (slope - slopeOther);
        }
        interY = interX * slope + add;
        return new Point((int) interX, (int) interY);
    }
	
}
