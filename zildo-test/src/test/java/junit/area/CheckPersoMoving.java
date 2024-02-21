package junit.area;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Assert;
import org.junit.Test;

import tools.EngineUT;
import tools.annotations.SpyHero;
import zildo.monde.items.Item;
import zildo.monde.items.ItemKind;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.sprites.utils.MouvementPerso;
import zildo.monde.sprites.utils.MouvementZildo;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
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
	public void doesntFallInWater_igorLily() {
		doesntFallInWater("igorlily", new Point(143,260));
	}
	
	@Test
	public void doesntFallInWater_igorVillage() {
		doesntFallInWater("igorvillage", new Point(415, 340));
	}
	
	@Test
	public void doesntFallInWater_sousbois4() {
		doesntFallInWater("sousbois4", new Point(911, 580));
	}
	
	private void doesntFallInWater(String mapName, Point heroPos) {
		mapUtils.loadMap(mapName);
		PersoPlayer zildo = spawnZildo(heroPos.x, heroPos.y);
		waitEndOfScripting();
		zildo.walkTile(false);
		Assert.assertFalse("Hero is still on the bridge ! He shouldn't have dived !", EngineZildo.scriptManagement.isQuestProcessing("dieInWater"));
		Assert.assertFalse(EngineZildo.scriptManagement.isScripting());
	}
	
	/** To ensure no regression has been caused with 'ponton' feature, check that regular case is still ok.
	 * When hero falls into water, jumping from a hill.
	 */
	@Test @SpyHero
	public void fallInRegularWater() {
		mapUtils.loadMap("igorvillage");
		PersoPlayer zildo = spawnZildo(502, 285);
		waitEndOfScripting();
		simulateDirection(0,1);
		renderFrames(20);
		// Check that hero is jumping
		Assert.assertEquals(MouvementZildo.SAUTE, zildo.getMouvement());
		// Wait for his jump to be over
		while (zildo.getMouvement() == MouvementZildo.SAUTE) {
			renderFrames(1);
		}
		// Check that the diving method has been called
		verify(zildo, times(1)).diveAndWound();
		// Check that according script has been launched
		Assert.assertTrue(EngineZildo.scriptManagement.isQuestProcessing("dieInWater"));
	}
	
	@Test
	public void fallInLava1() {
		mapUtils.loadMap("voleursg5");
		waitEndOfScripting();
		// Spawn hero in middle of lava
		spawnZildo(218, 133);
		simulateDirection(1,0);
		renderFrames(2);
		checkScriptRunning("dieInPit");
	}
	
	@Test
	public void fallInLava2() {
		mapUtils.loadMap("cavef6");
		waitEndOfScripting();
		// Spawn hero in middle of lava
		spawnZildo(87, 215);
		simulateDirection(1,0);
		renderFrames(2);
		checkScriptRunning("dieInPit");
	} 
	
	@Test
	public void fallInLava3() {
		mapUtils.loadMap("dragon");
		waitEndOfScripting();
		// Spawn hero in middle of lava
		PersoPlayer zildo = spawnZildo(163,204);
		zildo.floor = 0;	// With floor at 1 it doesn't work but that's fair ==> no tile at floor 1
		simulateDirection(1,0);
		renderFrames(2);
		checkScriptRunning("dieInPit");
	} 

	@Test
	public void jumpInLava() {
		mapUtils.loadMap("dragon");
		waitEndOfScripting();
		// Spawn hero in middle of lava
		PersoPlayer zildo = spawnZildo(583,536);
		zildo.floor = 2;
		// Hero goes forward and should reach an edge
		simulateDirection(0,-1);
		renderFrames(30);
		// Now he should be jumping in lava
		Assert.assertEquals(MouvementZildo.SAUTE, zildo.getMouvement());
		while (MouvementZildo.VIDE != zildo.getMouvement()) {
			renderFrames(1);
		}
		// Check that falling is automatically ran
		checkScriptRunning("dieInPit");
	}
	
	
	// Before, when hero looking north hit a gard before him, the attacked guy
	// could be projected toward south, coming back on hero. That was absurd.
	@Test
	public void projectionNotOnAttacker() {
		mapUtils.loadMap("polakyg3");
		PersoPlayer zildo = spawnZildo(175, 130);
		persoUtils.removePerso("bleu");
		Perso gard = persoUtils.persoByName("bleu");
		gard.setQuel_deplacement(MouvementPerso.IMMOBILE, true);
		waitEndOfScripting();

		zildo.setAngle(Angle.NORD);
		zildo.setWeapon(new Item(ItemKind.SWORD));
		zildo.attack();
		renderFrames(10);
		Assert.assertTrue(gard.isWounded());
		Assert.assertTrue(gard.getPy() < 0);
	}
}
