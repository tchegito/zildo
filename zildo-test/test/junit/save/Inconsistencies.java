package junit.save;

import junit.perso.EngineUT;

import org.junit.Assert;
import org.junit.Test;

import zildo.client.gui.menu.SaveGameMenu;
import zildo.fwk.file.EasyBuffering;
import zildo.fwk.input.KeyboardHandler.Keys;
import zildo.monde.Game;
import zildo.monde.quest.actions.ScriptAction;
import zildo.monde.sprites.elements.ElementGear;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Vector2f;
import zildo.server.EngineZildo;
import zildo.server.state.ClientState;

public class Inconsistencies extends EngineUT {

	@Test
	public void lockedInCell() {
		mapUtils.loadMap("prison");
		PersoPlayer zildo = spawnZildo(223,105);
		simulateDirection(new Vector2f(0, -1));
		waitEndOfScripting();
		renderFrames(5);
		
		// Walk to guard
		while (zildo.deltaMoveY != 0) {
			renderFrames(5);
		}
		// Talk to him
		simulatePressButton(Keys.Q, 1);
		// Ensure dialog is started, and with the right man
		Assert.assertTrue(EngineZildo.game.getLastDialog().size() == 1);
		Perso guard = zildo.getDialoguingWith();
		Assert.assertEquals("jaune", guard.getName());
		renderFrames(10);
		simulatePressButton(Keys.Q, 40);
		// Go on dialog
		simulatePressButton(Keys.Q, 40);
		// Be sure dialog is over
		Assert.assertTrue(zildo.getDialoguingWith() == null);

		ElementGear igorDoor = (ElementGear) EngineZildo.spriteManagement.getNamedElement("igordoor1");
		Assert.assertFalse(igorDoor.isOpen());
		
		// Go to Igor's cell, and wait for guard stand still
		simulateDirection(new Vector2f(-1,0.01f));
		renderFrames(40);
		simulateDirection(new Vector2f(0, 0));
		while (guard.deltaMoveX != 0 || guard.deltaMoveY != 0) {
			renderFrames(5);
		}
		// Check door is open
		Assert.assertTrue(igorDoor.isOpen());
		// Wait for guard returning to his location, and his dialog over
		while (!clientState.dialogState.dialoguing) {
			renderFrames(5);
		}
		simulatePressButton(Keys.Q, 5);
		simulatePressButton(Keys.Q, 40);
		while (clientState.dialogState.dialoguing) {
			renderFrames(5);
		}

		// Ask zildo to go in the cell
		waitEndOfScripting();
		simulateDirection(new Vector2f(-0.5f, -0.5f));
		renderFrames(100);
		// Ensure he is inside
		Assert.assertTrue(zildo.y < 127);
		
		// Save the game and reload
		EasyBuffering buffer = new EasyBuffering(5000);
		EngineZildo.game.serialize(buffer);
		// 2) Reload this game
    	SaveGameMenu.loadGameFromBuffer(buffer, false);
		
		// Check that door is opened
		igorDoor = (ElementGear) EngineZildo.spriteManagement.getNamedElement("igordoor1");
		waitEndOfScripting();
		renderFrames(30);
		Assert.assertTrue(igorDoor.isOpen());
	}
}
