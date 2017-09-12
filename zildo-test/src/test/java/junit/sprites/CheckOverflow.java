package junit.sprites;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import tools.EngineUT;
import tools.annotations.ClientMainLoop;
import zildo.client.ClientEngineZildo;
import zildo.client.stage.GameStage;
import zildo.fwk.input.KeyboardHandler.Keys;
import zildo.fwk.ui.Menu;
import zildo.monde.items.Item;
import zildo.monde.items.ItemKind;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.server.EngineZildo;

public class CheckOverflow extends EngineUT {

	SpriteDisplayMocked sd;

	@Before
	public void init() {
		sd = (SpriteDisplayMocked) ClientEngineZildo.spriteDisplay;
	}

	/** In ISsue 109, player was capable to reach 500 max sprites in a bank. It was probably during a dialog + guide displayed,
	 * accessed from the compass menu. Now we decided to forbid compass menu during a dialog.
	 */
	@Test @ClientMainLoop
	public void spriteSorterOverflow() {
		mapUtils.loadMap("sousbois4");
		PersoPlayer hero = spawnZildo(830, 136);
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
