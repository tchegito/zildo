/**
 * Legend of Zildo Copyright (C) 2006-2010 Evariste Boussaton Based on original Zelda : link to the past (C) Nintendo 1992 This program is
 * free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this
 * program. If not, see <http://www.gnu.org/licenses/>.
 */

package zildo.fwk.awt;

import java.awt.Point;

import org.lwjgl.LWJGLException;

import zeditor.core.TileSelection;
import zeditor.windows.managers.MasterFrameManager;
import zildo.client.ZildoRenderer;
import zildo.monde.map.Area;
import zildo.monde.map.Case;
import zildo.server.EngineZildo;
import zildo.server.MapManagement;

/**
 * Interface class between ZEditor and Zildo platform.
 * @author tchegito
 */
public class ZildoCanvas extends AWTOpenGLCanvas {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ZildoCanvas(ZildoScrollablePanel p_panel, String p_mapname) throws LWJGLException {
		super();
		panel = p_panel;
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

	public void applyBrush(Point p) {
		// Get brush
		TileSelection sel = MasterFrameManager.getTileSelection();
		if (sel != null) {
			int dx, dy;
			// Apply selected brush to the map
			Area map = EngineZildo.mapManagement.getCurrentMap();
			for (int h = 0; h < sel.getHeight(); h++) {
				for (int w = 0; w < sel.getWidth(); w++) {
					int item = sel.getItem(h * sel.getWidth() + w);
					if (item != -1) {
						dx = p.x / 16 + w;
						dy = p.y / 16 + h;
						if (map.getDim_x() >= dx && map.getDim_y() > dy) {
							// We know that this is a valid location
							map.writemap(dx, dy, item + 256 * sel.bank);
						}
					}
				}
			}
		}
	}

	public void saveMapFile(String p_mapName) {
		MapManagement map = EngineZildo.mapManagement;
		map.saveMapFile(p_mapName);
	}

	public void loadMap(String p_mapName) {
		MapManagement map = EngineZildo.mapManagement;
		map.charge_map(p_mapName);
		changeMap = true;
	}

	/**
	 * Set cursor size
	 * @param x number of horizontal tiles
	 * @param y number of vertical tiles
	 */
	public void setCursorSize(int x, int y) {
		cursorSize = new Point(x * 16, y * 16);
	}
}
