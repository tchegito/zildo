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

	/** Test: player loads a game, and he press Inventory key before Blackblur filter was over. **/
	@Test
	public void differentFadesOutTogether() {
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
    	Assert.assertEquals(FilterEffect.BLACKBLUR, fc.getActiveFade());
    	// Inventory requests for SEMIFADE filter, but it should be delayed when BLACKBLUR is finished
		renderFrames(2);
    	Assert.assertEquals(FilterEffect.BLACKBLUR, fc.getActiveFade());
    	int i = numberFramesCompleteFade;
    	boolean done = false;
    	while (i>0) {
    		renderFrames(1);
    		done = fc.getActiveFade() == FilterEffect.SEMIFADE;
    		if (done) break;
    		i--;
    	}
    	Assert.assertTrue("Semifade should have started !", done);
    	System.out.println(fc.getActiveFade());
	}
	
	/** Player loads a game before opening game's fade isn't over yet.
	 * Test a FADE in and a BLACKBLUR in **/
	@Test
	public void differentWayFade() {
		Assert.assertEquals(0, fc.getFadeLevel());
    	EngineZildo.askEvent(new ClientEvent(ClientEventNature.FADE_IN, FilterEffect.FADE));
    	renderFrames(numberFramesCompleteFade/2);
    	Assert.assertEquals(FilterEffect.FADE, fc.getActiveFade());
    	EngineZildo.askEvent(new ClientEvent(ClientEventNature.FADE_IN, FilterEffect.BLACKBLUR));
    	renderFrames(2);
    	// BlackBlur should have priority on other ones
    	Assert.assertEquals(FilterEffect.BLACKBLUR, fc.getActiveFade());
	}
	
	/** Players goes into inventory, and leaves it before semifade is over. **/
	@Test
	public void noFlicker() {
    	Assert.assertEquals(0, fc.getFadeLevel());
    	zildo.lookInventory();
    	renderFrames(2);
    	Assert.assertEquals(FilterEffect.SEMIFADE, fc.getActiveFade());
    	Assert.assertTrue("fade should have started !", fc.getFadeLevel() > 0);
    	renderFrames(numberFramesCompleteFade/4);
    	int instantFadeLevel = fc.getFadeLevel();
    	zildo.closeInventory();
    	renderFrames(4);
    	// We should stay in semifade, but on the other direction
    	Assert.assertEquals(FilterEffect.SEMIFADE, fc.getActiveFade());
    	int currentFadeLevel = fc.getFadeLevel();
    	Assert.assertTrue("fade level should have been decrease ! "+currentFadeLevel+" > "+instantFadeLevel, 
    			currentFadeLevel < instantFadeLevel);
	}
	
}
