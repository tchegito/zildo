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

package zeditor.tools.sprites;

import zildo.monde.map.Zone;

/**
 * @author Tchegito
 *
 * Class designed in order to add other sprites to ELEM.SPR (original from Zildo-2005)
 */
public class ElementsPlus extends SpriteBanque {

	public ElementsPlus() {
		zones=new Zone[] {
				new Zone(36, 0, 8, 15), new Zone(48, 0, 16, 16), new Zone(65, 2, 14, 13),
				new Zone(80, 1, 16, 15), new Zone(96, 0, 16, 16), new Zone(114, 0, 12, 16),
				new Zone(130, 0, 12, 16), new Zone(144, 0, 16, 16), new Zone(144, 17, 16, 16),
				// Excalibur
				new Zone(163, 0, 10, 22), new Zone(178, 0, 13, 16), new Zone(193, 0, 15, 16),
				new Zone(209, 0, 14, 16), new Zone(193, 17, 14, 16), new Zone(193, 34, 14, 16),
				new Zone(84, 40, 8, 9), new Zone(100, 34, 8, 16), new Zone(112, 34, 16, 16),
				new Zone(113, 17, 14, 16), new Zone(128, 17, 16, 17), new Zone(208, 35, 15, 15),
				// Coupe
				new Zone(0, 52, 16, 16), new Zone(208, 52, 16, 16), new Zone(26, 70, 12, 14),
				new Zone(42, 70, 12, 14), new Zone(58, 70, 12, 14), new Zone(74, 70, 12, 14),
				// Bouclier rouge
				new Zone(89, 69, 14, 16), new Zone(121, 69, 14, 16), new Zone(18, 56, 3, 3),
				new Zone(22, 61, 5, 5), new Zone(23, 53, 7, 7),
				// 3 bombs
				new Zone(142, 68, 16, 16)
		};
	}
}
