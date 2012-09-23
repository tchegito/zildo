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

package zildo.fwk.gfx;

import zildo.Zildo;
import zildo.monde.collision.Rectangle;
import zildo.monde.util.Point;
import zildo.monde.util.Vector3f;
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
public abstract class Ortho {

	static protected char[][][] fonts = new char[][][] { // 63 fonte
	{ { 0, 1, 1, 1, 0 }, { 1, 0, 0, 0, 1 }, { 1, 1, 1, 1, 1 }, { 1, 0, 0, 0, 1 }, { 1, 0, 0, 0, 1 } }, // A
			{ { 1, 1, 1, 1, 0 }, { 1, 0, 0, 0, 1 }, { 1, 1, 1, 1, 0 }, { 1, 0, 0, 0, 1 }, { 1, 1, 1, 1, 0 } },
			{ { 0, 1, 1, 1, 1 }, { 1, 0, 0, 0, 0 }, { 1, 0, 0, 0, 0 }, { 1, 0, 0, 0, 0 }, { 0, 1, 1, 1, 1 } },
			{ { 1, 1, 1, 1, 0 }, { 1, 0, 0, 0, 1 }, { 1, 0, 0, 0, 1 }, { 1, 0, 0, 0, 1 }, { 1, 1, 1, 1, 0 } },
			{ { 1, 1, 1, 1, 1 }, { 1, 0, 0, 0, 0 }, { 1, 1, 1, 0, 0 }, { 1, 0, 0, 0, 0 }, { 1, 1, 1, 1, 1 } },
			{ { 1, 1, 1, 1, 1 }, { 1, 0, 0, 0, 0 }, { 1, 1, 1, 0, 0 }, { 1, 0, 0, 0, 0 }, { 1, 0, 0, 0, 0 } },
			{ { 0, 1, 1, 1, 0 }, { 1, 0, 0, 0, 0 }, { 1, 0, 1, 1, 1 }, { 1, 0, 0, 0, 1 }, { 0, 1, 1, 1, 0 } },
			{ { 1, 0, 0, 0, 1 }, { 1, 0, 0, 0, 1 }, { 1, 1, 1, 1, 1 }, { 1, 0, 0, 0, 1 }, { 1, 0, 0, 0, 1 } },
			{ { 0, 1, 1, 1, 0 }, { 0, 0, 1, 0, 0 }, { 0, 0, 1, 0, 0 }, { 0, 0, 1, 0, 0 }, { 0, 1, 1, 1, 0 } }, // I
			{ { 0, 0, 0, 1, 0 }, { 0, 0, 0, 1, 0 }, { 1, 0, 0, 1, 0 }, { 1, 0, 0, 1, 0 }, { 0, 1, 1, 0, 0 } },
			{ { 1, 0, 0, 1, 0 }, { 1, 0, 1, 0, 0 }, { 1, 1, 0, 0, 0 }, { 1, 0, 1, 0, 0 }, { 1, 0, 0, 1, 0 } },
			{ { 1, 0, 0, 0, 0 }, { 1, 0, 0, 0, 0 }, { 1, 0, 0, 0, 0 }, { 1, 0, 0, 0, 0 }, { 1, 1, 1, 1, 1 } },
			{ { 1, 0, 0, 0, 1 }, { 1, 1, 0, 1, 1 }, { 1, 0, 1, 0, 1 }, { 1, 0, 0, 0, 1 }, { 1, 0, 0, 0, 1 } },
			{ { 1, 0, 0, 0, 1 }, { 1, 1, 0, 0, 1 }, { 1, 0, 1, 0, 1 }, { 1, 0, 0, 1, 1 }, { 1, 0, 0, 0, 1 } },
			{ { 0, 1, 1, 1, 0 }, { 1, 0, 0, 0, 1 }, { 1, 0, 0, 0, 1 }, { 1, 0, 0, 0, 1 }, { 0, 1, 1, 1, 0 } },
			{ { 1, 1, 1, 1, 0 }, { 1, 0, 0, 0, 1 }, { 1, 1, 1, 1, 0 }, { 1, 0, 0, 0, 0 }, { 1, 0, 0, 0, 0 } },
			{ { 0, 1, 1, 1, 0 }, { 1, 0, 0, 0, 1 }, { 1, 0, 0, 0, 1 }, { 1, 0, 0, 1, 1 }, { 0, 1, 1, 1, 1 } },
			{ { 1, 1, 1, 1, 0 }, { 1, 0, 0, 0, 1 }, { 1, 1, 1, 1, 0 }, { 1, 0, 0, 1, 0 }, { 1, 0, 0, 0, 1 } },
			{ { 0, 1, 1, 1, 1 }, { 1, 0, 0, 0, 0 }, { 0, 1, 1, 1, 0 }, { 0, 0, 0, 0, 1 }, { 1, 1, 1, 1, 0 } },
			{ { 1, 1, 1, 1, 1 }, { 0, 0, 1, 0, 0 }, { 0, 0, 1, 0, 0 }, { 0, 0, 1, 0, 0 }, { 0, 0, 1, 0, 0 } }, // T
			{ { 1, 0, 0, 0, 1 }, { 1, 0, 0, 0, 1 }, { 1, 0, 0, 0, 1 }, { 1, 0, 0, 0, 1 }, { 0, 1, 1, 1, 0 } },
			{ { 1, 0, 0, 0, 1 }, { 1, 0, 0, 0, 1 }, { 0, 1, 0, 1, 0 }, { 0, 1, 0, 1, 0 }, { 0, 0, 1, 0, 0 } },
			{ { 1, 0, 0, 0, 1 }, { 1, 0, 0, 0, 1 }, { 1, 0, 1, 0, 1 }, { 1, 1, 0, 1, 1 }, { 1, 0, 0, 0, 1 } },
			{ { 1, 0, 0, 0, 1 }, { 0, 1, 0, 1, 0 }, { 0, 0, 1, 0, 0 }, { 0, 1, 0, 1, 0 }, { 1, 0, 0, 0, 1 } },
			{ { 1, 0, 0, 0, 1 }, { 0, 1, 0, 1, 0 }, { 0, 0, 1, 0, 0 }, { 0, 0, 1, 0, 0 }, { 0, 0, 1, 0, 0 } },
			{ { 1, 1, 1, 1, 1 }, { 0, 0, 0, 1, 0 }, { 0, 0, 1, 0, 0 }, { 0, 1, 0, 0, 0 }, { 1, 1, 1, 1, 1 } }, // Z
			{ { 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0 }, { 0, 0, 1, 0, 0 } }, // .
			{ { 0, 0, 0, 0, 0 }, { 0, 0, 0, 1, 0 }, { 0, 0, 1, 0, 0 }, { 0, 1, 0, 0, 0 }, { 1, 0, 0, 0, 0 } }, // /
			{ { 0, 1, 1, 1, 0 }, { 1, 0, 0, 1, 1 }, { 1, 0, 1, 0, 1 }, { 1, 1, 0, 0, 1 }, { 0, 1, 1, 1, 0 } }, // 0
			{ { 0, 0, 1, 0, 0 }, { 0, 1, 1, 0, 0 }, { 0, 0, 1, 0, 0 }, { 0, 0, 1, 0, 0 }, { 1, 1, 1, 1, 1 } },
			{ { 0, 1, 1, 1, 0 }, { 1, 0, 0, 0, 1 }, { 0, 0, 1, 1, 0 }, { 0, 1, 0, 0, 0 }, { 1, 1, 1, 1, 1 } },
			{ { 1, 1, 1, 1, 0 }, { 0, 0, 0, 0, 1 }, { 0, 1, 1, 1, 0 }, { 0, 0, 0, 0, 1 }, { 1, 1, 1, 1, 0 } },
			{ { 1, 0, 0, 1, 0 }, { 1, 0, 0, 1, 0 }, { 1, 1, 1, 1, 1 }, { 0, 0, 0, 1, 0 }, { 0, 0, 0, 1, 0 } },
			{ { 1, 1, 1, 1, 1 }, { 1, 0, 0, 0, 0 }, { 0, 1, 1, 1, 0 }, { 0, 0, 0, 0, 1 }, { 1, 1, 1, 1, 0 } },
			{ { 0, 0, 1, 1, 0 }, { 0, 1, 0, 0, 0 }, { 1, 1, 1, 1, 0 }, { 1, 0, 0, 0, 1 }, { 0, 1, 1, 1, 0 } },
			{ { 1, 1, 1, 1, 1 }, { 0, 0, 0, 1, 0 }, { 0, 0, 1, 0, 0 }, { 0, 1, 0, 0, 0 }, { 0, 1, 0, 0, 0 } },
			{ { 0, 1, 1, 1, 0 }, { 1, 0, 0, 0, 1 }, { 0, 1, 1, 1, 0 }, { 1, 0, 0, 0, 1 }, { 0, 1, 1, 1, 0 } },
			{ { 0, 1, 1, 1, 0 }, { 1, 0, 0, 0, 1 }, { 0, 1, 1, 1, 1 }, { 0, 0, 0, 1, 0 }, { 0, 1, 1, 0, 0 } }, // 9
			{ { 0, 0, 1, 0, 0 }, { 0, 1, 0, 0, 0 }, { 1, 1, 1, 1, 1 }, { 0, 1, 0, 0, 0 }, { 0, 0, 1, 0, 0 } }, // Flèche
																												// gauche
			{ { 0, 0, 1, 0, 0 }, { 0, 0, 0, 1, 0 }, { 1, 1, 1, 1, 1 }, { 0, 0, 0, 1, 0 }, { 0, 0, 1, 0, 0 } }, // Droite
			{ { 0, 0, 0, 0, 0 }, { 0, 0, 1, 0, 0 }, { 0, 0, 0, 0, 0 }, { 0, 0, 1, 0, 0 }, { 0, 0, 0, 0, 0 } }, // :
			{ { 0, 0, 1, 0, 0 }, { 0, 1, 0, 1, 0 }, { 1, 0, 0, 0, 1 }, { 0, 1, 0, 1, 0 }, { 0, 0, 1, 0, 0 } }, // losange
			{ { 1, 1, 0, 0, 0 }, { 0, 0, 1, 0, 0 }, { 0, 0, 1, 1, 1 }, { 0, 0, 1, 0, 0 }, { 1, 1, 0, 0, 0 } }, //
			{ { 0, 0, 0, 0, 1 }, { 0, 0, 0, 1, 1 }, { 0, 0, 1, 0, 1 }, { 0, 1, 0, 0, 1 }, { 1, 1, 1, 1, 1 } }, // p=6
			{ { 1, 1, 1, 1, 1 }, { 1, 0, 1, 0, 1 }, { 1, 1, 1, 1, 1 }, { 1, 0, 1, 0, 1 }, { 1, 1, 1, 1, 1 } },
			{ { 0, 1, 1, 0, 1 }, { 1, 0, 0, 1, 0 }, { 0, 0, 0, 0, 0 }, { 0, 1, 1, 0, 1 }, { 1, 0, 0, 1, 0 } },
			{ { 0, 0, 1, 0, 0 }, { 0, 0, 0, 0, 0 }, { 1, 0, 0, 0, 1 }, { 0, 0, 0, 0, 0 }, { 0, 0, 1, 0, 0 } },
			{ { 0, 1, 0, 0, 1 }, { 1, 0, 0, 1, 1 }, { 1, 0, 1, 0, 1 }, { 1, 1, 0, 0, 1 }, { 1, 0, 0, 1, 0 } },
			{ { 0, 1, 0, 0, 0 }, { 1, 0, 1, 0, 0 }, { 0, 1, 0, 0, 0 }, { 0, 0, 0, 0, 0 }, { 1, 1, 1, 1, 1 } },
			{ { 0, 1, 1, 1, 0 }, { 1, 0, 1, 0, 1 }, { 0, 1, 1, 1, 0 }, { 0, 1, 0, 1, 0 }, { 0, 0, 1, 0, 0 } },
			{ { 1, 1, 1, 1, 1 }, { 0, 1, 0, 1, 0 }, { 0, 0, 1, 0, 0 }, { 0, 1, 0, 1, 0 }, { 1, 1, 1, 1, 1 } },
			{ { 1, 1, 1, 0, 0 }, { 1, 0, 1, 0, 0 }, { 0, 0, 1, 1, 1 }, { 0, 0, 1, 0, 1 }, { 0, 0, 1, 1, 1 } },
			{ { 1, 1, 1, 1, 1 }, { 0, 0, 0, 0, 1 }, { 1, 1, 1, 0, 1 }, { 0, 0, 1, 0, 1 }, { 1, 0, 1, 0, 1 } },
			{ { 0, 0, 1, 1, 1 }, { 0, 1, 0, 0, 0 }, { 0, 1, 1, 1, 0 }, { 1, 0, 0, 0, 0 }, { 0, 1, 1, 0, 0 } },
			{ { 1, 1, 1, 1, 0 }, { 0, 0, 0, 0, 1 }, { 1, 0, 0, 0, 1 }, { 1, 0, 0, 0, 0 }, { 0, 1, 1, 1, 1 } },
			{ { 0, 0, 1, 0, 0 }, { 0, 0, 0, 1, 0 }, { 1, 1, 1, 1, 1 }, { 0, 1, 0, 0, 0 }, { 0, 0, 1, 0, 0 } },
			{ { 0, 0, 0, 0, 1 }, { 0, 0, 0, 1, 0 }, { 1, 0, 1, 0, 0 }, { 0, 1, 0, 0, 0 }, { 1, 0, 1, 0, 0 } },
			{ { 0, 0, 1, 0, 0 }, { 0, 0, 0, 0, 0 }, { 1, 0, 0, 0, 1 }, { 1, 0, 0, 0, 1 }, { 0, 1, 1, 1, 0 } },
			{ { 1, 0, 0, 1, 0 }, { 0, 1, 0, 0, 0 }, { 0, 1, 1, 0, 0 }, { 1, 0, 0, 1, 0 }, { 1, 0, 0, 0, 0 } },
			{ { 0, 0, 1, 0, 0 }, { 0, 1, 1, 1, 0 }, { 0, 0, 1, 0, 0 }, { 0, 0, 1, 0, 0 }, { 1, 1, 1, 1, 1 } },
			{ { 1, 0, 1, 0, 1 }, { 0, 1, 1, 1, 0 }, { 1, 1, 1, 1, 1 }, { 0, 1, 1, 1, 0 }, { 1, 0, 1, 0, 1 } },
			{ { 1, 0, 0, 0, 1 }, { 1, 0, 0, 0, 1 }, { 0, 1, 1, 1, 0 }, { 1, 0, 0, 0, 1 }, { 0, 1, 1, 1, 0 } },
			{ { 0, 0, 0, 1, 0 }, { 0, 0, 0, 1, 0 }, { 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0 } }, // '
			{ { 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0 }, { 0, 1, 1, 1, 0 }, { 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0 } } }; // -

