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

package zeditor.core.prefetch.patch;

/**
 * @author Tchegito
 *
 */
public class HouseInsidePurple2 extends AbstractPatch12 {

	int[] conv_value = 
	{ -1, 13, 15, 14, 11, 12, -1, 118, 9, -1, 16, 118, 10, 118, 118, -1 };

	int[] value =
		getReverseTab(conv_value, 9);

	public HouseInsidePurple2() {
		super(true);
	}
	
	@Override
	public
	int toBinaryValue(int p_val) {
		int a = p_val - 256 * 2 - 9;
		if (a < 0 || a >= value.length) {
			return 0;
		}
		return value[a];
	}

	@Override
	public
	int toGraphicalValue(int p_val) {
		return conv_value[p_val] + 256 * 2;
	}

}
