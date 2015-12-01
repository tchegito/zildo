/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
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

package zeditor.core.prefetch.patch;

import zeditor.tools.AreaWrapper;
import static zildo.server.EngineZildo.hasard;

/**
 * Render for road (little or big one)
 * 
 * @author Tchegito
 * 
 */
public class Road extends AbstractPatch12 {

	byte[] value_chemin = // Valeurs en zone des chemins
	{ 0, 0, 0, 0, 0, 0, 0, 0, 8, 12, 4, 5, 1, 3, 2, 10, 14, 13, 7, 11, 14, 13,
			7, 11, 15 };

	byte[] conv_value_chemin = // Renvoie le motif en fonction de la valeur en
								// zone}
	{ -3, 4, 6, 5, 2, 3, 16, 10, 0, 16, 7, 15, 1, 13, 12, 16 };

	int startRoad = 49;

	public Road(boolean p_big) {
		super(p_big);
	}

	@Override
	protected void drawOneTile(AreaWrapper p_map, int p_x, int p_y, int p_val) {
		if (p_val > startRoad + 8 + 11 && p_val < startRoad + 8 + 16
				&& hasard.lanceDes(5)) {
			p_val -= 4;
		}
		p_map.writemap(p_x, p_y, p_val);
	}

	@Override
	public int toBinaryValue(int p_val) {
		int i = p_val - startRoad;
		if (i >= 0 && i < value_chemin.length) {
			return value_chemin[i];
		} else {
			return 0;
		}
	}

	@Override
	public int toGraphicalValue(int p_val) {
		return conv_value_chemin[p_val] + startRoad + 8;
	}
}
