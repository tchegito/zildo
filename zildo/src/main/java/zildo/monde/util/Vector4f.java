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
public class Vector4f {

    public float x;
    public float y;
    public float z;
    public float w;
    
	public Vector4f(float x, float y, float z, float w) {
        set(x, y, z, w);
    }
	
	public Vector4f(Vector4f v) {
		set(v);
	}
	
	public Vector4f(Vector3f v, float w) {
		set(v.x, v.y, v.z, w);
	}
	
    public void set(float x, float y, float z, float w)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    
    public void set(Vector4f v) {
    	set(v.x, v.y, v.z, v.w);
    }
    
    /**
     * Set and multiply each component by another vector component, without fourth one.<br/>
     * Useful for Color filtering, where alpha channel doesn't need to be changed.
     * @param v
     * @param coeff
     */
    public void setAndScale3(Vector4f v, Vector3f coeff) {
    	set(v.x * coeff.x, 
    			v.y * coeff.y, 
    			v.z * coeff.z, 
    			v.w);
    }
    
    public Vector4f scale(float scale)
    {
        x *= scale;
        y *= scale;
        z *= scale;
        w *= scale;
        return this;
    }
    
    public Vector4f interp(Vector4f target, float scaleZeroToOne) {
    	return new Vector4f(x + (target.x -x ) * scaleZeroToOne,
    			y + (target.y - y) * scaleZeroToOne,
    			z + (target.z - z) * scaleZeroToOne,
    			w + (target.w - w) * scaleZeroToOne);
    }
    
    @Override
	public String toString() {
    	return "x:"+x+", y:"+y+", z:"+z+", w:"+w;
    }
}
