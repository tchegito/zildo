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

package zildo.client;

import java.util.Iterator;
import java.util.List;

import zildo.fwk.db.Identified;
import zildo.fwk.gfx.engine.SpriteEngine;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.SpriteModel;
import zildo.monde.sprites.SpriteStore;
import zildo.monde.sprites.utils.SpriteSorter;
import zildo.monde.util.Point;
import zildo.monde.util.Zone;
import zildo.resource.Constantes;

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
	
	protected SpriteSorter spriteSorter;

	public int zildoId = -1;
	
	// We use an map to ease the access to an entity with his ID
	// Replaced by an array for performance reason on Android (Dalvik's hashmap are slow)
	SpriteEntity[] arrayEntities = new SpriteEntity[512];
	
	// ZEditor only
	public ForeBackController foreBackController=new ForeBackController();
	    
	public void setEntities(List<SpriteEntity> p_entities) {
		for (SpriteEntity entity : p_entities) {
			if (entity.getId() != -1) {
				if (entity.dying) {
					// This entity should be removed
					arrayEntities[entity.getId()] = null;
				} else {
					// Update or create this one
					arrayEntities[entity.getId()] = entity;
				}
			}
		}
		for (Iterator<SpriteEntity> it=spriteEntities.iterator();it.hasNext();) {
			SpriteEntity entity=it.next();
			if (entity.getId() != -1) {
				it.remove();
			}
		}
		for (int i=0;i<arrayEntities.length;i++) {
			SpriteEntity entity = arrayEntities[i];
			if (entity != null) {
				spriteEntities.add(entity);
			}
		}
	}
	
	public SpriteDisplay(SpriteEngine spriteEngine) {
		super();
		
		this.spriteEngine=spriteEngine;

		int SORTY_MAX=Constantes.SORTY_MAX;
		int SORTY_REALMAX=Constantes.SORTY_REALMAX;
		
		if (ClientEngineZildo.editing) {
			// TODO put back these values in order to fix ZEditor
			//SORTY_MAX=ZildoScrollablePanel.viewSizeY+40;
			//SORTY_REALMAX=ZildoScrollablePanel.viewSizeY+80;
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
		
		List<SpriteEntity> entities=spriteEntities;
		
		// Do perso animations
		// Mandatory to do that first, because one perso can be connected to other sprites
		for (SpriteEntity entity : entities) {
			if (entity.getEntityType().isPerso()) {
				// Camera moves
				entity.setScrX ( entity.getAjustedX() - cameraNew.x);
				entity.setScrY ( entity.getAjustedY() - cameraNew.y);
			}
		}
		
		for (SpriteEntity entity : entities) {
			if (entity != null) {
				// Camera moves
				if (entity.getEntityType().isEntity()) { 
					entity.setScrX(entity.getAjustedX() - cameraNew.x);
					entity.setScrY(entity.getAjustedY() - cameraNew.y);
				} else if (entity.getEntityType().isElement() && entity.isVisible()) {
					// Center sprite
					SpriteModel spr=entity.getSprModel();
					int tx = spr.getTaille_x();
					int ty = spr.getTaille_y();
					if (entity.rotation.isWidthHeightSwitched() ) {
						tx = spr.getTaille_y();
						ty = spr.getTaille_x();
					}
					
					// Offset tricks
					int ajX = 0;
					int ajY = 0;
					if (spr.getEmptyBorders() != null) {
						Zone offsets = spr.getEmptyBorders();
						// Offset X are not adressed now
						/*
						if (!entity.reverse.isHorizontal()) {
							ajX -= (offsets.x1 +offsets.x2 )/2;
							ajX += offsets.x1;	// Shift because between [x,x1] this is an empty border
						} else {
							ajX -= (spr.getTaille_x()-offsets.x2+offsets.x1)/2;
						}
						*/
						ajY += offsets.y1;
					}
					entity.setScrX(entity.getAjustedX() + ajX - cameraNew.x - (tx >> 1));
					entity.setScrY(entity.getAjustedY() + ajY - cameraNew.y - ty);
				}
			}
		}

		// Iterate through every entities to synchronize data with vertex buffer
		// spriteEntities list order correspond to the creation order with spawn*** methods.
		spriteEngine.startInitialization();
		
		boolean displayBackSprite=ClientEngineZildo.spriteDisplay.foreBackController.isDisplayBackground();
		
		int indexEntity = 0;
		for (SpriteEntity entity : entities) {
			// Manage sprite in the sort array
			if (entity.isVisible() && (entity.isForeground() || displayBackSprite)) {
				// Add in the sort array
				spriteSorter.insertSpriteInSortArray(entity);
				// Add in vertices buffer
				//entity.setLinkVertices(indEntity[entity.getNBank()]*4);
				entity.setLinkVertices(indexEntity);
			}
			indexEntity++;
		}
		
		//long t1 = ZUtils.getTime();
		
		// Sort perso along the Y-axis
		spriteSorter.orderSpritesByBank();			// Fill the quadOrder and bankOrder arrays
		//long t2 = ZUtils.getTime();
		spriteEngine.buildBuffers(spriteSorter.getQuadOrder(), entities);

		//long t3 = ZUtils.getTime();
		
		spriteEngine.endInitialization();

		//System.out.println("update spriteDisplay : t2 = "+(t2-t1)+"ms t3="+(t3-t2)+"ms ");

	}

	public void clearSprites() {
		spriteEntities.clear();
		for (int i=0;i<arrayEntities.length;i++) {
			arrayEntities[i] = null;
		}
		Identified.resetCounter(SpriteEntity.class);
	}
	
	public int[][][] getBankOrder() {
		return spriteSorter.getBankOrder();
	}

	public SpriteEntity getZildo() {
		return zildoId == -1 ? null : arrayEntities[zildoId];
	}
	
	public void setZildoId(int p_zildoId) {
		zildoId=p_zildoId;
	}
}