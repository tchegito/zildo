/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
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

package tools;

import zildo.monde.map.Area;
import zildo.monde.map.Case;
import zildo.monde.map.Tile;
import zildo.monde.map.TileCollision;
import zildo.monde.map.TileInfo;
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
		area.set_mapcase(x, y, c);
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
	
	public void loadMap(String name) {
		EngineZildo.mapManagement.loadMap(name, false);
		area = EngineZildo.mapManagement.getCurrentMap();
	}
	
	public void displayAltitude() {
		System.out.println("altitude");
		for (int y=0;y<area.getDim_x();y++) {
			System.out.print("y="+String.format("%02d",y)+" ");
			for (int x=0;x<area.getDim_y();x++) {
				int alt = area.readAltitude(x, y);
				System.out.print(alt);
			}
			System.out.println();
		}		
	}
	
	public void displayCollision() {
		for (int y=0;y<area.getDim_y();y++) {
			System.out.print("y="+String.format("%02d",y)+" ");
			for (int x=0;x<area.getDim_x();x++) {
				int tile = area.readmap(x, y);
				String display = " ";
				if (tile != -1) {
					TileInfo tileInfo = TileCollision.getInstance().getTileInfo(tile);
					switch (tileInfo.template) {
					case WALKABLE:
						display=".";
						break;
						default:
							display = tileInfo.template.name().substring(0,1);
					}
				}
				System.out.print(display);
			}
			System.out.println();
		}		
		
	}
}

