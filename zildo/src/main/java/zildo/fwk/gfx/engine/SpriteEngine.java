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

package zildo.fwk.gfx.engine;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import zildo.Zildo;
import zildo.fwk.bank.SpriteBank;
import zildo.fwk.gfx.Occluder;
import zildo.fwk.gfx.primitive.SpritePrimitive;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.SpriteModel;
import zildo.monde.sprites.SpriteStore;
import zildo.monde.sprites.elements.Element;
import zildo.monde.util.Point;
import zildo.monde.util.Vector3f;
import zildo.monde.util.Zone;
import zildo.resource.Constantes;

// SpriteEngine.cpp: implementation of the SpriteEngine class.
//
//////////////////////////////////////////////////////////////////////




public abstract class SpriteEngine {

	// 3D Objects (vertices and indices per bank)
	protected SpritePrimitive meshSprites[]=new SpritePrimitive[Constantes.NB_SPRITEBANK];

    protected boolean pixelShaderSupported;
    
	protected TextureEngine textureEngine;

	protected final Vector3f peopleNameColor = new Vector3f(0.9f, 0.5f, 0.2f);

	//////////////////////////////////////////////////////////////////////
	// Construction/Destruction
	//////////////////////////////////////////////////////////////////////
	
	public SpriteEngine()
	{
        super();
		
        pixelShaderSupported = Zildo.pdPlugin.pixelShaders.canDoPixelShader();
    }
	
	public void init(SpriteStore p_spriteStore) {
		prepareSprites();
		loadTextures(p_spriteStore);
	}

	public void cleanUp()
	{
		for (SpritePrimitive sp : meshSprites) {
			sp.cleanUp();
		}
		textureEngine.cleanTextures();
	}

	public abstract void loadTextures(SpriteStore p_spriteStore);
	
