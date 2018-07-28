package zeditor.tools.banque;

import java.util.Arrays;

import zeditor.tools.tiles.Banque;
import zeditor.tools.tiles.GraphChange;

public class Grass extends Banque {
	int[][] coordsInt;
	
	public Grass() {
		int[][] c = {
				// Tree
				{0, 0}, {16, 0}, {32, 0}, {48, 0},
				{0, 16}, {16, 16}, {32, 16}, {48, 16},
				{0, 32}, {16, 32}, {32, 32}, {48, 32},
				{0, 48}, {16, 48}, {32, 48}, {48, 48},
				{0, 64}, {16, 64}, {32, 64}, {48, 64},
				{0, 80}, {16, 80}, {32, 80}, {48, 80},
				// Barrier
				{64, 0}, {80, 0},
				{64, 16}, {80, 16},
				// Barrier 2
				{64, 32}, {80, 32},
				{64, 48}, {80, 48},
				{96, 32}, {96, 48},
				// Dark grass patch
				{112, 0}, {128, 0}, {144, 0},
				{112, 16}, {128, 16}, {144, 16},
				{112, 32}, {128, 32}, {144, 32},
				// Rock
				{112, 48}, {128, 48},
				// Path
				{64, 64}, {80, 64}, {96, 64}, {112, 64},
				{80, 80}, {96, 80}, {112, 80}, {128, 80},
				{64, 96}, {80, 96}, {96, 96}, {112, 96},
				// Bush
				{128, 64}
			
				
		};
		coordsInt = c;
		
		pkmChanges = Arrays.asList(new GraphChange("grass", 0, 0)); 
	}
	
	@Override
	public int[][] getCoordsInt() {
		return coordsInt;
	}
		
}
