package junit.save;

import java.util.List;

import junit.perso.EngineUT;

import org.junit.Assert;
import org.junit.Test;

import zildo.client.gui.menu.SaveGameMenu;
import zildo.fwk.file.EasyBuffering;
import zildo.fwk.input.KeyboardHandler.Keys;
import zildo.monde.Hasard;
import zildo.monde.items.Item;
import zildo.monde.items.ItemKind;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.elements.ElementGear;
import zildo.monde.sprites.elements.ElementGoodies;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Vector2f;
import zildo.server.EngineZildo;

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
	
	@Test
	public void freezeInLugduniaCave() {
		mapUtils.loadMap("foret");
		PersoPlayer zildo = spawnZildo(672,237);
		EngineZildo.scriptManagement.accomplishQuest("foretg_button_trig", false);
		simulateDirection(new Vector2f(0, -1f));
		renderFrames(100);
		
		waitEndOfScripting();
		Assert.assertEquals("foretg", EngineZildo.mapManagement.getCurrentMap().getName());
		Assert.assertFalse(zildo.isGhost());
	}
	
	@Test
	public void enemyDropItemNowhere() {
		// Enemy drop an item in a accessible case => we must have a goodies
		enemyDropItem(494, 273, true);
		// Enemy drop an item in a unaccessible case => we must not have a goodies
		enemyDropItem(547, 520, false);
	}
	
	private void enemyDropItem(int batX, int batY, boolean mustFound) {
		int start = mustFound ? 6 : 1;
		// If we must found, ensure that with at least one value (6) it works
		// But if we must not found, ensure that for EACH value, we found nothing
		for (int i=start;i<6;i++) {
			ElementGoodies goodies= enemyDieAndDropWithFakeHasard(batX, batY, i);

			Assert.assertEquals(mustFound, goodies != null);
		}
	}
	
	private ElementGoodies enemyDieAndDropWithFakeHasard(int batX, int batY, final int val) {
		EngineZildo.hasard = new Hasard() {
			@Override
			public int de6() {
				return val;
			}
		};
		mapUtils.loadMap("voleursg5");
		// Spawn Zildo at a random place, just to give him a necklace (otherwise, blue drop will never be dropped)
		PersoPlayer zildo = spawnZildo(101, 224);
		zildo.getInventory().add(new Item(ItemKind.NECKLACE));
		Perso bat = EngineZildo.persoManagement.getNamedPerso("new");
		// Place bat in a wall=blocked position
		bat.placeAt(batX, batY);
		bat.beingWounded(bat.x-5, bat.y, null, 2);
		renderFrames(50);
		// Ensure bat is dead
		Assert.assertTrue(bat.getPv() == 0);
		List<SpriteEntity> entities = EngineZildo.spriteManagement.getSpriteEntities(null);
		for (SpriteEntity entity : entities) {
			if (entity.isGoodies()) {
				System.out.println("Found ! "+entity);
				return (ElementGoodies) entity;
			}
		}
		return null;
	}
	
	@Test
	public void enemyDropItemWrongFloor() {
		// Check that a bat at floor 1 dying on a case where floor is at 0, drop an item on floor 0
		// So player can reach it
		ElementGoodies goodies = enemyDieAndDropWithFakeHasard(503, 358, 6);
		Assert.assertNotNull(goodies);
		Assert.assertEquals(0, goodies.getFloor());
	}
}
