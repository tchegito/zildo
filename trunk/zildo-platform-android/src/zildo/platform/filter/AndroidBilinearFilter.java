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

import zildo.fwk.gfx.GraphicStuff;
import zildo.fwk.gfx.filter.BilinearFilter;
import zildo.platform.opengl.AndroidOpenGLGestion;


public class AndroidBilinearFilter extends BilinearFilter {

	GL11 gl11;
	
	public AndroidBilinearFilter(GraphicStuff graphicStuff) {
		super(graphicStuff);
    	gl11 = (GL11) AndroidOpenGLGestion.gl10;
	}
	
	@Override
	public boolean renderFilter() {
		graphicStuff.fbo.endRendering();
		
		// Select right texture
		gl11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
        // Disable blend
		gl11.glDisable(GL11.GL_BLEND);

		gl11.glMatrixMode(GL11.GL_MODELVIEW);
		gl11.glLoadIdentity();
		gl11.glMatrixMode(GL11.GL_PROJECTION);
		gl11.glPushMatrix();
		gl11.glTranslatef(0,-sizeY,1);

		// FIXME: was previously color3f
		gl11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		
		// Draw texture with depth
		super.render();

		gl11.glPopMatrix();
		
		gl11.glMatrixMode(GL11.GL_MODELVIEW);
		
		return true;
	}
	
	@Override
	public void preFilter() {
		graphicStuff.fbo.startRendering(fboId, sizeX, sizeY);
		gl11.glClear(GL11.GL_COLOR_BUFFER_BIT); // Clear The Screen And The Depth Buffer
	}
	
}