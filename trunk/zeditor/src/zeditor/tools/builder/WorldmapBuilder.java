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

package zeditor.tools.builder;

import java.util.HashMap;
import java.util.Map;

import zildo.monde.map.Area;
import zildo.monde.map.ChainingPoint;
import zildo.monde.util.Point;
import zildo.server.EngineZildo;

/**
 * @author Tchegito
 *
 */
public class WorldmapBuilder {

	class WorldMap {
		Area theMap;
		Point location;
		
		public WorldMap(Area map, Point loc) {
			theMap = map;
			location = loc;
		}
	}
	
	Map<String, WorldMap> worldMaps;
	int minX = 0;
	int minY = 0;
	
	public WorldmapBuilder(String mapName) {
		Point starting = new Point(0, 0);
		worldMaps = new HashMap<String, WorldMap>();

		processMap(mapName, starting);
	}
	
	private void processMap(String mapName, Point loc) {
		// If this map is already in the world, leave it
		if (worldMaps.get(mapName) == null) {
			
			// Load asked map
			Area area = EngineZildo.mapManagement.getCurrentMap();
			EngineZildo.mapManagement.loadMap(mapName, true);
			Area nextMap = EngineZildo.mapManagement.getCurrentMap();
			
			worldMaps.put(mapName, new WorldMap(nextMap, loc));
			
			for (ChainingPoint ch : nextMap.getChainingPoints()) {
				if (ch.isBorder()) {
					Point shifted = area.getNextMapOffset(nextMap, ch.getComingAngle().opposite());
					Point mapLoc = new Point(loc);
					mapLoc.add(shifted);
					// Recursively add new map
					processMap(ch.getMapname(), shifted);
				}
			}
		}
	}
}