	protected int w, h;
	protected boolean orthoSetUp;

	protected Vector3f ambientColor; // Current ambient color (could be null)
	protected Vector3f filteredColor; // Filtered color (never null, at least 1,1,1)

	public final static Vector3f SEMI_NIGHT_FILTER = new Vector3f(0.75f, 0.85f, 1f);
	public final static Vector3f NIGHT_FILTER = new Vector3f(0.5f, 0.6f, 1f);

	public Ortho(int width, int height) {
		w = width;
		h = height;
		orthoSetUp = false;
		ambientColor = new Vector3f(1f, 1f, 1f);
		filteredColor = new Vector3f(1f, 1f, 1f);
	}

	public abstract void setOrthographicProjection(boolean p_zoom);

	/**
	 * Resize when using ZEditor
	 * 
	 * @param p_x
	 * @param p_y
	 */
	public void setSize(int p_x, int p_y, boolean p_zoom) {
		// And adapt ortho
		resetPerspectiveProjection(p_x, p_y);
		w = p_x;
		h = p_y;
		setOrthographicProjection(p_zoom);
	}

	public abstract void resetPerspectiveProjection(int p_x, int p_y);

	public void drawChar(int x, int y, char a) {
		int aa = a;
		if (aa != 32) {
			if (a == '.') {
				aa = 26;
			} else if (a == '\'') {
				aa = 62;
			} else if (a == '-') {
				aa = 63;
			} else if (aa >= 48 && aa <= 57) {
				aa -= 48 - 28;
			} else {
				if (aa >= 'a' && aa <= 'z') {
					aa -= 'a'; // -1;
				} else if (aa > 199) {
					aa -= 161;
				} else {
					aa -= 64;
				}
			}
			if (aa >= 0 && aa < fonts.length) {
				drawOneChar(x, y, aa);
			}
		}
	}

