package zildo.fwk.opengl;

import java.util.logging.Logger;

import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.vector.Vector4f;

import zildo.fwk.opengl.compatibility.FBO;
import zildo.fwk.opengl.compatibility.FBOHardware;
import zildo.fwk.opengl.compatibility.FBOSoftware;
import zildo.fwk.opengl.compatibility.VBO;
import zildo.fwk.opengl.compatibility.VBOHardware;
import zildo.fwk.opengl.compatibility.VBOSoftware;

public class OpenGLStuff {

    protected OpenGLGestion m_oglGestion;

    protected FBO fbo;
    protected VBO vbo;

    protected Logger logger = Logger.getLogger("MapManagement");

    // ////////////////////////////////////////////////////////////////////
    // Construction/Destruction
    // ////////////////////////////////////////////////////////////////////

    public OpenGLStuff() {
        m_oglGestion = null;
        if (isFBOSupported()) {
            fbo = new FBOHardware();
        } else {
            fbo = new FBOSoftware();
        }
        
        if (false && isVBOSupported()) {	// Don't use VBO now, it's slower than nothing !
        	vbo = new VBOHardware();
        } else {
        	vbo = new VBOSoftware();
        }
    }

    public OpenGLStuff(OpenGLGestion oglGestion) {
        // On récupère les pointeurs vers les objets OpenGL
        m_oglGestion = oglGestion;

    }
    
    /**
     * VBO support.
     * @return TRUE if the current hardware supports VBO.
     */
    protected boolean isVBOSupported() {
        return (GLContext.getCapabilities().GL_ARB_vertex_buffer_object);
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // FBO utils
    // ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // Frame Buffer Object : provide an offscreen render, which can be used further as a texture.
    // ///////////////////////////////////////////////////////////////////////////////////////////////////////
    protected boolean isFBOSupported() {
        return GLContext.getCapabilities().GL_EXT_framebuffer_object;
    }

    // /////////////////////////////////////////////////////////////////////////////////////
    // isPixelShaderSupported
    // /////////////////////////////////////////////////////////////////////////////////////
    protected boolean isPixelShaderSupported() {
        return GLContext.getCapabilities().GL_ARB_shader_objects && GLContext.getCapabilities().GL_ARB_fragment_shader
                && GLContext.getCapabilities().GL_ARB_vertex_shader && GLContext.getCapabilities().GL_ARB_shading_language_100;
    }

    // //////////////////////////////////////////////
    // Texture utils
    // //////////////////////////////////////////////
    // color: TRUE=color texture / FALSE=depth texture
    protected int generateTexture(int sizeX, int sizeY) {
        int textureId = Utils.generateTexture(sizeX, sizeY);

        logger.info("Created texture " + textureId);
        return textureId;
    }

    protected int generateDepthBuffer() {
        int depthId = fbo.generateDepthBuffer();

        logger.info("Created depth buffer " + depthId);
        return depthId;
    }

    public Vector4f createColor64(float r, float g, float b) {
        return new Vector4f(r / 63.0f, g / 63.0f, b / 63.0f, 1.0f);
    }

    public Vector4f createColor(long value) {
        Vector4f v = new Vector4f(value & 255, (value >> 8) & 255, (value >> 16) & 255, value >> 24);
        v.w = 0.5f * 255.0f;
        return v;
    }

    protected void cleanFBO(int id) {
        fbo.cleanUp(id);
        logger.info("Deleted FBO " + id);
    }

    protected void cleanTexture(int id) {
        Utils.cleanTexture(id);
        logger.info("Deleted texture " + id);
    }

    protected void cleanDepthBuffer(int id) {
        fbo.cleanDepthBuffer(id);
        logger.info("Deleted depth buffer " + id);
    }
}
