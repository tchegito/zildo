/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
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
 *         Class designed in order to add other sprites to ELEM.SPR (original
 *         from Zildo-2005)
 */
public class ElementsPlus extends SpriteBanque {

	public ElementsPlus() {
		zones = new Zone[] {

				// Buisson,pot,ombre
				new Zone(0, 0, 12, 13),
				new Zone(0, 13, 16, 15),
				new Zone(0, 28, 12, 6),
				// Feuilles
				new Zone(16, 13, 8, 8),
				new Zone(32, 9, 7, 9),	// Drop on the floor
				//new Zone(32, 7, 8, 11),	
				//new Zone(16, 21, 8, 8),
				// Fumée
				new Zone(0, 34, 14, 14),
				new Zone(14, 34, 12, 12),
				// Brique cassée
				new Zone(0, 48, 8, 8),
				// Feuille d'ortie
				new Zone(16, 21, 8, 8),
				// Fork
				new Zone(0, 264, 11, 24),
				// Coeur
				new Zone(12, 4, 8, 9),
				// Pierres
				new Zone(0, 72, 16, 15),
				new Zone(16, 72, 16, 15),

				// Sprites fixes (13)
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
				// Rambardes grotte (25)
				new Zone(184, 12, 8, 8),
				new Zone(184, 24, 16, 8),
				new Zone(196, 0, 8, 16),
				// Tonneau - Pierres
				new Zone(152, 32, 16, 24),
				new Zone(168, 32, 7, 7),
				new Zone(168, 39, 16, 8),
				new Zone(184, 32, 8, 16),
				// Etai - 32
				new Zone(249, 69, 32, 16),

				// Mort d'un ennemi
				new Zone(45, 0, 8, 8),
				new Zone(54, 0, 8, 8),
				new Zone(45, 8, 16, 16),
				new Zone(62, 8, 16, 16),
				new Zone(45, 24, 16, 15),
				new Zone(61, 24, 22, 22),
				new Zone(83, 0, 28, 28),

				// Falling drop (ex: Coeur qui vole)
				new Zone(41, 10, 3, 3),
				new Zone(40, 0, 3, 5),
				//new Zone(20, 4, 8, 7),
				//new Zone(24, 11, 8, 7),

				// Boule en pierre,ombre et éclats (42)
				new Zone(16, 46, 8, 8),
				new Zone(12, 30, 8, 4),
				new Zone(20, 56, 6, 6),
				new Zone(14, 65, 8, 6),
				new Zone(20, 63, 6, 7),
				new Zone(14, 63, 6, 7),

				// Diamant (argent)
				new Zone(60, 87, 9, 8), new Zone(69, 87, 9, 8), new Zone(78, 87, 9, 8),
				//new Zone(0, 103, 8, 14),
				//new Zone(8, 103, 8, 14),
				//new Zone(16, 103, 8, 14),
				new Zone(94, 119, 14, 11), new Zone(108, 119, 14, 11), new Zone(122, 119, 14, 11),
				//new Zone(0, 117, 8, 14),
				//new Zone(8, 117, 8, 14),
				//new Zone(16, 117, 8, 14),
				new Zone(0, 131, 8, 14),
				new Zone(8, 131, 8, 14),
				new Zone(16, 131, 8, 14),

				// Fumée (57)
				new Zone(17, 87, 8, 8),
				new Zone(25, 87, 7, 7),
				new Zone(17, 95, 5, 5),

				// Petite ombre (abeille)
				new Zone(20, 30, 6, 4),

				// Poutre d'entrée de grotte
				new Zone(168, 48, 16, 8),
				new Zone(192, 32, 8, 8),
				// Etai - suite
				new Zone(239, 69, 9, 30),

				// Rocher et ombre (64)
				new Zone(24, 95, 31, 31),
				new Zone(0, 197, 32, 19),
				new Zone(24, 18, 18, 6),

				// Bouclier
				new Zone(26, 24, 16, 24),

				// Pilier de bois
				new Zone(26, 48, 8, 16),

				// Blocs à bouger (69)
				new Zone(0, 145, 16, 16),
				new Zone(0, 161, 16, 16),

				// Arrows (4 same sprites : N-E-S-O) (71)
				new Zone(0, 0, 5, 15), new Zone(5, 0, 15, 5),
				new Zone(20, 0, 5, 15), new Zone(25, 0, 15, 5),
				// Arrows landing (4 3-sized sequences with N-E-S-O)
				new Zone(40, 0, 5, 12), 
				new Zone(45, 0, 6, 11),	new Zone(51, 0, 6, 11), 
				new Zone(57, 0, 12, 5), 
				new Zone(69, 0, 11, 6), new Zone(80, 0, 11, 6),
				new Zone(91, 0, 5, 12),
				new Zone(96, 0, 6, 11), new Zone(102, 0, 6, 11),
				new Zone(108, 0, 12, 5),
				new Zone(120, 0, 11, 6), new Zone(131, 0, 11, 6),
				// Boomerang (4 sprites identically rotated) (87)
				new Zone(142, 0, 10, 10), new Zone(152, 0, 10, 10),
				new Zone(162, 0, 10, 10), new Zone(172, 0, 10, 10),
				// Bow (4 2-sized sequences with N-E-S-O)
				new Zone(182, 0, 16, 7), new Zone(198, 0, 14, 8),
				new Zone(212, 0, 6, 16), new Zone(0, 16, 8, 14),
				new Zone(8, 16, 16, 7), new Zone(24, 16, 14, 8),
				new Zone(38, 16, 6, 16), new Zone(44, 16, 8, 14),
				// Explosion (99)
				new Zone(52, 16, 8, 8), new Zone(60, 16, 12, 12),
				new Zone(72, 16, 14, 14), new Zone(86, 16, 16, 16),
				// Red projectile (103)
				new Zone(102, 16, 3, 3), new Zone(105, 16, 4, 4),
				new Zone(109, 16, 7, 7), 
				// Bomb (and explosion) (106)
				new Zone(118, 17, 10, 13), new Zone(129, 16, 14, 14),
				new Zone(143, 16, 15, 15), new Zone(158, 16, 15, 16),
				new Zone(173, 16, 14, 14), new Zone(187, 16, 15, 15),
				new Zone(212, 16, 8, 8),
				// Flut and sword (113)
				new Zone(210, 16, 15, 15), new Zone(225, 16, 15, 15),
				// Wood stones (115)
				new Zone(1, 303, 16, 8), new Zone(1, 312, 8, 16),

				// Quad (117)	// Phase of removing (this shouldn't stay in the Android version)
				//new Zone(0, 35, 17, 23), new Zone(17, 35, 17, 23),
				// Dynamite (half burnt)
				new Zone(133, 33, 10, 13),	// Following is quad's remaining. Feel free to override them
				new Zone(34, 35, 17, 23), //new Zone(51, 35, 17, 23),
				new Zone(68, 35, 17, 23), new Zone(85, 35, 17, 23),
				new Zone(102, 35, 17, 23), new Zone(119, 35, 17, 23),
				
				// From here :Objets.png (123)
				new Zone(32, 0, 15, 7),
				new Zone(48, 0, 16, 16),
				new Zone(16, 85, 19, 19),	// FULL_MOON
				new Zone(35, 85, 9, 16),	 // MOON_FRAGMENT1
				new Zone(99, 0, 10, 16),
				new Zone(114, 0, 12, 16),
				new Zone(130, 0, 12, 16),
				new Zone(144, 0, 16, 16),
				new Zone(144, 17, 17, 17),	// Item scepter
				// Excalibur (133)
				new Zone(163, 0, 10, 22), new Zone(178, 0, 13, 16),
				new Zone(193, 0, 15, 16), /*new Zone(209, 0, 14, 16),*/ new Zone(99, 49, 20, 18),	// Another creeper
				new Zone(193, 17, 14, 16), new Zone(193, 34, 14, 16),
				new Zone(84, 40, 8, 9),
				new Zone(100, 34, 8, 16),
				new Zone(112, 34, 16, 16),
				new Zone(113, 17, 14, 16),
				new Zone(128, 17, 16, 17),
				new Zone(208, 35, 15, 15),
				// Coupe (145)
				new Zone(0, 52, 16, 16), new Zone(208, 52, 16, 16),
				new Zone(25, 68, 14, 16), new Zone(41, 68, 14, 16),
				new Zone(57, 68, 14, 16),
				new Zone(73, 68, 14, 16),
				// Bouclier rouge (151)
				new Zone(89, 69, 14, 16), new Zone(121, 69, 14, 16),
				new Zone(18, 56, 3, 3), new Zone(22, 61, 5, 5),
				new Zone(23, 53, 7, 7),
				// 3 bombs
				new Zone(142, 69, 16, 16),
				// Scepter
				new Zone(52, 46, 24, 11),
				// Spade
				new Zone(0,177,11,19),
				// Book sign
				new Zone(11, 177, 32, 20),
				// Leaf
				new Zone(16, 169, 8, 7),
				// Milk (161)
				new Zone(43, 181, 11, 16),
				// House options
				new Zone(204, 0, 32, 16),	// Window
				new Zone(236, 0, 26, 15),	// Portrait
				// Staff (which makes sound 'poum')
				new Zone(24, 169, 18, 8),
				// Water bridge
				new Zone(111, 0, 32, 24),
				// Flag
				new Zone(55, 95, 32, 24),	// Red
				new Zone(87, 95, 32, 24),	// Blue
				// Window (castle)
				new Zone(55, 119, 22, 32),
				// Statue
				new Zone(78, 119, 16, 24),
				// Door
				new Zone(55, 151, 16, 16), new Zone(55, 167, 16, 5),
				// Cemetery's door
				new Zone(51, 72, 16, 8), //42, 39, 8, 22),
				// Carpet
				new Zone(16, 145, 8, 8),
				// Thief Launcher
				new Zone(33, 71, 8, 7), new Zone(42, 71, 8, 9),
				// Projectiles
				new Zone(85, 28, 16, 16), new Zone(101, 28, 16, 16),
				new Zone(117, 24, 31, 31),
				new Zone(76, 46, 16, 16),
				// Platform
				new Zone(200, 16, 32, 32),
				new Zone(110, 130, 16, 22), //94, 130, 8, 12),	// Blue effect (take drop)
				// Back on "objets.png"
				new Zone(44, 85, 15, 10),	// MOON_FRAGMENT2
				new Zone(0, 85, 16, 16),	// NECKLACE
				// Note
				//new Zone(27, 64, 4, 7),
				new Zone(51, 58, 3, 8),
				// Psychic and sorcerer sign
				new Zone(32, 197, 32, 19), new Zone(0, 216, 32, 16),
				// Note 2 (192)
				new Zone(55, 58, 7, 8),
				// Rock bag
				new Zone(59, 85, 16, 16), new Zone(75, 85, 4, 4),
				// Rock pillar
				new Zone(262, 0, 35, 35),
				// Lava drop animation
				new Zone(185, 49, 9, 9), new Zone(195, 49, 9, 9), new Zone(205, 49, 9, 9), new Zone(215, 49, 9, 9),
				// Dust (200)
				new Zone(185, 59, 3, 6), new Zone(189, 59, 3, 6), new Zone(193, 59, 3, 6),
  	            // Zzz
		        new Zone(63, 58, 7, 7), new Zone(63, 66, 7, 6),
		        // Crates
		        new Zone(16, 153, 16, 16), new Zone(24, 126, 16, 24),
		        // Little table
		        new Zone(32, 150, 16, 15),
		        // Decors (plate, fork and candle)
		        new Zone(232, 16, 16, 15), new Zone(250, 16, 4, 12), 
		        new Zone(88, 81, 7, 14), new Zone(96, 81, 7, 14), new Zone(104, 81, 7, 14),
		        // Poison (213)
		        new Zone(113, 56, 16, 16), new Zone(129, 72, 16, 16),
		        // Carpet border
		        new Zone(146,71, 16, 8),
		        // Water lily flower
		        new Zone(146, 57, 18, 13), 
		        // Bottles
		        new Zone(165, 57, 8, 11), new Zone(174, 57, 6, 11),
				// Water lily leaf
		        new Zone(72, 153, 49, 32),
		        // Red projectile (flying serpent)
		        new Zone(146, 85, 5, 5), new Zone(152, 85, 5, 5), new Zone(158, 85, 5, 5),
		        new Zone(146, 80, 5, 5), new Zone(152, 80, 5, 5), new Zone(158, 80, 5, 5),
				// Anchors (226)
				new Zone(65, 190, 20,22),new Zone(86, 186, 26, 19),
				new Zone(0, 248, 32, 16), //, new Zone(82, 190, 16, 16),
				// Fall in water animation
				new Zone(238, 35, 24, 16), new Zone(263, 35, 24, 16),
				new Zone(288, 35, 24, 16), new Zone(238,52, 24, 16),
				// Inventor sign (233)
				new Zone(0, 232, 32, 16),
				// Bag (empty and full)
				new Zone(79, 85, 16, 16), new Zone(95, 85, 15, 17),
				// Sewer borders, 
				new Zone(297, 19, 8, 8), new Zone(297, 27, 8, 8), new Zone(305, 27, 8, 8),
				// Sewer smoke
				new Zone(263, 52, 7, 7), new Zone(263, 60, 7, 7),
				new Zone(271, 62, 5, 5), new Zone(274, 52, 6, 5), new Zone(277, 57, 5, 5),
				new Zone(283, 55, 4, 6),
				// Water waves
				new Zone(122, 153, 8, 25), new Zone(132, 153, 8, 25), new Zone(141, 153, 8, 25),
				// Fire ring
				new Zone(110, 85, 12, 10),
			    // Purple/Blue firefly
			    new Zone(197, 59, 6, 6), new Zone(211, 59, 6, 6),
				// Fire ball (for dragon) (245)
			    new Zone(256,0, 41, 42),
			    // feu follet (Will_o_wist)
			    //new Zone(298,2, 11, 13)
				new Zone(298, 16, 8, 10),
				new Zone(307, 19, 5, 6),
				// Bunch of leaves (248)
				new Zone(288, 52, 24, 14),
				new Zone(288, 66, 10, 6),	// Hole under the leaves
				// Middle sword (250)
				new Zone(122, 85, 15, 16),
				// Turret's bullet
				new Zone(152, 98, 5, 5),
				// Lianes (252)
				new Zone(64, 64, 34, 18), new Zone(64, 83, 32, 16), 
				new Zone(64, 113, 25, 25), new Zone(89, 99, 21, 39),
				// Fire animation
				new Zone(272, 0, 13, 11), new Zone(272, 11, 13, 11), new Zone(272, 22, 13, 11),
				new Zone(272, 33, 13, 11), new Zone(272, 44, 13, 11), new Zone(272, 55, 13, 11),
				// Cauldron (262)
				new Zone(32,216, 20, 16), new Zone(32, 232, 20, 16), new Zone(32, 248, 20, 16),
				new Zone(165, 69, 5, 5),	// Bubble
				// Blow from elemental
				new Zone(268, 201, 7, 9), new Zone(268, 211, 4, 13), new Zone(276, 201, 2, 15),
				// Dragon key (269)
				new Zone(138, 85, 16, 16),
				// Portal key
				new Zone(155, 85, 9, 16),
				// Bitey GNAP (271)
				new Zone(196, 18, 16, 22), new Zone(213, 18, 16, 22),
				new Zone(230, 18, 16, 22), new Zone(247, 18, 16, 22), new Zone(264, 18, 16, 22),
				// Wood catapult
				new Zone(24, 233, 11, 12), new Zone(0, 223, 29, 10),
				// Straw on the fork
				new Zone(0, 288, 19, 15),
				// Flying straw (279)
				new Zone(12, 278, 8, 6), new Zone(21, 278, 10, 7),new Zone(32, 278, 14, 7),
				//new Zone(11, 264, 21, 14)
				// Sand projection (282)
				new Zone(0, 292, 6, 11), new ZoneO(11, 285, 7, 16, 0, 0, -2),
				new ZoneO(23, 282, 11, 16, 0, 0, -5), new ZoneO(38, 287, 16, 9, 0, 0, -7),
				new ZoneO(55, 286, 26, 9, 0, 0, -8),
				// Amphora (287)
				new Zone(82, 144, 12, 16),
				// Bag of sand
				new Zone(0, 303, 16, 14),
				// Flower vase
				new Zone(249, 85, 18, 22),
				// Chair (290)
				new Zone(40, 126, 14, 22),
				// Hook of the butcher
				new Zone(1, 88, 5, 6), new Zone(0, 96, 7, 7),
				// Symbol on nature palace slabs
				new Zone(0, 502, 13, 12), new Zone(14, 502, 13,12), new Zone(28, 502, 13, 12),
				new Zone(42, 502, 13, 12), new Zone(56, 502, 13, 12),
				new Zone(0, 515, 13, 12), new Zone(14, 515, 13, 12), new Zone(28, 515, 13, 12),
				new Zone(42, 515, 13, 12), new Zone(56, 515, 13, 12),
				new Zone(282, 108, 19, 12),	// Bunch of leaves on a fork
				
				
		};
		
		pkmChanges = Arrays.asList(new GraphChange[] { new GraphChange("elem", 0, 0), 
				new GraphChange("elem2", 71, 0), 
				new GraphChange("interia7", 115, 0),
				new GraphChange("elem2", 117, 0),
				new GraphChange("objets", 123, 0),
				new GraphChange("palace1", 135, 0),
				new GraphChange("objets", 136, 0),
				new GraphChange("elem", 156, 0),
				new GraphChange("objets", 181, 0),
				new GraphChange("elem", 183, 0),
				new GraphChange("objets", 187, 0),
				new GraphChange("elem", 189, 0),
				new GraphChange("objets", 228, 0),
				new GraphChange("elem", 230, 0),
				new GraphChange("objets", 242, 0),
				new GraphChange("elem", 243, 0),
				new GraphChange("dragonpal", 245, 0),
				new GraphChange("elem", 248, 0),
				new GraphChange("objets", 250, 0),
				new GraphChange("elem", 251, 0),
				new GraphChange("palace1", 252, 0),
				new GraphChange("elem", 262, 0),
				new GraphChange("dragonpal", 266, 0),
				new GraphChange("objets", 269, 0),
				new GraphChange("bitey", 271, 0),
				new GraphChange("exteria10", 276,  0),
				new GraphChange("elem", 278, 0),
				new GraphChange("exteria10", 282, 0),
				new GraphChange("elem", 288, 0),
				new GraphChange("butcher", 291, 0),
				new GraphChange("interia6", 293, 0),
				new GraphChange("elem", 303, 0)
		});
	}
}