	public abstract void drawOneChar(int x, int y, int aa);
	
	public void drawText(int x, int y, String txt) {
		for (int i = 0; i < txt.length(); i++) {
			drawChar(x + 6 * i, y, txt.toLowerCase().charAt(i));
		}
	}

	public void drawText(int p_x, int p_y, String p_txt, Vector3f p_color) {
		// Center text
		int x = p_x;
		if (x == -1) {
			x = (Zildo.viewPortX - p_txt.length() * 6) / 2;
		}
		enableTexture2d(false);
		float factor = 0.2f;
		setColor(p_color.x * factor, p_color.y * factor, p_color.z * factor);
		drawText(x + 1, p_y + 1, p_txt);
		setColor(p_color.x, p_color.y, p_color.z);
		drawText(x, p_y, p_txt);
		setColor(1.0f, 1.0f, 1.0f);
		enableTexture2d(true);
	}

	public abstract void enableTexture2d(boolean enable);
	
	public abstract void setColor(float r, float g, float b);
	
	public void box(Rectangle p_rect, int palColor, Vector4f color) {
		Point cornerTopLeft = p_rect.getCornerTopLeft();
		Point size = p_rect.getSize();
		box(cornerTopLeft.x, cornerTopLeft.y, size.x, size.y, palColor, color);
	}

