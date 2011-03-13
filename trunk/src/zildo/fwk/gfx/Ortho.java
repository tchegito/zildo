/**
 * Legend of Zildo
 * Copyright (C) 2006-2011 Evariste Boussaton
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
import zildo.monde.map.Point;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import zildo.fwk.opengl.OpenGLStuff;

/**
 * Class which provides direct draw on render screen, in an orthographic context.
 *
 * It means that there's any projection. The z-coordinate is just used to know if an obect is behind or before another, but it doesn't
 * lead to perspective anymore.
 * 
 * 
 * @author tchegito
 *
 */
public class Ortho extends OpenGLStuff {

	static char[][][] fonts=new char[][][]{	//63 fonte
		      {{0,1,1,1,0},{1,0,0,0,1},{1,1,1,1,1},{1,0,0,0,1},{1,0,0,0,1}}, // A
		      {{1,1,1,1,0},{1,0,0,0,1},{1,1,1,1,0},{1,0,0,0,1},{1,1,1,1,0}},
		      {{0,1,1,1,1},{1,0,0,0,0},{1,0,0,0,0},{1,0,0,0,0},{0,1,1,1,1}},
		      {{1,1,1,1,0},{1,0,0,0,1},{1,0,0,0,1},{1,0,0,0,1},{1,1,1,1,0}},
		      {{1,1,1,1,1},{1,0,0,0,0},{1,1,1,0,0},{1,0,0,0,0},{1,1,1,1,1}},
		      {{1,1,1,1,1},{1,0,0,0,0},{1,1,1,0,0},{1,0,0,0,0},{1,0,0,0,0}},
		      {{0,1,1,1,0},{1,0,0,0,0},{1,0,1,1,1},{1,0,0,0,1},{0,1,1,1,0}},
		      {{1,0,0,0,1},{1,0,0,0,1},{1,1,1,1,1},{1,0,0,0,1},{1,0,0,0,1}},
		      {{0,1,1,1,0},{0,0,1,0,0},{0,0,1,0,0},{0,0,1,0,0},{0,1,1,1,0}}, // I
		      {{0,0,0,1,0},{0,0,0,1,0},{1,0,0,1,0},{1,0,0,1,0},{0,1,1,0,0}},
		      {{1,0,0,1,0},{1,0,1,0,0},{1,1,0,0,0},{1,0,1,0,0},{1,0,0,1,0}},
		      {{1,0,0,0,0},{1,0,0,0,0},{1,0,0,0,0},{1,0,0,0,0},{1,1,1,1,1}},
		      {{1,0,0,0,1},{1,1,0,1,1},{1,0,1,0,1},{1,0,0,0,1},{1,0,0,0,1}},
		      {{1,0,0,0,1},{1,1,0,0,1},{1,0,1,0,1},{1,0,0,1,1},{1,0,0,0,1}},
		      {{0,1,1,1,0},{1,0,0,0,1},{1,0,0,0,1},{1,0,0,0,1},{0,1,1,1,0}},
		      {{1,1,1,1,0},{1,0,0,0,1},{1,1,1,1,0},{1,0,0,0,0},{1,0,0,0,0}},
		      {{0,1,1,1,0},{1,0,0,0,1},{1,0,0,0,1},{1,0,0,1,1},{0,1,1,1,1}},
		      {{1,1,1,1,0},{1,0,0,0,1},{1,1,1,1,0},{1,0,0,1,0},{1,0,0,0,1}},
		      {{0,1,1,1,1},{1,0,0,0,0},{0,1,1,1,0},{0,0,0,0,1},{1,1,1,1,0}},
		      {{1,1,1,1,1},{0,0,1,0,0},{0,0,1,0,0},{0,0,1,0,0},{0,0,1,0,0}}, // T
		      {{1,0,0,0,1},{1,0,0,0,1},{1,0,0,0,1},{1,0,0,0,1},{0,1,1,1,0}},
		      {{1,0,0,0,1},{1,0,0,0,1},{0,1,0,1,0},{0,1,0,1,0},{0,0,1,0,0}},
		      {{1,0,0,0,1},{1,0,0,0,1},{1,0,1,0,1},{1,1,0,1,1},{1,0,0,0,1}},
		      {{1,0,0,0,1},{0,1,0,1,0},{0,0,1,0,0},{0,1,0,1,0},{1,0,0,0,1}},
		      {{1,0,0,0,1},{0,1,0,1,0},{0,0,1,0,0},{0,0,1,0,0},{0,0,1,0,0}},
		      {{1,1,1,1,1},{0,0,0,1,0},{0,0,1,0,0},{0,1,0,0,0},{1,1,1,1,1}}, // Z
		      {{0,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0},{0,0,1,0,0}}, // .
		      {{0,0,0,0,0},{0,0,0,1,0},{0,0,1,0,0},{0,1,0,0,0},{1,0,0,0,0}}, // /
		      {{0,1,1,1,0},{1,0,0,1,1},{1,0,1,0,1},{1,1,0,0,1},{0,1,1,1,0}}, // 0
		      {{0,0,1,0,0},{0,1,1,0,0},{0,0,1,0,0},{0,0,1,0,0},{1,1,1,1,1}},
		      {{0,1,1,1,0},{1,0,0,0,1},{0,0,1,1,0},{0,1,0,0,0},{1,1,1,1,1}},
		      {{1,1,1,1,0},{0,0,0,0,1},{0,1,1,1,0},{0,0,0,0,1},{1,1,1,1,0}},
		      {{1,0,0,1,0},{1,0,0,1,0},{1,1,1,1,1},{0,0,0,1,0},{0,0,0,1,0}},
		      {{1,1,1,1,1},{1,0,0,0,0},{0,1,1,1,0},{0,0,0,0,1},{1,1,1,1,0}},
		      {{0,0,1,1,0},{0,1,0,0,0},{1,1,1,1,0},{1,0,0,0,1},{0,1,1,1,0}},
		      {{1,1,1,1,1},{0,0,0,1,0},{0,0,1,0,0},{0,1,0,0,0},{0,1,0,0,0}},
		      {{0,1,1,1,0},{1,0,0,0,1},{0,1,1,1,0},{1,0,0,0,1},{0,1,1,1,0}},
		      {{0,1,1,1,0},{1,0,0,0,1},{0,1,1,1,1},{0,0,0,1,0},{0,1,1,0,0}}, // 9
		      {{0,0,1,0,0},{0,1,0,0,0},{1,1,1,1,1},{0,1,0,0,0},{0,0,1,0,0}}, // Flèche gauche
		      {{0,0,1,0,0},{0,0,0,1,0},{1,1,1,1,1},{0,0,0,1,0},{0,0,1,0,0}}, // Droite
		      {{0,0,0,0,0},{0,0,1,0,0},{0,0,0,0,0},{0,0,1,0,0},{0,0,0,0,0}}, // :
		      {{0,0,1,0,0},{0,1,0,1,0},{1,0,0,0,1},{0,1,0,1,0},{0,0,1,0,0}}, // losange
		      {{1,1,0,0,0},{0,0,1,0,0},{0,0,1,1,1},{0,0,1,0,0},{1,1,0,0,0}}, // 
		      {{0,0,0,0,1},{0,0,0,1,1},{0,0,1,0,1},{0,1,0,0,1},{1,1,1,1,1}}, //p=6
		      {{1,1,1,1,1},{1,0,1,0,1},{1,1,1,1,1},{1,0,1,0,1},{1,1,1,1,1}},
		      {{0,1,1,0,1},{1,0,0,1,0},{0,0,0,0,0},{0,1,1,0,1},{1,0,0,1,0}},
		      {{0,0,1,0,0},{0,0,0,0,0},{1,0,0,0,1},{0,0,0,0,0},{0,0,1,0,0}},
		      {{0,1,0,0,1},{1,0,0,1,1},{1,0,1,0,1},{1,1,0,0,1},{1,0,0,1,0}},
		      {{0,1,0,0,0},{1,0,1,0,0},{0,1,0,0,0},{0,0,0,0,0},{1,1,1,1,1}},
		      {{0,1,1,1,0},{1,0,1,0,1},{0,1,1,1,0},{0,1,0,1,0},{0,0,1,0,0}},
		      {{1,1,1,1,1},{0,1,0,1,0},{0,0,1,0,0},{0,1,0,1,0},{1,1,1,1,1}},
		      {{1,1,1,0,0},{1,0,1,0,0},{0,0,1,1,1},{0,0,1,0,1},{0,0,1,1,1}},
		      {{1,1,1,1,1},{0,0,0,0,1},{1,1,1,0,1},{0,0,1,0,1},{1,0,1,0,1}},
		      {{0,0,1,1,1},{0,1,0,0,0},{0,1,1,1,0},{1,0,0,0,0},{0,1,1,0,0}},
		      {{1,1,1,1,0},{0,0,0,0,1},{1,0,0,0,1},{1,0,0,0,0},{0,1,1,1,1}},
		      {{0,0,1,0,0},{0,0,0,1,0},{1,1,1,1,1},{0,1,0,0,0},{0,0,1,0,0}},
		      {{0,0,0,0,1},{0,0,0,1,0},{1,0,1,0,0},{0,1,0,0,0},{1,0,1,0,0}},
		      {{0,0,1,0,0},{0,0,0,0,0},{1,0,0,0,1},{1,0,0,0,1},{0,1,1,1,0}},
		      {{1,0,0,1,0},{0,1,0,0,0},{0,1,1,0,0},{1,0,0,1,0},{1,0,0,0,0}},
		      {{0,0,1,0,0},{0,1,1,1,0},{0,0,1,0,0},{0,0,1,0,0},{1,1,1,1,1}},
		      {{1,0,1,0,1},{0,1,1,1,0},{1,1,1,1,1},{0,1,1,1,0},{1,0,1,0,1}},
		      {{1,0,0,0,1},{1,0,0,0,1},{0,1,1,1,0},{1,0,0,0,1},{0,1,1,1,0}},
		      {{0,0,0,1,0},{0,0,0,1,0},{0,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0}}, // '
		      {{0,0,0,0,0},{0,0,0,0,0},{0,1,1,1,0},{0,0,0,0,0},{0,0,0,0,0}}}; // -

