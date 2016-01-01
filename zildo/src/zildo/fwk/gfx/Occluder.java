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
 * Object that takes a 2-dimensional space, cuts it on demand and return free space.
 * Useful for storing sprites on a fixed size texture.
 * 
 * @author Tchegito
 *
 */
public class Occluder {

	protected List<Zone> available;
	
	int width, height;
	
	public Occluder(int width, int height) {
		this.width = width;
		this.height = height;
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
	Point allocateZone(int width, int height) {
		int minAire = 256 *256;
		Zone bestZone=null;
		for (Zone z : available) {
			if (z.x2 >= width && z.y2 >= height) {	// Enough room
				int aire = z.x2*z.y2;
				if (aire < minAire) {
					minAire = aire;
					bestZone = z;
				}					
			}
		}
		if (bestZone != null) {
			remove(new Zone(bestZone.x1, bestZone.y1, width, height));
			return new Point(bestZone.x1, bestZone.y1);
		}
		return null;
	}
	
	public Point allocate(int width, int height) {
		Point p = allocateZone(width, height);
		if (p == null) {
			// Fails, so we try to recut and reallocate
			recut();
			p = allocateZone(width, height);
			if (p == null) {
				Zone z = recut(width, height);
				remove(z);
				return new Point(z.x1, z.y1);
			}
		}
		return p;
	}
	
	private void recut() {
		available = new OccluderArranger(this).recut();
	}
	
	private Zone recut(int width, int height) {
		OccluderArranger arranger = new OccluderArranger(this);
		Zone z = arranger.cutSpecificArea(width, height);
		if (z == null) throw new RuntimeException("Unable to allocate zone "+width+"x"+height);
		available = arranger.recut(z);
		return z;
	}
}
