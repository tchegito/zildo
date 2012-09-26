package zeditor.tools.sprites;

import java.util.Arrays;

import zeditor.tools.tiles.GraphChange;
import zildo.monde.util.Zone;

public class PjZildo extends SpriteBanque {

	public PjZildo() {
		zones=new Zone[] {
				//new Zone(87, 0, 24, 15),
				new Zone(49, 24, 22, 21)
		};
		
		pkmChanges = Arrays.asList(new GraphChange[]{
		new GraphChange("link3b", 0, 0)});
	}
}
