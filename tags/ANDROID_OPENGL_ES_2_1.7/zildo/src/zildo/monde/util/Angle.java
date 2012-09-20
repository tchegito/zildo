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

package zildo.monde.util;



public enum Angle {

	NORD(0, new Point(0,-1)),
	EST(1, new Point(1,0)),
	SUD(2, new Point(0,1)),
	OUEST(3, new Point(-1,0)),
	NORDEST(4, new Point(1,-1)),
	SUDEST(5, new Point(1,1)),
	SUDOUEST(6, new Point(-1,1)),
	NORDOUEST(7, new Point(-1,-1));
	
	public int value;
	public Point coords;

	
	static final Point[] saut_angle={
		new Point(0,-40), new Point(48,16),new Point(0,56),  new Point(-48,16),
		new Point(32,48), new Point(32,-32),new Point(-32,48),new Point(-32,-32)};
	
	private Angle(int value, Point coords) {
		this.value=value;
		this.coords=coords;
	}
	
	public boolean isVertical() {
		return this==NORD || this==SUD;
	}
	
	public boolean isHorizontal() {
		return this==EST || this==OUEST;
	}

    public boolean isDiagonal() {
        return value > 3;
    }
    
	public static Angle rotate(Angle a, int quart) {
		int val=(a.value + quart) % 4;
		if (!a.isDiagonal()) {
			return fromInt(val);
		} else {	// Diagonal
			return fromInt(val+4);
		}
	}
	
	public Angle opposite() {
		return Angle.rotate(this, 2);
	}
	
	static public Angle fromInt(int val) {
		for (Angle a : Angle.values()) {
			if (a.value == val) {
				return a;
			}
		}
		throw new RuntimeException(val+" n'est pas un angle reconnu.");
	}
	
	/**
	 * Returns a 0..8 ranged int based on this order (NORD, NORDEST, EST, SUDEST, SUD, SUDOUEST, OUEST, NORDOUEST)
	 * @return int
	 */
	private int getUsableValue() {
		if (isDiagonal()) {
			return (value - 4) *2 + 1;
		} else {
			return value*2;
		}
	}
	
	/**
	 * Returns TRUE if the given angle is this one's opposite, with/without tolerance of 1 scale.
	 * @param p_other
	 * @param p_tolerance
	 * @return boolean
	 */
	public boolean isOpposite(Angle p_other, boolean p_tolerance) {
		if (p_other == null) {
			return false;
		}
		int val1=getUsableValue();
		int val2=p_other.getUsableValue();
		int result=Math.abs(val2 - val1-4);
		if (p_tolerance) {
			return Math.abs(result - 1) == 0;
		} else {
			return result==0;
		}
	}
	
	/**
	 * Returns delta location with a jump in given angle.
	 * @return Point
	 */
	public Point getLandingPoint() {
		return saut_angle[value];
	}
	
	/**
	 * Get the angle from the given direction
	 * @param dx
	 * @param dy
	 * @return Angle
	 */
	public static Angle fromDirection(int dx, int dy) {
		int a=Integer.signum(dx);
		int b=Integer.signum(dy);
		for (Angle angle : Angle.values()) {
			if (angle.coords.x == a && angle.coords.y == b) {
				return angle;
			}
		}
		return null;
	}
	
	public static boolean isContained(Angle ref, Angle component) {
		if (ref == null) {
			return false;
		}
		return ((component.coords.x == ref.coords.x && ref.coords.x != 0) ||
				(component.coords.y == ref.coords.y && ref.coords.y != 0));
	}
}
