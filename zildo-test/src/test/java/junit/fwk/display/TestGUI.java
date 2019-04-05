package junit.fwk.display;

import org.junit.Assert;
import org.junit.Test;

import tools.EngineUT;
import zildo.client.ClientEngineZildo;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.util.Pointf;

public class TestGUI extends EngineUT {

	/** Before, a GUI blink was observed when a character was moved in PathFinder#collide. That is not right. **/ 
	@Test
	public void dontBlink() {
		mapUtils.loadMap("d4m8");
		waitEndOfScripting();
		
		Perso character = spawnTypicalPerso("walker",135, 111);
		character.setTarget(new Pointf(72, 111));
		character.setGhost(true);
		while (character.getTarget() != null) {
			renderFrames(1);
			Assert.assertTrue("GUI shouldn't have blinked during the character's move !", ClientEngineZildo.guiDisplay.isToDisplay_generalGui());
		}
		
	}
}
