package zildo.monde.sprites.persos.ia;

import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.ia.mover.CircularMoveOrder;
import zildo.monde.sprites.persos.ia.mover.Mover;
import zildo.monde.util.Pointf;

/**
 * Move a character describing an arc of circle.
 * 
 * This class uses {@link CircularMoveOrder} which was before used just for entity/element.
 * 
 * @author Tchegito
 *
 */
public class PathFinderArc extends PathFinder {

	CircularMoveOrder order;
	
	public PathFinderArc(Perso p_mobile) {
		super(p_mobile);
		target = p_mobile.getTarget();
		
		order = new CircularMoveOrder(target.x, target.y);
		order.init(new Mover(mobile));
	}

	@Override
	public Pointf reachDestination(float p_speed) {
		Pointf loc = new Pointf(mobile.x, mobile.y);
		if (!order.isActive()) {
			target = null;
		} else {
			float kx = mobile.x;
			float ky = mobile.y;
			loc = order.move();
			mobile.x = kx;
			mobile.y = ky;
		}
		return loc;
	}
	
}
