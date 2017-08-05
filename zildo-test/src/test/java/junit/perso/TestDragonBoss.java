package junit.perso;

import org.junit.Assert;
import org.junit.Test;

import tools.EngineUT;
import zildo.monde.items.Item;
import zildo.monde.items.ItemKind;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Vector2f;
import zildo.server.EngineZildo;

public class TestDragonBoss extends EngineUT {

	final static Vector2f NEAR_ROCK1 = new Vector2f(139, 435);
	final static Vector2f NEAR_ROCK2 = new Vector2f(847, 275);
	final static Vector2f NEAR_ROCK3 = new Vector2f(614, 546);
	
	/** Ensure that a rock projected by hero's dynamite will hit dragon **/
	@Test
	public void attackDragonWithRock1() {
		attackAndCheckWound(NEAR_ROCK1, 1);
	}
	
	@Test
	public void attackDragonWithRock2() {
		attackAndCheckWound(NEAR_ROCK2, 3);
	}
	
	@Test
	public void attackDragonWithRock3() {
		attackAndCheckWound(NEAR_ROCK3, 5);
	}
	
	private void attackAndCheckWound(Vector2f nearRock, int dragonPos) {
		mapUtils.loadMap("dragon");
		PersoPlayer hero = spawnZildo((int) nearRock.x, (int) nearRock.y);
		hero.setFloor(2);
		hero.setWeapon(new Item(ItemKind.DYNAMITE));
		hero.setCountBomb(20);
		waitEndOfScripting();
		
		// Animate dragon
		Perso dragon = EngineZildo.persoManagement.getNamedPerso("dragon");
		int dragonPv = dragon.getPv();
		Assert.assertNotNull(dragon);
		EngineZildo.scriptManagement.runPersoAction(dragon, "bossDragon", null, false);
		
		waitForScriptRunning("dragonDiveAndReappear");
		waitForScriptFinish("dragonDiveAndReappear");
		hero.setPos(nearRock);
		renderFrames(2);
		Assert.assertEquals(dragonPos+".0", EngineZildo.scriptManagement.getVarValue("dragonPos"));
		
		// Plant dynamite, no matter hero takes damage from it
		hero.attack();
		renderFrames(120);	// Wait for dynamite to explode
		boolean wounded = false;
		for (int j=0;j<100;j++) {
			System.out.println(dragon.z);
			if (dragon.isWounded()) {
				wounded = true;
				break;
			}
			renderFrames(1);
		}
		Assert.assertTrue("Dragon should have been wounded by the rock !", wounded);
		renderFrames(200);	// Wait for dragon to stop screaming from his wound
		Assert.assertFalse(dragon.isWounded());
		Assert.assertEquals(dragonPv - 1, dragon.getPv());
	}
}
