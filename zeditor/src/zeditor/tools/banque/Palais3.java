/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
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

package zeditor.tools.banque;

import java.util.Arrays;

import zeditor.tools.tiles.Banque;
import zeditor.tools.tiles.GraphChange;

/**
 * @author Tchegito
 *
 */
public class Palais3 extends Banque {
	
	int[][] coordsInt;

	public Palais3() {
		
		int[][] c = {
				// Corners
				{0, 0}, {16, 0}, {0, 16}, {16, 16},
				// Side walls
				{32, 0}, {32, 16}, {0, 32}, {16, 32},
				// Floors (corner + 4 typicals)
				{32, 32}, {0, 48}, {16, 48}, {0, 64}, {16,64},
				// Second floor (13)
				{0, 80}, {16, 80}, {0, 96}, {16, 96},
				// Pillar
				{48, 0}, {48, 16}, {48, 32},
				// Alcove
				{64, 0}, {80, 0}, {64, 16}, {80, 16}, 
				// Door (24)
				{160, 0}, {176, 0}, {192, 0},
				{160, 16}, {176, 16}, {192, 16},
				// Ledge
				{0, 112}, {16, 112}, {32, 112},
				{0, 128}, {16, 128}, {32, 128},
				{0, 144}, {16, 144}, {32, 144},
				// Pillar in the void (39)
				{48, 112}, {48, 128},
				{0, 160}, {16, 160}, {32, 160},
				// Wall interior corner
				{32, 80}, {48, 80}, {32, 96}, {48, 96},
				// Lit alcove (48)
				{96, 0}, {96, 16}, {96, 32}, {112, 0}, {128, 0}, {112, 16}, {128, 16},
				{144, 0}, {144, 16}, {144, 32},
				// Lit floor (58)
				{112, 32}, {128, 32}, {32, 64}, {48, 64},
				// Ledge interior corner (with void)
				{48, 144}, {48, 160},
				// Wall with shadow
				{208, 0}, {208, 16},
				// Floor with soil
				{64, 32}, {80, 32},
				// Lit door (68)
				{224, 0}, {240, 0}, {256, 0},
				{224, 16}, {240, 16}, {256, 16},
				{224, 32}, {240, 32}, {256, 32},
				// Barrier (77)
				{0, 176},
				// Animating candle (14 tiles per animation)
				{0, 0}, {16, 0}, {32, 0}, {48, 0},
				{0, 16}, {16, 16}, {32, 16}, {48, 16},
				{0, 32}, {16, 32}, {32, 32}, {48, 32},
				{16, 48}, {32, 48},
				{64, 0}, {80, 0}, {96, 0}, {112, 0},
				{64, 16}, {80, 16}, {96, 16}, {112, 16},
				{64, 32}, {80, 32}, {96, 32}, {112, 32},
				{80, 48}, {96, 48},
				{128, 0}, {144, 0}, {160, 0}, {176, 0},
				{128, 16}, {144, 16}, {160, 16}, {176, 16},
				{128, 32}, {144, 32}, {160, 32}, {176, 32},
				{144, 48}, {160, 48},
				// 121
				{192, 0}, {208, 0}, {224, 0}, {240, 0},
				{192, 16}, {208, 16}, {224, 16}, {240, 16},
				{192, 32}, {208, 32}, {224, 32}, {240, 32},
				{208, 48}, {224, 48},
				{256, 0}, {272, 0}, {288, 0}, {304, 0},
				{256, 16}, {272, 16}, {288, 16}, {304, 16},
				{256, 32}, {272, 32}, {288, 32}, {304, 32},
				{272, 48}, {288, 48},
				{320, 0}, {336, 0}, {352, 0}, {368, 0},
				{320, 16}, {336, 16}, {352, 16}, {368, 16},
				{320, 32}, {336, 32}, {352, 32}, {368, 32},
				{336, 48}, {352, 48},
				// Smaller flame shining on the floor (163)
				{16, 64}, {32, 64},
				{80, 64}, {96, 64},
				{144, 64}, {160, 64},
				{208, 64}, {224, 64},
				{272, 64}, {288, 64},
				{336, 64}, {352, 64},
				// Platform to jump (174)
				{32, 48}, {48, 48},
				// Slab (176)
				{64, 48}, {80, 48},
				// Rambard vertical
				{16, 176}, {32, 176}, {48, 176}
		};
				
		coordsInt = c;
				
		
	
		pkmChanges = Arrays.asList(new GraphChange("palace1", 0, 0),
				new GraphChange("interia8", 78, 192),
				new GraphChange("palace1", 174, -192)); 
	}
	
	@Override
	public int[][] getCoordsInt() {
		return coordsInt;
	}
}
