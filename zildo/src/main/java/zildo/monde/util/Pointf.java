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

package zildo.monde.util;

public class Pointf {

	public float x, y;
	
	public Pointf(float a, float b) {
		x = a;
		y = b;
	}
	
	public Pointf(double a, double b) {
		x = (float) a;
		y = (float) b;
	}
	
	public Pointf(Pointf source) {
		x = source.x;
		y = source.y;
	}
	
	public Pointf(Point source) {
		x = source.x;
		y = source.y;
	}
	
	public void add(Vector2f v) {
		add(v.x, v.y);
	}
	
    public void add(Pointf p_point) {
    	add(p_point.x, p_point.y);
    }
    
    public void add(Point p_point) {
    	add(p_point.x, p_point.y);
    }
    
    public void add(float p_xPlus, float p_yPlus) {
        this.x += p_xPlus;
        this.y += p_yPlus;
    }
    
    public void mul(float factor) {
    	x *= factor;
    	y *= factor;
    }
    
    public Pointf multiply(float factor) {
    	return new Pointf(x*factor, y*factor);
    }
    
    @Override
	public String toString() {
    	return "("+x+", "+y+")";
    }
    
    public boolean isEmpty() {
    	return x == 0 && y == 0;
    }
    
    public static float distance(float ax, float ay, float bx, float by) {
    	return new Pointf(ax, ay).distance(bx, by);
    }
    
    /** Returns the length of a 2-dimension vector [ax,ay] **/
    public static double pythagore(float ax, float ay) {
        float c = ax * ax;
        c += ay * ay;
        return Math.sqrt(c);
    }
    
    /**
     * Returns the distance between the current point and a given one.
     * @param p_other
     * @return float
     */
    public float distance(float x2, float y2) {
    	float c = x - x2;
    	float d = y - y2;
    	return (float) pythagore(c, d);
    }
    
    public boolean equals(Pointf p) {
    	return x == p.x && y == p.y;
    }
    
    @Override
    public boolean equals(Object o) {
    	if (o == null || !(o instanceof Pointf)) {
    		return false;
    	}
    	return equals( (Pointf) o);
    }
    
    public boolean isSame(Pointf other) {
    	return Math.abs((x-other.x)) < 0.001 && Math.abs((y-other.y)) < 0.001; 
    }
    
    public Point toPoint() {
    	return new Point(Math.round(x), Math.round(y));
    }

    public Pointf translate(float addX, float addY) {
        return new Pointf(x + addX, y + addY);
    }
    
    public Pointf translate(Vector2f v) {
        Pointf p = new Pointf(x,y);
        p.add(v);
        return p;
    }
    
    public Pointf translate(Point p) {
        return new Pointf(x + p.x, y + p.y);
    }
}
