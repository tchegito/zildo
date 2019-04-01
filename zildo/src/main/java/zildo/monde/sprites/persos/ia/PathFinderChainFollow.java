package zildo.monde.sprites.persos.ia;

import zildo.fwk.collection.FIFOBuffer;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.util.Point;
import zildo.monde.util.Pointf;

/** Pathfinder which can make a bunch of other {@link Perso} walk in the footsteps of another one.
 * 
 * It's quite different than {@link PathFinderFollow} because in the latter, mobile path follow the current target at each
 * frame. Here we ensure that each following character will be at the exact SAME location than his leader, with a fixed delay
 * (here = 10).
 * 
 * @author Tchegito
 *
 */
public class PathFinderChainFollow extends PathFinderFollow {

	PathFinder pfFollowed;
	
	Pointf lastTarget;
	
	FIFOBuffer followedLocations;
	
	final int DISTANCE;
	
	/** Note that 'followed' has to be a Perso, it's a restriction compared to superclass **/
	public PathFinderChainFollow(Perso p_mobile, PathFinder p_followed, int distance) {
		super(p_mobile, null);
		speed = p_followed.speed;
		pfFollowed = p_followed;
		
		DISTANCE = distance;
		
		followedLocations = new FIFOBuffer(DISTANCE);
	}

	@Override
	public void determineDestination() {
		/* Not used by this Pathfinder */
	}
	
	@Override
	public Pointf reachDestination(float p_speed) {
		Point p = new Point(pfFollowed.mobile.x, pfFollowed.mobile.y);
		
		Point next = followedLocations.pushAndPop(p);
		if (next == null) {
			// Location already in the queue => return current one
			return new Pointf(mobile.x, mobile.y);
		}
		target = new Pointf(next);
		return super.reachDestination(p_speed);
	}
	
	@Override
	public String toString() {
		return "pfChainFollower: "+mobile.getName()+"("+mobile.x+","+mobile.y+") following "+pfFollowed.mobile.getName()+" target="+getTarget();
	}
}
