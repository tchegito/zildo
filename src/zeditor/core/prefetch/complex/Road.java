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
import zildo.monde.Hasard;
import zildo.monde.map.Area;
import zildo.monde.map.Point;

/**
 * Render for road (little or big one)
 * 
 * @author Tchegito
 *
 */
public class Road extends DelegateDraw {

	boolean big;
	
	byte[] value_chemin=	// Valeurs en zone des chemins
    {0,0,0,0,0,0,0,0,
     8,12,4,5,1,3,2,10,
     14,13,7,11,14,13,7,11,15};
	
    byte[] conv_value_chemin= 	// Renvoie le motif en fonction de la valeur en zone}
    {-3,4,6,5,2,3,16,10,0,16,7,15,1,13,12,16};
    
	public Road(boolean p_big) {
		big=p_big;
	}
	
	@Override
	public void draw(Area p_map, Point p_start) {
		int size=big ? 3 : 2;
		int startRoad=49;
		
		for (int i=0;i<size;i++) {
			for (int j=0;j<size;j++) {
				int val=p_map.readmap(p_start.x+j, p_start.y+i);
				if (val >= startRoad && val < startRoad+24) {
					val=value_chemin[val - startRoad];
					if (big) {
						val=val | value_chemin[PrefDrop.GrandChemin.data[i*size +j]];
					} else {
						val=val | value_chemin[PrefDrop.PetitChemin.data[i*size +j]];
					}
					val=conv_value_chemin[val] + startRoad + 8;
				    // On a 2 séries de 4 motifs pour ces chemins (8--11) et (12--15)
					if (val > 11 && val < 16 && Hasard.lanceDes(5)) {
						val-=4;
					}
					p_map.writemap(p_start.x+j, p_start.y+i, val);
				}
			}
		}
	}

}
