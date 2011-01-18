/**
 * Legend of Zildo
 * Copyright (C) 2006-2011 Evariste Boussaton
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

package zildo.monde.map;

public class Zone {

	public int x1,y1;
	public int x2,y2;
	
	public int getX1() {
		return x1;
	}

	public void setX1(int x1) {
		this.x1 = x1;
	}

	public int getY1() {
		return y1;
	}

	public void setY1(int y1) {
		this.y1 = y1;
	}

	public int getX2() {
		return x2;
	}

	public void setX2(int x2) {
		this.x2 = x2;
	}

	public int getY2() {
		return y2;
	}

	public void setY2(int y2) {
		this.y2 = y2;
	}

	public Zone() {
		
	}
	
	public void incX1(int a) {
		x1+=a;
	}
	public void incX2(int a) {
		x2+=a;
	}
	public void incY1(int a) {
		y1+=a;
	}
	public void incY2(int a) {
		y2+=a;
	}
	public Zone(int x1, int y1, int x2, int y2) {
		this.x1=x1;
		this.x2=x2;
		this.y1=y1;
		this.y2=y2;
	}
	
	/**
	 * Return TRUE if given point is into the zone. We assume that x2 and y2 are the width/height of the zone.
	 * @param px
	 * @param py
	 * @return boolean
	 */
	public boolean isInto(int px, int py) {
	    return px >= x1 && py >= y1 && px <= (x1+x2) && py <= (y1+y2);
	}
	
	public String toString() {
		return x1+", "+y1+" "+x2+"x"+y2;
	}
}
