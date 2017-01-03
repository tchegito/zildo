package zildo.monde.quest.script;

import zildo.monde.util.Point;

/**
 * Dispatcher which goes from top to bottom, and bottom to top all over.
 * @author eboussaton
 *
 */
public class DispatchRoundTrip extends Dispatch {

    final int speed;
    boolean way;
    
    Point location;
    public DispatchRoundTrip(Point p_center, Point p_range, int p_speed) {
	super(p_center, p_range);
	
	speed = p_speed;
	
	location = new Point(p_center.x - p_range.x/2, p_center.y - p_range.y/2);
    }

    @Override
    public Point next() {
	int pas = way ? speed : -speed;
	
	location.y+=pas;
	if (location.y > (center.y + range.y/2) ||
	    location.y < (center.y - range.y/2)) {
	    location.y-=pas;
	    way=!way;	// Inverse the way
	}
	
	return location;
    }
}
