package junit.sprites;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import tools.EngineUT;
import tools.annotations.ClientMainLoop;
import zildo.client.ClientEngineZildo;
import zildo.client.stage.GameStage;
import zildo.client.stage.TitleStage;
import zildo.fwk.input.KeyboardHandler.Keys;
import zildo.fwk.ui.Menu;
import zildo.monde.items.Item;
import zildo.monde.items.ItemKind;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.resource.KeysConfiguration;
import zildo.server.EngineZildo;

public class CheckOverflow extends EngineUT {

	SpriteDisplayMocked sd;

	String syntheScript = "<adventure>" +
						  "  <scene id='displayTestSynthe'>" +
						  "    <speak who='synthe' text='info.episode3'/>" +
						  "  </scene>" +
						  "</adventure>";
	
	@Before
	public void init() {
		sd = (SpriteDisplayMocked) ClientEngineZildo.spriteDisplay;
	}

	/** In Issue 109, player was capable to reach 500 max sprites in a bank. It was probably during a dialog + guide displayed,
	 * accessed from the compass menu. Now we decided to forbid compass menu during a dialog.
	 */
	@Test @ClientMainLoop
	public void spriteSorterOverflow_dialog() {
		mapUtils.loadMap("sousbois4");
		hero = spawnZildo(830, 136);
		ClientEngineZildo.mapDisplay.setCamera(new Point(690, 0));
		ClientEngineZildo.mapDisplay.setCurrentMap(EngineZildo.mapManagement.getCurrentMap());
		System.out.println(ClientEngineZildo.mapDisplay.getCamera());
		Item flut = new Item(ItemKind.FLUT);
		hero.getInventory().add(flut);
		hero.setWeapon(flut);
		hero.setAngle(Angle.EST);
		sd.setZildoId(hero.getId());
		EngineZildo.scriptManagement.accomplishQuest("hero_princess", false);
		waitEndOfScripting();
		
		displayMaxQuadOrder();
		
		// Talk to crystallion (check if he's right here)
		talkAndCheck("sousbois4.e3.0");
		// Talk again (to display dialog frame)
		simulatePressButton(Keys.Q, 2);	// Skip

		wait(240);
		displayMaxQuadOrder();
		Assert.assertTrue(clientState.dialogState.isDialoguing());
		
		// Enter in guide menu
		simulatePressButton(Keys.COMPASS, 2);	// Skip
		Menu menu = ClientEngineZildo.client.getCurrentMenu();
		// No ! Since issue 109, we decided that Compass menu was unreachable if player is dialoguing
		Assert.assertNull(menu);
		/*
		Assert.assertEquals(CompassMenu.class, menu.getClass());
		ClientEngineZildo.client.setAction(menu.getItemNamed("m13.guide"));
		wait(10);
		GameStage stage = ClientEngineZildo.getClientForGame().getCurrentStages().get(1);
		Assert.assertEquals(TexterStage.class, stage.getClass());
		TexterStage texterStage = (TexterStage) stage;
		simulateKeyPressed(Keys.DOWN);
		wait(30);
		displayMaxQuadOrder();
*/
	}

