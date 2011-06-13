package zeditor.tools.banque;

import java.awt.Point;
import java.util.Arrays;

import zeditor.tools.tiles.Banque;
import zeditor.tools.tiles.GraphChange;

public class Foret1 extends Banque {

	// foret1 = exteria1 + exteria2 + exteria3 (254 tiles)
	public Foret1() {
		coords = new Point[] {
		/* La colline simple */
		new Point(0, 32), new Point(0, 16), new Point(32, 0), new Point(48, 0),
				new Point(64, 0), new Point(80, 16), new Point(96, 16),
				new Point(16, 16), /* 0--7 */
				new Point(96, 32), new Point(96, 48), new Point(96, 64),
				new Point(96, 80), new Point(48, 112), new Point(32, 112),
				new Point(0, 64), new Point(0, 48), /* 8--15 */
				new Point(16, 64), new Point(16, 48), new Point(16, 32),
				new Point(32, 32), new Point(32, 16), new Point(48, 16),
				new Point(64, 16), new Point(64, 32), /* 16--23 */
				new Point(80, 32), new Point(80, 48), new Point(80, 64),
				new Point(64, 64), new Point(80, 80), new Point(64, 80),
				new Point(64, 96), new Point(48, 96), /* 24--31 */
				new Point(48, 80), new Point(32, 96), new Point(32, 80),
				new Point(32, 64), new Point(16, 80), /* 32--36 */
				/* Angles gauche-droite de la colline */
				new Point(208, 48), new Point(208, 64), new Point(208, 80),
				new Point(224, 32), new Point(224, 48), new Point(240, 32),
				new Point(240, 48), /* 37--43 */
				new Point(256, 48), new Point(256, 64), new Point(256, 80), /*
																			 * 44--
																			 * 46
																			 */
				/* Angles haut de la colline */
				new Point(272, 32), new Point(288, 32), /* 47--48 */

				/* Herbe */
				new Point(112, 64), new Point(128, 64), new Point(144, 64),
				new Point(160, 64), new Point(112, 80), new Point(192, 64), /*
																			 * 0--
																			 * 5
																			 * +
																			 * 49
																			 */
				new Point(176, 64), new Point(128, 80), /* 6--7 +49 */

				/* Tournant du chemin */
				new Point(144, 0), new Point(160, 0), new Point(176, 0),
				new Point(176, 16), new Point(176, 32), new Point(160, 32),
				new Point(144, 32), new Point(144, 16), /* 0--7 +57 */
				/* Angles 1 du chemin */
				new Point(192, 0), new Point(208, 0), new Point(208, 16),
				new Point(192, 16), /* 8--11 +57 */
				/* Angles 2 du chemin */
				new Point(224, 0), new Point(240, 0), new Point(240, 16),
				new Point(224, 16), /* 12--15 +57 */
				/* Chemin */
				new Point(256, 0), new Point(272, 0), new Point(288, 0), /*
																		 * 16--18
																		 * +57
																		 */
				/* Bordure colline */
				new Point(272, 96), new Point(256, 112), new Point(272, 112),
				new Point(288, 112), new Point(256, 128), new Point(288, 128), /*
																				 * 0--
																				 * 5
																				 * +
																				 * 76
																				 */
				new Point(240, 144), new Point(304, 144), /* 6--7 +76 */
				new Point(240, 160), new Point(256, 160), new Point(288, 160),
				new Point(304, 160), new Point(272, 176), new Point(272, 192),/*
																			 * 8--
																			 * 13
																			 * +
																			 * 76
																			 */
				new Point(240, 176), new Point(304, 176), /* 14--15 +76 */
				new Point(224, 64), new Point(240, 64), new Point(224, 80), /*
																			 * 16--
																			 * 18
																			 * +
																			 * 76
																			 */
				new Point(240, 80), new Point(208, 96), new Point(224, 96),
				new Point(240, 96), new Point(256, 96), /* 19--23 +76 */

				/* Colline marron */
				new Point(272, 48), new Point(288, 48), new Point(304, 48),
				new Point(304, 64), /* 0--3 +100 */
				new Point(304, 80), new Point(288, 80), new Point(272, 80),
				new Point(272, 64), /* 4--7 +100 */

				/* Eau */
				new Point(96, 0), new Point(0, 64), new Point(0, 80),
				new Point(160, 0), new Point(160, 16), new Point(160, 32),
				new Point(16, 64), /* 0--6 +108 */
				new Point(48, 80), new Point(96, 32), new Point(32, 80),
				new Point(16, 80), new Point(0, 32), new Point(0, 16),
				new Point(0, 0), /* 7--13 +108 */
				new Point(144, 0), new Point(32, 48), new Point(144, 32),
				new Point(48, 64), new Point(96, 16), new Point(32, 64), /*
																		 * 14--19
																		 * +108
																		 */
				new Point(16, 32), new Point(16, 16), new Point(16, 0), /*
																		 * 20--22
																		 * +108
																		 */
				new Point(0, 48), new Point(80, 48), new Point(96, 48),
				new Point(112, 48), new Point(128, 48), /* 23--27 +108 */
				new Point(16, 48), new Point(64, 48), new Point(48, 48), /*
																		 * 28--30
																		 * +108
																		 */

				/* Arbre */
				new Point(80, 112), new Point(96, 112), new Point(112, 112),
				new Point(128, 112), /* 0--3 +139 */
				new Point(80, 128), new Point(96, 128), new Point(112, 128),
				new Point(128, 128), new Point(80, 144), new Point(96, 144),
				new Point(112, 144), new Point(128, 144), new Point(80, 160),
				new Point(96, 160), new Point(112, 160), new Point(128, 160),
				new Point(80, 176), new Point(96, 176), new Point(112, 176),
				new Point(128, 176), /* 16--19 +139 */

				/* Souche */
				new Point(144, 112), new Point(160, 112), new Point(144, 128),
				new Point(160, 128),/* 20--23 +139 */
				/* Fleurs anim‚s */
				new Point(128, 48), new Point(192, 128), /* 24--25 +139 */

				/* D‚cors */
				/******* Buisson */
				new Point(144, 80), new Point(192, 96), /* 0--1 +165 */
				new Point(160, 80), new Point(176, 112), new Point(192, 112), /*
																			 * 2--
																			 * 4
																			 * +
																			 * 165
																			 */
				new Point(160, 96), new Point(176, 96), new Point(176, 80),
				new Point(144, 96), new Point(192, 80), new Point(144, 144),
				new Point(160, 144), new Point(144, 160), new Point(160, 160), /*
																				 * 5--
																				 * 8
																				 * +
																				 * 165
																				 */

				/* Enclos */
				new Point(96, 0), new Point(112, 0), new Point(128, 0),
				new Point(128, 16), new Point(112, 16), new Point(112, 32),
				new Point(112, 48), /* 9--15 +165 */

				/* Statue */
				new Point(0, 0), new Point(16, 0), new Point(0, 16),
				new Point(16, 16), new Point(0, 32), new Point(16, 32), /*
																		 * 0--5
																		 * +181
																		 */
				new Point(32, 0), new Point(48, 0), new Point(32, 16),
				new Point(48, 16), /* Entr‚e de grotte {6--9 +181 */

				/* Grosse pierre */
				new Point(128, 64), new Point(144, 64), new Point(128, 80),
				new Point(144, 80), /* 10--13 +181 */

				/* Entr‚e dans une forˆt */
				new Point(48, 112), new Point(64, 112), new Point(80, 112),
				new Point(48, 128), new Point(64, 128), new Point(80, 128), /*
																			 * 14--
																			 * 19
																			 * +
																			 * 181
																			 */

				/* Echelle */
				new Point(160, 112), new Point(176, 112), /* 20--21 +181 */

				/* Eau anim‚e} {0--22 +209 */
				new Point(112, 0), new Point(0, 96), new Point(0, 112),
				new Point(192, 0), new Point(192, 16), new Point(192, 32),
				new Point(16, 96), new Point(48, 112), new Point(112, 32),
				new Point(32, 112), new Point(16, 112), new Point(32, 32),
				new Point(32, 16), new Point(32, 0), new Point(176, 0),
				new Point(32, 48), new Point(176, 32), new Point(48, 96),
				new Point(112, 16), new Point(32, 96), new Point(48, 32),
				new Point(48, 16), new Point(48, 0),
				/* 0--22 +231 */
				new Point(128, 0), new Point(0, 128), new Point(0, 144),
				new Point(224, 0), new Point(224, 16), new Point(224, 32),
				new Point(16, 128), new Point(48, 144), new Point(128, 32),
				new Point(32, 144), new Point(16, 144), new Point(64, 32),
				new Point(64, 16), new Point(64, 0), new Point(208, 0),
				new Point(32, 48), new Point(208, 32), new Point(48, 128),
				new Point(128, 16), new Point(32, 128), new Point(80, 32),
				new Point(16, 16), new Point(80, 0) };

		pkmChanges = Arrays.asList(new GraphChange[] { new GraphChange("exteria1", 0, 0), 
				new GraphChange("exteria2", 109, 192),
				new GraphChange("exteria1", 139, -192), new GraphChange("exteria3", 186, 192 + 176),
				new GraphChange("exteria2", 209, -176) });
	}
}
