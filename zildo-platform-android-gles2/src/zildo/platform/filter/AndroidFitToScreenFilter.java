/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
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
import zildo.client.ClientEngineZildo;
import zildo.fwk.gfx.GraphicStuff;
import zildo.fwk.gfx.filter.FitToScreenFilter;
import zildo.monde.sprites.Reverse;
import zildo.monde.util.Vector4f;
import zildo.platform.opengl.AndroidPixelShaders;
import android.opengl.GLES20;

/**
 * @author Tchegito
 *
 */
public class AndroidFitToScreenFilter extends FitToScreenFilter {

	Shaders shaders;
	
	/**
	 * @param graphicStuff
	 */
	public AndroidFitToScreenFilter(GraphicStuff graphicStuff) {
		super(graphicStuff);
		shaders = AndroidPixelShaders.shaders;
	}

	boolean resized = false;
	
	@Override
	public boolean renderFilter() {
        if (!resized) {
			startInitialization();
			updateQuad(0, 0, 1, 0.1f, Reverse.HORIZONTAL);
			endInitialization();
			startInitialization();
			updateTextureCoordinates(0, 0, textureSizeX, textureSizeY, false);
			endInitialization();
			resized = true;
        }
		int fadeLevel = getFadeLevel();
		shaders.setColor(new Vector4f(1, 1, 1, fadeLevel / 255f));

		GLES20.glEnable(GLES20.GL_BLEND);
    	GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, ClientEngineZildo.tileEngine.texBackMenuId);
		super.render();

		GLES20.glDisable(GLES20.GL_BLEND);
		
		return true;
	}
}
