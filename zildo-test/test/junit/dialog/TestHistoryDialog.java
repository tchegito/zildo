package junit.dialog;

import java.util.List;

import junit.perso.EngineUT;

import org.junit.Assert;
import org.junit.Test;

import zildo.fwk.ZUtils;
import zildo.monde.Game;
import zildo.monde.dialog.HistoryRecord;
import zildo.monde.dialog.MapDialog;
import zildo.monde.quest.actions.ScriptAction;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoNJ;
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

	// TODO: Tester les phrases qui redirigent vers les autres
	// TODO: Check if we need to remove repeated sentences

	private void prepare() {
		spawnZildo(160, 100);
	}

	@Test
	public void archive() {
		prepare();

		// Make people talking
		String sentence = "hello";
		String who = "Roger";
		String who2 = "testcharacter";
		Perso perso = new PersoNJ();

		MapDialog mapDialog = EngineZildo.mapManagement.getCurrentMap().getMapDialog();
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
		prepare();
		
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
		prepare();
		String sentence = "key.0";
		for (int i=0;i<2;i++) {
			EngineZildo.dialogManagement.launchDialog(clientState, null, new ScriptAction(sentence, "roger"));
		}
		// Check that same sentence isn't recorded twice in succession
		assertDialog(1, null, null);
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