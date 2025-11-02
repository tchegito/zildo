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

package shader;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;
import java.util.Map;

import zildo.Zildo;
import zildo.monde.util.Point;
import zildo.monde.util.Vector2f;
import zildo.monde.util.Vector3f;
import zildo.monde.util.Vector4f;
import zildo.platform.opengl.AndroidPixelShaders;
import android.opengl.GLES20;
import android.opengl.Matrix;

/**
 * @author Tchegito e
 * 
 */
public class Shaders {
	
	public enum GLShaders {
		// Basic one (flat and textured. 'Textured' can be clipped)
		uniColor, textured,
		// Filters
		pixelateFilter, circleFilter,
		// Specific for guards, and wounded enemies
		switchColor, wounded,
		// Gold (for invincibility)
		goldFilter,
		// Star (aura for black guards)
		star,
		// Blur with fade (used on game loading)
		blackBlur,
		// Fire
		fire,
		// Grid rotative transition
		circular;
		
		public int id;	// program id
		// One map for all shaders
		static Map<String, Integer> uniforms = new HashMap<String, Integer>();
		
		public void setUniforms(String... names) {
			for (String s : names) {
				int uniformId = GLES20.glGetUniformLocation(id, s);
				uniforms.put(""+id+s, uniformId);
			}
		}
		
		public int getUniform(String name) {
			Integer i = uniforms.get(""+id+name);
			return i == null ? -1 : i.intValue();	// NPE is handled this way => return -1
		}
	}
	
	final static int nbShaders = GLShaders.values().length;
	
	// attribute handles
	int hTexturedPosition;
	int hUntexturedPosition;
	int hTexturedTexPosition;
	
	float[] orthoMatrix;
	
	// All varying values
	Vector4f curColor = new Vector4f(1, 1, 1, 1);
	Vector2f translation = new Vector2f(0, 0);
	int squareSize;
	int radius;
	boolean clip;
	Vector2f center = new Vector2f(0, 0);
	Vector4f[] switchedColors = new Vector4f[4];
	Vector4f woundedColor = new Vector4f(1, 1, 1, 1);
	float goldFactor;
	Vector2f starNoise;
	Vector4f fireTime;
	
	GLShaders current = GLShaders.textured;	// Default is 'textured'
	
	AndroidPixelShaders aps;
	
	
	public Shaders(AndroidPixelShaders p_aps) {
		
		aps = p_aps;

		load();
		
		// Init switched colors array
		for (int i=0;i<switchedColors.length;i++) {
			switchedColors[i] = new Vector4f(0, 0, 0, 0);
		}
	}
	
	public void load() {
		
		// Compile and link all shaders
		for (GLShaders sh : GLShaders.values()) {
			aps.loadCompleteShader(sh);
		}

        // get handle to the shader attributes
		hTexturedPosition = GLES20.glGetAttribLocation(GLShaders.textured.id, "vPosition");
		hTexturedTexPosition = GLES20.glGetAttribLocation(GLShaders.textured.id, "TexCoord");
		
		hUntexturedPosition = GLES20.glGetAttribLocation(GLShaders.uniColor.id, "vPosition");
		
	}
	
