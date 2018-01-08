package zeditor.tools.sprites;

import java.util.Arrays;

import zeditor.tools.tiles.GraphChange;
import zildo.monde.util.Zone;

public class Pnj4 extends SpriteBanque {

	public Pnj4() {
		zones=new Zone[] {
			/* Turret under floor */
			new Zone(1, 1, 32, 25),
			// Sub-heads + canon
			new Zone(35, 5, 32, 21),new Zone(80, 5, 11, 5),
			new Zone(80, 11, 10, 8), new Zone(80, 21, 3, 3),
			new Zone(35, 27, 32, 21),
			// Vulnerable part
			new Zone(2,45,19,12), new Zone(1,28,15,15), 
			// Sleeping king (8)
			new Zone(0, 474, 18, 20), new Zone(19, 474, 18, 20), new Zone(38, 474, 18, 20),
			// Druid (11)
			new Zone(69, 469, 16, 27), new Zone(86, 470, 14, 26),
			new Zone(101, 469, 13, 27), new Zone(115, 470, 16, 26),
			new Zone(113, 189, 16, 26), new Zone(130, 189, 16, 26),

			// Vacto (17)
			new Zone(0, 3, 16, 26), new Zone(16, 2, 16, 27),
			new Zone(33, 4, 13, 25), new Zone(48, 3, 13, 26),
			new Zone(64, 3, 14, 26), new Zone(79, 2, 16, 27),
			
		    // Dying squirrel (23)
		    new Zone(1, 48, 14, 12),
		    new Zone(17, 48, 14, 12), 
		    new Zone(32, 47, 14, 13),
		    new Zone(48, 47, 14, 13), new Zone(64, 49, 18, 11), new Zone(84, 54, 24, 6),
		    
		    // Fire elemental (29)
		    new Zone(167, 201, 23, 27), new Zone(193, 201, 23, 27),
		    new Zone(218, 201, 23, 27), new Zone(242, 201, 25, 27),
		    
		    // Coal (33)
		    new Zone(0, 372, 26, 24), new Zone(27, 372, 26, 24),
		    new Zone(54, 377, 31, 19), new Zone(86, 383, 31, 13),
			
			// Bitey (37)
		    // Idle (near)
		    //new Zone(0, 3, 21, 24), new ZoneO(25+1, 5, 21-1, 22, 11), new Zone(50+2, 5, 21-3, 22),
		    new Zone(0, 3, 21, 24), new Zone(25, 5, 21, 22), new Zone(50, 5, 21, 22),
		    new Zone(75, 3, 21, 24), new Zone(100, 2, 21, 25), new Zone(125, 1, 21, 26),
		    new Zone(150, 0, 21, 27),
		    // Idle (far)
		    new Zone(0, 55, 21, 23), new Zone(25, 54, 21, 24),
		    new Zone(50, 54, 21, 24), new Zone(75, 55, 21, 23),
			// Attacking (48) (we use offsets here)
			new ZoneO(4, 105, 16, 22, /*offset*/4, 1,0),new ZoneO(25, 105, 21, 22, 1, 0, 0), new ZoneO(50, 105, 22, 22, /* */1, 0, 0), 
			new ZoneO(75, 106, 22, 21, 1, 0, 0), new ZoneO(101, 106, 20, 21, 1, 0, 0),new ZoneO(121, 105, 24, 22, 0, 4, 0),
			new Zone(1, 130, 38, 18), new Zone(40, 129, 40, 21), new Zone(81, 129, 41, 21),
			new Zone(123, 129, 43, 21), new Zone(1,152, 55, 22), new Zone(57, 159, 55, 12),
			new Zone(113, 157, 52, 14), new Zone(166, 158, 51, 13), new Zone(218, 159, 48, 12),
			new Zone(1, 189, 45, 12), new Zone(48, 187, 41, 13), new Zone(92, 178, 23, 22)
			
			
		};
		
		pkmChanges = Arrays.asList(new GraphChange[] { new GraphChange("turret", 0, 0),
				new GraphChange("pnj2", 8, 0),
				new GraphChange("squirrel", 23, 0),
				new GraphChange("dragonpal", 29, 0, true),
				new GraphChange("bitey", 37, 0, true)
		});
	}
}
