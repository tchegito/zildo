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

package shader;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import zildo.Zildo;
import zildo.monde.util.Vector2f;
import zildo.monde.util.Vector3f;
import zildo.monde.util.Vector4f;
import zildo.platform.opengl.AndroidPixelShaders;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

/**
 * @author Tchegito
 *
 */
public class Shaders {
	
	enum GLShaders {
		// Basic one (flat and textured)
		uniColor, textured,
		// Filters
		blendFilter, circleFilter,
		// Specific for guards, and wounded enemies
		switchColor;
		
		public int id;	// program id
	}
	
	final static int nbShaders = GLShaders.values().length;
	
	// attribute handles
	int hTexturedPosition;
	int hTextureTranslation;
	int hUntexturedPosition;
	int hTexturedTexPosition;
	int hTexturedOrthoMatrix;
	int hUntexturedOrthoMatrix;
	int hTextureIndex;
	int hColor;
	int hTexturedColor;
	
	float[] orthoMatrix;
	
	Vector4f curColor = new Vector4f(1, 1, 1, 1);
	Vector2f translation = new Vector2f(0, 0);
	
	AndroidPixelShaders aps;
	
	public Shaders(AndroidPixelShaders p_aps) {
		
		aps = p_aps;
		
		// Compile and link all shaders
		for (GLShaders sh : GLShaders.values()) {
			sh.id = aps.loadCompleteShader(sh.toString());
		}
		
		//mPTexturedOrtho = aps.loadCompleteShader("textured");
        // get handle to the shader attributes
		hTexturedPosition = GLES20.glGetAttribLocation(GLShaders.textured.id, "vPosition");
		hTexturedTexPosition = GLES20.glGetAttribLocation(GLShaders.textured.id, "TexCoord");
		// get handle to shader uniforms
		hTexturedOrthoMatrix = GLES20.glGetUniformLocation(GLShaders.textured.id, "uMVPMatrix");
		hTextureIndex = GLES20.glGetUniformLocation(GLShaders.textured.id, "sTexture");

		hTexturedColor = GLES20.glGetUniformLocation(GLShaders.textured.id, "CurColor");
		hTextureTranslation = GLES20.glGetUniformLocation(GLShaders.textured.id, "vTranslate");
		
		hUntexturedPosition = GLES20.glGetAttribLocation(GLShaders.uniColor.id, "vPosition");
		hUntexturedOrthoMatrix = GLES20.glGetUniformLocation(GLShaders.uniColor.id, "uMVPMatrix");
		hColor = GLES20.glGetUniformLocation(GLShaders.uniColor.id, "CurColor");
		
		
		Log.d("shaders", "handle for hTexturedOrthoMatrix = "+hTexturedOrthoMatrix);
		Log.d("shaders", "handle for hTexturedPosition = "+hTexturedPosition);
		Log.d("shaders", "handle for hTexturedTexPosition = "+hTexturedTexPosition);
		Log.d("shaders", "handle for hColor = "+hColor);
		Log.d("shaders", "handle for hTexturedColor = "+hTexturedColor);
		Log.d("shaders", "handle for hTextureTranslation = "+hTextureTranslation);

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
        GLES20.glUseProgram(GLShaders.textured.id);
        
        // Prepare the vertices data
        GLES20.glVertexAttribPointer(hTexturedPosition, 2, GLES20.GL_SHORT, false, 0, verticesBuffer);
        GLES20.glEnableVertexAttribArray(hTexturedPosition);
        
        // Prepare the texture data
        GLES20.glVertexAttribPointer(hTexturedTexPosition, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
        GLES20.glEnableVertexAttribArray(hTexturedTexPosition);
        
        GLES20.glUniform1i(hTextureIndex, 0); 
        
		GLES20.glUniformMatrix4fv(hTexturedOrthoMatrix, 1, false, orthoMatrix, 0);
		GLES20.glUniform2f(hTextureTranslation, translation.x, translation.y);
		GLES20.glUniform4f(hTexturedColor, curColor.x, curColor.y, curColor.z, curColor.w);

        GLES20.glDrawArrays(elementType, start, count);
	}
	
	public void drawIndicedAndTexture(ShortBuffer verticesBuffer, FloatBuffer textureBuffer,
									  ShortBuffer indicesBuffer, int elementType, int count) {
		// Add program to OpenGL environment
        GLES20.glUseProgram(GLShaders.textured.id);
        
        // Prepare the vertices data
        GLES20.glVertexAttribPointer(hTexturedPosition, 2, GLES20.GL_SHORT, false, 0, verticesBuffer);
        GLES20.glEnableVertexAttribArray(hTexturedPosition);
        
        // Prepare the texture data
        GLES20.glVertexAttribPointer(hTexturedTexPosition, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
        GLES20.glEnableVertexAttribArray(hTexturedTexPosition);
        
        GLES20.glUniform1i(hTextureIndex, 0); 
		GLES20.glUniform2f(hTextureTranslation, translation.x, translation.y);
		GLES20.glUniform4f(hTexturedColor, curColor.x, curColor.y, curColor.z, curColor.w);

		GLES20.glUniformMatrix4fv(hTexturedOrthoMatrix, 1, false, orthoMatrix, 0);

		GLES20.glDrawElements(elementType, count, GLES20.GL_UNSIGNED_SHORT, indicesBuffer);
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

		GLES20.glUniformMatrix4fv(hUntexturedOrthoMatrix, 1, false, orthoMatrix, 0);
		GLES20.glUniform4f(hColor, curColor.x, curColor.y, curColor.z, curColor.w);

        GLES20.glDrawArrays(elementType, 0, count);
	}

	public void setColor(Vector3f col) {
		curColor.set(col.x, col.y, col.z, 1f);
	}
	
	public void setColor(Vector4f col) {
		curColor.set(col.x, col.y, col.z, col.w);
	}
	
	public void setColor(float r, float g, float b, float a) {
		curColor.set(r, g, b, a);
	}
	
	public Vector4f getColor() {
		return curColor;
	}

	public void setTranslation(Vector2f translate) {
		translation.set(translate.x, translate.y);
	}
	
	public void setOrthographicProjection() {
		orthoMatrix = new float[16];
	
		Matrix.orthoM(orthoMatrix, 0, 0, Zildo.viewPortX, Zildo.viewPortY, 0, -1f, 1f);
	}

}
