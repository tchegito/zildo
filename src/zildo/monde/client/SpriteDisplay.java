package zildo.monde.client;

import java.util.List;

import zildo.fwk.gfx.PixelShaders;
import zildo.fwk.gfx.engine.SpriteEngine;
import zildo.monde.SpriteModel;
import zildo.monde.decors.Element;
import zildo.monde.decors.SpriteEntity;
import zildo.monde.persos.Perso;
import zildo.prefs.Constantes;

/**
 * All client side sprite problematic.
 *
 * Here, we take sprites positions, attributes, and so on, to display them correctly on the device.
 *
 * This class acts as a layer between SpriteManagement and SpriteEngine.
 *
 * We should work only on SpriteEntity here, because SpriteManagament do all the calculating jobs, about speed, acceleration, and
 * here, we just have to display a sprite at a given position. So, it remains one task to do:
 * TODO: Remove all references to Element or Perso classes. We just have to use scrX and scrY to localize sprite.
 *
 * @author tchegito
 *
 */
public class SpriteDisplay {

	SpriteEngine spriteEngine;
	// For Y-sorting
	private SpriteEntity[][] tab_tri=new SpriteEntity[Constantes.SORTY_REALMAX]
	                                                 [Constantes.SORTY_ROW_PER_LINE];
	private int quadOrder[][];
	private int lastInBank[];
	private int bankOrder[][];
	
	private boolean fillingMeshes;	// TRUE=We actually are in 'updateSprites', so it's the right time to spawn new sprite

	private int camerax;
	private int cameray;
	
	public SpriteDisplay(SpriteEngine spriteEngine) {
		this.spriteEngine=spriteEngine;
		
		
		// Clear really the sort array
		for (int i=0;i<Constantes.SORTY_REALMAX;i++) {
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
	
	public void updateSpritesClient(List<SpriteEntity> spriteEntities, int cameraXnew, int cameraYnew) {
		
		// Reset the sort array used for sprites
		clearSortArray();
		fillingMeshes=true;
		
		// Calculate camera diff
		int diffx=cameraXnew - camerax;
		int diffy=cameraYnew - cameray;
		
		// Do perso animations
		// Mandatory to do that first, because one perso can be connected to other sprites
		for (SpriteEntity entity : spriteEntities) {
			if (entity.getEntityType() == SpriteEntity.ENTITYTYPE_PERSO) {
				Perso perso=(Perso)entity;
				// Camera moves
				perso.setScrX ( perso.getAjustedX() - cameraXnew);
				perso.setScrY ( perso.getAjustedY() - cameraYnew);
				// Get sprite model
				SpriteModel spr=perso.getSprModel();
				if (!perso.isZildo()) {
					// Non-zildo sprite haven't same way to display correctly (bad...)
					perso.setScrX(perso.getScrX() - (spr.getTaille_x() >> 1) );
					perso.setScrY(perso.getScrY() - (spr.getTaille_y() - 3) );
				}
				perso.manageCollision();
			}
		}
		
		for (SpriteEntity entity : spriteEntities) {
			if (entity != null) {
				// Camera moves
				if (entity.getEntityType()==SpriteEntity.ENTITYTYPE_ENTITY) {
					// We have any x,y coordinates so just calculate a differential
					entity.setScrX((int) (entity.getScrX() - (float)diffx));
					entity.setScrY((int) (entity.getScrY() - (float)diffy));
				} else if (entity.getEntityType()==SpriteEntity.ENTITYTYPE_ELEMENT) {
					Element element = (Element)entity;
					entity.setScrX ((int) ( element.x - cameraXnew));
					entity.setScrY ((int) ( element.y - cameraYnew));
					// Center sprite
					SpriteModel spr=entity.getSprModel();
					entity.setScrX(entity.getScrX() - (spr.getTaille_x() >> 1));
					entity.setScrY(entity.getScrY() +  3-spr.getTaille_y());
				}
			}
		}
		
		// Iterate through every entities to synchronize data with vertex buffer
		// spriteEntities list order correspond to the creation order with spawn*** methods.
		spriteEngine.startInitialization();
		
		for (SpriteEntity entity : spriteEntities) {
			// Manage sprite in the sort array
			if (entity.isVisible()) {
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
		
		fillingMeshes=false;
		
		camerax=cameraXnew;
		cameray=cameraYnew;
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
			y=Constantes.SORTY_MAX;
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
		if (y>=0 && y<Constantes.SORTY_REALMAX) {
			int position=0;
			while (tab_tri[y][position] != null && y<Constantes.SORTY_REALMAX) {
				position++;
				if (position==Constantes.SORTY_ROW_PER_LINE) {
					y++;
					position=0;
				}
			}
			if (y<Constantes.SORTY_REALMAX && position < Constantes.SORTY_ROW_PER_LINE) {
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
		for (int i=0;i<Constantes.SORTY_REALMAX;i++) {
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
		for (int i=0;i<Constantes.SORTY_REALMAX;i++) {
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
			int currentFX=PixelShaders.ENGINEFX_NO_EFFECT;
			for (int i=0;i<Constantes.SORTY_REALMAX;i++) {
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
						int persoFX=entity.getSpecialEffect();
	
						if ((currentBank != entity.getNBank() || persoFX != currentFX) && currentBank != -1) {
							// We got a break into sprite sequence display on the bank level
							bankOrder[phase][bankOrderPosition*3]  =currentBank;
							bankOrder[phase][bankOrderPosition*3+1]=nbQuadFromSameBank;
							bankOrder[phase][bankOrderPosition*3+2]=currentFX;;
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
			bankOrder[phase][bankOrderPosition*3+2]=currentFX;;
			// Mark the end of sequences
			bankOrder[phase][bankOrderPosition*3+3]=-1;
		}
		for (int b=0;b<Constantes.NB_SPRITEBANK;b++) {
			quadOrder[b][lastInBank[b]]=-1;
		}
	
	}

}
