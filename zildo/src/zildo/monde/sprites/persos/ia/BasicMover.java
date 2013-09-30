package zildo.monde.sprites.persos.ia;

import zildo.monde.sprites.SpriteEntity;
import zildo.monde.util.Point;

public class BasicMover extends Mover {

	protected float speed = 1;
	/**
	 * Construct an inactive mover.
	 * @param mobile
	 */
	public BasicMover(SpriteEntity mobile) {
		super(mobile, (int) mobile.x, (int) mobile.y);
		active = false;
	}
	
	public BasicMover(SpriteEntity mobile, int x, int y, float speed) {
		super(mobile, x, y);
		if (speed != 0) {
			this.speed = speed;
		}
	}
	
	Point delta = new Point(0,0);
	
	@Override
	protected Point move() {
		delta.x = (int) Math.signum( ( target.x - mobile.x));
		delta.y = (int) Math.signum( ( target.y - mobile.y));
		
		mobile.x += speed * delta.x;
		mobile.y += speed * delta.y;
	
		return delta;
	}

	@Override
	public void merge(Mover m) {
		super.merge(m);
		this.speed = ((BasicMover) m).speed;		
	}
}
