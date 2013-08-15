/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
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
				// Second floor
				{0, 80}, {16, 80}, {0, 96}, {16, 96},
				// Pillar
				{48, 0}, {48, 16}, {48, 32},
				// Alcove
				{64, 0}, {80, 0}, {64, 16}, {80, 16}, 
				// Door
				{160, 0}, {176, 0}, {192, 0},
				{160, 16}, {176, 16}, {192, 16},
				// Ledge
				{0, 112}, {16, 112}, {32, 112},
				{0, 128}, {16, 128}, {32, 128},
				{0, 144}, {16, 144}, {32, 144},
				// Pillar in the void
				{48, 112}, {48, 128}
		};
				
		coordsInt = c;
				
		
	
		pkmChanges = Arrays.asList(new GraphChange("interia6", 0, 0)); 
	}
	
	@Override
	public int[][] getCoordsInt() {
		return coordsInt;
	}
}
