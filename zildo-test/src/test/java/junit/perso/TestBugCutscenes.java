package junit.perso;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import tools.EngineUT;
import zildo.fwk.input.KeyboardHandler.Keys;
import zildo.monde.dialog.HistoryRecord;
import zildo.monde.items.Item;
import zildo.monde.items.ItemKind;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Angle;
import zildo.monde.util.Vector2f;
import zildo.server.EngineZildo;

public class TestBugCutscenes extends EngineUT {

	@Test
	public void thiefAttack() {
		mapUtils.loadMap("voleurs");
		spawnZildo(900, 984);
		waitEndOfScripting();
		
		simulateDirection(new Vector2f(-2, 0));
	
		Assert.assertFalse(EngineZildo.scriptManagement.isQuestDone("attaque_voleurs"));
		renderFrames(200);
		Perso rouge = EngineZildo.persoManagement.getNamedPerso("rouge");
		assertNotBlocked(rouge);
		Assert.assertNull("Rouge should have reach his target ! He must be blocked by something.", rouge.getTarget());

		Assert.assertTrue(EngineZildo.scriptManagement.isQuestDone("attaque_voleurs"));
	}
	
	@Test
	public void freezeIgorCell_neverMet() {
		// Check scene when hero and Igor never met
		checkIgorSavingInPrison(12);
	}
	
	@Test
	public void freezeIgorCell_met() {
		// Check scene when hero and Igor already met (there's an "if" clause)
		EngineZildo.scriptManagement.accomplishQuest("igor_promise_sword", false);
		EngineZildo.scriptManagement.accomplishQuest("paid_guard", true);
		checkIgorSavingInPrison(9);
	}
	
	private void checkIgorSavingInPrison(int expectedDialogCount) {
		//EngineZildo.scriptManagement.accomplishQuest("prison(6, 2)", false);
		mapUtils.loadMap("prison7");
		Assert.assertFalse(EngineZildo.scriptManagement.isQuestDone("zildoAccessIgor"));
		waitEndOfScripting();

		// Put hero along the wall near the crack
		PersoPlayer zildo =spawnZildo(367+10, 163);
		// Blow the wall
		zildo.setWeapon(new Item(ItemKind.DYNAMITE));
		zildo.setCountBomb(4);
		zildo.attack();
		Assert.assertEquals(3,  zildo.getCountBomb());	// Be sure dynamite is planted
		Assert.assertEquals(148 + 256*3, EngineZildo.mapManagement.getCurrentMap().readmap(22, 10));
		renderFrames(130);
		Assert.assertNotEquals("Wall has not been blown !", 148 + 256*3, EngineZildo.mapManagement.getCurrentMap().readmap(22, 10));
		Assert.assertTrue(EngineZildo.scriptManagement.isQuestDone("prison7(22, 11)"));
		Assert.assertTrue(EngineZildo.scriptManagement.isQuestDone("prison(6, 2)"));

		// Controls no dialog history at the beginning
		List<HistoryRecord> records = EngineZildo.game.getLastDialog();
		Assert.assertEquals(0, records.size());

		// Go inside the hole and check we're on the right map
		simulateDirection(new Vector2f(1, 1));
		renderFrames(100);
		Assert.assertEquals("prison", EngineZildo.mapManagement.getCurrentMap().getName());
		
		Assert.assertTrue(EngineZildo.scriptManagement.isQuestDone("zildoAccessIgor"));

		// Pass the cutscene
		waitEndOfScriptingPassingDialog();
		
		// Control dialogs and script is over
		records = EngineZildo.game.getLastDialog();
		Assert.assertEquals(expectedDialogCount,  records.size());
		Assert.assertFalse(EngineZildo.scriptManagement.isScripting());
		Assert.assertEquals(Angle.SUD,  zildo.getAngle());
	}
	
	// Issue 83
	@Test
	public void checkForbiddenDynamitePlantInIgorCell() {
		mapUtils.loadMap("prison");
		PersoPlayer zildo = spawnZildo(104, 45);
		waitEndOfScripting();
		zildo.setWeapon(new Item(ItemKind.DYNAMITE));
		zildo.setCountBomb(4);
		zildo.attack();
		Assert.assertEquals("Player shouldn't be allowed to plant dynamite here !", 4,  zildo.getCountBomb());	// Be sure dynamite is planted
		renderFrames(2);
		Assert.assertEquals("Player should have been warned by a dialog !", 1, EngineZildo.game.getLastDialog().size());
		waitEndOfScriptingPassingDialog();
	}

	@Test
	public void checkForbiddenDynamitePlantInIgorCell2() {
		// Same but starts with another map before (that was a failed test when refactoring script executor processes)
		mapUtils.loadMap("prison2");
		waitEndOfScripting();
	}

	@Test
	public void checkAllowedDynamitePlanting() {
		// Check somewhere else
		waitEndOfScripting();
		mapUtils.loadMap("prison7");
		PersoPlayer zildo =spawnZildo(367+10, 163);
		zildo.setWeapon(new Item(ItemKind.DYNAMITE));
		zildo.setCountBomb(4);
		zildo.attack();
		Assert.assertEquals("Player should have been allowed to plant dynamite here !", 3,  zildo.getCountBomb());	// Be sure dynamite is planted
		renderFrames(2);
		Assert.assertEquals("No dialog should have popped !", 0, EngineZildo.game.getLastDialog().size());
		waitEndOfScriptingPassingDialog();
	}
	
	// At a given time, gard and hero was colliding so cutscene was blocked.
	@Test
	public void castlePolaky() {
		mapUtils.loadMap("polaky");
		Perso p = EngineZildo.persoManagement.getNamedPerso("g1");
		EngineZildo.spriteManagement.deleteSprite(p);
		p = EngineZildo.persoManagement.getNamedPerso("g2");
		EngineZildo.spriteManagement.deleteSprite(p);
		PersoPlayer hero = spawnZildo(615, 138);
		EngineZildo.scriptManagement.accomplishQuest("zildo_polaky_killguards", true);
		waitEndOfScripting();

		Assert.assertFalse(EngineZildo.scriptManagement.isScripting());

		simulateDirection(0, -1);
		renderFrames(80);
		Assert.assertTrue(EngineZildo.scriptManagement.isScripting());
		while (EngineZildo.scriptManagement.isScripting()) {
			simulatePressButton(Keys.Q, 2);
			renderFrames(1);
		}
		// Verify scene is over
		Assert.assertTrue(hero.y > 500);
	}
	
	@Test
	public void freezeVisitingPrisoners() {
		mapUtils.loadMap("prison");
		spawnZildo(213, 69);
		// Do as hero has already ask to visit prisoners
		EngineZildo.scriptManagement.accomplishQuest("ask_visitprison", false);
		waitEndOfScripting();
		simulateDirection(-1, 0);
		renderFrames(20);
		Assert.assertTrue(EngineZildo.scriptManagement.isScripting());
		waitEndOfScriptingPassingDialog();
	}
}
