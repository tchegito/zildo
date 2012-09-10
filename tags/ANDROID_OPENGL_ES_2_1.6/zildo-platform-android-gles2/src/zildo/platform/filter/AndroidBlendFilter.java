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

package zildo.platform.filter;

import shader.Shaders;
import shader.Shaders.GLShaders;
import android.opengl.GLES20;
import zildo.client.ClientEngineZildo;
import zildo.fwk.gfx.GraphicStuff;
import zildo.fwk.gfx.filter.BlendFilter;
import zildo.monde.sprites.Reverse;
import zildo.platform.opengl.AndroidOrtho;
import zildo.platform.opengl.AndroidPixelShaders;

/**
 * Draw boxes more and more large onto the screen, to get a soft focus effect.
 * 
 * If square boxes are 1-sized, don't do anything.
 * 
 * @author tchegito
 *
 */
public class AndroidBlendFilter extends BlendFilter {

	static final int SQUARE_SIZE = 20;
	
	AndroidOrtho ortho;
	Shaders shaders;
	
	public AndroidBlendFilter(GraphicStuff graphicStuff) {
		super(graphicStuff);
    	ortho = (AndroidOrtho) ClientEngineZildo.ortho;
    	shaders = AndroidPixelShaders.shaders;
    	
		// Flip the image vertically
		super.startInitialization();
		updateQuad(0, 0, 0, 0, Reverse.VERTICAL);
		super.endInitialization();
	}
	
	private int getCurrentSquareSize() {
		return 1 + (int) ((SQUARE_SIZE * getFadeLevel()) / 256.0f);
	}

	@Override
	public boolean renderFilter() {
		int currentSquareSize = getCurrentSquareSize();


		graphicStuff.fbo.endRendering();
		
		// FIXME: was previously 3f
		shaders.setColor(1f, 1f, 1f, 1f);
		
		// Draw squares
		// Select right texture
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

		// Draw texture
		shaders.setCurrentShader(GLShaders.blendFilter);
		shaders.setBlendSquareSize(currentSquareSize);
		super.render();
		shaders.setCurrentShader(GLShaders.textured);


		return true;
	}

	@Override
	public void preFilter() {
		// Copy last texture in TexBuffer
		graphicStuff.fbo.bindToTexture(textureID, fboId);
		graphicStuff.fbo.startRendering(fboId, sizeX, sizeY);
		//gl11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); // Clear The Screen And The Depth Buffer

	}
}
