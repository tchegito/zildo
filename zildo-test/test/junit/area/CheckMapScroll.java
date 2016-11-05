package junit.area;

import org.junit.Assert;
import org.junit.Test;

import tools.EngineUT;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Angle;
import zildo.server.EngineZildo;

public class CheckMapScroll extends EngineUT {

	/** Check that hero faces the right direction after a scroll **/
	@Test
	public void heroFaceRight() {
		mapUtils.loadMap("chateausud");
		PersoPlayer zildo = spawnZildo(6, 338);
		waitEndOfScripting();
		
		// Check that no script is rendering at this time
		Assert.assertFalse(EngineZildo.scriptManagement.isScripting());
		// Send hero on the border and wait for him to reach it
		simulateDirection(-1, 0);
		renderFrames(10);
		simulateDirection(0, 0);
		waitEndOfScripting();
		// Now we should be on the next map
		Assert.assertEquals("eleoforet1", EngineZildo.mapManagement.getCurrentMap().getName());
		Assert.assertFalse(EngineZildo.scriptManagement.isScripting());
		while (zildo.isGhost()) {
			renderFrames(1);
		}
		Assert.assertFalse(zildo.isGhost());
		// Check that hero faces right direction
		Assert.assertEquals(0,  (int) zildo.deltaMoveX);
		System.out.println(zildo.x +"," + zildo.y+zildo.getAngle());
		renderFrames(10);
		System.out.println(zildo.x +"," + zildo.y+zildo.getAngle());
		Assert.assertEquals(Angle.OUEST, zildo.getAngle());
	}
}
