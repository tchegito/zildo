/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
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

package zeditor.tools.builder;

import zildo.monde.map.Area;
import zildo.monde.map.Case;
import zildo.monde.sprites.Reverse;
import zildo.monde.sprites.Rotation;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.server.EngineZildo;

/**
 * @author Tchegito
 * 
 */
public class ReplaceAllMapsTables extends AllMapProcessor {

	@Override
	public boolean run() {
		boolean isReplacement = false;
		//if (true) return true;
		Area area = EngineZildo.getMapManagement().getCurrentMap();
		/*if (area.getName().equals("voleursm2.map")) {
			return false;
		}*/
		for (int y = 0; y < area.getDim_y(); y++) {
			for (int x = 0; x < area.getDim_x(); x++) {
				Case mapCase = area.get_mapcase(x, y + 4);
				int ind = mapCase.getBackTile().getValue() - 256 * 2;
				if (ind >= 117 && ind <= 119) {
					int nTile = ind - 10;
					System.out.println("found tables with accessories with tiles !");
					int xx = x * 16 + 16;
					int yy = y * 16 + 16;
					ElementDescription desc = null;
					switch (ind) {
					case 117:
						desc = ElementDescription.FORK;
						xx-=2;
						break;
					case 118:
						desc = ElementDescription.PLATE;
						xx-=8;
						break;
					case 119:
						desc = ElementDescription.CANDLE1;
						xx-=8;
						yy-=1;
						break;
					}
					EngineZildo.spriteManagement.spawnElement(desc, xx, yy, 0, 
							Reverse.NOTHING, Rotation.NOTHING);
					area.writemap(x, y, 256*2 + nTile);
					isReplacement = true;
				}
			}
		}
		return isReplacement;
	}

	public static void main(String[] args) {
		new ReplaceAllMapsTables().modifyAllMaps();
	}
}
