/**
 * Legend of Zildo
 * Copyright (C) 2006-2012 Evariste Boussaton
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

package zildo.platform.input;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import zildo.monde.util.Point;

/**
 * @author Tchegito
 *
 */
public class TouchPoints {

	Map<Integer, Point> points;
	
	public TouchPoints() {
		points = new HashMap<Integer, Point>();
	}
	
	public Collection<Point> getAll() {
		return points.values();
	}
	
	public Point getFirst() {
		return points.get(0);
	}
	
	public Point getSecond() {
		return points.get(1);
	}

	private void put(Integer i, Point p) {
		if (p == null) {
			points.remove(i);
		} else {
			points.put(i, p);
		}
	}
	
	public synchronized void set(int i, Point p) {
		if (i >=0 && i <= 1) {
			put(i, p);
		}
	}

	public void clear() {
		points.clear();
	}
	
	public synchronized void putAll(TouchPoints tp) {
		points.putAll(tp.points);
	}
	
	public int size() {
		return points.size();
	}
}
