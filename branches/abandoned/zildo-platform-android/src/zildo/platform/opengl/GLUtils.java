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
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL11;

import zildo.fwk.ZUtils;

/**
 * @author eboussaton
 */
public class GLUtils {

	static GL11 gl11;
	
    public static int generateTexture(int sizeX, int sizeY) {
        IntBuffer buf = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
        gl11.glGenTextures(1, buf); // Create Texture In OpenGL
        int textureID = buf.get(0);
        gl11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
        gl11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        gl11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        
        int wrapping=GL11.GL_REPEAT;	// Wrap texture (useful for cloud)
        gl11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, wrapping);
        gl11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, wrapping);
        
        gl11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, ZUtils.adjustTexSize(sizeX), ZUtils.adjustTexSize(sizeY), 0, GL11.GL_RGBA,
                GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);

        return textureID;
    }

    public static void cleanTexture(int id) {
    	gl11.glDeleteTextures(1, ZUtils.getBufferWithId(id));
    }

    public static int createVBO() {
    	int[] buffer = new int[1];
    	gl11.glGenBuffers(1, buffer, 0);
    	return buffer[0];
    }
    
    // Vertex & Indices Buffer
    public static void bufferData(int id, Buffer buffer, boolean statically) {
    	//glBufferData(GL_ARRAY_BUFFER, vertex_size+color_size, 0, GL_STATIC_DRAW);
    	int mode = GL11.GL_DYNAMIC_DRAW;
    	if (statically) {
    		mode = GL11.GL_STATIC_DRAW;
    	}
        gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, id);
        int size = buffer.remaining();
        if (buffer instanceof ShortBuffer) {
        	size = size << 1;
        } else {
        	size = size << 2;
        }
       	gl11.glBufferData(GL11.GL_ARRAY_BUFFER, size, buffer, mode);
    }

    public static void copyScreenToTexture(int p_texId, int p_sizeX, int p_sizeY) {
    	gl11.glBindTexture(GL11.GL_TEXTURE_2D, p_texId);
    	System.out.println("trying to bind framebuffer");
    	//((GL11ExtensionPack)gl11).glBindFramebufferOES();
    	gl11.glTexParameterx(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
    	gl11.glTexParameterx(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

    	gl11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, 0, 0, p_sizeX, p_sizeY, 0);
    }
}