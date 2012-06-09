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
import zildo.client.ClientEngineZildo;
import zildo.fwk.gfx.GraphicStuff;
import zildo.fwk.gfx.filter.CloudFilter;
import zildo.monde.sprites.Reverse;
import zildo.monde.util.Vector4f;
import zildo.platform.opengl.AndroidPixelShaders;
import android.opengl.GLES20;

/**
 * @author Tchegito
 *
 */
public class AndroidCloudFilter extends CloudFilter {

	Shaders shaders;
	
	public AndroidCloudFilter(GraphicStuff graphicStuff) {
		super(graphicStuff);
		
		shaders = AndroidPixelShaders.shaders;
	}
	
	@Override
	public boolean renderFilter() {
		super.startInitialization();
		updateQuad(0, 0, u, -v, Reverse.NOTHING);
		this.endInitialization();

		float colorFactor=0.2f;
		GLES20.glEnable(GLES20.GL_BLEND);
		
		shaders.setColor(new Vector4f(colorFactor, colorFactor, colorFactor, 0.1f));

		GLES20.glBlendFunc(GLES20.GL_ZERO, GLES20.GL_ONE_MINUS_SRC_COLOR);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, ClientEngineZildo.tileEngine.texCloudId);
		super.render();

		GLES20.glDisable(GLES20.GL_BLEND);
		
		return true;
	}
}
