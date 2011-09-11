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

import java.util.Arrays;

import zeditor.tools.tiles.GraphChange;
import zildo.monde.map.Zone;

/**
 * @author Tchegito
 *
 */
public class Pnj2 extends SpriteBanque {

	public Pnj2() {
		zones=new Zone[] {
			      // Maitre d'hotel - Oncle - Vieux
			          new Zone(95,80,16,24),new Zone(112,80,26,24),new Zone(139,83,16,21),
			          // Brigand - 3
			          new Zone(138,106,16,23),new Zone(170,106,16,23),new Zone(154,106,16,23),
			          // Brigand 2 - 6
			          new Zone(128,54,16,24),
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
			          new Zone(70,105,13,17),new Zone(83,101,12,21),new Zone(95,105,13,17),
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
			          // Princesse couchée
			          new Zone(0, 182, 24, 15), new Zone(24, 182, 22, 16),
			          new Zone(98, 173, 24, 15), new Zone(122, 173, 24, 15),
			          // Arbuste mouvant
			          new Zone(100, 0, 16, 17), new Zone(116, 0, 16, 17),
			          // Moustachu
			          new Zone(221, 100, 16, 24),
			          new Zone(237, 100, 14, 24), new Zone(251, 100, 14, 24),
			          // Electrique sous le choc
			          new Zone(108, 98, 15, 24), new Zone(123, 98, 15, 24),
			          new Zone(138, 99, 16, 23), new Zone(154, 99, 16, 23),
			          // Chauve-souris
			          new Zone(293, 4, 14, 12), new Zone(307, 4, 12, 16),
			          // Princess bunny
			          new Zone(69, 91, 13, 8), new Zone(82, 89, 11, 10),
			          // Garçon aux cheveux bleus
			          new Zone(210, 73, 16, 24), new Zone(226, 73, 14, 24),
			          // Garçon jaune
			          new Zone(241, 73, 16, 24), new Zone(257, 73, 14, 24),
			          // Fermière (villageoise rose)
			          new Zone(147, 175, 16, 24), new Zone(163, 175, 16, 24),
			          new Zone(179, 175, 14, 24), new Zone(193, 174, 13, 25),
			          new Zone(206, 174, 16, 25), new Zone(222, 174, 16, 25),
			          // Hector
			          new Zone(205, 125, 16, 23), new Zone(221, 125, 16, 24),
			          new Zone(237, 125, 14, 24), new Zone(251, 125, 14, 24)
			          
		};
		
		pkmChanges = Arrays.asList(new GraphChange[] { new GraphChange("pnj", 0, 0), new GraphChange("pnj2", 7, 0), 
				new GraphChange("pnj", 23, 0),
				new GraphChange("pnj2", 31, 0)});
	}
}
