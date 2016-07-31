package zeditor.tools.sprites;

import java.util.Arrays;

import zeditor.tools.tiles.GraphChange;
import zildo.monde.util.Zone;

public class Pnj4 extends SpriteBanque {

	public Pnj4() {
		zones=new Zone[] {
			/* Turret under floor (19)*/
			new Zone(1, 1, 32, 25),
			// Sub-heads + canon
			new Zone(35, 5, 32, 21),new Zone(80, 5, 11, 5),
			new Zone(80, 11, 10, 8), new Zone(80, 21, 3, 3),
			new Zone(35, 27, 32, 21),
			// Vulnerable part
			new Zone(2,45,19,12), new Zone(1,28,15,15), 
			// Sleeping king (8)
			new Zone(0, 474, 18, 20), new Zone(19, 474, 18, 20), new Zone(38, 474, 18, 20),
			// Druid
			new Zone(69, 469, 16, 27), new Zone(86, 470, 14, 26),
			new Zone(101, 469, 13, 27), new Zone(115, 470, 16, 26)
			// Bitey
			/*
		    new Zone(0, 3, 21, 24), new Zone(25, 5, 21, 22), new Zone(50, 5, 21, 22),
		    new Zone(75, 3, 21, 24), new Zone(100, 2, 21, 25), new Zone(125, 1, 21, 26),
		    new Zone(150, 0, 21, 27),
		    */
		};
		
		pkmChanges = Arrays.asList(new GraphChange[] { new GraphChange("turret", 0, 0),
				new GraphChange("pnj2", 8, 0)
		});
	}
}
