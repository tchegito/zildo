/**
 * Legend of Zildo
 * Copyright (C) 2006-2012 Evariste Boussaton
 * Based on original Zelda : link to the past (C) Nintendo 1992
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
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.server.EngineZildo;
import zildo.server.PersoManagement;

/**
 * @author Tchegito
 *
 */
public class PersoCollision {

	int[][][] presences;
	int[][][] presencesForeground;
	
	public PersoCollision() {
		presences = new int[64][64][2];
		presencesForeground = new int[64][64][2];
	}
	
	public void clear() {
		// Clear
		for (int i=0;i<64;i++) {
			for (int j=0;j<64;j++) {
				for (int k=0;k<2;k++) {
					presences[i][j][k] = -1;
					presencesForeground[i][j][k] = -1;
				}
			}
		}
	}
	
	public void initFrame(List<Perso> persos) {
		clear();
		for (Perso p : persos) {
			if (p.getPv() > 0) {
				int gridX = (int) p.getX() >> 4;
				int gridY = (int) p.getY() >> 4;
				if (!isOutOfBounds(gridX, gridY)) {
					if (p.isForeground()) {
						setId(gridX, gridY, presencesForeground, p.getId());
					} else {
						setId(gridX, gridY, presences, p.getId());
					}
				}
			}
		}
	}
	
	private boolean isOutOfBounds(int tx, int ty) {
		return (tx < 0 || ty < 0 || tx >= presences.length || ty >= presences.length);
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

		for (Angle a : Angle.values()) {
			Point offset = a.coords;
			int gx = gridX + offset.x;
			int gy = gridY + offset.y;
			perso = locatePerso(gx, gy, foreGround, fromId);
			if (perso != null && checkCollisionOnPerso(x, y, quelElement, perso, rayon)) {
				return perso;
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
		if (!isOutOfBounds(gridX, gridY)) {
			if (foreGround) {
				id = getId(gridX, gridY, presencesForeground, fromId);
			} else {
				id = getId(gridX, gridY, presences, fromId);
			}
		}
		if (id != -1 && id != fromId) {
			PersoManagement.parcours++;
			return (Perso) Identified.fromId(SpriteEntity.class, id);
		} else {
			return null;
		}
		
	}
	
	private int getId(int gridX, int gridY, int[][][] array, int fromId) {
		int id = array[gridY][gridX][0];
		if (id == fromId) {
			id = array[gridY][gridX][1];
		}
		return id;
	}
	
	private void setId(int gridX, int gridY, int[][][] array, int fromId) {
		int id = array[gridY][gridX][0];
		if (id == -1) {
			array[gridY][gridX][0] = fromId;
		} else {
			// TODO: and what should we do if this room isn't empty ?
			array[gridY][gridX][1] = fromId;
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
        PersoDescription descToCompare = quelPerso.getDesc();
        int rayonPersoToCompare = rayon;
        if (descToCompare != null) {
        	rayonPersoToCompare = descToCompare.getRadius();
        }
        // Do we have a Perso in parameters ?
        Perso perso = null;
        if (quelElement != null && quelElement.getEntityType().isPerso()) {
            perso = (Perso) quelElement;
        }
        if (EngineZildo.collideManagement.checkCollisionCircles(x, y, tx, ty, rayon, rayonPersoToCompare)) {
            if (perso != null && perso.isZildo() && perso.linkedSpritesContains(quelPerso)) {
                // Collision entre Zildo et l'objet qu'il porte dans les mains => on laisse
            } else if (quelElement == null || quelElement.getLinkedPerso() != quelPerso) {
                return true;
            }
        }
		// No one found
		return false;
	}
}
