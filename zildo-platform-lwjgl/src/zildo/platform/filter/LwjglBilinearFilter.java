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

import org.lwjgl.opengl.GL11;

import zildo.fwk.gfx.GraphicStuff;
import zildo.fwk.gfx.filter.BilinearFilter;


public class LwjglBilinearFilter extends BilinearFilter {

	public LwjglBilinearFilter(GraphicStuff graphicStuff) {
		super(graphicStuff);
	}
	
	@Override
	public boolean renderFilter() {
		graphicStuff.fbo.endRendering();
		
		// Select right texture
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
        // Disable blend
		GL11.glDisable(GL11.GL_BLEND);

		GL11.glMatrixMode(GL11.GL_MODELVIEW);
    	GL11.glLoadIdentity();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glTranslatef(0,-sizeY,1);

		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		
		// Draw texture with depth
		super.render();

		GL11.glPopMatrix();
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		
		return true;
	}
	
	@Override
	public void preFilter() {
		graphicStuff.fbo.startRendering(fboId, sizeX, sizeY);
   		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); // Clear The Screen And The Depth Buffer
	}
	
}