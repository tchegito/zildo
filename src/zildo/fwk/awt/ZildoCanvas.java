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

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

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
 *
 * @author tchegito
 *
 */
public class ZildoCanvas extends AWTOpenGLCanvas {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public ZildoCanvas(ZildoScrollablePanel p_panel, String p_mapname)
			throws LWJGLException {
		super();
		panel = p_panel;
		ZildoRenderer renderer = new ZildoRenderer(p_mapname);
		setRenderer(renderer);
	}

	public void applyBrush(Point p) {
		// Get brush
		TileSelection sel = MasterFrameManager.getTileSelection();
		if (sel != null) {
			drawBrush(p, sel);
		}
	}

	private void drawBrush(Point p, TileSelection p_sel) {
		int dx, dy;
		// Apply selected brush to the map
		Area map = EngineZildo.mapManagement.getCurrentMap();
		for (int h = 0; h < p_sel.getHeight(); h++) {
			for (int w = 0; w < p_sel.getWidth(); w++) {
				Case item = p_sel.getItem(h * p_sel.getWidth() + w);
				if (item != null) {
					dx = p.x / 16 + w;
					dy = p.y / 16 + h;
					if (map.getDim_x() >= dx && map.getDim_y() > dy) {
						// We know that this is a valid location
						map.set_mapcase(dx, dy + 4, item);
					}
				}
			}
		}
	}

	/**
	 * Clear a region of the map sized by the selected brush
	 * @param p
	 */
	public void clearWithBrush(Point p) {
		TileSelection sel = MasterFrameManager.getTileSelection();
		Point size;
		if (sel != null) {
			size = new Point(sel.getWidth(), sel.getHeight());
		} else {
			size = new Point(1, 1);
		}
		List<Case> cases=new ArrayList<Case>();
		Case empty=new Case();
		empty.setN_banque(0);
		empty.setN_motif(54);	// Empty in outside
		empty.setN_banque_masque(0);
		empty.setN_motif_masque(0);
		for (int i=0;i<size.x*size.y;i++) {
		    cases.add(empty);
		}
		TileSelection emptySel = new TileSelection(size.x, size.y, cases);
		drawBrush(p, emptySel);
	}

	public void saveMapFile(String p_mapName) {
		MapManagement map = EngineZildo.mapManagement;
		String fileName = p_mapName;
		if (p_mapName == null) {
			fileName = map.getCurrentMap().getName();
		}
		map.saveMapFile(fileName + ".MAP");
	}

	public void loadMap(String p_mapName) {
		MapManagement map = EngineZildo.mapManagement;
		map.charge_map(p_mapName);
		changeMap = true;
	}

	/**
	 * Set cursor size
	 *
	 * @param x
	 *            number of horizontal tiles
	 * @param y
	 *            number of vertical tiles
	 */
	public void setCursorSize(int x, int y) {
		cursorSize = new Point(x * 16, y * 16);
	}
}
