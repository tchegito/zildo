package zildo.monde.sprites.persos.ia.mover;

import zildo.monde.sprites.SpriteEntity;
import zildo.monde.util.Pointf;

/** Robotic movement with direction on PI/2 quarter **/ 
public class BasicMoveOrder extends MoveOrder {

	protected float speed = 1;
	/**
	 * Construct an inactive mover.
	 * @param mobile
	 */
	public BasicMoveOrder(SpriteEntity mobile) {
		super((int) mobile.x, (int) mobile.y);
		active = false;
	}
	
	public BasicMoveOrder(float x, float y, float speed) {
		super(x, y);
		if (speed != 0) {
			this.speed = speed;
		}
	}
	
	Pointf delta = new Pointf(0,0);
	
	@Override
	protected Pointf move() {
		calculateDelta();

		mobile.x += delta.x;
		mobile.y += delta.y;
	
		float dist = Pointf.distance(mobile.x, mobile.y, target.x, target.y);
		if (dist < speed) {
			// Mover has accomplished his duty. Fix float problems
			mobile.x = target.x;
			mobile.y = target.y;
			active = false;
		}
		return delta;
	}
	
	protected void calculateDelta() {
		delta.x = speed * Math.signum( ( target.x - mobile.x));
		delta.y = speed * Math.signum( ( target.y - mobile.y));
	}
}
