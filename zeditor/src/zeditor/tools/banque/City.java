package zeditor.tools.banque;

import java.util.Arrays;

import zeditor.tools.tiles.Banque;
import zeditor.tools.tiles.GraphChange;

public class City extends Banque {
	int[][] coordsInt;
	
	public City() {
		int[][] c = {
				// Blue house
				{32,0}, {48,0}, {80, 0}, {96, 0}, {112, 0},
				{0, 16}, {16, 16}, {32,16}, {48,16}, {64,16}, {80,16},{96,16}, {112,16}, {128,16}, {144,16},
				{0, 32}, {16, 32}, {32,32}, {48,32}, {64,32}, {80,32},{96,32}, {112,32}, {128,32}, {144,32},
				{0, 48}, {16, 48}, {32,48}, {48,48}, {64,48}, {80,48},{96,48}, {112,48}, {128,48}, {144,48},
				{0, 64}, {16, 64}, {32,64}, {48,64}, {64,64}, {80,64},{96,64}, {112,64}, {128,64}, {144,64},
				{0, 80}, {16, 80}, {32,80}, {48,80}, {64,80}, {80,80},{96,80}, {112,80}, {128,80}, {144,80},
				{0, 96}, {16, 96}, {32,96}, {48,96}, {64,96}, {80,96},{96,96}, {112,96}, {128,96}, {144,96},
				{0, 112}, {16, 112}, {32,112}, {48,112}, {64,112}, {80,112},{96,112}, {112,112}, {128,112}, {144,112},
				{0, 128}, {16, 128}, {32,128}, {48,128}, {64,128}, {80,128},{96,128}, {112,128}, {128,128},
				{48,144}, {64,144}, {80,144}
				
		};
		coordsInt = c;
		
		pkmChanges = Arrays.asList(new GraphChange("city", 0, 0)); 
	}
	
	@Override
	public int[][] getCoordsInt() {
		return coordsInt;
	}
}
