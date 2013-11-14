package zeditor.tools.banque;

import java.awt.Point;
import java.util.Arrays;

import zeditor.tools.tiles.Banque;
import zeditor.tools.tiles.GraphChange;

// Foret3 = exteria6 + exteria7 + exteria3 (207 tiles)
public class Foret3 extends Banque {

	public Foret3() {
		coords = new Point[] {
		/* Plage - 0 */
		new Point(176, 0), new Point(192, 0), new Point(208, 0),
				new Point(176, 16), new Point(192, 16), new Point(208, 16),
				new Point(176, 32), new Point(192, 32), new Point(208, 32),
				new Point(176, 48), new Point(192, 48), new Point(208, 48),
				new Point(224, 16), new Point(240, 16), new Point(256, 16),
				new Point(272, 16), new Point(224, 32), new Point(240, 32),
				new Point(256, 32), new Point(272, 32), new Point(224, 0),
				/* Plage suite - 21 */
				new Point(288, 0), new Point(304, 0), new Point(288, 16),
				new Point(304, 16),
				/* Marais - 25 */
				new Point(48, 112), new Point(64, 112), new Point(80, 112),
				new Point(48, 128), new Point(80, 128), new Point(48, 144),
				new Point(64, 144), new Point(80, 144), new Point(96, 112),
				new Point(112, 112), new Point(128, 112), new Point(96, 128),
				new Point(128, 128), new Point(96, 144), new Point(112, 144),
				new Point(128, 144), new Point(176, 64), new Point(192, 64),
				new Point(176, 80), new Point(192, 80),
				/* Rocher - 45 */
				new Point(0, 64), new Point(16, 64), new Point(32, 64),
				new Point(0, 80), new Point(16, 80), new Point(32, 80),
				new Point(0, 96), new Point(16, 96), new Point(32, 96),
				new Point(0, 112), new Point(16, 112), new Point(32, 112),
				new Point(0, 128), new Point(16, 128),
				/* D‚sert - 59 */
				new Point(256, 112), new Point(272, 96), new Point(288, 96),
				new Point(304, 96), new Point(272, 112), new Point(256, 96),
				new Point(208, 64), new Point(224, 64), new Point(240, 64),
				new Point(240, 80), new Point(240, 96), new Point(224, 96),
				new Point(208, 96), new Point(208, 80), new Point(256, 64),
				new Point(272, 64), new Point(272, 80), new Point(256, 80),
				new Point(288, 64), new Point(304, 64), new Point(304, 80),
				new Point(288, 80), new Point(224, 80),
				/* Montagnes - 91 */
				new Point(176, 96), new Point(128, 48), new Point(192, 96),
				new Point(144, 48), new Point(160, 48), new Point(128, 64),
				new Point(144, 64), new Point(160, 64), new Point(144, 96),
				new Point(160, 96), new Point(144, 112), new Point(160, 112),
				new Point(128, 96), new Point(112, 96),
				/* Animations eau - 80 */
				new Point(128, 80), new Point(224, 48), new Point(272, 48),
				new Point(144, 80), new Point(240, 48), new Point(288, 48),
				new Point(160, 80), new Point(256, 48), new Point(304, 48),

				/* Vraies montagnes - 106 */
				new Point(0, 16), new Point(80, 16), new Point(0, 32),
				new Point(16, 32), new Point(80, 32), new Point(0, 48),
				new Point(32, 48), new Point(80, 48), new Point(0, 64),
				new Point(32, 64), new Point(64, 64), new Point(32, 80),
				new Point(0, 96), new Point(80, 96), new Point(32, 112),

				new Point(80, 0), new Point(96, 0), new Point(112, 0),
				new Point(144, 0), new Point(160, 0), new Point(96, 16),
				new Point(112, 16), new Point(144, 16), new Point(160, 16),
				new Point(112, 32), new Point(128, 32), new Point(144, 32),
				new Point(96, 48), new Point(112, 48), new Point(128, 48),
				new Point(144, 48), new Point(160, 48), new Point(176, 48),
				new Point(96, 64), new Point(112, 64), new Point(160, 64),
				new Point(112, 80), new Point(128, 0), new Point(128, 16),
				new Point(96, 96), new Point(112, 96), new Point(128, 96),
				new Point(144, 96), new Point(96, 112), new Point(112, 112),
				new Point(128, 112), new Point(144, 112), new Point(32, 0),
				new Point(48, 0), new Point(32, 16), new Point(48, 16),
				new Point(160, 80), new Point(176, 80), new Point(160, 96),
				new Point(176, 96), new Point(208, 0), new Point(224, 0),
				new Point(240, 0), new Point(208, 16),
				/* Rocher */
				new Point(176, 0), new Point(192, 0), new Point(176, 16),
				new Point(192, 16),
				/* Entr‚e de grotte */
				new Point(192, 32), new Point(208, 32), new Point(192, 48),
				new Point(208, 48),
				/* 2e Entr‚e de grotte */
				new Point(0, 128), new Point(16, 128), new Point(0, 144),
				new Point(16, 144), new Point(0, 160), new Point(16, 160),
				/* Echelle */
				new Point(224, 16), new Point(240, 16),
				/* Chemin */
				new Point(80, 160), new Point(96, 160), new Point(112, 160),
				new Point(128, 160), new Point(32, 128), new Point(48, 128),
				new Point(64, 128), new Point(64, 144), new Point(64, 160),
				new Point(48, 160), new Point(32, 160), new Point(32, 144),
				new Point(80, 128), new Point(96, 128), new Point(96, 144),
				new Point(80, 144), new Point(112, 128), new Point(128, 128),
				new Point(128, 144), new Point(112, 144), new Point(48, 144),

				/* Marais */// 202
				new Point(128, 128), new Point(144, 128), new Point(128, 144),
				new Point(144, 144), new Point(208, 16), new Point(288, 48),  /*
																				 * 9--
																				 * 14
																				 * +
																				 * 104
																				 */
				/* Fisherman bridge */
				new Point(32, 192), new Point(48, 192), new Point(64, 192),
				new Point(32, 208), new Point(32, 224),
				new Point(48, 224), new Point(64, 224),
				/* Fisherman door */
				new Point(0, 208), new Point(16, 208),
				new Point(0, 224)	// Broken wall
				};

		pkmChanges = Arrays.asList(new GraphChange[] { new GraphChange("exteria6", 0, 0),
				new GraphChange("exteria7", 105, 160),
				new GraphChange("exteria3", 202, 176) });
	}
}
