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
import zildo.monde.util.Angle;
import zildo.server.EngineZildo;

/**
 * @author Tchegito
 * 
 */
public class ReplaceAllMapsPortraits extends AllMapProcessor {

	enum PortraitOrientation {
		NORTH(46, Angle.NORD, 2, 2, 18, 18); 

		int index;
		Angle angle;
		int[] repl;

		private PortraitOrientation(int index, Angle angle, int... replacements) {
			this.index = index;
			this.angle = angle;
			this.repl = replacements;
		}

		public static PortraitOrientation fromIndex(int i) {
			for (PortraitOrientation wo : values()) {
				if (wo.index == i) {
					return wo;
				}
			}
			return null;
		}

	}

	@Override
	public boolean run() {
		boolean isReplacement = false;
		Area area = EngineZildo.mapManagement.getCurrentMap();
		if (area.getName().equals("voleursm2.map")) {
			return false;
		}
		for (int y = 0; y < area.getDim_y(); y++) {
			for (int x = 0; x < area.getDim_x(); x++) {
				Case mapCase = area.get_mapcase(x, y + 4);
				int ind = mapCase.getBackTile().getValue() - 256 * 2;
				if (ind >= 224 && ind <= 227) {
					System.out.println("found portrait done with tiles !");
					PortraitOrientation wo = PortraitOrientation.NORTH;
					if (wo != null) {
						// 1: replace tiles
						area.writemap(x, y, 256*2 + wo.repl[0]);
						area.writemap(x + 1, y, 256*2 + wo.repl[1]);
						area.writemap(x, y + 1, 256*2 + wo.repl[2]);
						area.writemap(x + 1, y + 1, 256*2 + wo.repl[3]);
						// 2/ put window sprite
						Reverse rev = Reverse.NOTHING;
						Rotation rot = Rotation.fromAngle(wo.angle);
						int xx = x * 16 + 16;
						int yy = y * 16 + 16;
						switch (wo.angle) {
						case EST:
						case OUEST:
							xx+=8;
							break;
						case NORD:
						case SUD:
							yy+=8;
						}
						EngineZildo.spriteManagement.spawnElement(ElementDescription.WINDOW_WOOD, xx, yy, 0, rev,
								rot);
						isReplacement = true;
					}
				}
			}
		}
		return isReplacement;
	}

	public static void main(String[] args) {
		new ReplaceAllMapsPortraits().modifyAllMaps();
	}
}
