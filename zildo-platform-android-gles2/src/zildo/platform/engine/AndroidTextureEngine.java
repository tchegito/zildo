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

import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;

import zildo.Zildo;
import zildo.fwk.gfx.GraphicStuff;
import zildo.fwk.gfx.engine.TextureEngine;
import zildo.platform.opengl.utils.GLUtils;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
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
 *  GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureTab[i]);
 * where 'i' is the number of the generated texture
 *                   
 * @author tchegito
 *
 */
public class AndroidTextureEngine extends TextureEngine {
	
	Bitmap bitmap;
	BitmapFactory.Options opts;
	
	public AndroidTextureEngine(GraphicStuff graphicStuff) {
		super(graphicStuff);
        
		// This will tell the BitmapFactory to not scale based on the device's pixel density:
	    // (Thanks to Matthew Marshall for this bit)
	    opts = new BitmapFactory.Options();
	    opts.inScaled = false;
	}
    
	int[] pixels = new int[256 * 256];
	
    @Override
	public int doGenerateTexture() {

        // Create A IntBuffer For Image Address In Memory
        int id = GLUtils.genTextureId();
        
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
        
        // GLUtils.glTeximage2D was doing a multiplication of RGB by alpha channel, which isn't
        // what we want, because of modified pixels for guards.
        // If every pixel was 0, or 1 for alpha, it wouldn't need to do the following
        //GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, bitmap, 0);

        bitmap.getPixels(pixels, 0, 256, 0, 0, 256, 256);
        for (int i=0;i<pixels.length;i+=1) {
            int argb = pixels[i];
            pixels[i] = argb&0xff00ff00 | ((argb&0xff)<<16) | ((argb>>16)&0xff);
        }

        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, 256, 256, 
             0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, IntBuffer.wrap(pixels));
        
        // Free bitmap memory
    	bitmap.recycle();

        return id;
    }
    
    @Override
	public void getTextureImage(int p_texId) {
    	GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, p_texId);
    	// FIXME: this method doesn't exist in OpenGL ES, so we have to find another solution
    	// This method is only called for Zildo outfits : it's not urgent for now
    	//gL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, getTextureFormat(), GL11.GL_UNSIGNED_BYTE, scratch);
    }
    
    @Override
	public int loadTexture(String name) {
    	AssetFileDescriptor afd = (AssetFileDescriptor) Zildo.pdPlugin.openFd("textures/"+name+".png");
    	InputStream is = null;
		try {
			is = afd.createInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    bitmap = BitmapFactory.decodeStream(is, null, opts);
	    return super.generateTexture();
    	
    }
    @Override
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
