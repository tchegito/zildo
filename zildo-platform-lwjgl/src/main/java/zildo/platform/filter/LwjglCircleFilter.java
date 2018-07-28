/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
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
import zildo.fwk.gfx.filter.CircleFilter;
import zildo.fwk.gfx.filter.FilterEffect;
import zildo.monde.util.Vector3f;
import zildo.monde.util.Vector3i;
import zildo.monde.util.Vector4f;

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
public class LwjglCircleFilter extends CircleFilter {

	public LwjglCircleFilter(GraphicStuff graphicStuff) {
		super(graphicStuff);
	}
	
	@Override
	public void setCenter(int p_x, int p_y) {
		center.x = p_x;
		center.y = Zildo.viewPortY - p_y;
	}
	
	@Override
	public boolean renderFilter() {
		graphicStuff.fbo.endRendering();

		// Get on top of screen and disable blending
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
    	GL11.glLoadIdentity();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glTranslatef(0, -sizeY, 1);
		
		Vector3f curColor = ClientEngineZildo.ortho.getFilteredColor();
		GL11.glColor3f(curColor.x, curColor.y, curColor.z);
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		super.render();
		GL11.glBegin(GL11.GL_QUADS);
		int radius = (int) (coeffLevel * (255 - getFadeLevel())); // + 20;
		int radiusSquare = (int) Math.pow(radius, 2);
		Vector4f green = new Vector3i(73, 154, 73).normalize();
		
		int sizeA = center.y - radius;
		int sizeB = Zildo.viewPortY - (center.y + radius);

		// 1) Draw the two areas outside the circle
		if (sizeA > 0) {
			ClientEngineZildo.ortho.boxOpti(0, 0, Zildo.viewPortX, sizeA, green);
		}
		if (sizeB > 0) {
			ClientEngineZildo.ortho.boxOpti(0, center.y + radius, Zildo.viewPortX, sizeB, green);
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
			ClientEngineZildo.ortho.boxOpti(0, i, x1, 1, green);
			ClientEngineZildo.ortho.boxOpti(x2, i, Zildo.viewPortX - x2, 1, green);
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		
		// Reset full color
		GL11.glColor3f(1, 1, 1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glDisable(GL11.GL_BLEND);

		return true;
	}
	
	@Override
	public void preFilter() {
		graphicStuff.fbo.startRendering(fboId, sizeX, sizeY);
   		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); // Clear The Screen And The Depth Buffer
	}
	
	@Override
	public void doOnActive(FilterEffect effect) {
		ClientEngineZildo.ortho.setAmbientColor(ClientEngineZildo.ortho.getFilteredColor());
	}
}
