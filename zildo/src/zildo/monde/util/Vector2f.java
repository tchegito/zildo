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

/**
 * @author Tchegito
 *
 */
public class Vector2f {

	public float x;
	public float y;
	
    public Vector2f(float p_x, float p_y)
    {
        set(p_x, p_y);
    }
    
    public Vector2f(double p_x, double p_y)
    {
        set((float) p_x, (float) p_y);
    }
    
    public Vector2f(Pointf p) {
    	set(p.x, p.y);
    }
    
    public Vector2f(Pointf a, Pointf b) {
    	set(b.x - a.x, b.y - a.y);
    }
    
    public void set(float p_x, float p_y) {
        x = p_x;
        y = p_y;
    }
    
    public void set(double p_x, double p_y) {
        x = (float) p_x;
        y = (float) p_y;
    }
    public Vector2f add(float p_x, float p_y) {
    	x += x;
    	y += y;
    	return this;
    }
    
    public Vector2f add(Vector2f v) {
    	x += v.x;
    	y += v.y;
    	return this;
    }
    
    public Vector2f add(Point p) {
    	x += p.x;
    	y += p.y;
    	return this;
    }
    
    /** Do not modify current vector **/
    public Vector2f mul(float factor) {
    	Vector2f ret = new Vector2f(x, y);
    	ret.x *= factor;
    	ret.y *= factor;
    	return ret;
    }
    
    /**
     * Normalize the vector, according to a max value.
     * @param max
     */
    public void normalize(float max) {
    	// Calculate norm
    	float norme = norm();
    	float normalized = Math.min(norme, max);
    	if (normalized < norme) {
    		float ratio = normalized / norme;
    		x *= ratio;
    		y *= ratio;
    	}
    }
    
    public float norm() {
    	return Point.distance(0, 0, x, y);
    }
    
    public Vector2f abs() {
    	x = Math.abs(x);
    	y = Math.abs(y);
    	return this;
    }
    
    /** Rotation to get on X axis **/
	public Vector2f rotX() {
		return new Vector2f(Math.signum(x) * norm(), 0);
	}

    /** Rotation to get on Y axis **/
	public Vector2f rotY() {
		return new Vector2f(0, Math.signum(y) * norm());
	}

    @Override
    public boolean equals(Object o) {
    	if (o == null || !(o instanceof Vector2f)) {
    		return false;
    	}
    	return equals( (Vector2f) o);
    }
    
	public boolean equals(Vector2f v) {
		return x == v.x && y == v.y;
	}
	
	public float distance(Vector2f v) {
		return Pointf.distance(x, y, v.x, v.y);
	}
	
    @Override
	public String toString() {
    	return "x:"+x+", y:"+y;
    }
}
