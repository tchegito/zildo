/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
 * 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package zildo.monde.collision;

import java.util.List;

import zildo.fwk.collection.CycleIntBuffer;
import zildo.monde.map.Area;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.SpriteModel;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.EntityType;
import zildo.monde.sprites.desc.SpriteDescription;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.elements.ElementBoomerang;
import zildo.monde.sprites.elements.ElementGoodies;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Point;
import zildo.server.EngineZildo;

/**
 * Optimized way of evaluating sprite collisions. Before, we iterate over all sprite entities for
 * each collision check. Now we fill an array with every entities at the beginning of the frame.
 * And for each check, we look directly at the right position in the array.
 * @author Tchegito
 *
 */
public class SpriteCollision {
	
	int[][] presences;
	
	Point[] patchCoords;
	CycleIntBuffer indexSpr;
	
	/**
	 * Constructor
	 */
	public SpriteCollision() {
		presences = new int[64*16][64*16];
		
		patchCoords = new Point[4];
		for (int i=0;i<4;i++) {
			int cx = i % 2;
			int cy = i / 2;
			patchCoords[i] = new Point((cx*2 - 1) * 4, ((cy*2 - 1) * 2));
		}
		// Spriteentity'ID are less than 512
		indexSpr = new CycleIntBuffer(512);
	}
	
	/**
	 * Need to be called before any checks.
	 */
	public void clear() {
		Area area = EngineZildo.mapManagement.getCurrentMap();
		for (int i=0;i<area.getDim_y()*16;i++) {
			for (int j=0;j<area.getDim_x()*16;j++) {
				presences[i][j] = 0;
			}
		}
		indexSpr.init(-1);
	}
	
	public void initFrame(List<SpriteEntity> entities) {
		for (SpriteEntity entity : entities) {
			boolean isElement = entity.getEntityType().isElement();
			if ((isElement || entity.getEntityType().isEntity()) 
					&& !entity.dying) {
				boolean isBlockable = entity.isBlocking();
				boolean isGoodies = entity.isGoodies();

				if (isGoodies || isBlockable) {

					// Store entity's ID into 1 area with its size
					//Point center = entity.getCenter();
					int id = entity.getId();
					int x = (int) entity.x;
					int y = (int) entity.y;
					int loc = (y << 10) + x;	// << 10 means *64*16
					int previousLoc = indexSpr.get(id);
					if (previousLoc != loc) {
						if (previousLoc != -1) {
							// Element has moved => delete his previous patch
							int ancy = previousLoc >> 10;
							int ancx = previousLoc & (64*16 - 1);
							applyPatch(entity, 0, ancx, ancy);
						}
						addPatch(entity);
						indexSpr.set(id, loc);
					}
				}
			}
		}
		
	}
	
	private boolean isOutOfBounds(int tx, int ty) {
		return (tx < 0 || ty < 0 || tx >= presences.length || ty >= presences.length);
	}
	
	public void notifyDeletion(SpriteEntity removedEntity) {
		int id = removedEntity.getId();
		int loc = indexSpr.get(id);
		if (loc != -1) {	// Did this entity been present ?
			indexSpr.set(id, -1);
			removePatch(removedEntity);
		}
	}
	
