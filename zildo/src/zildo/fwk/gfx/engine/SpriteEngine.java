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

package zildo.fwk.gfx.engine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zildo.Zildo;
import zildo.client.ClientEngineZildo;
import zildo.fwk.bank.SpriteBank;
import zildo.fwk.gfx.GFXBasics;
import zildo.fwk.gfx.primitive.SpritePrimitive;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.SpriteModel;
import zildo.monde.sprites.SpriteStore;
import zildo.monde.sprites.desc.Outfit;
import zildo.monde.sprites.elements.Element;
import zildo.monde.util.Point;
import zildo.monde.util.Vector4f;
import zildo.resource.Constantes;
import zildo.server.SpriteManagement;

// SpriteEngine.cpp: implementation of the SpriteEngine class.
//
//////////////////////////////////////////////////////////////////////





public abstract class SpriteEngine {

	// 3D Objects (vertices and indices per bank)
	protected SpritePrimitive meshSprites[]=new SpritePrimitive[Constantes.NB_SPRITEBANK];

    protected boolean pixelShaderSupported;
    
	protected TextureEngine textureEngine;
	
	//////////////////////////////////////////////////////////////////////
	// Construction/Destruction
	//////////////////////////////////////////////////////////////////////
	
	public SpriteEngine()
	{
        super();
		
        pixelShaderSupported = Zildo.pdPlugin.pixelShaders.canDoPixelShader();
    }
	
	public void init(SpriteStore p_spriteStore) {
		prepareSprites(p_spriteStore);
	}

