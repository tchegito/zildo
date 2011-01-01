package zeditor.tools.banque;

import java.awt.Point;
import java.util.Arrays;

import zeditor.tools.tiles.Banque;
import zeditor.tools.tiles.GraphChange;

// Foret2 = exteria5 + exteria6 (253 tiles)
public class Foret2 extends Banque {

	public Foret2() {
		coords = new Point[] {
		/* Décors3 */
		new Point(0, 0), new Point(16, 0), new Point(0, 16), new Point(16, 16),
				new Point(0, 32), new Point(16, 32), new Point(0, 48),
				new Point(16, 48), new Point(32, 0), new Point(48, 0),
				new Point(32, 16), new Point(48, 16), new Point(16, 64),
				new Point(16, 80), new Point(96, 80), new Point(112, 80),
				new Point(96, 96), new Point(112, 96),
				/* Arbre rouge - 19 */
				new Point(32, 32), new Point(48, 32), new Point(64, 32),
				new Point(80, 32), new Point(32, 48), new Point(48, 48),
				new Point(64, 48), new Point(80, 48), new Point(32, 64),
				new Point(48, 64), new Point(64, 64), new Point(80, 64),
				new Point(32, 80), new Point(48, 80), new Point(64, 80),
				new Point(80, 80),
				/* Arbre jaune EXTERIA6.PKM - 34 */
				new Point(0, 0), new Point(16, 0), new Point(32, 0),
				new Point(48, 0), new Point(0, 16), new Point(16, 16),
				new Point(32, 16), new Point(48, 16), new Point(0, 32),
				new Point(16, 32), new Point(32, 32), new Point(48, 32),
				new Point(0, 48), new Point(16, 48), new Point(32, 48),
				new Point(48, 48),
				/* Maison de sorciŠre - 50 */
				new Point(64, 0), new Point(80, 0), new Point(96, 0),
				new Point(112, 0), new Point(64, 16), new Point(80, 16),
				new Point(96, 16), new Point(112, 16), new Point(64, 32),
				new Point(80, 32), new Point(96, 32), new Point(112, 32),
				new Point(64, 48), new Point(80, 48), new Point(96, 48),
				new Point(112, 48), new Point(64, 64), new Point(112, 64),
				new Point(64, 80), new Point(80, 80), new Point(96, 80),
				new Point(112, 80), new Point(80, 96), new Point(96, 96),

				/* Cimetierre EXTERIA5.PKM - 74 */
				new Point(0, 64), new Point(64, 0), new Point(64, 16),
				new Point(80, 0), new Point(96, 0), new Point(80, 16),
				new Point(96, 16), new Point(48, 128), new Point(64, 128),
				new Point(48, 144), new Point(64, 144), new Point(32, 128),
				/* Eglise exterieure - 62 */
				new Point(112, 0), new Point(128, 0), new Point(144, 0),
				new Point(160, 0), new Point(176, 0), new Point(192, 0),
				new Point(208, 0), new Point(224, 0), new Point(112, 16),
				new Point(128, 16), new Point(144, 16), new Point(160, 16),
				new Point(176, 16), new Point(192, 16), new Point(208, 16),
				new Point(224, 16), new Point(112, 32), new Point(128, 32),
				new Point(144, 32), new Point(160, 32), new Point(176, 32),
				new Point(192, 32), new Point(208, 32), new Point(224, 32),
				new Point(112, 48), new Point(128, 48), new Point(144, 48),
				new Point(160, 48), new Point(176, 48), new Point(192, 48),
				new Point(208, 48), new Point(224, 48), new Point(112, 64),
				new Point(128, 64), new Point(144, 64), new Point(160, 64),
				new Point(176, 64), new Point(208, 64), new Point(224, 64),
				new Point(240, 0), new Point(256, 0), new Point(32, 160),
				new Point(48, 160), new Point(64, 160), new Point(80, 160),
				new Point(32, 176), new Point(48, 176), new Point(64, 176),
				new Point(80, 176),
				/* Exterieur du palais - 96 */
				new Point(272, 0), new Point(288, 0), new Point(304, 0),
				new Point(240, 16), new Point(256, 16), new Point(304, 16), /*
																			 * 1er
																			 * bloc
																			 */
				new Point(240, 32), new Point(256, 32), new Point(272, 32),
				new Point(288, 32), new Point(304, 32), new Point(256, 48),
				new Point(272, 48), new Point(288, 48), new Point(304, 48),
				new Point(256, 64), new Point(272, 64), new Point(288, 64),
				new Point(240, 80), new Point(256, 80), new Point(272, 80),
				new Point(288, 80), new Point(304, 80), new Point(240, 96),
				new Point(256, 96), new Point(272, 96), new Point(288, 96),
				new Point(304, 96), new Point(240, 112), new Point(256, 112),
				new Point(304, 112), new Point(128, 80), new Point(144, 80),
				new Point(160, 80), new Point(176, 80), new Point(192, 80), /*
																			 * 2e
																			 * bloc
																			 */
				new Point(128, 96), new Point(144, 96), new Point(160, 96),
				new Point(176, 96), new Point(192, 96), new Point(128, 112),
				new Point(144, 112), new Point(160, 112), new Point(176, 112),
				new Point(192, 112), new Point(208, 80), new Point(224, 80),
				new Point(208, 96), new Point(224, 96), new Point(208, 112),
				new Point(224, 112), new Point(224, 128), new Point(240, 128),
				new Point(224, 144), new Point(240, 144), new Point(224, 160),
				new Point(240, 160), new Point(128, 128), new Point(144, 128),
				new Point(160, 128), new Point(128, 144), new Point(144, 144),
				new Point(160, 144), new Point(128, 160), new Point(160, 160),
				new Point(176, 128), new Point(192, 128), new Point(176, 144),
				new Point(192, 144), new Point(176, 160), new Point(192, 160),
				new Point(176, 176), new Point(192, 176), new Point(256, 128),
				new Point(272, 128), new Point(256, 144), new Point(272, 144),
				new Point(288, 144), new Point(256, 160), new Point(272, 160),
				new Point(288, 160), new Point(0, 96), new Point(16, 96),
				new Point(32, 96), new Point(48, 96), new Point(64, 96), /* Porte */
				new Point(0, 112), new Point(16, 112), new Point(32, 112),
				new Point(48, 112), new Point(64, 112), new Point(16, 128),
				new Point(0, 144), new Point(16, 144), new Point(32, 144),
				new Point(16, 160), /* Croix */
				new Point(208, 144), new Point(208, 160), /* Porte ferm‚e */

				/* Pont en bois */
				new Point(208, 176), new Point(224, 176), new Point(240, 176),
				new Point(256, 176), new Point(272, 176), new Point(288, 176),
				new Point(304, 176), new Point(304, 160), new Point(96, 176),
				new Point(112, 176), new Point(128, 176), new Point(144, 176),
				new Point(160, 176), new Point(144, 160),

				/* Cave secrête ! */
				new Point(96, 128), new Point(96, 144), new Point(112, 144),
				new Point(96, 160), new Point(112, 160) };

		pkmChanges = Arrays.asList(new GraphChange[] { new GraphChange("exteria5", 0, 0),
				new GraphChange("exteria6", 34, 192),
				new GraphChange("exteria5", 75, -192) });
	}
}
