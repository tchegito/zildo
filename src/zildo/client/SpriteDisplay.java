/**
 * Legend of Zildo
 * Copyright (C) 2006-2010 Evariste Boussaton
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
import zildo.fwk.bank.SpriteBank;
import zildo.fwk.gfx.PixelShaders.EngineFX;
import zildo.fwk.gfx.engine.SpriteEngine;
import zildo.monde.map.Point;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.SpriteModel;
import zildo.monde.sprites.SpriteStore;
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
	// For Y-sorting
	private SpriteEntity[][] tab_tri;
	private int quadOrder[][];
	private int lastInBank[];
	private int bankOrder[][];
	// bankOrder works like this { (BanqueN,i) , (BanqueM,j) , (BanqueP,k) ... }

	public int zildoId;

	private int SORTY_MAX;
	private int SORTY_REALMAX;
	
	// We use a map to ease the access to an entity with his ID
	Map<Integer, SpriteEntity> mapEntities=new HashMap<Integer, SpriteEntity>();
	
	SpriteEntity previousMapSprite;
	
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
		
		if (Client.isZEditor()) {
			SORTY_MAX=ZildoScrollablePanel.viewSizeY+40;
			SORTY_REALMAX=ZildoScrollablePanel.viewSizeY+80;
		} else {
			SORTY_MAX=Constantes.SORTY_MAX;
			SORTY_REALMAX=Constantes.SORTY_REALMAX;
		}
		tab_tri=new SpriteEntity[SORTY_REALMAX][Constantes.SORTY_ROW_PER_LINE];
		
		// Clear really the sort array
		for (int i=0;i<SORTY_REALMAX;i++) {
			for (int j=0;j<Constantes.SORTY_ROW_PER_LINE;j++) {
				tab_tri[i][j] = null;
			}
		}
		
		// Initialize structures associated with Y-sort
	
		quadOrder=new int[Constantes.NB_SPRITEBANK][Constantes.NB_SPRITE_PER_PRIMITIVE];
		lastInBank=new int[Constantes.NB_SPRITEBANK];
		bankOrder=new int[2][3 * Constantes.MAX_SPRITES_ON_SCREEN];
	
		bankOrder[0][0]=-1;	// Indicates no bank	
		bankOrder[1][0]=-1;	// Indicates no bank	
	
		clearEntirelySortArray();
	}
	
	/**
	 * -insert sprites into sort array
	 * @param cameraXnew
	 * @param cameraYnew
	 */
	public void updateSpritesClient(Point cameraNew) {
		
		// Reset the sort array used for sprites
		clearSortArray();
		
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
					entity.setScrX((int) (entity.x - cameraNew.x));
					entity.setScrY((int) (entity.y - cameraNew.y));
				} else if (entity.getEntityType()==SpriteEntity.ENTITYTYPE_ELEMENT) {
					// Center sprite
					SpriteModel spr=entity.getSprModel();
					entity.setScrX(entity.getAjustedX() - cameraNew.x - (spr.getTaille_x() >> 1));
					entity.setScrY(entity.getAjustedY() - cameraNew.y - spr.getTaille_y());
				} else if (entity.isZildo() && ClientEngineZildo.mapDisplay.isCapturing()) {
					entity.setVisible(false);
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
				insertSpriteInSortArray(entity);
				// Add in vertices buffer
				spriteEngine.synchronizeSprite(entity);
			}
		}
		
		spriteEngine.endInitialization();
		
		// Sort perso along the Y-axis
		orderSpritesByBank();			// Fill the quadOrder and bankOrder arrays
		spriteEngine.buildIndexBuffers(quadOrder);
		spriteEngine.setBankOrder(bankOrder);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// insertSpriteInSortArray
	///////////////////////////////////////////////////////////////////////////////////////
	// IN: sprite to insert
	///////////////////////////////////////////////////////////////////////////////////////
	// Declare a sprite at an Y position on screen
	///////////////////////////////////////////////////////////////////////////////////////
	void insertSpriteInSortArray(SpriteEntity sprite)
	{
		// Get the character's Y to check if it's on the screen
		int y=sprite.getScrY();
		if (sprite.getEntityType()==SpriteEntity.ENTITYTYPE_FONT) {
			y=SORTY_MAX;
		} else if (sprite.getEntityType()!=SpriteEntity.ENTITYTYPE_ENTITY) {
			// To get the right comparison, delete the adjustment done by updateSprites
			// just for filling the sort array
			SpriteModel spr=sprite.getSprModel();
			y+=spr.getTaille_y() - 3;
		} else {
			// Entity : make its always UNDER Zildo and other characters, at the same level
			// as the map tiles in fact.
			y=0;
		}
	
		// Find a placement for the entity in the sort array
		// 1) Try all positions on a row
		// 2) Go the next row and do it again, until we reach the last one
		if (y>=0 && y<SORTY_REALMAX) {
			int position=0;
			while (tab_tri[y][position] != null && y<SORTY_REALMAX) {
				position++;
				if (position==Constantes.SORTY_ROW_PER_LINE) {
					y++;
					position=0;
				}
			}
			if (y<SORTY_REALMAX && position < Constantes.SORTY_ROW_PER_LINE) {
				// Declare sprite at the right position
				tab_tri[y][position] = sprite;
				if (position < Constantes.SORTY_ROW_PER_LINE-1)
					tab_tri[y][position+1]=null;
			}
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// clearSortArray
	///////////////////////////////////////////////////////////////////////////////////////
	// Reset the sort array by setting to null the first column of each row
	///////////////////////////////////////////////////////////////////////////////////////
	void clearSortArray()
	{
		for (int i=0;i<SORTY_REALMAX;i++) {
			tab_tri[i][0]=null;
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// clearEntirelySortArray
	///////////////////////////////////////////////////////////////////////////////////////
	// Reset the sort array by setting to null each column of each row
	///////////////////////////////////////////////////////////////////////////////////////
	void clearEntirelySortArray()
	{
		for (int i=0;i<SORTY_REALMAX;i++) {
			for (int j=0;j<Constantes.SORTY_ROW_PER_LINE;j++) {
				tab_tri[i][j]=null;
			}
		}
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	// orderSpritesByBank
	///////////////////////////////////////////////////////////////////////////////////////
	// IN : nothing
	// OUT : array[nBank][nSprite] of resulting ordered sprites
	///////////////////////////////////////////////////////////////////////////////////////
	// Read the sort array to build two new arrays :
	// -quadOrder : quad orders sorted by bank and by increasing Y
	// -bankOrder : set of records from this model 
	//				(numBank, nbQuads)
	//              It will be the set to look over in order to display the sprites
	//				correctly, in SpriteEngine.
	///////////////////////////////////////////////////////////////////////////////////////
	void orderSpritesByBank() {
		// Initialize return array
		for (int nBank=0;nBank<Constantes.NB_SPRITEBANK;nBank++) {
			lastInBank[nBank]=0;
		}
	
		// Iterate through sort array
		for (int phase=0;phase<2;phase++) {
			int bankOrderPosition=0;
			int currentBank=-1;
			int nbQuadFromSameBank=0;
			EngineFX currentFX=EngineFX.NO_EFFECT;
			for (int i=0;i<SORTY_REALMAX;i++) {
				int position=0;
				while (position < Constantes.SORTY_ROW_PER_LINE) {
					SpriteEntity entity=tab_tri[i][position];
					if (entity == null)
						break;
					if ((!entity.isForeground() && phase==0) ||
						( entity.isForeground() && phase==1)) {
						// We got an entity : store it into return array
						int last=lastInBank[entity.getNBank()]++;
						quadOrder[entity.getNBank()][last]=entity.getLinkVertices();
						
						// Check if we need a special effect
						EngineFX persoFX=entity.getSpecialEffect();
	
						if ((currentBank != entity.getNBank() || persoFX != currentFX) && currentBank != -1) {
							// We got a break into sprite sequence display on the bank level
							bankOrder[phase][bankOrderPosition*3]  =currentBank;
							bankOrder[phase][bankOrderPosition*3+1]=nbQuadFromSameBank;
							bankOrder[phase][bankOrderPosition*3+2]=currentFX.ordinal();
							bankOrderPosition++;
							nbQuadFromSameBank=0;
						}
						currentBank = entity.getNBank();
						currentFX = persoFX;
						nbQuadFromSameBank++;
					}
	
					position++;
				}
			}
			// Save the last build sequence
			bankOrder[phase][bankOrderPosition*3]  =currentBank;
			bankOrder[phase][bankOrderPosition*3+1]=nbQuadFromSameBank;
			bankOrder[phase][bankOrderPosition*3+2]=currentFX.ordinal();
			// Mark the end of sequences
			bankOrder[phase][bankOrderPosition*3+3]=-1;
		}
		for (int b=0;b<Constantes.NB_SPRITEBANK;b++) {
			quadOrder[b][lastInBank[b]]=-1;
		}
	
	}

	public SpriteEntity getZildo() {
		return mapEntities.get(zildoId);
	}
	
	public void setZildoId(int p_zildoId) {
		zildoId=p_zildoId;
	}
	
	public void displayPreviousMap(Point p_cameraLocation) {

		if (previousMapSprite != null) {
			deleteSprite(previousMapSprite);
		}
		previousMapSprite=new SpriteEntity(p_cameraLocation.x, p_cameraLocation.y, false);
		previousMapSprite.setNSpr(0);
		previousMapSprite.setNBank(SpriteBank.BANK_COPYSCREEN);
		previousMapSprite.setVisible(true);
		spawnSprite(previousMapSprite);		
	}
}