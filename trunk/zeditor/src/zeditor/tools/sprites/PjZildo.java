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
				
				// RIGHT_FIXED
				new Zone(125, 60, 14, 22),
				new Zone(0, 86, 16, 24), new Zone(16, 86, 16, 24), new Zone(32, 86, 16, 24),
				new Zone(48, 86, 16, 24), new Zone(64, 86, 16, 24), new Zone(80, 86, 16, 24),
				new Zone(96, 86, 16, 24),
				
				// DOWN_FIXED
				new Zone(1, 1, 16, 22),
				new Zone(0, 62, 16, 24), new Zone(16, 62, 16, 24), new Zone(32, 62, 16, 24),
				new Zone(48, 62, 16, 24),
				
				// No lefts any more : 8 sprites
				
				// HANDSUP_UP_FIXED
				new Zone(0, 45, 16, 22), new Zone(16, 45, 16, 22), new Zone(32, 45, 16, 22),
				new Zone(48, 45, 16, 22), new Zone(64, 45, 16, 22),
				
				// HANDSUP_RIGHT_FIXED
				new Zone(0, 22, 16, 23), new Zone(16, 22, 16, 23), new Zone(32, 22, 16, 23),
				
				// HANDSUP_DOWN_FIXED
				new Zone(0, 0, 16, 22), new Zone(16, 0, 16, 22), new Zone(32, 0, 16, 22),
				new Zone(48, 0, 16, 22), new Zone(64, 0, 16, 22),

				// No lefts any more : 3 sprites
				
				// PUSHPULL_UP
				new Zone(0, 92, 16, 21), new Zone(16, 92, 16, 21),
				//RIGHT
				new Zone(0, 113, 16, 23), new Zone(16, 113, 16, 23),
				// DOWN
				new Zone(0, 136, 16, 20), new Zone(16, 136, 16, 20),
				// left should be removed
				new Zone(0, 156, 16, 23), new Zone(16, 156, 16, 23),
				
				// Lift
				new Zone(0, 179, 17, 21), new Zone(0, 179, 17, 21),
				
				// Attack with sword
				
				
				// PUSHPULL_RIGHT
				
				//new Zone(87, 0, 24, 15),
				new Zone(49, 24, 22, 21)
		};
		
		pkmChanges = Arrays.asList(new GraphChange[]{
		new GraphChange("link2b", 0, 0), new GraphChange("link3b", 20, 0)});
	}
}
