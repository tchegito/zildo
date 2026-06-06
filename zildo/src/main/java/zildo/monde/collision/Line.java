/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
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

package zildo.monde.collision;

import zildo.monde.util.Point;
import zildo.monde.util.Pointf;

public class Line {

	Pointf p1;
	Pointf p2;
	
	public Line(Point p_a, Pointf p_b) {
		this(new Pointf(p_a), p_b);
	}
	
	public Line(Pointf p_a, Pointf p_b) {
		p1=p_a;
		p2=p_b;
	}
	
	public boolean isVertical() {
		return p1.x == p2.x;
	}
	
	/**
	 * Returns the line's slope. BE CAREFUL to vertical line ! (division by zero) 
	 * @return float
	 */
	public float getSlope() {
		if (isVertical()) {
			return Float.NaN;
		}
		float deltaY=p2.y - p1.y;
		float deltaX=p2.x - p1.x;
		return deltaY / deltaX;
	}
	
	/**
     * Returns the intersecting point between the current line and the given one.
     * @param p_other
     * @return Point (NULL if lines never cross)
     */
    public Pointf intersect(Line p_other) {
        float interX, interY;
        if (isVertical() && p_other.isVertical()) {
            // Two lines are vertical
            if (p1.x != p_other.p1.x) {
                return null; // Never crossed
            } else {
                return p1; // Arbitrary point
            }
        } else if (isVertical()) {
            return p_other.intersect(this);
        }
        float slope = getSlope();
        float add = p1.y - slope * p1.x;
        if (p_other.isVertical()) { // No need to calculate the x coordinate : line is vertical
            interX = p_other.p1.x;
        } else { // Calculate the intersection x coordinate
            float slopeOther = p_other.getSlope();
            float addOther = p_other.p1.y - slopeOther * p_other.p1.x;

            if (slope == slopeOther) { // Lines are parallel
                if (add != addOther) {
                    return null; // Never crossed
                } else {
                    return p1; // Arbitrary point
                }
            }

            interX = (addOther - add) / (slope - slopeOther);
        }
        interY = interX * slope + add;
        return new Pointf(interX, interY);
    }

	public boolean isCrossingCircle(Point p_center, int p_radius) {
		return isCrossingCircle(new Pointf(p_center), p_radius);
	}
	
	// This method consider line as an infinite segment. Maybe this could
	// be a problem one day: keep that in mind.
	public boolean isCrossingCircle(Pointf p_center, int p_radius) {
		// 1) line equation
		float a = getSlope();
		if (a == Float.NaN) {
			// Particular case of vertical line => later
			return false;
		}
		float b = p1.y - a * p1.x;
		// Circle equation coordinates (x-c)² + (y-d)² < r²
		float c = p_center.x;
		float d = p_center.y;
		// 2) resolve equation with circle and line
		double equA = 1 + a*a;
		double equB = 2 * a * (b - d) - 2 * c;
		double equC = c*c + + Math.pow(b - d, 2) - p_radius * p_radius;
		double discri = Math.pow(equB, 2) - 4 * equA * equC;
		if (discri < 0) return false;
		return true;
		// See if it's useful to calculate the 2 solutions
		//double r1 = (-equB - Math.sqrt(discri)) / (2 * equA);
		//double r2 = (-equB + Math.sqrt(discri)) / (2 * equA);
		//return true;
	}
	
}
