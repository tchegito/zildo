/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
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

package zildo.platform.input;

import java.util.Arrays;

import zildo.monde.util.Point;

/**
 * Simple ArrayList to store current points pressed by player.
 * @author Tchegito
 *
 */
public class TouchPoints {

	static final int MAX_TOUCH_POINTS = 10;
	
	Point[] points;
	int nonNullValue;
	
	public TouchPoints() {
		points = new Point[MAX_TOUCH_POINTS];
	}
	
	public Point[] getAll() {
		return points;
	}
	
	public void set(int i, Point p) {
		if (i > MAX_TOUCH_POINTS) {
			// Can't handle it : too much touch !
		} else {
			points[i] = p;
		}
	}

	public void clear() {
		Arrays.fill(points, null);
	}
	
	// Copy given array in current one
	public void copy(TouchPoints tp) {
		for (int idx = 0; idx < tp.points.length; idx++) {
			points[idx] = tp.points[idx];
		}
	}
	
	public boolean isEmpty() {
		for (Point p : points) {
			if (p != null) return false;
		}
		return true;
	}
}
