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

package zildo.platform.opengl;

import org.lwjgl.opengl.GL11;

import zildo.fwk.gfx.GFXBasics;
import zildo.fwk.gfx.Ortho;
import zildo.monde.util.Vector4f;

/**
 * Class which provides direct draw on render screen, in an orthographic
 * context.
 * 
 * It means that there's any projection. The z-coordinate is just used to know
 * if an obect is behind or before another, but it doesn't lead to perspective
 * anymore.
 * 
 * 
 * @author tchegito
 * 
 */
public class LwjglOrtho extends Ortho {

	public LwjglOrtho(int width, int height) {
		super(width, height);
	}

	@Override
	public void setOrthographicProjection(boolean p_zoom) {
		if (!orthoSetUp) {
			// switch to projection mode
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			// save previous matrix which contains the
			// settings for the perspective projection
			GL11.glPushMatrix();
			// reset matrix
			GL11.glLoadIdentity();
			// set a 2D orthographic projection
			if (p_zoom) {
				GL11.glOrtho(0, w / 2, 0, h / 2, -99999, 99999);
				GL11.glTranslatef(0, -h / 2, 0);
			} else {
				GL11.glOrtho(0, w, 0, h, -99999, 99999);
			}
			// invert the y axis, down is positive
			// GL11.glScalef(1, -1, 1);
			// GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			// mover the origin from the bottom left corner
			// to the upper left corner
			GL11.glTranslatef(0, h, 0);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);

			orthoSetUp = true;
		}
	}

	@Override
	public void resetPerspectiveProjection(int p_x, int p_y) {
		if (orthoSetUp) {
			// Change viewport
			GL11.glViewport(0, 0, p_x, p_y);

			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glPopMatrix();
			GL11.glMatrixMode(GL11.GL_MODELVIEW);

			orthoSetUp = false;
		}
	}

	@Override
	public void drawOneChar(int x, int y, int aa) {
		GL11.glBegin(GL11.GL_POINTS);
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				char pixel = fonts[aa][j][i];
				if (pixel == 1) {
					GL11.glVertex2i(x + i, y + j);
					GL11.glVertex2f(x + i + 0.5f, y + j);
					// GL11.glVertex2f(x+i+0.5f, y+j+0.5f);
					// GL11.glVertex2f(x+i, y+j+0.5f);
				}
			}
		}
		GL11.glEnd();
	}
	
	@Override
	public void drawText(int x, int y, String txt) {
		for (int i = 0; i < txt.length(); i++) {
			drawChar(x + 6 * i, y, txt.toLowerCase().charAt(i));
		}
	}

	@Override
	public void setColor(float r, float g, float b) {
		GL11.glColor3f(r, g, b);
	}
	
	@Override
	public void enableTexture2d(boolean enable) {
		if (enable) {
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		} else {
			GL11.glDisable(GL11.GL_TEXTURE_2D);
		}
	}

	/**
	 * Just draw the colored box, without managing glBegin/glEnd
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param palColor
	 * @param color
	 */
	@Override
	public void boxOpti(int x, int y, int p_w, int p_h, int palColor, Vector4f color) {
		Vector4f col = color;
		if (color == null) {
			col = new Vector4f(GFXBasics.getColor(palColor));
			col.scale(1.0f / 256.0f);
		}
		GL11.glColor4f(col.x, col.y, col.z, col.w);
		GL11.glVertex2d(x, y);
		GL11.glVertex2d(x + p_w, y);
		GL11.glVertex2d(x + p_w, y + p_h);
		GL11.glVertex2d(x, y + p_h);
	}

	/**
	 * Just draw a textured box, without managing glBegin/glEnd
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param u
	 * @param v
	 * @param uw
	 * @param vh
	 */
	@Override
	public void boxTexturedOpti(int x, int y, int p_w, int p_h, float u, float v, float uw, float vh) {
		GL11.glTexCoord2f(u, v);
		GL11.glVertex2d(x, y);
		GL11.glTexCoord2f(u + uw, v);
		GL11.glVertex2d(x + p_w, y);
		GL11.glTexCoord2f(u + uw, v + vh);
		GL11.glVertex2d(x + p_w, y + p_h);
		GL11.glTexCoord2f(u, v + vh);
		GL11.glVertex2d(x, y + p_h);
	}

	/**
	 * Initialize the right matrix to draw quads, and do a glBegin.
	 * 
	 * @param withTexture
	 */
	@Override
	public void initDrawBox(boolean withTexture) {
		// On se met au premier plan et on annule le texturing
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPushMatrix();
		GL11.glTranslatef(0, 0, 1);
		if (!withTexture) {
			GL11.glDisable(GL11.GL_TEXTURE_2D);
		}
		GL11.glBegin(GL11.GL_QUADS);
	}

	/**
	 * Get back the original matrix, and go a glEnd.
	 */
	@Override
	public void endDraw() {
		GL11.glEnd();
		// On se remet où on était et on réactive le texturing
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glColor3f(1.0f, 1.0f, 1.0f); // , 1.0f);
		GL11.glPopMatrix();
	}

	/**
	 * Draw an empty box on foreground (z=1), same way that box.
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param palColor
	 * @param color
	 */
	@Override
	public void boxv(int x, int y, int p_w, int p_h, int palColor, Vector4f color) {
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		box(x, y, p_w, p_h, palColor, color);
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);

	}

	@Override
	public void enableBlend() {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	@Override
	public void disableBlend() {
		GL11.glDisable(GL11.GL_BLEND);
	}

}
