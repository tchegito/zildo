package junit.sprites;

import org.junit.Assert;
import org.junit.Test;

import tools.EngineUT;
import zildo.client.ClientEngineZildo;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Pointf;
import zildo.monde.util.Vector2f;
import zildo.resource.KeysConfiguration;
import zildo.server.EngineZildo;

public class CheckDisplaySorter extends EngineUT {

	@Test
	public void carryingItem() {
		mapUtils.loadMap("valori");
		PersoPlayer hero = spawnZildo(998,772);
		
		// Kill close mole
		persoUtils.removePerso("taupe2");
		waitEndOfScripting();
		
		simulateDirection(0, 1);
		renderFrames(20);
		simulateDirection(0, 0);
		simulatePressButton(KeysConfiguration.PLAYERKEY_ACTION.code, 2);
		renderFrames(10);
		Assert.assertNotNull(hero.getEn_bras());
		
		hero.setPos(new Vector2f(911, 556));
		simulateDirection(0, -1);
		// Hero is moving north, so wait until he has gone upstairs with the ladder
		while (hero.floor == 1) {
			Assert.assertEquals(1, hero.getEn_bras().getFloor());
			renderFrames(1);
		}
		Assert.assertEquals(2, hero.getFloor());
		Assert.assertEquals(2, hero.getEn_bras().getFloorForSort());
	}
	
	/** Issue 179: reported by Jared, dragon's flames are displayed under the ground => invisible **/
	@Test
	public void dragonFlames() {
		mapUtils.loadMap("dragon");
		spawnZildo(504, 200);
		
		// Animate dragon
		Perso dragon = EngineZildo.persoManagement.getNamedPerso("dragon");
		Assert.assertNotNull(dragon);
		EngineZildo.scriptManagement.runPersoAction(dragon, "bossDragon", null, false);
		
		SpriteEntity fireBall = waitForSpecificEntity(ElementDescription.FIRE_BALL);
		// Fireball should be in the viewport
		Assert.assertTrue(fireBall.isInsideView());
		// Fireball should be above the ground
		Assert.assertEquals(2, fireBall.getFloor());
		Assert.assertEquals("Fireball should be above the ground", 2, fireBall.getFloorForSort());

	}
	
	// Rotated sprites have their condition wether they are inside the view, which was wrongly calculated.
	// It didn't take rotation into account
	@Test
	public void creeperDisappear() {
		mapUtils.loadMap("nature2");
		PersoPlayer zildo = spawnZildo(199, 225);
		ClientEngineZildo.spriteDisplay.setZildoId(zildo.getId());
		waitEndOfScripting();

		SpriteEntity creeper = findEntitiesByDesc(ElementDescription.CREEPER3B).stream()
				.filter(e -> e.x <70 && e.y <= 212)
				.findAny().get();
		
		// In order to calculate scrX/scrY properly according to the camera
		ClientEngineZildo.spriteDisplay.setEntities(EngineZildo.spriteManagement.getSpriteEntities(null)); 
		
		// 230
		Assert.assertTrue(creeper.isInsideView());
		zildo.setTarget(new Pointf(235, 225));
		zildo.setGhost(true);
		renderFrames(30);
		Assert.assertTrue(creeper.isInsideView());
	}
}
