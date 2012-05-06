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

import zildo.monde.util.Angle;
import zildo.monde.util.Point;

/**
 * @author Tchegito
 *
 */
public class TouchMovement {

	TouchPoints points;
	Angle current;
	Point save;
	Point prec;
	
	public TouchMovement(TouchPoints points) {
		this.points = points;
		this.current = null;
		this.save = null;
		this.prec = null;
	}
	
	public void render() {
		if (points.size() != 0) {
			Point temp = points.getFirst();
			if (temp != null) {
				// There's a point
				if (save == null) {
					save = new Point(temp);	// First one
				} else {
					if (prec == null) {
						prec = save;
					}
					save = new Point(temp);
					if (!prec.equals(save)) {
						// Deduce an angle (if points are sufficiently far away)
						float dist = prec.distance(save);
						if (dist >= 5) {
							int dx = Math.round((save.x - prec.x) / dist);
							int dy = Math.round((save.y - prec.y) / dist);
							Angle tempAngle = Angle.fromDirection(dx, dy);
							//System.out.println("touch mouvement : deduce angle = "+current);
							if (tempAngle != current) {
								current = tempAngle;
								prec = new Point(save);
							}
						}
					}
				}
			}
		} else {
			current = null;
			save = null;
			prec = null;
		}
	}
	
	public Angle getCurrent() {
		return current;
	}
}
