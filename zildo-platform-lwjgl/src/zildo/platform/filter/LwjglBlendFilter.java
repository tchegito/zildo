/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
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

package zildo.platform.filter;

import org.lwjgl.opengl.GL11;

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
public class LwjglBlendFilter extends BlendFilter {

	static final int SQUARE_SIZE = 20;
	
	public LwjglBlendFilter(GraphicStuff graphicStuff) {
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
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
    	GL11.glLoadIdentity();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glTranslatef(0,-sizeY,0);
		
		GL11.glColor3f(1f, 1f, 1f);
		
		// Draw squares
		int nSquareX=Zildo.viewPortX / currentSquareSize;
		int nSquareY=Zildo.viewPortY / currentSquareSize;
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);

		GL11.glBegin(GL11.GL_QUADS);
		for (int i=0;i<nSquareY+1;i++) {
			for (int j=0;j<nSquareX+1;j++) {
				ClientEngineZildo.ortho.boxTexturedOpti(j*currentSquareSize, i*currentSquareSize,
						              currentSquareSize, currentSquareSize, 
						              (j*currentSquareSize) / (float) ScreenFilter.realX, 
						              (i*currentSquareSize) / (float) ScreenFilter.realY,0, 0);
			}
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glDisable(GL11.GL_BLEND);

		return true;
	}

	@Override
	public void preFilter() {
		if (getCurrentSquareSize() == 1) {
			return;
		}
		// Copy last texture in TexBuffer
		graphicStuff.fbo.bindToTexture(textureID, fboId);
		graphicStuff.fbo.startRendering(fboId, sizeX, sizeY);
   		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); // Clear The Screen And The Depth Buffer
	}
}
