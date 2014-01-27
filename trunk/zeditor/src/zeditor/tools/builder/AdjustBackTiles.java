/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
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

package zeditor.tools.builder;

import zildo.fwk.collection.IntSet;
import zildo.monde.map.Area;
import zildo.monde.map.Case;
import zildo.monde.map.Tile;
import zildo.server.EngineZildo;
import zildo.server.MapManagement;

/**
 * @author Tchegito
 *
 */
public class AdjustBackTiles extends AllMapProcessor {

	
	IntSet maskedTiles = new IntSet(
			// Foret
			159, 160, 161, 162,
			179, 180, 181, 182, 184, 185, 167, 169,
			196, 197, 198, 199,
			// Village
			256+0, 256+4, 256+191, 256+195, 256+213, 256+217,
			256+17, 256+208, 256+230,
			256+24, 256+28, 256+30, 256+33,
			256+25, 256+34, 256+35, 256+36, 256+37,
			256+67, 256+68, 256+70, 256+71, 256+72, 256+73, 256+74, 
			256+78, 256+79, 256+80, 256+81, 256+82, 256+83, 256+84, 256+85,
			256+90, 256+91, 256+92, 256+99, 256+100,
			256+105, 256+106, 256+107, 256+108, 256+109,
			256+114, 256+115, 256+116, 256+117,	// Swamp
			256+125, 256+129, 256+130,
			256+131, 256+132, 256+133, 256+134,
			256+135, 256+136, 256+137, 256+138,
			256+148, 256+149, 256+151, 256+152,
			// Foret2
			256*4+2, 256*4+3, 256*4+4, 256*4+5, 256*4+6, 256*4+7,
			256*4+12, 256*4+13,
			256*4+14, 256*4+15, 256*4+16, 256*4+17
			);
	
	IntSet maskedHouseTiles = new IntSet(
			// Maison
			110, 112, 113, 114,	// table
			120, 121,	// stool
			155, 157, 158, 159, 191, 192, 193, // Abreuvoir
			209, 210, 211,	// bar
			164, 165,	// bed
			151, 152, 153,	// cooker
			134, 135	// cheminey
			);
	
	@Override
	public boolean run() {
		MapManagement mapManagement = EngineZildo.mapManagement;
		Area area = mapManagement.getCurrentMap();
		int emptyTile = area.getAtmosphere().getEmptyTile();
		boolean isReplacements = false;
		for (int y = 0 ; y < area.getDim_y() ; y++) {
			for (int x = 0 ; x < area.getDim_x() ; x++) {
				Case mapCase = area.get_mapcase(x, y);
				if (mapCase.getBackTile2() == null) {
					if (maskedTiles.contains(mapCase.getBackTile().getValue())) {
						System.out.println("Replace tile at "+x+","+y);
						mapCase.setBackTile2(mapCase.getBackTile());
						mapCase.setBackTile(new Tile(emptyTile, mapCase));
						isReplacements = true;
					}
				}
			}
		}
		
		// house
		emptyTile = 256*2 + 33;
		for (int y = 0 ; y < area.getDim_y() ; y++) {
			for (int x = 0 ; x < area.getDim_x() ; x++) {
				Case mapCase = area.get_mapcase(x, y);
				if (mapCase.getBackTile2() == null) {
					if (maskedHouseTiles.contains(mapCase.getBackTile().getValue() - 256*2)) {
						System.out.println("(house)Replace tile at "+x+","+y);
						emptyTile = 256*2 + 33;
						if ((y%2)==1) {
							emptyTile = 256*2 + 54;
						}
						mapCase.setBackTile2(mapCase.getBackTile());
						mapCase.setBackTile(new Tile(emptyTile, mapCase));
						isReplacements = true;
					}
				}
			}
		}
		return isReplacements;
	}

}
