package zildo.monde.sprites.persos.ia;

import zildo.monde.sprites.persos.Perso;
import zildo.monde.util.Pointf;

/**
 * Path finder able to target not only (x,y) location but z too.
 * 
 * @author Tchegito
 *
 */
public class PathFinderFreeFlying extends PathFinder {

	public PathFinderFreeFlying(Perso mobile) {
		super(mobile);
		speed = 1.5f;
	}
	
	@Override
	public Pointf reachDestination(float p_speed) {
		Pointf p = reachLine(p_speed, true);

		// Reach z target too
		if (targetZ != null && targetZ != (int) mobile.z) {
			mobile.z += speed * Math.signum(targetZ - mobile.z);
		}

		return p;
	}
	
    public boolean hasReachedTarget() {
    	return (target == null || super.hasReachedTarget())
    			&& (targetZ == null || targetZ == (int) mobile.z);   	
    }
    
	public boolean hasNoTarget() {
		return target == null && targetZ == null;
	}
}
