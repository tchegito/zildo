package junit.script;

import static zildo.server.EngineZildo.spriteManagement;

import org.junit.Assert;
import org.junit.Test;

import tools.EngineUT;
import zildo.fwk.input.KeyboardHandler.Keys;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Angle;
import zildo.server.EngineZildo;

/**
 * Check mechanisms around objects hidden:<ul>
 * <li>on specific tile (under a pot for example)</li>
 * <li>on a given character, who'll give it when he dies</li>
 * <ul>
 * @author Tchegito
 *
 */
public class CheckScriptQuest extends EngineUT {

	PersoPlayer zildo;

	/** Check that a key should appear under a pot, as long as player doesn't take it **/
	@Test
	public void checkKeyUnderTile() {

		goInsideHouseAndLiftJar();
		Assert.assertNotNull("A key should have show up !", spriteManagement.lookFor(zildo, 2, ElementDescription.KEY, true));
		Assert.assertEquals(0, zildo.getCountKey());
		
		goInsideHouseAndLiftJar();
		Assert.assertNotNull("A key should have show up !", spriteManagement.lookFor(zildo, 2, ElementDescription.KEY, true));
	}
	
	/** Check that a key should NOT appear anymore once player has taken it **/
	@Test
	public void checkKeyUnderTileAfterTake() {

		goInsideHouseAndLiftJar();
		Assert.assertNotNull("A key should have show up !", spriteManagement.lookFor(zildo, 2, ElementDescription.KEY, true));
		Assert.assertEquals(0, zildo.getCountKey());
		simulateDirection(0, 1);
		renderFrames(40);
		Assert.assertEquals(1, zildo.getCountKey());
		
		simulatePressButton(Keys.Q, 2);	// Press to skip dialog
		simulatePressButton(Keys.Q, 2);	// Press to close dialog frame
				
		goInsideHouseAndLiftJar();
		Assert.assertNull("A key shouldn't have show up !", spriteManagement.lookFor(zildo, 2, ElementDescription.KEY, true));
	}
	
	/** Check that a key should appear when an expected character dies, as long as player doesn't take it **/
	@Test
	public void checkKeyOnCharacter() {
		goInPrisonAndKillGard();
		Assert.assertNotNull("A key should have show up !", spriteManagement.lookFor(zildo, 6, ElementDescription.KEY, false));

		goInPrisonAndKillGard();
		Assert.assertNotNull("A key should have show up !", spriteManagement.lookFor(zildo, 6, ElementDescription.KEY, false));
	}
	
	/** Check that a key should NOT appear anymore once player has taken it **/
	@Test
	public void checkKeyOnCharacterAfterTake() {
		goInPrisonAndKillGard();
		Assert.assertNotNull("A key should have show up !", spriteManagement.lookFor(zildo, 8, ElementDescription.KEY, false));

		Assert.assertEquals(0, zildo.getCountKey());
		simulateDirection(-1, 0);
		renderFrames(60);
		Assert.assertEquals(1, zildo.getCountKey());
		
		goInPrisonAndKillGard();
		Assert.assertNull("A key shouldn't have show up !", spriteManagement.lookFor(zildo, 4, ElementDescription.KEY, false));
	}
	

	
	// Load map, spawn hero and make him lift a jar, where a key is hidden
	private void goInsideHouseAndLiftJar() {
		mapUtils.loadMap("bosquetm");

		if (zildo != null) {
			// Do not create hero twice
			EngineZildo.persoManagement.clearPersos(true);
		}
		zildo = spawnZildo(119, 108);
		zildo.setAngle(Angle.SUD);

		// Check hero carries nothing
		Assert.assertEquals(null, zildo.getEn_bras());
		waitEndOfScripting();
		Assert.assertEquals(239 + 256*2, mapUtils.area.readmap(7, 7));
		simulateKeyPressed(Keys.Q);	// Press ACTION
		renderFrames(1);
		simulateKeyPressed();	// Hold ACTION
		renderFrames(1);
		Assert.assertEquals(240 + 256*2, mapUtils.area.readmap(7, 7));
	}
	
	private void goInPrisonAndKillGard() {
		mapUtils.loadMap("prison");
		zildo = spawnZildo(518, 292);
		waitEndOfScripting();
		
		Perso guard = EngineZildo.persoManagement.getNamedPerso("noir");
		guard.beingWounded(guard.x+4f, guard.y, zildo, 15);
		renderFrames(30);
		Assert.assertTrue(guard.getPv() < 0);

	}
}
