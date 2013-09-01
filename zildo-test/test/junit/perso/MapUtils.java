/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
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

package junit.perso;

import zildo.monde.map.Area;
import zildo.monde.map.Case;
import zildo.monde.map.Tile;
import zildo.server.EngineZildo;

public class MapUtils {

	public Area area;
	
	/**
	 * Create a MapUtils instance, getting the current map.
	 */
	public MapUtils() {
		area = EngineZildo.mapManagement.getCurrentMap();
	}
	
	public void writemap(int x, int y, int back, int back2, int fore) {
		Case c = new Case();
		c.setBackTile(new Tile(back, c));
		if (back2 != -1) {
			c.setBackTile2(new Tile(back2, c));
		}
		if (fore != -1) {
			c.setForeTile(new Tile(fore, c));
		}
		area.set_mapcase(x, y+4, c);
	}
	
	public void createClosedDoor(int x, int y) {
		writemap(x,   y,   256 + 22, -1, -1);
		writemap(x+1, y,   256 + 23, -1, -1);
		writemap(x,   y+1, 49, 256 + 36, -1);
		writemap(x+1, y+1, 49, 256 + 37, -1);		
	}
	
	public void createOpenedDoor(int x, int y) {
		writemap(x,   y,   256 + 58, -1, -1);
		writemap(x+1, y,   256 + 59, -1, -1);
		writemap(x,   y+1, 49, 256 + 36, -1);
		writemap(x+1, y+1, 49, 256 + 37, -1);		
	}
	
}

