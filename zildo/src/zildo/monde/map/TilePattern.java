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

package zildo.monde.map;

import zildo.monde.map.Case.TileLevel;
import zildo.monde.sprites.Rotation;
import zildo.monde.util.Point;

/**
 * @author Tchegito
 *
 */
public class TilePattern {

	public static TilePattern explodedHill = new TilePattern(
			new TilePart(0, 0, 256*2),
			new TilePart(1, 0, 256*2),
			new TilePart(0, 0, 220 + 256*5, TileLevel.FORE),
			new TilePart(1, 0, 221 + 256*5, TileLevel.FORE),
			new TilePart(0, 1, 222 + 256*5, TileLevel.BACK),
			new TilePart(1, 1, 223 + 256*5, TileLevel.BACK));
	
	public static TilePattern explodedCave = new TilePattern(
			new TilePart(0, 0, 187 + 256*3),	// Just for collision
			new TilePart(0, 0, 256*2, TileLevel.BACK2),	// Black wall
			new TilePart(1, 0, 188 + 256*3),
			new TilePart(1, 0, 256*2, TileLevel.BACK2),
			new TilePart(0, 0, 216 + 256*5, TileLevel.FORE),
			new TilePart(1, 0, 217 + 256*5, TileLevel.FORE),
			new TilePart(0, 1, 218 + 256*5, TileLevel.BACK),
			new TilePart(1, 1, 219 + 256*5, TileLevel.BACK));
	
	public static TilePattern explodedHouseWall = new TilePattern(
			new TilePart(0, 0, 256*2),
			new TilePart(1, 0, 256*2),
			new TilePart(0, 0, 225 + 256*5, TileLevel.BACK2),
			new TilePart(1, 0, 226 + 256 *5, TileLevel.BACK2));
	
	TilePart[] parts;
	int width, height;
	
	public TilePattern(TilePart... part) {
		// Determine width/height
		width = 0; height = 0;
		for (TilePart tp : part) {
			width = Math.max(width, tp.offset.x + 1);
			height = Math.max(height, tp.offset.y + 1);
		}
		parts = part;
	}

	public void apply(int x, int y, Area area) {
		apply(x, y, area, Rotation.NOTHING);
	}
	
	public void apply(int x, int y, Area area, Rotation rot) {
		for (TilePart tp : parts) {
			Point r = rot.rotate(tp.offset, width, height);
			area.writemap(x + r.x, y + r.y, tp.value, tp.level, rot);
		}
	}
}
