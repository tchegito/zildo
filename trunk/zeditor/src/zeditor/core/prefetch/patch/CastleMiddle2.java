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
public class CastleMiddle2 extends AbstractPatch12 {

    final byte[] conv_value =
    { 0, 13, 11, 12, 6, 10, 0, 108, 4, 0, 8, 107, 5, 106, 105, 0};

    final byte[] value = // Valeurs en zone des chemins
	    getReverseTab(conv_value, 4);
    
	public CastleMiddle2() {
		super(true);
	}

	@Override
	public
	int toBinaryValue(int p_val) {
		int a = p_val - 256 * 7 - 4;
		if (a < 0 || a >= value.length) {
			return 0;
		}
		return value[a];
	}

	@Override
	public
	int toGraphicalValue(int p_val) {
		return conv_value[p_val] + 256 * 7;
	}

}
