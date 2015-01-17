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
import zildo.monde.util.Angle;
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
	
	public WorldmapBuilder(String firstMapName) {
		Point starting = new Point(0, 0);
		worldMaps = new HashMap<String, WorldMap>();

		// Process all maps
		processMap(firstMapName, starting, null);
		
		// Adjust map positions
		int minX = 0;
		int minY = 0;
		for (WorldMap wm : worldMaps.values()) {
			minX = Math.min(minX, wm.location.x);
			minY = Math.min(minY, wm.location.y);
		}
		Point shift = new Point(-minX, -minY);
		for (WorldMap wm : worldMaps.values()) {
			wm.location.add(shift);
			System.out.println(wm.theMap.getName() + " at "+wm.location);
		}
		System.out.println("Min="+minX+" , "+minY);
	}
	
	/** Process a map from its name and iterate over each of its border chaining points. **/
	private void processMap(String mapName, Point loc, Angle angle) {
		// If this map is already in the world, leave it
		if (worldMaps.get(mapName) == null) {
			
			// Load asked map
			Area area = EngineZildo.mapManagement.getCurrentMap();
			EngineZildo.mapManagement.loadMap(mapName, true);
			Area nextMap = EngineZildo.mapManagement.getCurrentMap();
			
			// Shift map if we have an angle
			Point mapLoc = new Point(loc);
			if (angle != null) {
				Point shifted = area.getNextMapOffset(nextMap, angle);
				mapLoc.sub(shifted);
			}
			
			worldMaps.put(mapName, new WorldMap(nextMap, mapLoc));
			
			for (ChainingPoint ch : nextMap.getChainingPoints()) {
				if (ch.isBorder() || (ch.getPy()/2 >= (nextMap.getDim_y()-1))) {
					// Recursively add new map
					processMap(ch.getMapname(), mapLoc, ch.getComingAngle().opposite());
				}
			}
		}
	}
}
