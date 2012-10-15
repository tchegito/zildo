/**
 * Legend of Zildo
 * Copyright (C) 2006-2012 Evariste Boussaton
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

package zildo.platform.engine;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;

import zildo.client.ClientEngineZildo;
import zildo.fwk.bank.SpriteBank;
import zildo.fwk.gfx.EngineFX;
import zildo.fwk.gfx.GFXBasics;
import zildo.fwk.gfx.engine.SpriteEngine;
import zildo.fwk.gfx.engine.TextureEngine;
import zildo.monde.sprites.SpriteModel;
import zildo.monde.sprites.SpriteStore;
import zildo.monde.sprites.desc.Outfit;
import zildo.monde.util.Point;
import zildo.monde.util.Vector3f;
import zildo.monde.util.Vector4f;
import zildo.server.SpriteManagement;

// SpriteEngine.cpp: implementation of the SpriteEngine class.
//
//////////////////////////////////////////////////////////////////////





public class LwjglSpriteEngine extends SpriteEngine {
    
	public LwjglSpriteEngine(TextureEngine texEngine) {
		this.textureEngine = texEngine;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// render
	///////////////////////////////////////////////////////////////////////////////////////
	// Draw every sprite's primitives
	///////////////////////////////////////////////////////////////////////////////////////
	// IN: true=render BACKground
	//	   false=render FOREground
	///////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void render(boolean backGround) {
	
		// Display every sprites
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_BLEND);
		float[] color=textureEngine.graphicStuff.getFloat(GL11.GL_CURRENT_COLOR, 4);

		Vector3f ambient=ClientEngineZildo.ortho.getAmbientColor();
		Vector4f ambient4f = new Vector4f(1, 1, 1, 1);
		if (ambient != null) {
			color[0]=ambient.x;
			color[1]=ambient.y;
			color[2]=ambient.z;
			ambient4f = new Vector4f(color[0], color[1], color[2], 1);
		}
		// Respect order from bankOrder
		boolean endSequence=false;
		int posBankOrder=0;
	
		// Retrieve the sprite's order
		int[][] bankOrder = ClientEngineZildo.spriteDisplay.getBankOrder();
		
		int phase=(backGround)?0:1;
		while (!endSequence) {
			int numBank=bankOrder[phase][posBankOrder*4];
			if (numBank == -1) {
				endSequence=true;
			} else {
				// Render the n sprites from this bank
				int nbQuads=bankOrder[phase][posBankOrder*4 + 1];
				int iCurrentFX=bankOrder[phase][posBankOrder*4 + 2];
				int alpha=bankOrder[phase][posBankOrder*4 + 3];
				EngineFX currentFX=EngineFX.values()[iCurrentFX];
				int texId=textureEngine.getNthTexture(numBank);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);

				// Select the right pixel shader (if needed)
                if (pixelShaderSupported) {
                	switch (currentFX) {
                	case NO_EFFECT:
						ARBShaderObjects.glUseProgramObjectARB(0);
						break;
                	case PERSO_HURT:
						// A sprite has been hurt
						ARBShaderObjects.glUseProgramObjectARB(ClientEngineZildo.pixelShaders.getPixelShader(1));
						ClientEngineZildo.pixelShaders.setParameter(1, "randomColor", new Vector4f((float) Math.random(), (float) Math.random(), (float) Math.random(), 1));
						break;
					default:
						if (currentFX.needPixelShader()) {
							// This is a color replacement, so get the right ones
							Vector4f[] tabColors=ClientEngineZildo.pixelShaders.getConstantsForSpecialEffect(currentFX);
		
							// And enable the 'color replacement' pixel shader
							ARBShaderObjects.glUseProgramObjectARB(ClientEngineZildo.pixelShaders.getPixelShader(0));
							ClientEngineZildo.pixelShaders.setParameter(0, "Color1", tabColors[2]); //.scale(color[0]));
							ClientEngineZildo.pixelShaders.setParameter(0, "Color2", tabColors[3]); //.scale(color[0]));
							ClientEngineZildo.pixelShaders.setParameter(0, "Color3", tabColors[0]);
							ClientEngineZildo.pixelShaders.setParameter(0, "Color4", tabColors[1]);
							ClientEngineZildo.pixelShaders.setParameter(0, "curColor", ambient4f);
						} else {
							ARBShaderObjects.glUseProgramObjectARB(0);
						}
                	}
                }
                switch (currentFX) {
	                case SHINY:
	                    GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE); // _MINUS_SRC_ALPHA);
	                    GL11.glColor4f(1, (float) Math.random(), 0, (float) Math.random());
	                    break;
	                case QUAD:
	                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	                    GL11.glColor4f(0.5f + 0.5f * (float) Math.random(), 0.5f * (float) Math.random(), 0, 1);
	                    break;
	                case FOCUSED:
	                	GL11.glColor4f(1.0f, 1.0f, 1.0f, alpha / 255.0f);
	                	break;
	                default:
	                	color[3]=alpha / 255.0f;
	                	textureEngine.graphicStuff.setCurrentColor(color);
	                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                }
				meshSprites[numBank].render(nbQuads);
				posBankOrder++;
			}
		}

		// Deactivate pixel shader
		if (pixelShaderSupported) {
			ARBShaderObjects.glUseProgramObjectARB(0);
		}
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	
	@Override
	public void loadTextures(SpriteStore p_spriteStore) {
		// Load sprite banks
		textureEngine.init();
		for (int i = 0; i < SpriteManagement.sprBankName.length; i++) {
			SpriteBank sprBank = p_spriteStore.getSpriteBank(i);

			createModelsFromSpriteBank(sprBank);

			// Create a DirectX9 texture based on the current tiles
			createTextureFromSpriteBank(sprBank);
			//textureEngine.loadTexture("sprite"+i);
		}

		// Create Zildo with all outfits
		if (!ClientEngineZildo.editing) {
			// textureEngine.setCurentTexture(SpriteBank.BANK_ZILDOOUTFIT);
			// createTextureFromAnotherReplacement(SpriteBank.BANK_ZILDO,
			// ZildoOutfit.class);
		}

		// Prepare screen copy texture
		// textureTab[SpriteBank.BANK_COPYSCREEN]=generateTexture(0,64); //,
		// 1024); //, Zildo.viewPortY);
		// n_Texture++;
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
	private void createTextureFromSpriteBank(SpriteBank sBank) {
	
		GFXBasics surfaceGfx = textureEngine.prepareSurfaceForTexture(true);

		surfaceGfx.StartRendering();

		for (int n=0;n<sBank.getNSprite();n++)
		{
			SpriteModel spr=sBank.get_sprite(n);
			int longX=spr.getTaille_x();
			int longY=spr.getTaille_y();
			int x = spr.getTexPos_x();
			int y = spr.getTexPos_y();
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
						surfaceGfx.pset(i+x, j+y, a, replacedColor);
					}
				}
			}
		}
		//sBank.freeTempBuffer();
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
	private void createTextureFromAnotherReplacement(int p_originalTexture,
			Class<? extends Outfit> p_outfitClass) {

		GFXBasics surfaceGfx = textureEngine.prepareSurfaceForTexture(true);

		// 1) Store the color indexes once for all
		textureEngine.getTextureImage(p_originalTexture);
		Map<Integer, Integer> colorIndexes = new HashMap<Integer, Integer>();
		int i, j;
		for (j = 0; j < 256; j++) {
			for (i = 0; i < 256; i++) {
				Vector4f color = surfaceGfx.getPixel(i, j);
				if (color.w != 0) {
					colorIndexes
							.put(j * 256 + i, surfaceGfx.getPalIndex(color));
				}
			}
		}

		// 2) Create all textures according to the outfits
		boolean textureReady = true;
		Outfit[] outfits = p_outfitClass.getEnumConstants();
		for (Outfit outfit : outfits) {
			Point[] replacements = outfit.getTransforms();
			if (replacements.length == 0) {
				continue; // No replacements
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
			textureReady = false;
		}
	}
	
	@Override
	public void saveTextures() {
		textureEngine.saveAllTextures("sprite");
	}

}