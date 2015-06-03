/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
 * 
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

package zildo.monde.util;

import zildo.monde.collision.Rectangle;

public class Zone {

	public int x1, y1;
	public int x2, y2;

	public Zone() {

	}

	public void incX1(int a) {
		x1 += a;
	}

	public void incX2(int a) {
		x2 += a;
	}

	public void incY1(int a) {
		y1 += a;
	}

	public void incY2(int a) {
		y2 += a;
	}

	public Zone(int x1, int y1, int x2, int y2) {
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
	}

	/**
	 * Return TRUE if given point is into the zone. We assume that x2 and y2 are
	 * the width/height of the zone.
	 * 
	 * @param px
	 * @param py
	 * @return boolean
	 */
	public boolean isInto(int px, int py) {
		return px >= x1 && py >= y1 && px <= (x1 + x2) && py <= (y1 + y2);
	}

	public boolean isCrossing(Zone p_zone) {
		return new Rectangle(this).isCrossing(new Rectangle(p_zone));
	}
	
	public boolean isStrictCrossing(Zone p_zone) {
		return new Rectangle(this).isStrictCrossing(new Rectangle(p_zone));
	}
	
	public Point getCenter() {
		return new Point(x1 + x2 / 2, y1 + y2 / 2);
	}
	
	@Override
	public String toString() {
		return x1 + ", " + y1 + " " + x2 + "x" + y2;
	}
}
