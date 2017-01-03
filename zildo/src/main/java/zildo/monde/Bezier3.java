/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
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

package zildo.monde;

import zildo.monde.util.Pointf;

/**
 * @author Tchegito
 *
 */
public class Bezier3 {

	Pointf[] arr;
	
	public Bezier3(Pointf a, Pointf b, Pointf c) {
		arr = new Pointf[3];
		arr[0] = a;
		arr[1] = b;
		arr[2] = c;
	}
	
	private float pointCurve(float n1, float n2, float n3, float t) {
		return (1-t)*(1-t)*n1 + 2*t*(1-t)*n2 + t*t*n3;
	}
	
	/** Interpolate between control points.
	 * @param t
	 * @return
	 */
	public Pointf interpol(float t) {
		return new Pointf(pointCurve(arr[0].x, arr[1].x, arr[2].x, t),
						  pointCurve(arr[0].y, arr[1].y, arr[2].y, t));
	}
}
