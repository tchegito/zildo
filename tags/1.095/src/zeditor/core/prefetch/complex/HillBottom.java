/**
 * Legend of Zildo
 * Copyright (C) 2006-2011 Evariste Boussaton
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

package zeditor.core.prefetch.complex;


/**
 * @author Tchegito
 *
 */
public class HillBottom extends AbstractPatch12 {
	
	int[] conv_hill;
	int[] conv_hill_value = {54, 11, 13, 12, 4, 9, 0, 136, 
							 2, 0, 15, 137, 3, 138, 246, 49};
	
	/**
	 * @param p_big
	 */
	public HillBottom() {
		super(true);
		
		// creat Conv_hill
		conv_hill = new int[247];
		for (int i=0;i<73;i++) {	// default values
			conv_hill[i]=0;
		}
		for (int i=0;i<conv_hill_value.length;i++) {
			conv_hill[conv_hill_value[i]] = i;
		}
	}

	@Override
	int toBinaryValue(int p_val) {
		if (p_val == 54) {
			return 0;
		}
		int i = p_val;
		if (i >= 0 && i < conv_hill.length) {
			return conv_hill[i];
		} else return 15;
	}

	@Override
	int toGraphicalValue(int p_val) {
		if (p_val == 0) {
			return 54;
		}
		return conv_hill_value[p_val];
	}

}
