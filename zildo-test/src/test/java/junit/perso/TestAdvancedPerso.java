package junit.perso;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import tools.EngineUT;
import zildo.fwk.input.KeyboardHandler.Keys;
import zildo.fwk.script.context.SceneContext;
import zildo.monde.items.Item;
import zildo.monde.items.ItemKind;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.persos.ControllablePerso;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.sprites.utils.MouvementPerso;
import zildo.monde.sprites.utils.MouvementZildo;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.monde.util.Pointf;
import zildo.monde.util.Vector2f;
import zildo.monde.util.Zone;
import zildo.resource.KeysConfiguration;
import zildo.server.EngineZildo;

public class TestAdvancedPerso extends EngineUT {

	String guyScript="<adventure>"+
			" <scene id='testRemoveScript'>"+
			"  <perso who='thrower1' action=''/>" +
			" </scene>"+
			" <scene id='testChangeScript'>"+
			"  <perso who='thrower1' action='immobileDarkGuy'/>" +
			" </scene>"+
			"</adventure>";
	
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
		zildo.walkTile(false);
		waitForScriptRunning("dragonDiveAndReappear");
		
		zildo.beingWounded(4, 4, dragon, zildo.getPv());
		Assert.assertEquals(0, zildo.getPv());
		waitForScriptRunning("death");
		renderFrames(250);
	}
	
	@Test
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
	
	// Hero moves to the NORTH and jump
	@Test
	public void moveAndJump() {
		mapUtils.loadMap("foret");
		PersoPlayer zildo = spawnZildo(700, 376);
		zildo.setTarget(new Pointf(700, 280));
		zildo.setGhost(true);
		renderFrames(60);
		
		Assert.assertTrue(zildo.getY() < 350);
	}
	
	// Bug from Trish => scene about hero's dream was blocked, because hero couldn't jump out of his bed
	@Test
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
	
	// Hero is in his bed and jump on the EAST
	@Test
	public void jumpFromBed() {
		mapUtils.loadMap("d4m12");
		spawnZildo(160,100);	// No matter where => he will be replaced in the script
		EngineZildo.scriptManagement.execute("zildoDreams", true);
		waitEndOfScriptingPassingDialog();
	}
	
	@Test
	public void attackWithoutWeapon() {
		PersoPlayer hero = spawnZildo(522, 636);
		Item item = new Item(ItemKind.FLASK_RED);
		hero.setPv(hero.getMaxpv() - 1);
		hero.getInventory().add(item);
		hero.setWeapon(item);
		hero.attack();
	}
	
	@Test
	public void attackAndSwitchWeapon() {
		PersoPlayer hero = spawnZildo(522, 636);
		Item item = new Item(ItemKind.FLASK_RED);
		Item item2 = new Item(ItemKind.NECKLACE);
		hero.setPv(hero.getMaxpv() - 1);
		hero.getInventory().addAll(Arrays.asList(item, item2));
		hero.setWeapon(item);
		hero.attack();
		Assert.assertEquals(ItemKind.NECKLACE,  hero.getWeapon().kind);
		hero.attack();
	}
	
	// Issue 130: #1
	@Test
	public void suckedInPitThenRespawn() {
		mapUtils.loadMap("cavef6");
		PersoPlayer hero = spawnZildo(203, 124);
		int startPv = hero.getPv();
		waitEndOfScripting();
		// Back up game, then set hero just in front of fire elemental
		EngineZildo.backUpGame();
		EngineZildo.mapManagement.setStartLocation(mapUtils.area.getName(), new Point(203,124), Angle.NORD, 1);

		SpriteEntity plateform = EngineZildo.spriteManagement.getNamedEntity("platef1");
		plateform.x=318f; plateform.y= 278f;
		hero.setPos(new Vector2f(318, 278));
		hero.walkTile(false);
		// Wait for hero to fall in lava, sucked out by the elemental
		waitForScriptRunning("fallPit");
		Assert.assertTrue(hero.vx != 0);
		// Wait hero being respawned
		waitEndOfScripting();
		
		Assert.assertEquals(startPv-1, hero.getPv());
		Assert.assertTrue(hero.vx == 0);
	}
	
	/** Issue 108: NPE when player was dialoguing, then hit, then he presses ACTION **/
	@Test
	public void npeInDialog() {
		EngineZildo.scriptManagement.accomplishQuest("foretg_apres_grotte",false);
		mapUtils.loadMap("bosquet");
		PersoPlayer hero = spawnZildo(92, 370);
		waitEndOfScripting();
		
		Assert.assertNotNull(persoUtils.persoByName("maltus"));
		hero.setAngle(Angle.SUD);
		talkAndCheck("bosquet.maltus.2", false);
		Assert.assertTrue(hero.getDialoguingWith() != null);
		persoUtils.persoByName("g1").setAlerte(true);
		while (!hero.isWounded()) {
			renderFrames(1);
		}
		System.out.println(persoUtils.persoByName("g1"));
		System.out.println(hero.getPv());
		Assert.assertTrue(EngineZildo.scriptManagement.isScripting());
		// Wait for hero to be hitten
		while (hero.getPv() != 5) {
			renderFrames(1);
		}
		Assert.assertTrue(hero.getDialoguingWith() == null);
		Assert.assertTrue(EngineZildo.scriptManagement.isScripting());
		// Before, we had an NPE just after the line under
		simulatePressButton(KeysConfiguration.PLAYERKEY_ACTION.code, 2);
	}
	
	@Test
	public void turtleFollowHisRoute() {
		mapUtils.loadMap("sousbois7");
		PersoPlayer roxy = spawnZildo(116, 430);
		roxy.setAppearance(ControllablePerso.PRINCESS_BUNNY);
		waitEndOfScripting();
		simulateDirection(-1, 0);
		renderFrames(80);
		Assert.assertEquals(0, (int) roxy.z);
		simulatePressButton(Keys.W, 4);
		Assert.assertTrue(roxy.z>0);
		renderFrames(50);
		Assert.assertTrue("Roxy should have been on platform !", roxy.isOnPlatform());
		Perso turtle = persoUtils.persoByName("sacher");
		Assert.assertFalse(turtle.isAlerte());
	}
	
	@Test
	public void persoActionRemove() throws Exception {
		Perso guy1 = preparePersoAction();
		
		scriptMgmt.execute("testRemoveScript", true, new SceneContext(), null);
		renderFrames(10);
		Assert.assertFalse(guy1.isDoingAction());
	}
	
	@Test
	public void persoActionSwitch() throws Exception {
		Perso guy1 = preparePersoAction();
		
		scriptMgmt.execute("testChangeScript", true, new SceneContext(), null);
		renderFrames(10);
		Assert.assertTrue(guy1.isDoingAction());
		
		// Check that character isn't moving anymore
		int nbFrames = 100;
		while (nbFrames-- > 0 && guy1.getTarget() == null) {
			renderFrames(1);
		}
		Assert.assertNull("Character shouldn't move !", guy1.getTarget());
	}
	
	@Test
	public void persoActionWhenDead() throws Exception {
		preparePersoAction();
		
		// Kill him
		persoUtils.removePerso("thrower1");
		Assert.assertNull(persoUtils.persoByName("thrower1"));

		// Ensure that script won't fail even if the character isn't there
		scriptMgmt.execute("testChangeScript", true, new SceneContext(), null);
		renderFrames(2);
	}
	
	private Perso preparePersoAction() throws Exception {
		loadXMLAsString(guyScript);
		EngineZildo.scriptManagement.accomplishQuest("findDragonPortalKey", false);
		mapUtils.loadMap("prisonext");
		spawnZildo(140, 144);
		waitEndOfScriptingPassingDialog();
		
		Perso guy1 = persoUtils.persoByName("thrower1");
		Assert.assertNotNull(guy1);
		Assert.assertTrue(guy1.isDoingAction());
		
		return guy1;
	}
}
