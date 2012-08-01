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

package zildo.platform.engine;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import zildo.Zildo;
import zildo.fwk.gfx.GraphicStuff;
import zildo.fwk.gfx.engine.TextureEngine;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

/**
 * Abstract class which provides management of a texture set.
 * 
 * The first phase is the texture creation. It must be done like this:
 * -call prepareSurfaceForTexture()
 * -draw things on the GFXBasics returned
 * -call generateTexture() for adding texture to the texture set
 * 
 * To tell the openGL engine that it must draw with the right texture, just call:
 *  GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureTab[i]);
 * where 'i' is the number of the generated texture
 *                   
 * @author tchegito
 *
 */
public class AndroidTextureEngine extends TextureEngine {
	
	IntBuffer buf;
	
	Bitmap bitmap;
	Matrix flip;
	BitmapFactory.Options opts;
	
	public AndroidTextureEngine(GraphicStuff graphicStuff) {
		super(graphicStuff);
        buf = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
        
		// This will tell the BitmapFactory to not scale based on the device's pixel density:
	    // (Thanks to Matthew Marshall for this bit)
	    opts = new BitmapFactory.Options();
	    opts.inScaled = false;

	    // We need to flip the textures vertically:
	    flip = new Matrix();
	    flip.postScale(1f, -1f);
	}
    
    @Override
	public int doGenerateTexture() {

        // Create A IntBuffer For Image Address In Memory
        GLES20.glGenTextures(1, buf); // Create Texture In OpenGL

        int id = buf.get(0);
        
        Log.d("texture", "generate texture "+id);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id);
        // Typical Texture Generation Using Data From The Image

        int wrapping=GLES20.GL_REPEAT;	// Wrap texture (useful for cloud)
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, wrapping);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, wrapping);
        
        int filtering=GLES20.GL_NEAREST;
        // Linear Filtering
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, filtering);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, filtering);
        
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        return id;
    }
    
    @Override
	public void getTextureImage(int p_texId) {
    	GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, p_texId);
    	// FIXME: this method doesn't exist in OpenGL ES, so we have to find another solution
    	// This method is only called for Zildo outfits : it's not urgent for now
    	//gL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, getTextureFormat(), GL11.GL_UNSIGNED_BYTE, scratch);
    }
    
    public int loadTexture(String name) {
    	AssetFileDescriptor afd = (AssetFileDescriptor) Zildo.pdPlugin.openFd("textures/"+name);
	    Bitmap temp = BitmapFactory.decodeFileDescriptor(afd.getFileDescriptor(), null, opts);
	    bitmap = Bitmap.createBitmap(temp, 0, 0, temp.getWidth(), temp.getHeight(), flip, true);
	    temp.recycle();
	    
	    return super.generateTexture();
    	
    }
    public int generateTexture() { 
	    throw new RuntimeException("Should never been called ! All is going by #loadTexture method.");
    }
    
    /**
    public void saveScreen(int p_texId) {

		// Draw texture with depth
    	GLUtils.copyScreenToTexture(p_texId, 1024, 512);
    }
    */
}
