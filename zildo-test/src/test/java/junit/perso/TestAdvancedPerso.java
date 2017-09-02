package junit.perso;

import org.junit.Assert;
import org.junit.Test;

import tools.EngineUT;
import tools.annotations.InfoPersos;
import zildo.monde.items.Item;
import zildo.monde.items.ItemKind;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.sprites.utils.MouvementPerso;
import zildo.monde.sprites.utils.MouvementZildo;
import zildo.monde.util.Point;
import zildo.monde.util.Zone;
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
		EngineZildo.scriptManagement.runPersoAction(dragon, "bossDragon", null, false);
		
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
	
	/** During Episode 3 tests, we saw that hero can move during he's playing flut => that leads to blocking behavior **/
	@Test
	public void movingDuringFlut() {
		PersoPlayer zildo = spawnZildo(861,214);
		waitEndOfScripting();
		Item flut = new Item(ItemKind.FLUT);
		zildo.getInventory().add(flut);
		zildo.setWeapon(flut);
		zildo.attack();
		renderFrames(10);
		
		// Check that he's playing flut
		Assert.assertEquals(MouvementZildo.PLAYING_FLUT, zildo.getMouvement());
		float sameX = zildo.x;
		System.out.println(zildo.x);
		// Ask him to move: he should be busy by his flut playing
		simulateDirection(1, 0);
		renderFrames(5);
		Assert.assertEquals(sameX, zildo.x, 0.1);
	}
	
	/** during same campaign, we saw that a black guard holding a bow was switching to sword when wounded **/
	@Test
	public void bowGardWoundedSwitchWeapon() {
		
		Perso bowGuard = spawnPerso(PersoDescription.GARDE_CANARD, "noir", 160, 100);
		bowGuard.setQuel_deplacement(MouvementPerso.ZONEARC, true);
		bowGuard.setZone_deplacement(new Zone());
		bowGuard.initPersoFX();
		
		Perso zildo = spawnZildo(100, 100);
		waitEndOfScripting();
	
		Assert.assertEquals(ItemKind.BOW, bowGuard.getWeapon().kind);
		bowGuard.beingWounded(bowGuard.x+2, bowGuard.y, zildo, 2);
		while (bowGuard.isWounded()) {
			renderFrames(1);
		}
		renderFrames(5);
		System.out.println(bowGuard.getQuel_deplacement());
		Assert.assertEquals(ItemKind.BOW, bowGuard.getWeapon().kind);
		Assert.assertEquals(MouvementPerso.ZONEARC, bowGuard.getQuel_deplacement());
		
	}
	
	@Test
	public void moveAndJump() {
		mapUtils.loadMap("foret");
		PersoPlayer zildo = spawnZildo(700, 376);
		zildo.setTarget(new Point(700, 280));
		zildo.setGhost(true);
		renderFrames(60);
		
		Assert.assertTrue(zildo.getY() < 350);
	}
	
	@Test @InfoPersos
	public void moveAndStop() {
		mapUtils.loadMap("sousbois4");
		EngineZildo.scriptManagement.accomplishQuest("hero_princess", false);
		PersoPlayer zildo = spawnZildo(302, 714);
		waitEndOfScripting();
		EngineZildo.backUpGame();
		int startPv = zildo.getPv();
		simulateDirection(0, -1);
		renderFrames(20);
		waitEndOfScriptingPassingDialog();
		Assert.assertEquals("Hero shouldn't have during this cutscene !", startPv, zildo.getPv());
	}
}
