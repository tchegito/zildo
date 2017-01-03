package zeditor.tools.banque;

import java.awt.Point;
import java.util.Arrays;

import zeditor.tools.tiles.Banque;
import zeditor.tools.tiles.GraphChange;

public class Maison extends Banque {

	// 249 tiles
	public Maison() {
		// maison.dec = interia1.pkm + interia2.pkm à partir de la tile 239
		/* Vide */
		coords = new Point[] { new Point(16, 16),
		/* Bords */
		new Point(0, 0), new Point(16, 0), new Point(32, 0), new Point(32, 16),
				new Point(32, 32), new Point(16, 32), new Point(0, 32),
				new Point(0, 16), /* 1--8 */
				new Point(48, 0), new Point(64, 0), new Point(80, 0),
				new Point(80, 16), new Point(80, 32), new Point(64, 32),
				new Point(48, 32), new Point(48, 16), /* 9--15 */

				/* Pr‚sols */
				new Point(0, 48), new Point(16, 48), new Point(32, 48),
				new Point(32, 64), new Point(32, 80), new Point(16, 80),
				new Point(0, 80), new Point(0, 64), /* 16--23 */
				new Point(48, 48), new Point(64, 48), new Point(80, 48),
				new Point(80, 64), new Point(80, 80), new Point(64, 80),
				new Point(48, 80), new Point(48, 64), /* 24--31 */

				/* Sols */
				new Point(16, 64), new Point(64, 64), new Point(64, 16), /*
																		 * 32--34
																		 */
				new Point(96, 48), new Point(112, 48), new Point(128, 48),
				new Point(144, 48), /* 35--38 */
				new Point(96, 64), new Point(112, 64), new Point(144, 64), /*
																			 * 39--
																			 * 41
																			 */
				new Point(160, 0), new Point(176, 0), new Point(192, 0), /*
																		 * 42--44
																		 */

				/* Fenˆtres} {0--3 +41 */
				new Point(32, 96), new Point(48, 96), new Point(32, 112),
				new Point(48, 112), /* 4--9 +41 */
				new Point(0, 128), new Point(16, 128), new Point(0, 144),
				new Point(16, 144), /* 10--13 +41 */
				new Point(0, 96), new Point(16, 96), new Point(0, 112),
				new Point(16, 112), /* 14--17 +41 */
				new Point(32, 128), new Point(48, 128), new Point(32, 144),
				new Point(48, 144), /* 18--23 +41 */

				/* Portes */
				/* Porte1 */
				new Point(288, 128), new Point(304, 128), new Point(288, 144),
				new Point(304, 144), new Point(272, 96), new Point(288, 96),
				new Point(272, 112), new Point(288, 112), new Point(224, 128),
				new Point(240, 128), new Point(224, 144), new Point(240, 144),
				new Point(240, 96), new Point(256, 96), new Point(240, 112),
				new Point(256, 112),
				/* Porte2 */
				new Point(256, 128), new Point(272, 128), new Point(256, 144),
				new Point(272, 144), new Point(224, 160), new Point(240, 160),
				new Point(224, 176), new Point(240, 176), new Point(192, 128),
				new Point(208, 128), new Point(192, 144), new Point(208, 144),
				new Point(192, 160), new Point(208, 160), new Point(192, 176),
				new Point(208, 176),

				/* D‚cors */
				/* Tapis */
				new Point(192, 80), new Point(208, 80), new Point(224, 80),
				new Point(192, 96), new Point(208, 96), new Point(224, 96),/*
																			 * 0--
																			 * 5
																			 * +
																			 * 65
																			 */
				new Point(192, 112), new Point(208, 112), new Point(224, 112), /*
																				 * 6--
																				 * 8
																				 * +
																				 * 65
																				 */
				/* D‚cors bord */
				new Point(96, 0), new Point(112, 0), new Point(128, 0),
				new Point(144, 0), /* 9--12 +65 */
				/* Tables */
				new Point(96, 16), new Point(112, 16), new Point(128, 16),
				new Point(96, 32), new Point(112, 32), new Point(128, 32), /*
																			 * 13--
																			 * 18
																			 * +
																			 * 65
																			 */
				new Point(160, 16), new Point(176, 16), new Point(160, 32),
				new Point(176, 32), /* 19--22 +66 */
				new Point(96, 80), new Point(112, 80), new Point(128, 80), /*
																			 * 23--
																			 * 25
																			 * +
																			 * 65
																			 */
				/* Tabouret */
				new Point(144, 16), new Point(144, 32), /* 26--27 +65 */
				/* BibliothŠque */
				new Point(192, 16), new Point(208, 16), new Point(224, 16),
				new Point(192, 32), new Point(208, 32), new Point(224, 32),/*
																			 * 28--
																			 * 33
																			 * +
																			 * 65
																			 */
				new Point(272, 16), new Point(288, 16), new Point(304, 16),
				new Point(272, 32), new Point(288, 32), new Point(304, 32),
				/* Chaudron */
				new Point(160, 48), new Point(176, 48), new Point(160, 64),
				new Point(176, 64), /* 34--37 +65 */
				/* Feu du forgeron */
				new Point(144, 80), new Point(160, 80), new Point(176, 80),
				new Point(144, 96), new Point(160, 96), new Point(176, 96),
				new Point(160, 112), new Point(144, 128), new Point(160, 128),
				new Point(176, 128),
				/* Gaz */
				new Point(240, 64), new Point(256, 64), new Point(272, 64),
				new Point(240, 80), new Point(256, 80), new Point(272, 80),
				/* Abreuvoir1 */
				new Point(240, 16), new Point(256, 16), new Point(240, 32),
				new Point(256, 32), new Point(240, 48), new Point(256, 48),
				/* Lit */
				new Point(112, 96), new Point(128, 96), new Point(112, 112),
				new Point(128, 112), new Point(112, 128), new Point(128, 128),/*
																			 * 38--
																			 * 43
																			 * +
																			 * 65
																			 */
				/* Outils */
				new Point(112, 144), new Point(128, 144), new Point(144, 144),
				new Point(160, 144), new Point(112, 160), new Point(128, 160),
				new Point(144, 160), new Point(160, 160),
				/* Lampadaire */
				new Point(192, 48), new Point(192, 64), new Point(208, 48),
				new Point(224, 48), new Point(256, 160), new Point(256, 176),
				/* Estrade */
				new Point(64, 96), new Point(80, 96), new Point(96, 96),
				new Point(96, 112), new Point(96, 128), new Point(80, 128),
				new Point(64, 128), new Point(64, 112), /* 44--51 +65 */
				/* Abreuvoir2 */
				new Point(0, 160), new Point(16, 160), new Point(32, 160),
				new Point(0, 176), new Point(16, 176), new Point(32, 176),
				/* Suite feu forgeron */
				new Point(272, 160), new Point(272, 176),
				/* Rideaux */
				//new Point(64, 160), new Point(80, 160), new Point(96, 160),
				/* Stairs */
				new Point(0, 224), new Point(0, 240), new Point(16, 240), new Point(0, 256), new Point(16, 256),
				new Point(16, 224), new Point(32, 224),
				new Point(48, 160),
				/*
				new Point(64, 176), new Point(80, 176), new Point(96, 176),
				new Point(48, 160), new Point(48, 176),
				*/
				/* Bar */
				new Point(176, 144), new Point(176, 160), new Point(176, 176),
				new Point(144, 176), new Point(160, 176), new Point(256, 0),
				new Point(272, 0), new Point(288, 0),
				/* Table voyance */
				new Point(288, 160), new Point(288, 176), new Point(112, 176),
				new Point(128, 176), new Point(304, 96), new Point(304, 112),
				new Point(288, 80), new Point(304, 80), new Point(304, 160),
				new Point(304, 176), new Point(208, 64), new Point(224, 64),
				/* Caisses 224*/
				new Point(288, 48), new Point(304, 48), new Point(288, 64),
				new Point(304, 64),
				/* Escalier 228 */
				new Point(64, 144), new Point(80, 144), new Point(96, 144),
				/* Objets */
				new Point(80, 112), new Point(128, 64), new Point(272, 48),
				new Point(304, 0),
				/* Lanterne */
				new Point(208, 0), new Point(224, 0), new Point(240, 0),
				new Point(128, 64),
				/* Jarre */
				new Point(64, 192), new Point(64, 208),

				/* Angle optus */
				new Point(0, 192), new Point(16,192), new Point(0, 208),
				new Point(16,208), new Point(32, 192), new Point(48, 192),
				new Point(32, 208), new Point(48, 208),
		
				/* Full wall */
				new Point(80, 192), new Point(80, 208),

				/* Fisherman accessories */
				new Point(32, 240), new Point(48, 240), new Point(32, 256), new Point(48, 256),
				/*new Point(48, 224),*/ new Point(64, 224)
			};

		pkmChanges = Arrays.asList(new GraphChange("interia1", 0, 0));
	}
}
