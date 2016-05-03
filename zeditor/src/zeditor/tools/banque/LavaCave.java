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
				{0, 64}
		};
				
		coordsInt = c;
				
		
	
		pkmChanges = Arrays.asList(new GraphChange("interia7", 0, 0)); 
	}
	
	@Override
	public int[][] getCoordsInt() {
		return coordsInt;
	}
}
