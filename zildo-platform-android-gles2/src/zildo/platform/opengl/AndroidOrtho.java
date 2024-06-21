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

package zildo.platform.opengl;

import shader.Shaders;
import zildo.Zildo;
import zildo.fwk.gfx.GFXBasics;
import zildo.fwk.gfx.Ortho;
import zildo.monde.util.Vector3f;
import zildo.monde.util.Vector4f;
import zildo.platform.opengl.utils.Bufferizer;
import zildo.platform.opengl.utils.ShortBufferizer;
import android.opengl.GLES20;
import android.util.Log;
import java.util.Locale;

/**
 * Class which provides direct draw on render screen, in an orthographic
 * context.
 * 
 * It means that there's any projection. The z-coordinate is just used to know
 * if an obect is behind or before another, but it doesn't lead to perspective
 * anymore.
 * 
 * NOTE: glPointSize calls have been removed, because it no longer exists in opengl es 2.
 *       The only routine that used it was the blendFilter, but it will be replaced by another
 *       technique using shader.
 * 
 * @author tchegito
 * 
 */
public class AndroidOrtho extends Ortho {

	Shaders shaders;
	
	// Private buffer in order to draw boxes (OpenGL ES doesn't allow single vertex definition
	// surrounded by 'begin/end'. So we have to use buffers even for simple things.
	ShortBufferizer verticesBuffer;
	Bufferizer texCoordBuffer;

	public AndroidOrtho(int width, int height) {
		super(width, height);

		verticesBuffer = new ShortBufferizer((Zildo.viewPortX+1) * 2 * 64);
		texCoordBuffer = new Bufferizer((Zildo.viewPortX+1) * 2 * 64);
	}
	

	
	@Override
	public void setOrthographicProjection(boolean p_zoom) {
		// 'zoom' variable isn't used
		if (!orthoSetUp) {
			Log.d("ortho", "set orthographic projection");

			if (shaders == null) {
				// Shaders should have been initialized
				shaders = AndroidPixelShaders.shaders;
			}
			shaders.setOrthographicProjection();
			
			GLES20.glDisable(GLES20.GL_CULL_FACE);
			GLES20.glDisable(GLES20.GL_BLEND);

			orthoSetUp = true;
		}
	}
	
	@Override
	public void resetPerspectiveProjection(int p_x, int p_y) {
		// No need to do that in Android
	}

	//TODO: Need to optimize seriously  ! The whole fonts could be saved before on a texture.
	@Override
	public void drawOneChar(int x, int y, int aa) {
		verticesBuffer.rewind();
		int count=0;
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				char pixel = fonts[aa][j][i];
				if (pixel == 1) {
					verticesBuffer.addInt(x + i, y + j);
					//verticesBuffer.addFloat(x + i + 0.5f, y + j);
					count+=2;
				}
			}
		}
		
		shaders.drawUntexture(verticesBuffer.rewind(), GLES20.GL_POINTS, count);
	}
	
	@Override
	public void drawText(int x, int y, String txt) {
		for (int i = 0; i < txt.length(); i++) {
			drawChar(x + 6 * i, y, txt.toLowerCase(Locale.getDefault()).charAt(i));
		}
	}

	@Override
	public void setColor(float r, float g, float b) {
		shaders.setColor(new Vector3f(r, g, b));
	}
	
	@Override
	public void enableTexture2d(boolean enable) {
		if (enable) {
			//GLES20.glEnable(GLES20.GL_TEXTURE_2D);
		} else {
			//GLES20.glDisable(GLES20.GL_TEXTURE_2D);
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
		fourDots(x, y, p_w, p_h, palColor, color, GLES20.GL_TRIANGLE_FAN);
	}

	private void fourDots(int x, int y, int p_w, int p_h, int palColor, Vector4f color, int mode) {
		Vector4f col = color;
		if (color == null) {
			col = new Vector4f(GFXBasics.getColor(palColor));
			col.scale(1.0f / 256.0f);
		}
		shaders.setColor(col);
		
		int[] vertices = {x,  y,
			    x + p_w, y,
			    x + p_w, y + p_h,
			    x, y + p_h};
				
		shaders.drawUntexture(verticesBuffer.storeAndFlip(vertices), mode, 4);
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
		int[] vertices = {x,  y,
			    x + p_w, y,
			    x + p_w, y + p_h,
			    x, y + p_h};
				
		float[] texCoords = {u, v, 
				u + uw, v,
				u + uw, v + vh,
				u, v + vh};
		
		shaders.drawTexture(verticesBuffer.storeAndFlip(vertices), 
							texCoordBuffer.storeAndFlip(texCoords), 
							GLES20.GL_TRIANGLE_FAN, 0, 4);
	}
	

	public void addPointTexturedOpti(int x, int y, float u, float v) {
		int[] vertices = {x,  y};

		float[] texCoords = {u, v};
		
		verticesBuffer.store(vertices);
		texCoordBuffer.store(texCoords);
	}
	
	public void addLineOpti(int x, int y, float x2, float y2) {
		int[] vertices = {x,  y, (int) x2, (int) y2};

		verticesBuffer.store(vertices);
	}
	
	public void addBoxOpti(int x, int y, float p_w, float p_h) {
		int[] vertices = {x,  y,
			    x + (int) p_w, y,
			    x, y + (int) p_h,
				x + (int) p_w, y,
			    x + (int) p_w, y + (int) p_h,
			    x, y + (int) p_h};

		verticesBuffer.store(vertices);
	}

	public void initOptiDraw() {
		verticesBuffer.rewind();
		texCoordBuffer.rewind();
	}
	
	public void drawGlPoints(int size) {
		drawTexturedBufferized(GLES20.GL_POINTS);
	}
	
	public void drawTexturedBufferized(int glMode) {
		drawBufferized(glMode, true);
	}
	
	public void drawBufferized(int glMode, boolean textured) {
		int count = verticesBuffer.getCount();
		switch (glMode) {
		case GLES20.GL_LINES:
		case GLES20.GL_POINTS:
			count>>= 1;
		} 
		if (textured) {
			shaders.drawTexture(verticesBuffer.rewind(), 
								texCoordBuffer.rewind(),
								glMode, 0, count);
		} else {
			shaders.drawUntexture(verticesBuffer.rewind(),	glMode, count);
		}
		verticesBuffer.rewind();
		if (textured) {
			texCoordBuffer.rewind();
		}
	}
	
	/**
	 * Initialize the right matrix to draw quads, and do a glBegin.
	 * 
	 * @param withTexture
	 */
	@Override
	public void initDrawBox(boolean withTexture) {
		// Disable texturing, if asked
		if (!withTexture) {
			//GLES20.glDisable(GLES20.GL_TEXTURE_2D);
		}
	}

	Vector4f saveColor = new Vector4f(1, 1, 1, 1);
	
	/**
	 * Get back the original matrix, and go a glEnd.
	 */
	@Override
	public void endDraw() {
		// Re-enable texturing and color
		//GLES20.glEnable(GLES20.GL_TEXTURE_2D);
		shaders.setColor(1, 1, 1, 1);
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
		fourDots(x, y, p_w, p_h, palColor, color, GLES20.GL_LINE_LOOP);
	}

	@Override
	public void enableBlend() {
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_COLOR, GLES20.GL_ONE_MINUS_SRC_ALPHA);
	}

	@Override
	public void disableBlend() {
		GLES20.glDisable(GLES20.GL_BLEND);
	}

}
