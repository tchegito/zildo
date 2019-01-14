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

import shader.Shaders;
import shader.Shaders.GLShaders;
import zildo.client.ClientEngineZildo;
import zildo.fwk.gfx.EngineFX;
import zildo.fwk.gfx.engine.SpriteEngine;
import zildo.fwk.gfx.engine.TextureEngine;
import zildo.monde.sprites.SpriteStore;
import zildo.monde.util.Vector3f;
import zildo.monde.util.Vector4f;
import zildo.platform.opengl.AndroidPixelShaders;
import zildo.server.SpriteManagement;
import android.opengl.GLES20;

// SpriteEngine.cpp: implementation of the SpriteEngine class.
//
//////////////////////////////////////////////////////////////////////





public class AndroidSpriteEngine extends SpriteEngine {
    
	Shaders shaders;
	
	public AndroidSpriteEngine(TextureEngine texEngine) {
		this.textureEngine = texEngine;
		shaders = AndroidPixelShaders.shaders;
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
		GLES20.glEnable(GLES20.GL_BLEND);
		
		float[] color = new float[4]; //=textureEngine.graphicStuff.getFloat(GL11.GL_CURRENT_COLOR, 4);
		color[0]=1f;
		color[1]=1f;
		color[2]=1f;
		color[3]=1f;
		
		Vector3f ambient=ClientEngineZildo.ortho.getAmbientColor();
		if (ambient != null) {
			color[0]=ambient.x;
			color[1]=ambient.y;
			color[2]=ambient.z;
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
				GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
				GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);

				// Rest clipping
            	shaders.setClip(false);
            	
				// Select the right pixel shader (if needed)
                if (pixelShaderSupported) {
                	switch (currentFX) {
                	case NO_EFFECT:
						shaders.setCurrentShader(GLShaders.textured);
						break;
                	case PERSO_HURT:
						// A sprite has been hurt
						shaders.setCurrentShader(GLShaders.wounded);
						shaders.setWoundedColor(new Vector4f((float) Math.random(), (float) Math.random(), (float) Math.random(), 1));
                		/*
						ARBShaderObjects.glUseProgramObjectARB(ClientEngineZildo.pixelShaders.getPixelShader(1));
						ClientEngineZildo.pixelShaders.setParameter(1, "randomColor", );
						*/
						break;
	                case YELLOW_HALO:
	                	shaders.setCurrentShader(GLShaders.goldFilter);
	                	shaders.setGoldFactor((float) (0.6+0.4*Math.cos(3*gamma)));
	                	break;
	                case CLIP:
	                case FONT_PEOPLENAME:
						shaders.setCurrentShader(GLShaders.textured);
	                	shaders.setClip(true);	// Always clip with "font_peoplename"
	                	break;
	                case STAR:
	                	shaders.setCurrentShader(GLShaders.star);
	                	shaders.setStarNoise(gamma, (float) Math.random());
	                	break;
	                case FIRE:
	                	shaders.setCurrentShader(GLShaders.fire);
	                	shaders.setFireTime(new Vector4f(gamma, 0, 0, 0));
	                	break;
					default:
						if (currentFX.needPixelShader()) {
							// This is a color replacement, so get the right ones
							Vector4f[] tabColors=ClientEngineZildo.pixelShaders.getConstantsForSpecialEffect(currentFX);
		
							// And enable the 'color replacement' pixel shader
							shaders.setCurrentShader(GLShaders.switchColor);
							shaders.setSwitchColors(tabColors);
							/*
							ClientEngineZildo.pixelShaders.setParameter(0, "Color1", tabColors[2]);
							ClientEngineZildo.pixelShaders.setParameter(0, "Color2", tabColors[3]);
							ClientEngineZildo.pixelShaders.setParameter(0, "Color3", tabColors[0].scale(color[0]));
							ClientEngineZildo.pixelShaders.setParameter(0, "Color4", tabColors[1].scale(color[0]));
							*/
						} else {
							shaders.setCurrentShader(GLShaders.textured);
						}
                	}
                }
                
                switch (currentFX) {
	                case SHINY:
	                	GLES20.glBlendFunc(GLES20.GL_SRC_COLOR, GLES20.GL_ONE); // _MINUS_SRC_ALPHA);
	                	shaders.setColor(1, (float) Math.random(), 0, (float) Math.random());
	                    break;
	                case QUAD:
	                	GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
	                	shaders.setColor(0.5f + 0.5f * (float) Math.random(), 0.5f * (float) Math.random(), 0, 1);
	                    break;
	                case FOCUSED:
	            		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
	                	shaders.setColor(1.0f, 1.0f, 1.0f, alpha / 255.0f);
	                	break;
	                case FONT_PEOPLENAME:
	            		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
	            		shaders.setColor(new Vector4f(peopleNameColor, alpha / 255.0f));
	                	break;
	                default:
	                	color[3]=alpha / 255.0f;
	                    if (light != 0x00ffffff) {
	                    	int lightRed = (int) (light >> 16);
	                    	int lightGreen = (int) ((light >> 8) & 255);
	                    	int lightBlue = (int) (light & 255);
		                	shaders.setColor(lightRed / 255.0f, lightGreen / 255.0f, lightBlue / 255.0f, color[3]);
	                    } else {
	                    	shaders.setColor(color[0], color[1], color[2], color[3]);
	                    }
	                	GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
                }

				meshSprites[numBank].render(nbQuads);
				posBankOrder++;
			}
		}

		// Leave the room clean: deactivate pixel shader and clip
		if (pixelShaderSupported) {
			shaders.setCurrentShader(GLShaders.textured);
        	shaders.setClip(false);
		}
		GLES20.glDisable(GLES20.GL_BLEND);

		gamma = (float) ((gamma + 0.01f) % (Math.PI * 2));
	}

	/**
	 * Load prepared textures and calculate sprite locations.
	 */
	@Override
	public void loadTextures(SpriteStore p_spriteStore) {
		textureEngine.init();
		
		for (int i = 0; i < SpriteManagement.sprBankName.length; i++) {
		    ((AndroidTextureEngine)textureEngine).loadTexture("sprite"+i);
	    }
		
	}
}