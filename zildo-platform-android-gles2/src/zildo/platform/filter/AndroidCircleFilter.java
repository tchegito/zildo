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
import zildo.fwk.gfx.filter.CircleFilter;
import zildo.monde.sprites.Reverse;
import zildo.monde.util.Vector3f;
import zildo.platform.opengl.AndroidOrtho;
import zildo.platform.opengl.AndroidPixelShaders;

/**
 * Draws a circle around a specific center (Zildo !).<br/>
 * 
 * Mathematical explanation:<p/>
 * 
 * Each point respecting following equation must be shown :<br/>
 * (x - a)² + (y - b)² > r² <p/>
 * 
 * So the screen is divided vertically into 3 areas :<ol>
 * <li>before circle : all is black</li>
 * <li>circle : each line has two roots from an equation derived from the first one
 * <li>after circle : all is black again</li>
 * </ol>
 * 
 * In order to calculate the derived equation, let's set a variable Y = (y - b)² <br/>
 * So we have: <br/>
 * (x - a)² + Y > r² <br/>
 * wich gives : <br/>
 * x² - 2ax + a² + Y - r² > 0 <p/>
 * 
 * If we calculate the delta : <br/>
 * D = (-2a)² - 4 * (a² + Y - r²) <br/>
 * D = 4 * (a² - r²)<p>
 * 
 * So we deduce the 2 roots of the equation : <br/>
 * x1 = (2 * a - sqrt(D)) / 2 <br/>
 * x2 = (2 * a + sqrt(D)) / 2 <br/>
 * @author Tchegito
 *
 */
public class AndroidCircleFilter extends CircleFilter {

	AndroidOrtho ortho;
	Shaders shaders;
	
	public AndroidCircleFilter(GraphicStuff graphicStuff) {
		
		super(graphicStuff);
    	ortho = (AndroidOrtho) ClientEngineZildo.ortho;
    	shaders = AndroidPixelShaders.shaders;
    	
		// Flip the image vertically
		super.startInitialization();
		updateQuad(0, 0, 0, 0, Reverse.VERTICAL);
		super.endInitialization();
	}
	
	@Override
	public boolean renderFilter() {
		
		int radius = (int) (coeffLevel * (255 - getFadeLevel())); // + 20;


		graphicStuff.fbo.endRendering();
		
		Vector3f col = ClientEngineZildo.ortho.getFilteredColor();
		shaders.setColor(col);
		
		// Draw squares
		// Select right texture
		GLES20.glActiveTexture(0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

		// Draw texture
		shaders.setCurrentShader(GLShaders.circleFilter);
		shaders.setCircleParams(radius, center);
		super.render();
		shaders.setCurrentShader(GLShaders.textured);
		return true;
	}
	
	@Override
	public void preFilter() {
		graphicStuff.fbo.startRendering(fboId, sizeX, sizeY);
	}
	
}
