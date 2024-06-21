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
public class LavaCave extends Banque {
	
	int[][] coordsInt;

	public LavaCave() {
		
		int[][] c = {
				// Rock wall on lava
				{0, 0}, {16, 0}, {0, 16}, {16, 16}, {0, 32}, {16, 32}, {0, 48}, {16, 48},
				// Entrance
				{32, 0}, {48, 0}, {64, 0}, {80, 0},
				{32, 16}, {48, 16}, {64, 16}, {80, 16},
				// Floor
				{96, 0}, {112, 0}, {96, 16}, {112, 16}, {96, 32}, {112, 32},
				// Floor
				{128, 0}, {144, 0}, {128, 16}, {144, 16}, {128, 32}, {144, 32},
				// Corner
				{32, 32}, {48, 32}, {32, 48}, {48, 48},
				// Wall without lava
				{64, 32}, {80, 32},
				// Lava
				{0, 64},
				
				// Dragon cave
				{16, 64}, {32, 64}, {48, 64},
				{0, 80}, {16, 80}, {32, 80},
				{0, 96}, {16, 96}, {32, 96},
				{0, 112}, {16, 112}, {32, 112},
				{0, 128}, {16, 128}, {32, 128},
				{0, 144}, {16, 144}, {32, 144},
				{16, 160}, {32, 160},
				{16, 176}, {32, 176},
				{16, 192}, {32, 192},
				{160, 0}, {176, 0}, {192, 0}, {208, 0}, {224, 0}, {240, 0},
				{160, 16}, {176, 16}, {192, 16}, {208, 16}, {224, 16}, {240, 16},
				{160, 32}, {176, 32}, {192, 32}, {208, 32}, {224, 32}, {240, 32},
				{160, 48}, {176, 48}, {192, 48}, {208, 48}, {224, 48}, {240, 48},
				{64, 48}, {80, 48}, {96, 48},
				{64, 64}, {80, 64}, {96, 64},
				{64, 80}, {80, 80}, {96, 80},
				{256, 0}, {272, 0}, {256, 16}, {272, 16}, {272, 32},
				{48, 112}, {64, 112}, {48, 128}, {64, 128}, {48, 144}, {64, 144},
				{48, 160}, {64, 160}, {48, 176}, {64, 176},
				{112, 48}, {112, 64}, {112, 80}, {112, 96},
				{128, 48}, {144, 48}, {128, 64}, {144, 64},
				{128, 80}, {144, 80}, {160, 80},
				{128, 96}, {144, 96}, {160, 96},
				{128, 112}, {144, 112}, {160, 112},
				{48,96},
				
				// Stairs
				{288, 0}, {304, 0},
				{288, 16}, {304, 16}, {288, 32}, {304, 32},
				
				// Corner rock walls
				{80, 96},{96, 96}, {80, 112}, {96, 112},
				// Lever on left
				{256, 32},
				// Special tile for back2-fore misconception reason
				{48, 80},
				// Lava wall animation
				{80, 144}, {96, 144},
				{80, 160}, {96, 160}
		};
				
		coordsInt = c;
				
		
	
		pkmChanges = Arrays.asList(new GraphChange("interia7", 0, 0)); 
	}
	
	@Override
	public int[][] getCoordsInt() {
		return coordsInt;
	}
}
