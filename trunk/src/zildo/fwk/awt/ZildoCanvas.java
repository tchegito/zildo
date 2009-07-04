package zildo.fwk.awt;

import org.lwjgl.LWJGLException;

import zildo.fwk.gfx.Ortho;
import zildo.monde.Case;
import zildo.monde.client.ZildoRenderer;

public class ZildoCanvas extends AWTOpenGLCanvas {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	ZildoRenderer zildoRenderer;
	
	public ZildoCanvas(String p_mapName) throws LWJGLException {
		super();
		zildoRenderer=new ZildoRenderer(p_mapName);
		setRenderer(zildoRenderer);
	}
	
	public void moveCamera(int p_cameraX, int p_cameraY) {
		
	}
	
	public void changeTile(int p_x, int p_y, Case c) {
		
	}
}
