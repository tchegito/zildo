package zildo.monde.sprites.persos.ia.mover;

import zildo.monde.sprites.SpriteEntity;
import zildo.monde.util.Point;
import zildo.monde.util.Pointf;

public abstract class Mover {

	SpriteEntity mobile;
	Point target;
	boolean active;
	
	public Mover(SpriteEntity mobile, int x, int y) {
		this.mobile = mobile;
		this.target = new Point(x, y);
		this.active = true;
	}
	
	/**
	 * Moves the {@link #mobile} entity and returns the delta.
	 * @return {@link Pointf}
	 */
	protected abstract Pointf move();
	
	/**
	 * Returns TRUE if this mover is currently moving.
	 * @return boolean
	 */
	public boolean isActive() {
		return active;
	}
	
}
