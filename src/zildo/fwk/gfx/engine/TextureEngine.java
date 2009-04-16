package zildo.fwk.gfx.engine;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import org.lwjgl.devil.IL;
import org.lwjgl.opengl.GL11;

import zildo.fwk.GFXBasics;
import zildo.fwk.opengl.OpenGLStuff;
import zildo.prefs.Constantes;

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
    private ByteBuffer scratch;
    
    public TextureEngine() {
		// Initialize number of textures
		n_Texture=0;
	
		textureTab=new int[Constantes.NB_MOTIFBANK + Constantes.NB_SPRITEBANK];

    }
    
    public void finalize() {
		// Free the allocated textures
		for (int i=0;i<n_Texture;i++) {
			//SafeRelease(ppTexture[i]);
		}
    }
    
    public GFXBasics prepareSurfaceForTexture() {
        // Create image
        scratch = ByteBuffer.allocateDirect(256 * 256 * 4);
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

        int wrapping=GL11.GL_CLAMP;
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, wrapping);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, wrapping);
        
        int filtering=GL11.GL_NEAREST;
        // Linear Filtering
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filtering);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filtering);
        // Generate The Texture
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, IL.ilGetInteger(IL.IL_IMAGE_WIDTH), 
                IL.ilGetInteger(IL.IL_IMAGE_HEIGHT), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, scratch);
        
        // Reset bytebuffer scratch
        scratch.clear();
        
        // Store texture id
        textureTab[n_Texture]=buf.get(0);

        // Ready for next one
		n_Texture++;    	
    }
}
