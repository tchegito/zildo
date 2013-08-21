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

package zildo.monde.util;

public class Point {

    public int x, y;
    
    public Point() {

    }
    
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point(float x, float y) {
    	this.x = (int)x;
    	this.y = (int)y;
    }
    
    // Copy constructor
    public Point(Point p) {
        this.x = p.x;
        this.y = p.y;
    }
    
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void add(Point p_point) {
    	add(p_point.x, p_point.y);
    }
    
    public void add(int p_xPlus, int p_yPlus) {
        this.x += p_xPlus;
        this.y += p_yPlus;
    }

    public Point translate(int addX, int addY) {
        return new Point(x + addX, y + addY);
    }
    
    public Point translate(Point p_pointAdd) {
        return translate(p_pointAdd.x, p_pointAdd.y);
    }

    public Point multiply(float factor) {
    	return new Point(x*factor, y*factor);
    }
    
    @Override
	public String toString() {
    	return "("+x+", "+y+")";
    }
    
    /**
     * Returns the distance between the current point and a given one.
     * @param p_other
     * @return float
     */
    public float distance(Point p_other) {
        int c = Math.abs(x - p_other.x);
        int d = Math.abs(y - p_other.y);
        c = c * c;
        c += d * d;
        return (float) Math.sqrt(c);
    }
    
    public static float distance(float ax, float ay, float bx, float by) {
    	return new Point(ax, ay).distance(new Point(bx, by));
    }
    
    /**
     * Returns TRUE if given point have same coordinates as current one.
     */
    @Override
	public boolean equals(Object p_other) {
		if (!p_other.getClass().equals(Point.class)) {
    		return false;
    	}
    	Point p=(Point) p_other;
    	return p.x == x && p.y == y;
    }
    
    @Override
    public int hashCode() {
    	int hash = 17;
    	hash = hash*31 + x;
    	hash = hash*31 + y;
    	return hash;
    }
    public static Point fromString(String p_text) {
    	String[] coords=p_text.split(",");
    	return new Point(Integer.valueOf(coords[0]), Integer.valueOf(coords[1]));
    }
}
