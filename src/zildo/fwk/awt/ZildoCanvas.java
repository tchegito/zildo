package zildo.fwk.awt;

import org.lwjgl.LWJGLException;

import zildo.monde.Case;
import zildo.monde.client.ZildoRenderer;

public class ZildoCanvas extends AWTOpenGLCanvas {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ZildoCanvas() throws LWJGLException {
		super(new ZildoRenderer());
	}
	
	public void moveCamera(int p_cameraX, int p_cameraY) {
		
	}
	
	public void changeTile(int p_x, int p_y, Case c) {
		
	}
}
