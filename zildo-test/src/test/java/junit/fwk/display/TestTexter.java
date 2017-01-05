package junit.fwk.display;

import java.lang.reflect.Field;

import org.junit.Assert;
import org.junit.Test;

import tools.EngineUT;
import zildo.client.ClientEngineZildo;
import zildo.client.gui.GUIDisplay;
import zildo.client.gui.GUISpriteSequence;
import zildo.monde.dialog.HistoryRecord;
import zildo.monde.sprites.SpriteEntity;
import zildo.server.EngineZildo;

public class TestTexter extends EngineUT {

	private void fillHistory(int amount) {
		String[] characters = new String[] {"Nanie", "Oli", "Bebe", "Eliette", "Canelle"};
		
		for (int i=0;i<amount;i++) {
			String character = characters[i % characters.length];
			String key = "preintro." + i;
			EngineZildo.game.recordDialog(key, character, "coucou");
		}
		
	}
	@Test
	public void dialogHistory() {
		fillHistory(50);
		String text = HistoryRecord.getDisplayString(EngineZildo.game.getLastDialog());
		System.out.println(text);
		GUIDisplay guiDisplay = ClientEngineZildo.guiDisplay;

		ClientEngineZildo.guiDisplay.displayTexter(text, 0);
		
		// Count sprites
		GUISpriteSequence seq = getPrivateMember(guiDisplay, "creditSequence");
		int nbSprites = seq.size();
		int nbVisible = countVisible(seq);
		System.out.println(nbVisible+"/"+nbSprites);

		// Assert that some of them are hidden
		Assert.assertTrue("All sprites shouldn't be visible !", nbVisible < nbSprites);
		Assert.assertTrue("Some sprites should be visible !", nbVisible > 0);
		
		// Move cursor and check visibility again
		ClientEngineZildo.guiDisplay.displayTexter(text, 16);
		nbSprites = seq.size();
		int nbVisibleAfter = countVisible(seq);
		System.out.println(nbVisibleAfter+"/"+nbSprites);
		Assert.assertTrue("We should have less visible sprites after a line scrolled down ! ("+nbVisibleAfter+" <= "+nbVisible+")",
				nbVisibleAfter < nbVisible);
		
	}
	
	// Test special characters sequence like "#n", "@" and "$sell" for merchant
	@Test
	public void marginal() {
		String[] sharpKeys = {"d4m8.vipere.0", "d4m8.vipere.1", "d5m1.gerard.0", "igorv.bilel.2", "igorv4.boris.8",
				"d4m6.ritou.1"};
		String character = "noone";
		for (String s : sharpKeys) {
			EngineZildo.game.recordDialog(s, character, "coucou");
		}
		String text = HistoryRecord.getDisplayString(EngineZildo.game.getLastDialog());

		GUIDisplay guiDisplay = ClientEngineZildo.guiDisplay;

		guiDisplay.displayTexter(text, 0);
		GUISpriteSequence seq = getPrivateMember(guiDisplay, "creditSequence");

		int countCharacters = 0;
		for (int i=0;i<text.length();i++) {
			char a = text.charAt(i);
			if (a != ' ') {
				countCharacters++;
			}
		}
		int nbVisible = countVisible(seq);
		int rapport = countCharacters / nbVisible;
		System.out.println(text);
		Assert.assertTrue(text.indexOf("#") == -1);
		Assert.assertTrue(text.indexOf("$") == -1);
		Assert.assertTrue(text.indexOf("@") == -1);
		System.out.println(nbVisible + "/" +countCharacters + " ==> " + rapport);
		Assert.assertTrue(rapport < 2);
	}

	private int countVisible(GUISpriteSequence seq) {
		int nbVisible = 0;
		for (SpriteEntity entity : seq) {
			if (entity.isVisible()) {
				nbVisible++;
			}
		}
		return nbVisible;
	}
	
	@SuppressWarnings("unchecked")
	private <T> T getPrivateMember(Object clazz, String fieldName) {
		Field field = null;
		try {
			field = clazz.getClass().getDeclaredField(fieldName); //NoSuchFieldException
		} catch (NoSuchFieldException e) {
			// If the given object is mocked, we deal with a CGLIB instance. So superclass should be our desired one.
			try {
				field = clazz.getClass().getSuperclass().getDeclaredField(fieldName); //NoSuchFieldException
			} catch (NoSuchFieldException e2) {
				throw new RuntimeException("Unable to find field "+fieldName, e2);
			}
		}
		T value = null;
		if (field != null) {
			try {
				field.setAccessible(true);
				value = (T) field.get(clazz); //IllegalAccessException
			} catch (Exception e) {
				throw new RuntimeException("Unable to access private member !", e);
			}
		}
		return value;
		
	}
}
