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

package zeditor.core.prefetch.complex;

import zildo.monde.map.Area;
import zildo.monde.map.Point;

/**
 * Abstract class which provides a smart drawing from a patch designed with 12 different tiles.<br/>
 * 
 * All derived classes must have at least two overriding methods:<ul>
 * <li>toBinaryValue : convert graphical to 0..15 value</li>
 * <li>toGraphicalValue : convert 0..15 value to displayable one</li>
 * </ul>
 * @author Tchegito
 *
 */
public abstract class AbstractPatch12 extends DelegateDraw {

	byte[] smallPatch = new byte[] { 8, 4, 2, 1};
	byte[] bigPatch = new byte[] {8, 12, 4, 10, 15, 5, 2, 3, 1};

	boolean big;
	
	public AbstractPatch12(boolean p_big) {
		big = p_big;
	}

	/**
	 * Returns a 0..15 value from a tile number.
	 * @param p_val
	 * @return int
	 */
	abstract int toBinaryValue(int p_val);

	/**
	 * Returns a tile number from a 0..15 value.
	 * @param p_val
	 * @return int
	 */
	abstract int toGraphicalValue(int p_val);
		
	@Override
	/**
	 * Draw a patch on the map, and arrange existing drawing.
	 */
	public void draw(Area p_map, Point p_start) {
		int size=big ? 3 : 2;
		
		for (int i=0;i<size;i++) {
			for (int j=0;j<size;j++) {
				// Get map value
				int val=p_map.readmap(p_start.x+j, p_start.y+i);
				val=toBinaryValue(val);
				// Add patch value
				if (big) {
					val=val | bigPatch[i*size +j];
				} else {
					val=val | smallPatch[i*size +j];
				}
				// And render the arranged one
				val=toGraphicalValue(val); 
				drawOneTile(p_map, p_start.x+j, p_start.y+i, val);
			}
		}
	}
	
	public void drawOneTile(Area p_map, int p_x, int p_y, int p_val) {
		p_map.writemap(p_x, p_y, p_val);
	}

}
