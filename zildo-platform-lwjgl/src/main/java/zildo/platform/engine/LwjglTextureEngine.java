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

package zildo.platform.engine;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import zildo.fwk.gfx.GraphicStuff;
import zildo.fwk.gfx.engine.TextureEngine;
import zildo.resource.Constantes;

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
public class LwjglTextureEngine extends TextureEngine {
    
	public LwjglTextureEngine(GraphicStuff graphicStuff) {
		super(graphicStuff);
	}
	
    private int getTextureFormat() {
    	if (alphaChannel) {
    		return GL11.GL_RGBA;
    	} else {
    		return GL11.GL_RGB;
    	}
    }
    @Override
	public int doGenerateTexture() {
		scratch.position(0);

        // Create A IntBuffer For Image Address In Memory
        IntBuffer buf = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
        GL11.glGenTextures(buf); // Create Texture In OpenGL

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, buf.get(0));
        // Typical Texture Generation Using Data From The Image

        int wrapping=GL11.GL_REPEAT;	// Wrap texture (useful for cloud)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, wrapping);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, wrapping);
        
        int filtering=GL11.GL_NEAREST;
        // Linear Filtering
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filtering);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filtering);
        // Generate The Texture
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, getTextureFormat(), 256, 256, 0, getTextureFormat(), 
        		GL11.GL_UNSIGNED_BYTE, scratch);
        
        return buf.get(0);
    }
    
    public void deleteTexture(int id) {
        GL11.glDeleteTextures(id);
    }
    
    @Override
	public void getTextureImage(int p_nthTex) {
    	if (scratch == null) {
    		prepareSurfaceForTexture(true);
    	}
	    GL11.glBindTexture(GL11.GL_TEXTURE_2D, getNthTexture(p_nthTex));
	    scratch.position(0);
	    GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, getTextureFormat(), GL11.GL_UNSIGNED_BYTE, scratch);
    }
    
    @Override
	public int loadTexture(String name) {
	    try {
			Texture tex = TextureLoader.getTexture("PNG", new FileInputStream(Constantes.DATA_PATH+"textures/"+name+".png"));
			
			int id = tex.getTextureID();
			
	        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
	        // Typical Texture Generation Using Data From The Image

	        int wrapping=GL11.GL_REPEAT;	// Wrap texture (useful for cloud)
	        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, wrapping);
	        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, wrapping);
	        
	        int filtering=GL11.GL_NEAREST;
	        // Linear Filtering
	        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filtering);
	        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filtering);
	 
	        
	        // Store texture id
	        textureTab[n_Texture] = id;
	        alphaTab[n_Texture] = true;
	        
	        // Ready for next one
	        n_Texture++;
	        
			return id;
		} catch (Exception e) {
			throw new RuntimeException("Can't load texture "+name, e.getCause());
		}
    	
    }
    
    @Override
    public void init() {
    	if (n_Texture != 0) {
    		// Case where texture already exists (only in ZEditor for reload)
    		for (int i=0;i<textureTab.length;i++) {
    			deleteTexture(textureTab[i]);
    		}
    	}
    	super.init();
    }
    
    /**
    public void saveScreen(int p_texId) {

		// Draw texture with depth
    	GLUtils.copyScreenToTexture(p_texId, 1024, 512);
    }
    */
}
