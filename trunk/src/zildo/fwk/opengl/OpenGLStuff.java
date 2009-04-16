package zildo.fwk.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.logging.Logger;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.vector.Vector4f;

import zildo.Zildo;

public class OpenGLStuff {

	protected OpenGLGestion m_oglGestion;

	protected Logger logger=Logger.getLogger("MapManagement");
	
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
    public int generateTexture(int sizeX, int sizeY) {
	    IntBuffer buf = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
	    GL11.glGenTextures(buf); // Create Texture In OpenGL
	    int textureID=buf.get(0);
	    GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
	    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
	    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
	    
	    GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA,  adjustTexSize(sizeX), adjustTexSize(sizeY), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
	    
	    logger.info("Created texture "+textureID);
	    return textureID;
	}

    public int generateDepthBuffer() {
	    IntBuffer buf = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
	    EXTFramebufferObject.glGenRenderbuffersEXT(buf); // Create Texture In OpenGL
    	int depthID=buf.get(0);
    	
	    EXTFramebufferObject.glBindRenderbufferEXT( EXTFramebufferObject.GL_RENDERBUFFER_EXT, depthID );
    	EXTFramebufferObject.glRenderbufferStorageEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, GL11.GL_DEPTH_COMPONENT, adjustTexSize(Zildo.viewPortX), adjustTexSize(Zildo.viewPortY));
    	
	    logger.info("Created depth buffer "+depthID);
    	return depthID;
    }
	////////////////////////////////////////////////
	// FBO utils
	////////////////////////////////////////////////
    public int createFBO() {
    	if (GLContext.getCapabilities().GL_EXT_framebuffer_object) {
		    IntBuffer buffer = ByteBuffer.allocateDirect(1*4).order(ByteOrder.nativeOrder()).asIntBuffer(); // allocate a 1 int byte buffer
		    EXTFramebufferObject.glGenFramebuffersEXT( buffer ); // generate 
		    logger.info("Created FBO "+buffer.get(0));
		    return buffer.get();
    	} else {
	    	throw new RuntimeException("Unable to create FBO");
    	}
    }

    public void bindFBOToTextureAndDepth(int myTextureId, int myDepthId, int myFBOId) {
    	// On bind le FBO à la texture
	    EXTFramebufferObject.glBindFramebufferEXT( EXTFramebufferObject.GL_FRAMEBUFFER_EXT, myFBOId );
	    if (myDepthId > 0) {
	        EXTFramebufferObject.glFramebufferRenderbufferEXT( EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT,
		    		EXTFramebufferObject.GL_RENDERBUFFER_EXT, myDepthId);
	    }
    	EXTFramebufferObject.glFramebufferTexture2DEXT( EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT,
                GL11.GL_TEXTURE_2D, myTextureId, 0);

    	// Puis on détache la texture de la vue
    	EXTFramebufferObject.glBindFramebufferEXT( EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
    }
 
    public void startRenderingOnFBO(int myFBOId, int sizeX, int sizeY) {
	    EXTFramebufferObject.glBindFramebufferEXT( EXTFramebufferObject.GL_FRAMEBUFFER_EXT, myFBOId );

	    GL11.glPushAttrib(GL11.GL_VIEWPORT_BIT);
	    GL11.glViewport( 0, 0, Zildo.viewPortX, Zildo.viewPortY );
	    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
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
	
	public IntBuffer getBufferWithId(int id) {
		IntBuffer buf=BufferUtils.createIntBuffer(1);
		buf.put(id);
		buf.rewind();
		return buf;
	}
	public void cleanFBO(int id) {
		EXTFramebufferObject.glDeleteFramebuffersEXT(getBufferWithId(id));
		logger.info("Deleted FBO "+id);
	}
	
	public void cleanTexture(int id) {
		GL11.glDeleteTextures(getBufferWithId(id));
		logger.info("Deleted texture "+id);
	}
	
	public void cleanDepthBuffer(int id) {
		EXTFramebufferObject.glDeleteRenderbuffersEXT(getBufferWithId(id));
		logger.info("Deleted depth buffer "+id);
	}
}	