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

package zildo.fwk.gfx.filter;

import org.lwjgl.opengl.GL11;

import zildo.Zildo;
import zildo.client.ClientEngineZildo;
import zildo.monde.util.Point;

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
public class CircleFilter extends FadeScreenFilter {

	// Center of the circle
	public static Point center = new Point(Zildo.viewPortX / 2, Zildo.viewPortY / 2);
	
	// Coeff to get the circle drawn all over the screen
	private double coeffLevel = Math.sqrt(Math.pow(Zildo.viewPortX, 2) + Math.pow(Zildo.viewPortY, 2)) / 255f;
	
	@Override
	public boolean renderFilter() {
		
		// Get on top of screen and disable blending
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
    	GL11.glLoadIdentity();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glTranslatef(0, -sizeY, 1);
		
		GL11.glColor3f(1f, 1f, 1f);
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);

		GL11.glBegin(GL11.GL_QUADS);
		int radius = (int) (coeffLevel * (255 - getFadeLevel())); // + 20;
		int radiusSquare = (int) Math.pow(radius, 2);
		int col = 0;
		
		int sizeA = center.y - radius;
		int sizeB = Zildo.viewPortY - (center.y + radius);

		// 1) Draw the two areas outside the circle
		if (sizeA > 0) {
			ClientEngineZildo.ortho.boxOpti(0, 0, Zildo.viewPortX, sizeA, 2, null);
		}
		if (sizeB > 0) {
			ClientEngineZildo.ortho.boxOpti(0, center.y + radius, Zildo.viewPortX, sizeB, 2, null);
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
		GL11.glEnd();
		GL11.glPopMatrix();
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glDisable(GL11.GL_BLEND);

		return true;
	}
	
	public static void setCenter(int p_x, int p_y) {
		center.x = p_x;
		center.y = Zildo.viewPortY - p_y;
	}
}