	int w, h;
	boolean orthoSetUp;
	
	Vector3f ambientColor;	// Current ambient color (could be null)
	
	public Ortho(int width, int height) {
		w=width;
		h=height;
		orthoSetUp=false;
		ambientColor=new Vector3f(1f, 1f, 1f);
	}
	
	public void setOrthographicProjection(boolean p_zoom) {
		if (!orthoSetUp) {
			// switch to projection mode
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			// save previous matrix which contains the 
			//settings for the perspective projection
			GL11.glPushMatrix();
			// reset matrix
			GL11.glLoadIdentity();
			// set a 2D orthographic projection
			if (p_zoom) {
				GL11.glOrtho(0, w/2, 0, h/2, -99999, 99999);
				GL11.glTranslatef(0, -h/2, 0);
			} else {
				GL11.glOrtho(0, w, 0, h, -99999, 99999);
			}
			// invert the y axis, down is positive
			//GL11.glScalef(1, -1, 1);
			//GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			// mover the origin from the bottom left corner
			// to the upper left corner
			GL11.glTranslatef(0, h, 0);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			
			orthoSetUp=true;
		}
	}

	/**
	 * Resize when using ZEditor
	 * @param p_x
	 * @param p_y
	 */
	public void setSize(int p_x, int p_y, boolean p_zoom) {
		// Change viewport
		GL11.glViewport(0,0, p_x, p_y);
		// And adapt ortho
		resetPerspectiveProjection();
		w=p_x;
		h=p_y;
		setOrthographicProjection(p_zoom);
	}
	
