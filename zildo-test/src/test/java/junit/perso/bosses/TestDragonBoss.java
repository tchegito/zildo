package junit.perso.bosses;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import tools.EngineUT;
import zildo.monde.items.Item;
import zildo.monde.items.ItemKind;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Angle;
import zildo.monde.util.Vector2f;
import zildo.server.EngineZildo;

/** Check that dragon scene is working the way it should:
 * <ul>
 * <li>rock should hit the dragon when exploded</li>
 * <li>rock should reappear if they miss their target</li>
 * <li>coal should not be damageable by sword</li>
 * </ul>
 * @author Tchegito
 *
 */
public class TestDragonBoss extends EngineUT {

	final static Vector2f NEAR_ROCK1 = new Vector2f(139, 435);
	final static Vector2f NEAR_ROCK2 = new Vector2f(847, 275);
	final static Vector2f NEAR_ROCK3 = new Vector2f(614, 546);
	
	final static Vector2f FAR_FROM_ROCK1 = new Vector2f(376, 549);
	final static Vector2f FAR_FROM_ROCK2 = new Vector2f(582, 376);
	final static Vector2f FAR_FROM_ROCK3 = new Vector2f(969, 436);
	
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
	
	@Before
	public void init() {
		mapUtils.loadMap("dragon");
	}
	
	PersoPlayer hero;
	
