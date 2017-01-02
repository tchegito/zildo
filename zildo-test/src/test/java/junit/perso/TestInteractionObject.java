package junit.perso;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import tools.EngineUT;
import zildo.monde.dialog.WaitingDialog;
import zildo.monde.items.ItemKind;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.SpriteDescription;
import zildo.monde.sprites.desc.ZildoDescription;
import zildo.monde.sprites.elements.ElementGoodies;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.server.EngineZildo;

public class TestInteractionObject extends EngineUT {

	PersoPlayer zildo;
	
	/** Check when hero takes items that:
	 * -automatic dialog are well triggered
	 * -item are added into his inventory (if possible) **/
	@Test
	public void automaticBehavior() {
		zildo = spawnZildo(160, 100);
		// Very important: wait, because automatic behavior doesn't run without
		waitEndOfScripting();
		for (ItemKind kind : ItemKind.values()) {
			
			SpriteDescription desc = kind.representation;
			System.out.println(kind);
			boolean before = !(kind.isWeapon() || desc == ElementDescription.FLASK_RED || desc == ElementDescription.NECKLACE
					|| desc == ElementDescription.FLUT || desc != ZildoDescription.SHIELD_DOWN);
			boolean pick = pick(desc);
			// If automatic sentece is planned, check it
			if (kind.getFoundSentence("") != null) {
				Assert.assertNotNull(dialog(kind));
			}
			//Assert.assertTrue(kind+" should have return "+before+" !", before == pick);
			// Wait for end of dialog
			waitEndOfScriptingPassingDialog();
			if (kind.canBeInInventory()) {
				Assert.assertTrue(kind+" should have been in hero's inventory ! But he only has "+zildo.getInventory(), zildo.hasItem(kind));
			}
			// Remove item in hero's arms
			zildo.setEn_bras(null);
			EngineZildo.dialogManagement.resetQueue();
		}
	}
	
	/** Hero picks an item and return FALSE if he can get it into his inventory **/
	private boolean pick(SpriteDescription desc) {
		ElementGoodies goodie = new ElementGoodies();
		goodie.setDesc(desc);
		return zildo.pickGoodies(goodie, 0);
	}
	
	/** Get the pronounced sentence. Parameter is just for displaying error. **/
	private String dialog(ItemKind kind) {
		List<WaitingDialog> wds = EngineZildo.dialogManagement.getQueue();
		Assert.assertTrue("No automatic dialog was found for "+kind, wds.size() > 0);
		Assert.assertEquals("Only one sentence should have been launched !", 1, wds.size());
		String sentence = wds.get(0).sentence;
		System.out.println(sentence);
		return sentence; 
	}
}
