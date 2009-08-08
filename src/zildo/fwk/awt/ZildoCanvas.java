package zildo.fwk.awt;

import org.lwjgl.LWJGLException;

import zildo.client.ZildoRenderer;
import zildo.monde.map.Area;
import zildo.monde.map.Case;
import zildo.server.EngineZildo;
import zildo.server.MapManagement;

/**
 * Interface class between ZEditor and Zildo platform.
 * 
 * @author tchegito
 *
 */
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
    
    public void saveMapFile(String p_mapName) {
        MapManagement map = engineZildo.mapManagement;
    	map.saveMapFile(p_mapName);
    }
}
