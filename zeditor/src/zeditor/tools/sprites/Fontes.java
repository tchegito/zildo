/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
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

import java.util.Arrays;

import zeditor.tools.tiles.GraphChange;
import zildo.monde.util.Zone;

/**
 * @author Tchegito
 *
 */
public class Fontes extends SpriteBanque {

	public Fontes() {
		zones = new Zone[] {
				// Le cadre
				new Zone(0, 0, 10, 14),
				new Zone(12, 0, 10, 14),
				// Les icones de l'interface
				new Zone(0, 86, 7, 9),	// Blue drop
				new Zone(7, 86, 7, 9),	// Empty drop
				new Zone(14, 87, 9, 8),	// Coin
				new Zone(24, 84, 7, 11),	// Dynamite
				new Zone(31, 85, 10, 9),	// Arrow
				new Zone(50, 89, 11, 5),	// Key
				// Les 10 chiffres
				new Zone(0, 117, 7, 9), new Zone(7, 117, 7, 9),
				new Zone(14, 117, 7, 9), new Zone(21, 117, 7, 9),
				new Zone(28, 117, 7, 9), new Zone(35, 117, 7, 9),
				new Zone(42, 117, 7, 9), new Zone(49, 117, 7, 9),
				new Zone(56, 117, 7, 9), new Zone(63, 117, 7, 9),
				// Vie
				new Zone(43, 86, 7, 9),	// Small drop
								
				// Jauge etc...
				new Zone(185, 0, 16, 42),
				new Zone(201, 22, 8, 2),
				new Zone(201, 24, 16, 15),
				//new Zone(201, 0, 22, 22),
				
				// 	Virtual pad
				new Zone(223, 0, 80, 80),
				// Buttons (X and Y)
				new Zone(0, 577, 24, 24), new Zone(24, 577, 24, 24),
				// Compass
				new Zone(374, 0, 25, 23),
				//new Zone(0, 524, 24, 25), new Zone(26, 524, 24, 25),
				//new Zone(0, 551, 24, 25), new Zone(26, 551, 24, 25)
				//Touch aura
				new Zone(308, 33, 31, 31),
				// Squirrel for menu
				new Zone(185,43,9,16),
				// Gears for Android
				new Zone(0, 601, 28, 28)
		};
		
		pkmChanges = Arrays.asList(new GraphChange[]{
				new GraphChange("fontes5", 0, 0)});
	}
}
