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
    
    public void set(float p_x, float p_y) {
        x = p_x;
        y = p_y;
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
    
    public Vector2f mul(float factor) {
    	x *= factor;
    	y *= factor;
    	return this;
    }
    
    /**
     * Normalize the vector, according to a max value.
     * @param max
     */
    public void normalize(float max) {
    	// Calculate norm
    	float norme = Point.distance(0, 0, x, y);
    	float normalized = Math.min(norme, max);
    	if (normalized < norme) {
    		float ratio = normalized / norme;
    		x *= ratio;
    		y *= ratio;
    	}
    }
    
    @Override
	public String toString() {
    	return "x:"+x+", y:"+y;
    }
}
