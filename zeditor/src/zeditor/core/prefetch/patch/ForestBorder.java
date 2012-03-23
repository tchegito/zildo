/**
 * Legend of Zildo
 * Copyright (C) 2006-2012 Evariste Boussaton
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

package zeditor.core.prefetch.patch;


/**
 * @author Tchegito
 * 
 */
public class ForestBorder extends AbstractPatch12 {

	byte[] value_border = new byte[] { 15, 0, 0, 7, 3, 11, 1, 2, 5, 10, 15, 4,
			8, 15, 13, 12, 14 };

	byte[] conv_value_border = new byte[] { 73, 79, 80, 77, 84, 81, -1, 76, 85,
			-1, 82, 78, 88, 87, 89, 73, -1 };

	int startRoad = 256 * 6 + 73;

	public ForestBorder(boolean p_big) {
		super(p_big);
	}

	@Override
	public int toBinaryValue(int p_val) {
		int i = p_val - startRoad;
		if (i >= 0 && i < value_border.length) {
			return value_border[i];
		} else {
			return 0;
		}
	}

	@Override
	public int toGraphicalValue(int p_val) {
		int val = conv_value_border[p_val];
		if (val == -1) {
			val = 54;
		} else {
			val = 256 * 6 + val;
		}
		return val;
	}

}
