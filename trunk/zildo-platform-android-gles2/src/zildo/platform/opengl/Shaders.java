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

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import zildo.Zildo;
import zildo.monde.util.Vector3f;
import zildo.monde.util.Vector4f;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

/**
 * @author Tchegito
 *
 */
public class Shaders {

	// Untextured program
	private final String orthoVertexShader = 
		"uniform mat4 uMVPMatrix;  \n" +	// Ortho matrix
        "attribute vec2 vPosition; \n" +	// Vertex position
        "void main(){              \n" +
        "	gl_Position = uMVPMatrix * vec4(vPosition, 0.0, 1.0); \n" +
        "}                         \n";
	
	
	private final String orthoFragmentShader =
        //"precision mediump float;  \n" +
		"uniform lowp vec4 CurColor;		\n" +
		"void main(){						\n" +
		"	gl_FragColor = CurColor;\n"+
		"}	\n";
	
	// Textured program
	
	private final String orthoVertexTexturedShader = 
        "attribute vec2 vPosition;	\n" +	// Vertex position
		"attribute vec2 TexCoord;	\n" +
		"uniform mat4 uMVPMatrix;	\n" +	// Ortho matrix
		"varying mediump vec2 vTexCoord;	\n" +
        "void main(){				\n" +
        "	gl_Position = uMVPMatrix * vec4(vPosition, 0.0, 1.0); \n" +
        "	vTexCoord=TexCoord;	\n" +
        "}							\n";
	
	private final String orthoFragmentTexturedShader =
        "precision mediump float;  \n" +
		"uniform sampler2D sTexture;		\n" +
		"varying mediump vec2 vTexCoord;	\n" +
		"void main(){						\n" +
		"	gl_FragColor = texture2D(sTexture, vTexCoord);\n"+
		"}	\n";

	
	// programs
	int mPTexturedOrtho;
	int mPUntexturedOrtho;
	
	// attribute handles
	int hTexturedPosition;
	int hUntexturedPosition;
	int hTexturedTexPosition;
	int hTexturedOrthoMatrix;
	int hUntexturedOrthoMatrix;
	int hTextureIndex;
	int hColor;
	
	float[] orthoMatrix;
	
	AndroidPixelShaders aps;
	
	public Shaders(AndroidPixelShaders p_aps) {
		
		aps = p_aps;
		
		// Create all basic shaders
		mPTexturedOrtho = aps.loadCompleteShader(orthoVertexTexturedShader, orthoFragmentTexturedShader);
        // get handle to the shader attributes
		hTexturedPosition = GLES20.glGetAttribLocation(mPTexturedOrtho, "vPosition");
		hTexturedTexPosition = GLES20.glGetAttribLocation(mPTexturedOrtho, "TexCoord");
		// get handle to shader uniforms
		hTexturedOrthoMatrix = GLES20.glGetUniformLocation(mPTexturedOrtho, "uMVPMatrix");
		hTextureIndex = GLES20.glGetUniformLocation(mPTexturedOrtho, "sTexture");

		
		
		mPUntexturedOrtho = aps.loadCompleteShader(orthoVertexShader, orthoFragmentShader);

		hUntexturedPosition = GLES20.glGetAttribLocation(mPUntexturedOrtho, "vPosition");
		hUntexturedOrthoMatrix = GLES20.glGetUniformLocation(mPUntexturedOrtho, "uMVPMatrix");

		
		hColor = GLES20.glGetUniformLocation(mPUntexturedOrtho, "CurColor");

		
		
		Log.d("shaders", "handle for hTexturedOrthoMatrix = "+hTexturedOrthoMatrix);
		Log.d("shaders", "handle for hTexturedPosition = "+hTexturedPosition);
		Log.d("shaders", "handle for hTexturedTexPosition = "+hTexturedTexPosition);
		Log.d("shaders", "handle for hColor = "+hColor);

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
        GLES20.glUseProgram(mPTexturedOrtho);
        
        // Prepare the vertices data
        GLES20.glVertexAttribPointer(hTexturedPosition, 2, GLES20.GL_UNSIGNED_SHORT, false, 0, verticesBuffer);
        GLES20.glEnableVertexAttribArray(hTexturedPosition);
        
        // Prepare the texture data
        GLES20.glVertexAttribPointer(hTexturedTexPosition, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
        GLES20.glEnableVertexAttribArray(hTexturedTexPosition);
        
        GLES20.glUniform1i(hTextureIndex, 0); 
        
		GLES20.glUniformMatrix4fv(hTexturedOrthoMatrix, 1, false, orthoMatrix, 0);

        GLES20.glDrawArrays(elementType, start, count);
	}
	
	public void drawIndicedAndTexture(ShortBuffer verticesBuffer, FloatBuffer textureBuffer,
									  ShortBuffer indicesBuffer, int elementType, int count) {
		// Add program to OpenGL environment
        GLES20.glUseProgram(mPTexturedOrtho);
        
        // Prepare the vertices data
        GLES20.glVertexAttribPointer(hTexturedPosition, 2, GLES20.GL_UNSIGNED_SHORT, false, 0, verticesBuffer);
        GLES20.glEnableVertexAttribArray(hTexturedPosition);
        
        // Prepare the texture data
        GLES20.glVertexAttribPointer(hTexturedTexPosition, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
        GLES20.glEnableVertexAttribArray(hTexturedTexPosition);
        
        GLES20.glUniform1i(hTextureIndex, 0); 

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
        GLES20.glUseProgram(mPUntexturedOrtho);
        
        // Prepare the triangle data
        GLES20.glVertexAttribPointer(hUntexturedPosition, 2, GLES20.GL_UNSIGNED_SHORT, false, 0, verticesBuffer);
        GLES20.glEnableVertexAttribArray(hUntexturedPosition);

		GLES20.glUniformMatrix4fv(hUntexturedOrthoMatrix, 1, false, orthoMatrix, 0);

        GLES20.glDrawArrays(elementType, 0, count);
	}

	public void setColor(Vector3f col) {
		GLES20.glUniform4f(hColor, col.x, col.y, col.z, 1f);
	}
	
	public void setColor(Vector4f col) {
		GLES20.glUniform4f(hColor, col.x, col.y, col.z, 1f);
	}
	
	public void setOrthographicProjection() {
		orthoMatrix = new float[16];
	
		Matrix.orthoM(orthoMatrix, 0, 0, Zildo.viewPortX, Zildo.viewPortY, 0, -1f, 1f);
	}

}
