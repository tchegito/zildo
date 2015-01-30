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

package zeditor.tools;

import zildo.monde.map.Area;
import zildo.monde.map.Case;
import zildo.monde.map.Case.TileLevel;
import zildo.monde.sprites.Rotation;

/**
 * @author Tchegito
 *
 */
public class AreaWrapper {

	public final Area area;
	public final byte floor;
	
	public AreaWrapper(Area p_area, byte p_floor) {
		area = p_area;
		floor = p_floor;
	}
	
	public int getDim_x() {
		return area.getDim_x();
	}
	
	public int getDim_y() {
		return area.getDim_y();
	}
	
	public void set_mapcase(int x, int y, Case c) {
		area.set_mapcase(x, y, floor, c);
	}
	
	public Case get_mapcase(int x, int y) {
		return area.get_mapcase(x, y, floor);
	}
	
	//TODO: add floor
	public int readmap(int x, int y) {
		return area.readmap(x, y);
	}
	
	//TODO: add floor
	public void writemap(int x, int y, int quoi) {
		if (!area.isOutside(x, y)) {
			area.writemap(x, y, quoi);
		}
	}
	
	public void writemap(int x, int y, int quoi, Rotation rot) {
		area.writemap(x, y, quoi, TileLevel.BACK, rot);
	}
	
	public boolean isOutside(int x, int y) {
		return area.isOutside(x, y);
	}
	
}
