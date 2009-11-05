package zildo.fwk.opengl.compatibility;

/**
 * Allows to draw on a texture.
 * 
 * Supports two modes:
 * -software
 * -hardware
 * 
 * @author tchegito
 */
public interface FBO {

    public int create();

    public int generateDepthBuffer();

    public void bindToTextureAndDepth(int myTextureId, int myDepthId, int myFBOId);

    public void startRendering(int myFBOId, int sizeX, int sizeY);

    public void endRendering();

    public void cleanUp(int id);

    public void cleanDepthBuffer(int id);
}
