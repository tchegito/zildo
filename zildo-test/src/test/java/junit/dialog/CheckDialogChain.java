package junit.dialog;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import tools.EngineUT;
import tools.annotations.InfoPersos;
import zildo.fwk.input.KeyboardHandler.Keys;
import zildo.monde.items.Item;
import zildo.monde.items.ItemKind;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.monde.util.Vector2f;
import zildo.server.EngineZildo;

/**
 * Another set of UT about found bugs, from Nash and Emile especially.
 * 
 * About Boris and dynamite quest.
 * 
 * @author Tchegito
 *
 */
public class CheckDialogChain extends EngineUT {

	/*
	 * Boris' dialog switch:
	 * oooDizzie:9,
	 * init:0,
	 * itemEMPTY_BAG&!itemFULL_BAG&!giveSawdust&cheapDynamite:5,
	 * itemFULL_BAG&!giveSawdust&cheapDynamite:6,
	 * giveSawdust&!borisWait:7,
	 * borisWait:8,
	 * igor_promise_sword:3,
	 * 0
	*/
	
	PersoPlayer zildo;
	
	private void initIgor() {
		mapUtils.loadMap("igorv4");
		zildo = spawnZildo(148, 119);
		waitEndOfScripting();
		zildo.getInventory().add(new Item(ItemKind.EMPTY_BAG));
	}

	@Test
	public void borisFirstTime() {
		initIgor();
		// Check init (he introduces himself, even if character has a bag)
		checkInit();
		// Talk again
		simulatePressButton(Keys.Q, 2);
		Assert.assertTrue(zildo.getDialoguingWith() != null);
		// still the same dialog because hero didn't meet Igor
		checkNextDialog(3, "igorv4.boris.2");
	}
	
	@Test
	public void borisAfterMetIgor() {
		initIgor();
		EngineZildo.scriptManagement.accomplishQuest("igor_promise_sword", false);
		checkInit();
		// Talk again
		simulatePressButton(Keys.Q, 2);
		Assert.assertTrue(zildo.getDialoguingWith() != null);
		// still the same dialog because hero didn't met Igor
		checkNextDialog(4, "igorv4.boris.3");
		simulatePressButton(Keys.Q, 2);	// Skip
		simulatePressButton(Keys.Q, 2);	// Go on next
		checkNextDialog(5, "igorv4.boris.4");
		Assert.assertTrue(EngineZildo.scriptManagement.isQuestDone("cheapDynamite"));
		goOnDialog();
		Assert.assertTrue(zildo.getDialoguingWith() == null);
		
		// Talk again
		simulatePressButton(Keys.Q, 2);
		checkNextDialog(6, "igorv4.boris.5");	// Sentence about the empty bag
		

	}
	
	// In any case, character should follow all of his 'init' dialog, if he meets the character for the first time
	// Igor has 3 sentences to say on 'init' phase
	private void checkInit() {
		simulatePressButton(Keys.Q, 2);
		renderFrames(5);
		checkNextDialog(1, "igorv4.boris.0");
		goOnDialog();
		Assert.assertTrue(zildo.getDialoguingWith() != null);
		checkNextDialog(2, "igorv4.boris.1");
		goOnDialog();
		Assert.assertTrue(zildo.getDialoguingWith() != null);
		checkNextDialog(3, "igorv4.boris.2");
		goOnDialog();
		// Dialog should be over
		Assert.assertTrue(zildo.getDialoguingWith() == null);
	}

	@Test
	public void prisonGard() {
		mapUtils.loadMap("prison");
		zildo = spawnZildo(221,72);
		waitEndOfScripting();
		visitCell("igor", new Vector2f(196, 72));
		visitCell("gp", new Vector2f(196, 214));
		visitCell("igor", new Vector2f(196, 72));
	}

	private void visitCell(String who, Vector2f nearCell) {
		
		Assert.assertNotNull(EngineZildo.persoManagement.collidePerso(221, 64, zildo, 4));

		simulatePressButton(Keys.Q, 2);
		renderFrames(5);
		checkNextDialog("prison.gard.0");
		goOnDialog();
		Assert.assertTrue(zildo.getDialoguingWith() == null);
		// Go toward the Igor's cell
		simulateDirection(0, 1);
		zildo.setPos(nearCell);
		renderFrames(2);
		// Check that scene has been triggered (hero's visiting Igor cell)
		simulateDirection(null);
		Assert.assertTrue(EngineZildo.scriptManagement.isScripting());
		Assert.assertTrue(EngineZildo.scriptManagement.isQuestProcessing("visit_"+who+"prison"));
		waitEndOfScriptingPassingDialog();
		checkNextDialog("prison.gard.script");	// Said during cutscene
		// Talk when guard is waiting
		int gardY = (int) EngineZildo.persoManagement.getNamedPerso("jaune").getY();
		zildo.setPos(new Vector2f(190, gardY+12));
		simulatePressButton(Keys.Q, 2);
		renderFrames(5);
		Assert.assertTrue(zildo.getDialoguingWith() != null);
		checkNextDialog("prison.gard.1");
		goOnDialog();

		// Move around for guard to close the cell
		simulateDirection(new Vector2f(1f,0.8f));
		zildo.setPos(new Vector2f(247, nearCell.y + 60));
		renderFrames(1);
		Assert.assertTrue(EngineZildo.scriptManagement.isScripting());
		Assert.assertTrue(EngineZildo.scriptManagement.isQuestProcessing("visit_"+who+"prison_over"));
		simulateDirection(null);
		waitEndOfScriptingPassingDialog();
		// Talk to gard
		Assert.assertTrue(zildo.getDialoguingWith() == null);
		zildo.setPos(new Vector2f(221, 72));
		zildo.setAngle(Angle.NORD);
		simulateDirection(0,-1);
		renderFrames(10);
		simulatePressButton(Keys.Q, 2);
		renderFrames(5);
		Assert.assertTrue(zildo.getDialoguingWith() != null);
		checkNextDialog("prison.gard.0");
		goOnDialog();
	}
}
