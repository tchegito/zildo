/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
 * 
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
public class Pnj2 extends SpriteBanque {

	public Pnj2() {
		zones=new Zone[] {
			      // Maitre d'hotel - Oncle - Vieux
			          new Zone(205,100,16,24),new Zone(112,80,26,24),new Zone(139,83,16,22),
			          // Brigand - 3
			          new Zone(138,106,16,23),new Zone(170,106,16,23),new Zone(154,106,16,23),
			          // Brigand 2 - 6
			          new Zone(80, 239, 16, 24),
			          //new Zone(128,54,16,24),
			          // Brigand 3 - 7
			          new Zone(65,123,16,24),new Zone(81,123,16,24),
			          new Zone(65,147,15,24),new Zone(80,147,16,24),
			          new Zone(65,171,16,23),new Zone(81,171,16,23),
			          new Zone(95,147,15,24),new Zone(110,147,15,24),
			          // Homme en tailleur - 15
			          new Zone(47,158,16,24),
			          // Vautour - 16
			          new Zone(102,69,18,15),new Zone(120,71,24,13),new Zone(144,70,32,14),
			          // Spectre aux grandes mains - 19
			          new Zone(102,36,41,16),new Zone(102,52,41,16),
			          // Sahasrala - 21
			          new Zone(13,158,16,24),new Zone(29,157,16,25),
			          // Arc - 23
			          new Zone(299,129,16,7),new Zone(300,120,14,8),new Zone(300,76,8,14),new Zone(309,75,6,16),
			          new Zone(299,46,16,7),new Zone(300,54,14,8),new Zone(312,94,8,14),new Zone(305,93,6,16),
			          // Electrique vert - 31
			          new Zone(0, 307, 12, 21), new Zone(12, 308, 12, 20), new Zone(24, 309, 12, 19),
			          //new Zone(70,105,13,17),new Zone(83,101,12,21),new Zone(95,105,13,17),
			          // Squelette - 34
			          new Zone(200,28,16,25),new Zone(216,28,16,25),
			          new Zone(258,27,12,26),new Zone(270,27,14,26),
			          new Zone(168,27,16,26),new Zone(184,27,16,26),
			          new Zone(232,27,12,26),new Zone(244,27,14,26),
			          // Vieux - 42
			          new Zone(0,123,15,17),new Zone(15,123,15,17),new Zone(30,124,15,16),new Zone(45,123,16,17),
			          new Zone(0,140,16,17),new Zone(16,140,16,17),new Zone(32,141,15,16),new Zone(47,140,16,17),
			          // Lapin - 50
			          new Zone(17,110,16,12),new Zone(33,108,16,14),
			          // Oiseau vert - 52
			          new Zone(159,147,16,13), new Zone(175,147,16,16),
			          // Volant bleu
			          new Zone(178, 53, 18, 16), new Zone (196, 53, 26, 15),
			          new Zone(222, 53, 14, 16), new Zone (236, 53, 16, 15),
			          new Zone(252, 53, 18, 16), new Zone (270, 53, 24, 14),
			          // Princesse couchée (60)
			          new Zone(0, 182, 24, 15), new Zone(24, 182, 22, 16),
			          new Zone(98, 173, 24, 15), new Zone(122, 173, 24, 15),
			          // Arbuste mouvant
			          new Zone(100, 0, 16, 17), new Zone(116, 0, 16, 17),
			          // Moustachu (66)
			          new Zone(221, 100, 16, 24),
			          new Zone(237, 100, 14, 24), new Zone(251, 100, 14, 24),
			          // Electrique sous le choc
			          new Zone(36, 309, 14, 19), new Zone(50, 311, 16, 17),
			          //new Zone(108, 98, 15, 24), new Zone(123, 98, 15, 24),
			          new Zone(138, 99, 16, 23), new Zone(154, 99, 16, 23),
			          // Chauve-souris
			          new Zone(272, 67, 17, 15), new Zone(289, 67, 23, 15),
			          //new Zone(293, 4, 14, 12), new Zone(307, 4, 12, 16),
			          // Princess bunny
			          new Zone(69, 91, 13, 8), new Zone(82, 89, 11, 10),
			          // Garçon aux cheveux bleus
			          new Zone(210, 73, 16, 24), new Zone(226, 73, 14, 24),
			          // Garçon jaune
			          new Zone(241, 73, 16, 24), new Zone(257, 73, 14, 24),
			          // Fermière (villageoise rose) (81)
			          new Zone(147, 175, 16, 24), new Zone(163, 175, 16, 24),
			          new Zone(179, 175, 14, 24), new Zone(193, 174, 13, 25),
			          new Zone(206, 174, 16, 25), new Zone(222, 174, 16, 25),
			          // Hector
			          new Zone(205, 125, 16, 23), new Zone(221, 125, 16, 24),
			          new Zone(237, 125, 14, 24), new Zone(251, 125, 14, 24),
			          // SOM rabbit (regular size)
			          /*
			          new Zone(0, 198, 22, 16), new Zone(22, 198, 23, 16),
			          new Zone(45, 198, 27, 15), new Zone(72, 198, 19, 23),
			          new Zone(91, 198, 21, 19)
			          */
			          new Zone(0, 221, 19, 12), new Zone(19, 221, 19, 12),
			          new Zone(38, 221, 22, 12), new Zone(61, 221, 15, 17),
			          new Zone(76, 221, 17, 15),
			          // King (96)
			          new Zone(265, 107, 35, 33),
			          // Hat burglar
			          new Zone(0, 239, 16, 25), new Zone(16,240, 16, 24),
			          new Zone(32, 239, 16, 25), new Zone(48, 240, 16, 24),
			          new Zone(64, 239, 16, 25),
			          // Singer
			          new Zone(0, 265, 16, 25), new Zone(16, 265, 16, 25),
			          // Fire thing
			          new Zone(0, 291, 6, 6), new Zone(0, 298, 6, 6),
			          new Zone(7, 291, 12, 12), new Zone(19, 291, 16, 16),
			          // Rat (108)
			          /*
			          new Zone(147, 200, 12, 16), new Zone(159, 200, 12, 15),
			          new Zone(171, 200, 16, 11), new Zone(187, 200, 16, 12),
			          new Zone(203, 200, 15, 16), new Zone(218, 200, 15, 16),
			          new Zone(233, 201, 12, 15), new Zone(245, 200, 12, 16),
			          */
			          new Zone(147, 218, 8, 16), new Zone(155, 217, 8, 17),
			          new Zone(163, 217, 22, 9), new Zone(185, 217, 22, 9),
			          new Zone(207, 217, 20, 12), new Zone(228, 217, 21, 12),
			          new Zone(249, 217, 9, 16), new Zone(258, 217, 8, 18),
			          // Fox (116)
			          new Zone(221, 149, 16, 23), new Zone(205, 149, 16, 23),
			          new Zone(237, 149, 14, 25), new Zone(251, 149, 15, 25),
			          new Zone(282, 149, 16, 24), new Zone(266, 149, 16, 24),
			          // Sorcerer (perso) (122)
			          new Zone(272, 163, 16, 25), new Zone(291, 163, 16, 25),
			          // Vieux bleu (123)
			          new Zone(171, 1, 16, 22),
			          // Stone spider
			          new Zone(0, 329, 13, 13), new Zone(14, 329, 14, 13),
			          // Paper note
			          new Zone(94, 89, 9, 11),
			          // Sleeping fox
			          new Zone(299, 149, 14, 14), new Zone(299, 164, 13, 14),
			          new Zone(314, 149, 5, 6)

		};
		
		pkmChanges = Arrays.asList(new GraphChange[] { new GraphChange("pnj2", 0, 0), 
				new GraphChange("pnj", 1, 0), 
				new GraphChange("pnj2", 6, 0), 
				new GraphChange("pnj", 23, 0),
				new GraphChange("pnj2", 31, 0),
				new GraphChange("pnj", 122, 0),
				new GraphChange("pnj2", 125, 0)
				});
	}
}
