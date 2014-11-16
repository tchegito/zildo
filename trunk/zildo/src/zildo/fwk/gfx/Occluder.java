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

package zildo.fwk.gfx;

import java.util.ArrayList;
import java.util.List;

import zildo.monde.util.Point;
import zildo.monde.util.Zone;

/**
 * @author Tchegito
 *
 */
public class Occluder {

	List<Zone> available;
	
	public Occluder(int width, int height) {
		available = new ArrayList<Zone>();
		available.add(new Zone(0, 0, width, height));
	}
	
	public void remove(Zone cutted) {
		List<Zone> toRemove = new ArrayList<Zone>();
		List<Zone> toAdd = new ArrayList<Zone>();
		for (Zone z : available) {
			if (cutted.isStrictCrossing(z)) {
				// We need to cut
				toRemove.add(z);
				
				// Horizontal bars
				if (cutted.x1 > z.x1) {
					toAdd.add(new Zone(z.x1, cutted.y1, cutted.x1-z.x1, cutted.y2));
				}
				if (cutted.x1 + cutted.x2 < z.x1 + z.x2) {
					toAdd.add(new Zone(cutted.x1+cutted.x2, cutted.y1, z.x1+z.x2 - (cutted.x1+cutted.x2), cutted.y2));
				}
				// Vertical bars
				if (cutted.y1 > z.y1) {
					toAdd.add(new Zone(z.x1, z.y1, z.x2, cutted.y1 - z.y1));
				}
				if (cutted.y1+cutted.y2 < z.y1 + z.y2) {
					toAdd.add(new Zone(z.x1, cutted.y1+cutted.y2, z.x2, z.y1+z.y2 - (cutted.y1+cutted.y2)));
				}
			}
		}
		for (Zone z : toRemove) {
			available.remove(z);
		}
		available.addAll(toAdd);
	}
	
	/** Calculates available aire **/
	public int calculateAire() {
		int aire = 0;
		for (Zone z : available) {
			aire += (z.x2 * z.y2);
		}
		return aire;
	}

	/** Allocates a location with given width x height values. **/
	public Point allocate(int width, int height) {
		for (Zone z : available) {
			if (z.x2 >= width && z.y2 >= height) {
				remove(new Zone(z.x1, z.y1, width, height));
				return new Point(z.x1, z.y1);
			}
		}
		return null;
	}
}
