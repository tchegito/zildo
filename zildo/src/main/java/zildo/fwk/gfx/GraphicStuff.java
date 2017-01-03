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

    private Vector4f cColor = new Vector4f(0, 0, 0, 0);
	public Vector4f createColor(long value) {
        cColor.set(value & 255, (value >> 8) & 255, (value >> 16) & 255, value >> 24);
        cColor.w = 0.5f * 255.0f;
        return cColor;
    }
    public abstract VBO createVBO();
	public abstract float[] getFloat(int p_info,int p_size);
	public abstract int generateTexture(int sizeX, int sizeY);
    public abstract void cleanTexture(int id);
	public abstract void setCurrentColor(float[] p_color);
}
