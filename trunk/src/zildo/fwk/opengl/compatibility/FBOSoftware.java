package zildo.fwk.opengl.compatibility;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.Util;

import zildo.fwk.opengl.Utils;

/**
 * @author eboussaton
 */
public class FBOSoftware implements FBO {

    private int texRendered;
    private static int cnt = 0;
    private static final Map<Integer, Integer> texFboID = new HashMap<Integer, Integer>();

    public int create() {
        return cnt++;

    }

    public int generateDepthBuffer() {
        return 0;
    }

    public void bindToTextureAndDepth(int myTextureId, int myDepthId, int myFBOId) {
        // Keep a link between myTextureId and myFBOId
        texFboID.put(myFBOId, myTextureId);
    }

    public void startRendering(int myFBOId, int sizeX, int sizeY) {
        texRendered = texFboID.get(myFBOId);
    }

    public void endRendering() {
    	Utils.copyScreenToTexture(texRendered);
        Util.checkGLError();
    }

    public void cleanUp(int id) {
        Utils.cleanTexture(id);
    }

    public void cleanDepthBuffer(int id) {

    }
}