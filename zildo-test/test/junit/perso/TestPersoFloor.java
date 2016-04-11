package junit.perso;

import org.junit.Assert;
import org.junit.Test;

import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Point;
import zildo.server.EngineZildo;

public class TestPersoFloor extends EngineUT {

	
	// Check the code on ActionExecutor deltaFloor parts
	@Test
	public void testStairsOnHigherFloor() {
		mapUtils.loadMap("voleursg5");
		waitEndOfScripting();
		
		PersoPlayer zildo = spawnZildo(543, 335);
		zildo.setFloor(0);
		
		// 1) go upstairs
		simulateDirection(0,-1);
		renderFrames(20);
		waitEndOfScripting();
		
		// Check that hero raised a floor, because on this map, floor is higher after stairs
		Assert.assertTrue(zildo.y < 288);
		Assert.assertEquals("Hero should have gained a floor !", 1, zildo.getFloor());
		
		// 2) go downstairs
		simulateDirection(0, 1);
		renderFrames(20);
		waitEndOfScripting();
		Assert.assertTrue(zildo.y > 322);
		Assert.assertEquals("Hero should have stay on same floor !", 0, zildo.getFloor());
	}
	
	@Test
	public void testStairsOnSameFloor() {
		mapUtils.loadMap("prison10");
		waitEndOfScripting();
		
		PersoPlayer zildo = spawnZildo(192, 260);
		zildo.setFloor(1);
		
		// 1) go upstairs
		simulateDirection(0, -1);
		renderFrames(20);
		waitEndOfScripting();
		
		// On this map floor is the same before and after the stairs
		Assert.assertTrue(zildo.y < 224);
		Assert.assertEquals("Hero should have stay on same floor !", 1, zildo.getFloor());
		
		// 2) go downstairs
		simulateDirection(0, 1);
		renderFrames(20);
		waitEndOfScripting();
		Assert.assertTrue(zildo.y > 258);
		Assert.assertEquals("Hero should have stay on same floor !", 1, zildo.getFloor());
	}

	@Test
	public void testMoveCharacterHighFloor() {
		mapUtils.loadMap("prison12");
		waitEndOfScripting();
		Perso perso = EngineZildo.persoManagement.getNamedPerso("noir");
		// There's 2 characters named "noir" on this map : check we got expected one
		Assert.assertTrue(perso.x > 251);
		perso.setTarget(new Point(200, perso.y));
		renderFrames(50);
		System.out.println(perso.floor);
		Assert.assertTrue("Character should have been blocked by higher floor interruption ! ("+perso.x+")", perso.x > 264);
	}
}
