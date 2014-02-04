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

import java.util.HashMap;
import java.util.Map;

import zildo.monde.map.Area;
import zildo.monde.map.Case;
import zildo.monde.map.Tile;
import zildo.monde.util.Angle;
import zildo.server.EngineZildo;
import zildo.server.MapManagement;

/**
 * @author Tchegito
 *
 */
public class AdjustChestBackTiles extends AllMapProcessor {
	int chest = 231 + 256 * 2;
	
	
	int[] temp = new int[256 * 10];
	
	@Override
	public boolean run() {
		MapManagement mapManagement = EngineZildo.getMapManagement();
		Area area = mapManagement.getCurrentMap();
		int emptyTile = area.getAtmosphere().getEmptyTile();
		boolean isReplacements = false;
		
		// house
		emptyTile = 256*2 + 33;
		for (int y = 0 ; y < area.getDim_y() ; y++) {
			for (int x = 0 ; x < area.getDim_x() ; x++) {
				Case mapCase = area.get_mapcase(x, y+4);
				if (mapCase.getBackTile2() == null) {
					if (chest == mapCase.getBackTile().getValue() ) {
						System.out.println("(house)Replace tile at "+x+","+y);
						
						// guess empty tile
						for (int j = 0;j<temp.length;j++) {
							temp[j] = 0;
						}
						for (int i=0;i<8;i++) {
							Angle a = Angle.fromInt(i);
							int caseVal = area.readmap(x+a.coords.x, y+a.coords.y);
							if (caseVal == 165) {
								caseVal = 54;	// Special case => bushes (replaced by herb)
							}
							temp[caseVal]++;
						}
						int max = -1;
						int index = 0;
						for (int j = 0;j<temp.length;j++) {
							if (max < temp[j]) {
								max = temp[j];
								index = j;
							}
						}
						System.out.println("Guessed empty tile => "+index);
						
						emptyTile = index;
						
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
