/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
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

import zildo.fwk.db.Identified;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.SpriteDescription;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.monde.util.Zone;
import zildo.server.EngineZildo;

/**
 * Perso specific collision engine. The goal was to avoid a huge amount of 'for each' to determine
 * a Perso collision. So we store perso's ID in two presence array (1 for foreground, and 1 for back)
 * in order to know quickly if there's one on the given grid case.<p/>
 * 
 * @author Tchegito
 *
 */
public class PersoCollision {

	
	CollBuffer[] buffers;	// 0=foreground persos / 1=background persos
	int[][] capillarity;
	
	public PersoCollision() {
		buffers = new CollBuffer[2];
		for (int i=0;i<buffers.length;i++) {
			buffers[i] = new CollBuffer();
		}

	}
	
	public void clear() {
		buffers[0].clear();
		buffers[1].clear();
	}
	
	public void initFrame(List<Perso> persos) {
		for (Perso p : persos) {
			if (p.getPv() > 0) {
				int gridX = (int) p.getX() >> 4;
				int gridY = (int) p.getY() >> 4;
				if (!CollBuffer.isOutOfBounds(gridX, gridY) && !p.flying) {
					if (p.isForeground()) {
						buffers[0].updateId(gridX, gridY, p.getId());
					} else {
						buffers[1].updateId(gridX, gridY, p.getId());
					}
				}
			}
		}
	}

	public void notifyDeletion(Perso removedPerso) {
		int id = removedPerso.getId();
		if (removedPerso.isForeground()) {
			buffers[0].remove(id);
		} else {
			buffers[1].remove(id);
		}
	}
	
	/**
	 * Returns TRUE if given element is colliding something at given location.
	 * @param tx
	 * @param ty
	 * @param quelElement (can be NULL)
	 * @param rayon
	 * @return boolean
	 */
	public Perso checkCollision(int x, int y, Element quelElement, int rayon) {
		
		int gridX = x >> 4;
		int gridY = y >> 4;
		int fromId = quelElement != null ? quelElement.getId() : -1;
		boolean foreGround = quelElement != null && quelElement.isForeground();

		// Try on given grid case
		Perso perso = locatePerso(gridX, gridY, foreGround, fromId);
		if (perso != null && checkCollisionOnPerso(x, y, quelElement, perso, rayon)) {
			return perso;
		}				
		int nbPersoAround = CollBuffer.howManyAround(gridX, gridY);
		if (nbPersoAround <= 1) {
			return null;
		}
		nbPersoAround--;
		if (nbPersoAround != 0) {
			for (Angle a : Angle.values()) {
				Point offset = a.coords;
				int gx = gridX + offset.x;
				int gy = gridY + offset.y;
				perso = locatePerso(gx, gy, foreGround, fromId);
				if (perso != null && checkCollisionOnPerso(x, y, quelElement, perso, rayon)) {
					return perso;
				}

			}
		}
		return null;
	}

	/**
	 * Return a Perso if there's one in the presence array.
	 * @param gridX
	 * @param gridY
	 * @param foreGround
	 * @return Perso
	 */
	private Perso locatePerso(int gridX, int gridY, boolean foreGround, int fromId) {
		int id = -1;
		if (!CollBuffer.isOutOfBounds(gridX, gridY)) {
			if (foreGround) {
				id = buffers[0].getId(gridX, gridY, fromId);
			} else {
				id = buffers[1].getId(gridX, gridY, fromId);
			}
		}
		if (id != -1 && id != fromId) {
			SpriteEntity entity = Identified.fromId(SpriteEntity.class, id);
			if (entity == null || !entity.getEntityType().isPerso()) {
				// This entity is not a Perso, and shouldn't have been here. This could happen in a very rare case (not completely identified)
				Identified.remove(SpriteEntity.class, id);
				buffers[foreGround ? 0 : 1].resetId(gridX, gridY, id);
				return null;
			} else {
				return (Perso) Identified.fromId(SpriteEntity.class, id);
			}
		} else {
			return null;
		}
		
	}

	
	/**
	 * Check if 'quelPerso' is colliding with the given element.
	 * @param x
	 * @param y
	 * @param quelElement
	 * @param quelPerso
	 * @param rayon
	 * @return Perso
	 */
	private boolean checkCollisionOnPerso(int x, int y, Element quelElement, Perso quelPerso, int rayon) {
		if (quelPerso.isZildo() && quelElement != null && quelElement.getDesc() instanceof ElementDescription) {
    		ElementDescription d = (ElementDescription) quelElement.getDesc();
    		if (d.isPushable() && quelElement.vx+quelElement.vy != 0f) {
    			return false;
    		}
    	}
        int tx = (int) quelPerso.getX();
        int ty = (int) quelPerso.getY();
        SpriteDescription descToCompare = quelPerso.getDesc();
        int rayonPersoToCompare = rayon;
        if (descToCompare != null) {
        	rayonPersoToCompare = descToCompare.getRadius();
        }
        // Do we have a Perso in parameters ?
        Perso perso = null;
        if (quelElement != null && quelElement.getEntityType().isPerso()) {
            perso = (Perso) quelElement;
        }
        Zone size = null; Zone size2 = null;
        if (quelPerso.getMover() != null) {
        	size = quelPerso.getMover().getZone();
        }
        if (quelElement != null && quelElement.getMover() != null) {
        	size2 = quelElement.getMover().getZone();
        }
        // TODO: maybe merge this behavior with CollideManagement#checkColli
        boolean colli = false;
        if (size != null) {
        	colli = new Rectangle(size).isCrossingCircle(new Point(x, y), rayon);
        } else if (size2 != null) {
        	colli = new Rectangle(size2).isCrossingCircle(new Point(tx, ty), rayonPersoToCompare);
        } else {
        	colli = EngineZildo.collideManagement.checkCollisionCircles(x, y, tx, ty, rayon, rayonPersoToCompare);
        }
        if (colli) {
            if (perso != null && perso.isZildo() && perso.linkedSpritesContains(quelPerso)) {
                // Collision entre Zildo et l'objet qu'il porte dans les mains => on laisse
            } else if (quelElement == null || quelElement.getLinkedPerso() != quelPerso) {
                return true;
            }
        }
		// No collision
		return false;
	}
}
