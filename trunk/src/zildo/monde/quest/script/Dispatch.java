package zildo.monde.quest.script;

import zildo.monde.util.Point;

/**
 * Modelize a point dispatcher, to provide a nice animation (for example : staff thrown, or arrows shot)
 * @author eboussaton
 *
 */
public abstract class Dispatch {

    final Point center;
    final Point range;
    
    /**
     * Create the dispatcher. Points will be given inside the square (center-range/2 .. center+range/2)
     */
    public Dispatch(Point p_center, Point p_range) {
	center = p_center;
	range = p_range;
    }
    
    public abstract Point next();
}
