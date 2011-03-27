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

import zeditor.core.prefetch.PrefDrop;
import zildo.monde.map.Area;
import zildo.monde.map.Point;

/**
 * @author Tchegito
 *
 */
public class ForestBorder extends DelegateDraw {

	byte[] value_border =new byte[] {
		15, 0, 0, 7, 3, 11, 1, 2,
		5, 10, 15, 4, 8, 15, 13, 12, 14		
	};
	
	byte[] conv_value_border = new byte[] {
		73,
		79, 80, 77, 84, 81, -1, 76, 85,
		-1, 82, 78, 88, 87, 89, 73, -1
	};
	
	boolean big;
	
	public ForestBorder(boolean p_big) {
		big = p_big;
	}
	@Override
	public void draw(Area p_map, Point p_start) {
		int size=big ? 3 : 2;
		int startRoad=256 * 6 + 73;
		
		for (int i=0;i<size;i++) {
			for (int j=0;j<size;j++) {
				int val=p_map.readmap(p_start.x+j, p_start.y+i);
				if (val >= startRoad && val < startRoad+16) {
					val=value_border[val - startRoad];
				} else {
					val = 0;
				}
				if (big) {
					val=val | value_border[PrefDrop.GrandeLisiere.data[i*size +j] - startRoad % 256];
				} else {
					val=val | value_border[PrefDrop.PetiteLisiere.data[i*size +j] - startRoad % 256];
				}
				val=conv_value_border[val];
				if (val == -1) {
					val = 54;
				} else {
					val = 256*6 + val;
				}
				p_map.writemap(p_start.x+j, p_start.y+i, val);
			}
		}
	}

}
