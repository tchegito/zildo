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

import zildo.monde.map.Area;
import zildo.monde.map.Case;
import zildo.server.EngineZildo;

/**
 * @author Tchegito
 *
 */
public class ReplaceAllMapsFloor extends AllMapProcessor {

	@Override
	public boolean run() {
		boolean isReplacement = false;
		Area area = EngineZildo.getMapManagement().getCurrentMap();
		for (int y = 0; y < area.getDim_y(); y++) {
			for (int x = 0; x < area.getDim_x(); x++) {
				Case mapCase = area.get_mapcase(x, y + 4);
				int ind = mapCase.getBackTile().getValue() - 256 * 2;
				if (ind == 33 || ind == 35 || ind == 47) {
					if ((y % 2) == 1) {
						area.writemap(x, y, 256 * 2 + 54);
					} else {
						area.writemap(x, y, 256 * 2 + 33);
					}
					isReplacement = true;
				} else if (ind == 34 || ind == 47) {
					if ((y % 2) == 1) {
						area.writemap(x, y, 256 * 2 + 55);
						isReplacement = true;
					}
				}
			}
		}
		return isReplacement;
	}

}
