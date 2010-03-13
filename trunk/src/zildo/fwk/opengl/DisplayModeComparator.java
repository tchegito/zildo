/**
 * Legend of Zildo
 * Copyright (C) 2006-2010 Evariste Boussaton
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

package zildo.fwk.opengl;

import java.util.Comparator;

import org.lwjgl.opengl.DisplayMode;

public class DisplayModeComparator implements Comparator<DisplayMode> {

	/**
	 * Returns -1 if o1 is better than o2
	 */
	public int compare(DisplayMode o1, DisplayMode o2) {
		if (o1.getBitsPerPixel() >= o2.getBitsPerPixel()) {
			return -1;
		} else {
			return 1;
		}
	}
}
