package zildo.fwk.awt;

import org.lwjgl.LWJGLException;

import zildo.fwk.engine.EngineZildo;
import zildo.monde.Area;
import zildo.monde.Case;
import zildo.monde.client.ZildoRenderer;
import zildo.monde.serveur.MapManagement;

public class ZildoCanvas extends AWTOpenGLCanvas {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    private EngineZildo engineZildo;

    public ZildoCanvas(String p_mapname) throws LWJGLException {
        super();
        ZildoRenderer renderer = new ZildoRenderer(p_mapname);
        setRenderer(renderer);
        engineZildo = renderer.getEngineZildo();
    }
	
	public void moveCamera(int p_cameraX, int p_cameraY) {
		
	}
	
    public void changeTile(int p_x, int p_y, Case c) {
        MapManagement map = engineZildo.mapManagement;
        Area area = map.getCurrentMap();
        area.set_mapcase(p_x, p_y, c);
    }
}
