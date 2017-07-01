package junit.perso;

import org.junit.Assert;
import org.junit.Test;

import tools.EngineUT;
import tools.annotations.InfoPersos;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.server.EngineZildo;

public class TestAdvancedPerso extends EngineUT {

	/** We had a NPE at frame 645, because dragon has been removed, and he still had a 'persoAction' attached to him.
	 * In 'dragonDiveAndReappear', we tried to modify a removed character. Now, a character removal, occuring by
	 * 'remove' action in 'death' scene, include persoAction removal. **/
	@Test
	public void clearRunningPersoAction() {
		mapUtils.loadMap("dragon");
		waitEndOfScripting();
		PersoPlayer zildo = spawnZildo(769, 684);
		Perso dragon = EngineZildo.persoManagement.getNamedPerso("dragon");
		EngineZildo.scriptManagement.runPersoAction(dragon, "bossDragon", null);
		
		// Wait for dragon to dive
		waitForScriptRunning("dragonDiveAndReappear");
		
		zildo.beingWounded(4, 4, dragon, zildo.getPv());
		Assert.assertEquals(0, zildo.getPv());
		waitForScriptRunning("death");
		renderFrames(250);
	}
	
	@Test @InfoPersos
	public void dieStoppingAutomaticScenes() {
		mapUtils.loadMap("dragon");
		PersoPlayer zildo = spawnZildo(861,214);
		waitEndOfScripting();
		// Hit hero to project him on a stair
		zildo.beingWounded(zildo.x, zildo.y+6, null, zildo.getPv());
		renderFrames(10);
		
		// Check that hero hasn't triggered any stairs up/down scene
		Assert.assertFalse(EngineZildo.scriptManagement.isQuestProcessing("miniStairsUp"));
		Assert.assertFalse(EngineZildo.scriptManagement.isQuestProcessing("miniStairsDown"));
		Assert.assertTrue(zildo.y >= 205);
	}
	
}
