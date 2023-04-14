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

package zildo.platform.engine;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;

import zildo.client.ClientEngineZildo;
import zildo.fwk.gfx.EngineFX;
import zildo.fwk.gfx.engine.SpriteEngine;
import zildo.fwk.gfx.engine.TextureEngine;
import zildo.monde.sprites.SpriteStore;
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
	
	float gamma;
	
	///////////////////////////////////////////////////////////////////////////////////////
	// render
	///////////////////////////////////////////////////////////////////////////////////////
	// Draw every sprite's primitives
	///////////////////////////////////////////////////////////////////////////////////////
	// IN: true=render BACKground
	//	   false=render FOREground
	///////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void render(int floor, boolean backGround) {
	
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
		int[][][] bankOrder = ClientEngineZildo.spriteDisplay.getBankOrder();
		
		int phase=(backGround)?0:1;
		while (!endSequence) {
			int numBank=bankOrder[floor][phase][posBankOrder*5];
			if (numBank == -1) {
				endSequence=true;
			} else {
				// Render the n sprites from this bank
				int nbQuads=bankOrder[floor][phase][posBankOrder*5 + 1];
				int iCurrentFX=bankOrder[floor][phase][posBankOrder*5 + 2];
				int alpha=bankOrder[floor][phase][posBankOrder*5 + 3];
				int light=bankOrder[floor][phase][posBankOrder*5 + 4];
				EngineFX currentFX=EngineFX.values()[iCurrentFX];
				int texId=textureEngine.getNthTexture(numBank);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);

				ambient4f.w = alpha / 256f;
				// Select the right pixel shader (if needed)
                if (pixelShaderSupported) {
                	switch (currentFX) {
                	case NO_EFFECT:
						ARBShaderObjects.glUseProgramObjectARB(0);
						break;
                	case PERSO_HURT:
						// A sprite has been hurt
						ARBShaderObjects.glUseProgramObjectARB(ClientEngineZildo.pixelShaders.getPixelShader(1));
						ClientEngineZildo.pixelShaders.setParameter(1, "randomColor", randomVector);
						break;
                	case YELLOW_HALO:
						ARBShaderObjects.glUseProgramObjectARB(ClientEngineZildo.pixelShaders.getPixelShader(2));
						ClientEngineZildo.pixelShaders.setParameter(2, "factor", new Vector4f((float) (0.6+0.4*Math.cos(3*gamma)), 0, 0, alpha / 255f));
						break;
                	case STAR:
						ARBShaderObjects.glUseProgramObjectARB(ClientEngineZildo.pixelShaders.getPixelShader(3));
						ClientEngineZildo.pixelShaders.setParameter(5, "curColor", ambient4f);
						ClientEngineZildo.pixelShaders.setParameter(3, "noise", new Vector4f(gamma, (float) Math.random(), 0, 1));
						break;
                	case CLIP:
	                case FONT_PEOPLENAME:
						ARBShaderObjects.glUseProgramObjectARB(ClientEngineZildo.pixelShaders.getPixelShader(5));
						ClientEngineZildo.pixelShaders.setParameter(5, "curColor", ambient4f);
						break;
                	case FIRE:
						ARBShaderObjects.glUseProgramObjectARB(ClientEngineZildo.pixelShaders.getPixelShader(4));
						//ClientEngineZildo.pixelShaders.setParameter(4, "iResolution", new Vector4f(Zildo.viewPortX, Zildo.viewPortY, 0, 0));
						ClientEngineZildo.pixelShaders.setParameter(4, "iGlobalTime", new Vector4f(gamma, 0, 0, 0));
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
	                    //GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	                    //GL11.glColor4f(0.5f + 0.5f * (float) Math.random(), 0.5f * (float) Math.random(), 0, 1);
	                    GL11.glColor4f(0.7f, 0.6f, 0.8f, 1);
	                    break;
	                    /*
	                case WHITE_HALO:
	                	float r = (float) (0.6 + 0.4 * Math.random());
	                	GL11.glColor4f(r, 0, 0, 1);
	                    GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE_MINUS_SRC_ALPHA);
	                	break;
	                	*/
	                case FOCUSED:
	                	GL11.glColor4f(1.0f, 1.0f, 1.0f, alpha / 255.0f);
	                	break;
	                case FONT_PEOPLENAME:
	                	Vector4f v = new Vector4f(peopleNameColor, alpha / 255.0f);
						ClientEngineZildo.pixelShaders.setParameter(5, "curColor", v);
	                	break;
	                case INFO:
	                	GL11.glColor4f(0.9f, 0.8f, 0.72f, alpha / 255.0f);
	                	break;
	                default:
	                	color[3]=alpha / 255.0f;
	                	textureEngine.graphicStuff.setCurrentColor(color);
	                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                }
                if (light != 0x00ffffff) {
                	byte lightRed = (byte) (light >> 16);
                	byte lightGreen = (byte) ((light >> 8) & 255);
                	byte lightBlue = (byte) (light & 255);
            		GL11.glColor4ub(lightRed, lightGreen, lightBlue, (byte) alpha);
            		//GL11.glColor4ub((byte)255, (byte)1, (byte)1, (byte) (255.0f));
            		//GL11.glColor4f(1f, 1f, 0f, 1f);
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
		
		gamma += 0.01f;
	}
	
	
	@Override
	public void loadTextures(SpriteStore p_spriteStore) {
		// Load sprite banks
		textureEngine.init();
		for (int i = 0; i < SpriteManagement.sprBankName.length; i++) {
			textureEngine.loadTexture("sprite"+i);
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
	

}