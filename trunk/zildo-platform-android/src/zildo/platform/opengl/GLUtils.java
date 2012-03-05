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

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import zildo.fwk.ZUtils;

/**
 * @author eboussaton
 */
public class GLUtils {

	static GL10 gl10;
	
    public static int generateTexture(int sizeX, int sizeY) {
        IntBuffer buf = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
        gl10.glGenTextures(1, buf); // Create Texture In OpenGL
        int textureID = buf.get(0);
        gl10.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
        gl10.glTexParameterx(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        gl10.glTexParameterx(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        
        gl10.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, ZUtils.adjustTexSize(sizeX), ZUtils.adjustTexSize(sizeY), 0, GL11.GL_RGBA,
                GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);

        return textureID;
    }

    public static void cleanTexture(int id) {
    	gl10.glDeleteTextures(1, ZUtils.getBufferWithId(id));
    }

    public static int createVBO() {
        IntBuffer buffer = ZUtils.createIntBuffer(1);
        //ARBVertexBufferObject.glGenBuffersARB(buffer);
        return buffer.get();    	
    }
    
    // Vertex & Indices Buffer
    public static void bufferData(int id, Buffer buffer) {
    	/*
        ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, id);
        if (buffer instanceof FloatBuffer) {
            ARBVertexBufferObject.glBufferDataARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, (FloatBuffer) buffer,
                    ARBVertexBufferObject.GL_STREAM_DRAW_ARB);
        } else if (buffer instanceof IntBuffer) {
            ARBVertexBufferObject.glBufferDataARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, (IntBuffer) buffer,
                    ARBVertexBufferObject.GL_STREAM_DRAW_ARB);
        }
        */
    }

    public static void copyScreenToTexture(int p_texId, int p_sizeX, int p_sizeY) {
    	gl10.glBindTexture(GL11.GL_TEXTURE_2D, p_texId);

    	gl10.glTexParameterx(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
    	gl10.glTexParameterx(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

    	gl10.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, 0, 0, p_sizeX, p_sizeY, 0);
    }
}