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

import java.util.List;

import zildo.monde.util.Angle;
import zildo.monde.util.Point;

/**
 * @author Tchegito
 *
 */
public class TouchMovement {

	List<Point> points;
	Angle current;
	Point save;
	Point prec;
	
	public TouchMovement(List<Point> points) {
		this.points = points;
		this.current = null;
		this.save = null;
		this.prec = null;
	}
	
	public void render() {
		if (points.size() != 0) {
			// There's a point
			if (save == null) {
				save = new Point(points.get(0));	// First one
			} else {
				if (prec == null) {
					prec = save;
				}
				save = new Point(points.get(0));
				if (!prec.equals(save)) {
					// Deduce an angle (if points are sufficiently far away)
					if (prec.distance(save) >= 5) {
						Angle tempAngle = Angle.fromDirection((save.x - prec.x), (save.y - prec.y));
						//System.out.println("touch mouvement : deduce angle = "+current);
						if (tempAngle != current) {
							current = tempAngle;
							prec = new Point(save);
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
