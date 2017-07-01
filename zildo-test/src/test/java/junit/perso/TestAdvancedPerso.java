package junit.perso;

import org.junit.Assert;

import org.junit.Test;

import tools.EngineUT;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.server.EngineZildo;

public class TestAdvancedPerso extends EngineUT {

	/** We had a NPE at frame 645, because dragon has been removed, and he still had a 'persoAction' attached to him. **/
	@Test
	public void clearRunningPersoAction() {
		mapUtils.loadMap("dragon");
		waitEndOfScripting();
		PersoPlayer zildo = spawnZildo(769, 684); //577,374);
		Perso dragon = EngineZildo.persoManagement.getNamedPerso("dragon");
		EngineZildo.scriptManagement.runPersoAction(dragon, "bossDragon", null);
		
		// Wait for dragon to dive
		waitForScriptRunning("dragonDiveAndReappear");
		
		zildo.beingWounded(4, 4, dragon, zildo.getPv());
		Assert.assertEquals(0, zildo.getPv());
		waitForScriptRunning("death");
		renderFrames(250);
	}
}
