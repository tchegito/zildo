package zeditor.tools.sprites;

import java.util.Arrays;

import zeditor.tools.tiles.GraphChange;
import zildo.monde.util.Zone;

public class Pnj5 extends SpriteBanque {

	public Pnj5() {
		zones=new Zone[] {
		
				/*
			// Red Hooded (finir d'abord le badguy)
			new Zone(6, 4, 17, 28), new Zone(39, 5, 16, 27), new Zone(6, 36, 17, 28), new Zone(39, 37, 16, 27),
			new Zone(6, 69, 18, 27), new Zone(39, 68, 16, 28), new Zone(71, 69, 17, 27), new Zone(103, 68, 16, 28),
			new Zone(6, 103, 17, 25), new Zone(39, 102, 16, 26), new Zone(71, 100, 16, 28), new Zone(103, 101, 16, 27),
			new Zone(134, 103, 17, 25), new Zone(167, 102, 16, 26), new Zone(199, 100, 16, 28), new Zone(231, 101, 16, 27),
			// Bouncing
			new Zone(6,132, 17, 28), new Zone(38, 134, 18, 26), new Zone(69, 137, 20, 23), new Zone(100, 140, 22, 20),
			new Zone(134, 137, 19, 23), new Zone(166, 134, 17, 26),
			// Running
			new Zone(7, 164, 16, 28), new Zone(38, 167, 18, 25), new Zone(71, 162, 16, 28), new Zone(102, 161, 18, 28),
			new Zone(134, 162, 19, 27), new Zone(166, 167, 19, 25), new Zone(198, 166, 17, 26), new Zone(230, 165, 17, 25),
			// Dying
			new Zone(7, 196, 16, 28), new Zone(38, 198, 18, 26), new Zone(70, 200, 18, 23), new Zone(104, 200, 16, 23),
			// Dying 2
			new Zone(37, 229, 18, 27), new Zone(70, 231, 17, 25), new Zone(103, 231, 17, 24), new Zone(135, 239, 21, 17),
			new Zone(162, 244, 29, 12), new Zone(194, 245, 29, 12) //, new Zone()
			*/
			// Red Hooded (finir d'abord le badguy)
				
			new Zone(7, 4, 16, 28), new Zone(39, 5, 16, 27), new Zone(7, 36, 16, 28), new Zone(39, 37, 16, 27),
			new Zone(6, 69, 18, 27), new Zone(39, 68, 16, 28), new Zone(71, 69, 17, 27), new Zone(103, 68, 16, 28),
			new Zone(6, 103, 17, 25), new Zone(39, 102, 16, 26), new Zone(71, 100, 16, 28), new Zone(103, 101, 16, 27),
			new Zone(134, 103, 17, 25), new Zone(167, 102, 16, 26), new Zone(199, 100, 16, 28), new Zone(231, 101, 16, 27),
			// Bouncing
			new Zone(6,132, 17, 28), new Zone(38, 134, 18, 26), new Zone(69, 137, 20, 23), new Zone(100, 140, 22, 20),
			new Zone(134, 137, 19, 23), new Zone(166, 134, 17, 26),
			// Bouncing
			new Zone(7, 164, 16, 28), new Zone(38, 167, 18, 25), new ZoneO(71, 162, 16, 28, 0, 0, -2), new ZoneO(102, 161, 18, 28, 0, 0, -3),
			new ZoneO(134, 162, 19, 27, 1, 0, -3), new ZoneO(166, 167, 19, 25, 1, 0, 0), new ZoneO(198, 166, 17, 26, 1, 2, 0), new ZoneO(230, 165, 17, 27, 0, 1, 0),
			// Dying
			new Zone(7, 196, 16, 28), new Zone(38, 198, 18, 26), new Zone(70, 200, 18, 23), new Zone(104, 200, 16, 23),
			// Dying 2
			new Zone(37, 229, 18, 27), new Zone(70, 231, 17, 25), new Zone(103, 231, 17, 24), new Zone(135, 239, 21, 17),
			new Zone(162, 244, 29, 12), new Zone(194, 245, 29, 12), //, new Zone()
			
			// Attacking
			new Zone(7, 260, 16, 28), new Zone(40, 262, 16, 26), new Zone(71, 260, 19, 28), new Zone(102, 256, 18, 32),
			new Zone(134, 257, 26, 31), new Zone(166, 264, 18, 24), new Zone(199, 262, 16, 26), // + sprites already set
			
			// Poulpa (47)
			new Zone(0, 7, 23, 20), new Zone(25, 8, 24, 19), new Zone(51, 4, 18, 23), new Zone(71, 0, 18, 27),
			
			// Ice thrower (51)
			new Zone(0, 109, 23, 27), new Zone(25, 109, 23, 27), new Zone(50, 109, 23, 27), new Zone(74, 109, 25, 27)
		};
			
		pkmChanges = Arrays.asList(new GraphChange[] { 
				new GraphChange("hoodedAlpha4", 0, 0, true),
				new GraphChange("poulpa", 47, 0, false),
				new GraphChange("thrower", 51, 0, false)
		});
	}
}
