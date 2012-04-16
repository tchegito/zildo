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

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.opengl.GLU;
import android.util.Log;

import zildo.Zildo;
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
public class AndroidOrtho extends Ortho {

	GL11 gl11;
	
	// Private buffer in order to draw boxes (OpenGL ES doesn't allow single vertex definition
	// surrounded by 'begin/end'. So we have to use buffers even for simple things.
	Bufferizer verticesBuffer;
	Bufferizer texCoordBuffer;

	public AndroidOrtho(int width, int height) {
		super(width, height);
		
		gl11 = (GL11) AndroidOpenGLGestion.gl10;
		verticesBuffer = new Bufferizer(25 * 4);
		texCoordBuffer = new Bufferizer(25 * 4);
	}

	public void setGL(GL11 gl) {
		gl11 = gl;
	}
	
	@Override
	public void setOrthographicProjection(boolean p_zoom) {
		// 'zoom' variable isn't used
		if (!orthoSetUp) {
			Log.d("ortho", "set orthographic projection");
			// switch to projection mode
			gl11.glMatrixMode(GL11.GL_PROJECTION);
			// save previous matrix which contains the
			// settings for the perspective projection
			//gl11.glPushMatrix();
			// reset matrix
			gl11.glViewport(0, 0, Zildo.viewPortX, Zildo.viewPortY);
			gl11.glLoadIdentity();
			// set a 2D orthographic projection
			GLU.gluOrtho2D(gl11, 0, (float)Zildo.viewPortX, (float)Zildo.viewPortY, 0); 
			//gl11.glOrthof(0, Zildo.viewPortX, 0, Zildo.viewPortY, -99999, 99999);
			// invert the y axis, down is positive
			gl11.glScalef(1, 1, 1);
			// GL11.glDisable(GL11.GL_DEPTH_TEST);
			gl11.glDisable(GL11.GL_CULL_FACE);
			gl11.glDisable(GL11.GL_BLEND);
			gl11.glDisable(GL11.GL_ALPHA_TEST);
			// mover the origin from the bottom left corner
			// to the upper left corner
			//gl.glTranslatef(0, 480, 0);
			gl11.glMatrixMode(GL11.GL_MODELVIEW);

			orthoSetUp = true;
		}
	}

	public void forceOrtho(int sizeX, int sizeY) {
		orthoSetUp = false;
		Zildo.viewPortX = sizeX;
		Zildo.viewPortY = sizeY;
		setOrthographicProjection(false);
	}
	
	@Override
	public void resetPerspectiveProjection(int p_x, int p_y) {
		// No need to do that in Android
	}

	@Override
	public void drawOneChar(int x, int y, int aa) {
		verticesBuffer.rewind();
		int count=0;
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				char pixel = fonts[aa][j][i];
				if (pixel == 1) {
					verticesBuffer.addFloat(x + i, y + j);
					verticesBuffer.addFloat(x + i + 0.5f, y + j);
					count+=2;
				}
			}
		}
		
		gl11.glEnableClientState (GL10.GL_VERTEX_ARRAY);
		gl11.glVertexPointer (2, GL10.GL_FLOAT, 0, verticesBuffer.rewind());	
		gl11.glDrawArrays (GL10.GL_POINTS, 0, count);	
	}
	
	@Override
	public void drawText(int x, int y, String txt) {
		for (int i = 0; i < txt.length(); i++) {
			drawChar(x + 6 * i, y, txt.toLowerCase().charAt(i));
		}
	}

	@Override
	public void setColor(float r, float g, float b) {
		// FIXME: previously color3f
		gl11.glColor4f(r, g, b, 1f);
	}
	
	@Override
	public void enableTexture2d(boolean enable) {
		if (enable) {
			gl11.glEnable(GL11.GL_TEXTURE_2D);
		} else {
			gl11.glDisable(GL11.GL_TEXTURE_2D);
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
		fourDots(x, y, p_w, p_h, palColor, color, GL10.GL_TRIANGLE_FAN);
	}

	private void fourDots(int x, int y, int p_w, int p_h, int palColor, Vector4f color, int mode) {
		Vector4f col = color;
		if (color == null) {
			col = new Vector4f(GFXBasics.getColor(palColor));
			col.scale(1.0f / 256.0f);
		}
		gl11.glColor4f(col.x, col.y, col.z, col.w);
		
		float[] vertices = {x,  y,
			    x + p_w, y,
			    x + p_w, y + p_h,
			    x, y + p_h};
				
		gl11.glDisable(GL11.GL_TEXTURE_2D);
		gl11.glEnableClientState (GL10.GL_VERTEX_ARRAY);
		gl11.glVertexPointer (2, GL10.GL_FLOAT, 0, verticesBuffer.store(vertices));	
		gl11.glDrawArrays (mode, 0, 4);		
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
		float[] vertices = {x,  y,
			    x + p_w, y,
			    x + p_w, y + p_h,
			    x, y + p_h};
				
		float[] texCoords = {u, v, 
				u + uw, v,
				u + uw, v + vh,
				u, v + vh};
		
		gl11.glEnable(GL11.GL_TEXTURE_2D);
		gl11.glEnableClientState (GL10.GL_VERTEX_ARRAY);
		gl11.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl11.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texCoordBuffer.store(texCoords));
		gl11.glVertexPointer (2, GL10.GL_FLOAT, 0, verticesBuffer.store(vertices));
		gl11.glDrawArrays (GL10.GL_TRIANGLE_FAN, 0, 4);
	}

	/**
	 * Initialize the right matrix to draw quads, and do a glBegin.
	 * 
	 * @param withTexture
	 */
	@Override
	public void initDrawBox(boolean withTexture) {
		// On se met au premier plan et on annule le texturing
		gl11.glMatrixMode(GL11.GL_MODELVIEW);
		gl11.glPushMatrix();
		gl11.glTranslatef(0, 0, 1);
		if (!withTexture) {
			gl11.glDisable(GL11.GL_TEXTURE_2D);
		}
	}

	/**
	 * Get back the original matrix, and go a glEnd.
	 */
	@Override
	public void endDraw() {
		// On se remet où on était et on réactive le texturing
		gl11.glEnable(GL11.GL_TEXTURE_2D);
		// FIXME: was a color3f
		gl11.glColor4f(1.0f, 1.0f, 1.0f , 1.0f);
		gl11.glPopMatrix();
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
		fourDots(x, y, p_w, p_h, palColor, color, GL10.GL_LINE_LOOP);
	}

	@Override
	public void enableBlend() {
		gl11.glEnable(GL11.GL_BLEND);
		gl11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	@Override
	public void disableBlend() {
		gl11.glDisable(GL11.GL_BLEND);
	}

}
