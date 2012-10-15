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

package zildo.platform.filter;

import zildo.client.ClientEngineZildo;
import zildo.fwk.gfx.GraphicStuff;
import zildo.fwk.gfx.filter.BilinearFilter;
import zildo.monde.sprites.Reverse;
import zildo.monde.util.Vector3f;
import zildo.platform.opengl.AndroidPixelShaders;
import android.opengl.GLES20;


public class AndroidBilinearFilter extends BilinearFilter {

	public AndroidBilinearFilter(GraphicStuff graphicStuff) {
		super(graphicStuff);

		// Flip the image vertically
		super.startInitialization();
		updateQuad(0, 0, 0, 0, Reverse.VERTICAL);
		super.endInitialization();
	}
	
	private Vector3f full = new Vector3f(1, 1, 1);
	
	@Override
	public boolean renderFilter() {
		graphicStuff.fbo.endRendering();


		// Select right texture
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

		// Change all screen colors
		Vector3f col = ClientEngineZildo.ortho.getFilteredColor();
		AndroidPixelShaders.shaders.setColor(col);
		
		// Draw texture
		super.render();
		// Reset full color
		AndroidPixelShaders.shaders.setColor(full);
		
		return true;
	}
	
	@Override
	public void preFilter() {
		graphicStuff.fbo.startRendering(fboId, sizeX, sizeY);
	}
	
}