	/**
	 * Draw textured elements in orthographic mode.
	 * @param verticesBuffer
	 * @param textureBuffer
	 * @param elementType (GL_POINTS, GL_TRIANGLES, an so on ...)
	 * @param start first to draw
	 * @param count element's count
	 */
	public void drawTexture(ShortBuffer verticesBuffer, FloatBuffer textureBuffer,
							int elementType, int start, int count) {
		// Add program to OpenGL environment
        GLES20.glUseProgram(current.id);
        
        // Prepare the vertices data
        GLES20.glVertexAttribPointer(hTexturedPosition, 2, GLES20.GL_SHORT, false, 0, verticesBuffer);
        GLES20.glEnableVertexAttribArray(hTexturedPosition);
        
        // Prepare the texture data
        GLES20.glVertexAttribPointer(hTexturedTexPosition, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
        GLES20.glEnableVertexAttribArray(hTexturedTexPosition);
        
        int hTextureIndex = current.getUniform("sTexture");
        if (hTextureIndex != -1) {
        	GLES20.glUniform1i(hTextureIndex, 0);
        }
        
        int hTexturedOrthoMatrix = current.getUniform("uMVPMatrix");
		GLES20.glUniformMatrix4fv(hTexturedOrthoMatrix, 1, false, orthoMatrix, 0);
		
		specificShaders();

        GLES20.glDrawArrays(elementType, start, count);
	}
	
	public void drawIndicedAndTexture(ShortBuffer verticesBuffer, FloatBuffer textureBuffer,
									  ShortBuffer indicesBuffer, int elementType, int count) {
		// Add program to OpenGL environment
        GLES20.glUseProgram(current.id);
        
        // Prepare the vertices data
        GLES20.glVertexAttribPointer(hTexturedPosition, 2, GLES20.GL_SHORT, false, 0, verticesBuffer);
        GLES20.glEnableVertexAttribArray(hTexturedPosition);
        
        // Prepare the texture data
        GLES20.glVertexAttribPointer(hTexturedTexPosition, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
        GLES20.glEnableVertexAttribArray(hTexturedTexPosition);
        
        int hTextureIndex = current.getUniform("sTexture");
        GLES20.glUniform1i(hTextureIndex, 0); 

        int hTexturedOrthoMatrix = current.getUniform("uMVPMatrix");
		GLES20.glUniformMatrix4fv(hTexturedOrthoMatrix, 1, false, orthoMatrix, 0);

		specificShaders();

		GLES20.glDrawElements(elementType, count, GLES20.GL_UNSIGNED_SHORT, indicesBuffer);
	}
	
	private void specificShaders() {
		switch (current) {
			case pixelateFilter:
				uniform1f("squareSize", squareSize);
				uniform4f("CurColor", curColor);
				break;
			case circleFilter:
				GLES20.glUniform1i(current.getUniform("radius"), radius);
				uniform2f("center", center);
				uniform4f("CurColor", curColor);
				break;
			case switchColor:
				uniform4f("Color1", switchedColors[0]);
				uniform4f("Color2", switchedColors[1]);
				uniform4f("Color3", switchedColors[2]);
				uniform4f("Color4", switchedColors[3]);
				uniform4f("CurColor", curColor);
				break;
			case wounded:
				uniform4f("randomColor", woundedColor);
				break;
			case goldFilter:
				uniform1f("factor", goldFactor);
				break;
			case star:
				uniform2f("noise", starNoise);
				uniform4f("CurColor", curColor);
				break;
			case fire:
				uniform4f("iGlobalTime", fireTime);
				break;
			default:
				uniform4f("CurColor", curColor);
				uniform2f("vTranslate", translation);
				uniform1i("clip", clip ? 1 : 0);
				break;
		}
	}

	private int uniformId(String uniformName) {
		return current.getUniform(uniformName);
	}
	
	/** Convenient method to pass a 4-dimension vector via an uniform. **/
	public void uniform4f(String uniformName, Vector4f v) {
		GLES20.glUniform4f(uniformId(uniformName), v.x, v.y, v.z, v.w);
	}
	
	/** Convenient method to pass a 2-dimension vector via an uniform. **/
	private void uniform2f(String uniformName, Vector2f v) {
		GLES20.glUniform2f(uniformId(uniformName), v.x, v.y);
	}

	/** Convenient method to pass a float via an uniform. **/
	public void uniform1f(String uniformName, float f) {
		GLES20.glUniform1f(uniformId(uniformName), f);
	}
	
	private void uniform1i(String uniformName, int i) {
		GLES20.glUniform1i(uniformId(uniformName), i);
	}
	
	/**
	 * Draw untextured elements in orthographic mode.
	 * @param verticesBuffer
	 * @param elementType (GL_POINTS, GL_TRIANGLES, an so on ...)
	 * @param count element's count
	 */
	public void drawUntexture(ShortBuffer verticesBuffer, int elementType, int count) {
		// Add program to OpenGL environment
        GLES20.glUseProgram(GLShaders.uniColor.id);
        
        // Prepare the triangle data
        GLES20.glVertexAttribPointer(hUntexturedPosition, 2, GLES20.GL_SHORT, false, 0, verticesBuffer);
		GLES20.glEnableVertexAttribArray(hUntexturedPosition);

        int hMatrix = GLShaders.uniColor.getUniform("uMVPMatrix");
		GLES20.glUniformMatrix4fv(hMatrix, 1, false, orthoMatrix, 0);

		int hColor = GLShaders.uniColor.getUniform("CurColor");
		GLES20.glUniform4f(hColor, curColor.x, curColor.y, curColor.z, curColor.w);

        GLES20.glDrawArrays(elementType, 0, count);
	}
	
	public void setColor(Vector3f col) {
		curColor.set(col.x, col.y, col.z, 1f);
	}
	
	public void setColor(Vector4f col) {
		curColor.set(col.x, col.y, col.z, col.w);
	}
	
	public void setColor(Vector4f v, float a) {
		curColor.set(v.x, v.y, v.z, a);
	}
	
	public void setColor(float r, float g, float b, float a) {
		curColor.set(r, g, b, a);
	}

	public void setTranslation(Vector2f translate) {
		translation.set(translate.x, translate.y);
	}
	
	public void setOrthographicProjection() {
		orthoMatrix = new float[16];
	
		Matrix.orthoM(orthoMatrix, 0, 0, Zildo.viewPortX, Zildo.viewPortY, 0, -1f, 1f);
	}

	public void setCurrentShader(GLShaders sh) {
		current = sh;
	}
	
	public void setBlendSquareSize(int size) {
		squareSize = size;
	}
	
	public void setCircleParams(int radius, Point center) {
		this.radius = radius;
		this.center.set(center.x, center.y);
	}
	
	public void setSwitchColors(Vector4f[] tab) {
		switchedColors[0].set(tab[2]);
		switchedColors[1].set(tab[3]);
		switchedColors[2].set(tab[0]);
		switchedColors[3].set(tab[1]);
	}
	
	public void setWoundedColor(Vector4f v) {
		woundedColor.set(v);
	}
	
	public void setStarNoise(float f, float g) {
		starNoise = new Vector2f(f, g);
	}

	public void setFireTime(Vector4f time) {
		 fireTime = time;
	}

	public void setGoldFactor(float f) {
		goldFactor = f;
	}
	
	public void setClip(boolean v) {
		clip = v;
	}
}