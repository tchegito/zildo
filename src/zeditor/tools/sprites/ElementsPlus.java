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
 *         Class designed in order to add other sprites to ELEM.SPR (original
 *         from Zildo-2005)
 */
public class ElementsPlus extends SpriteBanque {

	public ElementsPlus() {
		zones = new Zone[] {
/*
				// Buisson,pot,ombre
				new Zone(0, 0, 12, 13),
				new Zone(0, 13, 16, 15),
				new Zone(0, 28, 12, 6),
				// Feuilles
				new Zone(16, 13, 8, 8),
				new Zone(16, 21, 8, 8),
				// Fumée
				new Zone(0, 34, 14, 14),
				new Zone(14, 34, 12, 12),
				// Brique cassée
				new Zone(0, 48, 8, 8),
				new Zone(8, 48, 8, 8),
				// Master Key
				new Zone(0, 56, 14, 16),
				// Coeur
				new Zone(12, 4, 8, 9),
				// Pierres
				new Zone(0, 72, 16, 15),
				new Zone(16, 72, 16, 15),

				// Sprites fixes
				// Eau
				new Zone(152, 0, 8, 8),
				new Zone(160, 0, 16, 8),
				new Zone(176, 0, 8, 8),
				new Zone(152, 8, 8, 16),
				new Zone(176, 8, 8, 16),
				new Zone(152, 24, 8, 8),
				new Zone(160, 24, 16, 8),
				new Zone(176, 24, 8, 8),
				// Séparation de hauteur dans les grottes (pointu!)
				new Zone(184, 0, 6, 6),
				new Zone(190, 0, 6, 6),
				new Zone(184, 6, 6, 6),
				new Zone(190, 6, 6, 6),
				// Rambardes grotte
				new Zone(184, 12, 8, 8),
				new Zone(184, 24, 16, 8),
				new Zone(196, 0, 8, 16),
				// Tonneau - Pierres
				new Zone(152, 32, 16, 24),
				new Zone(168, 32, 7, 7),
				new Zone(168, 39, 16, 8),
				new Zone(184, 32, 8, 16),
				// Poule - 32
				new Zone(0, 87, 17, 16),

				// Mort d'un ennemi
				new Zone(45, 0, 8, 8),
				new Zone(54, 0, 8, 8),
				new Zone(45, 8, 16, 16),
				new Zone(62, 8, 16, 16),
				new Zone(45, 24, 16, 15),
				new Zone(61, 24, 22, 22),
				new Zone(83, 0, 28, 28),

				// Coeur qui vole
				new Zone(20, 4, 8, 7),
				new Zone(24, 11, 8, 7),

				// Boule en pierre,ombre et éclats
				new Zone(16, 46, 8, 8),
				new Zone(12, 30, 8, 4),
				new Zone(20, 56, 6, 7),
				new Zone(14, 56, 6, 7),
				new Zone(20, 63, 6, 7),
				new Zone(14, 63, 6, 7),

				// Diamant (argent)
				new Zone(0, 103, 8, 14),
				new Zone(8, 103, 8, 14),
				new Zone(16, 103, 8, 14),
				new Zone(0, 117, 8, 14),
				new Zone(8, 117, 8, 14),
				new Zone(16, 117, 8, 14),
				new Zone(0, 131, 8, 14),
				new Zone(8, 131, 8, 14),
				new Zone(16, 131, 8, 14),

				// Fumée
				new Zone(17, 87, 8, 8),
				new Zone(25, 87, 7, 7),
				new Zone(17, 95, 5, 5),

				// Petite ombre (abeille)
				new Zone(20, 30, 6, 4),

				// Poutre d'entrée de grotte
				new Zone(168, 48, 16, 8),
				new Zone(192, 32, 8, 8),
				new Zone(192, 40, 8, 8),

				// Rocher et ombre
				new Zone(24, 95, 31, 31),
				new Zone(24, 126, 31, 31),
				new Zone(24, 18, 18, 6),

				// Bouclier
				new Zone(26, 24, 16, 24),

				// Pilier de bois
				new Zone(26, 48, 8, 16),

				// Blocs à bouger
				new Zone(0, 145, 16, 16),
				new Zone(0, 161, 16, 16),
*/
				// From here :Objets.png
				new Zone(36, 0, 8, 15),
				new Zone(48, 0, 16, 16),
				new Zone(65, 2, 14, 13),
				new Zone(80, 1, 16, 15),
				new Zone(96, 0, 16, 16),
				new Zone(114, 0, 12, 16),
				new Zone(130, 0, 12, 16),
				new Zone(144, 0, 16, 16),
				new Zone(144, 17, 16, 16),
				// Excalibur
				new Zone(163, 0, 10, 22), new Zone(178, 0, 13, 16),
				new Zone(193, 0, 15, 16), new Zone(209, 0, 14, 16),
				new Zone(193, 17, 14, 16), new Zone(193, 34, 14, 16),
				new Zone(84, 40, 8, 9),
				new Zone(100, 34, 8, 16),
				new Zone(112, 34, 16, 16),
				new Zone(113, 17, 14, 16),
				new Zone(128, 17, 16, 17),
				new Zone(208, 35, 15, 15),
				// Coupe
				new Zone(0, 52, 16, 16), new Zone(208, 52, 16, 16),
				new Zone(26, 70, 12, 14), new Zone(42, 70, 12, 14),
				new Zone(58, 70, 12, 14),
				new Zone(74, 70, 12, 14),
				// Bouclier rouge
				new Zone(89, 69, 14, 16), new Zone(121, 69, 14, 16),
				new Zone(18, 56, 3, 3), new Zone(22, 61, 5, 5),
				new Zone(23, 53, 7, 7),
				// 3 bombs
				new Zone(142, 68, 16, 16),
				// Scepter
				new Zone(100, 17, 23, 11),
				// Spade
				new Zone(0,177,11,19),
				// Book sign
				new Zone(11, 177, 32, 20),
				// Leaf
				new Zone(16, 169, 8, 7),
				// Milk
				new Zone(43, 181, 11, 16),
				// House options
				new Zone(104, 176, 32, 16),	// Window
				new Zone(136, 176, 26, 15),	// Portrait
				// Staff (which makes sound 'poum')
				new Zone(24, 169, 18, 8),
				// Water bridge
				new Zone(111, 0, 32, 24),
				// Flag
				new Zone(55, 95, 32, 24),	// Red
				new Zone(87, 95, 32, 24),	// Blue
				// Window (castle)
				new Zone(55, 119, 23, 32),
				// Statue
				new Zone(78, 119, 16, 24),
				// Door
				new Zone(55, 151, 16, 16), new Zone(55, 167, 16, 5)
		};
	}
}
