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

package zildo.monde.sprites.utils;

import zildo.fwk.gfx.EngineFX;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.SpriteModel;
import zildo.resource.Constantes;

/**
 * Sprite sorter which deals with every entity displayed on screen.<p/>
 * 
 * First of all, we sort sprites by the Y axis.<br/>
 * Secondly, we break the list about 3 criteria :<ul>
 * <li>foreground / background</li>
 * <li>bank</li>
 * <li>special effect {@link EngineFX}</li>
 * </ul>
 * @author Tchegito
 *
 */
public class SpriteSorter {
	// Sorting by bank and by Y axis
	// bankOrder works like this { (BanqueN,i) , (BanqueM,j) , (BanqueP,k) ... }
	// [floor][0=BACKGROUND/1=FOREGROUND][nSprite]
	private int[][][] bankOrder;   // Reference to an int array from SpriteManagement
	
	// For Y-sorting
	protected SpriteEntity[][] tab_tri;
	private int[] num_spritesByLine;
	private int quadOrder[][];
	private int lastInBank[];
	
	private final int SORTY_MAX;
	private final int SORTY_REALMAX;
	
	public SpriteSorter(int p_sortYMax, int p_sortYRealMax) {
		
		SORTY_MAX = p_sortYMax;
		SORTY_REALMAX = p_sortYRealMax;
		
		tab_tri=new SpriteEntity[SORTY_REALMAX][Constantes.SORTY_ROW_PER_LINE];
		num_spritesByLine=new int[SORTY_REALMAX];
		
		// Clear really the sort array
		for (int i=0;i<SORTY_REALMAX;i++) {
			for (int j=0;j<Constantes.SORTY_ROW_PER_LINE;j++) {
				tab_tri[i][j] = null;
			}
		}	
		
		// Initialize structures associated with Y-sort
		quadOrder=new int[Constantes.NB_SPRITEBANK][Constantes.NB_SPRITE_PER_PRIMITIVE];
		lastInBank=new int[Constantes.NB_SPRITEBANK];
		bankOrder=new int[Constantes.TILEENGINE_FLOOR][2][4 * Constantes.MAX_SPRITES_ON_SCREEN];
	
		for (int fl=0;fl<bankOrder.length;fl++) {
			bankOrder[fl][0][0]=-1;	// Indicates no bank	
			bankOrder[fl][1][0]=-1;	// Indicates no bank	
		}
		clearEntirelySortArray();
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	// clearSortArray
	///////////////////////////////////////////////////////////////////////////////////////
	// Reset the sort array by setting to null the first column of each row
	///////////////////////////////////////////////////////////////////////////////////////
	public void clearSortArray()
	{
		clearEntirelySortArray();
		for (int i=0;i<SORTY_REALMAX;i++) {
			tab_tri[i][0]=null;
			num_spritesByLine[i]=0;
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
	// insertSpriteInSortArray
	///////////////////////////////////////////////////////////////////////////////////////
	// IN: sprite to insert
	///////////////////////////////////////////////////////////////////////////////////////
	// Declare a sprite at an Y position on screen
	///////////////////////////////////////////////////////////////////////////////////////
	public void insertSpriteInSortArray(SpriteEntity sprite)
	{
		// Get the character's Y to check if it's on the screen
		int y = sprite.getScrY();
		
		if (sprite.getEntityType().isFont()) {
			y=SORTY_MAX;
		} else {
			SpriteModel spr=sprite.getSprModel();
			if (!sprite.getEntityType().isEntity()) {
				// To get the right comparison, delete the adjustment done by updateSprites
				// just for filling the sort array
				int height = sprite.rotation.isWidthHeightSwitched() ? spr.getTaille_x() : spr.getTaille_y();
				y+=height - 3 + 2;
				// Adjustment for turtle: if squirrel jumps on it, it must be displayed before turtle
				// See TestSpriteDisplay unit tests for further information
				if (sprite.isZildo()) y+=sprite.z*2;
			} else {
				// Entity : make its always UNDER Zildo and other characters, at the same level
				// as the map tiles in fact.
				y=0;
			}
			if (!sprite.isInsideView()) {
				y = -1;	// Doesn't display it => it's outside the camera
			}
		}
	
		// Find a placement for the entity in the sort array
		// 1) Try all positions on a row
		// 2) Go the next row and do it again, until we reach the last one
		if (y>=0 && y<SORTY_REALMAX) {
			int position=0;
			while (y<SORTY_REALMAX && tab_tri[y][position] != null) {
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
				num_spritesByLine[y]++;
			}
		}
	}
	
	/**
	 * Fill the quadOrder and bankOrder arrays.<p/>
	 * 
	 * Read the sort array to build two new arrays :<ol>
	 * <li>quadOrder : quad orders sorted by bank and by increasing Y</li>
	 * <li>bankOrder : set of records from this model (numBank, nbQuads, FX)</li>
	 * </ol>
	 * It will be the set to look over in order to display the sprites correctly, in SpriteEngine.
	 */
	public void orderSpritesByBank() {
		// Initialize return array
		for (int nBank=0;nBank<Constantes.NB_SPRITEBANK;nBank++) {
			lastInBank[nBank]=0;
		}
	
		// Iterate through sort array
		//TODO: optimize this !!! Why do we iterate through each phase X each floor ?
		// Couldn't we iterate simply in tab_tri, and take what we find ? Whatever floor or phase ?
		// Answer => entity have to be ordered, that's precisely the goal of THIS method.
		for (int floor=0;floor<Constantes.TILEENGINE_FLOOR;floor++) {
			for (int phase=0;phase<2;phase++) {
				int bankOrderPosition=0;
				int currentBank=-1;
				int nbQuadFromSameBank=0;
				int currentAlpha=0;
				int currentLight=0;
				EngineFX currentFX=EngineFX.NO_EFFECT;
				for (int i=0;i<SORTY_REALMAX;i++) {
					int position=0;
					while (position < num_spritesByLine[i]) {
						SpriteEntity entity=tab_tri[i][position];
						if (entity == null)
							break;
						if (entity.getFloorForSort() == floor && (
								(!entity.isForeground() && phase==0) ||
								( entity.isForeground() && phase==1) ) ) {
							// We got an entity : store it into return array
							int linkVertices = entity.getLinkVertices();
							int nbEntity = entity.repeatX * entity.repeatY;
							// Repeat entity if its fields are asking to
							int last=lastInBank[entity.getNBank()]++;
							quadOrder[entity.getNBank()][last]=linkVertices;
							
							// Check if we need a special effect
							EngineFX persoFX=entity.getSpecialEffect();
		
							if ((currentBank != entity.getNBank() || persoFX != currentFX 
									|| entity.getAlpha() != currentAlpha || entity.getLight() != currentLight) && currentBank != -1) {
								// We got a break into sprite sequence display on the bank level
								bankOrder[floor][phase][bankOrderPosition*5]  =currentBank;
								bankOrder[floor][phase][bankOrderPosition*5+1]=nbQuadFromSameBank;
								bankOrder[floor][phase][bankOrderPosition*5+2]=currentFX.ordinal();
								bankOrder[floor][phase][bankOrderPosition*5+3]=currentAlpha;
								bankOrder[floor][phase][bankOrderPosition*5+4]=currentLight;
								
								bankOrderPosition++;
								nbQuadFromSameBank=0;
							}
							currentBank = entity.getNBank();
							currentFX = persoFX;
							currentAlpha = entity.getAlpha();
							currentLight = entity.getLight();
							nbQuadFromSameBank+=nbEntity;
						}
		
						position++;
					}
				}
				// Save the last build sequence
				bankOrder[floor][phase][bankOrderPosition*5]  =currentBank;
				bankOrder[floor][phase][bankOrderPosition*5+1]=nbQuadFromSameBank;
				bankOrder[floor][phase][bankOrderPosition*5+2]=currentFX.ordinal();
				bankOrder[floor][phase][bankOrderPosition*5+3]=currentAlpha;
				bankOrder[floor][phase][bankOrderPosition*5+4]=currentLight;
				// Mark the end of sequences
				bankOrder[floor][phase][bankOrderPosition*5+5]=-1;
			}
		}
		for (int b=0;b<Constantes.NB_SPRITEBANK;b++) {
			quadOrder[b][lastInBank[b]]=-1;
		}
	
	}
	public int[][][] getBankOrder() {
		return bankOrder;
	}
	
	public int[][] getQuadOrder() {
		return quadOrder;
	}
}
