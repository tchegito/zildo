/**
 * Legend of Zildo
 * Copyright (C) 2006-2010 Evariste Boussaton
 * Based on original Zelda : link to the past (C) Nintendo 1992
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

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
	
    public ZildoCanvas(String p_mapname) throws LWJGLException {
        super();
        ZildoRenderer renderer = new ZildoRenderer(p_mapname);
        setRenderer(renderer);
    }
	
	public void moveCamera(int p_cameraX, int p_cameraY) {
		
	}
	
    public void changeTile(int p_x, int p_y, Case c) {
        MapManagement map = EngineZildo.mapManagement;
        Area area = map.getCurrentMap();
        area.set_mapcase(p_x, p_y, c);
    }
    
    public void saveMapFile(String p_mapName) {
        MapManagement map = EngineZildo.mapManagement;
    	map.saveMapFile(p_mapName);
    }
    
    public void loadMap(String p_mapName) {
        MapManagement map = EngineZildo.mapManagement;
    	map.charge_map(p_mapName);
    	changeMap=true;
    }
}
