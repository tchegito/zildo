package zildo.fwk.opengl.compatibility;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;
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
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texRendered);


        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, 0, 0, 1024, 512, 0); // Zildo.viewPortX, Zildo.viewPortY, 0);
        // GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, 1024, 512); // Zildo.viewPortX, Zildo.viewPortY, 0);
        Util.checkGLError();

    }

    public void cleanUp(int id) {
        Utils.cleanTexture(id);
    }

    public void cleanDepthBuffer(int id) {

    }
}