	public SpriteEntity getCollidingSpriteAt(int tx, int ty, SpriteEntity entityRef) {
		int refId = -1;
		if (entityRef != null) {
			refId = entityRef.getId();
		}
		boolean found = false;
		Element elem = null;
		
		for (int i=0;i<patchCoords.length && !found;i++) {
			Point p = patchCoords[i];
			
			if (!isOutOfBounds(tx + p.x, ty + p.y)) {
				int id = presences[ty + p.y][tx + p.x];
				if (id != 0 && id != refId) {
					found = true;

					// Check if element is linked to entityRef
					SpriteEntity entity = SpriteEntity.fromId(SpriteEntity.class, id);
					if (entity == null) {	// Entity doesn't exist anymore
						presences[ty + p.y][tx + p.x] = -1;
						found = false;
					} else {
						if (entity.getEntityType().isElement()) {
							elem = (Element) entity;
							if (elem.getLinkedPerso() == entityRef) {
								continue;
							}
						}
					}
					
					if (entity != null) {
						return entity;
					}
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Returns TRUE if given entity is colliding something at given location.
	 * @param tx
	 * @param ty
	 * @param entityRef
	 * @return boolean
	 */
	public boolean checkCollision(int tx, int ty, SpriteEntity entityRef) {
		int refId = -1;
		boolean isZildo = false;
		if (entityRef != null) {
			refId = entityRef.getId();
			isZildo = entityRef.isZildo();
		}
		boolean found = false;
		Element elem = null;
		for (int i=0;i<patchCoords.length && !found;i++) {
			Point p = patchCoords[i];
			
			if (!isOutOfBounds(tx + p.x, ty + p.y)) {
				int id = presences[ty + p.y][tx + p.x];
				if (id != 0 && id != refId) {
					// Check if element is linked to entityRef
					SpriteEntity entity = SpriteEntity.fromId(SpriteEntity.class, id);
					// For flying elements, check if they're below the collided element's Z
					if (entityRef.getEntityType() == EntityType.ELEMENT) {
						Element elementRef = (Element) entityRef;
						if (elementRef.flying) {
							SpriteDescription desc = entity.getDesc();
							if (desc instanceof ElementDescription) {
								ElementDescription elemDesc = (ElementDescription) desc;
								if (elemDesc.getZ() <= elementRef.z) {
									continue;
								}
							}
						}
					}
					found = true;

					if (entity == null) {	// Entity doesn't exist anymore
						presences[ty + p.y][tx + p.x] = -1;
						found = false;
					} else {
						if (entityRef != null && entity.getFloor() != entityRef.getFloor()) {
							found = false;	// Different layer
							continue;
						}
						if (entity.getEntityType().isElement()) {
							elem = (Element) entity;
							if (elem.getLinkedPerso() == entityRef) {
								continue;
							}
							if (!elem.isGoodies() && !elem.isSolid()) {
								found = false;
								continue;
							}
						}
						if (entity.dying) {	// Bugfix: Zildo could take goodies several times !
							continue;
						}
						// Rules not directly related to check (is it bad ?)
						boolean isGoodies = entity.isGoodies();
	
						if (!isGoodies && isZildo) {
							((PersoPlayer) entityRef).pushSomething(elem);
						}
						// Is it a goodies ?
						if (isGoodies) {
							if (isZildo) {
								PersoPlayer zildo = (PersoPlayer) entityRef;
								boolean disappear = zildo.pickGoodies((ElementGoodies) elem, 0);
								if (disappear && elem.fall()) {
									//elem.die();
								}
								break;
							} else {
								if (elem != null && elem.getClass().equals(
										ElementBoomerang.class)) {
									// Boomerang catches some goodies
									((ElementBoomerang) elem).grab(elem);
								} else {
									found=false;
								}
							}
						}
					}
					break;
				}
			}
		}
		return found;
	}
	
	/**
	 * Set patch modelizing entity into the presence tab.
	 * @param entity
	 */
	private void addPatch(SpriteEntity entity) {
		int x = (int) entity.x;
		int y = (int) entity.y;
		int id = entity.getId();
		applyPatch(entity, id, x, y);
	}
	
	private void removePatch(SpriteEntity entity) {
		int x = (int) entity.x;
		int y = (int) entity.y;
		applyPatch(entity, 0, x, y);
	}
	
	/**
	 * Apply a patch sized with entity SpriteModel at given place. Basically, a rectangle filled with
	 * entity's id will be appended to the presence array.<br/>
	 * We allow (ex,ey) coordinates, because it's not necessarily the entity's current location.
	 * @param entity
	 * @param id
	 * @param ex
	 * @param ey
	 */
	private void applyPatch(SpriteEntity entity, int id, int ex, int ey) {
		SpriteModel sprModel = entity.getSprModel();
		int sx = sprModel.getTaille_x();
		int sy = sprModel.getTaille_y();
		int repeatX = entity.repeatX;
		int repeatY = entity.repeatY;
		// Is the sprite rotated ?
		if (entity.rotation.isWidthHeightSwitched()) {
			int tempSize = sx; int tempRepeat = repeatX;
			sx = sy;       repeatX = repeatY; 
			sy = tempSize; repeatY = tempRepeat;
		}
		// Equivalent to 'entity.getCenter()' but with the right width/height
		int x = ex - sx/2;
		int y = ey - sy;
		// Is the sprite repeated ?
		sx*=repeatX;
		sy*=repeatY;
		for (int a = 0;a < sy;a++) {
			for (int b = 0;b < sx;b++) {
				if (!isOutOfBounds(x + b, y + a)) {
					presences[y + a][x + b] = id;
				}
			}
		}			
	}
}
