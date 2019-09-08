package junit.dialog;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import tools.EngineUT;
import zildo.fwk.ZUtils;
import zildo.fwk.file.EasyBuffering;
import zildo.fwk.ui.UIText;
import zildo.monde.Game;
import zildo.monde.dialog.HistoryRecord;
import zildo.monde.dialog.MapDialog;
import zildo.monde.quest.actions.ScriptAction;
import zildo.monde.sprites.persos.ControllablePerso;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoNJ;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.resource.Constantes;
import zildo.server.EngineZildo;

/**
 * Class which tests the History Dialog functionality. It means archiving a
 * limited number of dialogs, and retrieving them in the current {@link Game}
 * object.
 * 
 * @author eboussaton
 *
 */
public class TestHistoryDialog extends EngineUT {

	PersoPlayer hero;
	
	@Before
	public void prepare() {
		// Load any map but 'preintro', because we filter dialog on this one
		mapUtils.loadMap("coucou");
		hero = spawnZildo(160, 100);
	}

	@Test
	public void archive() {
		// Make people talking
		String sentence = "hello";
		String who = "Roger";
		String who2 = "testcharacter";
		Perso perso = new PersoNJ();

		MapDialog mapDialog = EngineZildo.mapManagement.getCurrentMap().getMapDialog();
		mapDialog.getDialogs().clear();
		mapDialog.addBehavior(who2, new short[] { 1, 2, 3 });
		mapDialog.addSentence("preintro.0");

		perso.setName(who2);

		// Check record
		// 1) Without perso : script case
		EngineZildo.dialogManagement.launchDialog(clientState, null, new ScriptAction(sentence, who));
		assertDialog(1, who, sentence);

		// 2) With perso : typical in-game case
		EngineZildo.dialogManagement.launchDialog(clientState, perso, null);
		assertDialog(2, who2, "preintro.0");
		/*
		 * // 3) Without perso (sign for example) : we don't want to record this
		 * EngineZildo.dialogManagement.launchDialog(clientState, null, new
		 * ScriptAction(sentence, "sign")); Assert.assertEquals(2,
		 * lastDialogs.size());
		 */
	}

	@Test
	public void overflow() {
		// Simulate a lot of talk (exceeds the limit)
		String sentence = "key.";
		for (int i=0;i<Constantes.NB_MAX_DIALOGS_HISTORY*2;i++) {
	        EngineZildo.dialogManagement.launchDialog(clientState, null, new ScriptAction(sentence + i, "roger"));
		}
		// Check that history hasn't overflowed
		assertDialog(Constantes.NB_MAX_DIALOGS_HISTORY, null, null);
	}
	
	@Test
	public void repeat() {
		String sentence = "key.0";
		for (int i=0;i<2;i++) {
			EngineZildo.dialogManagement.launchDialog(clientState, null, new ScriptAction(sentence, "roger"));
		}
		// Check that same sentence isn't recorded twice in succession
		assertDialog(1, null, null);
	}
	
	/** Check that hero's name change according to his appearance **/
	@Test
	public void rightPersoTalking() {
		String sentence = "key.0";
		UIText.setCharacterName("hero");	// Change default name
		EngineZildo.dialogManagement.launchDialog(clientState, null, new ScriptAction(sentence, "zildo"));
		assertDialog(1, "hero", sentence);
    	hero.setAppearance(ControllablePerso.PRINCESS_BUNNY);
		EngineZildo.dialogManagement.launchDialog(clientState, null, new ScriptAction(sentence, "zildo"));
		assertDialog(2, "roxy", sentence);
    	hero.setAppearance(ControllablePerso.ZILDO);
		EngineZildo.dialogManagement.launchDialog(clientState, null, new ScriptAction(sentence, "zildo"));
		assertDialog(3, "hero", sentence);
	}
	
	@Test
	public void encoding() {
		HistoryRecord record = new HistoryRecord("key",  "Grand-père", "wherever");
		EasyBuffering buffer = new EasyBuffering(150); 
		record.serialize(buffer);
		buffer.getAll().flip();
		
		HistoryRecord read = HistoryRecord.deserialize(buffer);
		Assert.assertEquals(record.who, read.who);
	}
	
	/**
	 * Asserts that dialog history's size is as expected, and performs checks on
	 * most recent record
	 **/
	private void assertDialog(int size, String who, String key) {
		List<HistoryRecord> lastDialogs = EngineZildo.game.getLastDialog();
		Assert.assertEquals(size, lastDialogs.size());

		HistoryRecord record = lastDialogs.get(size - 1);
		if (who != null || key != null) {
			Assert.assertEquals(ZUtils.capitalize(who), record.who);
			Assert.assertEquals(key, record.key);
		}
	}
}