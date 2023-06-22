package junit.area;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import tools.EngineUT;
import zildo.client.ClientEngineZildo;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
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
		waitChangingMap();
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
		spawnZildo(40, 146);
		waitEndOfScripting();
		
		// Get barrels
		List<SpriteEntity> barrels = findBarrels();
		Assert.assertTrue(barrels.size() > 1);
		
		// 1) transition to the left
		System.out.println(EngineZildo.spriteManagement.getSpriteEntities(null).size());
		simulateDirection(-1, 0);
		waitChangingMap();
		simulateDirection(0, 0);
		
		// Check that sprites are still there
		Assert.assertTrue(checkSpritesPresence(barrels));
		
		renderFrames(5);
		waitEndOfScroll();
		//Assert.assertTrue(checkSpritesPresence(barrels));

		renderFrames(5);
		mapUtils.assertCurrent("prison4");
		Assert.assertFalse(checkSpritesPresence(barrels));
		
		// 2) transition to the right
		barrels = findBarrels();
		simulateDirection(1, 0);
		waitChangingMap();
		simulateDirection(0, 0);
		
		// Check that sprites are still there
		Assert.assertTrue(checkSpritesPresence(barrels));
		
		renderFrames(5);
		waitEndOfScroll();
		Assert.assertFalse(checkSpritesPresence(barrels));

		renderFrames(5);
		mapUtils.assertCurrent("prison4r");
		Assert.assertFalse(checkSpritesPresence(barrels));
	}
	
	// Ensure that any suspended entities are not kept after a map change (without scroll)
	@Test
	public void checkEmptySuspendedEntities(){
		mapUtils.loadMap("fermem1");
		spawnZildo(208, 269);
		waitEndOfScripting();
		simulateDirection(0, 1);
		Assert.assertEquals(0, hackSuspendedEntities( ).size());
		while (!"ferme".equals(EngineZildo.mapManagement.getCurrentMap().getName())) {
			renderFrames(1);
		}
		Assert.assertEquals(0, hackSuspendedEntities().size());

	}
	
	@Test
	public void offsetOnMapVertical() {
		mapUtils.loadMap("cavef3");
		spawnZildo(495, 54);
		waitEndOfScripting();

		// Go north to reach cavef4
		simulateDirection(0, -1);
		System.out.println(ClientEngineZildo.mapDisplay.getCamera());
		Assert.assertEquals(320, camera().x);
		waitChangingMap();
		renderFrames(1);
		System.out.println(ClientEngineZildo.mapDisplay.getCamera());
		waitEndOfScroll();
		Assert.assertEquals("cavef4",  EngineZildo.mapManagement.getCurrentMap().getName());
		Assert.assertEquals(336,  camera().x);

		// And go back to cavef3
		simulateDirection(0, 1);
		waitChangingMap();
		renderFrames(1);
		waitEndOfScroll();
		Assert.assertEquals("cavef3",  EngineZildo.mapManagement.getCurrentMap().getName());
		Assert.assertEquals(335, camera().x);
	}
	
	@Test
	public void offsetOnMapHorizontal() {
		mapUtils.loadMap("cavef6");
		spawnZildo(672,336);
		waitEndOfScripting();

		// Go north to reach cavef4
		simulateDirection(1, 0);
		System.out.println(ClientEngineZildo.mapDisplay.getCamera());
		Assert.assertEquals(216, camera().y);
		waitChangingMap();
		renderFrames(1);
		System.out.println(ClientEngineZildo.mapDisplay.getCamera());
		waitEndOfScroll();
		Assert.assertEquals("cavef7",  EngineZildo.mapManagement.getCurrentMap().getName());
		Assert.assertEquals(224,  camera().y);

		// And go back to cavef3
		simulateDirection(-1, 0);
		waitChangingMap();
		renderFrames(1);
		waitEndOfScroll();
		Assert.assertEquals("cavef6",  EngineZildo.mapManagement.getCurrentMap().getName());
		Assert.assertEquals(216, camera().y);
	}
	
	
	
	private Point camera() {
		return ClientEngineZildo.mapDisplay.getCamera();
	}
	
	// Get a package protected field from SpriteManagement
	@SuppressWarnings("unchecked")
	private List<SpriteEntity> hackSuspendedEntities() {
		try {
		Field[] fields = EngineZildo.spriteManagement.getClass().getDeclaredFields();
        for (Field field : fields) {
            if ("suspendedEntities".equals(field.getName())) {
        	   field.setAccessible(true);
        	   return (List<SpriteEntity>) field.get(EngineZildo.spriteManagement);
            }
        }
		} catch (IllegalAccessException e) {
			// Exception will be thrown just below
		}
        throw new RuntimeException("Unable to find 'suspendedEntities' field from SpriteManagement instance");
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