	public void resetPerspectiveProjection() {
		if (orthoSetUp) {
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glPopMatrix();
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			
			orthoSetUp=false;
		}
	}

	public void drawChar(int x, int y, char a) {
		int aa=a;
		if (aa!=32) {
			if (a=='.') {
				aa=26;
			} else if (a=='\'') {
				aa=62;
			} else if (a=='-') {
				aa=63;
			} else if (aa>=48 && aa<=57) {
				aa-=48-28;
			} else {
				if (aa>='a' && aa<='z') {
					aa-='a'; //-1;
				} else if (aa>199) {
					aa-=161;
				} else {
					aa-=64;
				}
			}
			if (aa>=0 && aa<fonts.length) {
				GL11.glBegin(GL11.GL_POINTS);
				for (int i=0;i<5;i++) {
					for (int j=0;j<5;j++) {
						char pixel=fonts[aa][j][i];
						if (pixel==1) {
							GL11.glVertex2i(x+i, y+j);
							GL11.glVertex2f(x+i+0.5f, y+j);
							//GL11.glVertex2f(x+i+0.5f, y+j+0.5f);
							//GL11.glVertex2f(x+i, y+j+0.5f);
						}
					}
				}
				GL11.glEnd();
			}
		}
	}
	
	public void drawText(int x, int y, String txt) {
		for (int i=0;i<txt.length();i++) {
			drawChar(x+6*i, y, txt.toLowerCase().charAt(i));
		}
	}
	public void drawText(int p_x, int p_y, String p_txt, Vector3f p_color) {
		// Center text
		int x=p_x;
		if (x == -1) {
			x=(Zildo.viewPortX - p_txt.length() * 6) / 2;
		}
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		float factor=0.2f;
		GL11.glColor3f(p_color.x*factor, p_color.y*factor, p_color.z*factor);
		drawText(x+1,p_y+1,p_txt);
		GL11.glColor3f(p_color.x, p_color.y, p_color.z);
		drawText(x,p_y,p_txt);
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		GL11.glEnable(GL11.GL_TEXTURE_2D);

	}
	
