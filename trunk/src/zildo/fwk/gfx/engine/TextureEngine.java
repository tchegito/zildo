/**
 * Legend of Zildo
 * Copyright (C) 2006-2011 Evariste Boussaton
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

package zildo.fwk.gfx.engine;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;

import zildo.fwk.gfx.GFXBasics;
import zildo.fwk.opengl.OpenGLStuff;
import zildo.fwk.opengl.GLUtils;
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
public abstract class TextureEngine extends OpenGLStuff {

    protected int n_Texture;

    public int[] textureTab;
    int textureFormat;	// Current texture's format
    protected ByteBuffer scratch;

    
    public TextureEngine() {
    	
		// Initialize number of textures
		n_Texture=0;
	
		textureTab=new int[Constantes.NB_MOTIFBANK + Constantes.NB_SPRITEBANK];
    }
    
    @Override
	public void finalize() {
		// Free the allocated textures
		for (int i=0;i<n_Texture;i++) {
			//SafeRelease(ppTexture[i]);
		}
    }
    
    public GFXBasics prepareSurfaceForTexture(boolean p_alpha) {
        // Create image
    	if (p_alpha) {
    		scratch = ByteBuffer.allocateDirect(256 * 256 * 4);
    		textureFormat = GL11.GL_RGBA;
    	} else {
    		scratch = ByteBuffer.allocateDirect(256 * 256 * 3);
    		textureFormat = GL11.GL_RGB;
    	}
		GFXBasics surface=new GFXBasics(true);
		surface.SetBackBuffer(scratch, 256, 256);
    	
		return surface;
    }
    
    public void generateTexture() {

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
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, textureFormat, 256, 256, 0, textureFormat, 
        		GL11.GL_UNSIGNED_BYTE, scratch);
        
        // Reset bytebuffer scratch
        scratch.clear();
        
        // Store texture id
        textureTab[n_Texture]=buf.get(0);

        // Ready for next one
        n_Texture++;
    }
    
    public void getTextureImage(int p_texId) {
	    GL11.glBindTexture(GL11.GL_TEXTURE_2D, p_texId);
	    GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, textureFormat, GL11.GL_UNSIGNED_BYTE, scratch);
    }
    
    public void saveScreen(int p_texId) {

		// Draw texture with depth
    	GLUtils.copyScreenToTexture(p_texId, 1024, 512);
    }
}
