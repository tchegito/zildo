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

package zildo.monde.sprites;

import zildo.monde.util.Angle;
import zildo.monde.util.Point;

/**
 * @author Tchegito
 *
 */
public enum Rotation {

	NOTHING(0), CLOCKWISE(1), UPSIDEDOWN(2), COUNTERCLOCKWISE(3);
	
	public int value;
	
	private Rotation(int value) {
		this.value = value;
	}
	
	public Rotation succ() {
		return values()[(ordinal()+1) % 4];
	}
	
	public static Rotation fromInt(int v) {
		for (Rotation r : values()) {
			if (r.value == v) {
				return r;
			}
		}
		throw new RuntimeException("Unable to get a valid Rotation value.");
	}
	public static Rotation fromBooleans(boolean a, boolean b) {
		if (a) {
			if (b) {
				return Rotation.UPSIDEDOWN;
			} else {
				return Rotation.CLOCKWISE;
			}
		} else if (b) {
			return Rotation.COUNTERCLOCKWISE;
		} else {
			return Rotation.NOTHING;
		}
	}
	
	public boolean isWidthHeightSwitched() {
		return this == CLOCKWISE || this == COUNTERCLOCKWISE;
	}
	
	public static Rotation fromAngle(Angle a) {
		switch (a) {
		case NORD:
		default:
			return NOTHING;
		case EST:
			return CLOCKWISE;
		case OUEST:
			return COUNTERCLOCKWISE;
		case SUD:
			return UPSIDEDOWN;
		}
	}
	
	public boolean[] getBooleans() {
		boolean[] res = new boolean[2];
		res[0] = this == UPSIDEDOWN || this == CLOCKWISE;
		res[1] = this == COUNTERCLOCKWISE || this == UPSIDEDOWN;
		return res;
	}
	
	public Point rotate(Point start, int width, int height) {
		Point res = new Point(start);
		switch (this) {
			case NOTHING:
			default:
				break;
			case CLOCKWISE:
				res.x = height - start.y - 1;
				res.y = start.x;
				break;
			case UPSIDEDOWN:
				res.x = width - start.x - 1;
				res.y = height - start.y - 1;
				break;
			case COUNTERCLOCKWISE:
				res.x = start.y;
				res.y = width - start.x - 1;
				break;
		}
		return res;
	}
}
