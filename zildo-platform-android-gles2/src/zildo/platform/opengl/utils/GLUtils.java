/**
 * Legend of Zildo
 * Copyright (C) 2006-2012 Evariste Boussaton
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

package zildo.platform.opengl.utils;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import zildo.fwk.ZUtils;
import android.opengl.GLES20;

/**
 * @author eboussaton
 */
public class GLUtils {

	private static int current = 0;
	
	public static void resetTexId() {
		current=0;
	}
	public static int genTextureId() {
		return ++current;
	}
	
    public static int generateTexture(int sizeX, int sizeY) {
        int textureID = genTextureId();
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        
        int wrapping=GLES20.GL_REPEAT;	// Wrap texture (useful for cloud)
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, wrapping);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, wrapping);
        
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, ZUtils.adjustTexSize(sizeX), ZUtils.adjustTexSize(sizeY), 0, GLES20.GL_RGBA,
                GLES20.GL_UNSIGNED_BYTE, (ByteBuffer) null);

        return textureID;
    }

    public static void cleanTexture(int id) {
    	GLES20.glDeleteTextures(1, ZUtils.getBufferWithId(id));
    }

    public static int createVBO() {
    	int[] buffer = new int[1];
    	GLES20.glGenBuffers(1, buffer, 0);
    	return buffer[0];
    }
    
    // Vertex & Indices Buffer
    public static void bufferData(int id, Buffer buffer, boolean statically) {
    	//glBufferData(GL_ARRAY_BUFFER, vertex_size+color_size, 0, GL_STATIC_DRAW);
    	int mode = GLES20.GL_DYNAMIC_DRAW;
    	if (statically) {
    		mode = GLES20.GL_STATIC_DRAW;
    	}
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, id);
        int size = buffer.remaining();
        if (buffer instanceof ShortBuffer) {
        	size = size << 1;
        } else {
        	size = size << 2;
        }
       	GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, size, buffer, mode);
    }

    public static void copyScreenToTexture(int p_texId, int p_sizeX, int p_sizeY) {
    	GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, p_texId);
    	System.out.println("trying to bind framebuffer");
    	//((GLES20ExtensionPack)GLES20).glBindFramebufferOES();
    	GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
    	GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

    	GLES20.glCopyTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, 0, 0, p_sizeX, p_sizeY, 0);
    }
}