	public void box(Rectangle p_rect, int palColor, Vector4f color) {
		Point cornerTopLeft=p_rect.getCornerTopLeft();
		Point size=p_rect.getSize();
		box(cornerTopLeft.x, cornerTopLeft.y, size.x, size.y, palColor, color);
	}
	/**
	 * Draw a box on foreground (z=1) with desired color (palettized or not).
	 * This is not the right way for many boxes. Prefer (@link #boxOpti).
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param palColor index for palettized color
	 * @param color real color
	 */
	public void box(int x,int y, int w, int h, int palColor, Vector4f color) {
		initDrawBox(false);
		boxOpti(x, y, w, h, palColor, color);
		endDraw();
	}
	
	/**
	 * Just draw the colored box, without managing glBegin/glEnd
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param palColor
	 * @param color
	 */
	public void boxOpti(int x, int y, int w, int h, int palColor, Vector4f color) {
		Vector4f col=color;
		if (color == null) {
			col=new Vector4f(GFXBasics.getColor(palColor));
			col.scale(1.0f/256.0f);
		}
		GL11.glColor4f(col.x, col.y, col.z, col.w);
		GL11.glVertex2d(x, y);
		GL11.glVertex2d(x+w, y);
		GL11.glVertex2d(x+w, y+h);
		GL11.glVertex2d(x, y+h);
	}
	
	/**
	 * Just draw a textured box, without managing glBegin/glEnd
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param u
	 * @param v
	 * @param uw
	 * @param vh
	 */
	public void boxTexturedOpti(int x, int y, int w, int h, float u, float v, float uw, float vh) {
		GL11.glTexCoord2f(u, v);
		GL11.glVertex2d(x, y);
		GL11.glTexCoord2f(u+uw, v);
		GL11.glVertex2d(x+w, y);
		GL11.glTexCoord2f(u+uw, v+vh);
		GL11.glVertex2d(x+w, y+h);
		GL11.glTexCoord2f(u, v+vh);
		GL11.glVertex2d(x, y+h);
	}
	
	/**
	 * Initialize the right matrix to draw quads, and do a glBegin.
	 * @param withTexture
	 */
	public void initDrawBox(boolean withTexture) {
		// On se met au premier plan et on annule le texturing
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPushMatrix();
		GL11.glTranslatef(0,0,1);
		if (!withTexture) {
			GL11.glDisable(GL11.GL_TEXTURE_2D);
		}
		GL11.glBegin(GL11.GL_QUADS); 
	}
	
	/**
	 * Get back the original matrix, and go a glEnd.
	 */
	public void endDraw() {
		GL11.glEnd();
		// On se remet où on était et on réactive le texturing
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glColor3f(1.0f, 1.0f, 1.0f); //, 1.0f);
		GL11.glPopMatrix();
	}
	
	/**
	 * Draw an empty box on foreground (z=1), same way that box.
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param palColor
	 * @param color
	 */
	public void boxv(int x, int y, int w, int h, int palColor, Vector4f color) {
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		box(x ,y ,w ,h ,palColor, color);
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		
	}
	
	public void enableBlend() {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	public void disableBlend() {
		GL11.glDisable(GL11.GL_BLEND);
	}

	public Vector3f getAmbientColor() {
		return ambientColor;
	}

	public void setAmbientColor(Vector3f ambientColor) {
		this.ambientColor = ambientColor;
	}
}