	public void cleanUp()
	{
		for (SpritePrimitive sp : meshSprites) {
			sp.cleanUp();
		}
		textureEngine.cleanTextures();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// createTextureFromSpriteBank
	///////////////////////////////////////////////////////////////////////////////////////
	// IN: Bank to transform into texture
	///////////////////////////////////////////////////////////////////////////////////////
	// Create a Direct3DTexture9 object from a sprite bank. Every sprite is added at the
	// right side of the previous one. If it is too long, we shift to next line, which is
	// calculated as the highest sprite on the line. And so on.
	// Here we use a GFXBasics object to gain in readability and comfort. This one will be deleted
	// at the end, indeed. So we can say this is a beautiful method.
	public void createTextureFromSpriteBank(SpriteBank sBank) {
	
		GFXBasics surfaceGfx = textureEngine.prepareSurfaceForTexture(true);

		surfaceGfx.StartRendering();
		surfaceGfx.box(0,0,320,256,32,new Vector4f(0,0,0,0));
	
		int x=0,y=0,highestLine=0;
		for (int n=0;n<sBank.getNSprite();n++)
		{
			SpriteModel spr=sBank.get_sprite(n);
			int longX=spr.getTaille_x();
			int longY=spr.getTaille_y();
			// Test de dépassement sur la texture
			if ( (x+longX) > 256 ) {
				x=0;
				y+=highestLine;
				highestLine=0;
			}
			// On stocke la position du sprite sur la texture
			spr.setTexPos_x(x);
			spr.setTexPos_y(y); //+1);
			// On place le sprite sur la texture
			short[] motif=sBank.getSpriteGfx(n);
			Vector4f replacedColor;
			for (int j=0;j< longY;j++) {
				
				for (int i=0;i< longX;i++)
				{
					int a=motif[i+j*longX];
					if (a!=255)
					{
						// Regular size
						long modifiedColor=-1;
						if (pixelShaderSupported) {
							modifiedColor=sBank.modifyPixel(n,a);
						}
						replacedColor=modifiedColor==-1?null:textureEngine.graphicStuff.createColor(modifiedColor);
						surfaceGfx.pset(i+x,j+y,a,replacedColor);
					}
				}
			}

			// Next position
			x+=longX;
			if (longY > highestLine)	// Mark the highest sprite on the row
				highestLine = longY;
		}
		sBank.freeTempBuffer();
		textureEngine.generateTexture();
	}
	
    /**
     * Create a new texture from a given one, and replace colors as specified by
     * the {@link Outfit} class.<br/>
     * 
     * @param p_originalTexture
     * @param p_replacements
     *            list of replacements : for a point (x,y), color-index <b>x</b>
     *            become color-index <b>y</b>.
     */
    public void createTextureFromAnotherReplacement(int p_originalTexture,
	    Class<? extends Outfit> p_outfitClass) {

	GFXBasics surfaceGfx = textureEngine.prepareSurfaceForTexture(true);

	// 1) Store the color indexes once for all
	textureEngine.getTextureImage(textureEngine.getNthTexture(p_originalTexture));
	Map<Integer, Integer> colorIndexes = new HashMap<Integer, Integer>();
	int i, j;
	for (j = 0; j < 256; j++) {
	    for (i = 0; i < 256; i++) {
		Vector4f color = surfaceGfx.getPixel(i, j);
		if (color.w != 0) {
		    colorIndexes.put(j * 256 + i, surfaceGfx.getPalIndex(color));
		}
	    }
	}

	// 2) Create all textures according to the outfits
	boolean textureReady=true;
	Outfit[] outfits = p_outfitClass.getEnumConstants();
	for (Outfit outfit : outfits) {
	    Point[] replacements = outfit.getTransforms();
	    if (replacements.length == 0) {
		continue;	// No replacements
	    }
	    if (!textureReady) {
		surfaceGfx = textureEngine.prepareSurfaceForTexture(true);
	    }
	    surfaceGfx.StartRendering();
	    for (j = 0; j < 256; j++) {
		for (i = 0; i < 256; i++) {
		    Integer palIndex = colorIndexes.get(j * 256 + i);
		    if (palIndex != null) {
			for (Point p : replacements) {
			    if (palIndex == p.x) {
				surfaceGfx.pset(i, j, p.y, null);
			    }
			}
		    }
		}
	    }

	    textureEngine.generateTexture();
	    textureReady=false;
	}
    }
	
	///////////////////////////////////////////////////////////////////////////////////////
	// prepareSprites
	///////////////////////////////////////////////////////////////////////////////////////
	// IN: Bank to transform into texture
	///////////////////////////////////////////////////////////////////////////////////////
	// Prepare vertices and indices for drawing sprites
	void prepareSprites(SpriteStore p_spriteStore) {
		int i;
	
		// Allocate meshes
		for (i=0;i<Constantes.NB_SPRITEBANK;i++) {
			if (i==SpriteBank.BANK_COPYSCREEN) {
				meshSprites[i] = new SpritePrimitive(4, 6, 512, 256);
			} else {
				meshSprites[i] = new SpritePrimitive(Constantes.NB_SPRITE_PER_PRIMITIVE*4);
			}
		}
		// Load sprite banks
		for (i=0;i<SpriteManagement.sprBankName.length;i++) {
			SpriteBank sprBank=p_spriteStore.getSpriteBank(i);

			// Create a DirectX9 texture based on the current tiles
			createTextureFromSpriteBank(sprBank);
		}
		
		// Create Zildo with all outfits
		if (!ClientEngineZildo.editing) {
			//textureEngine.setCurentTexture(SpriteBank.BANK_ZILDOOUTFIT);
			//createTextureFromAnotherReplacement(SpriteBank.BANK_ZILDO, ZildoOutfit.class);
		}
		
		// Prepare screen copy texture
		//textureTab[SpriteBank.BANK_COPYSCREEN]=generateTexture(0,64); //, 1024); //, Zildo.viewPortY);
		//n_Texture++;
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
	
		meshSprites[entity.getNBank()].synchronizeSprite(entity.getScrX(),
				  									entity.getScrY() - z,
				  									spr.getTexPos_x(),
				  									spr.getTexPos_y(),
				  									revX * spr.getTaille_x(),
				  									revY * spr.getTaille_y(), 
				  									entity.repeatX,
				  									entity.repeatY, 
													entity.rotation);
		
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
	 * Capture screen before map scroll.
	 */
	public void captureScreen() {
		//saveScreen(textureTab[SpriteBank.BANK_COPYSCREEN]);
	}
	
	public abstract void render(boolean backGround);
}