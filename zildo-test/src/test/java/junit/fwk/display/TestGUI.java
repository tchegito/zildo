package junit.fwk.display;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import tools.EngineUT;
import tools.annotations.ClientMainLoop;
import tools.annotations.DisableSpyGuiDisplay;
import zildo.client.ClientEngineZildo;
import zildo.client.gui.GUIDisplay;
import zildo.client.gui.GUIDisplay.DialogMode;
import zildo.fwk.input.KeyboardHandler.Keys;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Pointf;
import zildo.server.EngineZildo;

public class TestGUI extends EngineUT {

	/** Before, a GUI blink was observed when a character was moved in PathFinder#collide. That is not right. **/ 
	@Test
	public void dontBlink() {
		mapUtils.loadMap("d4m8");
		waitEndOfScripting();
		
		Perso character = spawnTypicalPerso("walker",135, 111);
		character.setTarget(new Pointf(72, 111));
		character.setGhost(true);
		while (character.getTarget() != null) {
			renderFrames(1);
			Assert.assertTrue("GUI shouldn't have blinked during the character's move !", ClientEngineZildo.guiDisplay.isToDisplay_generalGui());
		}
		
	}
	
	/** Issue: 183 **/
	@Test
	@DisableSpyGuiDisplay @ClientMainLoop
	public void arrayIndexOutOfBounds() {
		/*
		1) call GUIDisplay#draw avec toDisplay_dialogMode en mode TEXTER
		 ==> frameDialogSequence.isDrawn should be TRUE
		2) call drawFrame
		 ==> exception
		 */
		
		GUIDisplay gd = ClientEngineZildo.guiDisplay;
		PersoPlayer hero = spawnZildo(160, 100);
		clientState.zildoId = hero.getId();
		List<SpriteEntity> entities = EngineZildo.spriteManagement.getSpriteEntities(null);
		ClientEngineZildo.spriteDisplay.setEntities(entities);
		ClientEngineZildo.spriteDisplay.setZildoId(hero.getId());
		waitEndOfScripting();
		/*
		// To set display_dialoguing to text, either call this
		gd.displayTexter("coucou", 0);	// For example during dialog history
		// Or call this
		//gd.setText("coucou", DialogMode.TEXTER);
		gd.setToRemove_dialoguing(true);
		gd.draw(false);
		gd.setToDisplay_dialoguing(true);
		gd.draw(true);
		*/
		
		Assert.assertEquals(null, gd.getToDisplay_dialogMode());
		simulatePressButton(Keys.COMPASS, 1);
		Assert.assertEquals(DialogMode.ADVENTURE_MENU, gd.getToDisplay_dialogMode());
		simulatePressButton(Keys.RETURN, 1);
		//Assert.assertEquals(DialogMode.TEXTER, gd.getToDisplay_dialogMode());
		// framesequence is cleared when
	}

}
