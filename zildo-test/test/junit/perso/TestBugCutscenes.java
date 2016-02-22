package junit.perso;

import org.junit.Assert;

import org.junit.Test;

import zildo.monde.util.Vector2f;
import zildo.server.EngineZildo;

public class TestBugCutscenes extends EngineUT {

	@Test
	public void thiefAttack() {
		mapUtils.loadMap("voleurs");
		spawnZildo(900, 984);
		waitEndOfScripting();
		
		simulateDirection(new Vector2f(-2, 0));
	
		Assert.assertFalse(EngineZildo.scriptManagement.isQuestDone("attaqueVoleurs"));
		renderFrames(100);
		Assert.assertTrue(EngineZildo.scriptManagement.isQuestDone("attaqueVoleurs"));
	}
}
