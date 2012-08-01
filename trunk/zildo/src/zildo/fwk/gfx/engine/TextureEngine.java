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

package zildo.fwk.gfx.engine;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import zildo.fwk.gfx.GFXBasics;
import zildo.fwk.gfx.GraphicStuff;
import zildo.monde.util.Vector4f;
import zildo.resource.Constantes;

/**
 * Abstract class which provides management of a texture set.<p/>
 * 
 * The first phase is the texture creation. It must be done like this:<ul>
 * <li>call prepareSurfaceForTexture()</li>
 * <li>draw things on the GFXBasics returned</li>
 * <li>call generateTexture() for adding texture to the texture set</li>
 * </ul>
 * To tell the openGL engine that it must draw with the right texture, just call:<br/>
 *  <code>GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureTab[i]);</code><br/>
 * where 'i' is the number of the generated texture
 *                   
 * @author tchegito
 *
 */
public abstract class TextureEngine {

    protected int n_Texture;

    protected int[] textureTab;
    protected boolean alphaChannel;	// Current texture's format (TRUE=RGBA / FALSE=RGB)
    protected ByteBuffer scratch;

    public GraphicStuff graphicStuff;
    
    public TextureEngine(GraphicStuff graphStuff) {
    	
    	graphicStuff = graphStuff;
    	
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
    	if (scratch == null) {	// Allocate only once
	    	if (p_alpha) {
	    		scratch = ByteBuffer.allocateDirect(256 * 256 * 4);
	    	} else {
	    		scratch = ByteBuffer.allocateDirect(256 * 256 * 3);
	    	}
    	}
    	alphaChannel = p_alpha;
		GFXBasics surface=new GFXBasics(true);
		surface.SetBackBuffer(scratch, 256, 256);
    	
        // Reset bytebuffer scratch
		scratch.clear();
		surface.clear(new Vector4f(0, 0, 0, 0));
		return surface;
    }
    
    /**
     * The real call to OpenGL methods
     * @return int texture ID
     */
    public abstract int doGenerateTexture();

    public int generateTexture() { 
    	
        scratch.position(0);
    	int idTexture = doGenerateTexture();
        
        // Store texture id
        textureTab[n_Texture]=idTexture;

        // Ready for next one
        n_Texture++;
        
        return textureTab[n_Texture-1];
    }
    
    public void saveImage(String filename) {
    	BufferedImage bufImage = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB);
		scratch.position(0);
    	for (int y = 0;y<256;y++) {
    		for (int x = 0;x<256;x++) {
				int r = 0xff & scratch.get();
				int g = 0xff & scratch.get();
				int b = 0xff & scratch.get();
				if (alphaChannel) {
					scratch.get();	// alpha channel => unused
				}
				int rgb = r << 16 | g << 8 | b;
				bufImage.setRGB(x,  y, rgb);
	   		}
    	}
    	try {
			ImageIO.write(bufImage, "png", new File(filename+".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public int getNthTexture(int nth) {
    	return textureTab[nth];
    }
    
    public ByteBuffer getBuffer() {
    	return scratch;
    }
    
    public void cleanTextures() {
		for (int i=0;i<n_Texture;i++) {
			int id=textureTab[i];
			graphicStuff.cleanTexture(id);
		}
    }
    
    public void init() {
    	n_Texture = 0;
    }
    
    public void saveAllTextures(String name) {
		for (int i=0;i<n_Texture;i++) {
			int id=textureTab[i];
			getTextureImage(id);
			saveImage(name+i);
		}
    }
    
    public abstract void getTextureImage(int p_texId);
/*    
    public void saveScreen(int p_texId) {

		// Draw texture with depth
    	GLUtils.copyScreenToTexture(p_texId, 1024, 512);
    }
    */
}
