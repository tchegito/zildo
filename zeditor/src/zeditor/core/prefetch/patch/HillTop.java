/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
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
import zeditor.tools.AreaWrapper;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;

/**
 * @author Tchegito
 * 
 */
public class HillTop extends AbstractPatch12 {

	// Cases équivalents :
	// 16 = 34
	// 26 = 29
	// 28 = 30
	// 33 = 36

	int[] conv_hill; // ={5, 0, 1, 7, 3, 11, 2, 0, 10, 0, 8, 0, 14, 0, 0, 12, 0,
						// 13, 4};

	int[] conv_hill_value = { 54, 29, 34, 32, 22, 25, 0, 27, 20, 0, 17, 35, 21,
			23, 19, 49 };
	/*
	 * 54, 27, 35, 32, 23, 25, 0, 27, 19, 0, 17, 35, 21, 21, 20, 49};
	 */

	Adjustment[] adjustments = new Adjustment[] {
			new Adjustment(32, Angle.SUD, 31, 12),
			new Adjustment(34, Angle.SUD, 36, 13),
			new Adjustment(34, Angle.OUEST, 14),
			new Adjustment(34, Angle.SUDOUEST, 13),
			new Adjustment(29, Angle.SUD, 28, 11),
			new Adjustment(29, Angle.EST, 10),
			new Adjustment(29, Angle.SUDEST, 11),
			new Adjustment(20, Angle.NORD, 2),
			new Adjustment(20, Angle.NORDOUEST, 1),
			new Adjustment(20, Angle.OUEST, 7),
			new Adjustment(21, Angle.NORD, 3),
			new Adjustment(22, Angle.NORD, 4),
			new Adjustment(22, Angle.NORDEST, 6),
			new Adjustment(22, Angle.EST, 5),
			new Adjustment(17, Angle.OUEST, 15),
			new Adjustment(25, Angle.EST, 9)

	};

	HillBottom hillBottom = new HillBottom();

	@Override
	public AbstractPatch12 getAdjustmentClass() {
		return hillBottom;
	}

	/**
	 * @param p_big
	 */
	public HillTop(boolean p_big) {
		super(p_big);

		/*
		 * setBigPatch(new int[] {0, 0, 12, 0, 0, 0, 8, 15, 4, 0, 10, 15, 15,
		 * 15, 5, 0, 2, 15, 1, 0, 0, 0, 3, 0, 0});
		 */
		conv_hill = new int[74];
		for (int i = 0; i < 73; i++) { // default values
			conv_hill[i] = 0;
		}
		for (int i = 0; i < conv_hill_value.length; i++) {
			conv_hill[conv_hill_value[i]] = i;
		}
	}

	@Override
	public void draw(AreaWrapper p_map, Point p_start) {
		super.draw(p_map, p_start);

		super.drawAdjustments(p_map, p_start);
	}

	@Override
	public Adjustment[] getAdjustments() {
		return adjustments;
	}

	@Override
	public
	int toBinaryValue(int p_val) {
		if (p_val == 73) {
			return 15;
		}
		int i = p_val;
		if (i >= 0 && i < conv_hill.length) {
			return conv_hill[i];
		} else {
			return 0;
		}
	}

	@Override
	public
	int toGraphicalValue(int p_val) {
		return conv_hill_value[p_val];
	}

}
