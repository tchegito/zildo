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
import zildo.server.EngineZildo;
import zildo.server.MapManagement;

/**
 * Adjust cave tiles on maps, after replacing graphics.
 * Previous tile didn't correspond to a good display, with new ones.
 * 
 * NOTE:can cause damage (1 tile seen on 'foretg')
 * 
 * @author Tchegito
 *
 */
public class AdjustGrotte extends AllMapProcessor {

	@Override
	public boolean run() {
		MapManagement mapManagement = EngineZildo.getMapManagement();
		Area area = mapManagement.getCurrentMap();
		boolean isReplacements = false;
		for (int y = 0 ; y < area.getDim_y() ; y++) {
			for (int x = 0 ; x < area.getDim_x() ; x++) {
				int idx = area.readmap(x, y) -256*3;
				int idx2,idx3;
				if (idx >= 0 && idx < 256) { // Cave tiles
					if (idx == 15 || idx == 14 || idx == 16) {
						idx2 = area.readmap(x-1,y+1) - 256*3;
						idx3 = area.readmap(x+1,y+1) - 256*3;
						if (idx2 == 7 || idx2 == 2 || idx2 == 16) {
							area.writemap(x-1,y, 256*3 + 236);
							isReplacements = true;
						} else if (idx3 == 3 || idx3 == 2 || idx3 == 14) {
							area.writemap(x+1,y, 256*3 + 235);
							isReplacements = true;
						}
					}
					if (idx == 1 || idx == 0 || idx == 2) {
						idx2 = area.readmap(x-1,y-1) - 256*3;
						idx3 = area.readmap(x+1,y-1) - 256*3;
						if (idx2 == 7 || idx2 == 2 || idx2 == 14) {
							area.writemap(x-1,y, 256*3 + 234);
							isReplacements = true;
						} else if (idx3 == 3 || idx3 == 0 || idx3 == 14) {
							area.writemap(x+1,y, 256*3 + 233);
							isReplacements = true;
						}
					}
				}
			}
		}
		if (isReplacements) {
			System.out.println("replacements !");
		}
		return isReplacements;
	}
}
