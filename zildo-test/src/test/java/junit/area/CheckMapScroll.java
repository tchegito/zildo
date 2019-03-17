package junit.area;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import tools.EngineUT;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Angle;
import zildo.monde.util.Vector2f;
import zildo.server.EngineZildo;

public class CheckMapScroll extends EngineUT {

	/** Check that hero faces the right direction after a scroll **/
	@Test //@InfoPersos
	public void heroFaceRight() {
		mapUtils.loadMap("chateausud");
		PersoPlayer zildo = spawnZildo(6, 338);
		waitEndOfScripting();
		
		// Check that no script is rendering at this time
		Assert.assertFalse(EngineZildo.scriptManagement.isScripting());

		// 1) check on WEST
		// Send hero on the border and wait for him to reach it
		simulateDirection(-1, 0);
		renderFrames(20);
		Assert.assertNotNull("Hero should have been changing map !", EngineZildo.mapManagement.getMapScrollAngle());
		waitEndOfScripting();
		simulateDirection(0, 0);	// Stop him
		// Now we should be on the next map
		Assert.assertEquals("eleoforet1", EngineZildo.mapManagement.getCurrentMap().getName());
		Assert.assertFalse(EngineZildo.scriptManagement.isScripting());
		while (zildo.isGhost()) {
			renderFrames(1);
		}
		// Check that hero stands still and faces right direction
		Assert.assertEquals(0,  (int) zildo.deltaMoveX);
		renderFrames(10);
		Assert.assertEquals(Angle.OUEST, zildo.getAngle());
		
		// 2) Same with opposite direction (EAST)
		simulateDirection(1, 0);
		renderFrames(20);
		Assert.assertNotNull("Hero should have been changing map !", EngineZildo.mapManagement.getMapScrollAngle());
		waitEndOfScripting();
		simulateDirection(0, 0);	// Stop him
		Assert.assertEquals("chateausud", EngineZildo.mapManagement.getCurrentMap().getName());
		while (zildo.isGhost()) {
			renderFrames(1);
		}
		Assert.assertEquals(0,  (int) zildo.deltaMoveX);
		renderFrames(10);
		Assert.assertEquals(Angle.EST, zildo.getAngle());
	}
	
	@Test
	public void scrollUnblock() {
		mapUtils.loadMap("sousbois3");
		EngineZildo.scriptManagement.accomplishQuest("hero_princess", false);
		PersoPlayer hero = spawnZildo(682, 8);
		waitEndOfScripting();
		// Goes up and wait for map change
		simulateDirection(0, -1);
		while (!EngineZildo.mapManagement.isChangingMap(hero)) {
			renderFrames(1);
		}
		simulateDirection(0, 0);
		renderFrames(5);
		waitEndOfScripting();
		renderFrames(5);
		Assert.assertEquals("sousbois4", EngineZildo.mapManagement.getCurrentMap().getName());
		Assert.assertNull("Hero should have been arrived to his target ! But he seems blocked.", hero.getTarget());
		/*
		while (!EngineZildo.mapManagement.isChangingMap(hero)) {
			renderFrames(1);
		}
		renderFrames(50); */
	}
	
	/** Issue 136: hero projected on chaining point, bug going on map in wrong axis => horizontal instead of vertical **/
	@Test
	public void scrollWrongAxis() {
		mapUtils.loadMap("bosquet");
		PersoPlayer hero = spawnZildo(524, 10);
		waitEndOfScripting();
		Perso guard = persoUtils.persoByName("g2");
		guard.setPos(new Vector2f(547, 54));
		guard.setAlerte(true);
		while (!hero.isWounded()) {
			renderFrames(1);
		}
		while (hero.isWounded()) {
			renderFrames(1);
		}
		mapUtils.assertCurrent("polaky");
		System.out.println(hero);
	}
	
	/** Issue 169: scroll left to right leaded to entities removal too early **/
	@Test
	public void entitiesDisappearAtRightTime() {
		mapUtils.loadMap("prison4r");
		PersoPlayer hero = spawnZildo(40, 146);
		waitEndOfScripting();
		
		// Get barrels
		List<SpriteEntity> barrels = findBarrels();
		Assert.assertTrue(barrels.size() > 1);
		
		// 1) transition to the left
		System.out.println(EngineZildo.spriteManagement.getSpriteEntities(null).size());
		simulateDirection(-1, 0);
		while (!EngineZildo.mapManagement.isChangingMap(hero)) {
			renderFrames(1);
		}
		simulateDirection(0, 0);
		
		// Check that sprites are still there
		Assert.assertTrue(checkSpritesPresence(barrels));
		
		renderFrames(5);
		waitEndOfScroll();
		Assert.assertTrue(checkSpritesPresence(barrels));

		renderFrames(5);
		mapUtils.assertCurrent("prison4");
		Assert.assertFalse(checkSpritesPresence(barrels));
		
		// 2) transition to the right
		barrels = findBarrels();
		simulateDirection(1, 0);
		while (!EngineZildo.mapManagement.isChangingMap(hero)) {
			renderFrames(1);
		}
		simulateDirection(0, 0);
		
		// Check that sprites are still there
		Assert.assertTrue(checkSpritesPresence(barrels));
		
		renderFrames(5);
		waitEndOfScroll();
		Assert.assertTrue(checkSpritesPresence(barrels));

		renderFrames(5);
		mapUtils.assertCurrent("prison4r");
		Assert.assertFalse(checkSpritesPresence(barrels));
	}
	
	private boolean checkSpritesPresence(List<SpriteEntity> candidates) {
		int found = 0;
		for (SpriteEntity entity : EngineZildo.spriteManagement.getSpriteEntities(null)) {
			if (candidates.contains(entity))
				found++;
		}
		return found == candidates.size();
	}
	
	private List<SpriteEntity> findBarrels() {
		List<SpriteEntity> barrels = new ArrayList<>();
		for (SpriteEntity entity : EngineZildo.spriteManagement.getSpriteEntities(null)) {
			if (entity.getDesc() == ElementDescription.BARREL) {
				barrels.add(entity);
			}
		}
		return barrels;
	}
}