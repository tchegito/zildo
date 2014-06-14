package zeditor.tools.banque;

import java.awt.Point;
import java.util.Arrays;

import zeditor.tools.tiles.Banque;
import zeditor.tools.tiles.GraphChange;

// Foret4 = exteria8 (194 tiles)
public class Foret4 extends Banque {

	public Foret4() {
		coords = new Point[] {
		/* Ruines */

		/* Bordures de collines */
		new Point(0, 0), new Point(16, 0), new Point(32, 0), new Point(48, 0),
				new Point(0, 16), new Point(16, 16), new Point(32, 16),
				new Point(48, 16), new Point(0, 32), new Point(32, 32),
				new Point(48, 32), new Point(0, 48), new Point(16, 48),
				new Point(32, 48), new Point(48, 48),
				/* Sols */
				new Point(32, 64), new Point(48, 64), new Point(64, 64),
				new Point(32, 80), new Point(64, 48),
				/* Grand pilier */
				new Point(96, 0), new Point(112, 0), new Point(144, 0),
				new Point(192, 0), new Point(96, 16), new Point(112, 16),
				new Point(192, 16), new Point(128, 16), new Point(144, 16),
				new Point(96, 32), new Point(112, 32), new Point(128, 32),
				new Point(144, 32), new Point(96, 48), new Point(112, 48),
				new Point(128, 48), new Point(144, 48),
				/* Statues */
				new Point(0, 64), new Point(16, 64), new Point(0, 80),
				new Point(16, 80), new Point(160, 0), new Point(176, 0),
				new Point(160, 16), new Point(176, 16), new Point(160, 32),
				new Point(176, 32),
				/* Maison jaune */
				new Point(208, 0), new Point(224, 0), new Point(272, 0),
				new Point(208, 16), new Point(224, 16), new Point(240, 16),
				new Point(256, 16), new Point(272, 16), new Point(208, 32),
				new Point(224, 32), new Point(272, 32), new Point(208, 48),
				new Point(224, 48),
				/* Déco de palais */
				new Point(64, 0), new Point(64, 16), new Point(64, 32),
				new Point(80, 0), new Point(80, 16), new Point(80, 32),
				new Point(192, 32), new Point(0, 96), new Point(16, 96),
				new Point(0, 112), new Point(16, 112), new Point(32, 112),
				new Point(48, 112),

				/* Forˆt enchant‚e */

				/* Sols bosquet */
				new Point(240, 80), new Point(240, 96), new Point(240, 112),
				/* Bordures forˆt */
				new Point(224, 64), new Point(240, 64), new Point(256, 64),
				new Point(224, 80), new Point(256, 80), new Point(208, 96),
				new Point(272, 96), new Point(208, 112), new Point(224, 112),
				new Point(256, 112), new Point(272, 112), new Point(224, 128),
				new Point(240, 128), new Point(256, 128),
				/* Bas du grand arbre */
				new Point(96, 64), new Point(112, 64), new Point(128, 64),
				new Point(144, 64), new Point(96, 80), new Point(112, 80),
				new Point(128, 80), new Point(144, 80),
				/* Petits arbres */
				new Point(288, 80), new Point(304, 80), new Point(288, 96),
				new Point(304, 96), new Point(272, 48), new Point(288, 48),
				new Point(272, 64), new Point(288, 64),
				/* Tronc d'arbre tunnel */
				new Point(160, 48), new Point(176, 48), new Point(192, 48),
				new Point(160, 64), new Point(176, 64), new Point(192, 64),
				new Point(160, 80), new Point(176, 80), new Point(192, 80),
				new Point(160, 96), new Point(176, 96), new Point(192, 96),
				new Point(160, 112), new Point(176, 112), new Point(192, 112),
				new Point(160, 128), new Point(176, 128), new Point(192, 128),
				new Point(96, 96), new Point(112, 96),
				/* Souche creuse */
				new Point(128, 96), new Point(144, 96), new Point(128, 112),
				new Point(144, 112), new Point(128, 128), new Point(144, 128),

				/* Clairière */

				/* Feuillage */
				new Point(0, 128), new Point(16, 128), new Point(32, 128),
				new Point(48, 128), new Point(0, 144), new Point(16, 144),
				new Point(32, 144), new Point(48, 144), new Point(0, 160),
				new Point(16, 160), new Point(32, 160), new Point(48, 160),
				new Point(0, 176), new Point(16, 176), new Point(32, 176),
				new Point(64, 128), new Point(80, 128),
				new Point(96, 128), new Point(112, 128), new Point(64, 144),
				new Point(80, 144), new Point(96, 144), new Point(112, 144),
				new Point(64, 160), new Point(80, 160), new Point(96, 160),
				new Point(112, 160), new Point(80, 176),
				new Point(96, 176), new Point(112, 176),
				/* Ombres */
				new Point(144, 144), new Point(160, 144), new Point(128, 160),
				new Point(144, 160), new Point(176, 160),
				new Point(144, 176), new Point(160, 176),
				/* Estrade de pierre */
				new Point(224, 144), new Point(240, 144), new Point(256, 144),
				new Point(208, 144), new Point(208, 160), new Point(224, 160),
				new Point(240, 160), new Point(256, 160), new Point(192, 144),
				new Point(192, 160), new Point(176, 176), new Point(192, 176),
				new Point(208, 176), new Point(224, 176), new Point(240, 176),
				new Point(256, 176), new Point(272, 176), new Point(288, 176),
				/* Piédestal */
				new Point(272, 144), new Point(288, 144), new Point(272, 160),
				new Point(288, 160),
				/* Close trees */
				new Point(304, 0), new Point(304, 16), new Point(304, 32),
				new Point(304, 48), new Point(304, 64),
		
				/* Buche creuse */
				new Point(208, 112),
				/* Wood stairs */
				new Point(272, 128),
				/* Hole in the ground */
				new Point(304, 112),
				/* Ill tree */
				new Point(0, 304), new Point(16, 304), new Point(32, 304), new Point(48, 304),
				new Point(0, 320), new Point(16, 320), new Point(32, 320), new Point(48, 320),
				new Point(0, 336), new Point(16, 336), new Point(32, 336), new Point(48, 336),
				new Point(0, 352), new Point(16, 352), new Point(32, 352), new Point(48, 352),
				
		
		};

		pkmChanges = Arrays.asList(new GraphChange[] {new GraphChange("exteria8", 0, 0)});
	}
}
