package zeditor.tools.sprites;

import java.util.Arrays;

import zeditor.tools.tiles.GraphChange;
import zildo.monde.util.Zone;

public class PjZildo extends SpriteBanque {

	public PjZildo() {
		zones=new Zone[] {
				// UP_FIXED
				new Zone(206, 150, 16, 22), 
				new Zone(0, 110, 16, 24),  new Zone(16, 110, 16, 24), new Zone(32, 110, 16, 24),
				new Zone(48, 110, 16, 24), new Zone(80, 110, 16, 24), new Zone(96, 110, 16, 24),
				new Zone(64, 110, 16, 24), new Zone(112, 110, 16, 24),
				
				// RIGHT_FIXED
				new Zone(280, 57, 16, 23),
				new Zone(1, 86, 15, 24), new Zone(16, 86, 16, 24), new Zone(33, 86, 16, 24),
				new Zone(50, 86, 16, 24), new Zone(66, 86, 16, 24), new Zone(82, 86, 16, 24),
				new Zone(97, 86, 16, 24),
				
				// DOWN_FIXED (17)
				new Zone(1, 1, 16, 22),
				new Zone(0, 62, 16, 24), new Zone(16, 62, 16, 24), new Zone(32, 62, 16, 24),
				new Zone(48, 62, 16, 24), new Zone(64, 62, 16, 24), new Zone(80, 62, 16, 24),
				new Zone(96, 62, 16, 24),
				
				// No lefts any more : 8 sprites
				
				// HANDSUP_UP_FIXED (25)
				new Zone(0, 45, 16, 22), new Zone(16, 45, 16, 22), new Zone(32, 45, 16, 22),
				new Zone(48, 45, 16, 22), new Zone(64, 45, 16, 22),
				
				// HANDSUP_RIGHT_FIXED (30)
				new Zone(0, 22, 16, 23), new Zone(16, 22, 16, 23), new Zone(32, 22, 16, 23),
				
				// HANDSUP_DOWN_FIXED
				new Zone(0, 0, 16, 22), new Zone(16, 0, 16, 22), new Zone(32, 0, 16, 22),
				new Zone(48, 0, 16, 22), new Zone(64, 0, 16, 22),

				// No lefts any more : 3 sprites
				
				// PUSHPULL_UP (38)
				new Zone(0, 92, 16, 21), new Zone(16, 92, 16, 21),
				//RIGHT
				new Zone(0, 113, 16, 23), new Zone(18, 113, 17, 23),
				// DOWN
				new Zone(0, 136, 16, 20), new Zone(16, 136, 16, 20),
				// left should be removed
				//new Zone(0, 156, 16, 23), new Zone(16, 156, 16, 23),
				
				// Lift (44)
				new Zone(2, 179, 15, 21), new Zone(17, 179, 15, 21),
				
				// Attack with sword
				// UP (46)
				new Zone(249, 17, 16, 24), new Zone(249, 41, 16, 24),
				new Zone(249, 65, 16, 24), new Zone(249, 89, 16, 24), new Zone(249, 113, 16, 24),
				// RIGHT (51)
				new Zone(283, 17, 16, 24), new Zone(283, 41, 16, 24),
				new Zone(283, 65, 18, 24), new Zone(283, 89, 16, 24), new Zone(283, 113, 16, 24),
				// DOWN (56)
				new Zone(266, 17, 16, 24), new Zone(266, 41, 16, 24),
				new Zone(266, 65, 16, 24), new Zone(266, 89, 16, 24), new Zone(266, 113, 16, 24),
				// 6 left removed
				
				// WOUND (61) LEFT has to be removed
				new Zone(0, 159, 16, 20), new Zone(16, 158, 16, 21),
				new Zone(32, 160, 16, 19), new Zone(48, 158, 16, 21),
				
				// PUSH (65)
				// UP
				new Zone(125, 0, 16, 22), new Zone(141, 0, 16, 22),
				new Zone(157, 0, 16, 22), new Zone(173, 0, 16, 22), new Zone(189, 0, 16, 22),
				// RIGHT
				new Zone(125, 22, 16, 23), new Zone(141, 22, 16, 23), new Zone(157, 22, 16, 23),
				// DOWN
				new Zone(125, 45, 16, 20), new Zone(141, 45, 16, 20), new Zone(157, 45, 16, 20),
				// LEFT (has to be removed)
				//new Zone(125, 65, 16, 23), new Zone(141, 65, 16, 23), new Zone(157, 65, 16, 23),
				
				// JUMP (76)
				new Zone(214, 0, 16, 20), new Zone(230, 0, 16, 20),
				new Zone(246, 0, 15, 21), new Zone(261, 0, 16, 20),
				
				// WATER FEET (80)
				new Zone(50, 70, 16, 8), new Zone(50, 79, 16, 8), new Zone(50, 88, 16, 8),

				// SHIELD (LEFT has to be removed)
				new Zone(34, 101, 6, 8), new Zone(42, 100, 4, 10),
				new Zone(33, 91, 8, 10), new Zone(47, 100, 4, 10),
				
				// ARMSRAISED (87)
				new Zone(126, 96, 16, 23),
				
				// Attack with bow (need to be reviewed => without bow)
				// UP
				new Zone(172, 77, 18, 22), new Zone(192, 77, 21, 21), new Zone(215, 77, 21, 22),
				// RIGHT
				new Zone(172, 52, 17, 23), new Zone(191, 52, 19, 22), new Zone(212, 52, 20, 23),
				// DOWN
				new Zone(172, 1, 18, 21), new Zone(192, 1, 17, 24), new Zone(211, 0, 18, 22),
				// LEFT : removed
				
				// DIRT (97)
				new Zone(67, 70, 16, 8), new Zone(67, 79, 16, 8), new Zone(67, 88, 16, 8),
				
				// LAYDOWN (100)
				new Zone(0, 181, 24, 15),
				
				// FALLING
				new Zone(71, 160, 22, 21),
				
				// SWORD
				new Zone(75, 146, 12, 6), new Zone(88, 146, 12, 14),
				new Zone(101, 146, 14, 14), new Zone(116, 146, 7, 12),
				
				// Zildo playing flut
				new Zone(4, 165, 15, 23),
				
				// Sleeping
				new Zone(25, 178, 15, 13), new Zone(25, 165, 15, 13),
				new Zone(42, 177, 15, 13), new Zone(58, 177, 13, 13)
		};
		
		pkmChanges = Arrays.asList(new GraphChange[]{
		new GraphChange("link2b", 0, 0),new GraphChange("link3b", 25, 0), 
		new GraphChange("link1c", 46, 0),
		new GraphChange("link2b", 61, 0), new GraphChange("link3b", 65, 0),
		new GraphChange("link1b", 88, 0), new GraphChange("link3b", 97, 0), 
		new GraphChange("link2b", 100, 0),
		new GraphChange("link1c", 102, 0)
		});
	}
}
