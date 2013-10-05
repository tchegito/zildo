package zildo.monde.sprites.persos.ia.mover;

import zildo.monde.sprites.SpriteEntity;
import zildo.monde.util.Pointf;

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
	
	public BasicMoveOrder(int x, int y, float speed) {
		super(x, y);
		if (speed != 0) {
			this.speed = speed;
		}
	}
	
	Pointf delta = new Pointf(0,0);
	
	@Override
	protected Pointf move() {
		delta.x = Math.signum( ( target.x - mobile.x));
		delta.y = Math.signum( ( target.y - mobile.y));
		
		mobile.x += speed * delta.x;
		mobile.y += speed * delta.y;
	
		if ((int) mobile.x == target.x && (int) mobile.y == target.y) {
			// Mover has accomplished his duty
			active = false;
		}
		return delta;
	}
}
