package junit.input;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.stubbing.Answer;

import tools.EngineWithMenuUT;
import tools.annotations.ClientMainLoop;
import zildo.Zildo;
import zildo.client.Client;
import zildo.client.ClientEngineZildo;
import zildo.client.PlatformDependentPlugin;
import zildo.client.gui.GUIDisplay;
import zildo.client.gui.GUIDisplay.DialogMode;
import zildo.client.gui.MenuTransitionProgress;
import zildo.client.stage.GameStage;
import zildo.client.stage.TexterStage;
import zildo.fwk.input.KeyboardHandler.Keys;
import zildo.fwk.ui.ItemMenu;
import zildo.fwk.ui.Menu;
import zildo.monde.items.Item;
import zildo.monde.items.ItemKind;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Angle;
import zildo.resource.KeysConfiguration;
import zildo.server.EngineZildo;

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
		waitEndOfScripting();
		clientState.zildoId = hero.getId();
		// Game is already started, so we must quit
		simulatePressButton(Keys.ESCAPE, 2);
		Client client = ClientEngineZildo.getClientForMenu();
		renderFrames(MenuTransitionProgress.BLOCKING_FRAMES_ON_MENU_INTERACTION);
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
		Assert.assertTrue("We should have at least a saved game in the right folder !", currentMenu.items.size() > 1);
		pickItem(save1.getKey());
		//client.setAction(save1);
		renderFrames(5 + MenuTransitionProgress.BLOCKING_FRAMES_ON_MENU_INTERACTION);
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
				field.setAccessible(true);
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
			renderFrames(MenuTransitionProgress.BLOCKING_FRAMES_ON_MENU_INTERACTION);
			Client client = ClientEngineZildo.getClientForMenu();
			Assert.assertNotNull(client.getCurrentMenu());
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
	
	/* We arrive with ingame menu + compass displayed
	key pressed:COMPASS at frame 22875
	key pressed:DOWN at frame 22892
	key pressed:DOWN at frame 22901
	key pressed:ESCAPE at frame 22934
	key pressed:COMPASS at frame 22937
	key pressed:RETURN at frame 22946
	key pressed:ESCAPE at frame 22947
	key pressed:COMPASS at frame 22947
	*/
	
	// Following test became obsolete with incoming of MenuTransitionProgress
	/*
	@Test @ClientMainLoop @DisableSpyGuiDisplay
	public void crashMenu() {

		
		// We need hero to get menu working
		PersoPlayer hero = spawnZildo(0, 0);
		waitEndOfScripting();
		clientState.zildoId = hero.getId();
		List<SpriteEntity> entities = EngineZildo.spriteManagement.getSpriteEntities(null);
		ClientEngineZildo.spriteDisplay.setEntities(entities);
		
		GUIDisplay gd = ClientEngineZildo.guiDisplay;
		gd.setToDisplay_dialogMode(DialogMode.CLASSIC);
		
		// Start the buggy sequence
		simulatePressButton(Keys.COMPASS, 1);
		renderFrames(15);
		Assert.assertNotNull(ClientEngineZildo.getClientForMenu().getCurrentMenu());
		Assert.assertEquals(DialogMode.ADVENTURE_MENU, gd.getToDisplay_dialogMode());
		simulatePressButton(Keys.DOWN, 2);
		simulatePressButton(Keys.DOWN, 2);

		simulatePressButton(Keys.ESCAPE, 2);
		simulatePressButton(Keys.COMPASS, 9);
		simulatePressButton(Keys.RETURN, 1);	// With 1 it's ok !

		System.out.println(stages());
		Assert.assertEquals(1,  stages().size());	// Menustage
		
		//renderFrames(1);
		System.out.println(stages());
		// BUG occurs because there's just one frame between the two actions below and above
		Assert.assertEquals(DialogMode.CLASSIC, gd.getToDisplay_dialogMode());

		System.out.println("two keys at the same time !");
		simulateKeyPressed(Keys.COMPASS, Keys.RETURN);

		renderFrames(1);
		simulateKeyPressed();
		renderFrames(1);

		Assert.assertEquals(1, stages().size());

	}
	*/

	@Test @ClientMainLoop
	public void bothCompassAndDialog() {
		Perso hero = spawnZildo(958, 275);
		mapUtils.loadMap("coucou");
		waitEndOfScripting();
		/*
		1) on fait face à un perso
		2) on rentre dans le menu Compass
		3) on laisse appuyer sur Q (touche pour parler)
		4) on rajoute RETURN
		*/
		
		GUIDisplay gd = ClientEngineZildo.guiDisplay;
		clientState.zildoId = hero.getId();
		List<SpriteEntity> entities = EngineZildo.spriteManagement.getSpriteEntities(null);
		ClientEngineZildo.spriteDisplay.setEntities(entities);
		hero.setAngle(Angle.NORD);	// Face the grandpa
		simulatePressButton(Keys.COMPASS, 1);
		renderFrames(MenuTransitionProgress.BLOCKING_FRAMES_ON_MENU_INTERACTION);
		Assert.assertEquals(DialogMode.ADVENTURE_MENU, gd.getToDisplay_dialogMode());
		Assert.assertNotNull(ClientEngineZildo.getClientForMenu().getCurrentMenu());
		
		// 1) typical case: player goes in "quest log menu"
		simulatePressButton(Keys.RETURN, 1);
		renderFrames(MenuTransitionProgress.BLOCKING_FRAMES_ON_MENU_INTERACTION+10);
		Assert.assertEquals(DialogMode.TEXTER, gd.getToDisplay_dialogMode());
		Assert.assertEquals(1, stages().size());
		Assert.assertEquals(TexterStage.class, stages().get(0).getClass());
		
		// Go out
		simulatePressButton(Keys.COMPASS, 1);
		Assert.assertNull(ClientEngineZildo.getClientForMenu().getCurrentMenu());
		renderFrames(MenuTransitionProgress.BLOCKING_FRAMES_ON_MENU_INTERACTION);
		Assert.assertEquals(0, stages().size());

		// then get back
		simulatePressButton(Keys.COMPASS, 1);
		renderFrames(MenuTransitionProgress.BLOCKING_FRAMES_ON_MENU_INTERACTION);
		Assert.assertEquals(DialogMode.ADVENTURE_MENU, gd.getToDisplay_dialogMode());
		
		// 2) marginal case: player hold Action, then go in "quest log"
		
		simulateKeyPressed(Keys.Q);
		renderFrames(10);
		simulateKeyPressed(Keys.Q, Keys.RETURN);
		renderFrames(5);
		simulateKeyPressed(Keys.Q);
		renderFrames(1);
		
		// Player shouldn't be talking with grandpa, but just inside quest log
		simulatePressButton(Keys.RETURN, 1);
		Assert.assertNull(hero.getDialoguingWith());
		Assert.assertEquals(1, stages().size());
		Assert.assertEquals(DialogMode.ADVENTURE_MENU, gd.getToDisplay_dialogMode());
	}
	
	private List<GameStage> stages() {
		return ClientEngineZildo.getClientForMenu().getCurrentStages();
	}
}
