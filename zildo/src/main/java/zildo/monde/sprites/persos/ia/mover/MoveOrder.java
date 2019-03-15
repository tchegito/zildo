package zildo.monde.sprites.persos.ia.mover;

import zildo.monde.sprites.SpriteEntity;
import zildo.monde.util.Pointf;

public abstract class MoveOrder {

	Mover wrapper;
	SpriteEntity mobile;
	Pointf target;
	boolean active;
	
	public MoveOrder(float x, float y) {
		this.target = new Pointf(x, y);
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
	
	/**
	 * Decorates this order with contextual info.
	 * @param p_wrapper
	 */
	void init(Mover p_wrapper) {
		this.wrapper = p_wrapper;
		this.mobile = wrapper.mobile;
	}
}
