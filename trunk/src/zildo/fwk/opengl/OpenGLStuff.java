package zildo.fwk.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBDepthTexture;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.vector.Vector4f;

public class OpenGLStuff {

	protected OpenGLGestion m_oglGestion;

	
	//////////////////////////////////////////////////////////////////////
	// Construction/Destruction
	//////////////////////////////////////////////////////////////////////
	
	public OpenGLStuff() {
		m_oglGestion=null;
	}
	
	public OpenGLStuff(OpenGLGestion oglGestion) {
		// On récupère les pointeurs vers les objets OpenGL
		m_oglGestion=oglGestion;
	
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	// VBO utils
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Vertex Buffer Object : permet de stocker des données (vertex, indices, textures, normales) dans la
	// VRAM pour accélerer le rendu. Similaire au combiné des Vertex Buffer et des Indices Buffer de DirectX.
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
    public int createVBO() {
    	if (GLContext.getCapabilities().GL_ARB_vertex_buffer_object) {
   			IntBuffer buffer= BufferUtils.createIntBuffer(1);
    		ARBVertexBufferObject.glGenBuffersARB(buffer);
    		return buffer.get();
	    } else {
	    	throw new RuntimeException("Unable to create VBO");
	    }
    	
    }
    
    // Vertex Buffer
    public void bufferData(int id, FloatBuffer buffer) {
    	  if (GLContext.getCapabilities().GL_ARB_vertex_buffer_object) {
    	    ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, id);
    	    ARBVertexBufferObject.glBufferDataARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, buffer, ARBVertexBufferObject.GL_STATIC_DRAW_ARB);
    	  }
    	}

    // Indices buffer
    public void bufferData(int id, IntBuffer buffer) {
		if (GLContext.getCapabilities().GL_ARB_vertex_buffer_object) {
			ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, id);
			ARBVertexBufferObject.glBufferDataARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, buffer, ARBVertexBufferObject.GL_STATIC_DRAW_ARB);
		}
    }
    
	////////////////////////////////////////////////
	// Texture utils
	////////////////////////////////////////////////
    // color: TRUE=color texture / FALSE=depth texture
    public int generateTexture(int sizeX, int sizeY, boolean color) {
	    IntBuffer buf = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
	    GL11.glGenTextures(buf); // Create Texture In OpenGL
	    int textureID=buf.get(0);
	    GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
	    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
	    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
	    
	    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_COMPARE_FUNC, GL11.GL_LEQUAL);
	    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_DEPTH_TEXTURE_MODE, GL11.GL_LUMINANCE);
	    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_COMPARE_MODE, GL14.GL_COMPARE_R_TO_TEXTURE);
	    
	    int texType=GL11.GL_RGBA;
	    if (!color) {
	    	GL11.glTexParameteri(GL11.GL_TEXTURE_2D, ARBDepthTexture.GL_DEPTH_TEXTURE_MODE_ARB, GL11.GL_INTENSITY);
	    	texType=GL11.GL_DEPTH_COMPONENT;
	    }
	    GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, texType,  adjustTexSize(sizeX), adjustTexSize(sizeY), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
	    
	    return textureID;
	}
    
	////////////////////////////////////////////////
	// FBO utils
	////////////////////////////////////////////////
    public int createFBO() {
    	if (GLContext.getCapabilities().GL_EXT_framebuffer_object) {
		    IntBuffer buffer = ByteBuffer.allocateDirect(1*4).order(ByteOrder.nativeOrder()).asIntBuffer(); // allocate a 1 int byte buffer
		    EXTFramebufferObject.glGenFramebuffersEXT( buffer ); // generate 
		    return buffer.get();
    	} else {
	    	throw new RuntimeException("Unable to create FBO");
    	}
    }
    
    public void bindFBOToTexture(int myTextureId, int myFBOId, boolean color) {
    	// On bind le FBO à la texture
    	int texType=EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT;
    	if (!color) {
    		texType=EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT;
    	}
	    EXTFramebufferObject.glBindFramebufferEXT( EXTFramebufferObject.GL_FRAMEBUFFER_EXT, myFBOId );
	    EXTFramebufferObject.glFramebufferTexture2DEXT( EXTFramebufferObject.GL_FRAMEBUFFER_EXT, texType,
	                    GL11.GL_TEXTURE_2D, myTextureId, 0);
	    // Puis on détache la texture de la vue
    	EXTFramebufferObject.glBindFramebufferEXT( EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);

    }
 
    public void startRenderingOnFBO(int myFBOId, int sizeX, int sizeY) {
	    EXTFramebufferObject.glBindFramebufferEXT( EXTFramebufferObject.GL_FRAMEBUFFER_EXT, myFBOId );
	    GL11.glPushAttrib(GL11.GL_VIEWPORT_BIT);
	    GL11.glViewport( 0, 0, 320, 240 );
	    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
	    GL11.glDepthMask(true);
	}
    
    public void endRenderingOnFBO() {
    	EXTFramebufferObject.glBindFramebufferEXT( EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
    	GL11.glPopAttrib();
    }
    	
	protected void checkCompleteness(int myFBOId) {
		int framebuffer = EXTFramebufferObject.glCheckFramebufferStatusEXT( EXTFramebufferObject.GL_FRAMEBUFFER_EXT ); 
		switch ( framebuffer ) {
			case EXTFramebufferObject.GL_FRAMEBUFFER_COMPLETE_EXT:
				break;
			case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT:
				throw new RuntimeException( "FrameBuffer: " + myFBOId
						+ ", has caused a GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT exception" );
			case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT:
				throw new RuntimeException( "FrameBuffer: " + myFBOId
						+ ", has caused a GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT exception" );
			case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT:
				throw new RuntimeException( "FrameBuffer: " + myFBOId
						+ ", has caused a GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT exception" );
			case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT:
				throw new RuntimeException( "FrameBuffer: " + myFBOId
						+ ", has caused a GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT exception" );
			case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT:
				throw new RuntimeException( "FrameBuffer: " + myFBOId
						+ ", has caused a GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT exception" );
			case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT:
				throw new RuntimeException( "FrameBuffer: " + myFBOId
						+ ", has caused a GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT exception" );
			default:
				throw new RuntimeException( "Unexpected reply from glCheckFramebufferStatusEXT: " + framebuffer );
		}		
	}
	
	public Vector4f createColor64(float r, float g, float b) {
		return new Vector4f(r/63.0f, g/63.0f, b/63.0f,1.0f);
	}
	
	public Vector4f createColor(long value) {
		Vector4f v=new Vector4f(value & 255, (value>>8)&255, (value>>16)&255, value >>24);
		v.w=0.5f * 255.0f;
		return v;
	}
	
	/**
	 * OpenGL likes "adjusted" size for texture. We take multiple of 256.
	 * @param n Initial size
	 * @return Adjusted size 
	 */
	static public int adjustTexSize(int n) {
		if (n % 256 == 0) {
			return n;
		}
		return (n & 0xff00) + 256;
	}
}	