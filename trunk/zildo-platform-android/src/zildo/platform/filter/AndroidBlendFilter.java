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

import javax.microedition.khronos.opengles.GL11;

import zildo.Zildo;
import zildo.client.ClientEngineZildo;
import zildo.fwk.gfx.GraphicStuff;
import zildo.fwk.gfx.filter.BlendFilter;
import zildo.fwk.gfx.filter.ScreenFilter;

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
	
	GL11 gl11;
	
	public AndroidBlendFilter(GraphicStuff graphicStuff) {
		super(graphicStuff);
	}
	
	private int getCurrentSquareSize() {
		return 1 + (int) ((SQUARE_SIZE * getFadeLevel()) / 256.0f);
	}

	@Override
	public boolean renderFilter() {
		int currentSquareSize = getCurrentSquareSize();

		if (currentSquareSize == 1) {
			return true;
		}
		graphicStuff.fbo.endRendering();

		// Get on top of screen and disable blending
		gl11.glMatrixMode(GL11.GL_MODELVIEW);
		gl11.glLoadIdentity();
		gl11.glMatrixMode(GL11.GL_PROJECTION);
		gl11.glPushMatrix();
		gl11.glTranslatef(0,-sizeY,0);
		
		// FIXME: was previously 3f
		gl11.glColor4f(1f, 1f, 1f, 1f);
		
		// Draw squares
		int nSquareX=Zildo.viewPortX / currentSquareSize;
		int nSquareY=Zildo.viewPortY / currentSquareSize;
		gl11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);

		for (int i=0;i<nSquareY+1;i++) {
			for (int j=0;j<nSquareX+1;j++) {
				ClientEngineZildo.ortho.boxTexturedOpti(j*currentSquareSize, i*currentSquareSize,
						              currentSquareSize, currentSquareSize, 
						              (j*currentSquareSize) / (float) ScreenFilter.realX, 
						              (i*currentSquareSize) / (float) ScreenFilter.realY,0, 0);
			}
		}
		gl11.glPopMatrix();
		
		gl11.glMatrixMode(GL11.GL_MODELVIEW);
		gl11.glDisable(GL11.GL_BLEND);

		return true;
	}

	@Override
	public void preFilter() {
		if (getCurrentSquareSize() == 1) {
			return;
		}
		// Copy last texture in TexBuffer
		graphicStuff.fbo.bindToTextureAndDepth(textureID, depthTextureID, fboId);
		graphicStuff.fbo.startRendering(fboId, sizeX, sizeY);
		gl11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); // Clear The Screen And The Depth Buffer
	}
}
