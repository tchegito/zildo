package zildo.monde.sprites.persos.ia;

import java.util.HashMap;
import java.util.Map;

import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.util.Point;
import zildo.monde.util.Pointf;

public abstract class Mover {

	SpriteEntity mobile;
	Point target;
	Map<Integer, SpriteEntity> linkedEntities;	// All entities carried by the mover
	boolean active;
	
	public Mover(SpriteEntity mobile, int x, int y) {
		this.mobile = mobile;
		this.target = new Point(x, y);
		this.linkedEntities = new HashMap<Integer, SpriteEntity>();
		this.active = true;
	}
	
	/**
	 * Moves the {@link #mobile} entity and returns the delta.
	 * @return {@link Point}
	 */
	protected abstract Point move();
	
	/**
	 * Returns TRUE if this mover is currently moving.
	 * @return boolean
	 */
	public boolean isActive() {
		return active;
	}
	
	/**
	 * Merge a mover inside this one, to keep linked entities.
	 * TODO:problem when mover is from different kind.
	 * @param m
	 */
	public void merge(Mover m) {
		this.target = m.target;
		this.active = m.active;
	}
	
	/**
	 * Link an entity to this mover.
	 * @param e
	 * @return TRUE if entity is just newly associated (=it wasn't before)
	 */
	public boolean linkEntity(SpriteEntity e) {
		return linkedEntities.put(e.getId(), e) == null;
	}
	
	/**
	 * Unlink an entity : it isn't on the moving entity anymore.
	 * @param e
	 */
	public void unlinkEntity(SpriteEntity e) {
		linkedEntities.remove(e.getId());
	}
	
	public void reachTarget() {
		Point delta = move();
		// Move the linked entities
		for (SpriteEntity entity : linkedEntities.values()) {
			// Handle collision for Perso (but is it really necessary ? Maybe for later !)
			if (entity.getEntityType().isPerso()) {
				Perso p = (Perso) entity;
				Pointf result = p.tryMove(p.x + delta.x, p.y + delta.y);
				p.x = result.x;
				p.y = result.y;
			} else {
				entity.x += delta.x;
				entity.y += delta.y;
			}
		}
		if ((int) mobile.x == target.x && (int) mobile.y == target.y) {
			// Mover has accomplished his duty
			active = false;
		}
	}
}
