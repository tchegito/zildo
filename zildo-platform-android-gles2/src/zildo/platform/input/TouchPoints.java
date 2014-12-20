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

package zildo.platform.input;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.util.Log;
import zildo.monde.util.Point;

/**
 * Simple ArrayList to store current points pressed by player.
 * @author Tchegito
 *
 */
public class TouchPoints {

	List<Point> points;
	
	public TouchPoints() {
		points = new ArrayList<Point>();
	}
	
	public Collection<Point> getAll() {
		return points;
	}
	
	public void set(int i, Point p) {
		if (p == null) {
			points.remove(i);
		} else if (i < points.size()){
			points.set(i, p);
		} else {
			Log.d("TOUCH", "Unable to set "+i+"-nth point "+p+" into the list");
		}
	}

	public void add(int i, Point p) {
		points.add(i, p);
	}

	public void clear() {
		points.clear();
	}
	
	public void addAll(TouchPoints tp) {
		points.addAll(tp.points);
	}
	
	public int size() {
		return points.size();
	}
}
