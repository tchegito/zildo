package junit.fwk.display;

import java.lang.reflect.Field;

import junit.perso.EngineUT;

import org.junit.Assert;
import org.junit.Test;

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
			String character = characters[(int) (Math.random() * characters.length)];
			String key = "preintro." + (int) (Math.random() * 10);
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
		// Assert that some of them are hidden

		System.out.println(nbVisible+"/"+nbSprites);
		Assert.assertTrue("All sprites shouldn't be visible !", nbVisible < nbSprites);
		Assert.assertTrue("Some sprites should be visible !", nbVisible > 0);
		
		// Move cursor and check visibility again
		ClientEngineZildo.guiDisplay.displayTexter(text, 16);
		Assert.assertTrue("We should have more visible sprites after a line scrolled down !", countVisible(seq) > nbVisible);
		System.out.println(countVisible(seq));
		
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
