package zeditor.tools.banque;

import java.awt.Point;
import java.util.Arrays;

import zeditor.tools.tiles.Banque;
import zeditor.tools.tiles.GraphChange;

public class Palais1 extends Banque {

	// Palais1 = interia4 (179 tiles)
	public Palais1() {
		coords = new Point[] {
		/* Murs étage 2 */
		new Point(0, 0), new Point(16, 0), new Point(64, 0), new Point(0, 16),
				new Point(16, 16), new Point(32, 16), new Point(48, 16),
				new Point(64, 16), new Point(16, 32), new Point(32, 32),
				new Point(48, 32), new Point(16, 48), new Point(32, 48),
				new Point(48, 48), new Point(0, 64), new Point(16, 64),
				new Point(64, 64),

				/* Coins étage 2 */
				new Point(160, 32), new Point(176, 32), new Point(160, 48),
				new Point(176, 48),

				/* Tour étage 2 */
				new Point(64, 112), new Point(80, 112), new Point(96, 112),
				new Point(64, 128), new Point(96, 128), new Point(64, 144),
				new Point(80, 144), new Point(96, 144),

				/* Virage étage2 */
				new Point(112, 112), new Point(128, 112), new Point(112, 128),
				new Point(128, 128),

				/* Murs étage 1 */
				new Point(80, 0), new Point(96, 0), new Point(144, 0),
				new Point(80, 16), new Point(96, 16), new Point(112, 16),
				new Point(128, 16), new Point(144, 16), new Point(96, 32),
				new Point(112, 32), new Point(128, 32), new Point(96, 48),
				new Point(112, 48), new Point(128, 48), new Point(80, 64),
				new Point(96, 64), new Point(144, 64),

				/* Coins étage 1 - 2 */
				new Point(192, 32), new Point(208, 32), new Point(192, 48),
				new Point(208, 48), new Point(256, 32), new Point(272, 32),
				new Point(256, 48), new Point(272, 48),

				/* Virages */
				new Point(0, 112), new Point(16, 112), new Point(32, 112),
				new Point(48, 112), new Point(0, 128), new Point(16, 128),
				new Point(32, 128), new Point(48, 128), new Point(0, 144),
				new Point(16, 144), new Point(32, 144), new Point(48, 144),
				new Point(0, 160), new Point(16, 160), new Point(32, 160),
				new Point(48, 160),
				/* Virages en haut */
				new Point(144, 112), new Point(160, 112), new Point(144, 128),
				new Point(160, 128),

				/* Coins */
				new Point(256, 0), new Point(272, 0), new Point(256, 16),
				new Point(272, 16),

				/* Estrade */
				new Point(112, 144), new Point(128, 144), new Point(144, 144),
				new Point(112, 160), new Point(144, 160), new Point(112, 176),
				new Point(128, 176), new Point(144, 176), new Point(0, 176),
				new Point(16, 176), new Point(32, 176),

				/* Sols */
				new Point(64, 96), new Point(80, 96), new Point(128, 160),
				new Point(96, 96),

				/* Fenêtres */
				new Point(128, 80), new Point(144, 80), new Point(128, 96),
				new Point(144, 96), new Point(176, 112), new Point(192, 112),
				new Point(176, 128), new Point(192, 128),
				/* Boucliers */
				new Point(224, 0), new Point(240, 0), new Point(224, 16),
				new Point(240, 16), new Point(224, 32), new Point(240, 32),
				new Point(224, 48), new Point(240, 48),
				/* Portes */
				new Point(0, 80), new Point(16, 80), new Point(0, 96),
				new Point(16, 96), new Point(32, 80), new Point(48, 80),
				new Point(32, 96), new Point(48, 96), new Point(160, 64),
				new Point(176, 64), new Point(160, 80), new Point(176, 80),
				new Point(192, 64), new Point(208, 64), new Point(192, 80),
				new Point(208, 80),

				/* Escaliers */
				new Point(160, 0), new Point(176, 0), new Point(160, 16),
				new Point(176, 16), new Point(192, 0), new Point(208, 0),
				new Point(192, 16), new Point(208, 16),

				/* Piliers */
				new Point(80, 160), new Point(80, 176), new Point(96, 160),
				new Point(96, 176), new Point(288, 32), new Point(304, 32),
				new Point(288, 96), new Point(304, 96),
				/* Rideaux */
				new Point(288, 48), new Point(304, 48), new Point(288, 112),
				new Point(304, 112),
				/* Tasse */
				new Point(288, 64), new Point(304, 64), new Point(288, 80),
				new Point(304, 80),
				/* Porte à escaliers */
				new Point(224, 64), new Point(240, 64), new Point(224, 80),
				new Point(240, 80), new Point(256, 64), new Point(272, 64),
				new Point(256, 80), new Point(272, 80), new Point(224, 96),
				new Point(240, 96), new Point(224, 112), new Point(240, 112),
				new Point(256, 96), new Point(272, 96), new Point(256, 112),
				new Point(272, 112),

				/* Fauteuil */
				new Point(160, 144), new Point(176, 144), new Point(160, 160),
				new Point(176, 160), new Point(192, 144), new Point(208, 144),
				new Point(224, 144), new Point(192, 160), new Point(208, 160),
				new Point(224, 160) };

		pkmChanges = Arrays.asList(new GraphChange[] {new GraphChange("interia4", 0, 0)});

	}
}
