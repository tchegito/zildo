package junit.area;

import org.junit.Assert;
import org.junit.Test;

import tools.EngineUT;
import tools.annotations.InfoPersos;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.server.EngineZildo;

public class CheckPersoMoving extends EngineUT {
/*
	
	 
	*/
	// B11 in Ruben's list
	@Test
	public void fallInWater_igorLily() {
		fall(143, 201, "igorlily");
	}

	@Test
	public void fallInWater_igorVillage() {
		fall(416,308, "igorvillage");
	}

	@Test
	public void fallInWater_sousBois4() {
		fall(912,543, "sousbois4");
	}

	/** Place hero just before a bridge over water, and make him walk toward water. Assert that the right scene is run. **/
	private void fall(int x, int y, String mapName) {
		mapUtils.loadMap("igorlily");
		spawnZildo(143,201);
		waitEndOfScripting();
		simulateDirection(0, 1);
		while (!EngineZildo.scriptManagement.isScripting()) {
			renderFrames(1);
		}
		Assert.assertFalse("Hero should not 'fall' in water, but splash into it !", EngineZildo.scriptManagement.isQuestProcessing("fallPit"));
		Assert.assertTrue(EngineZildo.scriptManagement.isQuestProcessing("dieInWater"));		
	}
	@Test
	public void fallInWater2() {
		mapUtils.loadMap("igorlily");
		PersoPlayer zildo = spawnZildo(143,260);
		waitEndOfScripting();
		zildo.walkTile(false);
		Assert.assertFalse("Hero is still on the bridge ! He shouldn't have dived !", EngineZildo.scriptManagement.isQuestProcessing("dieInWater"));
		Assert.assertFalse(EngineZildo.scriptManagement.isScripting());
	}
}