	/** Issue 137 : similar use case, where limit of 500 fonts is reached, during synthe displaying episode title,
	 * and player going into compass menu to read last dialogs, or guide.**/
	@Test @ClientMainLoop
	public void anotherOverflow_synthe() throws Exception {
		mapUtils.loadMap("sousbois1");
		hero = spawnZildo(830, 136);
		sd.setZildoId(hero.getId());
		loadXMLAsString(syntheScript);
		waitEndOfScripting();
		
		scriptMgmt.execute("displayTestSynthe",false);
		wait(10);
		
		// Ensure that synthe is displaying
		List<GameStage> stages = ClientEngineZildo.client.getCurrentStages();
		boolean found = false;
		for (GameStage gs : stages) {
			if (gs instanceof TitleStage) {
				found = true;
			}
		}
		Assert.assertTrue("We should have a TitleStage running !", found);
		
		// Ask for compass menu: this should be forbidden now
		simulatePressButton(KeysConfiguration.PLAYERKEY_ADVENTUREMENU.code, 1);
		Menu menu = ClientEngineZildo.client.getCurrentMenu();
		Assert.assertNull(menu);
		// Press ESCAPE and other touch leading to ingame menu: ingame menu should be forbidden
		Assert.assertNull(pressAndGetMenu(Keys.ESCAPE));
		Assert.assertNull(pressAndGetMenu(Keys.BACK));
		Assert.assertNull(pressAndGetMenu(Keys.TOUCH_BACK));
		Assert.assertNull(pressAndGetMenu(Keys.TOUCH_MENU));
		Assert.assertNull(pressAndGetMenu(Keys.DIALOG_FRAME));
	}
	
	/** Before, removed entities in server (SpriteManagement) weren't reported in client side (SpriteDisplay).
	 * To resolve that, we marked entity as 'dying' (already done) and remove them at the next frame. So as, SpriteDisplay could
	 * get the moment where entity need to be removed in its own data.
	 */
	@Test
	public void entitiesNotRemoved() {
		mapUtils.loadMap("ferme");
		hero = spawnZildo(459, 373);
		waitEndOfScripting();
		
		sd.setZildoId(hero.getId());
		
		renderFrameDisplayingSprite(1);
		
		// There is a delta between client and server entities: entities with ID=-1 in the GUI
		int delta = deltaEntities();
		
		EngineZildo.mapManagement.getCurrentMap().attackTile(1, new Point(30, 25), hero);
		EngineZildo.mapManagement.getCurrentMap().attackTile(1, new Point(31, 25), hero);
		EngineZildo.mapManagement.getCurrentMap().attackTile(1, new Point(30, 26), hero);
		EngineZildo.mapManagement.getCurrentMap().attackTile(1, new Point(31, 26), hero);
		
		renderFrameDisplayingSprite(400);

		// Check that there isn't any 'died' entity in SpriteDisplay (client render)
		for (SpriteEntity entity : sd.getEntities()) {
			Assert.assertFalse("It shouldn't be any died entity in client rendering entities !", entity.dying);
		}
		
		Assert.assertEquals(delta,  deltaEntities());
	}
	
	private int deltaEntities() {
		return sd.getEntities().size() - EngineZildo.spriteManagement.getSpriteEntities(null).size();
	}
	
	private PersoPlayer hero;
	
	private void renderFrameDisplayingSprite(int nbFrames) {
		for (int i=0;i<nbFrames;i++) {
			sd.setEntities(EngineZildo.spriteManagement.getSpriteEntities(null));
			sd.setZildoId(hero.getId());
			renderFrames(1);
	
			//System.out.println("sizeServer=" + EngineZildo.spriteManagement.getSpriteEntities(null).size());
			//System.out.println("sizeClient=" + sd.getEntities().size());
		}
	}
	
	private Menu pressAndGetMenu(Keys key) {
		simulatePressButton(key, 1);
		return ClientEngineZildo.client.getCurrentMenu();		
	}
	
	private void wait(int nFrames) {
		for (int i = 0; i < nFrames; i++) {
			sd.setEntities(EngineZildo.spriteManagement.getSpriteEntities(null));
			// Stages render is done in platform specific modules
			for (GameStage stage : ClientEngineZildo.getClientForGame().getCurrentStages()) {
				stage.renderGame();
			}
			renderFrames(1);
		}
	}

	private void displayMaxQuadOrder() {

		int[][] qo = sd.getQuadOrder();
		for (int i = 0; i < qo.length; i++) {
			int size = 0;
			for (int j = 0; j < qo[i].length; j++) {
				if (qo[i][j] == -1)
					break;
				size++;
			}
			if (size != 0)
				System.out.println("size[" + i + "]=" + size);
		}
	}
}
