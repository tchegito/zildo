package zildo.fwk.gfx;

import java.util.logging.Logger;

import zildo.fwk.opengl.compatibility.FBO;
import zildo.fwk.opengl.compatibility.VBO;
import zildo.monde.util.Vector4f;

public abstract class GraphicStuff {


    public FBO fbo;
    protected VBO vbo;

    protected Logger logger = Logger.getLogger("GraphicStuff");

    public void cleanFBO(int id) {
        fbo.cleanUp(id);
        logger.info("Deleted FBO " + id);
    }

    public void cleanDepthBuffer(int id) {
        fbo.cleanDepthBuffer(id);
        logger.info("Deleted depth buffer " + id);
    }
	
    public int generateDepthBuffer() {
        int depthId = fbo.generateDepthBuffer();

        logger.info("Created depth buffer " + depthId);
        return depthId;
    }
    
    public abstract VBO createVBO();
	public abstract float[] getFloat(int p_info,int p_size);
	public abstract int generateTexture(int sizeX, int sizeY);
    public abstract void cleanTexture(int id);
    public abstract Vector4f createColor(long value);
	public abstract void setCurrentColor(float[] p_color);
}