	private void attackAndCheckWound(Vector2f nearRock, int dragonPos) {
		hero = spawnZildo(nearRock);
		hero.setFloor(2);
		hero.setWeapon(new Item(ItemKind.DYNAMITE));
		hero.setCountBomb(20);
		hero.setPv(12);
		waitEndOfScripting();
		
		// Animate dragon
		Perso dragon = animateDragon();
		int dragonPv = dragon.getPv();
		hero.setPos(nearRock);
		renderFrames(2);
		Assert.assertEquals(dragonPos+".0", EngineZildo.scriptManagement.getVarValue("dragonPos"));
		
		// Plant dynamite, no matter hero takes damage from it
		hero.attack();
		renderFrames(120);	// Wait for dynamite to explode
		boolean wounded = false;
		for (int j=0;j<100;j++) {
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
	
	@Test
	public void coalCantBeHitBySword() {
		hero = spawnZildo(250,369);
		Item sword = new Item(ItemKind.MIDSWORD);
		hero.getInventory().add(sword);
		hero.setWeapon(sword);
		hero.setAngle(Angle.NORD);
		hero.setFloor(1);
		waitEndOfScripting();
		
		// Hit the coal
		Perso coal = EngineZildo.persoManagement.getNamedPerso("coal1");
		hero.attack();
		renderFrames(18*2);	// Wait for the sword to swing
		// Check that sprite hasn't changed
		Assert.assertEquals("Coal shouldn't be hit by hero's sword !", 0, coal.getAddSpr());
	}
	
	/** Project dynamite on dragon when he isn't on the right spot **/
	@Test
	public void rockRespawnA() {
		plantDynamiteAndGoAway("A", NEAR_ROCK1, FAR_FROM_ROCK1, false, true);
	}
	
	/** Project dynamite on dragon when he is on the right spot ==> he should take damage and rock doesn't respawn**/
	@Test
	public void rockDoesntRespawnA() {
		plantDynamiteAndGoAway("A", NEAR_ROCK1, FAR_FROM_ROCK1, true, false);
	}
	
	@Test
	public void rockRespawnA_fromDown() {
		// Shoot dynamite in the wrong direction
		plantDynamiteAndGoAway("A", new Vector2f(147,444), FAR_FROM_ROCK1, true, true);
	}
	
	@Test
	public void rockRespawnB() {
		plantDynamiteAndGoAway("B", NEAR_ROCK2, FAR_FROM_ROCK2, false, true);
	}
	
	/** Project dynamite on dragon when he is on the right spot ==> he should take damage and rock doesn't respawn**/
	@Test
	public void rockDoesntRespawnB() {
		plantDynamiteAndGoAway("B", NEAR_ROCK2, FAR_FROM_ROCK2, true, false);
	}
	
	@Test
	public void rockRespawnC() {
		plantDynamiteAndGoAway("C", NEAR_ROCK3, FAR_FROM_ROCK3, false, true);
	}
	
	/** Project dynamite on dragon when he is on the right spot ==> he should take damage and rock doesn't respawn**/
	@Test
	public void rockDoesntRespawnC() {
		plantDynamiteAndGoAway("C", NEAR_ROCK3, FAR_FROM_ROCK3, true, false);
	}
	
	/** Dragon's flames should light the coal **/
	@Test
	public void dragonLightCoal() {
		hero = spawnZildo(173, 375);
		Perso dragon = EngineZildo.persoManagement.getNamedPerso("dragon");
		Assert.assertNotNull(dragon);
		EngineZildo.scriptManagement.runPersoAction(dragon, "bossDragon", null, false);
		
		hero.walkTile(false);
		
		// Wait for flames to be thrown
		SpriteEntity flame = null;
		while (flame == null) {
			renderFrames(1);
			for (SpriteEntity entity : EngineZildo.spriteManagement.getSpriteEntities(null)) {
				if (entity.getDesc() == ElementDescription.FIRE_BALL) {
					flame = entity;
				}
			}
		}
		// Wait for the flame to touch the coal
		while (flame.x > 260) {
			renderFrames(1);
		}
		
		// Ensure that coal are lighted
		int sumAddSpr=0;
		for (int i=1;i<=3;i++) {
			Perso coal = persoUtils.persoByName("coal"+i);
			sumAddSpr += coal.getAddSpr();
		}
		Assert.assertNotEquals("Coal should have been lighted by the dragon flame !", 0, sumAddSpr);
	}
	
	/** Plant dynamite and put hero away, to trigger the rock respawn **/
	private void plantDynamiteAndGoAway(String which, Vector2f startLoc, Vector2f farLoc, boolean dragonRightSpot, boolean shouldRespawn) {
		hero = spawnZildo(startLoc);
		hero.setFloor(2);
		hero.setWeapon(new Item(ItemKind.DYNAMITE));
		hero.setCountBomb(20);
		waitEndOfScripting();

		// Place the dragon in the right spot
		if (dragonRightSpot) {
			animateDragon();
		}
		
		// Plant dynamite, and wait for boulder to fall in lava
		hero.setPos(startLoc);
		hero.attack();
		renderFrames(200);
		if (shouldRespawn) {
			Assert.assertTrue(EngineZildo.scriptManagement.isQuestDone("needToRespawnRock"+which));
		} else {
			Assert.assertFalse(EngineZildo.scriptManagement.isQuestDone("needToRespawnRock"+which));
		}

		Assert.assertFalse(EngineZildo.scriptManagement.isQuestDone("reappearRock"+which));
		
		// Move hero at another location, and check distance is > 10 tiles
		Assert.assertTrue(farLoc.distance(startLoc) > 16 * 10);
		hero.setPos(farLoc);
		hero.walkTile(false);
		renderFrames(1);
		
		if (shouldRespawn) {
			checkRockRespawn(which);
		} else {
			checkRockDoesntRespawn(which);
		}
	}
	
	private void checkRockRespawn(String which) {
		Assert.assertTrue(EngineZildo.scriptManagement.isQuestDone("reappearRock"+which));
		renderFrames(2);
		Assert.assertNotNull(EngineZildo.spriteManagement.getNamedEntity("rock"+which));
	}
	
	private void checkRockDoesntRespawn(String which) {
		Assert.assertFalse(EngineZildo.scriptManagement.isQuestDone("reappearRock"+which));
		renderFrames(2);
		Assert.assertNull(EngineZildo.spriteManagement.getNamedEntity("rock"+which));
		
	}
	
	private Perso animateDragon() {
		// Animate dragon
		Perso dragon = EngineZildo.persoManagement.getNamedPerso("dragon");
		Assert.assertNotNull(dragon);
		EngineZildo.scriptManagement.runPersoAction(dragon, "bossDragon", null, false);
		
		hero.walkTile(false);
		waitForScriptRunning("dragonDiveAndReappear");
		waitForScriptFinish("dragonDiveAndReappear");
		
		return dragon;
	}
}
