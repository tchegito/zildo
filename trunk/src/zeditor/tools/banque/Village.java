package zeditor.tools.banque;

import java.awt.Point;
import java.util.Arrays;

public class Village extends Banque {

	// village=exteria3 + exteria4 + exteria6 (244 tiles)
	public Village() {
		coords = new Point[] {
		/* Maison */
		new Point(160, 32), new Point(176, 32), new Point(192, 32),
				new Point(208, 32), new Point(224, 32), new Point(192, 80), /*
																			 * 0--
																			 * 5
																			 */
				new Point(160, 48), new Point(176, 48), new Point(192, 48),
				new Point(208, 48), new Point(224, 48), new Point(208, 80), /*
																			 * 6--
																			 * 11
																			 */
				new Point(160, 64), new Point(176, 64), new Point(192, 64),
				new Point(208, 64), new Point(224, 64), /* 12--16 */
				new Point(160, 80), new Point(176, 80), new Point(224, 80), /*
																			 * 17--
																			 * 19
																			 */
				new Point(224, 16), new Point(240, 16), new Point(192, 96),
				new Point(208, 96), new Point(240, 96), new Point(256, 96), /*
																			 * 20--
																			 * 25
																			 */
				new Point(0, 128), new Point(16, 128), new Point(160, 96),
				new Point(176, 96), /* 26--29 */
				new Point(256, 32), new Point(272, 32), new Point(256, 48),
				new Point(272, 48), /* 30--33 */
				new Point(240, 32), new Point(240, 48), new Point(192, 112),
				new Point(208, 112), /* 34--37 */

				/* Maison de sorciŠre */
				new Point(160, 128), new Point(176, 128), new Point(192, 128),
				new Point(208, 128), new Point(224, 128), new Point(240, 128), /*
																				 * 0--
																				 * 5
																				 * +
																				 * 37
																				 */
				new Point(160, 144), new Point(176, 144), new Point(192, 144),
				new Point(208, 144), new Point(224, 144), new Point(240, 144), /*
																				 * 6--
																				 * 11
																				 * +
																				 * 37
																				 */
				new Point(160, 160), new Point(176, 160), new Point(192, 160),
				new Point(208, 160), new Point(224, 160), new Point(240, 160), /*
																				 * 12--
																				 * 17
																				 * +
																				 * 37
																				 */
				new Point(160, 176), new Point(176, 176), new Point(192, 176),
				new Point(208, 176), new Point(240, 176), /* 18--22 +37 */
				/* Statuette de la maison de sorciŠre */
				new Point(256, 128), new Point(256, 144), /* 23--24 +37 */

				/* Haies */
				new Point(0, 48), new Point(16, 48), new Point(32, 48),
				new Point(0, 64), new Point(16, 64), new Point(32, 64), /*
																		 * 0--5
																		 * +62
																		 */
				new Point(0, 80), new Point(16, 80), new Point(0, 96),
				new Point(16, 96), new Point(0, 112), new Point(16, 112), /*
																		 * 6--11
																		 * +62
																		 */
				new Point(32, 80), new Point(32, 96), /* 12--13 +62 */

				/* Routes pav‚es */
				new Point(32, 32), new Point(48, 32), new Point(64, 32),
				new Point(80, 32), new Point(96, 32), new Point(48, 48), /*
																		 * 0--5
																		 * +76
																		 */
				new Point(64, 48), new Point(80, 48), new Point(96, 48), /*
																		 * 6--8
																		 * +76
																		 */
				new Point(80, 96), new Point(96, 96), new Point(288, 32),
				new Point(304, 32), /* 9--12 +76 */

				/* Maison en bois */
				new Point(256, 64), new Point(272, 64), new Point(288, 64),
				new Point(256, 80), new Point(272, 80), new Point(288, 80), /*
																			 * 0--
																			 * 5
																			 * +
																			 * 89
																			 */
				new Point(272, 96), new Point(288, 96), new Point(304, 96), /*
																			 * 6--
																			 * 8
																			 * +
																			 * 89
																			 */
				/* Chemin‚e */
				new Point(272, 112), new Point(288, 112), new Point(272, 128),
				new Point(288, 128), /* 9--12 +89 */
				/* Armes murs_rouge */
				new Point(224, 112), new Point(240, 112), /* 13--14 +89 */

				/* Abreuvoir - pancarte - trou noir */
				new Point(112, 32), new Point(128, 32), new Point(112, 48),
				new Point(128, 48), new Point(80, 16), /* 0--4 +104 */
				new Point(96, 112), new Point(112, 112), new Point(96, 128),
				new Point(112, 128), /* 5--8 +104 */
				/* Marais */
				new Point(128, 128), new Point(144, 128), new Point(128, 144),
				new Point(144, 144), new Point(208, 16), new Point(288, 48), /*
																			 * 9--
																			 * 14
																			 * +
																			 * 104
																			 */
				/* Entr‚e du village (5x3 - 4 motifs) */
				new Point(48, 64), new Point(64, 64), new Point(80, 64),
				new Point(96, 64), new Point(112, 64), /* 15--19 +104 */
				new Point(48, 80), new Point(64, 80), new Point(80, 80),
				new Point(96, 80), /* 20--23 +104 */
				new Point(48, 96), new Point(64, 96), /* 24--25 +104 */
				/* Arbuste rose */
				new Point(288, 160), new Point(304, 160), new Point(288, 176),
				new Point(304, 176), /* 26--29 +104 */
				/* Tas de pierre */
				new Point(256, 160), new Point(272, 160), new Point(256, 176),
				new Point(272, 176), /* 30--33 +104 */
				/* Tapis */
				new Point(128, 160), new Point(144, 160), new Point(128, 176),
				new Point(144, 176), /* 34--37 +104 */
				new Point(112, 144), new Point(112, 160), new Point(112, 176), /*
																				 * 38--
																				 * 40
																				 * +
																				 * 104
																				 */
				new Point(80, 176), new Point(96, 176), /* 41--42 +104 */
				/* Bidon - jarre - boite aux lettres */
				new Point(112, 80), new Point(112, 96), new Point(32, 128),
				new Point(304, 48), new Point(304, 64), /* 43--47 +104 */

				/* Enseignes */
				new Point(272, 144), new Point(288, 144), new Point(0, 176),
				new Point(16, 176),

				/* Maison verte */
				new Point(32, 144), new Point(48, 144), new Point(64, 144),
				new Point(32, 160), new Point(48, 160), new Point(64, 160),
				new Point(32, 176), new Point(48, 176), new Point(64, 176),
				new Point(80, 144), new Point(96, 144), new Point(80, 160),
				new Point(96, 160),

				/* Pont */
				new Point(64, 0), new Point(64, 16), new Point(160, 0),
				new Point(80, 0), new Point(96, 0), new Point(112, 0),
				new Point(112, 16),/* 0--6 */
				new Point(128, 0), new Point(144, 0), new Point(128, 16), /*
																		 * 7--9
																		 * +179
																		 */

				/* Pont2 */
				new Point(208, 0), new Point(224, 0), new Point(240, 0),
				new Point(256, 0), new Point(272, 0), new Point(288, 0),
				new Point(304, 0), /* 0--6 */
				new Point(256, 16), new Point(272, 16), new Point(288, 16),
				new Point(304, 16), /* 7--10 */

				/* Maison d'autres couleurs */
				new Point(160, 32), new Point(176, 32), new Point(192, 32),
				new Point(208, 32), new Point(224, 32), new Point(192, 80), /*
																			 * 0--
																			 * 5
																			 */
				new Point(160, 48), new Point(176, 48), new Point(192, 48),
				new Point(208, 48), new Point(224, 48), new Point(208, 80), /*
																			 * 6--
																			 * 11
																			 */
				new Point(160, 64), new Point(176, 64), new Point(192, 64),
				new Point(208, 64), new Point(224, 64), /* 12--16 */
				new Point(160, 80), new Point(176, 80), new Point(224, 80), /*
																			 * 17--
																			 * 19
																			 */
				new Point(224, 16), new Point(240, 16),

				new Point(80, 32), new Point(96, 32), new Point(112, 32),
				new Point(128, 32), new Point(144, 32), new Point(112, 80), /*
																			 * 0--
																			 * 5
																			 */
				new Point(80, 48), new Point(96, 48), new Point(112, 48),
				new Point(128, 48), new Point(144, 48), new Point(128, 80), /*
																			 * 6--
																			 * 11
																			 */
				new Point(80, 64), new Point(96, 64), new Point(112, 64),
				new Point(128, 64), new Point(144, 64), /* 12--16 */
				new Point(80, 80), new Point(96, 80), new Point(144, 80), /*
																		 * 17--19
																		 */
				new Point(144, 16), new Point(160, 16),

				/* Animations d'eau */
				new Point(128, 0), new Point(128, 16), new Point(128, 32),
				new Point(144, 0), new Point(144, 16), new Point(144, 32),
				new Point(160, 0), new Point(160, 16), new Point(160, 32) };

		pkmChanges = Arrays.asList(new Point(192, 192), new Point(236, 96));
	}

}
