package junit.input;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.stubbing.Answer;

import tools.EngineWithMenuUT;
import tools.annotations.ClientMainLoop;
import zildo.Zildo;
import zildo.client.Client;
import zildo.client.ClientEngineZildo;
import zildo.client.PlatformDependentPlugin;
import zildo.fwk.input.KeyboardHandler.Keys;
import zildo.fwk.ui.ItemMenu;
import zildo.fwk.ui.Menu;
import zildo.monde.items.Item;
import zildo.monde.items.ItemKind;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.resource.KeysConfiguration;

public class TestUnexpectedKeys extends EngineWithMenuUT {

	// Issue 119
	@Test
	public void stuckInInventory() {
		waitEndOfScripting();
		
		mapUtils.loadMap("cavef5");
		PersoPlayer hero = spawnZildo(255, 116);
		Item item = new Item(ItemKind.FIRE_RING);
		hero.getInventory().add(item);
		hero.setWeapon(item);

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
	
	@Test @ClientMainLoop
	public void navigateToIllegalPage() throws IllegalAccessException {
		Field field = null;
		PlatformDependentPlugin notMocked = Zildo.pdPlugin;

		try {
			// GIVEN
			PlatformDependentPlugin pdp = org.mockito.Mockito.spy(Zildo.pdPlugin);
			doAnswer(new Answer<File[]>() {
				// Simulate 11 savegames
				public File[] answer(org.mockito.invocation.InvocationOnMock invocation) throws Throwable {
					File[] files = new File[11];
					Arrays.fill(files, new File("1 coucou"));
					return files;
				};
			}).when(pdp).listFiles(anyString(), any(FilenameFilter.class));
			
			// Unbelievable here ! We are going to set a static final member !
			// In fact, that is preferrable than removing final keyword in given class. But don't tell anyone.
	
			try {
				field = Zildo.class.getDeclaredField("pdPlugin");
				// Allow modification on the field
				Field modifiersField = Field.class.getDeclaredField("modifiers");
				modifiersField.setAccessible(true);
				modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
	
				field.set(null, pdp);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			
			PersoPlayer hero = spawnZildo(0, 0);
			waitEndOfScripting();
			clientState.zildoId = hero.getId();
			// Game is started => get in savegame menu
			simulatePressButton(Keys.ESCAPE, 2); 
			Client client = ClientEngineZildo.getClientForMenu();
			pickItem("m7.save");
			renderFrames(2);
	
			Menu currentMenu = client.getCurrentMenu();
			Assert.assertNotNull(currentMenu);
			ItemMenu nextButton = currentMenu.items.get(10);
			// Click on next
			pickItem(nextButton.getKey());
			// Then click again (this should be impossible)
			client.setAction(nextButton);
			renderFrames(2);
			
			// Now, the same with 'previous' button
			ItemMenu prevButton = currentMenu.items.get(2);
			Assert.assertEquals("global.prec", prevButton.getKey()  );
			pickItem(prevButton.getKey());
			// Then click again (this should be impossible)
			client.setAction(prevButton);
			renderFrames(2);
		} finally {
			if (field != null) {
				// Get back to a non-mocked object, to avoid huge memory leak !
				field.set(null, notMocked);
			}
		}
	}
}