	/**
	 * Draw a box on foreground (z=1) with desired color (palettized or not).
	 * This is not the right way for many boxes. Prefer (@link #boxOpti).
	 * 
	 * @param x
	 * @param y
	 * @param p_w
	 * @param h
	 * @param palColor
	 *            index for palettized color
	 * @param color
	 *            real color
	 */
	public void box(int x, int y, int p_w, int p_h, int palColor, Vector4f color) {
		initDrawBox(false);
		boxOpti(x, y, p_w, p_h, palColor, color);
		endDraw();
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
	public abstract void boxOpti(int x, int y, int p_w, int p_h, int palColor, Vector4f color);

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
	public abstract void boxTexturedOpti(int x, int y, int p_w, int p_h, float u, float v, float uw, float vh);

	/**
	 * Initialize the right matrix to draw quads, and do a glBegin.
	 * 
	 * @param withTexture
	 */
	public abstract void initDrawBox(boolean withTexture);

	/**
	 * Get back the original matrix, and go a glEnd.
	 */
	public abstract void endDraw();

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
	public abstract void boxv(int x, int y, int p_w, int p_h, int palColor, Vector4f color);

	public abstract void enableBlend();

	public abstract void disableBlend();

	public Vector3f getAmbientColor() {
		return ambientColor;
	}

	public void setAmbientColor(Vector3f ambientColor) {
		this.ambientColor = ambientColor;
	}
	
	public Vector3f getFilteredColor() {
		return filteredColor;
	}
	
	public void setFilteredColor(Vector3f filtered) {
		this.filteredColor = filtered;
	}
	
	public boolean isNight() {
		return filteredColor == Ortho.NIGHT_FILTER;
	}
}
