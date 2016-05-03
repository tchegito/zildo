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

package zeditor.tools.builder;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import zeditor.core.Constantes;
import zeditor.fwk.awt.ZildoCanvas;
import zeditor.tools.ImageUtils;
import zildo.fwk.ZUtils;
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

	public class WorldMap {
		public Area theMap;
		public Point location;
		
		public WorldMap(Area map, Point loc) {
			theMap = map;
			location = loc;
		}
		
		@Override
		public String toString() {
			return theMap.getName()+" at "+location;
		}
	}
	
	final Map<String, WorldMap> worldMaps;
	Point size;	// Size of the full image, containing all connected maps
	ZildoCanvas canvas;	// Not null means we really want to capture images / NULL=unit test (dry run)
	String firstMap;
	
	public WorldmapBuilder(String firstMapName, ZildoCanvas canvas) {
		worldMaps = new HashMap<String, WorldMap>();
		this.canvas = canvas;
		this.firstMap = firstMapName;
		
		// Process all maps
		processMap(firstMapName, null, new Point(0, 0), null);
		
		// Adjust map positions
		int minX = 0;
		int minY = 0;
		int maxX = 0;
		int maxY = 0;
		for (WorldMap wm : worldMaps.values()) {
			minX = Math.min(minX, wm.location.x);
			minY = Math.min(minY, wm.location.y);
			maxX = Math.max(maxX, wm.location.x + wm.theMap.getDim_x()*16);
			maxY = Math.max(maxY, wm.location.y + wm.theMap.getDim_y()*16);
		}
		Point shift = new Point(-minX, -minY);
		size = new Point(maxX, maxY);
		size.add(shift);
		for (WorldMap wm : worldMaps.values()) {
			wm.location.add(shift);
		}
	}
	
	public boolean savePng() {
		// 0) Create big bufferedImage for final image
		System.out.println("Creating image with size=("+size.x+","+size.y+")");
		BufferedImage joinedImg = new BufferedImage(size.x, size.y, BufferedImage.TYPE_INT_ARGB);
		// 1) Consider we already have all images ready for patchwork
		try {
			for (WorldMap wm : worldMaps.values()) {
				// Load image
				String filename = Constantes.PATH_CAPTUREDMAPS+"\\"+wm.theMap.getName()+".png";
				System.out.println("Loading "+filename+"... to "+wm.location.x+","+wm.location.y);
				BufferedImage img1 = ImageIO.read(new File(filename));
				ImageUtils.joinBufferedImage(img1, wm.location.x, wm.location.y, joinedImg);
			}
			
			String finalPNGName = Constantes.PATH_WORLDMAP+"\\joined_from"+firstMap+".png";
			new File(Constantes.PATH_WORLDMAP).mkdirs();
			return ImageIO.write(joinedImg, "png", new File(finalPNGName));
		} catch (IOException e) {
			throw new RuntimeException("Unable to assemble all images into one !", e);
		}
		
	}
	
	/** Process a map from its name and iterate over each of its border chaining points. **/
	private void processMap(String mapName, Area currentMap, Point loc, Angle angle) {
		// If this map is already in the world, leave it
		if (worldMaps.get(mapName) == null) {
			
			if ("promenade".equals(mapName)) {
				System.out.println(mapName);
			}
			// Load asked map
			Area area = currentMap;
			if (area == null) {
				area = EngineZildo.mapManagement.getCurrentMap();
			}
			if (canvas == null) {
				EngineZildo.mapManagement.loadMap(mapName, true);
			} else {
				canvas.loadMap(mapName, null);
				canvas.askCapture();
				while (!canvas.isCaptureDone()) {
					ZUtils.sleep(200);
				}
			}

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
					processMap(ch.getMapname(), nextMap, mapLoc, ch.getComingAngle().opposite());
				}
			}
		}
	}
	
	public Map<String, WorldMap> getWorldMap() {
		return worldMaps;
	}
}
