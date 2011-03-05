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

package zeditor.tools.sprites;

import zildo.monde.map.Zone;

/**
 * @author Tchegito
 *
 */
public class Fontes extends SpriteBanque {

	public Fontes() {
		zones = new Zone[] {
				// Le cadre
				new Zone(0, 73, 7, 7),
				new Zone(7, 73, 7, 7),
				new Zone(0, 80, 7, 7),
				new Zone(7, 80, 7, 7),
				// Les icones de l'interface
				new Zone(0, 87, 7, 7),
				new Zone(7, 87, 7, 7),
				new Zone(14, 87, 8, 8),
				new Zone(22, 87, 8, 8),
				new Zone(30, 87, 14, 8),
				new Zone(51, 87, 8, 8),
				// Les 10 chiffres
				new Zone(0, 95, 7, 7), new Zone(7, 95, 7, 7),
				new Zone(14, 95, 7, 7), new Zone(21, 95, 7, 7),
				new Zone(28, 95, 7, 7), new Zone(35, 95, 7, 7),
				new Zone(42, 95, 7, 7), new Zone(49, 95, 7, 7),
				new Zone(56, 95, 7, 7), new Zone(63, 95, 7, 7),
				// Vie
				new Zone(0, 102, 44, 7), new Zone(44, 87, 7, 7),

				// Jauge etc...
				new Zone(185, 0, 16, 42),
				new Zone(201, 21, 8, 2), new Zone(201, 0, 22, 21) };
	}
}
