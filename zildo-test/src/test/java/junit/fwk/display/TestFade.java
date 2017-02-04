package junit.fwk.display;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import tools.EngineUT;
import zildo.client.ClientEngineZildo;
import zildo.client.ClientEvent;
import zildo.client.ClientEventNature;
import zildo.fwk.FilterCommand;
import zildo.fwk.gfx.filter.FilterEffect;
import zildo.monde.items.Item;
import zildo.monde.items.ItemKind;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.resource.Constantes;
import zildo.server.EngineZildo;

public class TestFade extends EngineUT {

	int numberFramesCompleteFade = (255 / Constantes.FADE_SPEED) + 1;
	
	FilterCommand fc;
	PersoPlayer zildo;
	
	@Before
	public void init() {
		// We must have Zildo because CircleFilter will be called
		zildo = spawnZildo(60, 60);
		// Add an item into its inventory in order to look at it
		Item item = new Item(ItemKind.FIRE_RING);
		zildo.getInventory().add(item);
		zildo.setWeapon(item);
		waitEndOfScripting();
    	fc = ClientEngineZildo.filterCommand;
	}
	
	@Test
	public void simpleFadeInOut() {
    	Assert.assertEquals(0, fc.getFadeLevel());

    	// Fade out
    	EngineZildo.askEvent(new ClientEvent(ClientEventNature.FADE_OUT, FilterEffect.BLACKBLUR));
    	renderFrames(numberFramesCompleteFade);
    	Assert.assertEquals(255, fc.getFadeLevel());
    	
    	// Then fade in
    	EngineZildo.askEvent(new ClientEvent(ClientEventNature.FADE_IN, FilterEffect.BLACKBLUR));
    	renderFrames(numberFramesCompleteFade);
    	Assert.assertEquals(0, fc.getFadeLevel());
	}
	
	@Test
	public void differentFadesTogether() {
		// Fade in like when player loads a game
    	EngineZildo.askEvent(new ClientEvent(ClientEventNature.FADE_IN, FilterEffect.BLACKBLUR));
    	// Wait in the middle of the fade
    	renderFrames(numberFramesCompleteFade / 2);
    	System.out.println(fc.getFadeLevel());
    	// Assert that fade isn't over
    	Assert.assertNotEquals(0, fc.getFadeLevel());
    	Assert.assertNotEquals(255, fc.getFadeLevel());
    	// Then ask for another one
    	zildo.lookInventory();
    	System.out.println(fc.displayActive());
		renderFrames(2);
    	System.out.println(fc.displayActive());
	}
	
}