	///////////////////////////////////////////////////////////////////////////////////////
	// prepareSprites
	///////////////////////////////////////////////////////////////////////////////////////
	// IN: Bank to transform into texture
	///////////////////////////////////////////////////////////////////////////////////////
	// Prepare vertices and indices for drawing sprites
	void prepareSprites() {
		// Allocate meshes
		for (int i=0;i<Constantes.NB_SPRITEBANK;i++) {
			meshSprites[i] = new SpritePrimitive(Constantes.NB_SPRITE_PER_PRIMITIVE*4);
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// addSprite
	///////////////////////////////////////////////////////////////////////////////////////
	// IN:  Entity to add into the primitive
	// OUT: Quad indice from the added entity
	///////////////////////////////////////////////////////////////////////////////////////
	// Ajoute un sprite dans les IB/VB
	// *On considère que StartInitialization a déjà été appelé*
	public void addSprite(SpriteEntity entity) {
		float z=0.0f;
		if (entity.getEntityType().isElement())
			z=((Element)entity).z;
	
		SpriteModel spr=entity.getSprModel();
		entity.setLinkVertices(
		meshSprites[entity.getNBank()].addSprite(entity.getScrX(),
											  entity.getScrY() - z,
											  spr.getTexPos_x(),
											  spr.getTexPos_y(),
											  spr.getTaille_x(),
											  spr.getTaille_y()));
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// synchronizeSprite
	///////////////////////////////////////////////////////////////////////////////////////
	// IN:  Entity to synchronize into the primitive
	///////////////////////////////////////////////////////////////////////////////////////
	// *On considère que StartInitialization a déjà été appelé*
	public void synchronizeSprite(SpriteEntity entity) {
	
		float z=0.0f;
		if (entity.getEntityType().isElement() ||
				entity.getEntityType().isPerso())
			z=entity.z;

		// Reverse attribute
		int revX = entity.reverse.isHorizontal() ? -1 : 1;
		int revY = entity.reverse.isVertical()   ? -1 : 1;
		
		SpriteModel spr=entity.getSprModel();
	
		boolean normalizeTex = entity.getSpecialEffect().isNormalizedTex();
		
		meshSprites[entity.getNBank()].synchronizeSprite(entity.getScrX(),
				  									entity.getScrY() - z,
				  									spr.getTexPos_x(),
				  									spr.getTexPos_y(),
				  									revX * spr.getTaille_x(),
				  									revY * spr.getTaille_y(), 
				  									entity.repeatX,
				  									entity.repeatY, 
													entity.rotation, entity.zoom,
													normalizeTex);
		
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// endInitialization
	///////////////////////////////////////////////////////////////////////////////////////
	public void endInitialization()
	{
		// Close meshes initialization
		for (int i=0;i<Constantes.NB_SPRITEBANK;i++) {
			meshSprites[i].endInitialization();
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// startInitialization
	///////////////////////////////////////////////////////////////////////////////////////
	public void startInitialization()
	{
		// Start meshes initialization
		for (int i=0;i<Constantes.NB_SPRITEBANK;i++) {
			meshSprites[i].startInitialization();
		}
	}
	
	public void initRendering() {
		// Initialize mesh the first half-time for background
		// Because foreground display will continue rendering just after it.
		for (int i=0;i<Constantes.NB_SPRITEBANK;i++)
			meshSprites[i].initRendering();
	}
	
	/**
	 * Build buffers as described in the received order sequence.
	 * Always -1 marks the end of sequence.
	 * @param quadOrder quad order (sorted by texture, and by Y-position), number of quad on each one
	 * @param entities
	 */
	public void buildBuffers(int[][] quadOrder, List<SpriteEntity> entities) {
		for (int i=0;i<Constantes.NB_SPRITEBANK;i++) {
			int[] quadOrderForOneBank=quadOrder[i];
			if (quadOrderForOneBank != null) {
				int j=0;
				while (true) {
			
					// Get the first quad's vertex
					int numQuad=quadOrderForOneBank[j];
					if (numQuad == -1)
						break;
					
					SpriteEntity entity = entities.get(numQuad);
					synchronizeSprite(entity);
					
					j++;
				}
			}
		}
	}
	
	
	/**
	 * Fills the {@link SpriteModel} objects with real texture coordinates (in range 0..256, 0..256) based on
	 * sizes from {@link SpriteBank} objects.
	 * @param sBank
	 */
	protected void createModelsFromSpriteBank(SpriteBank sBank) {
		int x=0,y=0,highestLine=0;


		// First pass to sort sprites on lower heights
		List<SpriteModel> modelSorted = new java.util.ArrayList<SpriteModel>();
		int n;
		for (n=0;n<sBank.getNSprite();n++) {
			SpriteModel spr=sBank.get_sprite(n);
			modelSorted.add(spr);
		}
		Collections.sort(modelSorted, new Comparator<SpriteModel>() {

			@Override
			public int compare(SpriteModel o1, SpriteModel o2) {
				return -Integer.valueOf(o1.getTaille_y()).compareTo(o2.getTaille_y());
			}
		});
		
		Occluder occ = new Occluder(256, 256);
		boolean withOccluder = false;
		for (SpriteModel spr : modelSorted) {
			int longX=spr.getTaille_x();
			int longY=spr.getTaille_y();
			// check for outer boundaries
			if ( (x+longX) > 256 ) {
				x=0;
				y+=highestLine;
				highestLine=0;
			}

			if (withOccluder || (y + longY) > 256) {
				// Needs occlusion to find some space inside the texture !
				Point p = occ.allocate(longX, longY);
				if (p == null) {
					//return;
					throw new RuntimeException("Unable to allocate "+longX+" x "+longY+" for "+sBank.getName()+" !");
				}
				x = p.x ; y = p.y;
				withOccluder = true;
			}
			occ.remove(new Zone(x, y, longX, longY));
			// Store sprite location on texture
			spr.setTexPos_x(x);
			spr.setTexPos_y(y); //+1);

			// Next position
			if (!withOccluder) {
				x+=longX;
				if (longY > highestLine)	// Mark the highest sprite on the row
					highestLine = longY;
			}
		}
	}
	
	public void saveTextures() {
		// Default : do nothing. Only LWJGL version can do that.
	}
	
	public abstract void render(int floor, boolean backGround);
}