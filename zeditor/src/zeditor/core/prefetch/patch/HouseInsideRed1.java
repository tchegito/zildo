/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
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
public class HouseInsideRed1 extends AbstractPatch12 {

	int[] conv_value = 
	{ 33, 21, 23, 22, 19, 20, -1, 241, 17, -1, 24, 242, 18, 243, 244, 33 };

	int[] value =
		getReverseTab(conv_value, 17);

	public HouseInsideRed1() {
		super(true);
	}

	@Override
	public
	int toBinaryValue(int p_val) {
		int a = p_val - 256 * 2 - 17;
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
