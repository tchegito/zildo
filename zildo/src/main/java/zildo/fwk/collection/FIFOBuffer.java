package zildo.fwk.collection;

import zildo.monde.util.Point;

/** Implementation of a circular buffer.
 * We add value in it via pushAndPop, and when buffer is full, we get the first entered.
 * Consecutive values are not allowed. **/
public class FIFOBuffer {

	Point[] followedLocations;
	int idx;
	int fill=0;
	
	final int distance;
	
	public FIFOBuffer(int size) {
		distance = size;
		
		followedLocations = new Point[distance];
		idx=0;
	}
	
	/** Returns NULL if given point is already the latest in the queue **/
	public Point pushAndPop(Point p) {
		Point ret = null;
		if (fill == distance) {
			ret = followedLocations[idx];
		}
		if (fill < 1 || !followedLocations[(idx+distance-1 ) % distance].equals(p)) {
			followedLocations[idx] = p;
			fill = Math.min(distance,  fill+1);
			idx = (idx+1) % distance;
		}
		return ret;
	}
}
