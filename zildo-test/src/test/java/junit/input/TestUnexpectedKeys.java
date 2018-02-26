package junit.input;

import org.junit.Assert;
import org.junit.Test;

import tools.EngineWithMenuUT;
import tools.annotations.ClientMainLoop;
import zildo.client.Client;
import zildo.client.ClientEngineZildo;
import zildo.fwk.input.KeyboardHandler.Keys;
import zildo.fwk.ui.ItemMenu;
import zildo.fwk.ui.Menu;
import zildo.monde.items.Item;
import zildo.monde.items.ItemKind;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.resource.KeysConfiguration;

public class TestUnexpectedKeys extends EngineWithMenuUT {

	// Issue 114
	@Test
	public void stuckInInventory() {
		waitEndOfScripting();
		
		mapUtils.loadMap("cavef5");
		PersoPlayer hero = spawnZildo(255, 116);
		Item item = new Item(ItemKind.FIRE_RING);
		hero.getInventory().add(item);
		hero.setWeapon(item);
		
		while (hero.isInventoring()) {
			simulatePressButton(KeysConfiguration.PLAYERKEY_INVENTORY.code, 1);
		}
		simulatePressButton(KeysConfiguration.PLAYERKEY_INVENTORY.code, 1);
		Assert.assertFalse(hero.isInventoring());
	}
	
	@Test @ClientMainLoop
	public void doubleGames() {
		PersoPlayer hero = spawnZildo(0, 0);
		clientState.zildoId = hero.getId();
		// Game is already started, so we must quit
		simulatePressButton(Keys.ESCAPE, 2);
		Client client = ClientEngineZildo.getClientForMenu();
		Menu currentMenu = client.getCurrentMenu();
		Assert.assertNotNull(currentMenu);
		// Press "quit" button
		pickItemAndCheckDifferentMenu("m7.quit");
		// Confirm
		pickItemAndCheckDifferentMenu("global.yes");
		// Choose "single player" in StartMenu
		pickItemAndCheckDifferentMenu("m1.single");
		// Choose "load" in SinglePlayerMenu
		pickItemAndCheckDifferentMenu("m6.load");
		// Choose any saved game
		currentMenu = client.getCurrentMenu();
		ItemMenu save1 = currentMenu.items.get(0);
		ItemMenu save2 = currentMenu.items.get(1);
		// Load a game
		pickItem(save1.getKey());
		//client.setAction(save1);
		renderFrames(5);
		// Load a second one ? Check that no menu is displayed
		Assert.assertNull(client.getCurrentMenu());
		// Try to activate item anyway
		client.setAction(save2);
		// If we reach here, that means we hadn't any RuntimeException !
	}
}
