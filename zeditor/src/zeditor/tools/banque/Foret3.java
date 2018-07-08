package zeditor.tools.banque;

import java.util.Arrays;

import zeditor.tools.tiles.Banque;
import zeditor.tools.tiles.GraphChange;

// Foret3 = exteria6 + exteria7 + exteria3 (207 tiles)
public class Foret3 extends Banque {

	int[][] coordsInt;
	
	public Foret3() {
		int[][] c = {
				// Desert tiles
				{0, 0}, {16, 0}, {32, 0},
				{0, 16}, {16, 16}, {32, 16},
				{0, 32}, {16, 32}, {32, 32},
				{0, 48}, {16, 48}, {32, 48},
				{48, 32}, {64, 32}, {80, 32}, {96, 32},
				{48, 48}, {64, 48}, {80, 48}, {96, 48},
				{96, 16}, {96, 32},
				// End
				{304, 0}, {288, 16},
				{304, 16},
				/* Marais - 25 */
				{48, 112}, {64, 112}, {80, 112},
				{48, 128}, {80, 128}, {48, 144},
				{64, 144}, {80, 144}, {96, 112},
				{112, 112}, {128, 112}, {96, 128},
				{128, 128}, {96, 144}, {112, 144},
				{128, 144}, {176, 64}, {192, 64},
				{176, 80}, {192, 80},
				/* Rocher - 45 */
				{0, 64}, {16, 64}, {32, 64},
				{0, 80}, {16, 80}, {32, 80},
				{0, 96}, {16, 96}, {32, 96},
				{0, 112}, {16, 112}, {32, 112},
				{0, 128}, {16, 128},
				/* D‚sert - 59 */
				{256, 112}, {272, 96}, {288, 96},
				{304, 96}, {272, 112}, {256, 96},
				{208, 64}, {224, 64}, {240, 64},
				{240, 80}, {240, 96}, {224, 96},
				{208, 96}, {208, 80}, {256, 64},
				{272, 64}, {272, 80}, {256, 80},
				{288, 64}, {304, 64}, {304, 80},
				{288, 80}, {224, 80},
				/* Montagnes - 91 */
				{176, 96}, {128, 48}, {192, 96},
				{144, 48}, {160, 48}, {128, 64},
				{144, 64}, {160, 64}, {144, 96},
				{160, 96}, {144, 112}, {160, 112},
				{128, 96}, {112, 96},
				/* Animations eau - 80 */
				{128, 80}, {224, 48}, {272, 48},
				{144, 80}, {240, 48}, {288, 48},
				{160, 80}, {256, 48}, {304, 48},

				/* Vraies montagnes - 106 */
				{0, 16}, {80, 16}, {0, 32},
				{16, 32}, {80, 32}, {0, 48},
				{32, 48}, {80, 48}, {0, 64},
				{32, 64}, {64, 64}, {32, 80},
				{0, 96}, {80, 96}, {32, 112},

				{80, 0}, {96, 0}, {112, 0},
				{144, 0}, {160, 0}, {96, 16},
				{112, 16}, {144, 16}, {160, 16},
				{112, 32}, {128, 32}, {144, 32},
				{96, 48}, {112, 48}, {128, 48},
				{144, 48}, {160, 48}, {176, 48},
				{96, 64}, {112, 64}, {160, 64},
				{112, 80}, {128, 0}, {128, 16},
				{96, 96}, {112, 96}, {128, 96},
				{144, 96}, {96, 112}, {112, 112},
				{128, 112}, {144, 112}, {32, 0},
				{48, 0}, {32, 16}, {48, 16},
				{160, 80}, {176, 80}, {160, 96},
				{176, 96}, {208, 0}, {224, 0},
				{240, 0}, {208, 16},
				/* Rocher */
				{176, 0}, {192, 0}, {176, 16},
				{192, 16},
				/* Entr‚e de grotte */
				{192, 32}, {208, 32}, {192, 48},
				{208, 48},
				/* 2e Entr‚e de grotte */
				{0, 128}, {16, 128}, {0, 144},
				{16, 144}, {0, 160}, {16, 160},
				/* Echelle */
				{224, 16}, {240, 16},
				/* Chemin */
				{80, 160}, {96, 160}, {112, 160},
				{128, 160}, {32, 128}, {48, 128},
				{64, 128}, {64, 144}, {64, 160},
				{48, 160}, {32, 160}, {32, 144},
				{80, 128}, {96, 128}, {96, 144},
				{80, 144}, {112, 128}, {128, 128},
				{128, 144}, {112, 144}, {48, 144},

				/* Marais */// 202
				{128, 128}, {144, 128}, {128, 144},
				{144, 144}, {208, 16}, {288, 48},  /*
																				 * 9--
																				 * 14
																				 * +
																				 * 104
																				 */
				/* Fisherman bridge */
				{32, 192}, {48, 192}, {64, 192},
				{32, 208}, {32, 224},
				{48, 224}, {64, 224},
				/* Fisherman door */
				{0, 208}, {16, 208},
				/* Cracked wall (cave) */
				{80, 192}, {96, 192}, {80, 208}, {96, 208},
				/* Cracked wall (outside) */
				{80, 224}, {96, 224}, {80, 240}, {96, 240},
				{48, 208},
				/* Cracked wall (house outside) */
				{112, 192}, {128, 192},
				/* Cracked hill (south side) */
				{0, 240}, {16, 240}, {32, 240}, {48, 240},
				/* Cracked cave wall */
				{112, 208}, {128, 208}
				};

		coordsInt = c;
		
		pkmChanges = Arrays.asList(new GraphChange[] { new GraphChange("exteria10", 0, 0),
				new GraphChange("exteria7", 105, 160),
				new GraphChange("exteria3", 202, 176) });
	}
	
	@Override
	public int[][] getCoordsInt() {
		return coordsInt;
	}
}
