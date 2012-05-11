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
import zildo.fwk.gfx.filter.CircleFilter;
import zildo.platform.opengl.AndroidOpenGLGestion;

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

	GL11 gl11;
	
	public AndroidCircleFilter(GraphicStuff graphicStuff) {
		super(graphicStuff);
    	gl11 = (GL11) AndroidOpenGLGestion.gl10;
	}
	
	@Override
	public boolean renderFilter() {
		
		// Get on top of screen and disable blending
		gl11.glMatrixMode(GL11.GL_MODELVIEW);
		gl11.glLoadIdentity();
		gl11.glMatrixMode(GL11.GL_PROJECTION);
		gl11.glPushMatrix();
		gl11.glTranslatef(0,sizeY,0);
		gl11.glScalef(1, -1, 1);
		
		// FIXME: Was previously color3f
		gl11.glColor4f(1f, 1f, 1f, 1f);
		
		gl11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);

		int radius = (int) (coeffLevel * (255 - getFadeLevel())); // + 20;
		int radiusSquare = (int) Math.pow(radius, 2);
		int col = 3;
		
		int sizeA = center.y - radius;
		int sizeB = Zildo.viewPortY - (center.y + radius);

		// 1) Draw the two areas outside the circle
		if (sizeA > 0) {
			ClientEngineZildo.ortho.boxOpti(0, 0, Zildo.viewPortX, sizeA, 3, null);
		}
		if (sizeB > 0) {
			ClientEngineZildo.ortho.boxOpti(0, center.y + radius, Zildo.viewPortX, sizeB, 3, null);
		}
		
		// 2) Draw the circle area
		int startI = Math.max(0, sizeA);
		int endI = Math.min(Zildo.viewPortY, center.y + radius);
		
		for (int i=startI;i<endI;i++) {
			int start = (int) Math.pow(i-center.y, 2);

			// Calculate DELTA and 2 roots x1 & x2
			double delta = 4 * (radiusSquare - start);
			double squareRootDelta = Math.sqrt(delta);
			int x1 = (int) (center.x - squareRootDelta / 2);
			int x2 = (int) (center.x + squareRootDelta / 2);
			ClientEngineZildo.ortho.boxOpti(0, i, x1, 1, col, null);
			ClientEngineZildo.ortho.boxOpti(x2, i, Zildo.viewPortX - x2, 1, col, null);
		}
		gl11.glPopMatrix();
		
		gl11.glMatrixMode(GL11.GL_MODELVIEW);
		gl11.glDisable(GL11.GL_BLEND);

		return true;
	}
}
