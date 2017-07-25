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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.GL11;

import zildo.Zildo;
import zildo.fwk.ZUtils;
import zildo.fwk.gfx.GFXBasics;

/**
 * @author eboussaton
 */
public class GLUtils {

    public static int generateTexture(int sizeX, int sizeY) {
        IntBuffer buf = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
        GL11.glGenTextures(buf); // Create Texture In OpenGL
        int textureID = buf.get(0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, ZUtils.adjustTexSize(sizeX), ZUtils.adjustTexSize(sizeY), 0, GL11.GL_RGBA,
                GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);

        return textureID;
    }

    public static void cleanTexture(int id) {
        GL11.glDeleteTextures(ZUtils.getBufferWithId(id));
    }

    public static int createVBO() {
        IntBuffer buffer = BufferUtils.createIntBuffer(1);
        ARBVertexBufferObject.glGenBuffersARB(buffer);
        return buffer.get();    	
    }
    
    // Vertex & Indices Buffer
    public static void bufferData(int id, Buffer buffer, boolean statically) {
    	int mode = ARBVertexBufferObject.GL_DYNAMIC_DRAW_ARB;
    	if (statically) {
    		mode = ARBVertexBufferObject.GL_STATIC_DRAW_ARB;
    	}
        ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, id);
        if (buffer instanceof FloatBuffer) {
            ARBVertexBufferObject.glBufferDataARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, (FloatBuffer) buffer,
            		mode);
        } else if (buffer instanceof IntBuffer) {
            ARBVertexBufferObject.glBufferDataARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, (IntBuffer) buffer,
            		mode);
        } else if (buffer instanceof ShortBuffer) {
            ARBVertexBufferObject.glBufferDataARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, (ShortBuffer) buffer,
            		mode);
        }
    }

    public static void copyScreenToTexture(int p_texId, int p_sizeX, int p_sizeY) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, p_texId);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, 0, 0, p_sizeX, p_sizeY, 0);
    }
    
    /**
     * Save a ByteBuffer as a PNG file, with given parameters.
     * @param filename file name with full path, without ".png"
     * @param scratch byte buffer containing data to save
     * @param width
     * @param height
     * @param alpha
     */
    public static void saveBufferAsPNG(String filename, ByteBuffer scratch, int width, int height, boolean alpha) {
    	int format = BufferedImage.TYPE_INT_RGB;
    	if (alpha) {
    		format = BufferedImage.TYPE_INT_ARGB;
        }
    	BufferedImage bufImage = new BufferedImage(width, height, format);
		scratch.position(0);
    	for (int y = 0;y<height;y++) {
    		for (int x = 0;x<width;x++) {
				int r = 0xff & scratch.get();
				int g = 0xff & scratch.get();
				int b = 0xff & scratch.get();
				int a = 0;
				if (alpha) {
					a = 0xff & scratch.get();
				}
				int argb = a << 24 | r << 16 | g << 8 | b;
				bufImage.setRGB(x,  y, argb);
	   		}
    	}
    	try {
			ImageIO.write(bufImage, "png", new File(filename+".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}    	
    }
    
    /**
     * Save an int buffer as a PNG file, with given parameters.
     * @param filename file name with full path, without ".png"
     * @param scratch byte buffer containing data to save
     * @param width
     * @param height
     */
    public static void saveBufferAsPNG(String filename, int[] buf, int width, int height) {
    	int format = BufferedImage.TYPE_INT_RGB;
    	BufferedImage bufImage = new BufferedImage(width, height, format);
    	int a = 0;
    	for (int y = 0;y<height;y++) {
    		for (int x = 0;x<width;x++) {
				bufImage.setRGB(x,  y, GFXBasics.getIntColor(buf[a++]));
	   		}
    	}
    	try {
			ImageIO.write(bufImage, "png", new File(filename+".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}    	
    }
    /**
     * Save a ByteBuffer as a PNG file, with given parameters.
     * @param filename file name with full path, without ".png"
     * @param scratch byte buffer containing data to save
     * @param width
     * @param height
     * @param alpha
     */
    public static void saveBufferAsPNG(String filename, BufferedImage bufImage) {
    	try {
			ImageIO.write(bufImage, "png", new File(filename+".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}    	
    }
    
    /**
     * Copy a portion of a 640x480 sized image into a bigger one.
     */
	public static void copy(ByteBuffer source, ByteBuffer dest, int width, int height, int destWidth, int dx, int dy, int addY, boolean alpha) {
		int bpp = 3 + (alpha ? 1 : 0);
    	for (int y = 0;y<height;y++) {
    		source.position(bpp * Zildo.screenX * (height - y - 1 + addY));
    		dest.position(bpp * (destWidth * (dy + y) + dx));
    		for (int x = 0;x<width;x++) {
				dest.put(source.get());
				dest.put(source.get());
				dest.put(source.get());
				if (alpha) {
					dest.put(source.get());
				}
	   		}
    	}
	}
}