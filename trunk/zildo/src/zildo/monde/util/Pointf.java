/**
 * Legend of Zildo
 * Copyright (C) 2006-2012 Evariste Boussaton
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
		x=a;
		y=b;
	}
	
    public void add(Pointf p_point) {
    	add(p_point.x, p_point.y);
    }
    
    public void add(float p_xPlus, float p_yPlus) {
        this.x += p_xPlus;
        this.y += p_yPlus;
    }
    
    @Override
	public String toString() {
    	return "("+x+", "+y+")";
    }
}
