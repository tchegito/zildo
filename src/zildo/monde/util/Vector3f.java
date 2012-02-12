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

/**
 * @author Tchegito
 *
 */
public class Vector3f {

	public float x;
    public float y;
    public float z;
    
	public Vector3f(float x, float y, float z)
    {
        set(x, y, z);
    }
	
	public Vector3f(Vector3f v) {
		set(v.x, v.y, v.z);
	}
	
    public void set(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
