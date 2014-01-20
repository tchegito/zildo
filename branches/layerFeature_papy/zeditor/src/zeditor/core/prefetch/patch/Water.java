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

import zeditor.core.prefetch.complex.Adjustment;
import zildo.monde.map.Area;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;

/**
 * @author Tchegito
 * 
 */
public class Water extends AbstractPatch12 {

	byte[] conv_water = { 12, 11, 0, 4, 5, 1, 7, 0, 0, 0, 0, 2, 10, 8, 13, 0,
			0, 0, 3, 0, 0, 15, 14 };

	int[] conv_water_value = { 0, 113, 119, 126, 111, 112, 0, 114, 121, 0, 120,
			109, 108, 122, 130, 129 };

	int startWater = 108;

	// Add hills around the water
	Adjustment[] adjustments = new Adjustment[] {
			new Adjustment(121, Angle.NORD, 28, 26, 27),
			new Adjustment(108, Angle.NORD, 131, 31, 32),
			new Adjustment(111, Angle.NORD, 36, 16, 35),
			new Adjustment(121, Angle.OUEST, 136, 25),
			new Adjustment(120, Angle.OUEST, 134, 25),
			new Adjustment(119, Angle.OUEST, 24, 23),
			new Adjustment(119, Angle.SUD, 24, 23),
			new Adjustment(126, Angle.SUD, 116, 21),
			new Adjustment(113, Angle.SUD, 18, 21),
			new Adjustment(113, Angle.EST, 18, 19),
			new Adjustment(112, Angle.EST, 135, 17),
			new Adjustment(111, Angle.EST, 137, 17) };

	public Water() {
		super(true);
	}

	@Override
	public void draw(Area p_map, Point p_start) {
		super.draw(p_map, p_start);

	}

	@Override
	public
	int toBinaryValue(int p_val) {
		int i = p_val - startWater;
		if (i >= 0 && i < conv_water.length) {
			return conv_water[i];
		} else {
			return 0;
		}
	}

	@Override
	public
	int toGraphicalValue(int p_val) {
		return conv_water_value[p_val];
	}

}
