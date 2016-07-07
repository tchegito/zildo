package junit.area;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import tools.EngineUT;
import zildo.fwk.input.KeyboardHandler.Keys;
import zildo.monde.dialog.HistoryRecord;
import zildo.monde.items.Item;
import zildo.monde.items.ItemKind;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.server.EngineZildo;

/**
 * Another set of UT about found bugs, from Nash and Emile especially.
 * 
 * @author Tchegito
 *
 */
public class CheckFoundBugs2 extends EngineUT {
	
	PersoPlayer zildo;
	
	@Before
	public void init() {
		mapUtils.loadMap("igorv4");
		zildo = spawnZildo(148, 119);
		waitEndOfScripting();
		zildo.getInventory().add(new Item(ItemKind.EMPTY_BAG));
	}

	@Test
	public void borisFirstTime() {
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
		EngineZildo.scriptManagement.accomplishQuest("igorAsk", false);
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

}
