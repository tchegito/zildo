/**
 * Legend of Zildo
 * Copyright (C) 2006-2011 Evariste Boussaton
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

package zildo.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import zildo.fwk.awt.ZildoScrollablePanel;
import zildo.fwk.gfx.engine.SpriteEngine;
import zildo.monde.map.Point;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.SpriteModel;
import zildo.monde.sprites.SpriteStore;
import zildo.monde.sprites.utils.SpriteSorter;
import zildo.prefs.Constantes;

/**
 * All client side sprite problematic.
 *
 * Here, we take sprites locations, attributes, and so on, to display them correctly on the device.
 *
 * This class acts as a layer between SpriteManagement and SpriteEngine.
 *
 * We only work on SpriteEntity here, because SpriteManagament do all the calculating jobs, about speed, acceleration, and
 * here, we just have to display a sprite at a given location.
 *
 * @author tchegito
 *
 */
public class SpriteDisplay extends SpriteStore {

	SpriteEngine spriteEngine;
	
	private SpriteSorter spriteSorter;

	public int zildoId;
	
	// We use a map to ease the access to an entity with his ID
	Map<Integer, SpriteEntity> mapEntities=new HashMap<Integer, SpriteEntity>();
	
	// ZEditor only
	public ForeBackController foreBackController=new ForeBackController();
	    
	public void setEntities(List<SpriteEntity> p_entities) {
		//mapEntities.clear();
		for (SpriteEntity entity : p_entities) {
			if (entity.dying) {
				// This entity should be removed
				mapEntities.remove(entity.getId());
			} else {
				// Update or create this one
				mapEntities.put(entity.getId(), entity);
			}
		}
		for (Iterator<SpriteEntity> it=spriteEntities.iterator();it.hasNext();) {
			SpriteEntity entity=it.next();
			if (entity.getId() != -1) {
				it.remove();
			}
		}
		spriteEntities.addAll(mapEntities.values());
	}
	
	public SpriteDisplay(SpriteEngine spriteEngine) {
		super();
		
		this.spriteEngine=spriteEngine;

		int SORTY_MAX=Constantes.SORTY_MAX;
		int SORTY_REALMAX=Constantes.SORTY_REALMAX;
		
		if (Client.isZEditor()) {
			SORTY_MAX=ZildoScrollablePanel.viewSizeY+40;
			SORTY_REALMAX=ZildoScrollablePanel.viewSizeY+80;
		}

		spriteSorter=new SpriteSorter(SORTY_MAX, SORTY_REALMAX);
	}
	
	/**
	 * -insert sprites into sort array
	 * @param cameraXnew
	 * @param cameraYnew
	 */
	public void updateSpritesClient(Point cameraNew) {
		
		// Reset the sort array used for sprites
		spriteSorter.clearSortArray();
		
		Collection<SpriteEntity> entities=spriteEntities;
		
		// Do perso animations
		// Mandatory to do that first, because one perso can be connected to other sprites
		for (SpriteEntity entity : entities) {
			if (entity.getEntityType() == SpriteEntity.ENTITYTYPE_PERSO) {
				// Camera moves
				entity.setScrX ( entity.getAjustedX() - cameraNew.x);
				entity.setScrY ( entity.getAjustedY() - cameraNew.y);
			}
		}
		
		for (SpriteEntity entity : entities) {
			if (entity != null) {
				// Camera moves
				if (entity.getEntityType()==SpriteEntity.ENTITYTYPE_ENTITY) { 
					entity.setScrX(entity.getAjustedX() - cameraNew.x);
					entity.setScrY(entity.getAjustedY() - cameraNew.y);
				} else if (entity.getEntityType()==SpriteEntity.ENTITYTYPE_ELEMENT) {
					// Center sprite
					SpriteModel spr=entity.getSprModel();
					entity.setScrX(entity.getAjustedX() - cameraNew.x - (spr.getTaille_x() >> 1));
					entity.setScrY(entity.getAjustedY() - cameraNew.y - spr.getTaille_y());
				}
			}
		}
		
		// Iterate through every entities to synchronize data with vertex buffer
		// spriteEntities list order correspond to the creation order with spawn*** methods.
		spriteEngine.startInitialization();
		
		boolean displayBackSprite=ClientEngineZildo.mapDisplay.foreBackController.isDisplayBackground();
		
		for (SpriteEntity entity : entities) {
			// Manage sprite in the sort array
			if (entity.isVisible() && (entity.isForeground() || displayBackSprite)) {
				// Add in the sort array
				spriteSorter.insertSpriteInSortArray(entity);
				// Add in vertices buffer
				spriteEngine.synchronizeSprite(entity);
			}
		}
		
		spriteEngine.endInitialization();
		
		// Sort perso along the Y-axis
		spriteSorter.orderSpritesByBank();			// Fill the quadOrder and bankOrder arrays
		spriteEngine.buildIndexBuffers(spriteSorter.getQuadOrder());
	}

	public int[][] getBankOrder() {
		return spriteSorter.getBankOrder();
	}

	public SpriteEntity getZildo() {
		return mapEntities.get(zildoId);
	}
	
	public void setZildoId(int p_zildoId) {
		zildoId=p_zildoId;
	}
}